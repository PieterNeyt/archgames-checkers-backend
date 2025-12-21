package be.kdg.ip3.checkersbackend.api.dto.external;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.portal.ai.AiBoardMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record AiMoveRequest(

        String gameId,

        @JsonProperty("game_name")
        String gameName,

        String[][] board,

        @JsonProperty("current_player")
        String currentPlayer,

        @JsonProperty("ai_player")
        String aiPlayer,

        @JsonProperty("king_movement_mode")
        String kingMovementMode,

        String difficulty,

        @JsonProperty("previous_moves")
        List<String> previousMoves
) {
    public static AiMoveRequest fromDomain(Game game) {
        List<String> mappedMoves = AiBoardMapper.mapMovesToStrings(game.getMoves());

        return new AiMoveRequest(
                game.getGameId().id().toString(),
                "CHECKERS",
                AiBoardMapper.toAiBoard(game.getBoard()),
                game.getCurrentPlayerColor().toString().substring(0, 1),
                game.getAiPLayer().color().toString().substring(0, 1),
                "SINGLE",
                game.getAiDifficulty().name(),
                mappedMoves.isEmpty() ? null : mappedMoves
        );
    }
}