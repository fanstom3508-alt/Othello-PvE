package Controller;
import java.util.List;

import Model.Board;
import Model.Node;

import java.util.ArrayList;

public class ComputerPlayer extends Player {

    public ComputerPlayer(int color) {
        super(color);
    }

    private final int MAXDEPTH = 10;
    // [23130186_TranLeMinhMan_Thêm Mới] Exception dùng để báo hiệu khi quá thời gian 1.8s
    public static class SearchTimeoutException extends RuntimeException {}
    private final int Pos_Infinity = 99999999;
    private final int Neg_Infinity = -99999999;
    
    // [23130186_TranLeMinhMan_Thêm Mới] Hàm thực hiện tìm kiếm và trả về nước đi tốt nhất tại một độ sâu cụ thể
    // [23130186_TranLeMinhMan_Cập nhật] Thêm tham số long startTimeMs
    	private int[] searchAtDepth(Board board, int depth, List<int[]> validMoves, long startTimeMs) {
        int bestValue = Neg_Infinity; // Khởi tạo giá trị tệ nhất
        int[] bestMove = validMoves.get(0); // Lấy tạm nước đi đầu tiên phòng hờ

        for (int[] move : validMoves) {
            Board child = board.copy();
            child.makeMove(move[0], move[1], color);
            Node childNode = new Node(child, move, color);
            
            // Gọi thuật toán Alpha-Beta cho nước đi này.
            // Bước tiếp theo là lượt của người chơi (min) nên maxmin = false
            // [23130186_TranLeMinhMan_Cập nhật] Truyền startTimeMs xuống cho alphaBeta
            int value = alphaBeta(false, childNode, depth - 1, Neg_Infinity, Pos_Infinity, startTimeMs);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }
        return bestMove;
    }

    // UC-3.18: Kích hoạt luồng Thread ngầm tính toán nước đi cho Máy
    // 23130186_TranLeMinhMan_CapNhatThem
    @Override
    public void makeMove(Board board, MoveCallBack callBack) {
        new Thread(() -> {
            Runtime rt = Runtime.getRuntime();
            rt.gc(); // dọn rác trước khi đo

            long memBefore = rt.totalMemory() - rt.freeMemory();
            long startTime = System.nanoTime();
            
            //[23130186_TranLeMinhMan_Thêm Mới] Lấy thời gian bắt đầu bằng mili-giây
            long startTimeMs = System.currentTimeMillis();

            List<int[]> validMoves = board.getValidMoves(color);
            if (validMoves.isEmpty()) {
                callBack.onMove(-1, -1);
                return;
            }
            // Code của bạn cũ
            
//            int bestValue = Integer.MIN_VALUE;
//            int[] bestMove = null;
       
//            for (int[] move : validMoves) {
//                Board child = board.copy();
//                child.makeMove(move[0], move[1], color);
//                Node childNode = new Node(child, move, color);
//                // chuyển sang min người
//                int value = alphaBeta(false, childNode, MAXDEPTH - 1, Neg_Infinity, Pos_Infinity);
//
//                if (value > bestValue) {
//                    bestValue = value;
//                    bestMove = move;
//                }
//            }
            /*
             * [23130186_TranLeMinhMan_Thêm Mới] Code được phát triển tiếp 
             */
            int[] bestMoveSoFar = validMoves.get(0); // Khởi tạo mốc lưu trữ nước đi tốt nhất
            int actualDepthReached = 0; // [Thêm mới] Biến lưu lại độ sâu lớn nhất đã hoàn thành

            //[23130186_TranLeMinhMan_Thêm Mới] Bắt ngoại lệ TimeoutException hoặc xử lý flag dừng ở hàm makeMove
            try {
            // Vòng lặp Iterative Deepening
	            for (int d = 1; d <= MAXDEPTH; d++) {
	                // Tìm kiếm nước đi tốt nhất ở độ sâu 'd' hiện tại
	            	// Truyền startTimeMs vào hàm searchAtDepth
	            	int[] currentBestMove = searchAtDepth(board, d, validMoves, startTimeMs);
	                
	            	// Chỉ khi tìm kiếm TRỌN VẸN độ sâu 'd' mà không bị ngắt, lệnh gán này mới được chạy
	                if (currentBestMove != null) {
	                    bestMoveSoFar = currentBestMove;
	                    actualDepthReached = d; // Cập nhật độ sâu thực tế đã hoàn thành trọn vẹn
	                }
	                
	                // In ra để thấy AI đang đào sâu dần
	                System.out.println("Đã hoàn thành tìm kiếm ở độ sâu: " + d);
	            }
            } catch (SearchTimeoutException e) {
                // Đã bị ngắt do lố 1.8 giây!
                // Vòng lặp for bị phá vỡ ngay lập tức. Các hàm đệ quy sâu bên trong đều dừng lại.
                System.out.println("Timeout Đã quá 1.8 giây! Dừng khẩn cấp thuật toán đệ quy, lấy kết quả tốt nhất để đánh");
            }

            // Đánh dấu thời gian, bộ nhớ kết thúc và in ra kết quả
            long endTime = System.nanoTime();
            long memAfter = rt.totalMemory() - rt.freeMemory();

            long timeMs = (endTime - startTime) / 1_000_000;
            long memKB = (memAfter - memBefore) / 1024;
            
            // log để xem hiệu suất chính xác
            System.out.println("\n THỐNG KÊ LƯỢT ĐI CỦA AI");
            System.out.println("Nhánh AI màu cờ : " + (color == Board.BLACK ? "Đen (1)" : "Trắng (2)"));
            System.out.println("AI chốt nước đi : (" + bestMoveSoFar[0] + ", " + bestMoveSoFar[1] + ")");
            System.out.println("Đạt tới độ sâu  : " + actualDepthReached + " (Max: " + MAXDEPTH + ")");
            System.out.println("Thời gian xử lý : " + timeMs + " ms");
            System.out.println("Bộ nhớ sử dụng  : " + memKB + " KB");
            System.out.println("\n");

            // Gửi kết quả tốt nhất cuối cùng thu được cho UI
            callBack.onMove(bestMoveSoFar[0], bestMoveSoFar[1]);
            
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

        } else { // MIN
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

    // UC-3.22: Thuật toán Alpha-Beta Pruning tìm độ sâu tối ưu
    // [23130186_TranLeMinhMan_Cập nhật] Thêm tham số long startTimeMs
    public int alphaBeta(boolean maxmin, Node state, int depth, int alpha, int beta, long startTimeMs) {
    	// [23130186_TranLeMinhMan_Cập nhật] KIỂM TRA THỜI GIAN: Nếu lố 1800ms (1.8s) thì ném lỗi ngắt ngay lập tức
    	if (System.currentTimeMillis() - startTimeMs > 1800) {
            throw new SearchTimeoutException();
        }
    	
        if (depth == 0 || state.getBoard().isGameOver()) {
            return heuristic(state);
        }
        
        int curColor = maxmin ? color : getOppColor();
        List<int[]> posMove = state.getBoard().getValidMoves(curColor);

        if (posMove.isEmpty()) {
        	// [23130186_TranLeMinhMan_Cập nhật]  thêm startTimeMs vào đệ quy
        	return alphaBeta(!maxmin, state, depth - 1, alpha, beta, startTimeMs);
        }
        if (maxmin) {    // MAX
            int temp = -99999999;
            for (int[] move : posMove) {
                Board childBoard = state.getBoard().copy();
                childBoard.makeMove(move[0], move[1], curColor);
                Node child = new Node(childBoard, move, curColor);

             // [23130186_TranLeMinhMan_Cập nhật] Thêm startTimeMs vào đệ quy
                temp = Math.max(temp, alphaBeta(false, child, depth - 1, alpha, beta, startTimeMs));
                // cập nhật alpha
                alpha = Math.max(alpha, temp);
                // cắt
                if (alpha >= beta) {
                    break;
                }
            }
            return temp;
        } else {    // MIN
            int temp = 99999999;
            for (int[] move : posMove) {
                Board childBoard = state.getBoard().copy();
                childBoard.makeMove(move[0], move[1], curColor);
                Node child = new Node(childBoard, move, curColor);

             // [23130186_TranLeMinhMan_Cập nhật] Thêm startTimeMs vào đệ quy
                temp = Math.min(temp, alphaBeta(true, child, depth - 1, alpha, beta, startTimeMs));
                // cập nhật alpha
                beta = Math.min(beta, temp);
                // cắt
                if (beta <= alpha) {
                    break;
                }
            }
            return temp;
        }
    }

    public int getOppColor() {
        return this.color == Board.WHITE ? Board.BLACK : Board.WHITE;
    }

    // UC-3.23: Hàm lượng giá đánh giá mức độ lợi thế của trạng thái bàn cờ
    private int heuristic(Node state) {
        Board board = state.getBoard();
        int[] score = board.getScore();
        int AI_Score = (color == Board.BLACK) ? score[0] : score[1];
        int oppScore = (color == Board.BLACK) ? score[1] : score[0];

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

        if (totalScore > 50)
            PotentialWeight = 2;
        return (AI_Score - oppScore) + cornerWeight * (AI_Corner - opp_Corner) + edgeWeight * (AI_Edge - Opp_Edge)
                - XWeight * (AI_X - Opp_X) - CWeight * (AI_C - Opp_C) + PotentialWeight * (AI_pW - Opp_mW);
    }
}
