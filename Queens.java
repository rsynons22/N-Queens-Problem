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
public class Queens
{
    private static int[][] board;   
    private static int numberOfSolutions;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n;
        System.out.println("What should n equal? Must be greater than 3 and less than 7 or 9 depending on your bluej stack size.");
        n = scanner.nextInt();
        runGame(n);
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
        System.out.println("Number of solutions: " + numberOfSolutions);
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
        numberOfSolutions++;
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
     * Postcondition: Makes every space in that column a zero and returns the row that the queen was on.
     */
    public static int nullifyColumn(int column) {
        int rowOfPreviousQueen = -1;
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if(j == column) {
                    if(board[i][j] == 1) {
                        rowOfPreviousQueen = i;
                    }
                    board[i][j] = 0;
                }

            }
        }
        return rowOfPreviousQueen;
    }

    /**
     * Precondition: Row and column are valid coordinates for the board size.
     * Postcondition: All solutions are printed
     */
    public static void placeQueens(int row, int column) {
        if(isSafe(row, column)) {
            board[row][column] = 1;
            if(column != board[0].length - 1) { // If we aren't on the last column, then move to the next one.
                placeQueens(0, column + 1);
            } else {
                printBoard();
                nullifyColumn(column); // Get rid of the final queen from the last solution.
                board[row][column] = 0;
                moveToNextRow(row, column); // Move down or back even though a solution was found, this is needed to keep finding solutions.
            }
        } else {
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

} 