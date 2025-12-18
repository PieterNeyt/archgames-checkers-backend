package be.kdg.ip3.checkersbackend.api.dto.external;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.portal.ai.AiBoardMapper;

import java.util.UUID;

public record AiMoveRequest(
        UUID game_id,
        String game_name,
        String[][] board,
        String current_player,
        String ai_player

) {
    public static AiMoveRequest fromDomain(Game game) {

        return new AiMoveRequest(
                game.getGameId().id(),
                "CHECKERS",
                AiBoardMapper.toAiBoard(game.getBoard()),
                game.getCurrentPlayerColor().toString(),
                game.getAiPLayer().color().toString()
        );
    }

}