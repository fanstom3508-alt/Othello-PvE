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
        restart();
    }

    // UC-1.4, UC-1.14: Xóa toàn bộ bàn cờ, đặt 4 quân ban đầu ở giữa, set lượt đi về BLACK
    public void restart() {
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
    
    // UC-1.20: Trả về trạng thái của ô tại vị trí hàng row, cột col
    public int getCell(int row, int col) {
        return board[row][col];
    }

    // UC-1.17, UC-1.25: Trả về màu của người chơi đang có lượt đi
    // UC-3.3: Lấy màu của người chơi đang có lượt đi hiện tại để kiểm tra tính hợp lệ
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getLength() {
        return board.length;
    }

    // UC-3.12, UC-3.27: Hoán đổi lượt chơi hiện tại của trận đấu (từ Người sang Máy và ngược lại)
    public void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }

    // UC-3.6, UC-3.9: Duyệt theo 8 hướng để thu thập danh sách tọa độ các quân cờ của đối phương bị kẹp giữa
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

    // UC-3.5, UC-3.7, UC-3.24: Tính số lượng quân cờ đối phương sẽ bị lật nếu đặt quân tại vị trí chỉ định
    public int getFlippableCount(int row, int col, int player) {
        return getFlippedCells(row, col, player).size();
    }

    // UC-1.22 Kiểm tra nước đi tại vị trí row, col có hợp lệ với player không (cho các ô trống), UC 3 implement
    // UC-3.4: Kiểm tra xem vị trí ô cờ được chọn có phải là nước đi hợp lệ hay không
    public boolean isValidMove(int row, int col, int player) {
        return getFlippableCount(row, col, player) > 0;
    }

    // UC-3.8, UC-3.21, UC-3.25: Đặt quân cờ vào ô chỉ định và thực hiện lật các quân cờ đối phương kẹp giữa
    public boolean makeMove(int row, int col, int player) {
        List<int[]> flipped = getFlippedCells(row, col, player);
        if (flipped.isEmpty()) return false;

        board[row][col] = player;
        for (int[] cell : flipped) {
            board[cell[0]][cell[1]] = player;
        }
        return true;
    }

    // UC-3.17, UC-3.19: Tìm kiếm và trả về danh sách tất cả các tọa độ nước đi hợp lệ của người chơi chỉ định
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
    // UC-3.14, UC-3.29: Kiểm tra điều kiện kết thúc trận đấu (khi cả hai người chơi đều không còn nước đi hợp lệ)
    public boolean isGameOver() {
        return getValidMoves(BLACK).isEmpty() && getValidMoves(WHITE).isEmpty();
    }

    // UC-1.18/UC-04:Đếm và trả về số quân đen và trắng hiện tại trên bàn cờ
    // UC-3.16: Đếm và trả về mảng số lượng quân Đen và quân Trắng hiện tại trên bàn cờ để cập nhật bảng điểm UI
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

    // UC-3.20: Sao chép trạng thái ma trận bàn cờ hiện tại sang một đối tượng Board độc lập để AI chạy thử giả định
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
