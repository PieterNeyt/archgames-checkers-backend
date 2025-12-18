package be.kdg.ip3.checkersbackend.portal.ai;

import java.util.ArrayList;
import java.util.List;

public final class AiMoveParser {

    private AiMoveParser() {}

    public static List<int[]> parseMoves(String executedMoves) {
        String[] parts = executedMoves.split("-");
        List<int[]> moves = new ArrayList<>();

        for (int i = 0; i < parts.length - 1; i++) {
            moves.add(new int[]{
                    Integer.parseInt(parts[i]),
                    Integer.parseInt(parts[i + 1])
            });
        }
        return moves;
    }
}
