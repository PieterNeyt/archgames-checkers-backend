package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameState;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GameDto(
        UUID gameId,
        BoardDto board,
        PlayerDto playerWhite,
        PlayerDto playerBlack,
        GameState state,
        PieceColor currentPlayerColor,
        List<PositionDto> activePieces
) {
    public static GameDto fromDomain(Game game) {
        return new GameDto(
                game.getGameId().id(),
                BoardDto.fromDomain(game.getBoard()),
                PlayerDto.fromDomain(game.getPlayerWhite()),
                PlayerDto.fromDomain(game.getPlayerBlack()),
                game.getState(),
                game.getCurrentPlayerColor(),
                game.getPlayablePieces().stream()
                        .map(PositionDto::fromDomain)
                        .collect(Collectors.toList())
        );
    }
}