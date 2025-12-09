package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.player.Move;

import java.util.List;
import java.util.stream.Collectors;

public record MoveDto(
        int fromRow,
        int fromCol,
        int toRow,
        int toCol,
        boolean isJump,
        List<PositionDto> capturedPositions
) {
    public static MoveDto fromDomain(Move move) {
        List<PositionDto> positions = move.getCapturedPositions().stream()
                .map(PositionDto::fromDomain)
                .collect(Collectors.toList());

        return new MoveDto(
                move.getFromRow(),
                move.getFromCol(),
                move.getToRow(),
                move.getToCol(),
                move.isJump(),
                positions
        );
    }
}
