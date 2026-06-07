package Controller;
import Model.Board;

public class GameSession {

	    private static String playerName;
	    private static int playerColor = Board.BLACK;

	    public static String getPlayerName() {
	        return playerName;
	    }

	    // 2.1.8 Hệ thống lưu tên của Player vào GameSession
	    public static void setPlayerName(String playerName) {
	        GameSession.playerName = playerName;
	    }

	    public static int getPlayerColor() {
	        return playerColor;
	    }

	    // 2.1.8 Hệ thống lưu màu quân của Player vào GameSession
	    public static void setPlayerColor(int playerColor) {
	        GameSession.playerColor = playerColor;
	    }

	    private static int difficulty = 10; // Default: Khó (depth = 10)

	    public static int getDifficulty() {
	        return difficulty;
	    }

	    // 2.1.10 Hệ thống lưu độ khó vào GameSession.
	    public static void setDifficulty(int diff) {
	        GameSession.difficulty = diff;
	    }
	}

