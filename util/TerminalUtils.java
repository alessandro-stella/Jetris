package util;

import java.io.IOException;

public class TerminalUtils {
  private static String ttyConfig;

  // Terminal control
  public static void enableRawMode() throws IOException, InterruptedException {
    ttyConfig = execCommand("stty -g");
    execCommand("stty raw -echo");
  }

  public static void disableRawMode() throws IOException, InterruptedException {
    if (ttyConfig != null)
      execCommand("stty " + ttyConfig);
  }

  private static String execCommand(String cmd) throws IOException, InterruptedException {
    Process p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", cmd });
    p.waitFor();
    java.io.InputStream is = p.getInputStream();
    byte[] buf = new byte[is.available()];
    is.read(buf);
    return new String(buf).trim();
  }

  public static void hideCursor() {
    System.out.print("\033[?25l");
    System.out.flush();
  }

  public static void showCursor() {
    System.out.print("\033[?25h");
    System.out.flush();
  }

  public static void moveCursorTo(int x, int y) {
    System.out.printf("\u001b[%d;%dH", y, x);
  }

  public static int readChar() throws IOException {
    if (System.in.available() > 0) {
      return System.in.read();
    }
    return -1;
  }
}
