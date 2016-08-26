package com.example.gogent.test2;

/**
 * Created by Gogent on 2015/10/8.
 */
public class Step {
    public int ROW;
    public int COL;
    public ChessType thisType;
    Step(int row, int col, ChessType thistype) {
        ROW = row;
        COL = col;
        thisType = thistype;
    }
}
