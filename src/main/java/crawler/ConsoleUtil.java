package crawler;

public class ConsoleUtil {
    // Colors
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    
    // Cursor
    private static final String ANSI_HIDE_CURSOR = "\u001B[?25l";
    private static final String ANSI_SHOW_CURSOR = "\u001B[?25h";
    private static final String ANSI_GOTO_COORD = "\u001B[%d;%dH";
    private static final String ANSI_CLEAR_SCREEN = "\033[H";
    private static final String ANSI_GOTO_ORIGIN = "\033[2J";

    public static void clearScreen() {
        System.out.print(ANSI_CLEAR_SCREEN);
        System.out.flush();
    }

    public static void gotoOrigin() {
        System.out.print(ANSI_GOTO_ORIGIN);
        System.out.flush();
    }

    public static void hideCursor() {
        System.out.print(ANSI_HIDE_CURSOR);
        System.out.flush();
    }

    public static void showCursor() {
        System.out.print(ANSI_SHOW_CURSOR);
        System.out.flush();
    }

    public static void gotoCoord(int x, int y) {
        System.out.printf(ANSI_GOTO_COORD, y, x);
        System.out.flush();
    }

    public static void update(String s) {
        System.out.print(ANSI_CLEAR_SCREEN);
        System.out.print(ANSI_GOTO_ORIGIN);
        System.out.print(s);
        System.out.flush();
    }

    public static void updateln(String s) {
        System.out.print(ANSI_CLEAR_SCREEN);
        System.out.print(ANSI_GOTO_ORIGIN);
        System.out.println(s);
        System.out.flush();
    }
}
