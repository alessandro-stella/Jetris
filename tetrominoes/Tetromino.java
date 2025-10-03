package tetrominoes;

public class Tetromino {
  private final int HEIGHT = 20, WIDTH = 10;

  public PieceProps pieceType;
  public int positionX;
  public int positionY;
  public int currentRotation = 0;
  public int[][] piecesCoords = new int[4][2];
  public boolean alreadyTouched = false;

  public Tetromino(PieceProps pieceType, int positionX, int positionY) {
    this.pieceType = pieceType;
    this.positionX = positionX;
    this.positionY = positionY;

    calculatePiecesPosition(positionX, positionY, this.currentRotation);
  }

  public void rotate() {
    this.currentRotation = (this.currentRotation + 1) % 4;
    calculatePiecesPosition(this.positionX, this.positionY, this.currentRotation);

    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (col < 0) {
        this.positionX += 1;
        calculatePiecesPosition(this.positionX, this.positionY, this.currentRotation);
        return;
      }

      if (col >= WIDTH) {
        this.positionX -= 1;
        calculatePiecesPosition(this.positionX, this.positionY, this.currentRotation);
        return;
      }
    }
  }

  public boolean moveDown(char[][] gameState) {
    int newY = this.positionY + 1;
    calculatePiecesPosition(this.positionX, newY, this.currentRotation);

    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (row >= HEIGHT || gameState[row][col] != '\u0000') {
        calculatePiecesPosition(this.positionX, this.positionY, this.currentRotation);
        return false;
      }
    }

    this.positionY = newY;
    return true;
  }

  public void moveRight() {
    updatePosition(this.positionX + 1, this.positionY);
  }

  public void moveLeft() {
    updatePosition(this.positionX - 1, this.positionY);
  }

  public void updatePosition(int x, int y) {
    calculatePiecesPosition(x, y, currentRotation);

    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (col < 0 || col >= WIDTH) {
        calculatePiecesPosition(this.positionX, this.positionY, currentRotation);
        return;
      }
    }

    this.positionX = x;
    this.positionY = y;
  }

  public void draw(char[][] gameState) {
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (row < 0 || row >= HEIGHT || col < 0 || col >= WIDTH) {
        continue;
      }

      gameState[row][col] = this.pieceType.getPiece();
    }
  }

  public void erase(char[][] gameState) {
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (row < 0 || row >= HEIGHT || col < 0 || col >= WIDTH) {
        continue;
      }

      gameState[row][col] = '\u0000';
    }
  }

  public void calculatePiecesPosition(int positionX, int positionY, int currentRotation) {}
}
