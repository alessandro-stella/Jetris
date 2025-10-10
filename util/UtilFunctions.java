package util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import org.jline.terminal.Terminal;

public class UtilFunctions {
  private static int TERMINAL_WIDTH;
  private static int[] GRID_SIZE = new int[2];
  private static int[] FIELD_TL = new int[2];
  private static int[] FIELD_BR = new int[2];

  private static Clip soundtrack, lineClear, fourLineClear, blockPlaced, levelUp, gameOver;
  private static float soundtrackVolume = 0.25f; // 0.0f - 1.0f

  final private static String vertLine = "┃";

  final public static String[] jetrisLogo = {
      "     ██╗███████╗████████╗██████╗ ██╗███████╗",
      "     ██║██╔════╝╚══██╔══╝██╔══██╗██║██╔════╝",
      "     ██║█████╗     ██║   ██████╔╝██║███████╗",
      "██   ██║██╔══╝     ██║   ██╔══██╗██║╚════██║",
      "╚█████╔╝███████╗   ██║   ██║  ██║██║███████║",
      " ╚════╝ ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚══════╝",
  };

  final static String[] controls = {
      "┏━━ Controls ━━┓",
      "┃              ┃",
      "┃ J/←: Left    ┃",
      "┃              ┃",
      "┃ L/→: Right   ┃",
      "┃              ┃",
      "┃ K/↓: Down    ┃",
      "┃              ┃",
      "┃ I/↑: Rotate  ┃",
      "┃              ┃",
      "┃ Space: Place ┃",
      "┃              ┃",
      "┗━━━━━━━━━━━━━━┛"
  };

  final public static String[] gameOverLogo = {
      " ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ██╗   ██╗███████╗██████╗ ",
      "██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ██╔═══██╗██║   ██║██╔════╝██╔══██╗",
      "██║  ███╗███████║██╔████╔██║█████╗      ██║   ██║██║   ██║█████╗  ██████╔╝",
      "██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ██║   ██║╚██╗ ██╔╝██╔══╝  ██╔══██╗",
      "╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ╚██████╔╝ ╚████╔╝ ███████╗██║  ██║",
      " ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝   ╚═══╝  ╚══════╝╚═╝  ╚═╝",
  };

  public static int[] getFieldTl() {
    return FIELD_TL;
  }

  // Print logo
  public static void printLogo(String[] logo, int offsetY) {
    int offsetX = (TERMINAL_WIDTH - logo[0].length()) / 2;

    for (int i = 0; i < logo.length; i++) {
      TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] - offsetY + i);
      System.out.print(logo[i]);
    }
  }

  // Controls
  public static void printControls() {
    for (int i = 0; i < controls.length; i++) {
      TerminalUtils.moveCursorTo(FIELD_BR[0] + 2, FIELD_TL[1] + 6 + i);
      System.out.print(controls[i]);
    }
  }

  public static void clearScreen() {
    System.out.print("\033[2J\033[H");
  }

  public static void setDimensions(int terminalWidth, int terminalHeight, int gridWidth, int gridHeight) {
    TERMINAL_WIDTH = terminalWidth;

    GRID_SIZE[0] = gridWidth;
    GRID_SIZE[1] = gridHeight;

    FIELD_TL[0] = (terminalWidth - (GRID_SIZE[0] * 2)) / 2;
    FIELD_TL[1] = (terminalHeight - GRID_SIZE[1] + 8) / 2;
    FIELD_BR[0] = FIELD_TL[0] + (GRID_SIZE[0] * 2);
    FIELD_BR[1] = FIELD_TL[1] + GRID_SIZE[1];
  }

  static String makeStatLine(int width, String label, String value) {
    StringBuilder line = new StringBuilder();
    line.append(vertLine).append(" ").append(label);
    int spaces = width - (label.length() + value.length() + 2);
    for (int i = 0; i < spaces; i++)
      line.append(" ");
    line.append(value).append(" ").append(vertLine);
    return line.toString();
  }

  // Game over
  public static void gameRecap(Terminal terminal, int score, int level, int lines) throws Exception {
    playGameOver();
    Thread.sleep(1500);

    System.out.print("\033[2J\033[H"); // pulisce il terminale
    System.out.print("\033[?25h"); // mostra il cursore
    System.out.flush();

    UtilFunctions.printLogo(gameOverLogo, 8);

    String[] borders = { "┏", "┓", "┗", "┛" };
    String vertLine = "┃";
    String horLine = "━";

    // Determina larghezza interna
    int innerWidth = Math.max(9, ("Score: " + score).length() + 2);
    if (innerWidth % 2 == 0)
      innerWidth++;

    int offsetX = (TERMINAL_WIDTH - innerWidth - 2) / 2;

    // Bottom line
    StringBuilder bottom = new StringBuilder();
    bottom.append(borders[2]);
    for (int i = 0; i < innerWidth; i++)
      bottom.append(horLine);
    bottom.append(borders[3]);

    // Empty line
    StringBuilder emptyLine = new StringBuilder();
    emptyLine.append(vertLine);
    for (int i = 0; i < innerWidth; i++)
      emptyLine.append(" ");
    emptyLine.append(vertLine);

    // Header ┏── Stats ──┓
    StringBuilder header = new StringBuilder();
    header.append(borders[0]);
    for (int i = 0; i < (innerWidth - 7) / 2; i++)
      header.append(horLine);
    header.append(" Stats ");
    for (int i = 0; i < (innerWidth - 7) / 2; i++)
      header.append(horLine);
    header.append(borders[1]);

    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1]);
    System.out.print(header);

    // Riga vuota sotto header
    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 1);
    System.out.print(emptyLine);

    // Funzione helper per le statistiche

    // Score
    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 2);
    System.out.print(makeStatLine(innerWidth, "Score:", String.valueOf(score)));

    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 3);
    System.out.print(emptyLine);

    // Level
    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 4);
    System.out.print(makeStatLine(innerWidth, "Level:", String.valueOf(level)));

    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 5);
    System.out.print(emptyLine);

    // Lines
    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 6);
    System.out.print(makeStatLine(innerWidth, "Lines:", String.valueOf(lines)));

    // Empty + bottom
    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 7);
    System.out.print(emptyLine);
    TerminalUtils.moveCursorTo(offsetX, FIELD_TL[1] + 8);
    System.out.print(bottom);

    // Input loop
    long lastTime = 0;
    int lastKey = -1;
    long delay = 100;

    while (true) {
      int ch = terminal.reader().read();
      long now = System.currentTimeMillis();

      if (ch != -1) {
        if (ch != lastKey || (now - lastTime) >= delay) {
          switch (ch) {
            case 'r':
              System.out.print("HOLA!");
              break;
            default:
              closeGame(terminal);
              return;
          }
        }
        lastKey = ch;
        lastTime = now;
      }
    }
  }

  public static void closeGame(Terminal terminal) throws Exception {
    terminal.close();
    System.exit(0);
  }

  // Audio management
  public static void loadAudio() {
    soundtrack = loadClip("sounds/soundtrack.wav");
    lineClear = loadClip("sounds/line-clear.wav");
    fourLineClear = loadClip("sounds/four-line-clear.wav");
    blockPlaced = loadClip("sounds/block-placed.wav");
    levelUp = loadClip("sounds/level-up.wav");
    gameOver = loadClip("sounds/game-over.wav");

    setClipVolume(soundtrack, soundtrackVolume);
  }

  private static Clip loadClip(String path) {
    try {
      File file = new File(path);
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);
      return clip;
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static void setClipVolume(Clip clip, float volume) {
    if (clip == null)
      return;
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    float dB = (float) (20.0 * Math.log10(volume <= 0.0 ? 0.0001 : volume));
    gainControl.setValue(dB);
  }

  public static void setSoundtrackVolume(float volumePercent) {
    soundtrackVolume = Math.min(Math.max(volumePercent, 0.0f), 1.0f);
    setClipVolume(soundtrack, soundtrackVolume);
  }

  public static void playThemeLoop() {
    if (soundtrack != null) {
      soundtrack.setMicrosecondPosition(0);
      soundtrack.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public static void stopTheme() {
    if (soundtrack != null) {
      soundtrack.stop();
    }
  }

  public static void playLineBreak(int rows) {
    Clip clipToPlay = (rows == 4) ? fourLineClear : lineClear;
    playClipOnce(clipToPlay);
  }

  public static void playBlockPlaced() {
    playClipOnce(blockPlaced);
  }

  public static void playLevelUp() {
    playClipOnce(levelUp);
  }

  public static void playGameOver() {
    stopTheme();
    playClipOnce(gameOver);
  }

  private static void playClipOnce(Clip clip) {
    if (clip == null)
      return;
    clip.stop();
    clip.setFramePosition(0);
    clip.start();
  }
}
