import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import tetrominoes.*;
import util.UtilFunctions;

public class GameHandler {
  public int score;
  public int sizeX;
  public int sizeY;
  public Clip soundtrack;

  public int topLeftX;
  public int topLeftY;
  public char[][] gameState;
  public char pieceInHold;
  public Tetromino currentPiece;
  public char[] nextPieces;

  private final TetrominoGenerator generator = new TetrominoGenerator();
  private boolean blockInput = false;

  public GameHandler(int sizeX, int sizeY, int topLeftX, int topLeftY) {
    this.score = 0;
    this.sizeX = sizeX;
    this.sizeY = sizeY;

    this.topLeftX = topLeftX + 1;
    this.topLeftY = topLeftY;
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
    this.blockInput = true;
    this.currentPiece = generator.generate(5, 0);
    this.currentPiece.draw(gameState);
    this.drawState();
    this.blockInput = false;
  }

  public void rotatePiece() {
    if (blockInput)
      return;

    this.currentPiece.erase(gameState);
    this.currentPiece.rotate(gameState);
    this.currentPiece.draw(gameState);
    this.drawState();
  }

  public void movePieceDown() {
    if (blockInput)
      return;

    this.currentPiece.erase(gameState);

    boolean moved = this.currentPiece.moveDown(gameState);

    if (!moved) {
      this.currentPiece.draw(gameState);
      try {
        this.checkRowsToDelete();
        this.createNewPiece();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      this.currentPiece.draw(gameState);
    }

    this.drawState();
  }

  public void movePieceRight() {
    if (blockInput)
      return;

    this.currentPiece.erase(gameState);
    this.currentPiece.moveRight(gameState);
    this.currentPiece.draw(gameState);
    this.drawState();
  }

  public void movePieceLeft() {
    if (blockInput)
      return;

    this.currentPiece.erase(gameState);
    this.currentPiece.moveLeft(gameState);
    this.currentPiece.draw(gameState);
    this.drawState();
  }

  public void checkRowsToDelete() {
    this.blockInput = true;

    int[] rowIndexes = new int[sizeY];
    int rowsToDelete = 0;
    boolean fullEmpty = false;

    for (int i = (sizeY - 1); i >= 0; i--) {
      boolean delete = true;
      boolean empty = true;

      for (int j = 0; j < sizeX; j++) {
        if (gameState[i][j] == '\u0000') {
          delete = false;
          break;
        } else {
          empty = false;
        }
      }

      if (delete) {
        rowIndexes[rowsToDelete] = i;
        rowsToDelete++;
      }

      if (empty) {
        if (fullEmpty) {
          break;
        }

        fullEmpty = true;
      }
    }

    if (rowsToDelete == 0) {
      this.blockInput = false;
      return;
    }

    for (int i = 0; i < rowsToDelete; i++) {
      this.deleteRow(rowIndexes[i]);
    }

    UtilFunctions.playLineBreak(rowsToDelete);
    this.drawState();

    try {
      Thread.sleep(500);
    } catch (Exception e) {
    }

    this.removeEmptyRows(rowsToDelete, rowIndexes);
    this.drawState();

    this.blockInput = false;
  }

  public void deleteRow(int rowIndex) {
    for (int i = 0; i < sizeX; i++) {
      gameState[rowIndex][i] = '\u0000';
    }
  }

  public void removeEmptyRows(int rowsToDelete, int rowIndexes[]) {
    for (int i = rowsToDelete - 1; i >= 0; i--) { // dal basso verso l'alto
      int row = rowIndexes[i];

      // Sposta le righe sopra verso il basso
      for (int j = row; j > 0; j--) {
        System.arraycopy(gameState[j - 1], 0, gameState[j], 0, sizeX);
      }

      // Pulisci la prima riga
      for (int k = 0; k < sizeX; k++) {
        gameState[0][k] = '\u0000';
      }
    }
  }

}
