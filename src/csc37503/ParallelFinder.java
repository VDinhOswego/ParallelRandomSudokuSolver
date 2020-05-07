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
 * This class recursively finds all the possible values for the given blank
 * boxes of a Sudoku puzzle by using Tasks from a ForkJoinPool
 *
 * @author Vincent
 */
public class ParallelFinder extends RecursiveTask<HashMap<int[], int[]>> {

    private final Integer[][] puz;
    private final int[][] points;

    /**
     * Creates a new ParallelFinder with the to be solved puzzle and the blank
     * spots of the given puzzle
     *
     * @param p The incomplete puzzle
     * @param n The positions of the blank boxes
     */
    public ParallelFinder(Integer[][] p, int[][] n) {
        puz = p;
        points = n;
    }

    /**
     * Finds the possible values a certain box can have by checking the row,
     * column, and box the point belongs to.
     *
     * @param p The point to solve for
     * @return Array of the possible values
     */
    private int[] findVal(int[] p) {
        boolean[] row = new boolean[9], col = new boolean[9], blo = new boolean[9];
        for (int i = 0; i < 9; i++) {
            if (puz[p[0]][i] != null) {
                row[puz[p[0]][i] - 1] = true;
            }
            if (puz[i][p[1]] != null) {
                col[puz[i][p[1]] - 1] = true;
            }
        }
        for (int a = (p[0] / 3) * 3; a < ((p[0] / 3) * 3) + 3; a++) {
            for (int b = (p[1] / 3) * 3; b < ((p[1] / 3) * 3) + 3; b++) {
                if (puz[a][b] != null) {
                    blo[puz[a][b] - 1] = true;
                }
            }
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (!(row[i] || col[i] || blo[i])) {
                list.add(i + 1);
            }
        }
        int[] temp = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            temp[i] = list.get(i);
        }
        return temp;
    }

    /**
     * Recursively by splitting the array of blank points in half until the
     * length is 13 or less then it calls findVal() on each point and returns a
     * map of the results
     *
     * @return Returns a map of the possible values for each point
     */
    @Override
    protected HashMap<int[], int[]> compute() {
        HashMap<int[], int[]> map = new HashMap<int[], int[]>();
        if (points.length <= 13) {
            for (int i = 0; i < points.length; i++) {
                map.put(points[i], findVal(points[i]));
            }
            return map;
        } else {
            int mid = points.length >>> 1;
            int[][] p1 = new int[mid][2];
            int[][] p2 = new int[points.length - mid][2];
            for (int i = 0; i < points.length; i++) {
                if (i < mid) {
                    p1[i] = points[i];
                } else {
                    p2[i - mid] = points[i];
                }
            }
            ParallelFinder ps1 = new ParallelFinder(puz, p1);
            ParallelFinder ps2 = new ParallelFinder(puz, p2);
            ps1.fork();
            map = ps2.compute();
            map.putAll(ps1.join());
            return map;
        }
    }
}
