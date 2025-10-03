package tetrominoes;

public class Tetromino {
  public PieceProps pieceType;
  public int positionX;
  public int positionY;
  public int currentRotation;
  public int[][] piecesCoords;

  public Tetromino(PieceProps pieceType, int positionX, int positionY) {
    this.pieceType = pieceType;
    this.currentRotation = 0;
    this.positionX = positionX;
    this.positionY = positionY;
    this.piecesCoords = new int[4][2];

    calculatePiecesPosition(positionX, positionY, this.currentRotation);
  }

  public void rotate() {
    this.currentRotation = (this.currentRotation + 1) % 4;
    calculatePiecesPosition(this.positionX, this.positionY, this.currentRotation);
  }

  public void moveDown() {
    updatePosition(this.positionX, this.positionY + 1);
  }

  public void updatePosition(int x, int y) {
    this.positionX = x;
    this.positionY = y;

    calculatePiecesPosition(x, y, currentRotation);
  }

  public void draw(char[][] gameState) {
    int HEIGHT = gameState.length;
    int WIDTH = gameState[0].length;

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
    int HEIGHT = gameState.length;
    int WIDTH = gameState[0].length;

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
