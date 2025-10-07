package util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import org.jline.terminal.Terminal;

public class UtilFunctions {
  private static int[] GRID_SIZE = new int[2];
  private static int[] FIELD_TL = new int[2];
  private static int[] FIELD_BR = new int[2];

  private static Clip soundtrack, lineClear, fourLineClear, blockPlaced, levelUp, gameOver;
  private static float soundtrackVolume = 0.25f; // 0.0f - 1.0f

  public static int[] getFieldTl() {
    return FIELD_TL;
  }

  // Logo
  public static void printLogo() {
    String[] logo = {
        "     ██╗███████╗████████╗██████╗ ██╗███████╗",
        "     ██║██╔════╝╚══██╔══╝██╔══██╗██║██╔════╝",
        "     ██║█████╗     ██║   ██████╔╝██║███████╗",
        "██   ██║██╔══╝     ██║   ██╔══██╗██║╚════██║",
        "╚█████╔╝███████╗   ██║   ██║  ██║██║███████║",
        " ╚════╝ ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚══════╝",
    };
    for (int i = 0; i < logo.length; i++) {
      TerminalUtils.moveCursorTo(FIELD_TL[0] - 12, FIELD_TL[1] - 8 + i);
      System.out.print(logo[i]);
    }
  }

  public static void clearScreen() {
    System.out.print("\033[2J\033[H");
  }

  public static void setDimensions(int terminalWidth, int terminalHeight, int gridWidth, int gridHeight) {
    GRID_SIZE[0] = gridWidth;
    GRID_SIZE[1] = gridHeight;

    FIELD_TL[0] = (terminalWidth - (GRID_SIZE[0] * 2)) / 2;
    FIELD_TL[1] = (terminalHeight - GRID_SIZE[1] + 8) / 2;
    FIELD_BR[0] = FIELD_TL[0] + (GRID_SIZE[0] * 2);
    FIELD_BR[1] = FIELD_TL[1] + GRID_SIZE[1];
  }

  public static void printScore(int level, int score, int lines, char nextPiece) {
    TerminalUtils.moveCursorTo(0, 0);
    System.out.println("Level: " + level + " - Score: " + score);

  }

  // End game
  public static void endGame(Terminal terminal) throws Exception {
    playGameOver();

    Thread.sleep(1500);

    System.out.print("\033[2J\033[H");
    System.out.print("\033[?25h");
    System.out.flush();
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
