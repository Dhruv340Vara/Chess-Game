import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ChessGame extends JFrame {
    Validate valid = new Validate();
    ChessBoard c = new ChessBoard();
    public ChessGame() {
        setTitle("Chess Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel chessPanel = valid.getChessPanel();
        JPanel wrapper = new JPanel(new GridBagLayout());
        c.drawBoard();
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                chessPanel.add(c.squares[i][j]);
            }
        }
        wrapper.add(chessPanel);
        add(wrapper, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ChessGame();
    }
}