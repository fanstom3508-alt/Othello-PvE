// UC-03 (1.1.9): Cơ chế MoveCallBack giúp AI (ComputerPlayer) trả về tọa độ tốt nhất 
// sau khi tính toán xong trên luồng riêng, tránh treo giao diện.
public interface MoveCallBack {
	
	// Tọa độ (row, col) sẽ được đẩy về lại cho OthelloGame 
    // để thực hiện đồng bộ lên giao diện ở Bước 1.1.10
	void onMove(int row, int col);

}
