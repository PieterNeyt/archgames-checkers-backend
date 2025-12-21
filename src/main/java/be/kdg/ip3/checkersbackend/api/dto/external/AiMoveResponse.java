package be.kdg.ip3.checkersbackend.api.dto.external;

public record AiMoveResponse(
        String game_name,
        String game_status,
        String executed_moves
) {
}