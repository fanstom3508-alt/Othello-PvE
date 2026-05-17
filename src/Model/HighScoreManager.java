package Model;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

// UC-04 / UC-05: Quản lý bảng điểm cao — lưu và đọc file cục bộ
public class HighScoreManager {
    // Đường dẫn file lưu điểm cao (cùng thư mục với ứng dụng)
    private static final String FILE_NAME = "highscores.dat";
    private static final int MAX_ENTRIES = 10;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Một dòng điểm cao
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        String playerName;
        int score;
        Date date;

        public ScoreEntry(String playerName, int score, Date date) {
            this.playerName = playerName;
            this.score = score;
            this.date = date;
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

        // Sắp xếp: điểm giảm dần, nếu trùng điểm thì ai đạt trước xếp trên
        @Override
        public int compareTo(ScoreEntry other) {
            if (this.score != other.score) {
                return Integer.compare(other.score, this.score); // giảm dần
            }
            return this.date.compareTo(other.date); // ai đạt trước xếp trên
        }
    }

    // Đọc danh sách điểm cao từ file
    public static List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return scores; // Chưa có dữ liệu → trả list rỗng
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    int score = Integer.parseInt(parts[1].trim());
                    Date date = DATE_FORMAT.parse(parts[2].trim());
                    scores.add(new ScoreEntry(name, score, date));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc file điểm cao: " + e.getMessage());
        }

        Collections.sort(scores);
        return scores;
    }

    // Lưu danh sách điểm cao xuống file
    private static void saveScores(List<ScoreEntry> scores) {
        Collections.sort(scores);
        // Chỉ giữ top MAX_ENTRIES
        while (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (ScoreEntry entry : scores) {
                writer.write(entry.playerName + "|" + entry.score + "|" + DATE_FORMAT.format(entry.date));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Lỗi ghi file điểm cao: " + e.getMessage());
        }
    }

    // UC-04: Thêm điểm mới — mỗi tên chỉ lưu 1 bản ghi, cập nhật nếu điểm cao hơn (BRule-09)
    public static boolean addScore(String playerName, int score) {
        List<ScoreEntry> scores = loadScores();

        ScoreEntry existing = null;
        for (ScoreEntry entry : scores) {
            if (entry.getPlayerName().equals(playerName)) {
                existing = entry;
                break;
            }
        }

        if (existing != null) {
            if (score <= existing.getScore())
                return false;
            scores.remove(existing);
        }

        ScoreEntry newEntry = new ScoreEntry(playerName, score, new Date());
        scores.add(newEntry);
        Collections.sort(scores);

        while (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1);
        }

        saveScores(scores);
        return scores.contains(newEntry);
    }

    // UC-05 5.1.1, 5.1.2: Đọc dữ liệu từ file và trả về top 10 đã sắp xếp giảm dần
    public static List<ScoreEntry> getTopScores() {
        List<ScoreEntry> scores = loadScores();
        // Giới hạn 10
        if (scores.size() > MAX_ENTRIES) {
            scores = scores.subList(0, MAX_ENTRIES);
        }
        return scores;
    }

    // UC-05 5.1.4: Tìm điểm cao nhất của một người chơi cụ thể
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
