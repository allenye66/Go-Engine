import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.*;

public class Go implements ActionListener {

    JFrame frame = new JFrame();

    JPanel grid = new JPanel();

    JButton[][] cells;

    JLabel textfield = new JLabel();

    JPanel titlePanel = new JPanel();

    boolean black_turn;

    int gridSize;

    ArrayList<List<Integer>> currGroup;

    String restoreColor;

    String white = "O";
    String black = "X";

    Go() {
        this(9);
    }

    Go(int gridSize) {
        this.gridSize = 9;
        cells = new JButton[gridSize][gridSize];

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBounds(0, 0, 800, 100);

        textfield.setHorizontalAlignment(JLabel.CENTER);

        grid.setLayout(new GridLayout(gridSize, gridSize));
        grid.setBackground(new Color(150, 150, 150));

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                cells[i][j] = new JButton();
                grid.add(cells[i][j]);
                cells[i][j].setFocusable(false);
                cells[i][j].addActionListener(this);
            }
        }
        titlePanel.add(textfield);
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(grid);

        gameStart();
    }

    public void gameStart() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        black_turn = true;
        textfield.setText("black starts");
    }

    public void setColor(int i, int j, String color) {
        cells[i][j].setText(color);
    }

    public void resetCell(int i, int j) {
        cells[i][j].setText("");
    }

    //check if we are at a black cell
    public boolean atBlack(int i, int j) {
        if (cells[i][j].getText() == "X") {
            return true;
        }
        return false;
    }


    //checks if a group is alive
    //first floodfill the group, gather all the cells part of the group
    //then, count the number of total empty spaces, if < 2, return false
    public boolean isAlive(int row, int col) {

        ArrayList<List<Integer>> floodfillGroup = fetchGroup(row, col);
        int totalAir = 0;

        for (List<Integer> coord : floodfillGroup) {
            totalAir += breathsOfAir(coord.get(0), coord.get(1));
        }

        if (totalAir == 0) {
            return false;
        }
        return true;
    }

    public String getOpponentColor(int row, int col) {
        if (atBlack(row, col)) {
            return white;
        }
        return black;
    }

    public boolean isEmptyCell(int row, int col) {
        return cells[row][col].getText() == "";
    }

    public int breathsOfAir(int row, int col) {

        int res = 0;

        //top
        if (row > 0 && isEmptyCell(row - 1, col)) {
            res += 1;
        }
        //left
        if (col > 0 && isEmptyCell(row, col - 1)) {
            res += 1;
        }
        //bottom
        if (row < this.gridSize - 1 && isEmptyCell(row + 1, col)) {
            res += 1;
        }
        //right
        if (col < this.gridSize - 1 && isEmptyCell(row, col + 1)) {
            res += 1;
        }

        return res;
    }

    public void floodfill(int row, int col, String target, Set<List<Integer>> visited) {
        if (row < 0 || col < 0 || row >= gridSize || col >= gridSize || cells[row][col].getText() != target || visited.contains(Arrays.asList(row, col))) {
            return;
        }
        currGroup.add(Arrays.asList(row, col));
        visited.add(Arrays.asList(row, col));
        for (List<Integer> neighbor : getSurrounding(row, col)) {
            if (target == black && atBlack(neighbor.get(0), neighbor.get(1))) {
                floodfill(neighbor.get(0), neighbor.get(1), target, visited);
            }
            if (target == white && !atBlack(neighbor.get(0), neighbor.get(1))) {
                floodfill(neighbor.get(0), neighbor.get(1), target, visited);
            }
        }

    }

    //floodfills a group to get the whole patch that contains the current cell
    public ArrayList<List<Integer>> fetchGroup(int row, int col) {
        currGroup = new ArrayList<List<Integer>>();
        String target;
        if (atBlack(row, col)) {
            target = black;
        } else {
            target = white;
        }

        Set<List<Integer>> tempSet = new HashSet<List<Integer>>();
        floodfill(row, col, target, tempSet);

        return currGroup;
    }

    public ArrayList<List<Integer>> getSurrounding(int row, int col) {
        ArrayList<List<Integer>> d = new ArrayList<List<Integer>>();
        if (row != 0) {
            d.add(Arrays.asList(-1 + row, col));
        }
        if (col != 0) {
            d.add(Arrays.asList(row, col - 1));
        }
        if (row != gridSize - 1) {
            d.add(Arrays.asList(1 + row, col));
        }
        if (col != gridSize - 1) {
            d.add(Arrays.asList(row, 1 + col));
        }

        return d;

    }

    //returns surrounding cells that are opponent cells
    public ArrayList<List<Integer>> toCheck(int row, int col) {

        ArrayList<List<Integer>> toCheck = new ArrayList<List<Integer>>();
        ArrayList<List<Integer>> directions = getSurrounding(row, col);

        for (List<Integer> direction : directions) {
            int newRow = direction.get(0);
            int newCol = direction.get(1);
            if (atBlack(row, col)) {
                if (cells[newRow][newCol].getText().equals(white)) {
                    toCheck.add(Arrays.asList(newRow, newCol));
                }
            } else {
                if (cells[newRow][newCol].getText().equals(black)) {
                    toCheck.add(Arrays.asList(newRow, newCol));
                }
            }
        }

        return toCheck;
    }

    public void updateBoard(int row, int col) {

        ArrayList<List<Integer>> toCheck = toCheck(row, col);

        for (List<Integer> pos : toCheck) {

            int r = pos.get(0);
            int c = pos.get(1);

            ArrayList<List<Integer>> floodfillGroup = fetchGroup(r, c);

            if (isAlive(r, c) == false) {
                restoreColor = getOpponentColor(r, c);
                for (List<Integer> toReset : floodfillGroup) {
                    resetCell(toReset.get(0), toReset.get(1));
                }
            }
        }

    }

    public ArrayList<List<Integer>> getToRestore(int row, int col) {

        ArrayList<List<Integer>> toCheck = toCheck(row, col);

        for (List<Integer> pos : toCheck) {
            int r = pos.get(0);
            int c = pos.get(1);

            ArrayList<List<Integer>> floodfillGroup = fetchGroup(r, c);

            if (isAlive(r, c) == false) {
                return floodfillGroup;
            }
        }
        return new ArrayList<List<Integer>>();

    }

    public void unUpdateBoard(ArrayList<List<Integer>> toRestore) {

        if (toRestore.size() > 0) {

            if (restoreColor == black) {

                while (toRestore.size() != 0) {
                    List<Integer> toRestoreIndex = toRestore.get(0);
                    setColor(toRestoreIndex.get(0), toRestoreIndex.get(1), white);
                    toRestore.remove(0);
                }

            } else {
                while (toRestore.size() != 0) {
                    List<Integer> toRestoreIndex = toRestore.get(0);
                    setColor(toRestoreIndex.get(0), toRestoreIndex.get(1), black);
                    toRestore.remove(0);
                }
            }
        }
        restoreColor = "";
    }


    //try placing the piece down
    //if the group dies, return false
    public boolean isValidPosition(int row, int col) {

        ArrayList<List<Integer>> placesToRestore = getToRestore(row, col);

        if (black_turn) {

            setColor(row, col, black);
            updateBoard(row, col);

            if (isAlive(row, col)) {

                unUpdateBoard(placesToRestore);
                resetCell(row, col);
                return true;

            } else {

                unUpdateBoard(placesToRestore);
                resetCell(row, col);
                return false;
            }
        } else {

            setColor(row, col, white);
            updateBoard(row, col);

            if (isAlive(row, col)) {

                unUpdateBoard(placesToRestore);
                resetCell(row, col);
                return true;

            } else {

                unUpdateBoard(placesToRestore);
                resetCell(row, col);
                return false;

            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells.length; j++) {
                if (e.getSource() == cells[i][j]) {
                    if (cells[i][j].getText() == "" && isValidPosition(i, j)) {


                        if (black_turn) {
                            setColor(i, j, black);
                            textfield.setText("white turn");
                        } else {
                            setColor(i, j, white);
                            textfield.setText("black turn");
                        }

                        black_turn = !black_turn;

                        updateBoard(i, j);

                    }
                }
            }
        }

    }

    public void printState() {
        for (int i = 0; i < this.gridSize; i++) {
            String row = "";
            for (int j = 0; j < this.gridSize; j++) {
                if (cells[i][j].getText() == "") {
                    row += " ";
                } else {
                    row += cells[i][j].getText();
                }
            }
            System.out.println(row);
        }
    }


}
