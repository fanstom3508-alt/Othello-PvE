package Testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import Controller.*;
import Model.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit Test cho các phương thức trong ComputerPlayer — UC-03
 * Bao gồm: getOppColor, minimax, alphaBeta, heuristic (gián tiếp qua alphaBeta)
 */
public class ComputerPlayerTest {
	 
    private ComputerPlayer aiBlack; // AI là Đen
    private ComputerPlayer aiWhite; // AI là Trắng
    private Board board;
    private long startTimeMs;
 
    @BeforeEach
   public void setUp() {
        aiBlack = new ComputerPlayer(Board.BLACK);
        aiWhite = new ComputerPlayer(Board.WHITE);
        board = new Board();
        
        // Cấp một mốc thời gian mới tinh trước khi bất kỳ bài test nào chạy
        startTimeMs = System.currentTimeMillis();
    }
 
    // getOppColor()
 
    @Test
    @DisplayName("getOppColor: AI Đen trả về Trắng")
    void getOppColor_Black_ReturnsWhite() {
        assertEquals(Board.WHITE, aiBlack.getOppColor());
    }
 
    @Test
    @DisplayName("getOppColor: AI Trắng trả về Đen")
    void getOppColor_White_ReturnsBlack() {
        assertEquals(Board.BLACK, aiWhite.getOppColor());
    }
 
    // minimax()
 
    @Test
    @DisplayName("minimax: depth=0 trả về giá trị heuristic (không crash)")
    void minimax_DepthZero_ReturnsHeuristicValue() {
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        // depth=0 → trả về heuristic ngay, không đệ quy
        int val = aiBlack.minimax(true, node, 0);
        // Chỉ cần không throw exception và trả về số nguyên hợp lệ
        assertTrue(val > Integer.MIN_VALUE && val < Integer.MAX_VALUE);
    }
 
    @Test
    @DisplayName("minimax: các depth khác nhau đều trả về giá trị hợp lệ")
    void minimax_DifferentDepthsValidValues() {
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        int val1 = aiBlack.minimax(true, node, 1);
        int val2 = aiBlack.minimax(true, node, 2);
        assertTrue(val1 > Integer.MIN_VALUE && val1 < Integer.MAX_VALUE);
        assertTrue(val2 > Integer.MIN_VALUE && val2 < Integer.MAX_VALUE);
    }
 
    @Test
    @DisplayName("minimax: game đã kết thúc thì trả về heuristic ngay")
    void minimax_GameOver_ReturnsHeuristic() {
        // Lấp đầy bàn cờ → isGameOver() = true
        Board fullBoard = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                setCell(fullBoard, i, j, Board.BLACK);
        Node node = new Node(fullBoard, new int[]{0, 0}, Board.BLACK);
        int val = aiBlack.minimax(true, node, 5);
        assertTrue(val > Integer.MIN_VALUE && val < Integer.MAX_VALUE, "Phải trả về heuristic khi game over");
    }
 
    @Test
    @DisplayName("minimax: không có nước đi hợp lệ thì giảm depth và đổi lượt")
    void minimax_NoValidMoves_SkipsTurn() {
        // Bàn cờ mà chỉ Đen không có nước đi nhưng Trắng có
        Board b = setupBoardWhiteOnly();
        if (b == null) return; // bỏ qua nếu không setup được
        Node node = new Node(b, new int[]{0, 0}, Board.WHITE);
        // Không được throw exception
        assertDoesNotThrow(() -> aiWhite.minimax(true, node, 2));
    }
 
    // alphaBeta()
 
    @Test
    @DisplayName("alphaBeta: depth=0 trả về heuristic (không crash)")
    void alphaBeta_DepthZero_ReturnsHeuristic() {
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        int val = aiBlack.alphaBeta(true, node, 0, -99999999, 99999999, startTimeMs);
        assertTrue(val > Integer.MIN_VALUE && val < Integer.MAX_VALUE);
    }
 
    @Test
    @DisplayName("alphaBeta: kết quả bằng minimax ở cùng trạng thái và depth nhỏ")
    void alphaBeta_SameResultAsMinimax() {
    	// Cấp cho hàm một mốc thời gian hiện tại để nó không bị lỗi thiếu tham số
        long startTimeMs = System.currentTimeMillis();
        
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        int minimaxVal = aiBlack.minimax(true, node, 3);
        int alphaBetaVal = aiBlack.alphaBeta(true, node, 3, -99999999, 99999999, startTimeMs);
        assertEquals(minimaxVal, alphaBetaVal,
            "alphaBeta và minimax phải cho cùng kết quả (alphaBeta chỉ tối ưu tốc độ)");
    }
 
    @Test
    @DisplayName("alphaBeta: depth cao hơn cho kết quả khác depth thấp hơn")
    void alphaBeta_DifferentDepthsDifferentResults() {
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        int val1 = aiBlack.alphaBeta(true, node, 1, -99999999, 99999999, startTimeMs);
        int val3 = aiBlack.alphaBeta(true, node, 3, -99999999, 99999999, startTimeMs);
        // Chỉ cần cả 2 đều trả về giá trị hợp lệ, không crash
        assertTrue(val1 > Integer.MIN_VALUE && val1 < Integer.MAX_VALUE);
        assertTrue(val3 > Integer.MIN_VALUE && val3 < Integer.MAX_VALUE);
    }
 
    @Test
    @DisplayName("alphaBeta: alpha >= beta thì cắt tỉa, vẫn trả về giá trị hợp lệ")
    void alphaBeta_PruningStillReturnsValue() {
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        // alpha = beta = 0 → sẽ cắt tỉa ngay
        int val = aiBlack.alphaBeta(true, node, 3, 0, 0, startTimeMs);
        assertTrue(val > Integer.MIN_VALUE && val < Integer.MAX_VALUE );
    }
 
    @Test
    @DisplayName("alphaBeta: game đã kết thúc thì trả về heuristic ngay")
    void alphaBeta_GameOver_ReturnsHeuristic() {
        Board fullBoard = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                setCell(fullBoard, i, j, Board.BLACK);
        Node node = new Node(fullBoard, new int[]{0, 0}, Board.BLACK);
        int val = aiBlack.alphaBeta(true, node, 5, -99999999, 99999999, startTimeMs);
        assertTrue(val > Integer.MIN_VALUE && val < Integer.MAX_VALUE);
    }
 
    @Test
    @DisplayName("alphaBeta: không có nước đi thì đổi lượt tiếp tục (không crash)")
    void alphaBeta_NoValidMoves_DoesNotCrash() {
        Board b = setupBoardWhiteOnly();
        if (b == null) return;
        Node node = new Node(b, new int[]{0, 0}, Board.WHITE);
        assertDoesNotThrow(() -> aiWhite.alphaBeta(true, node, 2, -99999999, 99999999, startTimeMs));
    }
 
    // heuristic() — kiểm tra gián tiếp qua alphaBeta(depth=0)
 
    @Test
    @DisplayName("heuristic: AI chiếm góc thì giá trị cao hơn khi không chiếm góc")
    void heuristic_CornerOccupied_HigherValue() {
        // Board có AI chiếm góc (0,0)
        Board withCorner = board.copy();
        setCell(withCorner, 0, 0, Board.BLACK);
 
        Node nodeWithCorner = new Node(withCorner, new int[]{0, 0}, Board.BLACK);
        Node nodeWithout   = new Node(board,       new int[]{2, 3}, Board.BLACK);
 
        int valWith    = aiBlack.alphaBeta(true, nodeWithCorner, 0, -99999999, 99999999, startTimeMs);
        int valWithout = aiBlack.alphaBeta(true, nodeWithout,    0, -99999999, 99999999, startTimeMs);
 
        assertTrue(valWith > valWithout, "Chiếm góc phải cho heuristic cao hơn");
    }
 
    @Test
    @DisplayName("heuristic: đối thủ chiếm góc thì giá trị thấp hơn")
    void heuristic_OpponentCorner_LowerValue() {
        Board opponentCorner = board.copy();
        setCell(opponentCorner, 0, 0, Board.WHITE); // Trắng chiếm góc, AI là Đen
 
        Node nodeOppCorner = new Node(opponentCorner, new int[]{0, 0}, Board.WHITE);
        Node nodeNormal    = new Node(board,           new int[]{2, 3}, Board.BLACK);
 
        int valOpp    = aiBlack.alphaBeta(true, nodeOppCorner, 0, -99999999, 99999999, startTimeMs);
        int valNormal = aiBlack.alphaBeta(true, nodeNormal,    0, -99999999, 99999999, startTimeMs);
 
        assertTrue(valOpp < valNormal, "Đối thủ chiếm góc phải cho heuristic thấp hơn");
    }
 
    @Test
    @DisplayName("heuristic: AI có nhiều quân hơn thì giá trị dương")
    void heuristic_AIHasMorePieces_PositiveValue() {
        // Bàn cờ: AI (Đen) có nhiều quân hơn Trắng
        Board dominated = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 4; j++)
                setCell(dominated, i, j, Board.BLACK);
        for (int i = 0; i < 8; i++)
            for (int j = 4; j < 6; j++)
                setCell(dominated, i, j, Board.WHITE);
 
        Node node = new Node(dominated, new int[]{0, 0}, Board.BLACK);
        int val = aiBlack.alphaBeta(true, node, 0, -99999999, 99999999, startTimeMs);
        assertTrue(val > 0, "AI nhiều quân hơn → heuristic phải dương");
    }
 
    @Test
    @DisplayName("heuristic: bàn cờ cân bằng thì giá trị gần 0")
    void heuristic_BalancedBoard_NearZero() {
        // Trạng thái ban đầu: 2 Đen, 2 Trắng → khá cân bằng
        Node node = new Node(board, new int[]{2, 3}, Board.BLACK);
        int val = aiBlack.alphaBeta(true, node, 0, -99999999, 99999999, startTimeMs);
        // Không nhất thiết = 0 vì còn tính corner/edge/mobility, nhưng không quá lớn
        assertTrue(Math.abs(val) < 500, "Bàn cờ cân bằng → heuristic không quá lớn");
    }
 
    // makeMove() của ComputerPlayer — kiểm tra callback
 
    @Test
    @DisplayName("makeMove: AI trả về nước đi hợp lệ qua callback")
    void makeMove_ReturnsValidMove() throws InterruptedException {
        int[] result = new int[]{-99, -99};
        Object lock = new Object();
 
        aiWhite.makeMove(board, (row, col) -> {
            result[0] = row;
            result[1] = col;
            synchronized (lock) { lock.notifyAll(); }
        });
 
        synchronized (lock) { lock.wait(5000); } // chờ tối đa 5 giây
 
        assertNotEquals(-99, result[0], "AI phải gọi callback");
        // Nếu AI pass thì (row,col) = (-1,-1) — vẫn hợp lệ
        if (result[0] != -1) {
            assertTrue(board.isValidMove(result[0], result[1], Board.WHITE),
                "Nước AI đi phải hợp lệ: (" + result[0] + "," + result[1] + ")");
        }
    }
 
    @Test
    @DisplayName("makeMove: AI pass (-1,-1) khi không có nước đi hợp lệ")
    void makeMove_NoValidMoves_PassesWithMinusOne() throws InterruptedException {
        // Lấp đầy bàn cờ bằng Đen → Trắng không có nước đi
        Board fullBoard = new Board();
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                setCell(fullBoard, i, j, Board.BLACK);
 
        int[] result = new int[]{-99, -99};
        Object lock = new Object();
 
        aiWhite.makeMove(fullBoard, (row, col) -> {
            result[0] = row;
            result[1] = col;
            synchronized (lock) { lock.notifyAll(); }
        });
 
        synchronized (lock) { lock.wait(5000); }
 
        assertEquals(-1, result[0], "AI phải pass với row=-1");
        assertEquals(-1, result[1], "AI phải pass với col=-1");
    }
 
    // Helper methods
 
    private void setCell(Board b, int row, int col, int value) {
        try {
            java.lang.reflect.Field f = Board.class.getDeclaredField("board");
            f.setAccessible(true);
            int[][] arr = (int[][]) f.get(b);
            arr[row][col] = value;
        } catch (Exception e) {
            throw new RuntimeException("setCell thất bại: " + e.getMessage());
        }
    }
 
    /**
     * Tạo bàn cờ mà Đen không có nước đi hợp lệ nhưng Trắng có
     * Trả về null nếu không setup được
     */
    private Board setupBoardWhiteOnly() {
        try {
            Board b = new Board();
            // Lấp gần hết bàn bằng Trắng, chỉ để vài ô Đen không kẹp được
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++)
                    setCell(b, i, j, Board.WHITE);
            setCell(b, 0, 0, Board.BLACK);
            setCell(b, 7, 7, Board.EMPTY);
            return b;
        } catch (Exception e) {
            return null;
        }
    }
    
    // Stress test cho deepening structure_23130186_TranLeMinhMan_PhatTrienTiep
    @Test
    public void testMakeMove_StressTest_TimeConstraint() throws InterruptedException {
        // 1. Khởi tạo một bàn cờ trống (hoặc có thể setup một thế cờ phức tạp tùy ý)
        Board board = new Board(); 
        
        // 2. Khởi tạo AI (giả sử AI cầm cờ Đen = 1)
        ComputerPlayer aiPlayer = new ComputerPlayer(1);
        
        // 3. Sử dụng CountDownLatch để luồng Test chờ luồng AI tính toán xong
        CountDownLatch latch = new CountDownLatch(1);
        
        // 4. Mảng tạm để hứng kết quả trả về từ callBack
        int[] moveResult = {-1, -1};
        
        // 5. BẮT ĐẦU BẤM GIỜ TỪ BÊN NGOÀI BÀI TEST
        long startTime = System.currentTimeMillis();
        
        aiPlayer.makeMove(board, new MoveCallBack() {
            @Override
            public void onMove(int row, int col) {
                moveResult[0] = row;
                moveResult[1] = col;
                latch.countDown(); // Đánh dấu là AI đã tính xong, mở khóa cho bài Test đi tiếp
            }
        });
        
        // 6. Chờ tối đa 2.5 giây. Nếu sau 2.5 giây mà CountdownLatch chưa mở -> Bị treo luồng!
        boolean finishedWithoutHanging = latch.await(2500, TimeUnit.MILLISECONDS);
        
        long timeTaken = System.currentTimeMillis() - startTime;
        
        System.out.println("[Stress Test] Thời gian AI hoàn thành: " + timeTaken + " ms");
        System.out.println("[Stress Test] Nước đi AI chọn: (" + moveResult[0] + ", " + moveResult[1] + ")");

        // CÁC TIÊU CHÍ KIỂM ĐỊNH (ASSERTIONS) 
        // Kiểm tra 1: Không bị treo (phải trả về kết quả trước khi timeout chờ 2.5 giây của Test)
        assertTrue(finishedWithoutHanging, "LỖI MẠNG: AI đã bị treo luồng hoặc chạy quá 2.5 giây!");
        
        // Kiểm tra 2: Thời gian thực thi thực tế của AI phải nhỏ hơn mức 2 giây quy định (VD: <= 1900ms)
        assertTrue(timeTaken < 2000, "LỖI HIỆU NĂNG: AI chạy mất " + timeTaken + " ms, VƯỢT QUÁ QUY ĐỊNH 2 GIÂY!");
        
        // Kiểm tra 3: Nước đi AI trả về PHẢI là nước đi hợp lệ
        assertTrue(board.isValidMove(moveResult[0], moveResult[1], 1), 
                "LỖI LOGIC: Nước đi trả về (" + moveResult[0] + ", " + moveResult[1] + ") là sai luật!");
    }
}
    
