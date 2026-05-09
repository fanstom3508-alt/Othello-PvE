import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class OthelloGame extends JFrame {
    private Board board;
    private Player blackPlayer;
    private Player whitePlayer;
    private OthelloCell[][] cells;
    private JPanel boardPanel;
    private JLabel statusLabel, timeBlackLabel, timeWhiteLabel;
    private Timer turnTimer;
    private long timeLeftBlack = 20L * 60 * 1000;
    private long timeLeftWhite = 20L * 60 * 1000;

    public OthelloGame() {
        board = new Board();
        setTitle("Cờ Othello - Người vs Máy");
        setSize(650, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // đóng game thì trở về menu
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Người chơi luôn là Đen, Máy luôn là Trắng
        blackPlayer = new HumanPlayer(Board.BLACK);
        whitePlayer = new ComputerPlayer(Board.WHITE);

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

    // Hàm mô tả thành phần menu
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
            turnTimer.stop();
            dispose();
        });
        exit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.add(endGame);
        gameMenu.addSeparator();
        gameMenu.add(backMenu);
        gameMenu.add(exit);
        menuBar.add(gameMenu);

        setJMenuBar(menuBar);
    }

    // Hàm tạo, vẽ bảng
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

    // Hàm tạo Panel thông tin trận đấu phía dưới bảng
    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Lượt đi: Đen (Bạn)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));

        timeBlackLabel = new JLabel("Đen (Bạn): 20:00");
        timeWhiteLabel = new JLabel("Trắng (Máy): 20:00");
        timeBlackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timeWhiteLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        timePanel.add(timeBlackLabel);
        timePanel.add(new JLabel("|"));
        timePanel.add(timeWhiteLabel);

        statusPanel.add(statusLabel);
        statusPanel.add(timePanel);
        add(statusPanel, BorderLayout.SOUTH);

        turnTimer = new Timer(1000, e -> updateTimer());
    }

    // Cập nhật dữ kiện thời gian trận đấu
    private void updateTimer() {
        int current = board.getCurrentPlayer();
        if (current == Board.BLACK) {
            timeLeftBlack = Math.max(0, timeLeftBlack - 1000);
            timeBlackLabel.setText("Đen (Bạn): " + formatTime(timeLeftBlack));
            if (timeLeftBlack <= 0) endGameByTime(Board.WHITE);
        } else {
            timeLeftWhite = Math.max(0, timeLeftWhite - 1000);
            timeWhiteLabel.setText("Trắng (Máy): " + formatTime(timeLeftWhite));
            if (timeLeftWhite <= 0) endGameByTime(Board.BLACK);
        }
    }

    private String formatTime(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Hàm bắt đầu game lại từ đầu
    private void resetGame() {
        board.reset();
        timeLeftBlack = 20L * 60 * 1000;
        timeLeftWhite = 20L * 60 * 1000;
        timeBlackLabel.setText("Đen (Bạn): 20:00");
        timeWhiteLabel.setText("Trắng (Máy): 20:00");
        if (turnTimer != null && turnTimer.isRunning()) turnTimer.stop();
        turnTimer.start();
        updateBoard();
    }

    // Hàm xử lý sự kiện khi người chơi click vào (chỉ lượt Đen - người chơi)
    private void handleMove(int row, int col) {
        if (board.getCurrentPlayer() == Board.BLACK) {
            if (board.isValidMove(row, col, Board.BLACK)) {
                board.makeMove(row, col, Board.BLACK);
                board.switchPlayer();
                TurnBegin();
            }
        }
    }

    private void TurnBegin() {
        if (board.isGameOver()) {
            endGame();
            return;
        }

        // Cập nhật bàn cờ hiển thị nước vừa đi
        updateBoard();

        int curPlayer = board.getCurrentPlayer();
        Player player = (curPlayer == Board.BLACK) ? blackPlayer : whitePlayer;

        // Kiểm tra nếu người chơi hiện tại không còn nước đi
        if (board.getValidMoves(curPlayer).isEmpty()) {
            JOptionPane.showMessageDialog(this,
                (curPlayer == Board.BLACK ? "Bạn" : "Máy") + " không còn nước đi, chuyển lượt!");
            board.switchPlayer();
            TurnBegin();
            return;
        }

        if (player instanceof ComputerPlayer) {
            MoveCallBack callback = new MoveCallBack() {
                @Override
                public void onMove(int row, int col) {
                    if (row == -1 && col == -1) { // Máy pass
                        board.switchPlayer();
                        TurnBegin();
                        return;
                    }
                    board.makeMove(row, col, curPlayer);
                    board.switchPlayer();
                    TurnBegin();
                }
            };
            player.makeMove(board, callback);
        }
    }

    // Hàm cập nhật lại bảng
    private void updateBoard() {
        if (board.isGameOver()) {
            endGame();
            return;
        }
        int current = board.getCurrentPlayer();
        int[] score = board.getScore();
        String turnLabel = (current == Board.BLACK) ? "Đen (Bạn)" : "Trắng (Máy)";
        statusLabel.setText(String.format(
            "Lượt: %s    |    Đen: %d    Trắng: %d",
            turnLabel, score[0], score[1]));

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

    // Hàm dừng và kết thúc game
    private void endGame() {
        turnTimer.stop();
        int[] score = board.getScore();
        String result = score[0] > score[1] ? "🖤 Bạn (Đen) thắng!" :
                        score[1] > score[0] ? "⚪ Máy (Trắng) thắng!" : "🤝 Hòa!";
        String msg = result + "\nĐiểm: Đen " + score[0] + " - Trắng " + score[1] +
                     "\nThời gian còn:\nBạn (Đen): " + formatTime(timeLeftBlack) +
                     "\nMáy (Trắng): " + formatTime(timeLeftWhite);
        int choice = JOptionPane.showOptionDialog(this, msg, "Kết thúc ván",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
            new String[]{"Chơi lại", "Về menu"}, "Chơi lại");
        if (choice == 0) resetGame();
        else dispose();
    }

    // Hàm kết thúc game do hết thời gian
    private void endGameByTime(int winner) {
        turnTimer.stop();
        String who = (winner == Board.BLACK) ? "Bạn (Đen)" : "Máy (Trắng)";
        String msg = who + " thắng do đối thủ hết giờ!";
        JOptionPane.showMessageDialog(this, msg, "Hết giờ!", JOptionPane.WARNING_MESSAGE);
    }
}
