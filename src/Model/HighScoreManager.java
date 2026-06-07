package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// UC-04 / UC-05: Quan ly bang diem cao, luu va doc file cuc bo.
//Phục vụ chính cho UC-05 (Xem bảng xếp hạng) và UC-07 (Đồng bộ trạng thái trận đấu).
public class HighScoreManager {
    private static final String FILE_NAME = "highscores.dat";
    private static final int MAX_ENTRIES = 10;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        String playerName;
        int score;
        Date date;

     // Cải tiến cấu trúc dữ liệu: Bổ sung thống kê để phục vụ luồng [5.1.5] và [5.1.6] của UC-05 (lọc theo hồ sơ người chơi).
        int totalMatches;
        int wins;
        int losses;
        int currentWinStreak;
        int maxWinStreak;

        public ScoreEntry(String playerName, int score, Date date) {
            this(playerName, score, date, 1, 0, 0, 0, 0);
        }

        public ScoreEntry(String playerName, int score, Date date, int totalMatches, int wins, int losses,
                int currentWinStreak, int maxWinStreak) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
            this.totalMatches = totalMatches;
            this.wins = wins;
            this.losses = losses;
            this.currentWinStreak = currentWinStreak;
            this.maxWinStreak = maxWinStreak;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        public Date getDate() {
            return date;
        }

        public int getTotalMatches() {
            return totalMatches;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public int getCurrentWinStreak() {
            return currentWinStreak;
        }

        public int getMaxWinStreak() {
            return maxWinStreak;
        }

     // Trận hòa được suy ra từ tổng trận, thắng và thua.
        public int getDraws() {
            return Math.max(0, totalMatches - wins - losses);
        }

     // Phục vụ cho bộ lọc bảng xếp hạng của luồng [5.1.7] UC-05.
        public double getWinRate() {
            if (totalMatches == 0) {
                return 0.0;
            }
            return ((double) wins / totalMatches) * 100.0;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            if (this.score != other.score) {
                return Integer.compare(other.score, this.score);
            }
            return this.date.compareTo(other.date);
        }
    }

 // [5.1.2] Được LeaderboardDialog gọi để tải danh sách kỷ lục.
    public static List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return scores;// [5.2.1] Nếu file chưa tồn tại (Ván đầu tiên)
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
         // [5.1.3] Thực hiện mở tệp cục bộ, đọc tuần tự dữ liệu và phân tích (parse) thành ScoreEntry.
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    Date date = DATE_FORMAT.parse(parts[2].trim());
                 // Đọc tương thích cả file cũ 3 cột và file mới 8 cột để không làm hỏng dữ liệu đã có.
                    if (parts.length >= 8) {
                        int totalMatches = Integer.parseInt(parts[3].trim());
                        int wins = Integer.parseInt(parts[4].trim());
                        int losses = Integer.parseInt(parts[5].trim());
                        int currentWinStreak = Integer.parseInt(parts[6].trim());
                        int maxWinStreak = Integer.parseInt(parts[7].trim());
                        scores.add(new ScoreEntry(name, score, date, totalMatches, wins, losses,
                                currentWinStreak, maxWinStreak));
                    } else {
                        scores.add(new ScoreEntry(name, score, date));
                    }
                }
            }
        } catch (Exception e) {
        	// [5.2.1.1] Bắt ngoại lệ IOException/Parse, tự động khởi tạo danh sách mảng rỗng trả về an toàn.
            System.err.println("Loi doc file diem cao: " + e.getMessage());
        }

        Collections.sort(scores);
        return scores;
    }

 // Ghi file 8 cột, giữ lại toàn bộ lịch sử (không cắt Top 10) để bảo toàn tổng quan dữ liệu người chơi.
    private static void saveScores(List<ScoreEntry> scores) {
        Collections.sort(scores);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (ScoreEntry entry : scores) {
                writer.write(entry.playerName + "|"
                        + entry.score + "|"
                        + DATE_FORMAT.format(entry.date) + "|"
                        + entry.totalMatches + "|"
                        + entry.wins + "|"
                        + entry.losses + "|"
                        + entry.currentWinStreak + "|"
                        + entry.maxWinStreak);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Loi ghi file diem cao: " + e.getMessage());
        }
    }
 // KHỐI HÀM LƯU ĐIỂM - LIÊN QUAN CHẶT CHẼ ĐẾN UC-07
    public static boolean addScore(String playerName, int score) {
        return saveMatchResult(playerName, score, false, false, false);
    }

    public static boolean addScore(String playerName, int score, boolean playerWon, boolean playerLost) {
        return saveMatchResult(playerName, score, true, playerWon, playerLost);
    }

    public static boolean addMatchResult(String playerName, int score, boolean playerWon, boolean playerLost) {
        return saveMatchResult(playerName, score, true, playerWon, playerLost);
    }

 // [7.1.4] Áp dụng quy tắc BRule-07: Hàm này KHÔNG ĐƯỢC GỌI nếu người chơi thoát khi trận đấu dang dở (gameEnded == false).
    // [7.2.1.1] Hàm này chỉ được thực thi hợp lệ từ UC-04 khi ván đấu đã kết thúc hoàn toàn (gameEnded == true).
    private static boolean saveMatchResult(String playerName, int score, boolean updateStats, boolean playerWon,
            boolean playerLost) {
        List<ScoreEntry> scores = loadScores();
        ScoreEntry existing = findByName(scores, playerName);

        if (existing == null) {
            existing = new ScoreEntry(playerName, score, new Date(), 0, 0, 0, 0, 0);
            scores.add(existing);
        }

        if (updateStats) {
            applyMatchStats(existing, playerWon, playerLost);
        }

        if (score > existing.score) {
            existing.score = score;
            existing.date = new Date();
        }

        Collections.sort(scores);
        saveScores(scores);
        int rank = scores.indexOf(existing);
        return rank >= 0 && rank < MAX_ENTRIES;// Trả về true nếu lọt Top 10
    }

    private static ScoreEntry findByName(List<ScoreEntry> scores, String playerName) {
        for (ScoreEntry entry : scores) {
            if (entry.getPlayerName().equals(playerName)) {
                return entry;
            }
        }
        return null;
    }

 // Cập nhật thống kê trận đấu mà không can thiệp vào logic của Board hay Player.
    private static void applyMatchStats(ScoreEntry entry, boolean playerWon, boolean playerLost) {
        entry.totalMatches++;
        if (playerWon) {
            entry.wins++;
            entry.currentWinStreak++;
            entry.maxWinStreak = Math.max(entry.maxWinStreak, entry.currentWinStreak);
        } else {
            if (playerLost) {
                entry.losses++;
            }
            entry.currentWinStreak = 0;
        }
    }
 // KHỐI HÀM LỌC BẢNG XẾP HẠNG - LIÊN QUAN CHẶT CHẼ ĐẾN UC-05
    // UC-05 cai tien: ho tro loc bang xep hang theo diem, ty le thang, tong thang va chuoi thang.
 // [5.1.7] Hàm xử lý tái cấu trúc bảng điểm dựa trên tiêu chí mới (criterion) được truyền vào.
    public static List<ScoreEntry> getSortedLeaderboard(String criteria) {
        List<ScoreEntry> scores = loadScores();
        String key = criteria == null ? "SCORE" : criteria.toUpperCase();

        // [5.1.8] Áp dụng bộ so sánh dữ liệu (Comparator) tương ứng để sắp xếp lại danh sách ScoreEntry.
        switch (key) {
            case "WIN_RATE":
                scores.sort((e1, e2) -> {
                    int comp = Double.compare(e2.getWinRate(), e1.getWinRate());
                    if (comp != 0) return comp;
                    return Integer.compare(e2.getWins(), e1.getWins());
                });
                break;
            case "TOTAL_WINS":
                scores.sort((e1, e2) -> {
                    int comp = Integer.compare(e2.getWins(), e1.getWins());
                    if (comp != 0) return comp;
                    return Double.compare(e2.getWinRate(), e1.getWinRate());
                });
                break;
            case "WIN_STREAK":
                scores.sort((e1, e2) -> {
                    int comp = Integer.compare(e2.getMaxWinStreak(), e1.getMaxWinStreak());
                    if (comp != 0) return comp;
                    return Integer.compare(e2.getWins(), e1.getWins());
                });
                break;
            case "SCORE":
            default:
                Collections.sort(scores);
                break;
        }
        return scores;
    }

    // [5.1.4] Mặc định ban đầu, sắp xếp theo "SCORE" giảm dần và cắt lấy tối đa 10 bản ghi đầu tiên.
    public static List<ScoreEntry> getTopScores() {
        List<ScoreEntry> scores = getSortedLeaderboard("SCORE");
        if (scores.size() > MAX_ENTRIES) {
            return new ArrayList<>(scores.subList(0, MAX_ENTRIES));
        }
        return scores;
    }

    public static int getBestScore(String playerName) {
        List<ScoreEntry> scores = loadScores();
        int best = -1;
        for (ScoreEntry entry : scores) {
            if (entry.playerName.equals(playerName) && entry.score > best) {
                best = entry.score;
            }
        }
        return best;
    }
}
