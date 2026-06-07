package Testing; // Hoặc để trống nếu bạn đặt trực tiếp trong default package của thư mục test

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

// ĐỒNG BỘ IMPORT ĐÚNG VỚI PACKAGE Model
import Model.HighScoreManager;
import Model.HighScoreManager.ScoreEntry;

public class HighScoreManagerTest {

    @BeforeEach
    public void setUp() {
        try {
            List<ScoreEntry> scores = HighScoreManager.loadScores();
            if (scores != null) {
                scores.clear();
            }
        } catch (Exception e) {
            // Tránh lỗi bất đồng bộ dòng lệnh
        }
    }

    @Test
    public void testAddScoreAndLimitTop10() {
        for (int i = 1; i <= 12; i++) {
            HighScoreManager.addScore("Player" + i, 10 + i, true, false);
        }
        
        List<ScoreEntry> leaderboard = HighScoreManager.getSortedLeaderboard("SCORE");
        assertNotNull(leaderboard);
        assertTrue(leaderboard.size() <= 10, "Bảng xếp hạng bắt buộc phải cắt bớt, chỉ giữ lại tối đa TOP 10 dòng.");
    }

    @Test
    public void testBusinessRule09_UpdateOnlyHigherScore() {
        HighScoreManager.addScore("TranLePhucAn", 30, true, false);
        
        // Thử nghiệm cập nhật điểm thấp hơn (20 điểm) -> Phải trả về false
        boolean checkLower = HighScoreManager.addScore("TranLePhucAn", 20, false, true);
        assertFalse(checkLower);
        
        // Thử nghiệm cập nhật điểm cao hơn (45 điểm) -> Phải trả về true
        boolean checkHigher = HighScoreManager.addScore("TranLePhucAn", 45, true, false);
        assertTrue(checkHigher);
    }
}