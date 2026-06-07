package Testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.Board;
import Model.Node;

public class NodeTest {

    private Board mockBoard;
    private int[] testMove;
    private int testPlayerColor;
    private Node node;

    @BeforeEach
    public void setUp() {
        // 1. Khởi tạo một bàn cờ giả lập (mock board)
        mockBoard = new Board();
        
        // 2. Tạo một nước đi giả định (ví dụ: đánh vào dòng 3, cột 4)
        testMove = new int[]{3, 4};
        
        // 3. Giả định người chơi hiện tại là Cờ Đen (1)
        testPlayerColor = Board.BLACK;
        
        // 4. Khởi tạo đối tượng Node cần test
        node = new Node(mockBoard, testMove, testPlayerColor);
    }

    @Test
    public void testNodeInitializationAndGetters() {
        // Kiểm tra xem node có được tạo thành công không
        assertNotNull(node, "Đối tượng Node phải được khởi tạo thành công");

        // Kiểm tra phương thức getBoard()
        assertEquals(mockBoard, node.getBoard(), "Bàn cờ lấy ra phải khớp với bàn cờ đã truyền vào");

        // Kiểm tra phương thức getMove()
        int[] retrievedMove = node.getMove();
        assertNotNull(retrievedMove, "Nước đi (Move) không được null");
        assertEquals(testMove[0], retrievedMove[0], "Tọa độ dòng (row) phải là 3");
        assertEquals(testMove[1], retrievedMove[1], "Tọa độ cột (col) phải là 4");

        // Kiểm tra phương thức getPlayer() hoặc getColor() 
        // (Tùy theo cách bạn đặt tên hàm trong code thực tế, ở đây mình dùng getPlayer theo ý bạn)
        assertEquals(testPlayerColor, node.getPlayer(), "Màu cờ (Player) phải là Đen (1)");
    }
    
    @Test
    public void testNodeWithNullMove() {
        // Trong trường hợp bị mất lượt (Passed), nước đi có thể là null hoặc mảng rỗng
        // Test case này đảm bảo Node vẫn hoạt động đúng khi bị mất lượt
        Node passNode = new Node(mockBoard, null, Board.WHITE);
        
        assertNotNull(passNode);
        assertNull(passNode.getMove(), "Nước đi phải là null khi khởi tạo với null");
        assertEquals(Board.WHITE, passNode.getPlayer(), "Người chơi phải là Trắng (2)");
    }
}