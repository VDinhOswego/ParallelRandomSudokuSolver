/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc37503;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A testing class for the ParallelFinder and ParallelSolver classes
 *
 * @author Vincent
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Integer[][] p;
        PuzzleMaker pm = new PuzzleMaker();
        p = pm.getPuzzle(4);
        long start = System.nanoTime();
        ArrayList<int[]> points = new ArrayList<int[]>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (p[i][j] == null) {
                    points.add(new int[]{i, j});
                }
            }
        }
        HashMap<int[], int[]> values;
        while (true) {
            values = new ParallelFinder(p, points.toArray(new int[points.size()][2])).compute();
            boolean change = false;
            ArrayList<int[]> removal = new ArrayList<int[]>();
            for (int i = 0; i < points.size(); i++) {
                if (values.get(points.get(i)).length == 1) {
                    int[] po = points.get(i);
                    p[po[0]][po[1]] = values.get(po)[0];
                    removal.add(po);
                    change = true;
                }
            }
            points.removeAll(removal);
            if (!change) {
                break;
            }
        }
        System.out.println(points.size());
        for (int i = 0; i < points.size(); i++) {
            System.out.println(Arrays.toString(points.get(i)) + " " + Arrays.toString(values.get(points.get(i))));
        }
        Integer[][] sol = new ParallelSolver(p, points, values, null, 0).compute();
        long elapsed = (System.nanoTime() - start);
        System.out.print("---------------------------------------------------------------\n");
        for (int i = 0; i < 9; i++) {
            System.out.print("||  ");
            for (int j = 0; j < 9; j++) {
                String temp;
                if (sol[i][j] == null) {
                    temp = "  ";
                } else {
                    temp = sol[i][j].toString();
                }
                if ((j + 1) % 3 == 0) {
                    System.out.print(temp + "  ||  ");
                } else {
                    System.out.print(temp + "  |   ");
                }
            }
            System.out.print("\n");
            if ((i + 1) % 3 == 0) {
                System.out.print("---------------------------------------------------------------\n");
            }
        }
        System.out.print("Time: " + elapsed + "ns");
    }
}
