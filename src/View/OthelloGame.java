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

/**
 * Màn hình chính điều khiển trận đấu Cờ Othello (Người vs Máy).
 * Tích hợp chặt chẽ UC-03 (Chơi game), UC-04 (Xem kết quả ván đấu), và UC-07 (Thoát ván an toàn).
 */
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
        
        // [7.1.0] ĐỒNG BỘ FIX LỖI: Chặn JFrame tự đóng ngang xương để ép luồng qua WindowListener kiểm tra
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Khởi tạo người và máy dựa trên màu truyền vào từ cấu hình màn hình trước
        this.humanColor = chosenHumanColor;

        // Lấy tên người chơi an toàn từ GameSession toàn cục của hệ thống
        String name = GameSession.getPlayerName();
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }

        int computerColor = (humanColor == Board.BLACK) ? Board.WHITE : Board.BLACK;
        humanPlayer = new HumanPlayer(humanColor);
        computerPlayer = new ComputerPlayer(computerColor);

        createMenu();
        createStatusPanel();
        createBoardPanel();
        restartGame();

        // [7.1.0] Bắt trọn vẹn hành vi người dùng thao tác trực tiếp với cửa sổ hệ thống
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // [7.1.2] Khi bấm nút X dở dang -> Chuyển hướng tới hàm xử lý thông báo YES/NO của UC-07
                forceEndGame();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // [7.1.6] Sau khi cửa sổ đồ họa bị hủy hoàn toàn, giải phóng luồng đếm giờ và kích hoạt lại MainMenu
                if (turnTimer != null && turnTimer.isRunning()) {
                    turnTimer.stop();
                }
                SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
            }
        });
    }

    private String getPlayerName(int color) {
        if (color == humanColor) {
            return (color == Board.BLACK) ? "Đen (Bạn)" : "Trắng (Bạn)";
        } else {
            return (color == Board.BLACK) ? "Đen (Máy)" : "Trắng (Máy)";
        }
    }

    // UC-1.7: Tạo thanh menu bar với các tùy chọn điều khiển trạng thái trận đấu
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Trò chơi");
        JMenuItem newGame = new JMenuItem("Chơi mới");
        JMenuItem endGame = new JMenuItem("Kết thúc ván");
        JMenuItem backMenu = new JMenuItem("Về menu chính");
        JMenuItem exit = new JMenuItem("Thoát");

        newGame.addActionListener(e -> restartGame());
        
        // Sự kiện kết thúc ván chủ động từ thanh công cụ
        endGame.addActionListener(e -> forceEndGame());
        
        // Sự kiện yêu cầu chuyển hướng điều hướng về màn hình chính
        backMenu.addActionListener(e -> {
            // [7.1.2] Hệ thống kiểm tra trạng thái ván đấu hiện tại
            if (!gameEnded) {
                // [7.1.3] Hiển thị thông báo xác nhận YES/NO nghiêm ngặt về dữ liệu
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn thoát ván đấu hiện tại về Menu chính?\nKết quả trận đấu dở dang này sẽ KHÔNG ĐƯỢC LƯU.",
                    "Xác nhận thoát trận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                // Nếu chọn NO -> Hủy lệnh, quay lại trạng thái game đang chơi
                if (confirm != JOptionPane.YES_OPTION) return;
            }
            
            // [7.1.1] Dừng đồng hồ đếm ngược ngay lập tức
            if (turnTimer != null && turnTimer.isRunning()) {
                turnTimer.stop();
            }
            // [7.1.4] Tuân thủ BRule-07: KHÔNG gọi ghi dữ liệu HighScoreManager khi trận đấu bị hủy ngang
            // [7.1.5] Giải phóng tài nguyên giao diện của JFrame hiện tại
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

    // UC-07: Xử lý quy trình Kết Thúc Ván chủ động từ phía người dùng (Menu Item & Button X)
    private void forceEndGame() {
        // [7.2.1] Nếu ván đấu đã kết thúc sẵn (do hết cờ, hết giờ) -> Cho phép đóng màn hình thẳng
        if (gameEnded) {
            dispose();
            return;
        }
        
        // [7.1.2] + [7.1.3] Xác định ván đấu đang diễn ra dở dang -> Đẩy ra hộp thoại thông báo YES/NO cảnh báo điểm số
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Bạn có chắc muốn kết thúc ván đấu hiện tại?\nKết quả trận đấu dở dang này sẽ KHÔNG ĐƯỢC LƯU vào bảng xếp hạng.", 
            "Xác nhận kết thúc ván đấu", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        // Nếu người dùng đồng ý (YES) -> Tiến hành hủy bỏ trận đấu
        if (confirm == JOptionPane.YES_OPTION) {
            // [7.1.1] Hủy luồng chạy đếm thời gian
            if (turnTimer != null && turnTimer.isRunning()) {
                turnTimer.stop();
            }
            // [7.1.4] Áp dụng quy tắc nghiệp vụ BRule-07: Bỏ qua và hủy bỏ hoàn toàn các bước ghi file
            // [7.1.5] Gọi lệnh giải phóng Frame
            dispose();
        }
    }

    // UC-1.13: Reset toàn bộ dữ liệu, khởi động lại đồng hồ và bắt đầu ván mới từ đầu
    private void restartGame() {
        board.restart();
        gameEnded = false;
        timeLeftBlack = 20L * 60 * 1000;
        timeLeftWhite = 20L * 60 * 1000;
        timeBlackLabel.setText(getPlayerName(Board.BLACK) + ": 20:00");
        timeWhiteLabel.setText(getPlayerName(Board.WHITE) + ": 20:00");
        if (turnTimer != null && turnTimer.isRunning())
            turnTimer.stop();
        turnTimer.start();
        lastMoveRow = -1;
        lastMoveCol = -1;
        lastMoveFlipped = 0;
        lastMovePlayer = -1;
        updateLastMoveLabels();
        updateBoard();

        // Nếu Máy đi trước (Máy chọn quân màu Đen), kích hoạt luồng xử lý AI
        if (board.getCurrentPlayer() != humanColor) {
            TurnBegin();
        }
    }

    // UC-1.10: Tạo lưới hiển thị bàn cờ kích thước tiêu chuẩn 8x8 gồm 64 ô vuông OthelloCell
    private void createBoardPanel() {
        boardPanel = new JPanel(new GridLayout(8, 8, 2, 2));
        cells = new OthelloCell[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                OthelloCell cell = new OthelloCell();
                final int row = i, col = j;
                cell.addMouseListener(new MouseAdapter() {
                    @Override
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

    // UC-1.8: Thiết lập cấu trúc Panel hiển thị đầy đủ thông tin trạng thái ván đấu dồi dào trực quan
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 0, 6));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Dòng 1: lượt + điểm số real-time
        statusLabel = new JLabel("Lượt đi: " + getPlayerName(Board.BLACK), SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Dòng 2: Đồng hồ đếm ngược từng bên
        timeBlackLabel = new JLabel(getPlayerName(Board.BLACK) + ": 20:00");
        timeWhiteLabel = new JLabel(getPlayerName(Board.WHITE) + ": 20:00");
        timeBlackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timeWhiteLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        timePanel.add(timeBlackLabel);
        timePanel.add(new JLabel("|"));
        timePanel.add(timeWhiteLabel);

        // Dòng 3: Tọa độ nước đi gần nhất vừa thực hiện trên bàn cờ
        lastMoveLabel = new JLabel("Nước vừa đi: —", SwingConstants.CENTER);
        lastMoveLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Dòng 4: Số lượng quân cờ lật ngược (ăn được) trong lượt đi đó
        flippedLabel = new JLabel("Số quân ăn được: —", SwingConstants.CENTER);
        flippedLabel.setFont(new Font("Arial", Font.BOLD, 16));

        statusPanel.add(statusLabel);
        statusPanel.add(timePanel);
        statusPanel.add(lastMoveLabel);
        statusPanel.add(flippedLabel);
        add(statusPanel, BorderLayout.SOUTH);

        turnTimer = new Timer(1000, e -> updateTimer());
    }

    // UC-1.24: Đếm ngược thời gian thi đấu sau mỗi giây của người chơi hiện tại
    private void updateTimer() {
        if (gameEnded) return;
        int current = board.getCurrentPlayer();
        if (current == Board.BLACK) {
            timeLeftBlack = Math.max(0, timeLeftBlack - 1000);
            timeBlackLabel.setText(getPlayerName(Board.BLACK) + ": " + formatTime(timeLeftBlack));
            // Xử lý UC-04: Hết giờ thi đấu -> Kết thúc game ngay lập tức và xử đối thủ thắng
            if (timeLeftBlack <= 0) {
                endGameByTime(Board.WHITE);
            }
        } else {
            timeLeftWhite = Math.max(0, timeLeftWhite - 1000);
            timeWhiteLabel.setText(getPlayerName(Board.WHITE) + ": " + formatTime(timeLeftWhite));
            // Xử lý UC-04: Hết giờ thi đấu -> Kết thúc game ngay lập tức và xử đối thủ thắng
            if (timeLeftWhite <= 0) {
                endGameByTime(Board.BLACK);
            }
        }
    }

    // UC-1.27: Chuyển đổi thời gian từ millisecond sang chuỗi văn bản định dạng chuẩn mm:ss
    private String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // UC-1.16: Đồng bộ hóa vẽ lại giao diện lưới bàn cờ và highlight các nước đi hợp lệ tiếp theo
    private void updateBoard() {
        int[] currentScore = board.getScore();
        int totalPieces = currentScore[0] + currentScore[1];
        
        // Điều kiện dừng: Khi bàn cờ đã đầy hoặc không còn nước đi hợp lệ cho cả 2 bên
        if (totalPieces > 4 && board.isGameOver()) {
            endGame();
            return;
        }
        
        int current = board.getCurrentPlayer();
        String turnLabel = getPlayerName(current);
        statusLabel.setText(String.format(
                "Lượt: %s    |    Đen: %d   Trắng: %d",
                turnLabel, currentScore[0], currentScore[1]));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                if (val != Board.EMPTY) {
                    cells[i][j].setState(val);
                } else {
                    cells[i][j].setState(board.isValidMove(i, j, current) && !gameEnded
                            ? OthelloCell.VALID_MOVE
                            : OthelloCell.EMPTY);
                }
            }
        }
    }

    // UC-03: Xử lý sự kiện đặt quân cờ từ phía người chơi
    private void handleMove(int row, int col) {
        if (gameEnded) return;
        int current = board.getCurrentPlayer();
        if (current == humanColor) {
            if (board.isValidMove(row, col, current)) {
                int flippedCount = board.getFlippableCount(row, col, current);
                board.makeMove(row, col, current);

                // Lưu lại vết lịch sử nước đi vừa thực hiện xong
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

    // Luồng nghiệp vụ điều phối chuyển giao lượt đi và kích hoạt AI cho Máy
    private void TurnBegin() {
        if (board.isGameOver()) {
            updateBoard();
            return;
        }

        updateBoard();
        if (gameEnded) return;

        int curPlayer = board.getCurrentPlayer();
        Player player = (curPlayer == humanColor) ? humanPlayer : computerPlayer;

        // Xử lý luật đổi lượt: Nếu một bên không còn nước đi hợp lệ nào trên bàn cờ
        if (board.getValidMoves(curPlayer).isEmpty()) {
            int nextPlayer = (curPlayer == Board.BLACK) ? Board.WHITE : Board.BLACK;
            if (board.getValidMoves(nextPlayer).isEmpty()) {
                // Cả hai bên cùng bế tắc -> Kết thúc và phân định kết quả
                endGame();
                return;
            }
            String who = (curPlayer == humanColor) ? "Bạn" : "Máy";
            JOptionPane.showMessageDialog(this, who + " không còn nước đi hợp lệ, tự động chuyển quyền đi!");
            board.switchPlayer();
            TurnBegin();
            return;
        }

        // Kích hoạt luồng chạy tính toán nước đi thông minh của Máy (ComputerPlayer)
        if (player instanceof ComputerPlayer) {
            MoveCallBack callback = new MoveCallBack() {
                @Override
                public void onMove(int row, int col) {
                    if (gameEnded) return;
                    if (row == -1 && col == -1) { 
                        board.switchPlayer();
                        TurnBegin();
                        return;
                    }

                    int flippedCount = board.getFlippableCount(row, col, curPlayer);
                    board.makeMove(row, col, curPlayer);

                    lastMoveRow = row;
                    lastMoveCol = col;
                    lastMoveFlipped = flippedCount;
                    lastMovePlayer = curPlayer;

                    // Đồng bộ dữ liệu hiển thị giao diện đồ họa an toàn trên Main Thread
                    SwingUtilities.invokeLater(() -> {
                        updateLastMoveLabels();
                        board.switchPlayer();
                        TurnBegin();
                    });
                }
            };

            // Tạo khoảng trễ giả lập 1 giây giúp người chơi dễ theo dõi chiến thuật của Máy
            Timer aiDelay = new Timer(1000, e -> {
                if (!gameEnded) {
                    player.makeMove(board, callback);
                }
            });
            aiDelay.setRepeats(false);
            aiDelay.start();
        }
    }

    // UC-1.15: Cập nhật văn bản hiển thị chi tiết lịch sử tọa độ bàn cờ (A1 - H8)
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

    // UC-04: Xử lý Kết thúc trận đấu tiêu chuẩn (Do hết quân hoặc hai bên hết nước đi hợp lệ)
    private void endGame() {
        if (gameEnded) return;
        gameEnded = true;

        // Ngắt bộ đếm thời gian
        if (turnTimer != null && turnTimer.isRunning()) {
            turnTimer.stop();
        }

        // [4.1.1] Tính toán kiểm đếm quân số chung cuộc của cả 2 bên màu Đen và Trắng
        int[] score = board.getScore();
        int blackScore = score[0];
        int whiteScore = score[1];

        // [4.1.2] Thực hiện so sánh số lượng quân cờ để phân định Thắng/Thua/Hòa
        String resultText;
        String resultTitle;
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

        // [4.1.3] Cập nhật nhãn trạng thái tổng quan
        statusLabel.setText(String.format("KẾT THÚC  |  Đen: %d    Trắng: %d", blackScore, whiteScore));

        // Xóa hoàn toàn lưới hiển thị các ô gợi ý nước đi hợp lệ cũ
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                cells[i][j].setState(val != Board.EMPTY ? val : OthelloCell.EMPTY);
            }
        }

        // [4.1.4] Tiến hành lưu kết quả và đồng bộ hóa lâu dài vào tệp tin cơ sở dữ liệu
        boolean playerWon = (humanColor == Board.BLACK && blackScore > whiteScore)
                || (humanColor == Board.WHITE && whiteScore > blackScore);
        boolean playerLost = (humanColor == Board.BLACK && blackScore < whiteScore)
                || (humanColor == Board.WHITE && whiteScore < blackScore);

        // [7.2.1.1] Đồng bộ kết quả hợp lệ sau trận đấu kết thúc hoàn tất
        boolean isTopScore = HighScoreManager.addMatchResult(playerName, playerScore, playerWon, playerLost);

        // [4.1.5] Hiển thị hộp thoại Dialog thông báo kết quả chung cuộc cho người chơi
        showEndGameDialog(resultTitle, resultText, blackScore, whiteScore, playerScore, isTopScore);
    }

    // UC-04 4.2.1: Xử lý trường hợp kết thúc trận đấu do một bên bị cạn kiệt thời gian thi đấu
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
        int playerScore = (humanColor == Board.BLACK) ? blackScore : whiteScore;

        if (humanColor == Board.BLACK) {
            if (winner == Board.BLACK) {
                resultText = "Đối thủ hết giờ! Bạn THẮNG!";
                resultTitle = "Chiến thắng!";
            } else {
                resultText = "Bạn đã hết giờ! Bạn THUA!";
                resultTitle = "Hết giờ!";
            }
        } else {
            if (winner == Board.WHITE) {
                resultText = "Đối thủ hết giờ! Bạn THẮNG!";
                resultTitle = "Chiến thắng!";
            } else {
                resultText = "Bạn đã hết giờ! Bạn THUA!";
                resultTitle = "Hết giờ!";
            }
        }

        statusLabel.setText(String.format("HẾT GIỜ  |  Đen: %d    Trắng: %d", blackScore, whiteScore));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int val = board.getCell(i, j);
                cells[i][j].setState(val != Board.EMPTY ? val : OthelloCell.EMPTY);
            }
        }

        // [7.2.1.1] Đồng bộ lưu kết quả phân định khi ván đấu hoàn tất toàn vẹn thời gian
        boolean playerWon = (humanColor == Board.BLACK && winner == Board.BLACK) 
                || (humanColor == Board.WHITE && winner == Board.WHITE);
        boolean playerLost = !playerWon;

        boolean isTopScore = HighScoreManager.addMatchResult(playerName, playerScore, playerWon, playerLost);

        showEndGameDialog(resultTitle, resultText, blackScore, whiteScore, playerScore, isTopScore);
    }

    // UC-04: Thiết lập và cấu trúc giao diện Dialog thông báo kết quả chi tiết
    private void showEndGameDialog(String title, String resultText,
            int blackScore, int whiteScore, int playerScore, boolean isTopScore) {

        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        resultPanel.setBackground(new Color(30, 60, 15));

        JLabel resultLabel = new JLabel(resultText, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 22));
        resultLabel.setForeground(new Color(255, 220, 50));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(resultLabel);
        resultPanel.add(Box.createVerticalStrut(15));

        JLabel scoreDetail = new JLabel(
                String.format("Đen: %d  —  Trắng: %d", blackScore, whiteScore), SwingConstants.CENTER);
        scoreDetail.setFont(new Font("Arial", Font.BOLD, 18));
        scoreDetail.setForeground(Color.WHITE);
        scoreDetail.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(scoreDetail);
        resultPanel.add(Box.createVerticalStrut(8));

        JLabel playerScoreLabel = new JLabel(
                String.format("Điểm của bạn (%s): %d", playerName, playerScore), SwingConstants.CENTER);
        playerScoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        playerScoreLabel.setForeground(new Color(180, 220, 130));
        playerScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(playerScoreLabel);

        // Hiển thị thông báo phụ nếu thành tích lọt vào Top 10 kỷ lục cao nhất
        if (isTopScore) {
            resultPanel.add(Box.createVerticalStrut(8));
            JLabel topLabel = new JLabel("(*) Thành tích xuất sắc lọt vào Bảng xếp hạng!", SwingConstants.CENTER);
            topLabel.setFont(new Font("Arial", Font.BOLD, 15));
            topLabel.setForeground(new Color(255, 200, 50));
            topLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultPanel.add(topLabel);
        }

        dialog.add(resultPanel, BorderLayout.CENTER);

        // Thanh công cụ chứa các nút tùy chọn hành động sau ván đấu
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(30, 60, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Nút Chơi lại ván mới lập tức
        JButton restartBtn = createDialogButton("Chơi lại", new Color(46, 160, 67));
        restartBtn.addActionListener(e -> {
            dialog.dispose();
            restartGame();
        });
        buttonPanel.add(restartBtn);

        // [5.1.0] Người chơi click nút "Xếp hạng" -> Mở màn hình Leaderboard Dialog dạng Modal
        JButton leaderboardBtn = createDialogButton("Xếp hạng", new Color(33, 109, 185));
        leaderboardBtn.addActionListener(e -> {
            new LeaderboardDialog(dialog, playerName).setVisible(true);
        });
        buttonPanel.add(leaderboardBtn);

        // Nút giải phóng ván đấu để quay về giao diện Menu chính
        JButton menuBtn = createDialogButton("Menu", new Color(120, 60, 20));
        menuBtn.addActionListener(e -> {
            dialog.dispose();
            dispose(); 
        });
        buttonPanel.add(menuBtn);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

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

    // Setter cho tên người chơi (được gọi từ điều hướng MainMenu / UC-01)
    public void setPlayerName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.playerName = name.trim();
        }
    }

    // [5.1.0] Mở trực tiếp màn hình xem bảng xếp hạng trong game
    public void openLeaderboard() {
        new LeaderboardDialog(this, playerName).setVisible(true);
    }
}