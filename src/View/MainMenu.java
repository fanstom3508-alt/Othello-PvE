package View;

import javax.swing.*;

import Controller.GameSession;
import Model.Board;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.URI;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Cờ Othello");
        setSize(520, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(new MenuPanel());
    }

    private class MenuPanel extends JPanel {

        private JButton btnPlay;
        private JButton btnLeaderboard; // UC-05: Nút xem bảng xếp hạng
        private JButton btnHowTo;

        public MenuPanel() {
            setLayout(null);
            setBackground(new Color(30, 60, 15));

            // UC-01: Nút bắt đầu game - chơi với máy
            btnPlay = createStyledButton("Chơi với máy", new Color(46, 160, 67), new Color(36, 130, 52));
            btnPlay.setBounds(110, 350, 300, 65);
            btnPlay.addActionListener(e -> startGame());
            add(btnPlay);

            // UC-05: Cấu hình nút Bảng xếp hạng trên giao diện chính
            btnLeaderboard = createStyledButton("Bảng xếp hạng", new Color(33, 150, 243), new Color(25, 118, 210));
            btnLeaderboard.setBounds(110, 440, 300, 65);
            // [5.1.0] Người chơi nhấn nút "Bảng xếp hạng" trên màn hình giao diện.
            btnLeaderboard.addActionListener(e -> openLeaderboard());
            add(btnLeaderboard);

            btnHowTo = createStyledButton("Cách chơi", new Color(255, 193, 7), new Color(213, 162, 2));
            btnHowTo.setBounds(110, 530, 300, 65);
            btnHowTo.addActionListener(e -> openHowToPlay());
            add(btnHowTo);
        }

        // UC-1: Vẽ nền, bàn cờ minh họa, tiêu đề
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();

            // Gradient nền
            GradientPaint bg = new GradientPaint(0, 0, new Color(20, 50, 10), 0, getHeight(), new Color(40, 80, 20));
            g2.setPaint(bg);
            g2.fillRect(0, 0, w, getHeight());

            // Mini bàn cờ minh họa 4x4 ở trê
            drawMiniBoard(g2, w);

            // Tiêu đề OTHELLO
            g2.setColor(new Color(255, 220, 50));
            g2.setFont(new Font("Arial", Font.BOLD, 52));
            FontMetrics fm = g2.getFontMetrics();
            String title = "OTHELLO";
            g2.drawString(title, (w - fm.stringWidth(title)) / 2, 290);

            // Phụ đề
            g2.setColor(new Color(180, 220, 130));
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            fm = g2.getFontMetrics();
            String sub = "Cờ lật đĩa kinh điển";
            g2.drawString(sub, (w - fm.stringWidth(sub)) / 2, 318);
        }

        // UC-1: vẽ mini board
        private void drawMiniBoard(Graphics2D g2, int panelWidth) {
            int cellSize = 38;
            int cols = 6, rows = 4;
            int boardW = cols * cellSize, boardH = rows * cellSize;
            int startX = (panelWidth - boardW) / 2, startY = 40;

            // Nền bàn cờ
            g2.setColor(new Color(53, 101, 21));
            g2.fillRoundRect(startX - 4, startY - 4, boardW + 8, boardH + 8, 12, 12);

            // Ô lưới
            g2.setColor(new Color(0, 70, 0));
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    g2.drawRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);

            // Vẽ quân cờ trang trí
            int[][] blacks = { { 1, 1 }, { 1, 4 }, { 2, 2 }, { 0, 3 }, { 3, 0 }, { 3, 5 } };
            int[][] whites = { { 0, 0 }, { 0, 5 }, { 1, 2 }, { 1, 3 }, { 2, 3 }, { 3, 2 }, { 3, 3 } };
            for (int[] pos : blacks)
                drawDisc(g2, startX + pos[1] * cellSize + cellSize / 2, startY + pos[0] * cellSize + cellSize / 2,
                        cellSize / 2 - 4, Color.BLACK, null);
            for (int[] pos : whites)
                drawDisc(g2, startX + pos[1] * cellSize + cellSize / 2, startY + pos[0] * cellSize + cellSize / 2,
                        cellSize / 2 - 4, Color.WHITE, Color.DARK_GRAY);
        }

        // UC-1: vẽ quân cờ
        private void drawDisc(Graphics2D g2, int cx, int cy, int r, Color fill, Color border) {
            g2.setColor(fill);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }
        }

        // UC-1: Tạo nút với hiệu ứng hover
        private JButton createStyledButton(String text, Color normalColor, Color hoverColor) {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = getModel().isRollover() ? hoverColor : normalColor;
                    if (getModel().isPressed())
                        bg = bg.darker();
                    g2.setColor(new Color(0, 0, 0, 60));
                    g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, 18, 18);
                    
                    // Shadow & Main Button
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 18, 18);
                    
                    g2.fillRoundRect(2, 4, getWidth(), getHeight(), 18, 18);

                    // Shadow
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                    // Text
                    g2.setColor(Color.WHITE);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(getText(), tx, ty);
                    g2.dispose();
                }
            };
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return btn;
        }
    }

    // UC-1.1: Khởi động ván game mới, tạo đối tượng OthelloGame và chuyển sang màn hình chơi
    // UC-02: Player chọn "Chơi với máy", nhập tên hiển thị và chọn màu
    private void startGame() {
        // 2.1.1 Player nhấn nút "Chơi với máy" từ màn hình chính → UC-02 được kích hoạt
        // (method này được gọi khi click btnPlay)
        String playerName;

        while (true) {

            // 2.1.2 Hệ thống hiển thị hộp thoại nhập tên hiển thị
            playerName = JOptionPane.showInputDialog(
                    this,
                    "Nhập tên hiển thị (1-20 ký tự):",
                    "UC-02 - Enter display name",
                    JOptionPane.PLAIN_MESSAGE);

            // 2.5.1 Player hủy bỏ toàn bộ tại bất kỳ bước nào (Cancel/X trên dialog nhập tên, playerName == null)
            if (playerName == null) {
                return;
            }

            // 2.1.3 Player nhập tên hiển thị của mình.
            // 2.1.4 Hệ thống nhận chuỗi nhập, gọi trim() để loại bỏ khoảng trắng đầu/cuối.
            playerName = playerName.trim();

            // 2.1.5 Hệ thống kiểm tra tính hợp lệ: tên không được rỗng và không quá 20 ký tự.
            // 2.2.1 Tên rỗng (isEmpty() == true sau khi trim()) -> Hệ thống hiển thị dialog lỗi
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Tên không được để trống. Vui lòng nhập lại!");
                continue;
            }

            // 2.3.1 Tên vượt quá 20 ký tự (length() > 20) -> Hệ thống hiển thị dialog lỗi
            if (playerName.length() > 20) {
                JOptionPane.showMessageDialog(
                        this,
                        "Tên tối đa 20 ký tự. Vui lòng nhập lại!");
                continue;
            }

            // Tên hợp lệ -> thoát vòng lặp kiểm tra
            break;
        }

        // Chọn màu quân cờ

        int playerColor;

        // loop để xử lý chọn màu
        while (true) {

            // 2.1.6 Tên hợp lệ → Hệ thống hiển thị hộp thoại chọn màu quân cờ
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Chọn màu quân cờ:",
                    "UC-02 - Choose color",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] { "Đen (BLACK)", "Trắng (WHITE)" },
                    "Đen (BLACK)");

            // 2.4.1 Player không chọn màu quân (đóng dialog màu bằng nút X / Cancel, choice == -1)
            if (choice == -1) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn chưa chọn màu. Dùng mặc định (Đen)?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Nếu [Có]: playerColor = BLACK
                    playerColor = Board.BLACK;
                    break;
                } else {
                    // Nếu [Không]: kết thúc use case (return).
                    return;
                }
            }

            // 2.1.7 Player chọn màu quân muốn chơi (Đen hoặc Trắng).
            playerColor = (choice == 1) ? Board.WHITE : Board.BLACK;
            break;
        }

        // 2.1.8 Hệ thống lưu tên và màu quân của Player vào GameSession.
        GameSession.setPlayerName(playerName);
        GameSession.setPlayerColor(playerColor);

        // Khởi tạo và hiển thị màn hình game
        dispose();
        SwingUtilities.invokeLater(() ->
                new OthelloGame(playerColor).setVisible(true)
        );
        // Tác giả: Phan Quang Huy – UC-02 Extension: Chọn độ khó AI
        int depth;
        while (true) {
            // 2.1.9 Hệ thống hiển thị hộp thoại chọn độ khó AI (Dễ, Trung bình, Khó). Player chọn độ khó.
            int diffChoice = JOptionPane.showOptionDialog(
                    this,
                    "Chọn độ khó của máy (AI):",
                    "UC-02 - Chọn độ khó",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[] { "Dễ", "Trung bình", "Khó" },
                    "Khó");

            // 2.5.1 Player không chọn độ khó (đóng dialog độ khó bằng nút X / Cancel, diffChoice == -1)
            if (diffChoice == -1) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn chưa chọn độ khó. Dùng mặc định (Khó)?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Nếu [Có]: depth = 10
                    depth = 10;
                    break;
                } else {
                    // Nếu [Không]: kết thúc use case (return).
                    return;
                }
            }

            if (diffChoice == 0) depth = 3;
            else if (diffChoice == 1) depth = 5;
            else depth = 10;
            break;
        }

        // 2.1.10 Hệ thống lưu độ khó vào GameSession.
        GameSession.setDifficulty(depth);

        // Tác giả: Phan Quang Huy – UC-02 Extension: Hộp thoại tóm tắt xác nhận thông tin ván đấu
        // 2.1.11 Hệ thống hiển thị hộp thoại tóm tắt thông tin và yêu cầu xác nhận
        String diffStr = depth == 3 ? "Dễ" : (depth == 5 ? "Trung bình" : "Khó");
        String colorStr = playerColor == Board.BLACK ? "Đen (Đi trước)" : "Trắng (Đi sau)";
        String summary = String.format("Xác nhận thông tin ván đấu:\n\n" +
                                       "  • Người chơi: %s\n" +
                                       "  • Màu quân: %s\n" +
                                       "  • Độ khó: %s\n\n" +
                                       "Bắt đầu ván đấu?",
                                       playerName, colorStr, diffStr);

        int confirmStart = JOptionPane.showConfirmDialog(
                this,
                summary,
                "Xác nhận thông tin",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        // 2.7.1 Luồng thay thế - Player từ chối xác nhận thông tin
        if (confirmStart != JOptionPane.YES_OPTION) {
            return;
        }

        // 2.1.12 Đóng MainMenu và chuyển sang UC-03 (Start Game Screen)
        dispose();

        // 2.1.13 Hệ thống tự động gán màu quân còn lại cho đối thủ máy (AI) (trong constructor OthelloGame).
        SwingUtilities.invokeLater(() -> new OthelloGame(playerColor).setVisible(true));
    }

    // UC-05 [5.1.0]: Mở màn hình Leaderboard từ Menu chính
    private void openLeaderboard() {
        // Lấy tên người chơi hiện hành từ GameSession để phục vụ cho việc Highlight dòng [5.1.5]
        String currentName = GameSession.getPlayerName();
        // [5.1.1] Khởi tạo và hiển thị hộp thoại giao diện LeaderboardDialog dạng Modal phía trên cửa sổ chính
        new LeaderboardDialog(this, currentName).setVisible(true);
    }

    // UC-03: Mở cách chơi
    private void openHowToPlay() {
        String url = "https://www.thegioididong.com/game-app/othello-la-gi-huong-dan-luat-chien-thuat-cach-choi-co-lat-don-1323614";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Không mở được trình duyệt.\nVui lòng truy cập:\n" + url,
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }
}
