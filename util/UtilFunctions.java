package util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import org.jline.terminal.*;
import tetrominoes.*;

public class UtilFunctions {

  // Color print
  public static void printColored(PieceProps color) {
    String ansi = String.format("\u001b[38;2;%d;%d;%dm", color.getR(), color.getG(), color.getB());
    String reset = "\u001b[0m";
    System.out.print(ansi + "██" + reset);
  }

  // Cursor movement
  public static void moveCursorTo(int x, int y) {
    System.out.printf("\u001b[%d;%dH", y, x);
  }

  // Logo print
  public static void printLogo(int x, int y) {
    String[] logo = {
        "     ██╗███████╗████████╗██████╗ ██╗███████╗",
        "     ██║██╔════╝╚══██╔══╝██╔══██╗██║██╔════╝",
        "     ██║█████╗     ██║   ██████╔╝██║███████╗",
        "██   ██║██╔══╝     ██║   ██╔══██╗██║╚════██║",
        "╚█████╔╝███████╗   ██║   ██║  ██║██║███████║",
        " ╚════╝ ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚══════╝",
    };

    for (int i = 0; i < logo.length; i++) {
      moveCursorTo(x - 12, y - 8 + i);
      System.out.print(logo[i]);
    }
  }

  // End game
  public static void endGame(Terminal terminal) throws Exception {
    System.out.print("\033[2J\033[H");
    System.out.print("\033[?25h");
    System.out.flush();
    terminal.close();
    System.exit(0);
  }

  // Theme music
  private static Clip soundtrack;
  private static long clipTime = 0;

  public static void loadTheme() {
    try {
      if (soundtrack == null) {
        File file = new File("sounds/soundtrack.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        soundtrack = AudioSystem.getClip();
        soundtrack.open(audioStream);
      }
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public static void playThemeLoop() {
    if (soundtrack != null) {
      soundtrack.setMicrosecondPosition(0);
      soundtrack.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public static void pauseTheme() {
    if (soundtrack != null && soundtrack.isRunning()) {
      clipTime = soundtrack.getMicrosecondPosition();
      soundtrack.stop();
    }
  }

  public static void resumeTheme() {
    if (soundtrack != null) {
      soundtrack.setMicrosecondPosition(clipTime);
      soundtrack.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public static void stopTheme() {
    if (soundtrack != null) {
      soundtrack.stop();
      soundtrack.setMicrosecondPosition(0);
    }
  }

  // Line break sound
  public static void playLineBreak(int rows) {
    String fileName = rows == 4 ? "four-line-clear" : "line-clear";

    pauseTheme();

    try {
      File file = new File("sounds/" + fileName + ".wav");
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);
      clip.start();

      clip.addLineListener(event -> {
        if (event.getType() == LineEvent.Type.STOP) {
          clip.close();
          resumeTheme();
        }
      });

    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
      resumeTheme();
    }
  }
}
