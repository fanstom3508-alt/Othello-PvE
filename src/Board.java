import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int[][] board;
    private int currentPlayer;

    public Board() {
        board = new int[8][8];
        reset();
    }

    // UC-03: Khởi tạo bàn cờ về trạng thái ban đầu
    public void reset() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EMPTY;
            }
        }
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
        currentPlayer = BLACK;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getLength() {
        return board.length;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }

    // Trả về danh sách quân bị lật nếu đặt tại (row, col)
    // TODO (UC-04):
    public List<int[]> getFlippedCells(int row, int col, int player) {
        return new ArrayList<>();
    }

    // Số quân bị lật nếu đặt tại (row, col)
    // TODO (UC-04):
    public int getFlippableCount(int row, int col, int player) {
        return 0;
    }

    // Kiểm tra nước đi hợp lệ
    //TODO (UC-04):
    public boolean isValidMove(int row, int col, int player) {
    	return true;
    }

    // Đặt quân và lật quân
    // TODO (UC-04):
    public boolean makeMove(int row, int col, int player) {
        return true;
    }

    // Trả về danh sách nước đi hợp lệ của player
    // TODO (UC-04):
    public List<int[]> getValidMoves(int player) {
        return new ArrayList<>();
    }

    // UC-03/UC-05: Kiểm tra game kết thúc
    public boolean isGameOver() {
        return getValidMoves(BLACK).isEmpty() && getValidMoves(WHITE).isEmpty();
    }

    // UC-03/UC-05: Đếm điểm
    public int[] getScore() {
        int[] score = new int[2];
        int black = 0, white = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == BLACK) black++;
                else if (board[i][j] == WHITE) white++;
            }
        }
        score[0] = black;
        score[1] = white;
        return score;
    }

    // UC-04: Dùng để AI tính toán nước đi (copy bàn cờ)
    public Board copy() {
        return null;
    }

    // TODO (UC-04): Các hàm heuristic cho AI - countCorner, countEdge, countXSquare, countCSquare
}
