package Classwork;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.accessibility.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
/**
 * Solves the N Queens problem
 *
 * @ Ryan Symons
 * @ 6/8/20
 */
public class QueensGraphics
{
    private static int[][] board;    
    private static JLayeredPane layeredPane;
    private static JLabel chessBoard;
    private static Image boardImage = null;
    private static JTextArea textArea;
    private static JFrame frame;
    private static JPanel panel;
    private static JFrame boardFrame;
    private static JPanel boardPanel;
    private static JLabel lastCreatedQueen;
    private static ArrayList<JLabel> queenList = new ArrayList<JLabel>();
    private static int boardSize = -1;
    private static int speed = -1;
    //slow = 0, fast = 1, ludicrous = 2
    public static void main(String[] args) {
        runSelectionPhase();

        runBoardPhase();

        runGame(boardSize);
    }
    
    /**
     * Precondition: n > 3 and n < 7 or n < 9 depending on users bluej stack size.
     * Postcondition: Creates an empty board in the 2d array.
     */
    public static void runGame(int n) {
        board = new int[n][n];
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                board[i][j] = 0;
            }
        }
        placeQueens(0, 0);
    }
    
    /**
     * Precondition: None.
     * Postcondition: Prints the 2d array board and its code.
     */
    public static void printBoard() {
        for(int i = 0; i < board.length; i++) {
            System.out.println(Arrays.toString(board[i]));
        }
        String boardCode = "";

        for(int j = 0; j < board[0].length; j++) {
            for(int i = 0; i < board.length; i++) {
                if(board[i][j] == 1) {
                    boardCode += (i + 1);
                    break;
                }
            }
        }

        System.out.println("[" + boardCode + "]");
        System.out.println("-------------------------------");
    }

    /**
     * Precondition: Row and column are valid coordinates for the board size.
     * Postcondition: Returns if the Queen is safe by checking vertically, horizontally, and diagonally for other queens.
     */
    public static boolean isSafe(int row, int column) {
        int length = board[0].length;

        // Check Horizontal
        for(int i = 0; i < length; i++) {
            if(board[row][i] == 1) {
                return false;
            }
        }

        // Check Vertical
        for(int i = 0; i < length; i++) {
            if(board[i][column] == 1) {
                return false;
            }
        }

        // Check Diagonals
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                // If two spaces are on the same diagonal, the difference in coordinates will be the same.
                if(Math.abs(j - column) == Math.abs(i - row)) {
                    if(board[i][j] == 1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Precondition: Column is a valid column for the board size.
     * Postcondition: Makes every space in that column a zero, removes all visual queens from that column, and returns the row that the queen was on.
     */
    public static int nullifyColumn(int column) {
        int rowOfPreviousQueen = -1;
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(j == column) {
                    if(board[i][j] == 1) {
                        rowOfPreviousQueen = i;
                        
                        for(int k = 0; k < queenList.size(); k++) {
                            if(queenList.get(k).getX() / 60 == j && queenList.get(k).getY() / 60 == i) { // Checks if the coordinates are the same.
                                layeredPane.remove(queenList.get(k));
                                layeredPane.repaint();
                                queenList.remove(k);
                                break;
                            }
                        }
                    }
                    board[i][j] = 0;
                }
                
            }
        }
        return rowOfPreviousQueen;
    }
    
    /**
     * Precondition: Row and column are valid coordinates for the board size.
     * Postcondition: All solutions are printed and shown on the GUI.
     */
    public static void placeQueens(int row, int column) {
        createQueen(row, column);
        sleep();
        
        if(isSafe(row, column)) {
            board[row][column] = 1;
            if(column != board[0].length - 1) { // If we aren't on the last column, then move to the next one.
                placeQueens(0, column + 1);
            } else {
                try{
                    Thread.sleep(2000); // Wait for 2 seconds to show a solution was found.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                printBoard();
                nullifyColumn(column); // Get rid of the final queen from the last solution.
                board[row][column] = 0;
                moveToNextRow(row, column); // Move down or back even though a solution was found, this is needed to keep finding solutions.
            }
        } else {
            layeredPane.remove(queenList.get(queenList.size() - 1));
            queenList.remove(queenList.size() - 1);
            layeredPane.repaint();
            moveToNextRow(row, column);
        }
    }
    
    /**
     * Precondition: Row and column are valid coordinates for the board size.
     * Postcondition: Moves the the next row and moves x columns if the last row is reached.
     */
    public static void moveToNextRow(int row, int column) {
        while(row == board[0].length - 1) { // If we are on the last row, that means a mistake was made previously.
            if(column == 0) { // If we are on the last row of the first column, that means we have tried every solution and it is over.
                return;
            }
            row = nullifyColumn(column - 1);
            column --;
        }
        placeQueens(row + 1, column); // Move down to the next row.
    }

    /**
     * Precondition: None.
     * Postcondition: Creates and shows the selection GUI. Waits for the user to make selections before the method ends. 
     * Changes the boardSize and speed variables depending on user choice.
     */
    public static void runSelectionPhase() {
        frame = new JFrame("Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JCheckBox board4x4CB = new JCheckBox("4 x 4");
        JCheckBox board5x5CB = new JCheckBox("5 x 5");
        JCheckBox board6x6CB = new JCheckBox("6 x 6");
        JCheckBox board7x7CB = new JCheckBox("7 x 7");
        JCheckBox board8x8CB = new JCheckBox("8 x 8");

        JCheckBox slowSpeedCB = new JCheckBox("Slow Speed");
        JCheckBox fastSpeedCB = new JCheckBox("Fast Speed");
        JCheckBox ludicrousSpeedCB = new JCheckBox("Ludicrous Speed!");

        panel = new JPanel();
        panel.setOpaque(true);

        panel.add(board4x4CB);
        panel.add(board5x5CB);
        panel.add(board6x6CB);
        panel.add(board7x7CB);
        panel.add(board8x8CB);

        panel.add(slowSpeedCB);
        panel.add(fastSpeedCB);
        panel.add(ludicrousSpeedCB);

        board4x4CB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        board5x5CB.setEnabled(false);
                        board6x6CB.setEnabled(false);
                        board7x7CB.setEnabled(false);
                        board8x8CB.setEnabled(false);
                        boardSize = 4;
                    } else {//checkbox has been deselected
                        board5x5CB.setEnabled(true);
                        board6x6CB.setEnabled(true);
                        board7x7CB.setEnabled(true);
                        board8x8CB.setEnabled(true);
                        boardSize = -1;
                    }
                }
            });

        board5x5CB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        board4x4CB.setEnabled(false);
                        board6x6CB.setEnabled(false);
                        board7x7CB.setEnabled(false);
                        board8x8CB.setEnabled(false);
                        boardSize = 5;
                    } else {//checkbox has been deselected
                        board4x4CB.setEnabled(true);
                        board6x6CB.setEnabled(true);
                        board7x7CB.setEnabled(true);
                        board8x8CB.setEnabled(true);
                        boardSize = -1;
                    }
                }
            });

        board6x6CB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        board5x5CB.setEnabled(false);
                        board4x4CB.setEnabled(false);
                        board7x7CB.setEnabled(false);
                        board8x8CB.setEnabled(false);
                        boardSize = 6;
                    } else {//checkbox has been deselected
                        board5x5CB.setEnabled(true);
                        board4x4CB.setEnabled(true);
                        board7x7CB.setEnabled(true);
                        board8x8CB.setEnabled(true);
                        boardSize = -1;
                    }
                }
            });

        board7x7CB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        board5x5CB.setEnabled(false);
                        board6x6CB.setEnabled(false);
                        board4x4CB.setEnabled(false);
                        board8x8CB.setEnabled(false);
                        boardSize = 7;
                    } else {//checkbox has been deselected
                        board5x5CB.setEnabled(true);
                        board6x6CB.setEnabled(true);
                        board4x4CB.setEnabled(true);
                        board8x8CB.setEnabled(true);
                        boardSize = -1;
                    }
                }
            });

        board8x8CB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        board5x5CB.setEnabled(false);
                        board6x6CB.setEnabled(false);
                        board7x7CB.setEnabled(false);
                        board4x4CB.setEnabled(false);
                        boardSize = 8;
                    } else {//checkbox has been deselected
                        board5x5CB.setEnabled(true);
                        board6x6CB.setEnabled(true);
                        board7x7CB.setEnabled(true);
                        board4x4CB.setEnabled(true);
                        boardSize = -1;
                    }
                }
            });

        slowSpeedCB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        fastSpeedCB.setEnabled(false);
                        ludicrousSpeedCB.setEnabled(false);
                        speed = 0;
                    } else {//checkbox has been deselected
                        fastSpeedCB.setEnabled(true);
                        ludicrousSpeedCB.setEnabled(true);
                        speed = -1;
                    }
                }
            });

        fastSpeedCB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        slowSpeedCB.setEnabled(false);
                        ludicrousSpeedCB.setEnabled(false);
                        speed = 1;
                    } else {//checkbox has been deselected
                        slowSpeedCB.setEnabled(true);
                        ludicrousSpeedCB.setEnabled(true);
                        speed = -1;
                    }
                }
            });

        ludicrousSpeedCB.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        fastSpeedCB.setEnabled(false);
                        slowSpeedCB.setEnabled(false);
                        speed = 2;
                    } else {//checkbox has been deselected
                        fastSpeedCB.setEnabled(true);
                        slowSpeedCB.setEnabled(true);
                        speed = -1;
                    }
                }
            });

        textArea = new JTextArea(); 
        textArea.setEditable(false);
        textArea.append("Select one board size and one speed.");

        panel.add(textArea);
        frame.setContentPane(panel);

        frame.pack();
        frame.setResizable(false);
        frame.setSize(250,150);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while(boardSize == -1 || speed == -1) { /* Wait for user to make selection */ }
    }
    
    /**
     * Precondition: Method runSelectionPhase was called before this method.
     * Postcondition: Creates and shows the board based on user selected options in the selection phase.
     */
    public static void runBoardPhase() {
        frame.setVisible(false);

        String imageName = "Board" + boardSize + "x" + boardSize + ".png";
        int boardPixelLength = boardSize * 60;

        boardFrame = new JFrame("Board");
        boardPanel = new JPanel();

        try{
            URL url = new URL("https://raw.githubusercontent.com/rsymons22/N-Queens-Problem/master/" + imageName);
            boardImage = ImageIO.read(url);
        }catch (IOException e) {
            e.printStackTrace();
        }

        ImageIcon chessBoardIcon = new ImageIcon(boardImage);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(boardPixelLength, boardPixelLength));

        chessBoard = new JLabel(chessBoardIcon);

        chessBoard.setBounds(0, 0, chessBoardIcon.getIconWidth(), chessBoardIcon.getIconHeight());

        layeredPane.add(chessBoard, new Integer(0), 0); 

        boardPanel.add(layeredPane);

        boardPanel.setOpaque(true);
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boardFrame.setContentPane(boardPanel);
        boardFrame.pack();
        boardFrame.setResizable(false);
        boardFrame.setSize(boardPixelLength, boardPixelLength + 35); 
        boardFrame.setLocationRelativeTo(null);
        boardFrame.setVisible(true);
    }
    
    /**
     * Precondition: Row and column are valid coordinates for the board size.
     * Postcondition: Creates a queen image on the specified coorindates and adds the label to queenList.
     */
    public static void createQueen(int row, int column) {
        Image queenImage = null;
        try{
            URL url = new URL("https://raw.githubusercontent.com/rsymons22/N-Queens-Problem/master/Queen.png");
            queenImage = ImageIO.read(url);
        }catch (IOException e) {
            e.printStackTrace();
        }
        ImageIcon queenIcon = new ImageIcon(queenImage);
        
        JLabel queenLabel = new JLabel(queenIcon);
        
        queenLabel.setBounds(column * 60, row * 60, queenIcon.getIconWidth(), queenIcon.getIconHeight());
        
        layeredPane.add(queenLabel, new Integer(0), 0);
        
        queenList.add(queenLabel);
    }
    
    /**
     * Precondition: Method runSelectionPhase was called before this method is used.
     * Postcondition: Stops the program from running for a specific amount of time related to the speed variable.
     */
    public static void sleep() {
        try{
            if(speed == 0) {
                Thread.sleep(750);
            } else if(speed == 1) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
