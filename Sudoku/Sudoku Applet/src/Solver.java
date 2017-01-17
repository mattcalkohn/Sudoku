/**
 *  Solver class handles the solving of potential
 *     Sudoku boards and access of solved boards
 */

// Necessary imports
import java.util.*;

public class Solver {

    // Instance variables
    private Set<Integer>[][] options;     // Matrix of valid number sets for each spot on the board
    private int[][] board;                // Matrix of numbers on the board
    private Set<Integer>[][] tempOptions; // Temporary options matrix for solving by recursion
    private boolean solved;               // Indicator variable for whether or not board is solved

    // Solver constructor
    public Solver(int[][] b) {
        board = b;
        options = new TreeSet[9][9];
        tempOptions = new TreeSet[9][9];
        initializeOptions();
        solved = false;
    }

    // Standard accessor methods
    public int[][] getBoard() { return board; }

    // Initializes the options matrix
    public void initializeOptions() {
        TreeSet<Integer> initial = new TreeSet<Integer>();
        for (int i = 1; i <= 9; i++)
            initial.add(i);

        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board[0].length; c++) {
                options[r][c] = new TreeSet<Integer>();
                tempOptions[r][c] = new TreeSet<Integer>();
                if (board[r][c] == 0)
                    options[r][c].addAll(initial);
                else
                    options[r][c].add(board[r][c]);
            }
    }

    // Prints the current board from the options, for testing
    public void printBoard(Set<Integer>[][] opt) {
        for (int r = 0; r < opt.length; r++) {
            for (int c = 0; c < opt[0].length; c++)
                if (opt[r][c].size() == 1)
                    System.out.print(opt[r][c].toArray()[0] + " ");
                else
                    System.out.print("0 ");
            System.out.println();
        }
        System.out.println();
    }

    // Solves the current board and returns it
    public int[][] solve() {
        if (isSolved()) return board;
        solveSimple();
        if (isSolved()) return board;
        solveRecursion();
        return board;
    }

    // Indicates whether or not the board is solved
    public boolean isSolved() {
        return isSolved(board, options);
    }

    // Checks to see whether or not the given board is solved
    public boolean isSolved(int[][] b, Set<Integer>[][] opt) {
        // checks if no 0's in board and all sets only contain one item
        for (int[] arr : b)
            for (int el : arr)
                if (el == 0)
                    return false;
        for (Set<Integer>[] arr : opt)
            for (Set<Integer> el : arr)
                if (el.size() != 1)
                    return false;
        return true;
    }

    // Attempts to solve board through elimination and deduction
    public void solveSimple() {
        int i = 0;
        while (!isSolved() && i < 20) {
            elimRows();
            elimCols();
            elimBlocks();
            i++;
        }
    }

    // Traverses entire board and eliminates entries from options
    //   matrix by checking rows for placed numbers
    public void elimRows() {
        for (int r = 0; r < board.length; r++) {
            Set<Integer> rem = getRow(r);
            for (int c = 0; c < board[0].length; c++) {
                if (options[r][c].size() != 1) {
                    options[r][c].removeAll(rem);
                    if (options[r][c].size() == 1) {
                        board[r][c] = (int) options[r][c].toArray()[0];
                        rem.add(board[r][c]);
                    }
                }
            }
        }
    }

    // Traverses entire board and eliminates entries from options
    //   matrix by checking columns for placed numbers
    public void elimCols() {
        for (int c = 0; c < board[0].length; c++) {
            Set<Integer> rem = getCol(c);
            for (int r = 0; r < board.length; r++) {
                if (options[r][c].size() != 1) {
                    options[r][c].removeAll(rem);
                    if (options[r][c].size() == 1) {
                        board[r][c] = (int) options[r][c].toArray()[0];
                        rem.add(board[r][c]);
                    }
                }
            }
        }
    }

    // Traverses entire board and eliminates entries from options
    //   matrix by checking set blocks for placed numbers
    public void elimBlocks() {
        for (int rowb = 0; rowb < 3; rowb++)
            for (int colb = 0; colb < 3; colb++) {
                Set<Integer> rem = getBlock(rowb, colb);
                for (int r = rowb * 3; r < rowb * 3 + 3; r++)
                    for (int c = colb * 3; c < colb * 3 + 3; c++)
                        if (options[r][c].size() != 1) {
                            options[r][c].removeAll(rem);
                            if (options[r][c].size() == 1) {
                                board[r][c] = (int) options[r][c].toArray()[0];
                                rem.add(board[r][c]);
                            }
                        }
            }
    }

    // Returns a set of all of the numbers within a single row
    public Set<Integer> getRow(int row) {
        Set<Integer> rem = new TreeSet<Integer>();
        for (int col = 0; col < board[0].length; col++)
            if (board[row][col] != 0)
                rem.add(board[row][col]);
        return rem;
    }

    // Returns a set of all of the numbers within a single column
    public Set<Integer> getCol(int col) {
        Set<Integer> rem = new TreeSet<Integer>();
        for (int row = 0; row < board.length; row++)
            if (board[row][col] != 0)
                rem.add(board[row][col]);
        return rem;
    }

    // Returns a set of all of the numbers within a single block
    public Set<Integer> getBlock(int row, int col) {
        Set<Integer> rem = new TreeSet<Integer>();
        for (int r = row * 3; r < row * 3 + 3; r++)
            for (int c = col * 3; c < col * 3 + 3; c++)
                if (board[r][c] != 0)
                    rem.add(board[r][c]);
        return rem;
    }

    // Attempts to solve the board using recursion and guessing
    public void solveRecursion() {
        for(int r = 0; r < board.length; r++)
            for(int c = 0; c < board[0].length; c++)
                tempOptions[r][c].clear();

        attemptSolve(0, 0, options);
    }

    // Recursively guesses and checks whether a possible number
    //   leads to a solvable sudoku board
    public void attemptSolve(int r, int c, Set<Integer>[][] opt) {
        Set<Integer>[][] newOpt = new TreeSet[9][9];
        Iterator<Integer> it;
        if (opt[r][c].size() != 1) {
            while (!opt[r][c].isEmpty() && !solved) {
                for (int rw = 0; rw < opt.length; rw++)
                    for (int cl = 0; cl < opt[0].length; cl++) {
                        tempOptions[rw][cl].clear();
                        it = opt[rw][cl].iterator();
                        while (it.hasNext())
                            tempOptions[rw][cl].add(it.next());
                    }
                int guess = (int) opt[r][c].toArray()[0];
                tempOptions[r][c].clear();
                tempOptions[r][c].add(guess);
                for (int i = 0; i < 5; i++) {
                    elimRows(opt);
                    elimCols(opt);
                    elimBlocks(opt);
                }
                if (isSolvable(tempOptions)) {
                    for (int rw = 0; rw < opt.length; rw++)
                        for (int cl = 0; cl < opt[0].length; cl++) {
                            newOpt[rw][cl] = new TreeSet<>();
                            it = tempOptions[rw][cl].iterator();
                            while (it.hasNext())
                                newOpt[rw][cl].add(it.next());
                        }
                    if (c == opt[0].length - 1)
                        attemptSolve(r + 1, 0, newOpt);
                    else
                        attemptSolve(r, c + 1, newOpt);

                    if (!solved) {
                        opt[r][c].remove(new Integer(guess));
                        if (opt[r][c].isEmpty())
                            return;
                    }
                } else {
                    opt[r][c].removeAll(tempOptions[r][c]);
                    if (opt[r][c].isEmpty())
                        return;
                }
            }
        } else if (r != opt.length - 1 || c != opt[0].length - 1) {
            if (c == opt[0].length - 1)
                attemptSolve(r + 1, 0, opt);
            else
                attemptSolve(r, c + 1, opt);
        }
        if (!solved && r == opt.length - 1 && c == opt[0].length - 1) {
            for (int rw = 0; rw < opt.length; rw++)
                for (int cl = 0; cl < opt[0].length; cl++) {
                    board[rw][cl] = (int) tempOptions[rw][cl].toArray()[0];
                    options[rw][cl].clear();
                    options[rw][cl].addAll(tempOptions[rw][cl]);
                }
            solved = true;
        }
    }

    // Prints out the possible options for a space in a list, for testing
    public static void toList(Set<Integer> lst) {
        Iterator<Integer> it = lst.iterator();
        while (it.hasNext())
            System.out.print(it.next() + " ");
        System.out.println();
    }

    // Indicates whether or not a board is solvable by checking
    //   if there are any spaces where there cannot be any number
    public boolean isSolvable(Set<Integer>[][] opt) {
        for (Set<Integer>[] rows : opt)
            for (Set<Integer> el : rows)
                if (el.isEmpty())
                    return false;
        return true;
    }

    // Traverses entire board and eliminates entries from options
    //   matrix by checking rows for placed numbers through given options board
    public void elimRows(Set<Integer>[][] opt) {
        for (int r = 0; r < tempOptions.length; r++) {
            for (int c = 0; c < tempOptions[0].length; c++) {
                Set<Integer> rem = getRow(r, c, tempOptions);
                if (tempOptions[r][c].size() > 0) {
                    tempOptions[r][c].removeAll(rem);
                    if (tempOptions[r][c].size() == 1) {
                        rem.addAll(tempOptions[r][c]);
                    }
                }
            }
        }
    }

    // Traverses entire board and eliminates entries from options
    //   matrix by checking columns for placed numbers through given options board
    public void elimCols(Set<Integer>[][] opt) {
        for (int c = 0; c < tempOptions[0].length; c++) {
            for (int r = 0; r < tempOptions.length; r++) {
                Set<Integer> rem = getCol(c, r, tempOptions);
                if (tempOptions[r][c].size() > 0) {
                    tempOptions[r][c].removeAll(rem);
                    if (tempOptions[r][c].size() == 1) {
                        rem.add((int) tempOptions[r][c].toArray()[0]);
                    }
                }
            }
        }
    }

    // Traverses entire board and eliminates entries from options
    //   matrix by checking blocks for placed numbers through given options board
    public void elimBlocks(Set<Integer>[][] opt) {
        for (int rowb = 0; rowb < 3; rowb++)
            for (int colb = 0; colb < 3; colb++) {
                for (int r = rowb * 3; r < rowb * 3 + 3; r++)
                    for (int c = colb * 3; c < colb * 3 + 3; c++)
                        if (tempOptions[r][c].size() > 0) {
                            Set<Integer> rem = getBlock(rowb, colb, r, c, tempOptions);
                            tempOptions[r][c].removeAll(rem);
                            if (tempOptions[r][c].size() == 1) {
                                rem.add((int) tempOptions[r][c].toArray()[0]);
                            }
                        }
            }
    }

    // Returns a set of all of the numbers within a single row for a given options matrix
    public Set<Integer> getRow(int row, int c, Set<Integer>[][] opt) {
        Set<Integer> rem = new TreeSet<Integer>();
        for (int col = 0; col < opt[0].length; col++)
            if (col != c)
                if (opt[row][col].size() == 1)
                    rem.addAll(opt[row][col]);
        return rem;
    }

    // Returns a set of all of the numbers within a single column for a given options matrix
    public Set<Integer> getCol(int col, int r, Set<Integer>[][] opt) {
        Set<Integer> rem = new TreeSet<Integer>();
        for (int row = 0; row < opt.length; row++)
            if (row != r)
                if (opt[row][col].size() == 1)
                    rem.addAll(opt[row][col]);
        return rem;
    }

    // Returns a set of all of the numbers within a single block for a given options matrix
    public Set<Integer> getBlock(int row, int col, int rw, int cl, Set<Integer>[][] opt) {
        Set<Integer> rem = new TreeSet<Integer>();
        for (int r = row * 3; r < row * 3 + 3; r++)
            for (int c = col * 3; c < col * 3 + 3; c++)
                if (r != rw && c != cl)
                    if (opt[r][c].size() == 1)
                        rem.addAll(opt[r][c]);
        return rem;
    }

}