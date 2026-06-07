package Testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.HighScoreManager;
import Model.HighScoreManager.ScoreEntry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit Test cho lớp HighScoreManager
 * Đặt file này ở thư mục nguồn test và sử dụng Default Package để gọi trực tiếp các lớp trong game.
 */
public class HighScoreManagerTest {

    @BeforeEach
    public void setUp() {
        // Khởi tạo hoặc dọn dẹp dữ liệu giả lập về trạng thái ban đầu trước mỗi ca test
        try {
            List<ScoreEntry> scores = HighScoreManager.loadScores();
            if (scores != null) {
                scores.clear();
            }
        } catch (Exception e) {
            // Bỏ qua nếu danh sách trả về là Unmodifiable
        }
    }

    @Test
    public void testAddScoreAndLimitTop10() {
        // TC-5.06: Kiểm thử giới hạn hiển thị hệ thống tối đa TOP 10 dòng dữ liệu
        for (int i = 1; i <= 12; i++) {
            HighScoreManager.addScore("Player" + i, 10 + i, true, false);
        }
        
        List<ScoreEntry> leaderboard = HighScoreManager.getSortedLeaderboard("SCORE");
        assertNotNull(leaderboard, "Bảng xếp hạng không được rỗng");
        assertTrue(leaderboard.size() <= 10, "Bảng xếp hạng bắt buộc phải cắt bớt, chỉ giữ lại tối đa TOP 10 dòng.");
    }

    @Test
    public void testBusinessRule09_UpdateOnlyHigherScore() {
        // TC-5.07 & TC-5.08: Quy tắc nghiệp vụ BRule-09 (Chỉ cập nhật khi điểm mới cao hơn)
        HighScoreManager.addScore("TranLePhucAn", 30, true, false);
        
        // Thử nghiệm cập nhật điểm thấp hơn (20 điểm) -> Kết quả trả về phải là false, giữ nguyên 30
        boolean checkLower = HighScoreManager.addScore("TranLePhucAn", 20, false, true);
        assertFalse(checkLower, "Không được cập nhật khi điểm số mới thấp hơn điểm số cũ.");
        
        List<ScoreEntry> leaderboard = HighScoreManager.getSortedLeaderboard("SCORE");
        if (leaderboard != null && !leaderboard.isEmpty()) {
            assertEquals(30, leaderboard.get(0).getScore(), "Điểm số cũ phải được giữ nguyên.");
        }
        
        // Thử nghiệm cập nhật điểm cao hơn (45 điểm) -> Kết quả trả về phải là true và ghi đè
        boolean checkHigher = HighScoreManager.addScore("TranLePhucAn", 45, true, false);
        assertTrue(checkHigher, "Phải cập nhật thành công khi điểm số mới cao hơn điểm số cũ.");
        
        leaderboard = HighScoreManager.getSortedLeaderboard("SCORE");
        if (leaderboard != null && !leaderboard.isEmpty()) {
            assertEquals(45, leaderboard.get(0).getScore(), "Điểm số mới phải được ghi nhận.");
        }
    }
}