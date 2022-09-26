import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.*;

public class Test {

    public static void main(String[] args) {

        // if (!testCornerRemoval()) {
        // System.out.println("WRONG OUTPUT");
        // }
        if (!testCornerRemoval()) {
            System.out.println("WRONG OUTPUT");
            return;
        }
        if (!testSingleRemoval()) {
            System.out.println("WRONG OUTPUT");
            return;
        }
        if (!testDoubleRemoval()) {
            System.out.println("WRONG OUTPUT");
            return;
        }
        System.out.println("ALL TESTS PASSED");

    }

    public static boolean testCornerRemoval() {
        Go go = new Go();

        go.setColor(0, 0, "X");
        go.setColor(0, 1, "0");
        go.setColor(1, 0, "0");
        go.updateBoard(1, 0);

        if (go.isEmptyCell(0, 0)) {
            return true;
        }
        return false;

    }

    public static boolean testSingleRemoval() {

        Go go = new Go();

        go.setColor(3, 3, "0");
        go.setColor(4, 3, "X");
        go.setColor(5, 3, "0");
        go.setColor(4, 2, "0");
        go.setColor(4, 4, "0");

        go.updateBoard(4, 4);

        if (go.isEmptyCell(4, 3)) {
            return true;
        }
        return false;

    }

    public static boolean testDoubleRemoval() {
        Go go = new Go();

        go.setColor(0, 0, "X");
        go.setColor(0, 1, "X");
        go.setColor(1, 0, "0");
        go.setColor(1, 1, "0");
        go.setColor(0, 2, "0");
        go.updateBoard(0, 2);

        if (go.isEmptyCell(0, 0) && go.isEmptyCell(0, 1)) {
            return true;
        }
        return false;
    }
}
