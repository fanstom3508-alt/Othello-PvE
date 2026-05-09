
public class GameSession {

	    private static String playerName;
	    private static int playerColor = Board.BLACK;

	    public static String getPlayerName() {
	        return playerName;
	    }

	    public static void setPlayerName(String playerName) {
	        GameSession.playerName = playerName;
	    }

	    public static int getPlayerColor() {
	        return playerColor;
	    }

	    public static void setPlayerColor(int playerColor) {
	        GameSession.playerColor = playerColor;
	    }
	}

