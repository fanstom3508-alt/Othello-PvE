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

/**
 * UC-04 / UC-05: Quản lý bảng điểm cao, lưu và đọc file cục bộ.
 * Phục vụ chính cho UC-05 (Xem bảng xếp hạng) và UC-07 (Đánh giá & thoát ván đấu an toàn).
 */
public class HighScoreManager {
    private static final String FILE_NAME = "highscores.dat";
    private static final int MAX_ENTRIES = 10;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        public String playerName;
        public int score;
        public Date date;

        // Cải tiến cấu trúc dữ liệu: Bổ sung thống kê để phục vụ luồng [5.1.5] và [5.1.6] của UC-05.
        public int totalMatches;
        public int wins;
        public int losses;
        public int currentWinStreak;
        public int maxWinStreak;

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

        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public Date getDate() { return date; }
        public int getTotalMatches() { return totalMatches; }
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getCurrentWinStreak() { return currentWinStreak; }
        public int getMaxWinStreak() { return maxWinStreak; }

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

    // [5.1.2] Bộ điều khiển giao diện gọi phương thức tĩnh này để bắt đầu tải danh sách kỷ lục
    public static synchronized List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(FILE_NAME);
        
        // [5.2.1.1] Tệp dữ liệu kỷ lục highscores.dat chưa tồn tại: tự động khởi tạo danh sách mảng rỗng và trả về an toàn
        if (!file.exists()) {
            return scores;
        }

        // [5.1.3] Hệ thống thực hiện mở tệp cục bộ highscores.dat, đọc tuần tự dữ liệu và phân tích thành ScoreEntry
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    Date date = DATE_FORMAT.parse(parts[2]);
                    int totalMatches = Integer.parseInt(parts[3]);
                    int wins = Integer.parseInt(parts[4]);
                    int losses = Integer.parseInt(parts[5]);
                    int currentWinStreak = Integer.parseInt(parts[6]);
                    int maxWinStreak = Integer.parseInt(parts[7]);

                    scores.add(new ScoreEntry(name, score, date, totalMatches, wins, losses, currentWinStreak, maxWinStreak));
                }
            }
        } catch (IOException e) {
            // [5.2.1.1] Bắt ngoại lệ IOException, tự động khởi tạo danh sách mảng rỗng (new ArrayList<>()) và trả về an toàn.
            System.err.println("Ngoại lệ IO khi đọc file: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Lỗi định dạng dữ liệu file: " + e.getMessage());
            return new ArrayList<>();
        }
        return scores;
    }

    // [5.1.4] Mặc định ban đầu, hệ thống gọi hàm sắp xếp danh sách theo tiêu chí điểm số giảm dần ("SCORE") và cắt lấy tối đa 10 bản ghi đầu tiên.
    public static List<ScoreEntry> getTopScores() {
        List<ScoreEntry> scores = getSortedLeaderboard("SCORE");
        if (scores.size() > MAX_ENTRIES) {
            return new ArrayList<>(scores.subList(0, MAX_ENTRIES));
        }
        return scores;
    }

    // KHỐI HÀM LỌC BẢNG XẾP HẠNG - LIÊN QUAN CHẶT CHẼ ĐẾN UC-05
    // [5.1.7] Hệ thống bắt sự kiện thay đổi, gọi hàm xử lý tái cấu trúc bảng điểm dựa trên tiêu chí mới được truyền vào.
    public static List<ScoreEntry> getSortedLeaderboard(String criteria) {
        List<ScoreEntry> scores = loadScores();
        String key = criteria == null ? "SCORE" : criteria.toUpperCase();

        // [5.1.8] Lớp nghiệp vụ áp dụng bộ so sánh dữ liệu (Comparator) tương ứng để sắp xếp lại danh sách ScoreEntry theo tiêu chí mới.
        switch (key) {
            case "WIN_RATE":
                scores.sort((e1, e2) -> {
                    int comp = Double.compare(e2.getWinRate(), e1.getWinRate());
                    if (comp != 0) return comp;
                    return Integer.compare(e2.getScore(), e1.getScore());
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

    // Ghi file toàn bộ 8 cột dữ liệu để duy trì lịch sử tổng quan của người chơi.
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
            System.err.println("Lỗi ghi file điểm cao: " + e.getMessage());
        }
    }

    // KHỐI HÀM LƯU ĐIỂM KỶ LỤC VÀ ĐỒNG BỘ TRẬN ĐẤU - LIÊN QUAN CHẶT CHẼ ĐẾN UC-07
    public static boolean addScore(String playerName, int score) {
        return saveMatchResult(playerName, score, false, false, false);
    }

    public static boolean addScore(String playerName, int score, boolean playerWon, boolean playerLost) {
        return saveMatchResult(playerName, score, true, playerWon, playerLost);
    }

    public static boolean addMatchResult(String playerName, int score, boolean playerWon, boolean playerLost) {
        return saveMatchResult(playerName, score, true, playerWon, playerLost);
    }

    // [7.1.4] Áp dụng quy tắc BRule-07: Hệ thống trực tiếp bỏ qua việc gọi hàm HighScoreManager.addScore(), điểm số của trận đấu dở dang không được phép ghi lại nếu người chơi nhấn dứt khoát huỷ trận (gameEnded == false).
    // [7.2.1] Trận đấu đã kết thúc trước đó (gameEnded == true) khi người chơi chọn lệnh quay về menu hoặc hoàn thành bình thường, hàm này mới được thực thi hợp lệ.
    private static boolean saveMatchResult(String playerName, int score, boolean updateStats, boolean playerWon, boolean playerLost) {
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
        return rank >= 0 && rank < MAX_ENTRIES; // Trả về true nếu lọt vào top 10 bảng xếp hạng
    }

    private static ScoreEntry findByName(List<ScoreEntry> scores, String playerName) {
        for (ScoreEntry entry : scores) {
            if (entry.getPlayerName().equalsIgnoreCase(playerName)) {
                return entry;
            }
        }
        return null;
    }

    // Cập nhật thống kê trận đấu phục vụ lâu dài cho hồ sơ người chơi.
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

    public static int getBestScore(String playerName) {
        List<ScoreEntry> scores = loadScores();
        int best = -1;
        for (ScoreEntry entry : scores) {
            if (entry.playerName.equalsIgnoreCase(playerName) && entry.score > best) {
                best = entry.score;
            }
        }
        return best;
    }
}