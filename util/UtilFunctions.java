package util;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import org.jline.terminal.*;
import tetrominoes.*;

public class UtilFunctions {
  public static void printColored(PieceProps color) {
    String ansi = String.format("\u001b[38;2;%d;%d;%dm", color.getR(), color.getG(), color.getB());
    String reset = "\u001b[0m";
    System.out.print(ansi + "██" + reset);
  }

  public static void playTheme() {
    try {
      File file = new File("soundtrack.wav");
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

      Clip clip = AudioSystem.getClip();
      clip.open(audioStream);
      clip.loop(Clip.LOOP_CONTINUOUSLY);

    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public static void moveCursorTo(int x, int y) {
    System.out.printf("\u001b[%d;%dH", y, x);
  }

  public static void printLogo(int x, int y) {
    String[] logo = {
      "████████╗███████╗████████╗██████╗ ██╗███████╗",
      "╚══██╔══╝██╔════╝╚══██╔══╝██╔══██╗██║██╔════╝",
      "   ██║   █████╗     ██║   ██████╔╝██║███████╗",
      "   ██║   ██╔══╝     ██║   ██╔══██╗██║╚════██║",
      "   ██║   ███████╗   ██║   ██║  ██║██║███████║",
      "   ╚═╝   ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝╚══════╝"
    };

    for (int i = 0; i < logo.length; i++) {
      moveCursorTo(x - 12, y - 8 + i);
      System.out.print(logo[i]);
    }
  }

  public static void endGame(Terminal terminal) throws Exception {
    System.out.print("\033[2J\033[H");
    System.out.print("\033[?25h");
    System.out.flush();
    terminal.close();
    System.exit(0);
  }
}
