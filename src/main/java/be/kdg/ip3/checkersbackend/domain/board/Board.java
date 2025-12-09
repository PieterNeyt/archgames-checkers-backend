package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.NotFoundException;
import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.piece.PieceType;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Position;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Board {
    @Identity
    private final BoardId boardId;
    private final Square[][] board;

    public Board() {
        this(BoardId.create(), new Square[8][8]);
        initializeBoard();
    }

    public Board(BoardId boardId, Square[][] board) {
        this.boardId = boardId;
        this.board = board;
    }

    private void initializeBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int ii = 0; ii < board[i].length; ii++) {
                board[i][ii] = new Square(i, ii, ((i + ii) % 2 == 0) ? SquareColor.LIGHT_BROWN : SquareColor.DARK_BROWN);
            }
        }
        setupPieces();
    }

    private void setupPieces() {
        // Black pieces
        for (int i = 0; i < board.length / 2 - 1; i++) {
            for (int ii = 0; ii < board[i].length; ii++) {
                if ((i + ii) % 2 != 0) {
                    board[i][ii].placePiece(new Piece(PieceColor.BLACK, PieceType.MAN));
                }
            }
        }

        // White pieces
        for (int i = board.length / 2 + 1; i < board.length; i++) {
            for (int ii = 0; ii < board[i].length; ii++) {
                if ((i + ii) % 2 != 0) {
                    board[i][ii].placePiece(new Piece(PieceColor.WHITE, PieceType.MAN));
                }
            }
        }
    }

    public Square getSquare(int row, int col) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[row].length) {
            throw new NotFoundException("Square not found at row " + row + ", col " + col);
        }

        Square square = board[row][col];
        if (square == null) {
            throw new NotFoundException("Square not found at row " + row + ", col " + col);
        }
        return square;
    }

    public List<Move> getValidMoves(int row, int col, PieceColor currentPlayerColor) {
        var fromSquare = getSquare(row, col);

        if (fromSquare.isEmpty()) {
            return new ArrayList<>();
        }

        var piece = fromSquare.getPiece();
        if (piece.getColor() != currentPlayerColor) {
            return new ArrayList<>();
        }

        var jumps = getValidJumps(row, col, piece, currentPlayerColor);
        if (!jumps.isEmpty()) {
            return jumps;
        }

        return getValidSimpleMoves(row, col, piece,currentPlayerColor);
    }


    //  Geeft lijst van posities van stukken die op dit moment een geldige zet kunnen doen.
    public List<Position> getPiecesWithValidMoves(PieceColor color) {
        List<Position> positions = new ArrayList<>();
        //om te kijken of er geslagen kan worden
        var mustJump = hasJumpsAvailable(color);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var square = getSquare(row, col);
                if (!square.isEmpty() && square.getPiece().getColor() == color) {
                    if (mustJump) {
                        // Als er gesprongen moet worden
                        if (!getValidJumps(row, col, square.getPiece(),color).isEmpty()) {
                            positions.add(new Position(row, col));
                        }
                    } else {
                        if (!getValidMoves(row, col, color).isEmpty()) {
                            positions.add(new Position(row, col));
                        }
                    }
                }
            }
        }
        return positions;
    }

    private int[] getMoveDirections(Piece piece) {

        //koning mag achterwaards
        if (piece.isKing()) {
            return new int[]{-1, 1};
        }

        if (piece.getColor() == PieceColor.WHITE) {
            return new int[]{-1}; // Wit gaat naar boven op het bord
        } else {
            return new int[]{1}; // Zwart gaat naar beneden op het bord
        }
    }


    private List<Move> getValidSimpleMoves(int row, int col, Piece piece, PieceColor currentPlayerColor) {
        List<Move> moves = new ArrayList<>();
        var directions = getMoveDirections(piece);

        for (int dRow : directions) {
            for (int dCol : new int[]{-1, 1}) {
                var newRow = row + dRow;
                var newCol = col + dCol;

                if (isValidPosition(newRow, newCol) && getSquare(newRow, newCol).isEmpty()) {
                    moves.add(new Move(row, col, newRow, newCol, currentPlayerColor));
                }
            }
        }

        return moves;
    }

    public List<Move> getValidJumps(int row, int col, Piece piece, PieceColor playerColor) {
        List<Move> jumps = new ArrayList<>();
        var directions = getMoveDirections(piece);

        for (int dRow : directions) {
            for (int dCol : new int[]{-1, 1}) {
                var captureRow = row + dRow;
                var captureCol = col + dCol;
                var jumpRow = row + 2 * dRow;
                var jumpCol = col + 2 * dCol;

                if (isValidJump(jumpRow, jumpCol, captureRow, captureCol, piece)) {
                    jumps.add(new Move(row, col, jumpRow, jumpCol, List.of(new Position(captureRow, captureCol)), playerColor));
                }
            }
        }

        return jumps;
    }

    private boolean isValidJump( int toRow, int toCol, int captureRow, int captureCol, Piece piece) {
        if (!isValidPosition(toRow, toCol) || !isValidPosition(captureRow, captureCol)) {
            return false;
        }

        var targetSquare = getSquare(toRow, toCol);
        var captureSquare = getSquare(captureRow, captureCol);

        return targetSquare.isEmpty()
                && !captureSquare.isEmpty()
                && captureSquare.getPiece().getColor() != piece.getColor();
    }

    public boolean hasJumpsAvailable(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var square = getSquare(row, col);
                if (!square.isEmpty() && square.getPiece().getColor() == color && !getValidJumps(row, col, square.getPiece(), color).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Move executeMove(Move move, PieceColor currentPlayerColor) {
        var fromSquare = getSquare(move.getFromRow(), move.getFromCol());
        var toSquare = getSquare(move.getToRow(), move.getToCol());

        if (fromSquare.isEmpty()) {
            throw new IllegalArgumentException("No piece at start position");
        }

        var piece = fromSquare.getPiece();

        // verwijder de gevangen stukken
        for (Position capturedPos : move.getCapturedPositions()) {
            getSquare(capturedPos.row(), capturedPos.col()).removePiece();
        }

        fromSquare.removePiece();
        toSquare.placePiece(piece);

        if (shouldPromote(piece, move.getToRow())) {
            piece.promoteToKing();
        }
        move.playedBy(currentPlayerColor);
        return move;
    }

    private boolean shouldPromote(Piece piece, int row) {
        if (piece.isKing()) {
            return false;
        }
        return (piece.getColor() == PieceColor.WHITE && row == 0) || (piece.getColor() == PieceColor.BLACK && row == 7);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean hasMovesAvailable(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var square = getSquare(row, col);
                if (!square.isEmpty() && square.getPiece().getColor() == color) {
                    if (!getValidMoves(row, col, color).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}