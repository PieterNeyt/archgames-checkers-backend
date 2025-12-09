package be.kdg.ip3.checkersbackend.domain.player;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ValueObject
public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final List<Position> capturedPositions;
    private final LocalDateTime timestamp;
    private final boolean isJump;
    private PieceColor playerColor;

    public Move(int fromRow, int fromCol, int toRow, int toCol, PieceColor playerColor) {
        this(fromRow, fromCol, toRow, toCol, new ArrayList<>(), playerColor);
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, List<Position> capturedPositions, PieceColor playerColor) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.capturedPositions = new ArrayList<>(capturedPositions);
        this.timestamp = LocalDateTime.now();
        this.isJump = !capturedPositions.isEmpty();
        this.playerColor = playerColor;
    }

    public void playedBy(PieceColor playerColor) {
        this.playerColor=playerColor;
    }
}