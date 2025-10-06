import javax.sound.sampled.Clip;

import org.jline.terminal.*;
import util.*;

public class Jetris {
  static final int[] GRID_SIZE = { 10, 20 };

  static int TERMINAL_WIDTH;
  static int TERMINAL_HEIGHT;
  static int[] FIELD_TL = new int[2];
  static int[] FIELD_BR = new int[2];

  static void initTerminalSize() throws Exception {
    Process p = new ProcessBuilder("stty", "size").redirectInput(ProcessBuilder.Redirect.INHERIT).start();
    try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
      String line = br.readLine();
      if (line != null) {
        String[] parts = line.trim().split("\\s+");
        UtilFunctions.setDimensions(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]), GRID_SIZE[0], GRID_SIZE[1]);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Terminal terminal = TerminalBuilder.builder().jna(true).system(true).build();

    terminal.enterRawMode();
    terminal.writer().print("\033[?25l");
    terminal.flush();

    initTerminalSize();

    GameHandler game = new GameHandler(GRID_SIZE[0], GRID_SIZE[1], terminal);

    UtilFunctions.loadAudio();
    UtilFunctions.playThemeLoop();

    game.createNewPiece();

    new Thread(
        () -> {
          while (true) {
            game.movePieceDown();

            try {
              Thread.sleep(game.calculateFallingSpeed());
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
