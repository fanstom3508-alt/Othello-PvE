package View;

import javax.swing.*;
import javax.swing.table.*;

import Model.HighScoreManager;
import Model.HighScoreManager.ScoreEntry;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

// UC-05: Xem và lọc bảng xếp hạng (View & Filter Leaderboard)
public class LeaderboardDialog extends JDialog {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String currentPlayerName; // Tên người chơi hiện tại để highlight

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    
    // [5.1.1] Hệ thống khởi tạo và hiển thị hộp thoại giao diện LeaderboardDialog dạng Modal
    public LeaderboardDialog(Window owner, String currentPlayerName) {
        super(owner, "Bảng xếp hạng & Thống kê", ModalityType.APPLICATION_MODAL);
        this.currentPlayerName = currentPlayerName;
        initUI();
    }

    private void initUI() {
        // Mở rộng kích thước cửa sổ để chứa các cột thống kê cải tiến mới
        setSize(850, 520);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        // ---- Panel tiêu đề ----
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 60, 15));
        headerPanel.setPreferredSize(new Dimension(850, 65));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("BẢNG XẾP HẠNG & THỐNG KÊ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 220, 50));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ---- PANEL TRUNG TÂM (CENTER): Gồm Thanh Bộ Lọc & Bảng Dữ Liệu ----
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        
        // ---- Khởi tạo thanh công cụ lọc (Filter ComboBox) ----
     // [5.1.6] Người chơi nhấp chọn thay đổi tiêu chí lọc trên thanh công cụ
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Sắp xếp theo: "));
        String[] filterOptions = {
            "Điểm số (SCORE)", 
            "Tổng trận thắng (TOTAL_WINS)", 
            "Tỉ lệ thắng (WIN_RATE)", 
            "Chuỗi thắng dài nhất (WIN_STREAK)"
        };
        filterComboBox = new JComboBox<>(filterOptions);
        
        
        filterComboBox.addActionListener(e -> {
            int selectedIndex = filterComboBox.getSelectedIndex();
            String criterion = "SCORE";
            if (selectedIndex == 1) criterion = "TOTAL_WINS";
            else if (selectedIndex == 2) criterion = "WIN_RATE";
            else if (selectedIndex == 3) criterion = "WIN_STREAK";
            
            // Gọi hàm cập nhật lại dữ liệu
            updateTableData(criterion);
        });
        filterPanel.add(filterComboBox);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // ---- Cấu hình Bảng hiển thị (JTable) ----
        // Cải tiến UC-05: Thêm các cột thống kê tương ứng với ScoreEntry
        String[] columnNames = { "Hạng", "Tên người chơi", "Điểm", "Trận", "Thắng", "Tỉ lệ (%)", "Chuỗi Max", "Ngày" };
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép nhấp đúp chỉnh sửa nội dung
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(53, 101, 21));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        // Căn giữa nội dung các cột (trừ cột Tên)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Căn chỉnh độ rộng từng cột cho cân đối
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Hạng
        table.getColumnModel().getColumn(1).setPreferredWidth(160); // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // Điểm
        table.getColumnModel().getColumn(3).setPreferredWidth(50);  // Trận
        table.getColumnModel().getColumn(4).setPreferredWidth(50);  // Thắng
        table.getColumnModel().getColumn(5).setPreferredWidth(70);  // Tỉ lệ
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Chuỗi Max
        table.getColumnModel().getColumn(7).setPreferredWidth(140); // Ngày

        // [5.1.5] Render JTable & Highlight dòng của người chơi hiện tại
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
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);

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
        
        // [5.1.10] Người chơi nhấn nút "Đóng"
        // [5.1.11] Hệ thống giải phóng tài nguyên cửa sổ con (dispose())
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // [5.1.2] Bộ điều khiển gọi phương thức tĩnh khởi tạo dữ liệu Mặc định (SCORE)
        updateTableData("SCORE");
    }

    // Hàm tiện ích để nạp dữ liệu vào bảng (được gọi khi mở form hoặc khi đổi Filter)
    private void updateTableData(String criterion) {
        // [5.1.7] + [5.1.8] Gọi hàm xử lý lấy danh sách đã sắp xếp từ HighScoreManager
        List<HighScoreManager.ScoreEntry> scores = HighScoreManager.getSortedLeaderboard(criterion);

        // [5.1.9] Hệ thống xóa dữ liệu cũ trên bảng đồ họa
        tableModel.setRowCount(0);

        if (scores.isEmpty()) {
            // [5.2.1.2] Hiển thị bảng trống kèm thông báo tinh tế
            tableModel.addRow(new Object[]{"-", "Chưa có dữ liệu xếp hạng", "-", "-", "-", "-", "-", "-"});
        } else {
            // [5.1.4] Lấy tối đa 10 bản ghi đầu tiên
            int limit = Math.min(scores.size(), 10);
            for (int i = 0; i < limit; i++) {
                HighScoreManager.ScoreEntry entry = scores.get(i);
                Object[] rowData = {
                    (i + 1),
                    entry.getPlayerName(),
                    entry.getScore(),
                    entry.getTotalMatches(),
                    entry.getWins(),
                    String.format("%.1f%%", entry.getWinRate()), // Định dạng 1 chữ số thập phân
                    entry.getMaxWinStreak(),
                    DATE_FORMAT.format(entry.getDate())
                };
                // [5.1.9] Nạp dữ liệu mới vào DefaultTableModel (giao diện sẽ tự động refresh)
                tableModel.addRow(rowData);
            }
        }
    }
}