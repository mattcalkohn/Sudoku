/**
 * The actual Sudoku Board and game
 */

// Necessary imports
import java.util.*;

public class SudokuGame {

    // Instance variables
    private int[][] board = new int[9][9];    // Matrix containing current Sudoku board
    private int[][] solved = new int[9][9];   // Matrix containing solved Sudoku board
    private Solver solver;                    // Sudoku solver for checking a potential board

    // SudokuGame constructor
    public SudokuGame(){
        startNewGame("easy");
    }

    // Accessor methods
    public int[][] getBoard() { return board; }
    public int[][] getSolution() { return solved; }

    // Starts a new Sudoku game
    public void startNewGame(String difficulty){
        makeBoard();
        int n, row, col;
        if(difficulty.equals("easy"))
            n = 40;
        else
            n = 60;

        while(n > 0) {
            do {
                row = (int) (Math.random() * 9);
                col = (int) (Math.random() * 9);
            } while (board[row][col] == 0);

            board[row][col] = 0;
            n--;
        }
    }

    // Generates a new Sudoku board
    public void makeBoard() {

        int n, num = 0, row, col;
        boolean broken;

        do {
            broken = false;
            for (int r = 0; r < board.length; r++)
                for (int c = 0; c < board[0].length; c++)
                    board[r][c] = 0;
            n = 10;
            while (n > 0) {
                do {
                    row = (int)(Math.random() * 9);
                    col = (int)(Math.random() * 9);
                } while (board[row][col] != 0);


                Set<Integer> opt = valid(row,col);
                if(opt.size() == 0) {
                    broken = true;
                    n = 0;
                }
                else{
                    Iterator<Integer> it = opt.iterator();
                    for(int i = 0; i <= (int)(Math.random()*opt.size())-1;i++)
                        it.next();
                    num = it.next();
                }

                board[row][col] = num;
                n--;
            }
            if(!broken) {
                solver = new Solver(board);
                solver.solve();
                broken = !solver.isSolved();
            }
        } while(broken);

        board = solver.getBoard();
        for(int r = 0; r < board.length; r++)
            for(int c = 0; c < board[0].length; c++)
                solved[r][c] = board[r][c];
    }

    // Returns a set of all of the valid integers for a specific space
    public Set<Integer> valid(int r, int c){

        Set<Integer> set = new TreeSet<>(), res = new TreeSet<>();
        for(int col = 0; col < board[0].length; col++)
            if(board[r][col] != 0)
                set.add(board[r][col]);
        for(int row = 0; row < board[0].length; row++)
            if(board[row][c] != 0)
                set.add(board[row][c]);
        for(int row = (r/3)*3; row < (r/3)*3+3; row++)
            for(int col = (c/3)*3; col < (c/3)*3+3; col++)
                if(board[row][col] != 0)
                    set.add(board[row][col]);

        for(int i = 1; i <= 9; i++)
            res.add(i);
        res.removeAll(set);

        return res;
    }
}


