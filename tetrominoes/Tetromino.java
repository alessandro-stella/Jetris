package tetrominoes;

public class Tetromino {
  private final int HEIGHT = 20, WIDTH = 10;

  public PieceProps pieceType;
  public int positionX;
  public int positionY;
  public int currentRotation = 0;
  public int[][] piecesCoords = new int[4][2];

  public Tetromino(PieceProps pieceType, int positionX, int positionY) {
    this.pieceType = pieceType;
    this.positionX = positionX;
    this.positionY = positionY;

    calculatePiecesPosition(positionX, positionY, currentRotation);
  }

  public void rotate(char[][] gameState) {
    int nextRotation = (currentRotation + 1) % 4;
    int[][] backup = new int[4][2];
    for (int i = 0; i < 4; i++) {
      backup[i][0] = piecesCoords[i][0];
      backup[i][1] = piecesCoords[i][1];
    }

    calculatePiecesPosition(positionX, positionY, nextRotation);
    if (isValidPositionAllowTop(gameState)) {
      currentRotation = nextRotation;
      return;
    }

    int[] kicks = { -1, 1, -2, 2 };
    for (int dx : kicks) {
      calculatePiecesPosition(positionX + dx, positionY, nextRotation);
      if (isValidPositionAllowTop(gameState)) {
        positionX += dx;
        currentRotation = nextRotation;
        return;
      }
    }

    for (int i = 0; i < 4; i++) {
      piecesCoords[i][0] = backup[i][0];
      piecesCoords[i][1] = backup[i][1];
    }
  }

  public boolean moveDown(char[][] gameState) {
    return updatePosition(positionX, positionY + 1, gameState);
  }

  public void moveRight(char[][] gameState) {
    updatePosition(positionX + 1, positionY, gameState);
  }

  public void moveLeft(char[][] gameState) {
    updatePosition(positionX - 1, positionY, gameState);
  }

  private boolean updatePosition(int x, int y, char[][] gameState) {
    calculatePiecesPosition(x, y, currentRotation);
    if (!isValidPositionAllowTop(gameState)) {
      calculatePiecesPosition(positionX, positionY, currentRotation);
      return false;
    }
    positionX = x;
    positionY = y;
    return true;
  }

  public void draw(char[][] gameState) {
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (row < 0 || row >= HEIGHT || col < 0 || col >= WIDTH)
        continue;

      gameState[row][col] = pieceType.getPiece();
    }
  }

  public void erase(char[][] gameState) {
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (row < 0 || row >= HEIGHT || col < 0 || col >= WIDTH)
        continue;

      gameState[row][col] = '\u0000';
    }
  }

  private boolean isValidPositionAllowTop(char[][] gameState) {
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (col < 0 || col >= WIDTH)
        return false;
      if (row >= HEIGHT)
        return false;
      if (row >= 0 && gameState[row][col] != '\u0000')
        return false;
    }
    return true;
  }

  public void calculatePiecesPosition(int posX, int posY, int rotation) {

  }
}
