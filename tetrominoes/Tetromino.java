package tetrominoes;

public class Tetromino {
  public Colors color;
  public int positionX;
  public int positionY;
  public int currentRotation;
  public int[][] piecesCoords;

  public Tetromino(Colors color, int positionX, int positionY) {
    this.color = color;
    this.currentRotation = 0;
    this.positionX = positionX;
    this.positionY = positionY;
    this.piecesCoords = new int[4][2];

    calculatePiecesPosition(positionX, positionY, 0);
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
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];
      if (row >= 0 && row < gameState.length && col >= 0 && col < gameState[0].length) {
        gameState[row][col] = this.color.getPiece();
      }
    }
  }

  public void calculatePiecesPosition(int positionX, int positionY, int currentRotation) {}
}
