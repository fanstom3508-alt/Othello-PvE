package View;

import javax.swing.*;
import javax.swing.table.*;

import Model.HighScoreManager;
import Model.HighScoreManager.ScoreEntry;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

// UC-05: Xem bảng xếp hạng (View Leaderboard) — hiển thị top 10
public class LeaderboardDialog extends JDialog {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String currentPlayerName; // Tên người chơi hiện tại để highlight

    public LeaderboardDialog(Window owner, String currentPlayerName) {
        super(owner, "Bảng xếp hạng", ModalityType.APPLICATION_MODAL);
        this.currentPlayerName = currentPlayerName;
        initUI();
    }

    private void initUI() {
        setSize(520, 500);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        // ---- Panel tiêu đề ----
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 60, 15));
        headerPanel.setPreferredSize(new Dimension(520, 70));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("TOP 10 DIEM CAO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 220, 50));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // UC-05 5.1.1: Đọc dữ liệu từ file lưu trữ cục bộ
        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getTopScores();

        if (scores.isEmpty()) {
            // UC-05 5.2.1: Chưa có dữ liệu → hiển thị thông báo
            JLabel emptyLabel = new JLabel("No high scores available", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 18));
            emptyLabel.setForeground(Color.GRAY);
            add(emptyLabel, BorderLayout.CENTER);
        } else {
            // UC-05 5.1.2: Sắp xếp giảm dần (xử lý trong HighScoreManager.getTopScores)
            // UC-05 5.1.3: Hiển thị top 10 dạng bảng
            String[] columnNames = { "Hạng", "Tên người chơi", "Điểm", "Ngày đạt được" };
            Object[][] data = new Object[scores.size()][4];

            for (int i = 0; i < scores.size(); i++) {
                HighScoreManager.ScoreEntry entry = scores.get(i);
                data[i][0] = (i + 1);
                data[i][1] = entry.getPlayerName();
                data[i][2] = entry.getScore();
                data[i][3] = DATE_FORMAT.format(entry.getDate());
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Không cho chỉnh sửa
                }
            };

            JTable table = new JTable(tableModel);
            table.setFont(new Font("Arial", Font.PLAIN, 15));
            table.setRowHeight(32);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            table.getTableHeader().setBackground(new Color(53, 101, 21));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setFillsViewportHeight(true);

            // Căn giữa các cột số
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

            // Đặt độ rộng cột
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setPreferredWidth(180);
            table.getColumnModel().getColumn(2).setPreferredWidth(70);
            table.getColumnModel().getColumn(3).setPreferredWidth(150);

            // UC-05 5.1.4: Highlight dòng của người chơi hiện tại
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);

                    // Highlight hàng có tên trùng với người chơi hiện tại
                    String name = (String) table.getValueAt(row, 1);
                    if (currentPlayerName != null && currentPlayerName.equals(name)) {
                        c.setBackground(new Color(255, 255, 180)); // Vàng nhạt
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (!isSelected) {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                    }

                    if (isSelected) {
                        c.setBackground(new Color(53, 101, 21));
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setForeground(Color.BLACK);
                    }

                    return c;
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(scrollPane, BorderLayout.CENTER);
        }

        // ---- Nút đóng ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        JButton closeBtn = new JButton("Đóng");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.setBackground(new Color(53, 101, 21));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}
