import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.*;
import java.util.*;
import java.util.List;

class Validate {
    int kingRow = -1;
    int kingCol = -1;
    String currentPlayer = "white";
    private JPanel chessPanel = new JPanel(new GridLayout(8, 8));
    public boolean isKingInCheck = false;
    List<int[]> checkingPieces = new ArrayList<>();
    boolean whiteKingMoved = false;
    boolean blackKingMoved = false;
    boolean whiteRookLeftMoved = false; // rook at (7,0)
    boolean whiteRookRightMoved = false; // rook at (7,7)
    boolean blackRookLeftMoved = false; // rook at (0,0)
    boolean blackRookRightMoved = false; // rook at (0,7)

    char[][] board = new char[][] {
    { 'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r' },
    { 'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' },
    { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a' },
    { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a' },
    { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a' },
    { 'a', 'a', 'a', 'a', 'a', 'a', 'a', 'a' },
    { 'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
    { 'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R' },
    };
    protected HashMap<Character, String> pieces = new HashMap<>() {
        {
            put('K', "\u2654");
            put('Q', "\u2655");
            put('R', "\u2656");
            put('B', "\u2657");
            put('N', "\u2658");
            put('P', "\u2659");
            put('k', "\u265A");
            put('q', "\u265B");
            put('r', "\u265C");
            put('b', "\u265D");
            put('n', "\u265E");
            put('p', "\u265F");
            put('a', " ");
        }
    };

    Validate() {
        this.chessPanel.setPreferredSize(new Dimension(600, 600));
        this.chessPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
    }

    void setChessPanel(JPanel chessPanel) {
        this.chessPanel = chessPanel;
        chessPanel.repaint();
    }

    JPanel getChessPanel() {
        return chessPanel;
    }

    public boolean isValidMove(String currentPlayer, char fromPiece, char toPiece, int fromRow, int fromCol, int toRow,
            int toCol, char[][] board) {

        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }

        if ((currentPlayer.equals("black") && Character.isLowerCase(toPiece)) ||
                (currentPlayer.equals("white") && Character.isUpperCase(toPiece))) {
            if (toPiece != 'a')
                return false;
        }

        if (Character.toLowerCase(fromPiece) == 'r' ||
                Character.toLowerCase(fromPiece) == 'b' ||
                Character.toLowerCase(fromPiece) == 'q') {

            int rowDir = Integer.signum(toRow - fromRow);
            int colDir = Integer.signum(toCol - fromCol);

            int row = fromRow + rowDir;
            int col = fromCol + colDir;

            while (row != toRow || col != toCol) {
                if (board[row][col] != 'a') {
                    if (!(board[row][col] == 'K' && currentPlayer.equals("black"))
                            && !(board[row][col] == 'k' && currentPlayer.equals("white")))
                        return false;
                }
                if (row != toRow)
                    row += rowDir;
                if (col != toCol)
                    col += colDir;
            }
        }

        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        switch (Character.toLowerCase(fromPiece)) {
            case 'r':
                return rowDiff == 0 || colDiff == 0;
            case 'n':
                return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
            case 'b':
                return rowDiff == colDiff;
            case 'q':
                return (rowDiff == 0 || colDiff == 0) || (rowDiff == colDiff);
            case 'k':
                if (Math.abs(fromCol - toCol) == 2 && rowDiff == 0) {
                    if (isCastlingMove(fromPiece, fromRow, fromCol, toRow, toCol, board)) {
                        return true;
                    }
                }
                return rowDiff <= 1 && colDiff <= 1;
            case 'p':
                if (fromPiece == 'p') {
                    if (toPiece == 'a') {
                        if (toRow - fromRow == 1 && colDiff == 0)
                            return true;
                        if (fromRow == 1 && toRow - fromRow == 2 && colDiff == 0 && board[fromRow + 1][fromCol] == 'a')
                            return true;
                    } else {
                        return (toRow - fromRow == 1 && colDiff == 1);
                    }
                } else {
                    if (toPiece == 'a') {
                        if (fromRow - toRow == 1 && colDiff == 0)
                            return true;
                        if (fromRow == 6 && fromRow - toRow == 2 && colDiff == 0 && board[fromRow - 1][fromCol] == 'a')
                            return true;
                    } else {
                        return (fromRow - toRow == 1 && colDiff == 1);
                    }
                }
                return false;

            default:
                return false;
        }
    }

    boolean isCastlingMove(char frompiece, int fromRow, int fromCol, int toRow, int toCol, char[][] board) {
        if (Character.toLowerCase(frompiece) != 'k')
            return false;

        if (frompiece == 'K' && (fromRow != 7 || fromCol != 4))
            return false;
        if (frompiece == 'k' && (fromRow != 0 || fromCol != 4))
            return false;

        if (fromRow != toRow || Math.abs(fromCol - toCol) != 2)
            return false;

        int direction = (toCol - fromCol) > 0 ? 1 : -1;
        int rookCol = direction == 1 ? 7 : 0;

        if (board[fromRow][rookCol] != (frompiece == 'K' ? 'R' : 'r'))
            return false;

        if (frompiece == 'K') {
            if (whiteKingMoved)
                return false;
            if (direction == -1 && whiteRookLeftMoved)
                return false;
            if (direction == 1 && whiteRookRightMoved)
                return false;
        } else if (frompiece == 'k') {
            if (blackKingMoved)
                return false;
            if (direction == -1 && blackRookLeftMoved)
                return false;
            if (direction == 1 && blackRookRightMoved)
                return false;
        }

        for (int c = fromCol + direction; c != rookCol; c += direction) {
            if (board[fromRow][c] != 'a')
                return false;
        }
        for (int c = fromCol; c != toCol + direction; c += direction) {
            if (isSquareAttacked(fromRow, c))
                return false;
        }

        return true;
    }

    void performCastlingMove(char frompiece, int fromRow, int fromCol, int toRow, int toCol, char[][] board) {
        int direction = (toCol - fromCol) > 0 ? 1 : -1;
        int rookCol = direction == 1 ? 7 : 0;

        board[toRow][toCol] = frompiece;
        board[fromRow][fromCol] = 'a';

        board[toRow][toCol - direction] = board[toRow][rookCol];
        board[toRow][rookCol] = 'a';

        if (frompiece == 'K') {
            whiteKingMoved = true;
            if (direction == -1)
                whiteRookLeftMoved = true;
            else
                whiteRookRightMoved = true;
        } else if (frompiece == 'k') {
            blackKingMoved = true;
            if (direction == -1)
                blackRookLeftMoved = true;
            else
                blackRookRightMoved = true;
        }
        chessPanel.revalidate();
        chessPanel.repaint();
    }

    void isKingInCheck(ChessSquare[][] squares, String currentPlayer) {
        boolean isWhite = currentPlayer.equals("white");
        char kingChar = isWhite ? 'K' : 'k';
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == kingChar) {
                    kingRow = i;
                    kingCol = j;
                    break;
                }
            }
        }
        checkingPieces.clear();
        isKingInCheck = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (piece == 'a')
                    continue;
                if ((Character.isUpperCase(piece) && isWhite) || (Character.isLowerCase(piece) && !isWhite))
                    continue;
                if (isValidMove(isWhite ? "black" : "white", piece, board[kingRow][kingCol], i, j, kingRow, kingCol,
                        board)) {
                    isKingInCheck = true;
                    checkingPieces.add(new int[] { i, j });
                    squares[kingRow][kingCol].setColor(Color.RED);
                }
            }
        }
    }

    boolean isSquareAttacked(int row, int col) {
        boolean isWhite = currentPlayer.equals("white");

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                char piece = board[i][j];
                if (piece == 'a')
                    continue;

                if ((Character.isUpperCase(piece) && isWhite) || (Character.isLowerCase(piece) && !isWhite))
                    continue;

                if (isValidMove(isWhite ? "black" : "white", piece, board[row][col], i, j, row, col, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    void promotePawn(int row, int col, boolean isWhite) {
        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        String choice = (String) JOptionPane.showInputDialog(
                chessPanel,
                "Choose promotion piece:",
                "Pawn Promotion",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                "Queen");

        if (choice == null)
            choice = "Queen";

        switch (choice) {
            case "Rook":
                board[row][col] = isWhite ? 'R' : 'r';
                break;
            case "Bishop":
                board[row][col] = isWhite ? 'B' : 'b';
                break;
            case "Knight":
                board[row][col] = isWhite ? 'N' : 'n';
                break;
            default:
                board[row][col] = isWhite ? 'Q' : 'q';
                break;
        }
    }

}