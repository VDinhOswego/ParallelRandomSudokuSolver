/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc37503;

import java.util.Random;

/**
 * Makes incomplete Sudoku puzzles of various blankness
 *
 * @author Vincent
 */
public class PuzzleMaker {

    private Integer[][] p;

    /**
     * Creates a complete puzzle by randomly placing an int from 1 to 9 that is
     * not currently in the row, column, and box. If this doesn't place an int
     * by 8 tries it goes back one spot and tries again. If the puzzle isn't
     * finished by 1000 tries then the whole puzzle is reset and the process is
     * repeated.
     */
    public PuzzleMaker() {
        int maxtries = 1000;
        boolean done = false;
        while (!done) {
            int timeout = 0;
            p = new Integer[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    boolean tf = false;
                    int count = 0;
                    while (!tf) {
                        count++;
                        int temp = new Random().nextInt(9) + 1;
                        for (int a = 0; a < 9; a++) {
                            if (p[i][a] != null && p[i][a] == temp && a != j) {
                                break;
                            }
                            if (p[a][j] != null && p[a][j] == temp && a != i) {
                                break;
                            }
                            if (a == 8) {
                                tf = true;
                            }
                        }
                        for (int a = (i / 3) * 3; a < ((i / 3) * 3) + 3; a++) {
                            for (int b = (j / 3) * 3; b < ((j / 3) * 3) + 3; b++) {
                                if (p[a][b] != null && p[a][b] == temp && a != i && b != j) {
                                    tf = false;
                                    break;
                                }
                            }
                        }
                        if (tf) {
                            p[i][j] = temp;
                        }
                        if (count >= 8) {
                            if (j == 0 && i > 0) {
                                i--;
                                j = 8;
                            } else if (j > 0) {
                                j--;
                            }
                            count = 0;
                        }
                        if (++timeout > maxtries) {
                            break;
                        }
                    }
                    if (timeout > maxtries) {
                        break;
                    }
                }
                if (timeout > maxtries) {
                    break;
                }
                if (i == 8) {
                    done = true;
                }
            }
        }
    }

    /**
     * Iterates thorough the puzzle and randomly sets every spot to null with a
     * multiple of 15% chance. Has a maximum cap on empty spaces at 42.
     *
     * @param d The multiplier of the chance
     */
    private void setDifficulty(int d) {
        double chance = .15 * d;
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (new Random().nextDouble() < chance) {
                    p[i][j] = null;
                    if (++count >= 42) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * Checks if each row, column, and box of a completed puzzle does not have
     * duplicates. Returns false if not correct.
     *
     * @param puz The puzzle to check
     * @return true if no duplicates
     */
    public boolean check(Integer[][] puz) {
        for (int i = 0; i < 9; i++) {
            boolean[] temp = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (temp[puz[i][j] - 1]) {
                    return false;
                } else {
                    temp[puz[i][j] - 1] = true;
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            boolean[] temp = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (temp[puz[j][i] - 1]) {
                    return false;
                } else {
                    temp[puz[j][i] - 1] = true;
                }
            }
        }
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                boolean[] temp = new boolean[9];
                for (int a = i; a < i + 3; a++) {
                    for (int b = j; b < j + 3; b++) {
                        if (temp[puz[a][b] - 1]) {
                            return false;
                        } else {
                            temp[puz[a][b] - 1] = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Prints out if the puzzle has a solution and empties a portion of the
     * puzzle and returns the incomplete puzzle
     *
     * @param d the difficulty
     * @return incomplete Sudoku puzzle
     */
    public Integer[][] getPuzzle(int d) {
        System.out.println("Solvable: " + check(p));
        setDifficulty(d);
        return p;
    }
}
