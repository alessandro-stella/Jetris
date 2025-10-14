import org.jline.terminal.*;
import util.*;

public class Jetris {

  static final int[] GRID_SIZE = { 10, 20 };

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

  public static void runGame(Terminal terminal, GameHandler game) throws Exception {
    UtilFunctions.stopGame = false;

    UtilFunctions.loadAudio();
    UtilFunctions.playThemeLoop();
    game.createNewPiece();

    Thread gravityThread = new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        if (UtilFunctions.stopGame) {
          Thread.currentThread().interrupt();
          break;
        }

        try {
          Thread.sleep(game.calculateFallingSpeed());
          game.movePieceDown();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }

    });
    gravityThread.start();

    long lastTime = 0;
    int lastKey = -1;
    long delay = 100;

    while (true) {
      if (UtilFunctions.stopGame) {
        gravityThread.interrupt();
        return;
      }

      int ch = terminal.reader().read(100);
      long now = System.currentTimeMillis();

      if (ch != -1) {
        if (ch != lastKey || (now - lastTime) >= delay) {

          switch (ch) {
            case 'q':
              gravityThread.interrupt();
              UtilFunctions.stopGame = true;
              return;

            case 27: // ESC
              int next1 = terminal.reader().read();

              if (next1 == -1) {
                gravityThread.interrupt();
                UtilFunctions.stopGame = true;
                return;
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

            case 'i':
              game.rotatePiece();
              break;

            case 'k':
              game.movePieceDown();
              break;

            case 'l':
              game.movePieceRight();
              break;

            case 'j':
              game.movePieceLeft();
              break;

            case 'c':
              game.movePieceLeft();
              break;

            case 32: // Space
              game.moveOnBottom();
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

  public static void main(String[] args) throws Exception {
    Terminal terminal = TerminalBuilder.builder().jna(true).system(true).build();

    terminal.enterRawMode();
    TerminalUtils.hideCursor();
    terminal.flush();

    initTerminalSize();

    while (true) {
      GameHandler game = new GameHandler(GRID_SIZE[0], GRID_SIZE[1]);

      runGame(terminal, game);

      if (UtilFunctions.stopGame) {
        UtilFunctions.gameRecap(terminal, game.score, game.level, game.linesDeleted);
      }

      if (!UtilFunctions.shouldRestart()) {
        UtilFunctions.closeGame(terminal);
      }
    }
  }
}
