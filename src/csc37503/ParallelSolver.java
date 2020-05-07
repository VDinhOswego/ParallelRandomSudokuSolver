/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc37503;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RecursiveTask;

/**
 * Solves the given puzzle by recursive calls with a given point with a given
 * value as the next move to make.
 *
 * @author Vincent
 */
public class ParallelSolver extends RecursiveTask<Integer[][]> {

    private final Integer[][] current;
    private final ArrayList<int[]> points;
    private final HashMap<int[], int[]> values;

    /**
     * Sets the current solution as the previous with the next move placed and
     * removes the point from the unsolved list and reevaluates the possible
     * values for each of the unsolved points.
     *
     * @param prev The solution before the given next move
     * @param p The unsolved points before the given next move
     * @param v The possible values for each unsolved for point
     * @param point The given next point
     * @param next The value to place in given point
     */
    public ParallelSolver(Integer[][] prev, ArrayList<int[]> p, HashMap<int[], int[]> v, int[] point, int next) {
        Integer[][] c = new Integer[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(prev[i], 0, c[i], 0, prev[i].length);
        }
        if (point != null) {
            c[point[0]][point[1]] = next;
        }
        current = c;
        points = new ArrayList<int[]>();
        values = new HashMap<int[], int[]>();
        if (isValid()) {
            points.addAll(p);
            if (point != null) {
                points.remove(point);
                if (!points.isEmpty()) {
                    while (true) {
                        values.putAll(new ParallelFinder(current, points.toArray(new int[points.size()][2])).compute());
                        boolean change = false;
                        ArrayList<int[]> removal = new ArrayList<int[]>();
                        for (int i = 0; i < points.size(); i++) {
                            if (values.get(points.get(i)).length == 1) {
                                int[] po = points.get(i);
                                current[po[0]][po[1]] = values.get(po)[0];
                                removal.add(po);
                                change = true;
                            }
                        }
                        points.removeAll(removal);
                        if (!change) {
                            break;
                        }
                    }
                }
            } else {
                values.putAll(v);
            }
        }
    }

    /**
     * Checks if each row, column, and box of a puzzle does not have duplicates.
     * Returns false if not correct.
     *
     * @return
     */
    public boolean isValid() {
        for (int i = 0; i < 9; i++) {
            boolean[] temp = new boolean[9];
            boolean[] temp1 = new boolean[9];
            for (int j = 0; j < 9; j++) {
                if (current[i][j] != null && temp[current[i][j] - 1]) {
                    return false;
                } else if (current[i][j] != null) {
                    temp[current[i][j] - 1] = true;
                }
                if (current[j][i] != null && temp1[current[j][i] - 1]) {
                    return false;
                } else if (current[j][i] != null) {
                    temp1[current[j][i] - 1] = true;
                }
            }
        }
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                boolean[] temp = new boolean[9];
                for (int a = i; a < i + 3; a++) {
                    for (int b = j; b < j + 3; b++) {
                        if (current[a][b] != null && temp[current[a][b] - 1]) {
                            return false;
                        } else if (current[a][b] != null) {
                            temp[current[a][b] - 1] = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the current solution is a valid layout first then if there are
     * no empty points left to solve for then it returns the solution if not it
     * checks if only one more point is left then solves for the value the point
     * should be if it is found then the solution is returned. Then it If there
     * are more unsolved points then a new ParallelSolver is created for every
     * possible value for the still empty points. Then the results of the method
     * calls are checked as they return. If a solution is found then it is
     * returned. If no solution is found then null is returned.
     *
     * @return A valid solution or null if no solution
     */
    @Override
    protected Integer[][] compute() {
        if (!isValid()) {
            return null;
        }
        if (points == null || points.isEmpty()) {
            return current;
        } else if (points.size() < 2) {
            for (int i = 0; i < values.get(points.get(0)).length; i++) {
                current[points.get(0)[0]][points.get(0)[1]] = values.get(points.get(0))[i];
                if (isValid()) {
                    return current;
                }
            }
        } else {
            for (int i = 0; i < points.size(); i++) {
                if (values.get(points.get(i)).length == 0) {
                    return null;
                }
            }
            ParallelSolver[][] children = new ParallelSolver[points.size()][9];
            for (int i = 0; i < points.size(); i++) {
                for (int j = 0; j < values.get(points.get(i)).length; j++) {
                    children[i][j] = new ParallelSolver(current, points, values, points.get(i), values.get(points.get(i))[j]);
                    if (!(i == points.size() - 1 && j == values.get(points.get(points.size() - 1)).length - 1)) {
                        children[i][j].fork();
                    }
                }
            }
            Integer[][] temp = children[points.size() - 1][values.get(points.get(points.size() - 1)).length - 1].compute();
            if (temp != null) {
                for (int i = 0; i < points.size(); i++) {
                    for (int j = 0; j < values.get(points.get(i)).length; j++) {
                        if (!(i == points.size() - 1 && j == values.get(points.get(points.size() - 1)).length - 1)) {
                            children[i][j].cancel(true);
                        }
                    }
                }
                return temp;
            }
            for (int i = 0; i < points.size(); i++) {
                for (int j = 0; j < values.get(points.get(i)).length; j++) {
                    if (!(i == points.size() - 1 && j == values.get(points.get(points.size() - 1)).length - 1)) {
                        temp = children[i][j].join();
                        if (temp != null) {
                            for (int a = i; a < points.size(); a++) {
                                for (int b = 0; b < values.get(points.get(i)).length; b++) {
                                    if (!(a == points.size() - 1 && b == values.get(points.get(points.size() - 1)).length - 1)) {
                                        children[a][b].cancel(true);
                                    }
                                }
                            }
                            return temp;
                        }
                    }
                }
            }
        }
        return null;
    }
}
