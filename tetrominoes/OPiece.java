package tetrominoes;

public class OPiece extends Tetromino {
  public OPiece(int positionX, int positionY) {
    super(PieceProps.fromPiece('O'), positionX, positionY);
  }

  @Override
  public void calculatePiecesPosition(int positionX, int positionY, int currentRotation) {
    // --[]██--
    // --████--
    // --------
    this.piecesCoords =
        new int[][] {
          {positionY, positionX},
          {positionY, positionX + 1},
          {positionY + 1, positionX},
          {positionY + 1, positionX + 1}
        };
    return;
  }
}
