package be.kdg.ip3.checkersbackend.api.dto.external;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameState;
import be.kdg.ip3.checkersbackend.domain.player.Move;
import be.kdg.ip3.checkersbackend.portal.ai.AiBoardMapper;
import be.kdg.ip3.checkersbackend.portal.ai.AiMoveParser;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
public record AiMoveRequest(
        @JsonProperty("gameId") // BELANGRIJK: map naar de juiste naam
        String gameId,
        String game_name,
        String[][] board,
        String current_player,
        String ai_player,
        String king_movement_mode,
        String difficulty,
        List<String> previous_moves,
        GameState game_status
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
                mappedMoves.isEmpty() ? null : mappedMoves,
                game.getState()
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