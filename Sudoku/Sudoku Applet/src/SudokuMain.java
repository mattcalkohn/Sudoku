/**
 *  Sudoku applet class
 */

// Necessary imports
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class SudokuMain extends Applet implements KeyListener, MouseListener {

    // Instance variables
    private SudokuGame sG;          // SudokuGame for running the board
    private int[][] board;          // Matrix containing current Sudoku board
    private int[][] original;       // Matrix containing original Sudoku board
    private int myRow, myCol;       // Integers representing location of cursor
    private boolean selfSolve;      // Indicator variable for if the user solved the board themself or with the menu
    private RedirectingMenuItem easy, hard, solve;   // Menu items for difficulty and solving

    // Initializes applet and game
    public void init(){

        sG = new SudokuGame();
        board = sG.getBoard();
        original = new int[9][9];
        selfSolve = true;
        setOriginal();

        Object f = getParent ();
        while (! (f instanceof Frame))
            f = ((Component) f).getParent ();
        Frame frame = (Frame) f;

        MenuBar mb = new MenuBar();
        Menu ng, slv;
        mb.add(ng = new Menu("New Game"));
        ng.add(easy = new RedirectingMenuItem(this,"Easy Game"));
        ng.add(hard = new RedirectingMenuItem(this,"Hard Game"));

        mb.add(slv = new Menu("Help"));
        slv.add(solve = new RedirectingMenuItem(this, "Solve"));

        frame.setMenuBar(mb);
        addMouseListener(this);
        addKeyListener(this);
    }

    // Action method for receiving and handling menu interactions
    public boolean action(Event e, Object arg) {
        if (e.target == easy) {
            sG.startNewGame("easy");
            board = sG.getBoard();
            selfSolve = true;
            setOriginal();
        } else if (e.target == hard) {
            sG.startNewGame("hard");
            board = sG.getBoard();
            selfSolve = true;
            setOriginal();
        } else if (e.target == solve) {
            board = sG.getSolution();
            selfSolve = false;
            // lock the board
        } else if (e.target instanceof MenuItem)
            board[myRow][myCol] = Integer.parseInt(((MenuItem)e.target).getLabel());


        repaint();
        return super.action(e,arg);
    }

    // Paints the Sudoku board
    public void paint(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,400,400);
        g.setColor(Color.BLACK);
        for(int i = 1; i <= 2; i++){
            g.fillRect(0,133*i,400,5);
            g.fillRect(133*i,0,5,400);
        }
        for(int i = 1; i <= 8; i++){
            g.fillRect(0,400*i/9,400,2);
            g.fillRect(400*i/9,0,2,400);
        }

        g.setFont(new Font("Courier New",Font.BOLD,35));

        boolean complete = true;

        for(int r = 0; r < board.length; r++)
            for(int c = 0; c < board[0].length; c++)
                if(board[r][c] != 0){
                    if(original[r][c] == 0)
                        g.setColor(Color.RED);
                    else
                        g.setColor(Color.BLACK);
                    g.drawString(((Integer)board[r][c]).toString(),(400*c)/9+10,(400*(r+1))/9-10);
                } else
                    complete = false;

        if(complete && selfSolve){
            g.fillRect(27,117,346,136);
            g.setColor(Color.CYAN);
            g.fillRect(30,120,340,130);
            g.setColor(Color.BLACK);
            g.drawString("Congratulations!",32,150);
            g.setFont(new Font("Courier New",Font.BOLD,15));
            g.drawString("Press ENTER to start another game",45,230);
        }
    }

    // Sets the original board to the newly assigned board
    public void setOriginal(){
        for(int r = 0; r < board.length; r++)
            for(int c = 0; c < board[0].length; c++)
                original[r][c] = board[r][c];
    }

    // Indicates whether the game is finished or not
    public boolean isOver() {
        for(int r = 0; r < board.length; r++)
            for(int c = 0; c < board[0].length; c++)
                if(board[r][c] == 0)
                    return false;
        return true;
    }

    // Handles the keyboard events
    public void keyPressed(KeyEvent e){
        Set<Integer> keychain = getKeyChain(valid(myRow,myCol));
        if(!isOver()) {
            if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == 8) {
                if(original[myRow][myCol] == 0 && board[myRow][myCol] != 0)
                    board[myRow][myCol] = 0;
            } else if(keychain.contains(e.getKeyCode())) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_1:
                        board[myRow][myCol] = 1;
                        break;
                    case KeyEvent.VK_2:
                        board[myRow][myCol] = 2;
                        break;
                    case KeyEvent.VK_3:
                        board[myRow][myCol] = 3;
                        break;
                    case KeyEvent.VK_4:
                        board[myRow][myCol] = 4;
                        break;
                    case KeyEvent.VK_5:
                        board[myRow][myCol] = 5;
                        break;
                    case KeyEvent.VK_6:
                        board[myRow][myCol] = 6;
                        break;
                    case KeyEvent.VK_7:
                        board[myRow][myCol] = 7;
                        break;
                    case KeyEvent.VK_8:
                        board[myRow][myCol] = 8;
                        break;
                    case KeyEvent.VK_9:
                        board[myRow][myCol] = 9;
                        break;
                    default:
                        break;

                }
            }
        } else {
            //to start new game
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                sG.startNewGame("easy");
                board = sG.getBoard();
                selfSolve = true;
                setOriginal();
            }
        }
        repaint();
    }

    // Returns the possible keycodes for valid integers
    public Set<Integer> getKeyChain(Set<Integer> val) {
        Iterator<Integer> it = val.iterator();
        Set<Integer> keychain = new TreeSet<>();
        int num;
        while(it.hasNext()){
            num = it.next();
            switch (num) {
                case 1:
                    keychain.add(KeyEvent.VK_1);
                    break;
                case 2:
                    keychain.add(KeyEvent.VK_2);
                    break;
                case 3:
                    keychain.add(KeyEvent.VK_3);
                    break;
                case 4:
                    keychain.add(KeyEvent.VK_4);
                    break;
                case 5:
                    keychain.add(KeyEvent.VK_5);
                    break;
                case 6:
                    keychain.add(KeyEvent.VK_6);
                    break;
                case 7:
                    keychain.add(KeyEvent.VK_7);
                    break;
                case 8:
                    keychain.add(KeyEvent.VK_8);
                    break;
                case 9:
                    keychain.add(KeyEvent.VK_9);
                    break;
                default:
                    break;
            }
        }

        return keychain;
    }

    // Necessary KeyListener methods
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}

    // Handles the mouse events
    public void mouseClicked(MouseEvent e){

        PopupMenu popupMenu = new PopupMenu("Options");
        int x = e.getX(), y = e.getY(), r, c;
        c = x*9/400;
        r = y*9/400;

        //paint the box
        Graphics g = this.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(c*400/9,r*400/9,4,400/9);
        g.fillRect(c*400/9,r*400/9,400/9,4);
        g.fillRect((c+1)*400/9,r*400/9,4,400/9);
        g.fillRect(c*400/9,(r+1)*400/9,400/9,4);

        myRow = r;
        myCol = c;

        if(board[r][c] != 0) return;

        Set<Integer> options = valid(r,c);
        Iterator<Integer> it = options.iterator();

        while(it.hasNext())
            popupMenu.add(new MenuItem(it.next().toString()));

        add(popupMenu);

        popupMenu.show(e.getComponent(),e.getX(),e.getY());
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

    // Necessary MouseListener methods
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

}