// Player.java
public abstract class Player {
    protected int color;

    public Player(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
    public abstract void makeMove(Board board, MoveCallBack callBack);

}