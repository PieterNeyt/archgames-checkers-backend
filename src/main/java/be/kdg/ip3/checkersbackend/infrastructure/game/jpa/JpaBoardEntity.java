package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.board.Board;
import be.kdg.ip3.checkersbackend.domain.board.BoardId;
import be.kdg.ip3.checkersbackend.domain.board.Square;
import be.kdg.ip3.checkersbackend.domain.board.SquareColor;
import be.kdg.ip3.checkersbackend.domain.piece.Piece;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Table(name = "board", schema = "checkers")
public class JpaBoardEntity {

    @Id
    private UUID id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaSquareEntity> squares = new ArrayList<>();

    public JpaBoardEntity() {
    }

    public JpaBoardEntity(UUID id, List<JpaSquareEntity> squares) {
        this.id = id;
        this.squares = squares;
    }

    public static JpaBoardEntity fromDomain(Board board) {
        List<JpaSquareEntity> squareEntities = new ArrayList<>();
        JpaBoardEntity entity = new JpaBoardEntity(board.getBoardId().id(), squareEntities);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square s = board.getSquare(r, c);
                JpaSquareEntity sq = JpaSquareEntity.fromDomain(s);
                sq.setBoard(entity);
                squareEntities.add(sq);
            }
        }
        return entity;
    }

    public void updateFromDomain(Board domain) {

        Map<UUID, JpaPieceEntity> existingPieceById = new HashMap<>();
        Map<UUID, JpaSquareEntity> pieceOwnerSquare = new HashMap<>();
        Map<String, JpaSquareEntity> squareByPos = new HashMap<>();

        for (JpaSquareEntity sq : this.squares) {
            squareByPos.put(sq.getRow() + "-" + sq.getCol(), sq);

            if (sq.getPiece() != null) {
                existingPieceById.put(sq.getPiece().getId(), sq.getPiece());
                pieceOwnerSquare.put(sq.getPiece().getId(), sq);
            }
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Square domainSquare = domain.getSquare(r, c);
                JpaSquareEntity targetSq = squareByPos.get(r + "-" + c);

                if (targetSq == null) {
                    continue;
                }

                if (domainSquare.isEmpty()) {
                    if (targetSq.getPiece() != null) {
                        targetSq.setPiece(null);
                    }
                } else {
                    Piece dp = domainSquare.getPiece();
                    UUID pieceId = dp.getPieceId().id();

                    JpaPieceEntity managedPiece = existingPieceById.get(pieceId);

                    if (managedPiece != null) {
                        managedPiece.setType(dp.getType());

                        JpaSquareEntity oldOwner = pieceOwnerSquare.get(pieceId);
                        if (oldOwner != null && oldOwner != targetSq) {
                            oldOwner.setPiece(null);
                            pieceOwnerSquare.put(pieceId, targetSq);
                        }

                        targetSq.setPiece(managedPiece);
                    } else {
                        JpaPieceEntity newPiece = JpaPieceEntity.fromDomain(dp);
                        targetSq.setPiece(newPiece);

                        existingPieceById.put(pieceId, newPiece);
                        pieceOwnerSquare.put(pieceId, targetSq);
                    }
                }
            }
        }
    }

    public Board toDomain() {
        Square[][] arr = new Square[8][8];
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                arr[r][c] = new Square(r, c,
                        ((r + c) % 2 == 0) ? SquareColor.LIGHT_BROWN : SquareColor.DARK_BROWN);

        for (JpaSquareEntity s : squares) {
            Square sq = s.toDomain();
            arr[sq.getRow()][sq.getCol()] = sq;
        }

        return new Board(new BoardId(id), arr);
    }
}
