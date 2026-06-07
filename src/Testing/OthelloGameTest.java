package Testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Board;
import View.OthelloGame;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OthelloGameTest {

    private OthelloGame game;
    private Board board;

    @BeforeEach
    public void setUp() throws Exception {
        // Khởi tạo game với người chơi cầm cờ Đen (1)
        game = new OthelloGame(Board.BLACK);

        // Dùng Reflection để lấy đối tượng Board bên trong OthelloGame ra để kiểm tra
        Field boardField = OthelloGame.class.getDeclaredField("board");
        boardField.setAccessible(true);
        board = (Board) boardField.get(game);
    }

    @Test
    public void testConstructor_Initialization() throws Exception {
        assertNotNull(board, "Bàn cờ phải được khởi tạo");
        
        // Kiểm tra màu cờ của người chơi (humanColor)
        Field humanColorField = OthelloGame.class.getDeclaredField("humanColor");
        humanColorField.setAccessible(true);
        int humanColor = (int) humanColorField.get(game);
        
        assertEquals(Board.BLACK, humanColor, "Người chơi phải được gán màu Đen (1)");
    }

    @Test
    public void testHandleMove_ValidMove() throws Exception {
        // Lấy phương thức handleMove(int, int) ra bằng Reflection
        Method handleMoveMethod = OthelloGame.class.getDeclaredMethod("handleMove", int.class, int.class);
        handleMoveMethod.setAccessible(true);

        // Giả sử (2, 3) là một nước đi hợp lệ đầu game cho cờ Đen
        int targetRow = 2;
        int targetCol = 3;
        
        // Ghi nhận lượt hiện tại
        int initialPlayer = board.getCurrentPlayer();
        assertEquals(Board.BLACK, initialPlayer, "Lượt đầu tiên phải là cờ Đen");

        // Gọi hàm handleMove thông qua reflection
        try {
            handleMoveMethod.invoke(game, targetRow, targetCol);
        } catch (Exception e) {
            // Nếu có lỗi cập nhật UI (NullPointerException do chưa bật Frame), ta có thể bỏ qua
            // vì mục đích là test logic game.
        }

        // Kiểm tra xem cờ đã được đặt xuống bàn chưa
        assertEquals(Board.BLACK, board.getCell(targetRow, targetCol), "Ô (2,3) phải có cờ Đen");
        
        // Lượt chơi phải được đổi sang Máy (Trắng)
        assertEquals(Board.WHITE, board.getCurrentPlayer(), "Phải chuyển sang lượt cờ Trắng sau khi đi");
    }

    @Test
    public void testHandleMove_InvalidMove() throws Exception {
        Method handleMoveMethod = OthelloGame.class.getDeclaredMethod("handleMove", int.class, int.class);
        handleMoveMethod.setAccessible(true);

        // Nước đi (0,0) là nước đi sai luật lúc khởi đầu
        int initialBlackCount = getPieceCount(Board.BLACK);
        
        try {
            handleMoveMethod.invoke(game, 0, 0);
        } catch (Exception e) {}

        // Kiểm tra xem ô (0,0) vẫn trống
        assertEquals(Board.EMPTY, board.getCell(0, 0), "Ô (0,0) phải trống do nước đi không hợp lệ");
        
        // Số lượng cờ trên bàn không đổi
        assertEquals(initialBlackCount, getPieceCount(Board.BLACK), "Số cờ đen không được tăng lên");
        
        // Lượt vẫn giữ nguyên là cờ Đen
        assertEquals(Board.BLACK, board.getCurrentPlayer(), "Không được đổi lượt nếu đi sai");
    }

    @Test
    public void testTurnBegin_GameOverCondition() throws Exception {
        Method turnBeginMethod = OthelloGame.class.getDeclaredMethod("TurnBegin");
        turnBeginMethod.setAccessible(true);

        // Giả lập trạng thái ván cờ kết thúc (lấp đầy bàn cờ)
        for (int r = 0; r < board.getLength(); r++) {
            for (int c = 0; c < board.getLength(); c++) {
                board.makeMove(r, c, Board.BLACK); // Ép đặt cờ để isGameOver() == true
            }
        }

        assertTrue(board.isGameOver(), "Bàn cờ phải ở trạng thái kết thúc");

        // Chạy TurnBegin()
        try {
            turnBeginMethod.invoke(game);
        } catch (Exception e) {
            // Thường sẽ có hộp thoại Dialog hiện lên (JOptionPane) khi game over.
        }
        
        // Kiểm tra biến gameEnded (nếu có) thông qua Reflection
        Field gameEndedField = OthelloGame.class.getDeclaredField("gameEnded");
        gameEndedField.setAccessible(true);
        boolean isEnded = (boolean) gameEndedField.get(game);
        
        assertTrue(isEnded, "Biến gameEnded phải được set thành true");
    }

    // Hàm phụ trợ đếm số cờ trên bàn
    private int getPieceCount(int color) {
        int count = 0;
        for (int r = 0; r < board.getLength(); r++) {
            for (int c = 0; c < board.getLength(); c++) {
                if (board.getCell(r, c) == color) count++;
            }
        }
        return count;
    }
}