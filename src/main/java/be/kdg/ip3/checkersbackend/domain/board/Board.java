package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.NotFoundException;
import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.piece.PieceType;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.domain.player.Position;
import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@ValueObject
public class Board {
    private final BoardId boardId;
    private final Square[][] squares;

    public Board() {
        this(BoardId.create(), initializeBoard());
    }

    public Board(BoardId boardId, Square[][] squares) {
        this.boardId = boardId;
        this.squares = squares;
    }

    private static Square[][] initializeBoard() {
        var grid = new Square[8][8];
        for (int i = 0; i < 8; i++) {
            for (int ii = 0; ii < 8; ii++) {
                grid[i][ii] = new Square(i, ii, ((i + ii) % 2 == 0) ? SquareColor.LIGHT_BROWN : SquareColor.DARK_BROWN);
            }
        }
        setupPieces(grid);
        return grid;
    }

    private static void setupPieces(Square[][] grid) {
        for (int i = 0; i < 3; i++) {
            for (int ii = 0; ii < 8; ii++) {
                if ((i + ii) % 2 != 0) {
                    grid[i][ii] = grid[i][ii].withPiece(new Piece(PieceColor.BLACK, PieceType.MAN));
                }
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int ii = 0; ii < 8; ii++) {
                if ((i + ii) % 2 != 0) {
                    grid[i][ii] = grid[i][ii].withPiece(new Piece(PieceColor.WHITE, PieceType.MAN));
                }
            }
        }
    }

    public Square getSquare(int row, int col) {
        if (!isValidPosition(row, col)) {
            throw new NotFoundException("Square not found at row " + row + ", col " + col);
        }
        return squares[row][col];
    }

    public MoveResult executeMove(Move move) {

        var fromSquare = getSquare(move.fromRow(), move.fromCol());

        if (fromSquare.isEmpty()) {
            throw new IllegalArgumentException("No piece at start position");
        }

        var newGrid = copyGrid(this.squares);
        var piece = fromSquare.piece();

        for (Position capturedPos : move.capturedPositions()) {
            var capSq = newGrid[capturedPos.row()][capturedPos.col()];
            newGrid[capturedPos.row()][capturedPos.col()] = capSq.emptied();
        }

        newGrid[move.fromRow()][move.fromCol()] = newGrid[move.fromRow()][move.fromCol()].emptied();

        if (shouldPromote(piece, move.toRow())) {
            piece = piece.promoted();
        }

        newGrid[move.toRow()][move.toCol()] = newGrid[move.toRow()][move.toCol()].withPiece(piece);

        return new MoveResult(new Board(this.boardId, newGrid), move);
    }

    private Square[][] copyGrid(Square[][] source) {
        Square[][] dest = new Square[8][];
        for (int i = 0; i < 8; i++) {
            dest[i] = Arrays.copyOf(source[i], 8);
        }
        return dest;
    }

    public List<Move> getValidMoves(int row, int col, PieceColor currentPlayerColor) {
        var fromSquare = getSquare(row, col);

        if (fromSquare.isEmpty()) {
            return new ArrayList<>();
        }

        var piece = fromSquare.piece();
        if (piece.color() != currentPlayerColor) {
            return new ArrayList<>();
        }

        var jumps = getValidJumps(row, col, piece, currentPlayerColor);
        if (!jumps.isEmpty()) {
            return jumps;
        }

        return getValidSimpleMoves(row, col, piece, currentPlayerColor);
    }

    public List<Position> getPiecesWithValidMoves(PieceColor color) {
        List<Position> positions = new ArrayList<>();
        var mustJump = hasJumpsAvailable(color);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var square = getSquare(row, col);
                if (!square.isEmpty() && square.piece().color() == color) {
                    if (mustJump) {
                        if (!getValidJumps(row, col, square.piece(), color).isEmpty()) {
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
        if (piece.isKing()) {
            return new int[]{-1, 1};
        }
        if (piece.color() == PieceColor.WHITE) {
            return new int[]{-1};
        } else {
            return new int[]{1};
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

    private boolean isValidJump(int toRow, int toCol, int captureRow, int captureCol, Piece piece) {
        if (!isValidPosition(toRow, toCol) || !isValidPosition(captureRow, captureCol)) {
            return false;
        }

        var targetSquare = getSquare(toRow, toCol);
        var captureSquare = getSquare(captureRow, captureCol);

        return targetSquare.isEmpty() && !captureSquare.isEmpty() && captureSquare.piece().color() != piece.color();
    }

    public boolean hasJumpsAvailable(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var square = getSquare(row, col);
                if (!square.isEmpty() && square.piece().color() == color && !getValidJumps(row, col, square.piece(), color).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldPromote(Piece piece, int row) {
        if (piece.isKing()) {
            return false;
        }
        return (piece.color() == PieceColor.WHITE && row == 0) || (piece.color() == PieceColor.BLACK && row == 7);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean hasMovesAvailable(PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var square = getSquare(row, col);
                if (!square.isEmpty() && square.piece().color() == color) {
                    if (!getValidMoves(row, col, color).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}