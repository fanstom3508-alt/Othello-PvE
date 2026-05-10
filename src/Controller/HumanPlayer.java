package Controller;
import Model.Board;

public class HumanPlayer extends Player {
    public HumanPlayer(int color) {
        super(color);
    }

    @Override
    public void makeMove(Board board, MoveCallBack callBack) {
        // TODO (UC-04): Xử lý nước đi của người chơi qua mouse click
    }
}
