package be.kdg.ip3.checkersbackend.api.dto.external;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.portal.ai.AiBoardMapper;
import be.kdg.ip3.checkersbackend.portal.ai.AiMoveParser;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record AiMoveRequest(
        @JsonProperty("gameId")
        String gameId,

        @JsonProperty("game_name")
        String gameName,

        @JsonProperty("board")
        String[][] board,

        @JsonProperty("current_player")
        String currentPlayer,

        @JsonProperty("ai_player")
        String aiPlayer,

        @JsonProperty("king_movement_mode")
        String kingMovementMode,

        @JsonProperty("difficulty")
        String difficulty,

        @JsonProperty("previous_moves")
        List<String> previousMoves
) {
    public static AiMoveRequest fromDomain(Game game) {
        List<String> mappedMoves = mapMovesToStrings(game.getMoves());

        return new AiMoveRequest(
                game.getGameId().id().toString(),
                "CHECKERS",
                AiBoardMapper.toAiBoard(game.getBoard()),
                game.getCurrentPlayerColor().toString().substring(0, 1).toUpperCase(),
                game.getAiPLayer().color().toString().substring(0, 1).toUpperCase(),
                "SINGLE",
                "MEDIUM",
                mappedMoves.isEmpty() ? null : mappedMoves
        );
    }

    private static List<String> mapMovesToStrings(List<Move> moves) {
        if (moves == null || moves.isEmpty()) return List.of();

        return moves.stream()
                .map(m -> {
                    int aiFromRow = 7 - m.fromRow();
                    int aiFromCol = 7 - m.fromCol();
                    int aiToRow = 7 - m.toRow();
                    int aiToCol = 7 - m.toCol();

                    String fromNum = AiMoveParser.getAiNumber(aiFromRow, aiFromCol);
                    String toNum = AiMoveParser.getAiNumber(aiToRow, aiToCol);

                    return fromNum + "-" + toNum;
                })
                .toList();
    }
}