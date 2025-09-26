package tetrominoes;

public enum Colors {
  CYAN('I', 0, 255, 255),
  YELLOW('O', 255, 255, 0),
  MAGENTA('T', 128, 0, 128),
  GREEN('S', 0, 255, 0),
  RED('Z', 255, 0, 0),
  BLUE('J', 0, 0, 255),
  ORANGE('L', 255, 165, 0);

  private final char piece;
  private final int r, g, b;

  Colors(char piece, int r, int g, int b) {
    this.piece = piece;
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public char getPiece() {
    return piece;
  }

  public int getR() {
    return r;
  }

  public int getG() {
    return g;
  }

  public int getB() {
    return b;
  }

  public static boolean isValidPiece(char c) {
    for (Colors color : Colors.values()) {
      if (color.piece == c) {
        return true;
      }
    }
    return false;
  }

  public static Colors fromPiece(char c) {
    for (Colors color : Colors.values()) {
      if (color.piece == c) {
        return color;
      }
    }
    return null;
  }
}
