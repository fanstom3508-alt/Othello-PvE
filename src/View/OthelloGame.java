package View;
import javax.swing.*;

import Controller.ComputerPlayer;
import Controller.GameSession;
import Controller.HumanPlayer;
import Controller.MoveCallBack;
import Controller.Player;
import Model.Board;
import Model.HighScoreManager;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class OthelloGame extends JFrame {
    private Board board;
    private Player humanPlayer;
    private Player computerPlayer;
    private int humanColor;
    
    private OthelloCell[][] cells;
    private JPanel boardPanel;
    private JLabel statusLabel, timeBlackLabel, timeWhiteLabel;
    private JLabel lastMoveLabel, flippedLabel;
    private Timer turnTimer;
    private long timeLeftBlack = 20L * 60 * 1000;
    private long timeLeftWhite = 20L * 60 * 1000;
    private int lastMoveRow = -1, lastMoveCol = -1, lastMoveFlipped = 0;
    private int lastMovePlayer = -1;
    // UC-05/UC-06: Tên người chơi dùng để lưu điểm và highlight leaderboard
    private String playerName = "Player";
    private boolean gameEnded = false;

    public OthelloGame(int chosenHumanColor) {
        board = new Board();
        setTitle("Cờ Othello - Người vs Máy");
        setSize(650, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // đóng game thì trở về menu
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // khởi tạo người và máy dựa trên màu truyền vô
        this.humanColor = chosenHumanColor;
        int computerColor = (humanColor == Board.BLACK) ? Board.WHITE : Board.BLACK;

        // UC-01/UC-05: Lấy tên người chơi từ GameSession (do UC-01 thiết lập)
        
        String name = GameSession.getPlayerName();
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }

        humanPlayer = new HumanPlayer(humanColor);
        computerPlayer = new ComputerPlayer(computerColor);

        createMenu();
        createStatusPanel();
        createBoardPanel();
        resetGame();  

        // Khi đóng cửa sổ game, hiện lại MainMenu
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
            }
        });
    }
    
    // UC-3.9, UC-3.26: Trả về tên hiển thị của người chơi theo màu quân (Bạn hoặc Máy)
    private String getPlayerName(int color) {
        if (color == humanColor) {
            return (color == Board.BLACK) ? "Đen (Bạn)" : "Trắng (Bạn)";
        } else {
            return (color == Board.BLACK) ? "Đen (Máy)" : "Trắng (Máy)";
        }
    }
    
    // UC-3.7 Tạo thanh menu bar với các tùy chọn Chơi mới, Kết thúc ván, Về menu, Thoát
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Trò chơi");
        JMenuItem newGame = new JMenuItem("Chơi mới");
        JMenuItem endGame = new JMenuItem("Kết thúc ván");
        JMenuItem backMenu = new JMenuItem("Về menu chính");
        JMenuItem exit = new JMenuItem("Thoát");

        newGame.addActionListener(e -> resetGame());
        endGame.addActionListener(e -> endGame());
        backMenu.addActionListener(e -> {
            // TODO (UC-09): Thêm xác nhận trước khi thoát giữa ván
            turnTimer.stop();
            dispose();
        });
        exit.addActionListener(e -> {
            // TODO (UC-09): Thêm hộp thoại "Are you sure?"
            System.exit(0);
        });

        gameMenu.add(newGame);
        gameMenu.add(endGame);
        gameMenu.addSeparator();
        gameMenu.add(backMenu);
        gameMenu.add(exit);
        menuBar.add(gameMenu);

        setJMenuBar(menuBar);
    }
    
    // UC-3.13 Reset toàn bộ trạng thái game về ban đầu, khởi động lại đồng hồ
    private void resetGame() {
        board.reset();
        gameEnded = false;
        timeLeftBlack = 20L * 60 * 1000;
        timeLeftWhite = 20L * 60 * 1000;
        timeBlackLabel.setText("Đen (Bạn): 20:00");
        timeWhiteLabel.setText("Trắng (Máy): 20:00");
        if (turnTimer != null && turnTimer.isRunning()) turnTimer.stop();
        turnTimer.start();
        lastMoveRow = -1; lastMoveCol = -1; lastMoveFlipped = 0; lastMovePlayer = -1;
        updateLastMoveLabels();
        updateBoard();
        
        // Nếu Máy đi trước (Máy là Đen), kích hoạt lượt của Máy
        if (board.getCurrentPlayer() != humanColor) {
            TurnBegin();
        }
    }

    // UC-3.10: Tạo lưới bàn cờ 8x8 gồm 64 ô OthelloCell
    private void createBoardPanel() {
        boardPanel = new JPanel(new GridLayout(8, 8, 2, 2));
        cells = new OthelloCell[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                OthelloCell cell = new OthelloCell();
                final int row = i, col = j;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    // UC-04
                    public void mouseClicked(MouseEvent e) {
                        handleMove(row, col);
                    }
                });
                cells[i][j] = cell;
                boardPanel.add(cell);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    // UC-3.8: Tạo panel hiển thị thông tin trận đấu gồm lượt đi, đồng hồ đếm ngược, nước vừa đi, số quân ăn được
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 0, 6));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Dòng 1: lượt + điểm
        statusLabel = new JLabel("Lượt đi: " + getPlayerName(Board.BLACK), SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Dòng 2: đồng hồ
        timeBlackLabel = new JLabel(getPlayerName(Board.BLACK) + ": 20:00");
        timeWhiteLabel = new JLabel(getPlayerName(Board.WHITE) + ": 20:00");
        timeBlackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timeWhiteLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        timePanel.add(timeBlackLabel);
        timePanel.add(new JLabel("|"));
        timePanel.add(timeWhiteLabel);

        // Dòng 3: vị trí nước đi vừa rồi
        lastMoveLabel = new JLabel("Nước vừa đi: —", SwingConstants.CENTER);
        lastMoveLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Dòng 4: số quân bị lật
        flippedLabel = new JLabel("Số quân ăn được: —", SwingConstants.CENTER);
        flippedLabel.setFont(new Font("Arial", Font.BOLD, 16));

        statusPanel.add(statusLabel);
        statusPanel.add(timePanel);
        statusPanel.add(lastMoveLabel);
        statusPanel.add(flippedLabel);
        add(statusPanel, BorderLayout.SOUTH);

        turnTimer = new Timer(1000, e -> updateTimer());
    }

    // UC-3.24:  Đếm ngược đồng hồ mỗi giây cho người chơi hiện tại
    private void updateTimer() {
        int current = board.getCurrentPlayer();
        if (current == Board.BLACK) {
            timeLeftBlack = Math.max(0, timeLeftBlack - 1000);
            timeBlackLabel.setText(getPlayerName(Board.BLACK) + ": " + formatTime(timeLeftBlack));
            // UC-05: Hết giờ → kết thúc game, đối thủ thắng
            if (timeLeftBlack <= 0) { endGameByTime(Board.WHITE); return; }
        } else {
            timeLeftWhite = Math.max(0, timeLeftWhite - 1000);
            timeWhiteLabel.setText(getPlayerName(Board.WHITE) + ": " + formatTime(timeLeftWhite));
            // UC-05: Hết giờ → kết thúc game, đối thủ thắng
            if (timeLeftWhite <= 0) { endGameByTime(Board.BLACK); return; }
        }
    }

    // UC-3.27: Chuyển đổi thời gian từ millisecond sang định dạng mm:ss để hiển thị
    private String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // UC-3.16: Vẽ lại toàn bộ bàn cờ, cập nhật trạng thái từng ô và highlight ô hợp lệ
    private void updateBoard() {
    	// UC-05 bổ sung thêm
    	
    	// Chỉ kiểm tra kết thúc nếu game đã thực sự bắt đầu (có nhiều hơn 4 quân ban đầu)
    	int[] currentScore = board.getScore();
    	int totalPieces = currentScore[0] + currentScore[1];
        if (totalPieces > 4 && board.isGameOver()) {
        	endGame();
        	return;
        }
        int current = board.getCurrentPlayer();
        int[] score = board.getScore();
        String turnLabel = getPlayerName(current);
        statusLabel.setText(String.format(
                "Lượt: %s    |    %s: %d    %s: %d",
                turnLabel, getPlayerName(Board.BLACK), score[0], getPlayerName(Board.WHITE), score[1]));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                if (val != Board.EMPTY) {
                    cells[i][j].setState(val);
                } else {
                    cells[i][j].setState(board.isValidMove(i, j, current)
                        ? OthelloCell.VALID_MOVE : OthelloCell.EMPTY);
                }
            }
        }
    }

    // UC-04: Thực hiện nước đi  (placeholder — do người làm UC-04 implement)

    private void handleMove(int row, int col) {
        if (gameEnded) return;
        int current = board.getCurrentPlayer();
        if (current == humanColor) {
            if (board.isValidMove(row, col, current)) {
                int flippedCount = board.getFlippableCount(row, col, current);
                board.makeMove(row, col, current);
                
                // Cập nhật thông tin nước đi cuối
                lastMoveRow = row; lastMoveCol = col;
                lastMoveFlipped = flippedCount; lastMovePlayer = current;
                updateLastMoveLabels();
                
                board.switchPlayer();
                TurnBegin();
            }
        }
    }

    private void TurnBegin() {
        if (board.isGameOver()) {
            updateBoard(); // Cập nhật lần cuối để hiện kết quả
            return;
        }

        updateBoard();

        int curPlayer = board.getCurrentPlayer();
        Player player = (curPlayer == humanColor) ? humanPlayer : computerPlayer;

        // Kiểm tra nếu người chơi hiện tại không còn nước đi
        if (board.getValidMoves(curPlayer).isEmpty()) {
            String who = (curPlayer == humanColor) ? "Bạn" : "Máy";
            JOptionPane.showMessageDialog(this, who + " không còn nước đi, chuyển lượt!");
            board.switchPlayer();
            TurnBegin();
            return;
        }

        if (player instanceof ComputerPlayer) {
            MoveCallBack callback = new MoveCallBack() {
                @Override
                public void onMove(int row, int col) {
                    if (row == -1 && col == -1) { // Máy pass (dù đã check ở trên nhưng dự phòng)
                        board.switchPlayer();
                        TurnBegin();
                        return;
                    }
                    
                    int flippedCount = board.getFlippableCount(row, col, curPlayer);
                    board.makeMove(row, col, curPlayer);
                    
                    // Cập nhật thông tin nước đi cuối
                    lastMoveRow = row; lastMoveCol = col;
                    lastMoveFlipped = flippedCount; lastMovePlayer = curPlayer;
                    
                    // Dùng SwingUtilities để update UI từ thread AI
                    SwingUtilities.invokeLater(() -> {
                        updateLastMoveLabels();
                        board.switchPlayer();
                        TurnBegin();
                    });
                }
            };
            
            // Thêm delay 1 giây để người chơi kịp quan sát trước khi Máy đi
            Timer aiDelay = new Timer(1000, e -> player.makeMove(board, callback));
            aiDelay.setRepeats(false);
            aiDelay.start();
        }
    }

    // UC-3.15: Cập nhật nhãn hiển thị vị trí nước vừa đi và số quân ăn được
    private void updateLastMoveLabels() {
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        lastMoveLabel.setFont(labelFont);
        flippedLabel.setFont(labelFont);
        if (lastMoveRow < 0) {
            lastMoveLabel.setText("Nước vừa đi: —");
            flippedLabel.setText("Số quân ăn được: —");
            return;
        }
        char colChar = (char)('A' + lastMoveCol);
        int rowNum = lastMoveRow + 1;
        String who = getPlayerName(lastMovePlayer);
        lastMoveLabel.setText(String.format("Nước vừa đi: %s đặt tại %c%d", who, colChar, rowNum));
        flippedLabel.setText(String.format("Số quân ăn được: %d", lastMoveFlipped));
        
    }

    // UC-05: Kết thúc trò chơi
    private void endGame() {
        if (gameEnded) return; // Tránh gọi nhiều lần
        gameEnded = true;

        // Dừng đồng hồ
        if (turnTimer != null && turnTimer.isRunning()) {
            turnTimer.stop();
        }

        // UC-05 [1]: Đếm số quân mỗi bên
        int[] score = board.getScore();
        int blackScore = score[0];
        int whiteScore = score[1];

        // UC-05 [2]: Xác định thắng/thua/hòa
        String resultText;
        String resultTitle;
        // Người chơi là đen hay trắng tuỳ vào humanColor
        int playerScore = (humanColor == Board.BLACK) ? blackScore : whiteScore;

        if (blackScore > whiteScore) {
            if (humanColor == Board.BLACK) {
                resultText = "Chúc mừng! Bạn đã THẮNG!";
                resultTitle = "Chiến thắng!";
            } else {
                resultText = "Bạn đã THUA!";
                resultTitle = "Thất bại!";
            }
        } else if (whiteScore > blackScore) {
            if (humanColor == Board.WHITE) {
                resultText = "Chúc mừng! Bạn đã THẮNG!";
                resultTitle = "Chiến thắng!";
            } else {
                resultText = "Bạn đã THUA!";
                resultTitle = "Thất bại!";
            }
        } else {
            resultText = "Trận đấu HÒA!";
            resultTitle = "Hòa!";
        }

        // UC-05 [3]: Cập nhật status label
        statusLabel.setText(String.format(
            "KẾT THÚC  |  Đen: %d    Trắng: %d", blackScore, whiteScore));

        // Xóa highlight nước đi hợp lệ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                cells[i][j].setState(val != Board.EMPTY ? val : OthelloCell.EMPTY);
            }
        }

        // UC-05 [4]: Lưu điểm của Player
        boolean isTopScore = HighScoreManager.addScore(playerName, playerScore);

        // UC-05 [5]: Hiển thị dialog kết quả với tùy chọn
        showEndGameDialog(resultTitle, resultText, blackScore, whiteScore, playerScore, isTopScore);
    }

    // UC-05 [A1]: Xử lý kết thúc do hết giờ
    private void endGameByTime(int winner) {
        if (gameEnded) return;
        gameEnded = true;

        if (turnTimer != null && turnTimer.isRunning()) {
            turnTimer.stop();
        }

        int[] score = board.getScore();
        int blackScore = score[0];
        int whiteScore = score[1];

        String resultText;
        String resultTitle;
        int playerScore;

        if (humanColor == Board.BLACK) {
            playerScore = blackScore;
            if (winner == Board.BLACK) {
                resultText = "Đối thủ hết giờ! Bạn THẮNG!";
                resultTitle = "Chiến thắng!";
            } else {
                resultText = "Bạn đã hết giờ! Bạn THUA!";
                resultTitle = "Hết giờ!";
            }
        } else {
            playerScore = whiteScore;
            if (winner == Board.WHITE) {
                resultText = "Đối thủ hết giờ! Bạn THẮNG!";
                resultTitle = "Chiến thắng!";
            } else {
                resultText = "Bạn đã hết giờ! Bạn THUA!";
                resultTitle = "Hết giờ!";
            }
        }

        statusLabel.setText(String.format(
            "HẾT GIỜ  |  Đen: %d    Trắng: %d", blackScore, whiteScore));

        // Xóa highlight
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                cells[i][j].setState(val != Board.EMPTY ? val : OthelloCell.EMPTY);
            }
        }

        boolean isTopScore = HighScoreManager.addScore(playerName, playerScore);
        showEndGameDialog(resultTitle, resultText, blackScore, whiteScore, playerScore, isTopScore);
    }

    // UC-05: Dialog hiển thị kết quả với các tùy chọn
    private void showEndGameDialog(String title, String resultText,
            int blackScore, int whiteScore, int playerScore, boolean isTopScore) {

        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        // Panel kết quả
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        resultPanel.setBackground(new Color(30, 60, 15));

        // Dòng kết quả chính
        JLabel resultLabel = new JLabel(resultText, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 22));
        resultLabel.setForeground(new Color(255, 220, 50));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(resultLabel);
        resultPanel.add(Box.createVerticalStrut(15));

        // Điểm số chi tiết
        JLabel scoreDetail = new JLabel(
            String.format("Đen: %d  —  Trắng: %d", blackScore, whiteScore),
            SwingConstants.CENTER);
        scoreDetail.setFont(new Font("Arial", Font.BOLD, 18));
        scoreDetail.setForeground(Color.WHITE);
        scoreDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(scoreDetail);
        resultPanel.add(Box.createVerticalStrut(8));

        // Điểm của người chơi
        JLabel playerScoreLabel = new JLabel(
            String.format("Điểm của bạn (%s): %d", playerName, playerScore),
            SwingConstants.CENTER);
        playerScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        playerScoreLabel.setForeground(new Color(180, 220, 130));
        playerScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(playerScoreLabel);

        // Thông báo nếu lọt top
        if (isTopScore) {
            resultPanel.add(Box.createVerticalStrut(8));
            JLabel topLabel = new JLabel("(*) Bạn đã lọt vào bảng xếp hạng!", SwingConstants.CENTER);
            topLabel.setFont(new Font("Arial", Font.BOLD, 15));
            topLabel.setForeground(new Color(255, 200, 50));
            topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(topLabel);
        }

        dialog.add(resultPanel, BorderLayout.CENTER);

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(30, 60, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // UC-08: Nút Chơi lại
        JButton restartBtn = createDialogButton("Chơi lại", new Color(46, 160, 67));
        restartBtn.addActionListener(e -> {
            dialog.dispose();
            gameEnded = false;
            resetGame();
        });
        buttonPanel.add(restartBtn);

        // UC-06: Nút Xem bảng xếp hạng
        JButton leaderboardBtn = createDialogButton("Xếp hạng", new Color(33, 109, 185));
        leaderboardBtn.addActionListener(e -> {
            new LeaderboardDialog(dialog, playerName).setVisible(true);
        });
        buttonPanel.add(leaderboardBtn);

        // Nút Về menu
        JButton menuBtn = createDialogButton("Menu", new Color(120, 60, 20));
        menuBtn.addActionListener(e -> {
            dialog.dispose();
            dispose(); // Đóng game, windowClosed sẽ mở lại MainMenu
        });
        buttonPanel.add(menuBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // UC-05: Tạo nút cho dialog kết quả
    private JButton createDialogButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return btn;
    }

    // UC-05/UC-06: Setter cho tên người chơi (được gọi từ MainMenu/UC-01)
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    // UC-06: Mở bảng xếp hạng từ game
    public void openLeaderboard() {
        new LeaderboardDialog(this, playerName).setVisible(true);
    }

    // UC-09: Thoát game  (placeholder — do người làm UC-09 implement)


}