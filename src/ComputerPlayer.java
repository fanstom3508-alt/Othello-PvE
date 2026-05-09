
import java.util.List;

public class ComputerPlayer extends Player {
	private final int MAXDEPTH = 5 ;
	private final int Pos_Infinity = 99999999;
	private final int Neg_Infinity = -99999999;

	public ComputerPlayer(int color) {
		super(color);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void makeMove(Board board, MoveCallBack callBack) {
		// TODO Auto-generated method stub
		new Thread(() -> {

	        Runtime rt = Runtime.getRuntime();
	        rt.gc(); // dọn rác trước khi đo

	        long memBefore = rt.totalMemory() - rt.freeMemory();
	        long startTime = System.nanoTime();
	        
			List<int[]> validMoves = board.getValidMoves(color);
			if (validMoves.isEmpty()) {
				callBack.onMove(-1, -1);
				return;
			}
			int bestValue = Integer.MIN_VALUE;
			int[] bestMove = null;

			for (int[] move : validMoves) {
				Board child = board.copy();
				child.makeMove(move[0], move[1], color);
				Node childNode = new Node(child, move, color);
				// chuyển sang min người
//				int value = minimax(false, childNode, MAXDEPTH - 1);
				int value = alphaBeta(false, childNode, MAXDEPTH -1 , Neg_Infinity, Pos_Infinity);

				if (value > bestValue) {
					bestValue = value;
					bestMove = move;
				}
			}
			long endTime = System.nanoTime();
	        long memAfter = rt.totalMemory() - rt.freeMemory();

	        long timeMs = (endTime - startTime) / 1_000_000;
	        long memKB = (memAfter - memBefore) / 1024;

	        System.out.println("Executed Time : " + timeMs + " ms");
	        System.out.println("Memory Used   : " + memKB + " KB");
	        
	        
			callBack.onMove(bestMove[0], bestMove[1]);
		}).start();
	}

	public int minimax(boolean maxmin, Node state, int depth) {
		if (depth == 0 || state.getBoard().isGameOver()) {
			return heuristic(state);
		}
		int curColor = maxmin ? this.color : getOppColor();
		List<int[]> posMoves = state.getBoard().getValidMoves(curColor);
		if (posMoves.isEmpty()) {
			return minimax(!maxmin, state, depth - 1);
		}
		if (maxmin) { // MAX
			int temp = -99999999;
			for (int[] move : posMoves) {
				Board childBoard = state.getBoard().copy();
				childBoard.makeMove(move[0], move[1], curColor);
				Node child = new Node(childBoard, move, curColor);

				// đệ quy
				int value = minimax(false, child, depth - 1);
				temp = Math.max(temp, value);
			}
			return temp;

		}
		else { // MIN
			int temp = 99999999;
			for (int[] move : posMoves) {
				Board childBoard = state.getBoard().copy();
				childBoard.makeMove(move[0], move[1], curColor);
				Node child = new Node(childBoard, move, curColor);

				// đệ quy
				int value = minimax(true, child, depth - 1);
				temp = Math.min(temp, value);
			}
			return temp;
		}

	}
	
	// Cắt tỉa alpha Beta 	
	public int alphaBeta(boolean maxmin, Node state, int depth, int alpha, int beta) {
		if(depth == 0 || state.getBoard().isGameOver()) {
			return heuristic(state);
		}
		int curColor = maxmin?color: getOppColor();
		List<int[]> posMove = state.getBoard().getValidMoves(curColor);
		
		if(posMove.isEmpty()) {
			return alphaBeta(!maxmin, state, depth -1, alpha, beta);
		}
		if(maxmin) {	// MAX
			int temp = -99999999;
			for(int[] move: posMove) {
				Board childBoard = state.getBoard().copy();
				childBoard.makeMove(move[0],move[1], curColor);
				Node child= new Node(childBoard, move,curColor);
				
				temp = Math.max(temp, alphaBeta(false, child, depth -1, alpha, beta));
				// cập nhật alpha
				alpha = Math.max(alpha, temp);
				// cắt
				if(alpha >= beta) {
					break;
				}
			}
			return temp;
		}
		else {	// MIN
			int temp = 99999999;
			for(int[] move: posMove) {
				Board childBoard = state.getBoard().copy();
				childBoard.makeMove(move[0],move[1], curColor);
				Node child= new Node(childBoard, move,curColor);
				
				temp = Math.min(temp, alphaBeta(true, child, depth -1, alpha, beta));
				// cập nhật alpha
				beta = Math.min(beta,temp);
				// cắt
				if(beta <= alpha) {
					break;
				}
			}
			return temp;
		}
	}

	public int getOppColor() {
		return this.color == Board.WHITE ? Board.BLACK : Board.WHITE;
	}

	private int heuristic(Node state) {
		// TODO Auto-generated method stub
		Board board = state.getBoard();
		int [] score = board.getScore();
		int AI_Score = (color == Board.BLACK)? score[0]:score[1];
		int oppScore = (color == Board.BLACK)? score[1]:score[0];
		
		// heuristic ưu tiên đánh 4 góc trong cùng
		int cornerWeight = 20;
		int AI_Corner = board.countCorner(board, color);
		int opp_Corner = board.countCorner(board, getOppColor());
		
		// heuristic ưu tiên đánh hàng trên dưới trái phải
		int edgeWeight = 10;
		int AI_Edge = board.countEdge(board, color);
		int Opp_Edge = board.countEdge(board, getOppColor());
		
		// heuristic né X square ô chéo của ô góc
		int XWeight = 15;
		int AI_X = board.countXSquare(board, color);
		int Opp_X = board.countXSquare(board, getOppColor());
		
		// heuristic né C Square ô cạnh của ô góc
		int CWeight = 10;
		int AI_C = board.countCSquare(board, color);
		int Opp_C = board.countCSquare(board, getOppColor());
		
		// heuristic ưu tiên tối đa hóa nước mình và giảm nước đối thủ
		int PotentialWeight = 5;
		int totalScore = AI_Score + oppScore;
		int AI_pW = board.getValidMoves(color).size();
		int Opp_mW = board.getValidMoves(getOppColor()).size();
		
		if(totalScore >50)
			PotentialWeight = 2;
		return (AI_Score - oppScore) + cornerWeight * (AI_Corner - opp_Corner) + edgeWeight * (AI_Edge - Opp_Edge)
				- XWeight * (AI_X - Opp_X) - CWeight * (AI_C - Opp_C) + PotentialWeight * (AI_pW - Opp_mW);
	}
}
=======
public class ComputerPlayer extends Player {

    public ComputerPlayer(int color) {
        super(color);
    }

    @Override
    public void makeMove(Board board, MoveCallBack callBack) {
        // TODO (UC-04): Implement AI Minimax + Alpha-Beta pruning
    }
}

