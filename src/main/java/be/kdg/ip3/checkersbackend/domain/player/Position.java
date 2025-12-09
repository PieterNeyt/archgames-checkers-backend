package be.kdg.ip3.checkersbackend.domain.player;

import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;


@ValueObject
public record Position(int row, int col) {
}
