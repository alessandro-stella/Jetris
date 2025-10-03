package tetrominoes;

public class IPiece extends Tetromino {
  public IPiece(int positionX, int positionY) {
    super(PieceProps.fromPiece('I'), positionX, positionY);
  }

  @Override
  public void calculatePiecesPosition(int positionX, int positionY, int currentRotation) {
    switch (currentRotation) {

      // --------
      // ██[]████
      // --------
      // --------
      case 0:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY, positionX - 1},
              {positionY, positionX + 1},
              {positionY, positionX + 2}
            };
        break;

      // ----██--
      // ----[]--
      // ----██--
      // ----██--
      case 1:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY - 1, positionX},
              {positionY + 1, positionX},
              {positionY + 2, positionX}
            };
        break;

      // --------
      // --------
      // ████[]██
      // --------
      case 2:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY, positionX + 1},
              {positionY, positionX - 1},
              {positionY, positionX - 2}
            };
        break;

      // --██-..-
      // --██----
      // --[]----
      // --██----
      case 3:
        this.piecesCoords =
            new int[][] {
              {positionY, positionX},
              {positionY - 1, positionX},
              {positionY - 2, positionX},
              {positionY + 1, positionX}
            };
        break;

      default:
        break;
    }
  }
}
