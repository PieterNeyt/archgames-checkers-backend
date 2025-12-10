package be.kdg.ip3.checkersbackend.domain.player;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;
import java.util.List;

@ValueObject
public record Move(
        int fromRow,
        int fromCol,
        int toRow,
        int toCol,
        List<Position> capturedPositions,
        LocalDateTime timestamp,
        boolean isJump,
        PieceColor playerColor
) {

    public Move(int fromRow, int fromCol, int toRow, int toCol, List<Position> capturedPositions, PieceColor playerColor) {
        this(fromRow, fromCol, toRow, toCol,
                capturedPositions == null ? List.of() : List.copyOf(capturedPositions),
                LocalDateTime.now(),
                capturedPositions != null && !capturedPositions.isEmpty(),
                playerColor);
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, PieceColor playerColor) {
        this(fromRow, fromCol, toRow, toCol, List.of(), playerColor);
    }
}