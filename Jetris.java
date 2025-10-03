import org.jline.terminal.*;
import util.*;

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

  static void drawBorder() {
    int startX = FIELD_TL[0] - 1;
    int startY = FIELD_TL[1] - 1;
    int endY = FIELD_BR[1];

    UtilFunctions.moveCursorTo(startX, startY);
    for (int i = 0; i < GRID_SIZE[0] + 2; i++) System.out.print(PRINT_CHAR);

    for (int i = startY + 1; i < endY; i++) {
      UtilFunctions.moveCursorTo(startX, i);
      System.out.print(PRINT_CHAR);
      UtilFunctions.moveCursorTo(startX + ((GRID_SIZE[0] + 1) * 2), i);
      System.out.print(PRINT_CHAR);
    }

    UtilFunctions.moveCursorTo(startX, endY);
    for (int i = 0; i < GRID_SIZE[0] + 2; i++) System.out.print(PRINT_CHAR);
  }

  public static void main(String[] args) throws Exception {
    Terminal terminal = TerminalBuilder.builder().jna(true).system(true).build();

    terminal.enterRawMode();
    terminal.writer().print("\033[?25l");
    terminal.flush();

    initTerminalSize();

    GameHandler game = new GameHandler(GRID_SIZE[0], GRID_SIZE[1], FIELD_TL[0], FIELD_TL[1]);

    clearScreen();
    drawBorder();
    UtilFunctions.printLogo(FIELD_TL[0], FIELD_TL[1]);
    UtilFunctions.playTheme();

    game.createNewPiece();

    new Thread(
            () -> {
              while (true) {
                game.movePieceDown();
                drawBorder();

                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  break;
                }
              }
            })
        .start();

    long lastTime = 0;
    int lastKey = -1;
    long delay = 100;

    while (true) {
      int ch = terminal.reader().read();
      long now = System.currentTimeMillis();

      if (ch != -1) {
        if (ch != lastKey || (now - lastTime) >= delay) {
          switch (ch) {
            case 'q':
              UtilFunctions.endGame(terminal);
              break;

            case 27: // ESC
              int next1 = terminal.reader().read();
              if (next1 == -1) {
                UtilFunctions.endGame(terminal);
              } else if (next1 == '[') {
                int next2 = terminal.reader().read();
                switch (next2) {
                  case 'A': // Arrow up
                    game.rotatePiece();
                    break;

                  case 'B': // Arrow down
                    game.movePieceDown();
                    break;

                  case 'C': // Arrow right
                    game.movePieceRight();
                    break;

                  case 'D': // Arrow left
                    game.movePieceLeft();
                    break;
                }
              }
              break;

            case 2:
              break;

            default:
              break;
          }

          lastKey = ch;
          lastTime = now;
        }
      }
      Thread.sleep(10);
    }
  }
}
