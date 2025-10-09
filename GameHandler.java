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
  public Tetromino currentPiece;
  public Tetromino nextPiece;

  private final TetrominoGenerator generator = new TetrominoGenerator();
  private boolean blockInput = false;
  private Terminal terminal;

  public GameHandler(int sizeX, int sizeY, Terminal terminal) {
    this.score = 0;
    this.level = 10;
    this.linesDeleted = 0;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.terminal = terminal;
    this.nextPiece = generator.generate();

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

        boolean top = i == 0;
        boolean bottom = i == heightWithBorder - 1;
        boolean left = j == 0;
        boolean right = j == widthWithBorder - 1;

        // --- BORDI ---
        if (top && left) {
          buffer.append("┏");
          continue;
        }
        if (top && right) {
          buffer.append("┓");
          continue;
        }
        if (bottom && left) {
          buffer.append("┗");
          continue;
        }
        if (bottom && right) {
          buffer.append("┛");
          continue;
        }
        if (top || bottom) {
          buffer.append("━━");
          continue;
        }
        if (left || right) {
          if (left) {

            buffer.append("┃");
          } else {
            buffer.append("┃");
          }
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
    printGameStats(topLeftX, topLeftY - 1);
    printNextPiece(this.nextPiece, topLeftX + widthWithBorder * 2 - 3, topLeftY - 1);
  }

  public void printNextPiece(Tetromino piece, int x, int y) {
    StringBuilder buffer;

    for (int i = 0; i < 6; i++) {
      TerminalUtils.moveCursorTo(x, y + i);
      buffer = new StringBuilder();

      for (int j = 0; j < 8; j++) {
        if (i == 5) {
          buffer.append("┗━━━━━━━━━━━━━━┛");
          j = 8;
          continue;
        }

        if (i == 0) {
          buffer.append("┏━━━━ Next ━━━━┓");
          j = 8;
          continue;
        }

        if (j == 0 || j == 7) {
          if (j == 7) {
            buffer.append("\u001b[0m");
          }

          buffer.append(j == 0 ? "┃ " : " ┃");
          continue;
        }

        if (i == 1 || i == 4) {
          buffer.append("  ");
          continue;
        }

        if (j == 1 || j == 6) {
          buffer.append("  ");
          continue;
        }

        if (piece == null) {
          buffer.append("  ");
          continue;
        }

        String ansi = String.format("\u001b[38;2;%d;%d;%dm", piece.pieceType.getR(),
            piece.pieceType.getG(), piece.pieceType.getB());
        buffer.append(ansi);
        char pieceType = piece.pieceType.getPiece();

        switch (pieceType) {
          case 'I':
            if (i == 3) {
              buffer.append("██");
            } else {
              buffer.append("  ");
            }
            break;

          case 'O':
            if (j != 2 && j != 5) {
              buffer.append("██");
            } else {
              buffer.append("  ");
            }
            break;

          case 'J':
            if (i == 2) {
              buffer.append(" ██     ");
            } else {
              buffer.append(" ██████ ");
            }
            break;

          case 'L':
            if (i == 2) {
              buffer.append("     ██ ");
            } else {
              buffer.append(" ██████ ");
            }
            break;

          case 'S':
            if (i == 2) {
              buffer.append("   ████ ");
            } else {
              buffer.append(" ████   ");
            }
            break;

          case 'T':
            if (i == 2) {
              buffer.append("   ██   ");
            } else {
              buffer.append(" ██████ ");
            }
            break;

          case 'Z':
            if (i == 2) {
              buffer.append(" ████   ");
            } else {
              buffer.append("   ████ ");
            }
            break;

          default:
            break;
        }

        if (pieceType != 'I' && pieceType != 'O') {
          j = 5;
        }

      }

      System.out.print(buffer);
    }
  }

  public void printGameStats(int x, int y) {
    int width = 17;
    printBlock(x - width - 3, y, "Score", width, this.score);
    printBlock(x - width - 3, y + 4, "Level", width, this.level);
    printBlock(x - width - 3, y + 8, "Lines", width, this.linesDeleted);
  }

  public void printBlock(int x, int y, String text, int totalWidth, int value) {
    // Top
    StringBuilder buffer = new StringBuilder();
    String horLine = "━", vertLine = "┃";
    int fillerChars = (totalWidth - 4 - text.length()) / 2;

    buffer.append("┏");

    for (int i = 0; i < fillerChars; i++) {
      buffer.append(horLine);
    }

    buffer.append(" " + text + " ");

    for (int i = 0; i < fillerChars; i++) {
      buffer.append(horLine);
    }

    buffer.append("┓");

    TerminalUtils.moveCursorTo(x, y);
    System.out.print(buffer);

    // Middle
    buffer = new StringBuilder();
    buffer.append(vertLine);

    int valueLength = String.valueOf(value).length();

    for (int i = 0; i < totalWidth - 2 - valueLength; i++) {
      buffer.append(" ");
    }

    buffer.append(value);
    buffer.append(vertLine);

    TerminalUtils.moveCursorTo(x, y + 1);
    System.out.print(buffer);

    // Bottom
    buffer = new StringBuilder();
    buffer.append("┗");

    for (int i = 0; i < totalWidth - 2; i++) {
      buffer.append(horLine);
    }

    buffer.append("┛");

    TerminalUtils.moveCursorTo(x, y + 2);
    System.out.print(buffer);
  }

  public void createNewPiece() throws InterruptedException {
    this.blockInput = true;
    this.currentPiece = this.nextPiece;
    this.nextPiece = generator.generate();

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

      this.currentPiece = null;

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

  public synchronized void moveOnBottom() {
    if (blockInput)
      return;

    if (!this.currentPiece.couldMoveDown(gameState)) {
      this.currentPiece.moveDown(gameState);
      return;
    }

    int[][] ghostCoords = this.currentPiece.getGhostPosition(gameState);

    this.currentPiece.erase(gameState);
    this.currentPiece.moveDown(gameState, ghostCoords[0][0]);
    this.currentPiece.draw(gameState);

    this.currentPiece = null;

    try {
      this.checkRowsToDelete();
      UtilFunctions.playBlockPlaced();
      this.createNewPiece();
    } catch (Exception e) {
      e.printStackTrace();
    }

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
    int newLevel = Math.min(linesDeleted / 10 + 1, 20);

    if (newLevel != this.level) {
      UtilFunctions.playLevelUp();
      this.level = newLevel;
    }

    int mult = 200 * (rowsToDelete - 1) + 100 + (rowsToDelete == 4 ? 100 : 0);
    this.score += mult * this.level;

    TerminalUtils.moveCursorTo(0, 150);
    System.out.print(this.score);
  }
}
