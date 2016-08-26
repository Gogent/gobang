package com.example.gogent.test2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Stack;

/**
 * Created by Gogent on 2015/9/27.
 */
public class DrawView extends View {

    private static int screenWidth = Screen.screenWidth;
    private static int screenHeight = Screen.screenHeight;
    public static final String path = "/data/data/com.example.gogent.test2/files/history";
    public static final int ROWS = 15;//列
    public static final int COLS = 15;//行

    private static float PADDING = ((float) (screenWidth) / (COLS - 1)) / 2;    //棋盘和屏幕边缘的距离
    private static float PADDING_LEFT = (float) (screenWidth) / (COLS - 1); //与屏幕左端的距离，用于确定X坐标
    private static float PADDING_TOP = ((float) (screenHeight) / (ROWS - 1)) / 2 ;   //与屏幕顶端的距离，用于确定Y坐标    要修改
    private static float ROW_MARGIN = ((float) (screenHeight - PADDING * 2)) / (ROWS - 1);  //每一行的距离
    private static float COL_MARGIN = ((float) (screenWidth - PADDING * 2)) / (COLS - 1);   //每一列的距离
    private static float MARGIN = ROW_MARGIN < COL_MARGIN ? ROW_MARGIN : COL_MARGIN; //格间距
    private Context context = null;
    private boolean gameOver = false;
    private Stack<Step> steps = new Stack<Step>();   //悔棋
    private Stack<Step> back = new Stack<Step>();   //悔棋后返回

    private ChessType playerType = ChessType.BLACK;
    private ChessType computerType = ChessType.WHITE;
    private ChessType[][] chessMap = new ChessType[ROWS][COLS];
    private ComputerPlayer computerPlayer = new ComputerPlayer(chessMap, computerType, playerType);

    //构造函数
    public DrawView(Context context) {
        super(context);
        this.context = context;
        PADDING_LEFT = ((screenWidth) / (COLS - 1)) / 2;
        PADDING_TOP = ((screenHeight) / (ROWS - 1)) / 2;
        PADDING = PADDING_LEFT < PADDING_TOP ? PADDING_LEFT : PADDING_TOP;
        ROW_MARGIN = ((screenHeight - PADDING * 2)) / (ROWS - 1);
        COL_MARGIN = ((screenWidth - PADDING * 2)) / (COLS - 1);
        MARGIN = ROW_MARGIN < COL_MARGIN ? ROW_MARGIN : COL_MARGIN;
        PADDING_LEFT = (screenWidth - (COLS - 1) * MARGIN) / 2;
        PADDING_TOP = (screenHeight - (ROWS - 1) * MARGIN) / 2;

        initChess();
    }


    public void initChess() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                chessMap[i][j] = ChessType.NONE;
            }
        }
        invalidate();
    }

    public void reStart(){
        initChess();
        gameOver = false;
        while(!steps.isEmpty()) {
            steps.pop();
        }
        while(!back.isEmpty()) {
            back.pop();
        }
    }
    @Override
    public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);

        Bitmap white = BitmapFactory.decodeResource(getResources(), R.drawable.white2);
        Bitmap white_now = BitmapFactory.decodeResource(getResources(), R.drawable.white_now);
        Bitmap black = BitmapFactory.decodeResource(getResources(), R.drawable.black2);
        //Bitmap black_now = BitmapFactory.decodeResource(getResources(), R.drawable.black_now);
        Bitmap resizeWhite = resizeImage(white);
        Bitmap resizeWhiteNow = resizeImage(white_now);
        Bitmap resizeBlack = resizeImage(black);
        //Bitmap resizeBlackNow = resizeImage(black_now);
        Bitmap exit = BitmapFactory.decodeResource(getResources(), R.drawable.button);
        Bitmap resizeExit = resizeImage(exit, 0.5f, 0.5f);
        Bitmap retract = BitmapFactory.decodeResource(getResources(), R.drawable.retract);
        Bitmap resizeRetract = resizeImage(retract, 0.6f, 0.6f);

        setBackgroundResource(R.drawable.bg3);

        //行
        for (int i = 0; i < ROWS; i++) {
            canvas.drawLine(PADDING_LEFT, i * MARGIN + PADDING_TOP, (COLS - 1) * MARGIN + PADDING_LEFT, i * MARGIN + PADDING_TOP, paint);
        }
        //列
        for (int i = 0; i < COLS; i++) {
            canvas.drawLine(PADDING_LEFT + i * MARGIN, PADDING_TOP, PADDING_LEFT + i * MARGIN, MARGIN * (ROWS - 1) + PADDING_TOP, paint);
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                // System.out.print(chessMap[r][c] + " ");
                if(!gameOver) {
                    if (chessMap[r][c] == ChessType.NONE)
                        continue;
                    if (chessMap[r][c] == ChessType.BLACK) {
                        //if(chessMap[r][c] == chessMap[steps.peek().ROW][steps.peek().COL])
                        //    canvas.drawBitmap(resizeBlackNow, r * MARGIN + PADDING_LEFT - MARGIN / 2, c * MARGIN + PADDING_TOP - MARGIN / 2, paint);
                        //paint.setColor(Color.BLACK);
                        //canvas.drawCircle(r * MARGIN + PADDING_LEFT, c * MARGIN + PADDING_TOP, MARGIN / 2, paint);
                        //steps.push(new Step(r,c,chessMap[r][c]));
                        //else
                        canvas.drawBitmap(resizeBlack, r * MARGIN + PADDING_LEFT - MARGIN / 2, c * MARGIN + PADDING_TOP - MARGIN / 2, paint);
                    } else if (chessMap[r][c] == ChessType.WHITE) {
                        if (r == steps.peek().ROW && c == steps.peek().COL)
                            canvas.drawBitmap(resizeWhiteNow, r * MARGIN + PADDING_LEFT - MARGIN / 2, c * MARGIN + PADDING_TOP - MARGIN / 2, paint);
                        /*paint.setColor(Color.RED);
                        canvas.drawCircle(r * MARGIN + PADDING_LEFT, c * MARGIN + PADDING_TOP, MARGIN / 2, paint);*/
                        //steps.push(new Step(r, c, chessMap[r][c]));
                        else
                            canvas.drawBitmap(resizeWhite, r * MARGIN + PADDING_LEFT - MARGIN / 2, c * MARGIN + PADDING_TOP - MARGIN / 2, paint);
                    }
                }
            }
            // System.out.println();
        }
        //canvas.drawBitmap(resizeExit, PADDING_LEFT, COLS * MARGIN + PADDING_TOP + 10, paint);
        canvas.drawBitmap(resizeRetract, PADDING_LEFT, COLS * MARGIN + PADDING_TOP + 10, paint);
        //paint.setTextSize(100);
        //canvas.drawText(String.valueOf(PADDING_TOP).toString(),50,100,paint);
    }

    private static Bitmap resizeImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.18f,0.18f); //长和宽放大缩小的比例
        Bitmap resizePicture = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizePicture;
    }

    private static Bitmap resizeImage(Bitmap bitmap, float resizeWidth, float resizeHeight) {
        Matrix matrix = new Matrix();
        matrix.postScale(resizeWidth,resizeHeight); //长和宽放大缩小的比例
        Bitmap resizePicture = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizePicture;
    }

    public boolean hasWin(int r, int c) {
        ChessType chessType = chessMap[r][c];
        //System.out.println(chessType);
        int count1 = 1;
        int count2 = 1;
        int count3 = 1;
        int count4 = 1;
        // 纵向搜索
        for (int i = r + 1; i < r + 5; i++) {
            if (i >= DrawView.ROWS)
                break;
            if (chessMap[i][c] == chessType) {
                count1++;
            } else
                break;
        }
        for (int i = r - 1; i > r - 5; i--) {
            if (i < 0)
                break;
            if (chessMap[i][c] == chessType)
                count1++;
            else
                break;
        }
        // System.out.println(count +" "+"1");
        if (count1 >= 5) {
            if (chessType == ChessType.BLACK) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("黑棋胜，是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time, Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + ",0\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + ",1\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            } else if (chessType == ChessType.WHITE) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("白棋胜，是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + ",0\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + ",1\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            }
        }
        // 横向搜索
        //count = 1;
        for (int i = c + 1; i < c + 5; i++) {
            if (i >= DrawView.COLS)
                break;
            if (chessMap[r][i] == chessType)
                count2++;
            else
                break;
        }
        for (int i = c - 1; i > c - 5; i--) {
            if (i < 0)
                break;
            if (chessMap[r][i] == chessType)
                count2++;
            else
                break;
        }
        // System.out.println(count +" " +"2");
        if (count2 >= 5) {
            if(chessType == ChessType.BLACK) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("黑棋胜，是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + ",0\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + ",1\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            }
            else if(chessType == ChessType.WHITE) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("白棋胜,是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + ",0\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + ",1\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            }
        }
        // 斜向"\"
        //count = 1;
        for (int i = r + 1, j = c + 1; i < r + 5; i++, j++) {
            if (i >= DrawView.ROWS || j >= DrawView.COLS) {
                break;
            }
            if (chessMap[i][j] == chessType)
                count3++;
            else
                break;
        }
        for (int i = r - 1, j = c - 1; i > r - 5; i--, j--) {
            if (i < 0 || j < 0)
                break;
            if (chessMap[i][j] == chessType)
                count3++;
            else
                break;
        }
        // System.out.println(count +" " +"3");
        if (count3 >= 5) {
            if(chessType == ChessType.BLACK) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("黑棋胜，是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + "," + "0" + "\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + "," + "1" + "\n");
                                else;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            }
            else if(chessType == ChessType.WHITE) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("白棋胜，是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + "," + "0" + "\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + "," + "1" + "\n");
                                else;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            }
        }
        // 斜向"/"
        //count = 1;
        for (int i = r + 1, j = c - 1; i < r + 5; i++, j--) {
            if (i >= DrawView.ROWS || j < 0)
                break;
            if (chessMap[i][j] == chessType)
                count4++;
            else
                break;
        }
        for (int i = r - 1, j = c + 1; i > r - 5; i--, j++) {
            if (i < 0 || j >= DrawView.COLS)
                break;
            if (chessMap[i][j] == chessType)
                count4++;
            else
                break;
        }
        // System.out.println(count +" " +"4");
        if (count4 >= 5) {
            if(chessType == ChessType.BLACK) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("黑棋胜，是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int sum = 0;
                                int win = 0;
                                FileInputStream in = null;
                                BufferedReader reader = null;
                                StringBuilder content = new StringBuilder();

                                FileOutputStream out = null;
                                BufferedWriter writer = null;
                                try {
                                    //读取玩家胜负数据
                                    in = context.openFileInput("winrate");
                                    reader = new BufferedReader(new InputStreamReader(in));
                                    String total = "";
                                    boolean isWin = true;
                                    while( (total = reader.readLine()) != null ) {
                                        if(isWin == true) {
                                            win = Integer.parseInt(total);
                                            isWin = false;
                                        }
                                        sum = Integer.parseInt(total);
                                    }
                                    win++;
                                    sum++;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if(reader != null) {
                                            reader.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //写入玩家胜负数据
                                try {
                                    out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                                    writer = new BufferedWriter(new OutputStreamWriter(out));
                                    writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if(writer != null) {
                                            writer.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                //保存用户数据
                                java.util.Date utilDate = new java.util.Date();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                                String time = formatter.format(utilDate);
                                FileOutputStream output = null;
                                BufferedWriter filewriter = null;
                                try {
                                    output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                                    filewriter = new BufferedWriter(new OutputStreamWriter(output));
                                    ChessType type;
                                    while(!steps.empty()) {
                                        Step step = steps.pop();
                                        type = step.thisType;
                                        if(type == ChessType.BLACK)
                                            filewriter.write(step.ROW + "," + step.COL + "," + "0" + "\n");
                                        else if(type == ChessType.WHITE)
                                            filewriter.write(step.ROW + "," + step.COL + "," + "1" + "\n");
                                        else;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if(filewriter != null) {
                                            filewriter.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            win++;
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                    return true;
                }
                else if(chessType == ChessType.WHITE) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("白棋胜,是否保存记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //保存用户数据
                        java.util.Date utilDate = new java.util.Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
                        String time = formatter.format(utilDate);
                        FileOutputStream output = null;
                        BufferedWriter filewriter = null;
                        try {
                            output = context.openFileOutput(time + ".txt", Context.MODE_PRIVATE);
                            filewriter = new BufferedWriter(new OutputStreamWriter(output));
                            ChessType type;
                            while(!steps.empty()) {
                                Step step = steps.pop();
                                type = step.thisType;
                                if(type == ChessType.BLACK)
                                    filewriter.write(step.ROW + "," + step.COL + "," + "0" + "\n");
                                else if(type == ChessType.WHITE)
                                    filewriter.write(step.ROW + "," + step.COL + "," + "1" + "\n");
                                else;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(filewriter != null) {
                                    filewriter.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int sum = 0;
                        int win = 0;
                        FileInputStream in = null;
                        BufferedReader reader = null;
                        StringBuilder content = new StringBuilder();

                        FileOutputStream out = null;
                        BufferedWriter writer = null;
                        try {
                            //读取玩家胜负数据
                            in = context.openFileInput("winrate");
                            reader = new BufferedReader(new InputStreamReader(in));
                            String total = "";
                            boolean isWin = true;
                            while( (total = reader.readLine()) != null ) {
                                if(isWin == true) {
                                    win = Integer.parseInt(total);
                                    isWin = false;
                                }
                                sum = Integer.parseInt(total);
                            }
                            sum++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(reader != null) {
                                    reader.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //写入玩家胜负数据
                        try {
                            out = context.openFileOutput("winrate", Context.MODE_PRIVATE);
                            writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(String.valueOf(win).toString() + "\n" + String.valueOf(sum).toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(writer != null) {
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).show();
                return true;
            }
        }
        /*if(count1 == 5 || count2 == 5 || count3 == 5 || count4 == 5) {
            if(chessType == ChessType.BLACK) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("黑棋胜").setPositiveButton("确定", null).show();
                return true;
            }
            else if(chessType == ChessType.WHITE) {
                gameOver = true;
                new AlertDialog.Builder(context).setTitle("游戏结束").setMessage("白棋胜").setPositiveButton("确定", null).show();
                return true;
            }
        }*/
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Bitmap retract = BitmapFactory.decodeResource(getResources(), R.drawable.retract);
        Bitmap resizeRetract = resizeImage(retract, 0.6f, 0.6f);

        float x = event.getX();
        float y = event.getY();
        int r = Math.round((x - this.PADDING_LEFT) / this.MARGIN);
        int c = Math.round((y - this.PADDING_TOP) / this.MARGIN);
     /*   if(x > PADDING_LEFT && x < PADDING_LEFT + resizeExit.getWidth() && y > COLS * MARGIN + PADDING_TOP && y < COLS * MARGIN + PADDING_TOP + resizeExit.getHeight()) {
            new AlertDialog.Builder(context).setTitle("退出").setMessage("是否要退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
            }).setNegativeButton("取消", null).show();
            //System.exit(0);
        }*/
        //悔棋
        if(x > PADDING_LEFT && x < PADDING_LEFT + resizeRetract.getWidth() &&
           y > COLS * MARGIN + PADDING_TOP && y < COLS * MARGIN + PADDING_TOP + resizeRetract.getHeight()) {
            if(!steps.isEmpty()) {
                Step temp = steps.pop();
                Step temp2 = steps.pop();
                chessMap[temp.ROW][temp.COL] = ChessType.NONE;
                chessMap[temp2.ROW][temp2.COL] = ChessType.NONE;
                back.push(temp);
                back.push(temp2);
                this.invalidate();
            }
        }

        if (!(r >= 0 && r < ROWS && c >= 0 && c < COLS)) {
            return false;
        }
        if (!gameOver) {
            if (chessMap[r][c] == ChessType.NONE) {
                chessMap[r][c] = this.playerType;
                steps.push(new Step(r, c, chessMap[r][c]));
                //this.invalidate();
                this.hasWin(r, c);
                /*if(this.hasWin(r, c)) {
                    this.gameOver = true;
                    new AlertDialog.Builder(context).setTitle("提示").setMessage("玩家胜利").setPositiveButton("确定", null).show();
                }*/

                Point p = computerPlayer.start();
                chessMap[p.x][p.y] = this.computerType;
                if(!gameOver) {
                    steps.push(new Step(p.x, p.y, chessMap[p.x][p.y]));
                    this.invalidate();
                    this.hasWin(p.x, p.y);
                    //this.invalidate();
                }
                /*if(this.hasWin(r, c)) {
                    this.gameOver = true;
                    new AlertDialog.Builder(context).setTitle("提示").setMessage("电脑胜利").setPositiveButton("确定", null).show();
                }*/
            }

        } else {
            new AlertDialog.Builder(context).setTitle("提示").setMessage("游戏已结束,是否重新开始?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reStart();
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            }).show();
        }
        this.invalidate();
        //return true;
        return super.onTouchEvent(event);
    }
}
