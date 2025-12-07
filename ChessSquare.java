import javax.swing.JButton;
import java.awt.*;

class ChessSquare extends JButton {
    protected boolean highlight = false;
    protected int row, col;

    public ChessSquare(String text, int row, int col) {
        super(text);
        setFont(new Font("Arial Unicode MS", Font.PLAIN, 36));
        this.setRow(row);
        this.setCol(col);
        setBackground((row + col) % 2 == 0 ? Color.WHITE : new Color(170, 170, 255));
        setOpaque(true);
        setBorderPainted(false);
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getcol() {
        return col;
    }

    public void setColor(Color c) {
        setBackground(c);
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (highlight) {
            Graphics2D g2 = (Graphics2D) g;
            int d = Math.min(getWidth(), getHeight()) / 2;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;
            if(this.getText() != " "){
                g2.setColor(Color.RED);
                g2.drawOval(x, y, d, d);
            }else{
                g2.setColor(new Color(0, 200, 0, 150));
                g2.fillOval(x, y, d, d);
            }
        }
    }

    public void selected(){
        setBackground(Color.GRAY);
    }

    public void unselected(){
        setBackground((row + col) % 2 == 0 ? Color.WHITE : new Color(170, 170, 255));
    }
}
