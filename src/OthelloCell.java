
// Class vẽ cờ trong 1 ô
import javax.swing.*;
import java.awt.*;

public class OthelloCell extends JPanel {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int VALID_MOVE = 3;

    private int state = EMPTY;
    private static final Color BG_COLOR = new Color(53, 101, 21); // Màu xanh
    private static final Color LIGHT_GREEN = new Color(144, 238, 144);

    public OthelloCell() {
        setPreferredSize(new Dimension(70, 70));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(new Color(0, 70, 0), 1));
    }

    public void setState(int state) {
        this.state = state;
        repaint();
    }
    public int getState() {
    	return this.state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int diameter = Math.min(w, h) - 8;
        int x = (w - diameter) / 2;
        int y = (h - diameter) / 2;

        g2d.setColor(BG_COLOR);
        g2d.fillRect(0, 0, w, h);

        if (state == BLACK) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x, y, diameter, diameter);
        } else if (state == WHITE) {
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, diameter, diameter);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawOval(x, y, diameter, diameter);
        } else if (state == VALID_MOVE) {
            g2d.setColor(LIGHT_GREEN);
            g2d.fillOval(x, y, diameter, diameter);
            g2d.setColor(Color.ORANGE);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawOval(x, y, diameter, diameter);
        }

        g2d.dispose();
    }

// Class vẽ cờ trong 1 ô
import javax.swing.*;
import java.awt.*;

public class OthelloCell extends JPanel {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int VALID_MOVE = 3;

    private int state = EMPTY;
    private static final Color BG_COLOR = new Color(53, 101, 21); // Màu xanh
    private static final Color LIGHT_GREEN = new Color(144, 238, 144);

    public OthelloCell() {
        setPreferredSize(new Dimension(70, 70));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(new Color(0, 70, 0), 1));
    }

    public void setState(int state) {
        this.state = state;
        repaint();
    }
    public int getState() {
    	return this.state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int diameter = Math.min(w, h) - 8;
        int x = (w - diameter) / 2;
        int y = (h - diameter) / 2;

        g2d.setColor(BG_COLOR);
        g2d.fillRect(0, 0, w, h);

        if (state == BLACK) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x, y, diameter, diameter);
        } else if (state == WHITE) {
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, diameter, diameter);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawOval(x, y, diameter, diameter);
        } else if (state == VALID_MOVE) {
            g2d.setColor(LIGHT_GREEN);
            g2d.fillOval(x, y, diameter, diameter);
            g2d.setColor(Color.ORANGE);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawOval(x, y, diameter, diameter);
        }

        g2d.dispose();
    }

}