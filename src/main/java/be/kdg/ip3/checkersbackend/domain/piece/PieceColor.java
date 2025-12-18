package be.kdg.ip3.checkersbackend.domain.piece;

public enum PieceColor {
    BLACK("B"),
    WHITE("W");

    private final String value;

    PieceColor(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
