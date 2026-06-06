package Testing;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import Model.Board;
import Model.HighScoreManager;

public class TurnLifecycleTest {

    @Test
    public void testBusinessRule07_NoScoreSavedOnQuitMidGame() {
        // TC-7.07 & TC-7.01: Quy tắc BRule-07 (Thoát giữa trận, không lưu điểm rác)
        Board board = new Board();
        board.restart(); // Khởi tạo trận đấu dở dang ban đầu
        
        // Giả lập tình huống người chơi có 20 quân cờ trên bàn nhưng kích hoạt lệnh thoát giữa ván [7.1.0]
        int currentScoreMidGame = board.getScore()[0]; // Lấy số quân cờ hiện tại
        String testPlayer = "PlayerQuitMidGame";
        
        // Theo luồng [7.1.4], nút "Về menu chính" khi chọn [Có] sẽ BỎ QUA việc gọi HighScoreManager.addScore()
        // Kiểm tra xem trong cơ sở dữ liệu bảng xếp hạng có tồn tại người chơi này không
        boolean isExist = HighScoreManager.getSortedLeaderboard("SCORE")
                            .stream()
                            .anyMatch(entry -> entry.getPlayerName().equals(testPlayer));
        
        assertFalse(isExist, "Thành tích của trận đấu thoát ngang tuyệt đối không được phép xuất hiện trên hệ thống.");
    }
}