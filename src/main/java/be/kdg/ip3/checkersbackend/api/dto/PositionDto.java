package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.player.Position;

public record PositionDto(int row, int col) {
    public static PositionDto fromDomain(Position position) {
        return new PositionDto(position.row(), position.col());
    }
}