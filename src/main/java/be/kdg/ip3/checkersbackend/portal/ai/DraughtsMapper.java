package be.kdg.ip3.checkersbackend.portal.ai;

import be.kdg.ip3.checkersbackend.domain.player.Position;

public final class DraughtsMapper {

    private DraughtsMapper() {}

    public static Position toBackendPosition(int number) {
        int index = number - 1;

        int rowFromTop = index / 4;
        int colInPlayableRow = index % 4;

        int col = (rowFromTop % 2 == 0)
                ? colInPlayableRow * 2 + 1
                : colInPlayableRow * 2;

        int backendRow = 7 - rowFromTop;

        return new Position(backendRow, col);
    }
}
