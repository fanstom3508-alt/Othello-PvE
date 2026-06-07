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
 // Tên người chơi dùng để highlight leaderboard
    private String playerName = "Player";
    private boolean gameEnded = false;

    public OthelloGame(int chosenHumanColor) {
        board = new Board();
        setTitle("Cờ Othello - Người vs Máy");
        setSize(650, 750);
        // [7.1.0] Đóng bằng nút X cũng kích hoạt dispose → windowClosed → [7.1.6] MainMenu
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // khởi tạo người và máy dựa trên màu truyền vô
        this.humanColor = chosenHumanColor;
        int computerColor = (humanColor == Board.BLACK) ? Board.WHITE : Board.BLACK;

        // Lấy tên người chơi từ GameSession
        String name = GameSession.getPlayerName();
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }

        humanPlayer = new HumanPlayer(humanColor);
        computerPlayer = new ComputerPlayer(computerColor);

        createMenu();
        createStatusPanel();
        createBoardPanel();
        restartGame();

        // [7.1.6] Khi đóng cửa sổ game kết thúc, bắt sự kiện WindowClosed hiện lại MainMenu
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
            }
        });
    }


    // UC-1.9, UC-1.26, UC-1.19: Trả về tên hiển thị của người chơi theo màu quân (Bạn hoặc Máy)
    // UC-3.11: Lấy tên hiển thị của người chơi dựa trên ID (màu cờ)
    private String getPlayerName(int color) {
        if (color == humanColor) {
            return (color == Board.BLACK) ? "Đen (Bạn)" : "Trắng (Bạn)";
        } else {
            return (color == Board.BLACK) ? "Đen (Máy)" : "Trắng (Máy)";
        }
    }
    // UC-1.7 Tạo thanh menu bar với các tùy chọn Chơi mới, Kết thúc ván, Về menu, Thoát
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Trò chơi");
        JMenuItem newGame = new JMenuItem("Chơi mới");
        JMenuItem endGame = new JMenuItem("Kết thúc ván");
        JMenuItem backMenu = new JMenuItem("Về menu chính");
        JMenuItem exit = new JMenuItem("Thoát");

        newGame.addActionListener(e -> restartGame());
        endGame.addActionListener(e -> endGame());
        backMenu.addActionListener(e -> {
        	// [7.1.0] Thoát trận đấu hiện tại qua menu
            // [7.1.2] Hệ thống kiểm tra trạng thái ván đấu (gameEnded)
            if (!gameEnded) {
                // [7.1.3] Ván đấu đang diễn ra. Xác nhận thoát.
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn thoát ván đấu hiện tại?\nKết quả sẽ không được lưu.",
                    "Xác nhận thoát trận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                // Nếu người chơi hủy, tiếp tục
                if (confirm != JOptionPane.YES_OPTION) return;
            }
            // [7.2.1] Game đã kết thúc (gameEnded == true) → bỏ qua confirm, thoát thẳng
            
            // [7.1.1] Dừng đồng hồ đếm ngược
            if (turnTimer != null && turnTimer.isRunning()) {
                turnTimer.stop();
            }
            // [7.1.4] Quy tắc BRule-07: KHÔNG gọi HighScoreManager.addScore() tại đây nếu trận đang dở.
            
            // [7.1.5] Đóng cửa sổ game, giải phóng bộ nhớ.
            dispose();
        });
        exit.addActionListener(e -> {
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

    // UC-1.13 restart toàn bộ trạng thái game về ban đầu, khởi động lại đồng hồ
    private void restartGame() {
        board.restart();
        gameEnded = false;
        timeLeftBlack = 20L * 60 * 1000;
        timeLeftWhite = 20L * 60 * 1000;
        timeBlackLabel.setText("Đen (Bạn): 20:00");
        timeWhiteLabel.setText("Trắng (Máy): 20:00");
        if (turnTimer != null && turnTimer.isRunning())
            turnTimer.stop();
        turnTimer.start();
        lastMoveRow = -1;
        lastMoveCol = -1;
        lastMoveFlipped = 0;
        lastMovePlayer = -1;
        updateLastMoveLabels();
        updateBoard();

        // Nếu Máy đi trước (Máy là Đen), kích hoạt lượt của Máy
        if (board.getCurrentPlayer() != humanColor) {
            TurnBegin();
        }
    }

    // UC-1.10: Tạo lưới bàn cờ 8x8 gồm 64 ô OthelloCell
    private void createBoardPanel() {
        boardPanel = new JPanel(new GridLayout(8, 8, 2, 2));
        cells = new OthelloCell[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                OthelloCell cell = new OthelloCell();
                final int row = i, col = j;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    // UC-3.1: Bắt sự kiện click chuột của người chơi trên bàn cờ (boardPanel)
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

    // UC-1.8: Tạo panel hiển thị thông tin trận đấu gồm lượt đi, đồng hồ đếm ngược, nước vừa đi, số quân ăn được
    // nước vừa đi, số quân ăn được
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

    // UC-1.24:  Đếm ngược đồng hồ mỗi giây cho người chơi hiện tại
    private void updateTimer() {
        int current = board.getCurrentPlayer();
        if (current == Board.BLACK) {
            timeLeftBlack = Math.max(0, timeLeftBlack - 1000);
            timeBlackLabel.setText(getPlayerName(Board.BLACK) + ": " + formatTime(timeLeftBlack));
            // UC-04: Hết giờ → kết thúc game, đối thủ thắng
            if (timeLeftBlack <= 0) {
                endGameByTime(Board.WHITE);
                return;
            }
        } else {
            timeLeftWhite = Math.max(0, timeLeftWhite - 1000);
            timeWhiteLabel.setText(getPlayerName(Board.WHITE) + ": " + formatTime(timeLeftWhite));
            // UC-04: Hết giờ → kết thúc game, đối thủ thắng
            if (timeLeftWhite <= 0) {
                endGameByTime(Board.BLACK);
                return;
            }
        }
    }

    // UC-1.27: Chuyển đổi thời gian từ millisecond sang định dạng mm:ss để hiển thị
    private String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // UC-1.16: Vẽ lại toàn bộ bàn cờ, cập nhật trạng thái từng ô và highlight ô hợp lệ
    // UC-3.15: Cập nhật lại toàn bộ giao diện (lật cờ) và điểm số trên màn hình
    private void updateBoard() {
        // UC-04 bổ sung thêm

        // Chỉ kiểm tra kết thúc nếu game đã thực sự bắt đầu (có nhiều hơn 4 quân ban
        // đầu)
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
                            ? OthelloCell.VALID_MOVE
                            : OthelloCell.EMPTY);
                }
            }
        }
    }

    // UC-3.2: Hàm chính xử lý logic nước đi sau khi người chơi click

    private void handleMove(int row, int col) {
        if (gameEnded)
            return;
        int current = board.getCurrentPlayer();
        if (current == humanColor) {
            if (board.isValidMove(row, col, current)) {
                int flippedCount = board.getFlippableCount(row, col, current);
                board.makeMove(row, col, current);

                // Cập nhật thông tin nước đi cuối
                lastMoveRow = row;
                lastMoveCol = col;
                lastMoveFlipped = flippedCount;
                lastMovePlayer = current;
                updateLastMoveLabels();

                board.switchPlayer();
                TurnBegin();
            }
        }
    }

    // UC-3.13, UC-3.36 (Trước khi phát triển): Hàm bản lề điều phối vòng lặp, kiểm tra trạng thái bàn cờ và kích hoạt lượt AI
    // 23130186_TranLeMinhMan_CapNhatThem UC-3.37 (Sau khi phát triển)
    	/* Điểm khác biệt & Cải tiến cốt lõi:
        * 1. Tối ưu NFR-01 (Zero Artificial Delay): Loại bỏ Timer tạo trễ 1 giây cũ, nhường trọn vẹn quỹ thời gian cho AI suy nghĩ thực tế.
        * 2. Non-blocking UI: Kích hoạt AI đánh cờ qua cơ chế Callback ngầm, giúp giao diện Swing không bị đóng băng khi tới lượt Máy.
        * 3. An toàn luồng (Thread-Safety): Sử dụng `SwingUtilities.invokeLater()` để hứng kết quả từ Thread ngầm, đảm bảo an toàn tuyệt đối khi vẽ lại giao diện (EDT).
        * 4. Tự động hóa trạng thái: Tự động kiểm tra Game Over, hoặc tự động ép qua lượt (pass lượt) nếu một bên không còn nước đi hợp lệ.
        */
    private void TurnBegin() {
        if (board.isGameOver()) {
            updateBoard(); // Cập nhật lần cuối để hiện kết quả
            return;
        }

        updateBoard();
//	code người cũ
//        int curPlayer = board.getCurrentPlayer();
//        List<int[]> validMoves = board.getValidMoves(curPlayer);
//
//        // Kiểm tra nếu người chơi hiện tại không còn nước đi
//        if (validMoves.isEmpty()) {
//            String who = (curPlayer == humanColor) ? "Bạn" : "Máy";
//            JOptionPane.showMessageDialog(this, who + " không còn nước đi, chuyển lượt!");
//            board.switchPlayer();
//            TurnBegin();
//            return;
//        }
//
//        // Kiểm tra lượt AI
//        if (curPlayer == computerPlayer.getColor()) { 
//            computerPlayer.makeMove(board, new MoveCallBack() {
//                @Override
//             // UC-3.30: Callback nhận tọa độ tối ưu từ luồng AI trả về để tiến hành thực thi lên giao diện
//                public void onMove(int row, int col) {
//                    // Lệnh này đang nằm trên luồng ngầm (AI Thread).
//                    // Bắt buộc đẩy về luồng UI (Event Dispatch Thread) để không bị crash giao diện.
//                	// UC-3.33: Bọc tác vụ thay đổi UI để trả quyền điều khiển về luồng UI chính
//                    SwingUtilities.invokeLater(() -> {
//                        if (row != -1 && col != -1) {
//                            // Lưu trạng thái nước đi cuối cùng
//                            lastMoveRow = row;
//                            lastMoveCol = col;
//                            lastMovePlayer = curPlayer;
//                            lastMoveFlipped = board.getFlippableCount(row, col, curPlayer);         
//                            
//                            // Thực hiện đặt cờ lên bàn cờ thật
//                            board.makeMove(row, col, curPlayer);
//                        }
//                        // Cập nhật nhãn lịch sử đi cờ
//                        updateLastMoveLabels();
//                        
//                        // Đổi phiên và bắt đầu lại vòng lặp cho Người
//                        board.switchPlayer();
//                        TurnBegin(); 
//                    });
//                }
//            });
//        }
        // 23130186_TranLeMinhMan_CapNhatThem
        int curPlayer = board.getCurrentPlayer();
        List<int[]> validMoves = board.getValidMoves(curPlayer);

        // Xử lý mất lượt
        if (validMoves.isEmpty()) {
            // (Tùy chọn: Hiện thông báo Toast/Dialog mất lượt tại đây)
            board.switchPlayer();
            TurnBegin(); // Gọi đệ quy nhường lượt
            return;
        }
        // Kiểm tra lượt AI

        if (curPlayer == computerPlayer.getColor()) { 
            computerPlayer.makeMove(board, new MoveCallBack() {
                @Override
             // UC-3.30: Callback nhận tọa độ tối ưu từ luồng AI trả về để tiến hành thực thi lên giao diện
                public void onMove(int row, int col) {
                    // Lệnh này đang nằm trên luồng ngầm (AI Thread).
                    // Bắt buộc đẩy về luồng UI (Event Dispatch Thread) để không bị crash giao diện.
                	// UC-3.32 (Sau khi phát triển): Bọc tác vụ thay đổi UI để trả quyền điều khiển về luồng UI chính
                    SwingUtilities.invokeLater(() -> {
                        if (row != -1 && col != -1) {
                            // Lưu trạng thái nước đi cuối cùng
                            lastMoveRow = row;
                            lastMoveCol = col;
                            lastMovePlayer = curPlayer;
                            lastMoveFlipped = board.getFlippableCount(row, col, curPlayer);         
                          
                            // Thực hiện đặt cờ lên bàn cờ thật
                            board.makeMove(row, col, curPlayer);
                        }

                        // Cập nhật nhãn lịch sử đi cờ
                        updateLastMoveLabels();
                    
                        // Đổi phiên và bắt đầu lại vòng lặp cho Người
                        board.switchPlayer();
                        TurnBegin(); 
                    });
                }
            });
        }
    }

    // UC-1.15: Cập nhật nhãn hiển thị vị trí nước vừa đi và số quân ăn được
    // UC-3.10, UC-3.34 (Trước khi phát triển) : Cập nhật giao diện hiển thị thông tin nước đi cuối cùng
    // UC-3.35 (Trước khi phát triển) : Cập nhật giao diện hiển thị thông tin nước đi cuối cùng
    private void updateLastMoveLabels() {
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        lastMoveLabel.setFont(labelFont);
        flippedLabel.setFont(labelFont);
        if (lastMoveRow < 0) {
            lastMoveLabel.setText("Nước vừa đi: —");
            flippedLabel.setText("Số quân ăn được: —");
            return;
        }
        char colChar = (char) ('A' + lastMoveCol);
        int rowNum = lastMoveRow + 1;
        String who = getPlayerName(lastMovePlayer);
        lastMoveLabel.setText(String.format("Nước vừa đi: %s đặt tại %c%d", who, colChar, rowNum));
        flippedLabel.setText(String.format("Số quân ăn được: %d", lastMoveFlipped));

    }

    // UC-04: Xem kết quả trận đấu (View Game Result)
    private void endGame() {
        if (gameEnded)
            return; // Tránh gọi nhiều lần
        gameEnded = true;

        // Dừng đồng hồ
        if (turnTimer != null && turnTimer.isRunning()) {
            turnTimer.stop();
        }

        // UC-04 4.1.1: Đếm số quân mỗi bên
        int[] score = board.getScore();
        int blackScore = score[0];
        int whiteScore = score[1];

        // UC-04 4.1.2: Xác định thắng/thua/hòa
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

        // UC-04 4.1.3: Cập nhật status label
        statusLabel.setText(String.format(
                "KẾT THÚC  |  Đen: %d    Trắng: %d", blackScore, whiteScore));

        // Xóa highlight nước đi hợp lệ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                cells[i][j].setState(val != Board.EMPTY ? val : OthelloCell.EMPTY);
            }
        }

        // UC-04 4.1.4: Lưu điểm của Player
        // UC-07 cai tien: luu them ket qua thang/thua/hoa de cap nhat thong ke ca nhan.
        boolean playerWon = (humanColor == Board.BLACK && blackScore > whiteScore)
                || (humanColor == Board.WHITE && whiteScore > blackScore);
        boolean playerLost = (humanColor == Board.BLACK && blackScore < whiteScore)
                || (humanColor == Board.WHITE && whiteScore < blackScore);

        // UC-05 cai tien: van tra ve trang thai lot top 10 de dialog ket qua hien thong bao nhu cu.
        boolean isTopScore = HighScoreManager.addScore(playerName, playerScore, playerWon, playerLost);

        // UC-04 4.1.5: Hiển thị dialog kết quả với tùy chọn
        showEndGameDialog(resultTitle, resultText, blackScore, whiteScore, playerScore, isTopScore);
    }

    // UC-04 4.2.1: Xử lý kết thúc do hết giờ
    private void endGameByTime(int winner) {
        if (gameEnded)
            return;
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

     // [7.2.1.1] Lưu điểm của Player và kết quả để đồng bộ thống kê
        boolean playerWon = (humanColor == Board.BLACK && blackScore > whiteScore)
                || (humanColor == Board.WHITE && whiteScore > blackScore);
        boolean playerLost = (humanColor == Board.BLACK && blackScore < whiteScore)
                || (humanColor == Board.WHITE && whiteScore < blackScore);

        boolean isTopScore = HighScoreManager.addScore(playerName, playerScore, playerWon, playerLost);

        showEndGameDialog(resultTitle, resultText, blackScore, whiteScore, playerScore, isTopScore);
    }

    // UC-04: Dialog hiển thị kết quả trận đấu với các tùy chọn
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

        // Nút Chơi lại
        JButton restartBtn = createDialogButton("Chơi lại", new Color(46, 160, 67));
        restartBtn.addActionListener(e -> {
            dialog.dispose();
            gameEnded = false;
            restartGame();
        });
        buttonPanel.add(restartBtn);

     // [5.1.0] Mở bảng xếp hạng từ kết quả
        JButton leaderboardBtn = createDialogButton("Xếp hạng", new Color(33, 109, 185));
        leaderboardBtn.addActionListener(e -> {
            new LeaderboardDialog(dialog, playerName).setVisible(true);
        });
        buttonPanel.add(leaderboardBtn);

        JButton menuBtn = createDialogButton("Menu", new Color(120, 60, 20));
        menuBtn.addActionListener(e -> {
            dialog.dispose();
            dispose(); 
        });
        buttonPanel.add(menuBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // UC-04: Tạo nút cho dialog kết quả trận đấu
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

    // UC-05: Setter cho tên người chơi (được gọi từ MainMenu/UC-01)
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    // UC-05 5.1.0: Mở bảng xếp hạng từ game
    public void openLeaderboard() {
        new LeaderboardDialog(this, playerName).setVisible(true);
    }

    // UC-08: Thoát ứng dụng (Exit application)

}
