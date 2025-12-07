import java.awt.*;
import javax.swing.JPanel;

public class ChessBoard {
    int selectedRow = -1, selectedCol = -1;
    ChessSquare[][] squares = new ChessSquare[8][8];
    Validate valid = new Validate();

    public String piece(int i, int j) {
        return valid.pieces.get(valid.board[i][j]);
    }

    public void drawBoard() {
        JPanel chessPanel = valid.getChessPanel();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = new ChessSquare(piece(i, j), i, j);
                int row = i, col = j;
                squares[i][j].addActionListener(e -> handleClick(row, col));
            }
        }
        chessPanel.revalidate();
        valid.setChessPanel(chessPanel);
    }

    void handleClick(int row, int col) {
        char piece = valid.board[row][col];

        if (((valid.currentPlayer.equals("white") && Character.isUpperCase(piece)) ||
                (valid.currentPlayer.equals("black") && Character.isLowerCase(piece))) && piece != 'a') {

            clearHighlights();

            if (selectedRow != -1 && selectedCol != -1) {
                squares[selectedRow][selectedCol].unselected();
            }

            selectedRow = row;
            selectedCol = col;
            squares[row][col].setColor(Color.GRAY);
            hiliteMove(piece, row, col);
        } else if (selectedRow != -1 && selectedCol != -1 && squares[row][col].highlight) {
            char movingPiece = valid.board[selectedRow][selectedCol];
            if (Character.toLowerCase(movingPiece) == 'k' && Math.abs(col - selectedCol) == 2) {
                valid.performCastlingMove(movingPiece, selectedRow, selectedCol, row, col, valid.board);
            } else {
                valid.board[row][col] = movingPiece;
                valid.board[selectedRow][selectedCol] = 'a';
            }
            if (Character.toLowerCase(valid.board[row][col]) == 'p') {
                if (row == 0 && valid.board[row][col] == 'P') {
                    valid.promotePawn(row, col, true);
                } else if (row == 7 && valid.board[row][col] == 'p') {
                    valid.promotePawn(row, col, false);
                }
            }

            squares[row][col].setText(piece(row, col));
            squares[selectedRow][selectedCol].setText(piece(selectedRow, selectedCol));
            if (Character.toLowerCase(movingPiece) == 'k' && Math.abs(col - selectedCol) == 2) {
                int rookFromCol = (col - selectedCol) > 0 ? 7 : 0;
                int rookToCol = (col - selectedCol) > 0 ? col - 1 : col + 1;

                squares[row][rookFromCol].setText(piece(row, rookFromCol));
                squares[row][rookToCol].setText(piece(row, rookToCol));
            }
            
            // After moving piece on board:
            if (Character.toLowerCase(movingPiece) == 'k') {
                if (valid.currentPlayer.equals("white")) {
                    valid.whiteKingMoved = true;
                } else {
                    valid.blackKingMoved = true;
                }
            } else if (Character.toLowerCase(movingPiece) == 'r') {
                if (valid.currentPlayer.equals("white")) {
                    if (selectedRow == 7 && selectedCol == 0)
                        valid.whiteRookLeftMoved = true;
                    if (selectedRow == 7 && selectedCol == 7)
                        valid.whiteRookRightMoved = true;
                } else {
                    if (selectedRow == 0 && selectedCol == 0)
                        valid.blackRookLeftMoved = true;
                    if (selectedRow == 0 && selectedCol == 7)
                        valid.blackRookRightMoved = true;
                }
            }

            squares[selectedRow][selectedCol].unselected();
            clearHighlights();
            valid.currentPlayer = valid.currentPlayer.equals("white") ? "black" : "white";
            valid.isKingInCheck(squares, valid.currentPlayer);
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    public void hiliteMove(char piece, int row, int col) {
        clearHighlights();
        if (valid.isKingInCheck) {

            if (valid.checkingPieces.size() > 1) {
                if (Character.toLowerCase(piece) == 'k') {
                    highlightKingMoves(piece, row, col);
                }
                return;
            }

            int[] attacker = valid.checkingPieces.get(0);
            int attackerRow = attacker[0], attackerCol = attacker[1];

            if (Character.toLowerCase(piece) == 'k') {
                highlightKingMoves(piece, row, col);
            } else {
                if (valid.isValidMove(valid.currentPlayer, piece, valid.board[attackerRow][attackerCol], row, col,
                        attackerRow,
                        attackerCol, valid.board)) {
                    squares[attackerRow][attackerCol].setHighlight(true);
                }

                char attackerPiece = Character.toLowerCase(valid.board[attackerRow][attackerCol]);
                if (attackerPiece == 'b' || attackerPiece == 'r' || attackerPiece == 'q') {
                    int rowDir = Integer.signum(valid.kingRow - attackerRow);
                    int colDir = Integer.signum(valid.kingCol - attackerCol);
                    int r = attackerRow + rowDir, c = attackerCol + colDir;
                    while (r != valid.kingRow || c != valid.kingCol) {
                        if (valid.isValidMove(valid.currentPlayer, piece, valid.board[r][c], row, col, r, c,
                                valid.board)) {
                            squares[r][c].setHighlight(true);
                        }
                        r += rowDir;
                        c += colDir;
                    }
                }
            }
        } else {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    if (valid.isValidMove(valid.currentPlayer, piece, valid.board[r][c], row, col, r, c, valid.board)) {
                        if (Character.toLowerCase(piece) == 'k') {
                            if (!valid.isSquareAttacked(r, c)) {
                                squares[r][c].setHighlight(true);
                            }
                        } else {
                            squares[r][c].setHighlight(true);
                        }
                    }
                }
            }
        }
    }

    void clearHighlights() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j].setHighlight(false);
            }
        }
    }

    void highlightKingMoves(char king, int row, int col) {
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                    if (valid.isValidMove(valid.currentPlayer, king, valid.board[r][c], row, col, r, c, valid.board)) {
                        if (!valid.isSquareAttacked(r, c)) {
                            squares[r][c].setHighlight(true);
                        }
                    }
                }
            }
        }
    }

}