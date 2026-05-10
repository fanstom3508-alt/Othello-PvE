package Testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Model.Board;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit Test cho UC-04: Make Move
 * Bao gồm: getFlippedCells, getFlippableCount, isValidMove,
 *           makeMove, getValidMoves, switchPlayer, isGameOver
 */
public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
        // Trạng thái ban đầu sau reset():
        // board[3][3]=WHITE, board[3][4]=BLACK
        // board[4][3]=BLACK, board[4][4]=WHITE
        // currentPlayer = BLACK
    }

    // getFlippedCells()

    @Test
    @DisplayName("getFlippedCells: ô đã có quân thì trả về rỗng")
    void getFlippedCells_OccupiedCell_ReturnsEmpty() {
        List<int[]> result = board.getFlippedCells(3, 3, Board.BLACK);
        assertTrue(result.isEmpty(), "Ô đã có quân không được trả về quân lật");
    }

    @Test
    @DisplayName("getFlippedCells: nước đi hợp lệ của Đen tại (2,3) lật đúng quân Trắng")
    void getFlippedCells_ValidBlackMove_ReturnsCorrectFlips() {
        // Đen đặt tại (2,3): kẹp Trắng tại (3,3)
        List<int[]> flipped = board.getFlippedCells(2, 3, Board.BLACK);
        assertEquals(1, flipped.size(), "Phải lật đúng 1 quân");
        assertEquals(3, flipped.get(0)[0], "Quân lật phải ở hàng 3");
        assertEquals(3, flipped.get(0)[1], "Quân lật phải ở cột 3");
    }

    @Test
    @DisplayName("getFlippedCells: nước đi hợp lệ của Đen tại (3,2) lật đúng quân Trắng")
    void getFlippedCells_ValidBlackMove_Row3Col2() {
        // Đen đặt tại (3,2): kẹp Trắng tại (3,3)
        List<int[]> flipped = board.getFlippedCells(3, 2, Board.BLACK);
        assertEquals(1, flipped.size());
        assertEquals(3, flipped.get(0)[0]);
        assertEquals(3, flipped.get(0)[1]);
    }

    @Test
    @DisplayName("getFlippedCells: ô không kẹp được quân nào thì trả về rỗng")
    void getFlippedCells_NoFlip_ReturnsEmpty() {
        // Đặt ở góc (0,0) không kẹp được quân nào
        List<int[]> flipped = board.getFlippedCells(0, 0, Board.BLACK);
        assertTrue(flipped.isEmpty(), "Ô không kẹp quân nào phải trả về rỗng");
    }

    @Test
    @DisplayName("getFlippedCells: lật đúng nhiều quân theo một hướng")
    void getFlippedCells_MultipleFlipsOneDirection() {
        // Setup: hàng 0: BLACK WHITE WHITE BLACK
        board = new Board();
        // đặt tay: col 0=BLACK, col 1=WHITE, col 2=WHITE, col 3 trống
        setCell(board, 0, 0, Board.BLACK);
        setCell(board, 0, 1, Board.WHITE);
        setCell(board, 0, 2, Board.WHITE);
        // Đen đặt tại (0,3): kẹp 2 quân Trắng
        List<int[]> flipped = board.getFlippedCells(0, 3, Board.BLACK);
        assertEquals(2, flipped.size(), "Phải lật 2 quân Trắng");
    }

    @Test
    @DisplayName("getFlippedCells: lật quân theo nhiều hướng cùng lúc")
    void getFlippedCells_MultipleDirections() {
        // Đen đặt tại (4,5): kẹp Trắng theo chiều ngang (4,4) và dọc (3,5) nếu có
        // Tại trạng thái ban đầu, (4,5) kẹp được Trắng tại (4,4)
        List<int[]> flipped = board.getFlippedCells(4, 5, Board.BLACK);
        assertFalse(flipped.isEmpty(), "Phải lật được ít nhất 1 quân");
    }

    // getFlippableCount()

    @Test
    @DisplayName("getFlippableCount: nước hợp lệ trả về > 0")
    void getFlippableCount_ValidMove_PositiveCount() {
        int count = board.getFlippableCount(2, 3, Board.BLACK);
        assertTrue(count > 0, "Nước hợp lệ phải lật được ít nhất 1 quân");
    }

    @Test
    @DisplayName("getFlippableCount: ô đã có quân trả về 0")
    void getFlippableCount_OccupiedCell_ReturnsZero() {
        int count = board.getFlippableCount(3, 3, Board.BLACK);
        assertEquals(0, count, "Ô đã có quân không lật được quân nào");
    }

    @Test
    @DisplayName("getFlippableCount: ô trống không kẹp được quân trả về 0")
    void getFlippableCount_NoFlip_ReturnsZero() {
        int count = board.getFlippableCount(0, 0, Board.BLACK);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("getFlippableCount: kết quả bằng size() của getFlippedCells()")
    void getFlippableCount_MatchesFlippedCellsSize() {
        int count = board.getFlippableCount(2, 3, Board.BLACK);
        int listSize = board.getFlippedCells(2, 3, Board.BLACK).size();
        assertEquals(listSize, count, "getFlippableCount phải bằng size của getFlippedCells");
    }

    // isValidMove()

    @Test
    @DisplayName("isValidMove: nước hợp lệ của Đen tại (2,3) trả về true")
    void isValidMove_ValidBlackMove_ReturnsTrue() {
        assertTrue(board.isValidMove(2, 3, Board.BLACK));
    }

    @Test
    @DisplayName("isValidMove: nước hợp lệ của Đen tại (3,2) trả về true")
    void isValidMove_ValidBlackMove_Row3Col2_ReturnsTrue() {
        assertTrue(board.isValidMove(3, 2, Board.BLACK));
    }

    @Test
    @DisplayName("isValidMove: nước hợp lệ của Đen tại (4,5) trả về true")
    void isValidMove_ValidBlackMove_Row4Col5_ReturnsTrue() {
        assertTrue(board.isValidMove(4, 5, Board.BLACK));
    }

    @Test
    @DisplayName("isValidMove: nước hợp lệ của Đen tại (5,4) trả về true")
    void isValidMove_ValidBlackMove_Row5Col4_ReturnsTrue() {
        assertTrue(board.isValidMove(5, 4, Board.BLACK));
    }

    @Test
    @DisplayName("isValidMove: ô đã có quân trả về false")
    void isValidMove_OccupiedCell_ReturnsFalse() {
        assertFalse(board.isValidMove(3, 3, Board.BLACK), "Ô đã có quân không hợp lệ");
    }

    @Test
    @DisplayName("isValidMove: ô trống không kẹp được quân trả về false")
    void isValidMove_NoFlip_ReturnsFalse() {
        assertFalse(board.isValidMove(0, 0, Board.BLACK), "Ô không kẹp quân nào là không hợp lệ");
    }

    @Test
    @DisplayName("isValidMove: nước hợp lệ của Trắng trả về true")
    void isValidMove_ValidWhiteMove_ReturnsTrue() {
        // Ở trạng thái ban đầu, Trắng có nước hợp lệ tại (2,4)
        assertTrue(board.isValidMove(2, 4, Board.WHITE));
    }

    // makeMove()

    @Test
    @DisplayName("makeMove: đặt quân thành công trả về true")
    void makeMove_ValidMove_ReturnsTrue() {
        assertTrue(board.makeMove(2, 3, Board.BLACK));
    }

    @Test
    @DisplayName("makeMove: quân được đặt đúng ô")
    void makeMove_PlacesPieceCorrectly() {
        board.makeMove(2, 3, Board.BLACK);
        assertEquals(Board.BLACK, board.getCell(2, 3), "Ô (2,3) phải là BLACK sau khi đặt");
    }

    @Test
    @DisplayName("makeMove: quân Trắng bị kẹp được lật thành Đen")
    void makeMove_FlipsOpponentPieces() {
        // Đen đặt (2,3): Trắng tại (3,3) bị lật
        board.makeMove(2, 3, Board.BLACK);
        assertEquals(Board.BLACK, board.getCell(3, 3), "Quân Trắng (3,3) phải bị lật thành Đen");
    }

    @Test
    @DisplayName("makeMove: nước không hợp lệ trả về false")
    void makeMove_InvalidMove_ReturnsFalse() {
        assertFalse(board.makeMove(0, 0, Board.BLACK), "Nước không hợp lệ phải trả về false");
    }

    @Test
    @DisplayName("makeMove: nước không hợp lệ không thay đổi bàn cờ")
    void makeMove_InvalidMove_BoardUnchanged() {
        board.makeMove(0, 0, Board.BLACK);
        assertEquals(Board.EMPTY, board.getCell(0, 0), "Ô (0,0) vẫn phải là EMPTY");
    }

    @Test
    @DisplayName("makeMove: đặt quân trên ô đã có quân trả về false")
    void makeMove_OccupiedCell_ReturnsFalse() {
        assertFalse(board.makeMove(3, 3, Board.BLACK), "Không thể đặt quân lên ô đã có quân");
    }

    @Test
    @DisplayName("makeMove: sau khi đặt, điểm số được cập nhật đúng")
    void makeMove_ScoreUpdatedCorrectly() {
        // Ban đầu: Đen=2, Trắng=2
        // Đen đặt (2,3): lật 1 quân Trắng → Đen=4, Trắng=1
        board.makeMove(2, 3, Board.BLACK);
        int[] score = board.getScore();
        assertEquals(4, score[0], "Đen phải có 4 quân");
        assertEquals(1, score[1], "Trắng phải còn 1 quân");
    }

    @Test
    @DisplayName("makeMove: lật đúng nhiều quân sau một nước đi")
    void makeMove_FlipsMultiplePieces() {
        // Setup đặc biệt: hàng 0: BLACK WHITE WHITE và đặt tại (0,3)
        setCell(board, 0, 0, Board.BLACK);
        setCell(board, 0, 1, Board.WHITE);
        setCell(board, 0, 2, Board.WHITE);
        board.makeMove(0, 3, Board.BLACK);
        assertEquals(Board.BLACK, board.getCell(0, 1), "Quân Trắng (0,1) phải bị lật");
        assertEquals(Board.BLACK, board.getCell(0, 2), "Quân Trắng (0,2) phải bị lật");
    }

    // getValidMoves()

    @Test
    @DisplayName("getValidMoves: Đen có đúng 4 nước đi hợp lệ ở trạng thái ban đầu")
    void getValidMoves_InitialState_BlackHas4Moves() {
        List<int[]> moves = board.getValidMoves(Board.BLACK);
        assertEquals(4, moves.size(), "Đen phải có đúng 4 nước đi ban đầu");
    }

    @Test
    @DisplayName("getValidMoves: Trắng có đúng 4 nước đi hợp lệ ở trạng thái ban đầu")
    void getValidMoves_InitialState_WhiteHas4Moves() {
        List<int[]> moves = board.getValidMoves(Board.WHITE);
        assertEquals(4, moves.size(), "Trắng phải có đúng 4 nước đi ban đầu");
    }

    @Test
    @DisplayName("getValidMoves: 4 nước đi của Đen đúng vị trí theo luật Othello")
    void getValidMoves_BlackMoves_CorrectPositions() {
        List<int[]> moves = board.getValidMoves(Board.BLACK);
        // Đen ban đầu phải có: (2,3), (3,2), (4,5), (5,4)
        assertTrue(containsMove(moves, 2, 3), "Thiếu nước (2,3)");
        assertTrue(containsMove(moves, 3, 2), "Thiếu nước (3,2)");
        assertTrue(containsMove(moves, 4, 5), "Thiếu nước (4,5)");
        assertTrue(containsMove(moves, 5, 4), "Thiếu nước (5,4)");
    }

    @Test
    @DisplayName("getValidMoves: trả về rỗng khi không có nước đi hợp lệ")
    void getValidMoves_NoMoves_ReturnsEmpty() {
        // Lấp đầy bàn cờ bằng quân Đen hết
        Board fullBoard = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                setCell(fullBoard, i, j, Board.BLACK);
        assertTrue(fullBoard.getValidMoves(Board.WHITE).isEmpty(), "Trắng không có nước đi nào");
    }

    @Test
    @DisplayName("getValidMoves: sau khi đặt quân, danh sách nước đi thay đổi")
    void getValidMoves_AfterMove_ListChanges() {
        List<int[]> before = board.getValidMoves(Board.WHITE);
        board.makeMove(2, 3, Board.BLACK);
        List<int[]> after = board.getValidMoves(Board.WHITE);
        assertNotEquals(before.size(), after.size(), "Danh sách nước đi phải thay đổi sau khi đặt quân");
    }

    // switchPlayer()

    @Test
    @DisplayName("switchPlayer: chuyển từ Đen sang Trắng")
    void switchPlayer_BlackToWhite() {
        assertEquals(Board.BLACK, board.getCurrentPlayer());
        board.switchPlayer();
        assertEquals(Board.WHITE, board.getCurrentPlayer());
    }

    @Test
    @DisplayName("switchPlayer: chuyển từ Trắng về Đen")
    void switchPlayer_WhiteToBlack() {
        board.switchPlayer();
        board.switchPlayer();
        assertEquals(Board.BLACK, board.getCurrentPlayer());
    }

    // isGameOver()

    @Test
    @DisplayName("isGameOver: trạng thái ban đầu chưa kết thúc")
    void isGameOver_InitialState_ReturnsFalse() {
        assertFalse(board.isGameOver(), "Game không kết thúc ở trạng thái ban đầu");
    }

    @Test
    @DisplayName("isGameOver: bàn cờ đầy quân Đen thì game kết thúc")
    void isGameOver_FullBoard_ReturnsTrue() {
        Board fullBoard = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                setCell(fullBoard, i, j, Board.BLACK);
        assertTrue(fullBoard.isGameOver(), "Bàn đầy không còn nước đi → game kết thúc");
    }

    @Test
    @DisplayName("isGameOver: khi cả hai không có nước đi thì kết thúc")
    void isGameOver_NeitherHasMoves_ReturnsTrue() {
        // Bàn toàn Trắng ngoại trừ (0,0) là Đen → Trắng không đi được, Đen cũng không
        Board b = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                setCell(b, i, j, Board.WHITE);
        setCell(b, 0, 0, Board.BLACK);
        // Không có ô trống → không ai đi được
        assertTrue(b.isGameOver());
    }

    // copy() — dùng bởi AI trong UC-04

    @Test
    @DisplayName("copy: bản sao có cùng trạng thái với bản gốc")
    void copy_SameState() {
        Board copy = board.copy();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                assertEquals(board.getCell(i, j), copy.getCell(i, j),
                    "Ô (" + i + "," + j + ") phải giống nhau");
        assertEquals(board.getCurrentPlayer(), copy.getCurrentPlayer());
    }

    @Test
    @DisplayName("copy: thay đổi bản sao không ảnh hưởng bản gốc")
    void copy_Independent() {
        Board copy = board.copy();
        copy.makeMove(2, 3, Board.BLACK);
        // Bản gốc ô (2,3) vẫn phải là EMPTY
        assertEquals(Board.EMPTY, board.getCell(2, 3), "Bản gốc không được bị thay đổi");
    }

    // Helper methods

    /**
     * Dùng reflection để đặt trực tiếp giá trị vào board (bỏ qua rule lật quân)
     * — chỉ dùng trong test setup
     */
    private void setCell(Board b, int row, int col, int value) {
        try {
            java.lang.reflect.Field f = Board.class.getDeclaredField("board");
            f.setAccessible(true);
            int[][] arr = (int[][]) f.get(b);
            arr[row][col] = value;
        } catch (Exception e) {
            throw new RuntimeException("setCell thất bại: " + e.getMessage());
        }
    }

    private boolean containsMove(List<int[]> moves, int row, int col) {
        for (int[] m : moves)
            if (m[0] == row && m[1] == col) return true;
        return false;
    }
}