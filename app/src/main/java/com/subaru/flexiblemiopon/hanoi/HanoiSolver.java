package com.subaru.flexiblemiopon.hanoi;

import android.util.Log;

/**
 * Created by shiny_000 on 2015/02/24.
 */
public class HanoiSolver {

    public static String solveHanoi(int n) {
        return moveHanoiTower(1, 3, n);
    }


    public static String moveHanoiTower(int fromIndex, int toIndex, int heightOfTower) {
        if (heightOfTower == 0) {
            return "";
        }
        return moveHanoiTower(fromIndex, 2, heightOfTower - 1) + moveFloor(fromIndex, toIndex, heightOfTower) + moveHanoiTower(2, fromIndex, heightOfTower - 1) + moveHanoiTower(fromIndex, toIndex, heightOfTower - 1);
    }

    public static String moveFloor(int fromIndex, int toIndex, int sizeOfBlock) {
        return "(" + Integer.toString(fromIndex) + ", " + Integer.toString(toIndex) + ", " + Integer.toString(sizeOfBlock) + ") -> ";
    }
}
