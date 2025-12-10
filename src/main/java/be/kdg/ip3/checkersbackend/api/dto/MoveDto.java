package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.player.Move;

import java.util.List;

public record MoveDto(
        int fromRow,
        int fromCol,
        int toRow,
        int toCol,
        boolean isJump,
        List<PositionDto> capturedPositions
) {
    public static MoveDto fromDomain(Move move) {
        List<PositionDto> positions = move.capturedPositions().stream()
                .map(PositionDto::fromDomain)
                .toList();

        return new MoveDto(
                move.fromRow(),
                move.fromCol(),
                move.toRow(),
                move.toCol(),
                move.isJump(),
                positions
        );
    }
}