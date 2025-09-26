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

  public GameHandler(int sizeX, int sizeY, int topLeftX, int topLeftY) {
    this.score = 0;
    this.topLeftX = topLeftX + 1;
    this.topLeftY = topLeftY;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.gameState = new char[sizeY][sizeX];
  }

  public void drawState() {
    for (int i = 0; i < sizeY; i++) {
      for (int j = 0; j < sizeX; j++) {
        if (Colors.isValidPiece(gameState[i][j])) {
          UtilFunctions.moveCursorTo(topLeftX + j * 2, topLeftY + i);
          UtilFunctions.printColored(Colors.fromPiece(gameState[i][j]));
        }
      }
    }
  }

  public void drawPiece() {
    Tetromino sPiece = new SPiece(4, 6);
    sPiece.draw(this.gameState);

    Tetromino zPiece = new ZPiece(6, 6);
    zPiece.draw(this.gameState);

    Tetromino tPiece = new TPiece(2, 10);
    tPiece.draw(this.gameState);

    Tetromino iPiece = new IPiece(5, 10);
    iPiece.draw(this.gameState);

    Tetromino jPiece = new JPiece(3, 14);
    jPiece.draw(this.gameState);

    Tetromino lPiece = new LPiece(7, 14);
    lPiece.draw(this.gameState);

    Tetromino oPiece = new OPiece(5, 18);
    oPiece.draw(this.gameState);

    drawState();
  }
}
