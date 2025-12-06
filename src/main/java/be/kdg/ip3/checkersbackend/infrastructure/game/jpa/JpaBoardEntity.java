package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;


import be.kdg.ip3.checkersbackend.domain.board.Board;
import be.kdg.ip3.checkersbackend.domain.board.BoardId;
import be.kdg.ip3.checkersbackend.domain.board.Square;
import be.kdg.ip3.checkersbackend.domain.board.SquareColor;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "board", schema = "checkers")
public class JpaBoardEntity {

    @Id
    private UUID id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaSquareEntity> squares = new ArrayList<>();

    public JpaBoardEntity() {}

    public JpaBoardEntity(UUID id, List<JpaSquareEntity> squares) {
        this.id = id;
        this.squares = squares;
    }

    public static JpaBoardEntity fromDomain(Board board) {
        JpaBoardEntity entity = new JpaBoardEntity();
        entity.id = board.getBoardId().id();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquare(i, j);
                JpaSquareEntity squareEntity = JpaSquareEntity.fromDomain(square);
                squareEntity.setBoard(entity);
                entity.squares.add(squareEntity);
            }
        }

        return entity;
    }

    public Board toDomain() {
        Square[][] boardArray = new Square[8][8];

        // Init alle vakjes zonder pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                boardArray[r][c] =
                        new Square(
                                r,
                                c,
                                ((r + c) % 2 == 0)
                                        ? SquareColor.LIGHT_BROWN
                                        : SquareColor.DARK_BROWN
                        );
            }
        }

        // Pieces toevoegen vanuit database
        for (JpaSquareEntity squareEntity : squares) {
            Square square = squareEntity.toDomain();
            boardArray[square.getRow()][square.getCol()] = square;
        }

        return new Board(new BoardId(id), boardArray);
    }

}
