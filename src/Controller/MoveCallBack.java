package Controller;

//UC-3.30: Hàm callback nhận kết quả tọa độ (bestRow, bestCol) trả về từ luồng AI
public interface MoveCallBack {
	void onMove(int row, int col);

}
