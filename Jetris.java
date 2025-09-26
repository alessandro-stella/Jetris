import util.UtilFunctions;

public class Jetris {
  static final int[] GRID_SIZE = {10, 20};
  static final String PRINT_CHAR = "██";

  static int TERMINAL_WIDTH;
  static int TERMINAL_HEIGHT;
  static int[] FIELD_TL = new int[2];
  static int[] FIELD_BR = new int[2];

  static void clearScreen() {
    System.out.print("\033[2J\033[H");
  }

  static void clearScreen(boolean avoidFilling) {
    System.out.print("\033[2J\033[H");
  }

  static void initTerminalSize() throws Exception {
    Process p =
        new ProcessBuilder("stty", "size").redirectInput(ProcessBuilder.Redirect.INHERIT).start();

    try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
      String line = br.readLine();
      if (line != null) {
        String[] parts = line.trim().split("\\s+");
        TERMINAL_HEIGHT = Integer.parseInt(parts[0]);
        TERMINAL_WIDTH = Integer.parseInt(parts[1]);

        FIELD_TL[0] = (TERMINAL_WIDTH - (GRID_SIZE[0] * 2)) / 2;
        FIELD_TL[1] = (TERMINAL_HEIGHT - GRID_SIZE[1] + 8) / 2;
        FIELD_BR[0] = FIELD_TL[0] + (GRID_SIZE[0] * 2);
        FIELD_BR[1] = FIELD_TL[1] + GRID_SIZE[1];
      }
    }
  }

  static void initField() {
    int startX = FIELD_TL[0] - 1;
    int startY = FIELD_TL[1] - 1;
    int endY = FIELD_BR[1];

    UtilFunctions.moveCursorTo(startX, startY);
    for (int i = 0; i < GRID_SIZE[0] + 2; i++) {
      System.out.print(PRINT_CHAR);
    }

    for (int i = startY + 1; i < endY; i++) {
      UtilFunctions.moveCursorTo(startX, i);
      System.out.print(PRINT_CHAR);

      UtilFunctions.moveCursorTo(startX + ((GRID_SIZE[0] + 1) * 2), i);
      System.out.print(PRINT_CHAR);
    }

    UtilFunctions.moveCursorTo(startX, endY);
    for (int i = 0; i < GRID_SIZE[0] + 2; i++) {
      System.out.print(PRINT_CHAR);
    }
  }

  public static void main(String[] args) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> clearScreen(true)));

    try {
      initTerminalSize();
    } catch (Exception e) {
      System.out.println("Unable to get screen size!");
      return;
    }

    GameHandler game = new GameHandler(GRID_SIZE[0], GRID_SIZE[1], FIELD_TL[0], FIELD_TL[1]);

    clearScreen();
    initField();
    UtilFunctions.printLogo(FIELD_TL[0], FIELD_TL[1]);
    UtilFunctions.playTheme();
    game.drawPiece();

    while (true) {
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
      }
    }
  }
}
