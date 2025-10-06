package tetrominoes;

import java.util.Arrays;

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
    if (isValidPosition(piecesCoords, gameState)) {
      currentRotation = nextRotation;
      return;
    }

    int[] kicks = { -1, 1, -2, 2 };
    for (int dx : kicks) {
      calculatePiecesPosition(positionX + dx, positionY, nextRotation);
      if (isValidPosition(piecesCoords, gameState)) {
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

    if (!isValidPosition(piecesCoords, gameState)) {
      calculatePiecesPosition(positionX, positionY, currentRotation);
      return false;
    }

    positionX = x;
    positionY = y;
    return true;
  }

  public boolean draw(char[][] gameState) {
    for (int i = 0; i < 4; i++) {
      int row = piecesCoords[i][0];
      int col = piecesCoords[i][1];

      if (row < 0 || row >= HEIGHT || col < 0 || col >= WIDTH)
        continue;

      if (gameState[row][col] != '\u0000') {
        return false;
      }

      gameState[row][col] = pieceType.getPiece();
    }

    return true;
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

  private boolean isValidPosition(int[][] coordsToCheck, char[][] gameState) {
    for (int i = 0; i < coordsToCheck.length; i++) {
      int row = coordsToCheck[i][0];
      int col = coordsToCheck[i][1];

      if (col < 0 || col >= WIDTH)
        return false;
      if (row >= HEIGHT)
        return false;
      if (row >= 0 && gameState[row][col] != '\u0000')
        return false;
    }
    return true;
  }

  private boolean hasOverlap(int[][] coords1, int[][] coords2) {
    for (int[] g : coords1) {
      for (int[] p : coords2) {
        if (Arrays.equals(g, p)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean couldMoveDown(char[][] gameState) {
    for (int i = 0; i < piecesCoords.length; i++) {
      int nextRow = piecesCoords[i][0] + 1;
      int col = piecesCoords[i][1];

      if (nextRow >= HEIGHT)
        return false;

      boolean isOwnCell = false;
      for (int[] coord : piecesCoords) {
        if (coord[0] == nextRow && coord[1] == col) {
          isOwnCell = true;
          break;
        }
      }
      if (isOwnCell)
        continue;

      if (gameState[nextRow][col] != '\u0000')
        return false;
    }
    return true;
  }

  public int[][] getGhostPosition(char[][] gameState) {
    int[][] ghostCoords = new int[piecesCoords.length][piecesCoords[0].length];

    for (int i = 0; i < piecesCoords.length; i++) {
      System.arraycopy(piecesCoords[i], 0, ghostCoords[i], 0, piecesCoords[i].length);
    }

    while (hasOverlap(ghostCoords, piecesCoords)) {
      for (int i = 0; i < ghostCoords.length; i++) {
        ghostCoords[i][0] += 1;
      }
    }

    while (isValidPosition(ghostCoords, gameState)) {
      for (int i = 0; i < ghostCoords.length; i++) {
        ghostCoords[i][0] += 1;
      }
    }

    for (int i = 0; i < ghostCoords.length; i++) {
      ghostCoords[i][0] -= 1;
    }

    return ghostCoords;
  }

  public void calculatePiecesPosition(int posX, int posY, int rotation) {
    // Overridden
  }
}
