package Testing;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import View.LeaderboardDialog;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class LeaderboardDialogTest {

    private LeaderboardDialog dialog;
    private JFrame fakeOwner;
    private final String CURRENT_PLAYER = "Player Test";

    @BeforeEach
    public void setUp() {
        // Khởi tạo một Frame rỗng làm chủ sở hữu (owner) cho Dialog
        fakeOwner = new JFrame();
        // Khởi tạo đối tượng cần test (Hệ thống tự động gọi initUI và nạp dữ liệu mặc định SCORE)
        dialog = new LeaderboardDialog(fakeOwner, CURRENT_PLAYER);
    }

    @AfterEach
    public void tearDown() {
        // Giải phóng tài nguyên giao diện sau mỗi ca kiểm thử
        if (dialog != null) {
            dialog.dispose();
        }
        if (fakeOwner != null) {
            fakeOwner.dispose();
        }
    }

    @Test
    public void testDialogProperties_Initialization() {
        // [Kiểm thử cấu hình giao diện 5.1.1]
        assertNotNull(dialog, "Hộp thoại LeaderboardDialog không được phép null");
        assertEquals("Bảng xếp hạng & Thống kê", dialog.getTitle(), "Tiêu đề Dialog không đúng cấu hình");
        
        // Đảm bảo Dialog được thiết lập ở chế độ APPLICATION_MODAL (chặn tương tác với cửa sổ cha)
        assertTrue(dialog.isModal(), "Hộp thoại bắt buộc phải là dạng Modal Dialog");
        
        // Kiểm tra kích thước khung hình đã được mở rộng để chứa các cột cải tiến mới
        assertEquals(850, dialog.getWidth(), "Chiều rộng cửa sổ phải là 850");
        assertEquals(520, dialog.getHeight(), "Chiều cao cửa sổ phải là 520");
        assertFalse(dialog.isResizable(), "Cửa sổ bảng xếp hạng không được phép cho co giãn kích thước");
    }

    @Test
    public void testTableNonEditableProperty() {
        // Lấy đối tượng JTable thông qua việc tìm kiếm cấu trúc Component
        JTable table = findComponent(dialog, JTable.class);
        assertNotNull(table, "Hệ thống phải khởi tạo cấu hình bảng JTable hiển thị");

        TableModel model = table.getModel();
        // Kiểm tra điều kiện ràng buộc: Người chơi không được nhấp đúp để chỉnh sửa dữ liệu trực tiếp trên bảng
        assertFalse(model.isCellEditable(0, 1), "Dữ liệu trên bảng xếp hạng bắt buộc không được phép chỉnh sửa");
        assertFalse(model.isCellEditable(2, 5), "Dữ liệu trên bảng xếp hạng bắt buộc không được phép chỉnh sửa");
    }

    @Test
    public void testFilterComboBoxOptions() {
        // Tìm kiếm thanh bộ lọc JComboBox trên UI
        JComboBox<?> comboBox = findComponent(dialog, JComboBox.class);
        assertNotNull(comboBox, "Thanh bộ lọc ComboBox không được null");
        
        // Xác thực số lượng tiêu chí lọc tương ứng [5.1.6]
        assertEquals(4, comboBox.getItemCount(), "Thanh bộ lọc phải có chính xác 4 tùy chọn");
        assertEquals("Điểm số (SCORE)", comboBox.getItemAt(0));
        assertEquals("Tổng trận thắng (TOTAL_WINS)", comboBox.getItemAt(1));
        assertEquals("Tỉ lệ thắng (WIN_RATE)", comboBox.getItemAt(2));
        assertEquals("Chuỗi thắng dài nhất (WIN_STREAK)", comboBox.getItemAt(3));
    }

    @Test
    public void testLeaderboardEmptyStateHandling() {
        JTable table = findComponent(dialog, JTable.class);
        assertNotNull(table);
        
        TableModel model = table.getModel();
        
        // Trường hợp chưa có dữ liệu hoặc file điểm trống [Xử lý ngoại lệ 5.2.1.2]
        if (model.getRowCount() == 1 && "-".equals(model.getValueAt(0, 0))) {
            assertEquals("Chưa có dữ liệu xếp hạng", model.getValueAt(0, 1), 
                    "Hệ thống phải hiển thị thông báo tinh tế tại cột Tên người chơi khi bảng trống");
        } else {
            // Nếu có dữ liệu, kiểm tra xem số dòng hiển thị có vượt quá giới hạn tối đa 10 bản ghi hay không [5.1.4]
            assertTrue(model.getRowCount() <= 10, "Bảng xếp hạng chỉ được hiển thị tối đa 10 dòng");
        }
    }

    /**
     * Hàm bổ trợ (Helper method) giúp tìm kiếm một Component cụ thể trong cấu trúc giao diện Container
     */
    @SuppressWarnings("unchecked")
    private <T extends Component> T findComponent(Container container, Class<T> clazz) {
        Component[] components = container.getComponents();
        for (Component comp : components) {
            if (clazz.isInstance(comp)) {
                return (T) comp;
            } else if (comp instanceof Container) {
                T result = findComponent((Container) comp, clazz);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}