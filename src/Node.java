// class mô tả bước đi để dẫn đến bàn cờ này và người chơi của bàn cờ
public class Node {
	public Board board;
	public int [] move;
	public int player;
	/**
	 * @param board
	 * @param move
	 * @param player
	 */
	public Node(Board board, int[] move, int player) {
		super();
		this.board = board;
		this.move = move;
		this.player = player;
	}
	public Board getBoard() {
		return board;
	}
	public int[] getMove() {
		return move;
	}
	public int getPlayer() {
		return player;
	}

}
