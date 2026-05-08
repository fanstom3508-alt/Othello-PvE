import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Cờ Othello");
        setSize(520, 620);
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

            // TODO (UC-06): Uncomment khi implement xong Leaderboard
            // btnLeaderboard = createStyledButton("🏆  Bảng xếp hạng", new Color(33, 109, 185), new Color(24, 85, 150));
            // btnLeaderboard.setBounds(110, 440, 300, 65);
            // btnLeaderboard.addActionListener(e -> openLeaderboard());
            // add(btnLeaderboard);
            
            
            btnHowTo = createStyledButton("?  Cách chơi", new Color(33, 109, 185), new Color(24, 85, 150));
            btnHowTo.setBounds(110, 440, 300, 65);
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
    private void startGame() {
// TODO (UC-01): Gọi màn hình hoặc hộp thoại nhập tên trước
        
        // TODO (UC-02): Gọi màn hình chọn màu (ví dụ: dùng JOptionPane hoặc tạo JPanel mới)
        // Hiện tại gán mặc định là Board.BLACK để game chạy được, 
        // người làm UC-02 sau này chỉ cần thay biến chosenColor này bằng kết quả người dùng chọn.
        int chosenColor = Board.BLACK; 

        dispose();
        SwingUtilities.invokeLater(() -> new OthelloGame(chosenColor).setVisible(true));
    }

    // TODO (UC-06): Implement mở màn hình Leaderboard
    // private void openLeaderboard() { ... }

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
