package be.kdg.ip3.checkersbackend.api.dto;

import be.kdg.ip3.checkersbackend.domain.game.Game;
import be.kdg.ip3.checkersbackend.domain.game.GameState;
import be.kdg.ip3.checkersbackend.domain.piece.PieceColor;
import be.kdg.ip3.checkersbackend.domain.player.Player;
import be.kdg.ip3.checkersbackend.domain.player.PlayerType;

import java.util.UUID;

public record GameDto(
        UUID gameId,
        BoardDto board,
        PlayerDto playerWhite,
        PlayerDto playerBlack,
        GameState state,
        PieceColor currentPlayerColor
) {
    public static GameDto fromDomain(Game game) {
        return new GameDto(
                game.getGameId().id(),
                BoardDto.fromDomain(game.getBoard()),
                PlayerDto.fromDomain(game.getPlayerWhite()),
                PlayerDto.fromDomain(game.getPlayerBlack()),
                game.getState(),
                game.getCurrentPlayerColor()
        );
    }
}