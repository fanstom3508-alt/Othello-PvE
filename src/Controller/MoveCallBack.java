package Controller;

//UC-3.30 (Trước khi phát triển): Hàm callback nhận kết quả tọa độ (bestRow, bestCol) trả về từ luồng AI
//UC-3.31 (Sau khi phát triển): Hàm callback nhận kết quả tọa độ (bestRow, bestCol) trả về từ luồng AI
public interface MoveCallBack {
	void onMove(int row, int col);

}
