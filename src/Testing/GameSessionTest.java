package Testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Controller.GameSession;
import Model.Board;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Development Testing (Unit Testing) cho UC-02 – phần phát triển thêm
 * Tác giả: Phan Quang Huy
 *
 * Kiểm thử logic lưu độ khó vào GameSession (bước 2.1.10).
 * Lưu ý: JOptionPane.showConfirmDialog() là thành phần UI (Swing),
 * không thể kiểm thử trực tiếp bằng JUnit mà không có môi trường GUI.
 * Do đó, Unit Test chỉ kiểm thử phần logic xử lý dữ liệu (GameSession).
 */
public class GameSessionTest {

    @BeforeEach
    void setUp() {
        // Reset về trạng thái mặc định trước mỗi test (mặc định Khó = 20)
        GameSession.setDifficulty(20);
        GameSession.setPlayerName(null);
        GameSession.setPlayerColor(Board.BLACK);
    }

    @Test
    @DisplayName("TC-2.09a: setDifficulty(5) lưu depth Dễ thành công")
    void setDifficulty_Easy_Returns5() {
        GameSession.setDifficulty(5);
        assertEquals(5, GameSession.getDifficulty());
    }

    @Test
    @DisplayName("TC-2.02a: setDifficulty(10) lưu depth Trung bình thành công")
    void setDifficulty_Medium_Returns10() {
        GameSession.setDifficulty(10);
        assertEquals(10, GameSession.getDifficulty());
    }

    @Test
    @DisplayName("TC-2.09b: setDifficulty(20) lưu depth Khó thành công")
    void setDifficulty_Hard_Returns20() {
        GameSession.setDifficulty(20);
        assertEquals(20, GameSession.getDifficulty());
    }

    @Test
    @DisplayName("TC-2.09c: Giá trị mặc định của difficulty là 20 (Khó)")
    void getDifficulty_Default_Returns20() {
        // Luồng 2.5.1 nhánh YES: không chọn → mặc định depth=20
        assertEquals(20, GameSession.getDifficulty());
    }

    @Test
    @DisplayName("TC-2.09d: setDifficulty ghi đè giá trị cũ thành công")
    void setDifficulty_Overwrite_Success() {
        GameSession.setDifficulty(5);
        GameSession.setDifficulty(10);
        assertEquals(10, GameSession.getDifficulty());
    }

    @Test
    @DisplayName("TC-2.12: GameSession lưu đầy đủ tên, màu, độ khó sau khi thiết lập")
    void gameSession_StoresAllFields_Correctly() {
        // Mô phỏng kết quả sau khi hoàn tất UC-02 với chức năng mới
        GameSession.setPlayerName("Alpha");
        GameSession.setPlayerColor(Board.WHITE);
        GameSession.setDifficulty(5);

        assertAll(
            () -> assertEquals("Alpha", GameSession.getPlayerName()),
            () -> assertEquals(Board.WHITE, GameSession.getPlayerColor()),
            () -> assertEquals(5, GameSession.getDifficulty())
        );
    }

    @Test
    @DisplayName("TC-2.02b: Luồng chính TC-2.02 – tên hợp lệ, màu Trắng, độ khó Trung bình")
    void gameSession_TC202_FullFlow() {
        GameSession.setPlayerName("Test");
        GameSession.setPlayerColor(Board.WHITE);
        GameSession.setDifficulty(10);

        assertAll(
            () -> assertEquals("Test", GameSession.getPlayerName()),
            () -> assertEquals(Board.WHITE, GameSession.getPlayerColor()),
            () -> assertEquals(10, GameSession.getDifficulty())
        );
    }
}
