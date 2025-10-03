import tetrominoes.*;
import util.UtilFunctions;

public class GameHandler {
  public int score;
  public int sizeX;
  public int sizeY;
  public int topLeftX;
  public int topLeftY;
  public char[][] gameState;
  public char pieceInHold;
  public Tetromino currentPiece;
  public char[] nextPieces;

  private final TetrominoGenerator generator = new TetrominoGenerator();

  public GameHandler(int sizeX, int sizeY, int topLeftX, int topLeftY) {
    this.score = 0;
    this.topLeftX = topLeftX + 1;
    this.topLeftY = topLeftY;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.gameState = new char[sizeY][sizeX];
  }

  public void clearBoard() {
    for (int i = 0; i < sizeY; i++) {
      UtilFunctions.moveCursorTo(topLeftX, topLeftY + i);
      for (int j = 0; j < sizeX; j++) {
        System.out.print("  ");
      }
    }
  }

  public void clearState() {
    this.gameState = new char[this.sizeY][this.sizeX];
  }

  public void drawState() {
    for (int i = 0; i < sizeY; i++) {
      UtilFunctions.moveCursorTo(topLeftX, topLeftY + i);

      for (int j = 0; j < sizeX; j++) {
        if (PieceProps.isValidPiece(gameState[i][j])) {
          UtilFunctions.printColored(PieceProps.fromPiece(gameState[i][j]));
        } else {
          System.out.print("  ");
        }
      }
    }
  }

  public void createNewPiece() throws InterruptedException {
    this.currentPiece = generator.generate(5, 0);
    this.currentPiece.draw(gameState);
    this.drawState();
  }

  public void movePieceDown() {
    this.currentPiece.erase(gameState);
    this.currentPiece.moveDown();
    this.currentPiece.draw(gameState);
    this.drawState();
  }
}
