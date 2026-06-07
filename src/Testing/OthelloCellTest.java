package Testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import View.OthelloCell;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OthelloCell (UC-1.11 / UC-1.21 / UC-1.23)
 */
public class OthelloCellTest {
	//OthelloCellTest kiểm tra phần nhỏ nhưng quan trọng của UI: lớp OthelloCell. Cụ thể:

	//Test đầu xác nhận setState và getState hoạt động đúng với các trạng thái EMPTY/BLACK/WHITE/VALID_MOVE (đảm bảo ô được cập nhật chính xác khi logic trò chơi yêu cầu).
	//Test thứ hai kiểm tra các hằng số state là khác nhau (tránh trùng giá trị gây lỗi).
    @Test
    @DisplayName("setState/getState: set và lấy state hoạt động")
    void setStateAndGetState() {
        OthelloCell cell = new OthelloCell();
        assertEquals(OthelloCell.EMPTY, cell.getState(), "Mặc định phải là EMPTY");

        cell.setState(OthelloCell.BLACK);
        assertEquals(OthelloCell.BLACK, cell.getState(), "State phải là BLACK sau khi set");

        cell.setState(OthelloCell.WHITE);
        assertEquals(OthelloCell.WHITE, cell.getState(), "State phải là WHITE sau khi set");

        cell.setState(OthelloCell.VALID_MOVE);
        assertEquals(OthelloCell.VALID_MOVE, cell.getState(), "State phải là VALID_MOVE sau khi set");
    }

    @Test
    @DisplayName("constants validity: giá trị hằng số không trùng nhau")
    void constantsAreDistinct() {
        assertNotEquals(OthelloCell.EMPTY, OthelloCell.BLACK);
        assertNotEquals(OthelloCell.EMPTY, OthelloCell.WHITE);
        assertNotEquals(OthelloCell.EMPTY, OthelloCell.VALID_MOVE);
        assertNotEquals(OthelloCell.BLACK, OthelloCell.WHITE);
        assertNotEquals(OthelloCell.BLACK, OthelloCell.VALID_MOVE);
        assertNotEquals(OthelloCell.WHITE, OthelloCell.VALID_MOVE);
    }
}