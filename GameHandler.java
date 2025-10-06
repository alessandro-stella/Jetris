import javax.sound.sampled.*;
import org.jline.terminal.*;
import java.util.*;
import tetrominoes.*;
import util.*;

public class GameHandler {
  public int score;
  public int level;
  public int linesDeleted;
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
  private Terminal terminal;

  public GameHandler(int sizeX, int sizeY, Terminal terminal) {
    this.score = 0;
    this.level = 1;
    this.linesDeleted = 0;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.terminal = terminal;

    int[] tlCoords = UtilFunctions.getFieldTl();
    this.topLeftX = tlCoords[0] + 1;
    this.topLeftY = tlCoords[1];
    this.gameState = new char[sizeY][sizeX];
  }

  public int calculateFallingSpeed() {
    double G;
    if (level < 20) {
      G = 1.0 / (20 - level);
    } else {
      G = 1.0;
    }
    double delay = 20 / G;
    return (int) Math.round(delay);
  }

  public void clearBoard() {
    for (int i = 0; i < sizeY; i++) {
      TerminalUtils.moveCursorTo(topLeftX, topLeftY + i);
      for (int j = 0; j < sizeX; j++) {
        System.out.print(" ");
      }
    }
  }

  public void clearState() {
    this.gameState = new char[this.sizeY][this.sizeX];
  }

  public void drawState() {
    UtilFunctions.clearScreen();
    UtilFunctions.printLogo();

    StringBuilder buffer = new StringBuilder();
    int widthWithBorder = sizeX + 2;
    int heightWithBorder = sizeY + 2;

    boolean couldMoveDown = this.currentPiece != null && this.currentPiece.couldMoveDown(gameState);

    int[][] ghostCoords = new int[0][0];
    if (couldMoveDown && currentPiece != null) {
      ghostCoords = currentPiece.getGhostPosition(gameState);
    }

    for (int i = 0; i < heightWithBorder; i++) {
      buffer.append("\033[").append(topLeftY - 1 + i).append(";").append(topLeftX - 2).append("H");
      for (int j = 0; j < widthWithBorder; j++) {

        if (i == 0 || i == heightWithBorder - 1) {
          buffer.append("██");
          continue;
        }

        if (j == 0 || j == widthWithBorder - 1) {
          buffer.append("██");
          continue;
        }

        int row = i - 1;
        int col = j - 1;
        char c = gameState[row][col];

        boolean isRealPiece = currentPiece != null && Arrays.stream(currentPiece.piecesCoords)
            .anyMatch(pc -> pc[0] == row && pc[1] == col);

        boolean isGhost = !isRealPiece && ghostCoords.length > 0 &&
            Arrays.stream(ghostCoords).anyMatch(gc -> gc[0] == row && gc[1] == col);

        if (isRealPiece) {
          PieceProps p = currentPiece.pieceType;
          String ansi = String.format("\u001b[38;2;%d;%d;%dm", p.getR(), p.getG(), p.getB());
          buffer.append(ansi).append("██").append("\u001b[0m");
        } else if (isGhost) {
          buffer.append("[]");
        } else if (PieceProps.isValidPiece(c)) {
          PieceProps p = PieceProps.fromPiece(c);
          String ansi = String.format("\u001b[38;2;%d;%d;%dm", p.getR(), p.getG(), p.getB());
          buffer.append(ansi).append("██").append("\u001b[0m");
        } else {
          buffer.append("  ");
        }
      }
    }

    System.out.print(buffer);
    UtilFunctions.printScore(this.level, this.score, this.linesDeleted);
  }

  public void createNewPiece() throws InterruptedException {
    this.blockInput = true;
    this.currentPiece = generator.generate(5, 0);
    boolean isValidPosition = this.currentPiece.draw(gameState);

    if (!isValidPosition) {
      try {
        UtilFunctions.endGame(this.terminal);
      } catch (Exception e) {
      }
    }

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

  public synchronized void movePieceDown() {
    if (blockInput)
      return;

    this.currentPiece.erase(gameState);
    boolean moved = this.currentPiece.moveDown(gameState);

    if (!moved) {
      this.currentPiece.draw(gameState);
      try {
        this.checkRowsToDelete();
        UtilFunctions.playBlockPlaced();
        this.createNewPiece();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      this.currentPiece.draw(gameState);
    }
    this.drawState();
  }

  public synchronized void movePieceRight() {
    if (blockInput)
      return;

    this.currentPiece.erase(gameState);
    this.currentPiece.moveRight(gameState);
    this.currentPiece.draw(gameState);
    this.drawState();
  }

  public synchronized void movePieceLeft() {
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

    for (int i = sizeY - 1; i >= 0; i--) {
      boolean full = true;
      boolean empty = true;
      for (int j = 0; j < sizeX; j++) {
        if (gameState[i][j] == '\u0000')
          full = false;
        else
          empty = false;
      }
      if (full)
        rowIndexes[rowsToDelete++] = i;
      if (empty)
        break;
    }

    if (rowsToDelete == 0) {
      this.blockInput = false;
      return;
    }

    for (int i = 0; i < rowsToDelete; i++)
      deleteRow(rowIndexes[i]);
    UtilFunctions.playLineBreak(rowsToDelete);
    this.drawState();

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    removeRows(rowIndexes, rowsToDelete);
    this.drawState();
    this.blockInput = false;
  }

  private void deleteRow(int rowIndex) {
    Arrays.fill(gameState[rowIndex], '\u0000');
  }

  private void removeRows(int[] rowIndexes, int rowsToDelete) {
    int targetRow = sizeY - 1;

    for (int i = sizeY - 1; i >= 0; i--) {
      boolean toDelete = false;
      for (int r = 0; r < rowsToDelete; r++) {
        if (i == rowIndexes[r]) {
          toDelete = true;
          break;
        }
      }

      if (!toDelete) {
        if (i != targetRow) {
          System.arraycopy(gameState[i], 0, gameState[targetRow], 0, sizeX);
        }
        targetRow--;
      }
    }

    for (int i = targetRow; i >= 0; i--)
      Arrays.fill(gameState[i], '\u0000');

    this.updateScore(rowsToDelete);
  }

  public void updateScore(int rowsToDelete) {
    this.linesDeleted += rowsToDelete;
    this.level = Math.min(linesDeleted / 10 + 1, 20);

    int mult = 200 * (rowsToDelete - 1) + 100 + (rowsToDelete == 4 ? 100 : 0);
    this.score += mult * this.level;

    TerminalUtils.moveCursorTo(0, 150);
    System.out.print(this.score);
  }
}
