package tetrominoes;

public class TPiece extends Tetromino {
  public TPiece(int positionX, int positionY) {
    super(PieceProps.fromPiece('T'), positionX, positionY);
  }

  @Override
  public void calculatePiecesPosition(int positionX, int positionY, int currentRotation) {
    switch (currentRotation) {

      // --██--
      // ██████
      // ------
      case 0:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY, positionX + 1},
              {positionY, positionX - 1},
              {positionY - 1, positionX}
            };
        break;

      // --██--
      // --████
      // --██--
      case 1:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY, positionX + 1},
              {positionY - 1, positionX},
              {positionY + 1, positionX}
            };
        break;

      // ------
      // ██████
      // --██--
      case 2:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY, positionX + 1},
              {positionY, positionX - 1},
              {positionY + 1, positionX}
            };
        break;

      // --██--
      // ████--
      // --██--
      case 3:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY - 1, positionX},
              {positionY + 1, positionX},
              {positionY, positionX - 1}
            };
        break;

      default:
        break;
    }
  }
}
