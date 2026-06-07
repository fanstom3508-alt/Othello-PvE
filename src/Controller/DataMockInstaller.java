package Controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Lớp tiện ích tự động sinh dữ liệu lịch sử đấu đa dạng tiêu chí để test UC-05
 * ĐÃ ĐỒNG BỘ 100% CẤU TRÚC DẤU PHÂN CÁCH '|' VÀ ĐỊNH DẠNG SCHEMA 8 CỘT VỚI HIGHSCOREMANAGER.
 */
public class DataMockInstaller {

    // Đường dẫn chuẩn file dat của hệ thống
    private static final String HIGH_SCORE_FILE = "highscores.dat"; 
    
    // Đồng bộ cấu trúc thời gian có cả Giờ:Phút giống HighScoreManager
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU KHỞI TẠO DỮ LIỆU LỊCH SỬ ĐẤU GIẢ LẬP ===");
        generateMockData();
        System.out.println("=== KHỞI TẠO THÀNH CÔNG! HÃY MỞ BẢNG XẾP HẠNG ĐỂ KIỂM THỬ ===");
    }

    private static void generateMockData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE, false))) {
            
            // Khởi tạo mốc thời gian giả lập
            Date t1 = getDateDaysAgo(0);  // Hôm nay
            Date t2 = getDateDaysAgo(1);  // Hôm qua
            Date t3 = getDateDaysAgo(3);  // 3 ngày trước
            Date t4 = getDateDaysAgo(7);  // 1 tuần trước

            // Nạp dữ liệu giả lập chuẩn 8 cột (Tên, Điểm, Ngày giờ, Tổng trận, Thắng, Thua, Chuỗi hiện tại, Chuỗi thắng max)
            writeLine(writer, "NguyenVanA", 96, t1, 10, 10, 0, 10, 10);
            writeLine(writer, "Player_Pro", 35, t1, 12, 9, 3, 5, 8);
            writeLine(writer, "Player", 64, t2, 5, 3, 2, 1, 3); // Tên trùng để kiểm thử Highlight dòng
            writeLine(writer, "LuckyGuy", 55, t3, 2, 2, 0, 2, 2);
            writeLine(writer, "BinhMinh", 70, t4, 10, 6, 4, 0, 4);
            writeLine(writer, "HoangHon", 62, t4, 10, 6, 4, 2, 3);
            writeLine(writer, "GaCongNghiep", 20, t4, 15, 1, 14, 0, 1);
            writeLine(writer, "NoobMaster", 18, t2, 8, 2, 6, 0, 1);
            writeLine(writer, "Tester_01", 42, t3, 6, 3, 3, 2, 2);
            writeLine(writer, "Tester_02", 48, t1, 7, 4, 3, 3, 3);
            writeLine(writer, "Tester_03", 50, t2, 5, 3, 2, 0, 2);
            writeLine(writer, "ZeroHero", 30, t4, 4, 0, 4, 0, 0);

            writer.flush();
        } catch (IOException e) {
            System.err.println("Lỗi ghi file dữ liệu test: " + e.getMessage());
        }
    }

    private static void writeLine(BufferedWriter writer, String name, int score, Date date, int totalMatches, int wins, int losses, int currentStreak, int maxStreak) throws IOException {
        // Biến đổi đối tượng thời gian sang chuỗi văn bản dạng "dd/MM/yyyy HH:mm"
        String dateStr = DATE_FORMAT.format(date);
        
        // Chuẩn cấu trúc lưu trữ 8 trường dữ liệu của hệ thống cách nhau bởi dấu '|'
        String line = String.format("%s|%d|%s|%d|%d|%d|%d|%d", 
                name, score, dateStr, totalMatches, wins, losses, currentStreak, maxStreak);
        
        writer.write(line);
        writer.newLine();
    }

    private static Date getDateDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }
}