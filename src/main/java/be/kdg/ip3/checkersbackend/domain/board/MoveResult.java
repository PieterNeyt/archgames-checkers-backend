package be.kdg.ip3.checkersbackend.domain.board;

import be.kdg.ip3.checkersbackend.domain.player.Move;

public record MoveResult(Board newBoard, Move executedMove) {}
