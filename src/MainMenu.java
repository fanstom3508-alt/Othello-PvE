import javax.swing.*;
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
        private JButton btnLeaderboard; // TODO (UC-06): Nút xem bảng xếp hạng
        private JButton btnHowTo;

        public MenuPanel() {
            setLayout(null);
            setBackground(new Color(30, 60, 15));

            // UC-03: Nút bắt đầu game - chơi với máy
            btnPlay = createStyledButton("Chơi với máy", new Color(46, 160, 67), new Color(36, 130, 52));
            btnPlay.setBounds(110, 350, 300, 65);
            btnPlay.addActionListener(e -> startGame());
            add(btnPlay);

            // TODO (UC-01): Trước khi startGame() cần mở màn hình nhập tên

            // UC-06: Nút xem bảng xếp hạng
            btnLeaderboard = createStyledButton("Bảng xếp hạng", new Color(33, 109, 185), new Color(24, 85, 150));
            btnLeaderboard.setBounds(110, 440, 300, 65);
            btnLeaderboard.addActionListener(e -> openLeaderboard());
            add(btnLeaderboard);
            
            
            btnHowTo = createStyledButton("?  Cách chơi", new Color(33, 109, 185), new Color(24, 85, 150));
            btnHowTo.setBounds(110, 520, 300, 65);
            btnHowTo.addActionListener(e -> openHowToPlay());
            add(btnHowTo);
        }

        // Vẽ nền, bàn cờ minh họa, tiêu đề
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

            //Mini bàn cờ minh họa 4x4 ở trê
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
            int[][] blacks = {{1,1},{1,4},{2,2},{0,3},{3,0},{3,5}};
            int[][] whites = {{0,0},{0,5},{1,2},{1,3},{2,3},{3,2},{3,3}};
            for (int[] pos : blacks)
                drawDisc(g2, startX + pos[1]*cellSize + cellSize/2, startY + pos[0]*cellSize + cellSize/2, cellSize/2-4, Color.BLACK, null);
            for (int[] pos : whites)
                drawDisc(g2, startX + pos[1]*cellSize + cellSize/2, startY + pos[0]*cellSize + cellSize/2, cellSize/2-4, Color.WHITE, Color.DARK_GRAY);
        }

        private void drawDisc(Graphics2D g2, int cx, int cy, int r, Color fill, Color border) {
            g2.setColor(fill);
            g2.fillOval(cx-r, cy-r, r*2, r*2);
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx-r, cy-r, r*2, r*2);
            }
        }
        
        // Tạo nút với hiệu ứng hover
        private JButton createStyledButton(String text, Color normalColor, Color hoverColor) {
            JButton btn = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = getModel().isRollover() ? hoverColor : normalColor;
                    if (getModel().isPressed()) bg = bg.darker();
                    g2.setColor(new Color(0, 0, 0, 60));
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

    // UC-03: Mở màn hình game
 // UC-01: Player chọn "Chơi với máy" và nhập tên hiển thị
    private void startGame() {
    	 // 1.1 Player chọn "Chơi với máy" từ MainMenu
        // (method này được gọi khi click btnPlay)
    	String playerName;

        while (true) {

            // 1.2 System hiển thị hộp thoại nhập tên
            playerName = JOptionPane.showInputDialog(
                    this,
                    "Nhập tên hiển thị (1-20 ký tự):",
                    "UC-01 - Enter display name",
                    JOptionPane.PLAIN_MESSAGE
            );

            // 1.A1: Người dùng bấm Cancel → quay lại menu
            if (playerName == null) {
                return;
            }

            // 1.3 Player nhập tên hiển thị
            playerName = playerName.trim();

            // 1.5 System kiểm tra tính hợp lệ của tên
            // 1.A1: Tên rỗng → yêu cầu nhập lại
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Tên không được để trống. Vui lòng nhập lại!"
                );
                continue;
            }

            // 1.A2: Tên > 20 ký tự → báo lỗi
            if (playerName.length() > 20) {
                JOptionPane.showMessageDialog(
                        this,
                        "Tên tối đa 20 ký tự. Vui lòng nhập lại!"
                );
                continue;
            }

            // 1.4 System nhận tên người chơi
            // 1.5 validation OK → thoát vòng lặp
            break;
        }

        // 1.6 System lưu tên vào GameSession
        GameSession.setPlayerName(playerName);

        // 1.7 System lưu thành công
        System.out.println("Saved player name: " + GameSession.getPlayerName());


       
     // UC-02: Player chọn màu quân cờ
     
        int playerColor;

        // loop để xử lý A2 (quay lại UC-01)
        while (true) {

            // 2.1 System hiển thị dialog chọn màu
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Chọn màu quân cờ:",
                    "UC-02 - Choose color",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Đen (BLACK)", "Trắng (WHITE)"},
                    "Đen (BLACK)"
            );

            // 2.A1: Player không chọn (Cancel)
            if (choice == -1) {

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn chưa chọn màu. Dùng mặc định (Đen)?",
                        "Xác nhận",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    // 2.5 System xử lý default
                    playerColor = Board.BLACK;
                    break;
                } else {
                    // 2.A2: quay lại UC-01
                    startGame();
                    return;
                }
            }

            // 2.2 + 2.3 Player chọn màu
            playerColor = (choice == 1) ? Board.WHITE : Board.BLACK;

            // 2.4 return value OK
            break;
        }

        // 2.7 System lưu màu vào GameSession
        GameSession.setPlayerColor(playerColor);

        // =========================
        // UC-03: Start Game Screen
        // =========================

        // 2.8 Khởi tạo game
        dispose();

        // 2.9 Hiển thị màn hình game
        SwingUtilities.invokeLater(() ->
                new OthelloGame(playerColor).setVisible(true)
        );
    }

    // UC-06: Mở màn hình Leaderboard
    private void openLeaderboard() {
        new LeaderboardDialog(this, null).setVisible(true);
    }

    // UC -03: Mở cách chơi
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
