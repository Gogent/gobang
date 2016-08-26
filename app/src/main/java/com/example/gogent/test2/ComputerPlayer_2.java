package com.example.gogent.test2;

import android.graphics.Point;

/**
 * Created by Gogent on 2015/11/17.
 */
public class ComputerPlayer_2 {
    private ChessType[][] chessMap;
    private int[][] computerMap = new int[DrawView.ROWS][DrawView.COLS];
    private int[][] playerMap = new int[DrawView.ROWS][DrawView.COLS];
    // 电脑的棋子颜色
    private ChessType computerType = ChessType.WHITE;
    // 玩家的棋子颜色
    private ChessType playerType = ChessType.BLACK;
    private ChessStatus[] chessStatus = new ChessStatus[4];

    public ComputerPlayer_2(ChessType[][] chessMap, ChessType computerType,
                            ChessType playerType) {
        this.chessMap = chessMap;
        this.playerType = playerType;
        this.computerType = computerType;
    }

    public Point start() {
        return getBestPoint();
    }

    /*
     *  初始化电脑和玩家的棋盘分值表
    */
    private void initValue() {
        for (int r = 0; r < DrawView.ROWS; r++) {
            for (int c = 0; c < DrawView.COLS; c++) {
                computerMap[r][c] = 0;
                playerMap[r][c] = 0;
            }
        }
        for (int i = 0; i < chessStatus.length; i++) {
            chessStatus[i] = ChessStatus.DIED;
        }
    }

    private Point getBestPoint() {
        initValue();
        for (int r = 0; r < DrawView.ROWS; r++) {
            for (int c = 0; c < DrawView.COLS; c++) {
                this.computerMap[r][c] = getValue(r, c, computerType);
                this.playerMap[r][c] = getValue(r, c, playerType);
            }
        }
        int pcMax = 0, playerMax = 0;
        Point pcPoint = new Point();
        Point playerPoint = new Point();
        for (int r = 0; r < DrawView.ROWS; r++) {
            for (int c = 0; c < DrawView.COLS; c++) {
                //找出电脑棋盘中的最大值点
                if (pcMax == computerMap[r][c]) ;
                else if (pcMax < computerMap[r][c]) {
                    pcMax = computerMap[r][c];
                    pcPoint.x = r;
                    pcPoint.y = c;
                } else ;

                //找出玩家棋盘中的最大值点
                if (playerMax == playerMap[r][c]) ;
                else if (playerMax < playerMap[r][c]) {
                    playerMax = playerMap[r][c];
                    playerPoint.x = r;
                    playerPoint.y = c;
                }
            }
        }
        //选择电脑和玩家棋盘分数最高的点
        if (pcMax == playerMax) {
            return pcPoint;
        } else {
            return pcMax > playerMax ? pcPoint : playerPoint;
        }
    }

    private int getValue(int r, int c, ChessType type) {
        int num[] = new int[4];
        num[0] = this.getHorCount(r, c, type); //横向棋子个数
        num[1] = this.getVerCount(r, c, type); //纵向棋子个数
        num[2] = this.getSloRCount(r, c, type);//反斜向棋子个数
        num[3] = this.getSloLCount(r, c, type);//斜向棋子个数

        //成五
        for (int i = 0; i < 4; i++) {
            if (num[i] >= 5)
                return ScoreTable.FIVE;
        }
        int temp = 0;
        // 双活四
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] != ChessStatus.DIED)
                temp++;
            if (temp == 2)
                return ScoreTable.DOUBLE_ALIVE_FOUR;
        }

        int t1 = 0, t2 = 0;
        // 活四死四
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] == ChessStatus.ALIVE)
                t1 = 1;
            if (num[i] == 4 && chessStatus[i] != ChessStatus.DIED)
                t2 = 1;
            if (t1 == 1 && t2 == 1)
                return ScoreTable.ALIVE_FOUR_AND_DEAD_FOUR;
        }
        // 活四活三
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] != ChessStatus.DIED)
                t1 = 1;
            if (num[i] == 3 && chessStatus[i] == ChessStatus.ALIVE)
                t2 = 1;
            if (t1 == 1 && t2 == 1)
                return ScoreTable.ALIVE_FOUR_AND_ALIVE_THREE;
        }
        // 活四死三
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] != ChessStatus.DIED)
                t1 = 1;
            if (num[i] == 3 && chessStatus[i] != ChessStatus.DIED)
                t2 = 1;
            if (t1 == 1 && t2 == 1) {
                return ScoreTable.ALIVE_FOUR_AND_DEAD_THREE;
            }
        }
        // 活四活二
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] != ChessStatus.DIED)
                t1 = 1;
            if (num[i] == 2 && chessStatus[i] != ChessStatus.ALIVE)
                t2 = 1;
            if (t1 == 1 && t2 == 1)
                return ScoreTable.ALIVE_FOUR_AND_ALIVE_TWO;
        }

        // 活四
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] == ChessStatus.ALIVE) {
                return ScoreTable.ALIVE_FOUR;
            }
        }

        // 双死四
        temp = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] == ChessStatus.DIED)
                temp++;
            if (temp == 2)
                return ScoreTable.DOUBLE_DEAD_FOUR;
        }

        // 死四活3
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] == ChessStatus.DIED)
                t1 = 1;
            if (num[i] == 3 && chessStatus[i] != ChessStatus.DIED)
                t2 = 1;
            if (t1 == 1 && t2 == 1) {
                return ScoreTable.DEAD_FOUR_AND_ALIVE_THREE;
            }
        }

        // 死四活2
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] == ChessStatus.DIED)
                t1 = 1;
            if (num[i] == 2 && chessStatus[i] != ChessStatus.DIED)
                t2 = 1;
            if (t1 == 1 && t2 == 1)
                return ScoreTable.DEAD_FOUR_AND_ALIVE_TWO;
        }

        // 双活三
        temp = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 3 && chessStatus[i] != ChessStatus.DIED)
                temp++;
            if (temp == 2)
                return ScoreTable.DOUBLE_ALIVE_THREE;
        }
        // 活死三
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 3 && chessStatus[i] == ChessStatus.ALIVE)
                t1 = 1;
            if (num[i] == 3 && chessStatus[i] == ChessStatus.DIED)
                t2 = 1;
            if (t1 == 1 && t2 == 1)
                return ScoreTable.ALIVE_THREE_AND_DEAD_THREE;
        }

        // 活三
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 3 && chessStatus[i] == ChessStatus.ALIVE)
                return ScoreTable.ALIVE_THREE;
        }

        // 死四
        for (int i = 0; i < num.length; i++) {
            if (num[i] == 4 && chessStatus[i] == ChessStatus.DIED)
                return ScoreTable.DEAD_FOUR;
        }

        // 半活死3
        t1 = 0;
        t2 = 0;
        for (int i = 0; i < 4; i++) {
            if (num[i] == 3 && chessStatus[i] == ChessStatus.DIED)
                t1 = 1;
            if (num[i] == 3 && chessStatus[i] == ChessStatus.HALFALIVE)
                t2 = 1;
            if (t1 == 1 && t2 == 1)
                return ScoreTable.ALIVE_THREE_AND_DEAD_THREE;
        }

        // 双活2
        temp = 0;
        for (int i = 0; i < 4; i++) {
            if (num[i] == 2 && chessStatus[i] == ChessStatus.ALIVE)
                temp++;
            if (temp == 2)
                return ScoreTable.DOUBLE_ALIVE_TWO;
        }

        // 死3
        for (int i = 0; i < 4; i++)
            if (num[i] == 3 && chessStatus[i] == ChessStatus.DIED)
                return ScoreTable.DEAD_THREE;

        // 活2
        for (int i = 0; i < 4; i++)
            if (num[i] == 2 && chessStatus[i] == ChessStatus.ALIVE)
                return ScoreTable.ALIVE_TWO;

        // 死2
        for (int i = 0; i < 4; i++)
            if (num[i] == 2 && chessStatus[i] == ChessStatus.DIED)
                return ScoreTable.DEAD_TWO;
        return 0;
    }

    /*
     * 横向搜索棋子的个数
     *
     * @param r
     *      棋盘对应的行数
     * @param c
     *      棋盘对应的列数
     * @param type
     *      棋子的类型
     * @return
     *      得到棋子的个数
     */
    private int getHorCount(int r, int c, ChessType type) {
        int count = 1;
        int t = c + 1; //记录所下棋子右边的位置
        for(int i = c + 1; i < c + 5; i++) {
            if(i > DrawView.COLS) {
                chessStatus[0] = ChessStatus.DIED;
                break;
            }
            if(chessMap[r][i] == computerType) {
                count++;
                if (count >= 5)
                    return count;
            } else {
                    chessStatus[0] = (chessMap[r][i] == ChessType.NONE) ? ChessStatus.ALIVE : ChessStatus.DIED;
                    t = i;
                    break;
            }
        }

        for(int j = c - 1; j < c - 5; j--) {
            if(j < 0) {
               // if (chessStatus[0] == ChessStatus.DIED && count < 5)
                    return 0;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if(count >= 5)
                    return count;
            }
        }
        return count;
    }

    /*
     * 纵向搜索
     */
    private int getVerCount(int r, int c, ChessType type) {
        int count = 1;
        int t = r + 1; //记录所下棋子右边的位置
        for(int i = r + 1; i < r + 5; i++) {
            if(i > DrawView.ROWS) {
                chessStatus[0] = ChessStatus.DIED;
                break;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if (count >= 5)
                    return count;
            } else {
                chessStatus[1] = (chessMap[r][i] == ChessType.NONE) ? ChessStatus.ALIVE : ChessStatus.DIED;
                t = i;
                break;
            }
        }

        for(int j = r - 1; j < c - 5; j--) {
            if(j < 0) {
                return 0;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if(count >= 5)
                    return count;
            }
        }
        return count;
    }

    /*
     * 反斜向搜索
     */
    private int getSloRCount(int r, int c, ChessType type) {
        int count = 1;
        int t = c + 1; //记录所下棋子右边的位置
        for(int i = c + 1,j = r + 1; i < c + 5 && j < r + 5; i++,j++) {
            if(i > DrawView.COLS || j > DrawView.ROWS) {
                chessStatus[2] = ChessStatus.DIED;
                break;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if (count >= 5)
                    return count;
            } else {
                chessStatus[2] = (chessMap[j][i] == ChessType.NONE) ? ChessStatus.ALIVE : ChessStatus.DIED;
                break;
            }
        }

        for(int i = c - 1,j = r - 1; i < c - 5 && j < r - 5; i--,j--) {
            if(j < 0 || i < 0) {
                return 0;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if(count >= 5)
                    return count;
            }
        }
        return count;
    }

    /*
     * 斜向搜索
     */
    private int getSloLCount(int r, int c, ChessType type) {
        int count = 1;
        int t = c + 1; //记录所下棋子右边的位置
        for(int i = c - 1,j = r + 1; i < c - 5 && j < r + 5; i--,j++) { //左下
            if(i < 0 || j > DrawView.ROWS) {
                chessStatus[3] = ChessStatus.DIED;
                break;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if (count >= 5)
                    return count;
            } else {
                chessStatus[3] = (chessMap[j][i] == ChessType.NONE) ? ChessStatus.ALIVE : ChessStatus.DIED;
                //t = i;
                break;
            }
        }

        for(int i = c + 1,j = r - 1;i < c + 5 && j < r - 5; j--, i++) { //右上
            if(i < 0 || j > DrawView.ROWS) {
                return 0;
            }
            if(chessMap[r][c] == computerType) {
                count++;
                if(count >= 5)
                    return count;
            }
        }
        return count;
    }
}
