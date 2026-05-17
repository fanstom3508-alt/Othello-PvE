package Model;
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

    // UC-3.4, UC-3.14 reset: Xóa toàn bộ bàn cờ, đặt 4 quân ban đầu ở giữa, set lượt đi về BLACK
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
    
    // UC-3.20: Trả về trạng thái của ô tại vị trí hàng row, cột col
    public int getCell(int row, int col) {
        return board[row][col];
    }

    // UC-3.17, UC-3.25: Trả về màu của người chơi đang có lượt đi
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getLength() {
        return board.length;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }

    // Tra ve danh sach quan co bi lat
    public List<int[]> getFlippedCells(int row, int col, int player) {
        if (board[row][col] != EMPTY) return new ArrayList<>();
        List<int[]> flipped = new ArrayList<>();
        int opponent = (player == BLACK) ? WHITE : BLACK;
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};

        for (int[] dir : directions) {
            List<int[]> temp = new ArrayList<>();
            int r = row + dir[0];
            int c = col + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == opponent) {
                temp.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
            if (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player && !temp.isEmpty()) {
                flipped.addAll(temp);
            }
        }
        return flipped;
    }

    // so luong quan co bi lat
    public int getFlippableCount(int row, int col, int player) {
        return getFlippedCells(row, col, player).size();
    }

    // Check xem có phải là một nước đi hơp lệ không
    public boolean isValidMove(int row, int col, int player) {
        return getFlippableCount(row, col, player) > 0;
    }

    // đặt quân cờ vào ô này
    public boolean makeMove(int row, int col, int player) {
        List<int[]> flipped = getFlippedCells(row, col, player);
        if (flipped.isEmpty()) return false;

        board[row][col] = player;
        for (int[] cell : flipped) {
            board[cell[0]][cell[1]] = player;
        }
        return true;
    }

    // UC3.22/UC-04: Kiểm tra nước đi tại vị trí row, col có hợp lệ với player không (UC4 implement)
    // trả về danh sách các nước đi hợp lệ của người chơi
    public List<int[]> getValidMoves(int player) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(i, j, player)) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }

    // UC-03/UC-04: Kiểm tra game kết thúc
    public boolean isGameOver() {
        return getValidMoves(BLACK).isEmpty() && getValidMoves(WHITE).isEmpty();
    }

    // UC-3.18/UC-04: Đếm và trả về số quân đen và trắng hiện tại trên bàn cờ
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
        Board copy = new Board();
        for (int i = 0; i < 8; i++) {
            System.arraycopy(this.board[i], 0, copy.board[i], 0, 8);
        }
        copy.currentPlayer = this.currentPlayer;
        return copy;
    }

    public int countCorner(Board board, int player) {
        int lastBoardPos = board.getLength() - 1;
        int count = 0;
        if (board.getCell(0, 0) == player) count++;
        if (board.getCell(lastBoardPos, lastBoardPos) == player) count++;
        if (board.getCell(0, lastBoardPos) == player) count++;
        if (board.getCell(lastBoardPos, 0) == player) count++;
        return count;
    }

    public int countEdge(Board board, int player) {
        int count = 0;
        int n = board.getLength() - 1;
        for (int i = 2; i < n - 1; i++) {
            if (board.getCell(0, i) == player) count++; // trên
            if (board.getCell(n, i) == player) count++; // dưới
            if (board.getCell(i, 0) == player) count++; // trái
            if (board.getCell(i, n) == player) count++; // phải
        }
        return count;
    }

    public int countXSquare(Board board, int player) {
        int count = 0;
        int n = board.getLength() - 1;
        if (board.getCell(1, 1) == player && board.getCell(0, 0) == Board.EMPTY) count++;
        if (board.getCell(1, n - 1) == player && board.getCell(0, n) == Board.EMPTY) count++;
        if (board.getCell(n - 1, 1) == player && board.getCell(n, 0) == Board.EMPTY) count++;
        if (board.getCell(n - 1, n - 1) == player && board.getCell(n, n) == Board.EMPTY) count++;
        return count;
    }

    public int countCSquare(Board board, int player) {
        int count = 0;
        int n = board.getLength() - 1;

        // trái trên 
        if (board.getCell(0, 1) == player && board.getCell(0, 0) == Board.EMPTY) count++;
        if (board.getCell(1, 0) == player && board.getCell(0, 0) == Board.EMPTY) count++;

        // trái dưới 
        if (board.getCell(n - 1, 0) == player && board.getCell(n, 0) == Board.EMPTY) count++;
        if (board.getCell(n, 1) == player && board.getCell(n, 0) == Board.EMPTY) count++;

        // phải trên 
        if (board.getCell(0, n - 1) == player && board.getCell(0, n) == Board.EMPTY) count++;
        if (board.getCell(1, n) == player && board.getCell(0, n) == Board.EMPTY) count++;

        // phải dưới
        if (board.getCell(n - 1, n) == player && board.getCell(n, n) == Board.EMPTY) count++;
        if (board.getCell(n, n - 1) == player && board.getCell(n, n) == Board.EMPTY) count++;
        return count;
    }
}
