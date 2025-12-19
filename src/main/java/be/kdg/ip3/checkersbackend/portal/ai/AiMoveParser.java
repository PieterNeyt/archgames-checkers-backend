package be.kdg.ip3.checkersbackend.portal.ai;

import be.kdg.ip3.checkersbackend.domain.player.Position;

import java.util.ArrayList;
import java.util.List;

public final class AiMoveParser {

    private AiMoveParser() {}
    private static final int[][] BOARD_MAP = {
            {0,1}, {0,3}, {0,5}, {0,7}, // 1-4
            {1,0}, {1,2}, {1,4}, {1,6}, // 5-8
            {2,1}, {2,3}, {2,5}, {2,7}, // 9-12
            {3,0}, {3,2}, {3,4}, {3,6}, // 13-16
            {4,1}, {4,3}, {4,5}, {4,7}, // 17-20
            {5,0}, {5,2}, {5,4}, {5,6}, // 21-24
            {6,1}, {6,3}, {6,5}, {6,7}, // 25-28
            {7,0}, {7,2}, {7,4}, {7,6}  // 29-32
    };

    public static Position getBackendPosition(String aiPos) {
        int index = Integer.parseInt(aiPos) - 1;
        int aiRow = BOARD_MAP[index][0];
        int aiCol = BOARD_MAP[index][1];

        return new Position(7 - aiRow, 7 - aiCol);
    }

    public static String getAiNumber(int aiRow, int aiCol) {
        for (int i = 0; i < BOARD_MAP.length; i++) {
            if (BOARD_MAP[i][0] == aiRow && BOARD_MAP[i][1] == aiCol) {
                return String.valueOf(i + 1);
            }
        }
        return "";
    }
}
