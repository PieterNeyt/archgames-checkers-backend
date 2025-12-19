package be.kdg.ip3.checkersbackend.api.dto.external;

import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Move;

public record AiMoveResponse(
        String game_name,
        String game_status,
        String executed_moves
) {
}