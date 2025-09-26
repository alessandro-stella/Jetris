public class Main {
  static final int[] GRID_SIZE = {10, 20};
  static final String PRINT_CHAR = "██";

  static int TERMINAL_WIDTH;
  static int TERMINAL_HEIGHT;
  static int[] FIELD_BOTTOM_LEFT = new int[2];
  static int[] FIELD_TOP_RIGHT = new int[2];

  static void initTerminalSize() throws Exception {
    Process p =
        new ProcessBuilder("stty", "size").redirectInput(ProcessBuilder.Redirect.INHERIT).start();

    try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
      String line = br.readLine();
      if (line != null) {
        String[] parts = line.trim().split("\\s+");
        TERMINAL_HEIGHT = Integer.parseInt(parts[0]);
        TERMINAL_WIDTH = Integer.parseInt(parts[1]);

        FIELD_BOTTOM_LEFT[0] = (TERMINAL_WIDTH - (GRID_SIZE[0] * 2)) / 2;
        FIELD_TOP_RIGHT[0] = FIELD_BOTTOM_LEFT[0] + (GRID_SIZE[0] * 2);

        FIELD_BOTTOM_LEFT[1] = (TERMINAL_HEIGHT - (GRID_SIZE[1] * 2)) / 2;
        FIELD_TOP_RIGHT[1] = FIELD_BOTTOM_LEFT[1] + (GRID_SIZE[1] * 2);
      }
    }
  }

  static void moveCursorTo(int x, int y) {
    System.out.print(String.format("\u001b[%d;%dH", x, y));
  }

  static void clearScreen() {
    System.out.print("\033\143");

    for (int i = 0; i < TERMINAL_HEIGHT; i++) {
      for (int j = 0; j < TERMINAL_WIDTH; j++) {
        System.out.print(" ");
      }
      System.out.print("\n");
    }
  }

  static void clearScreen(boolean avoidFilling) {
    System.out.print("\033\143");
  }

  public static void main(String[] args) {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  clearScreen(true);
                }));

    try {
      initTerminalSize();
      clearScreen();
    } catch (Exception e) {
      System.out.println("Unable to get screen size!");
      return;
    }

    moveCursorTo(FIELD_BOTTOM_LEFT[1], FIELD_BOTTOM_LEFT[0]);
    System.out.print(PRINT_CHAR);

    moveCursorTo(FIELD_TOP_RIGHT[1], FIELD_TOP_RIGHT[0]);
    System.out.print(PRINT_CHAR);

    while (true) {
      try {
        Thread.sleep(1000); // evita CPU al 100%
      } catch (Exception e) {

      }
    }
  }
}
