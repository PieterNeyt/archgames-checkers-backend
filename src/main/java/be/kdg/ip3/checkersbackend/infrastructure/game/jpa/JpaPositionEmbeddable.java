package be.kdg.ip3.checkersbackend.infrastructure.game.jpa;

import be.kdg.ip3.checkersbackend.domain.player.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class JpaPositionEmbeddable {

    @Column(name = "captured_row", nullable = false)
    private int row;

    @Column(name = "captured_col", nullable = false)
    private int col;

    public JpaPositionEmbeddable() {}

    public JpaPositionEmbeddable(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public static JpaPositionEmbeddable fromDomain(Position position) {
        return new JpaPositionEmbeddable(position.row(), position.col());
    }

    public Position toDomain() {
        return new Position(row, col);
    }
}