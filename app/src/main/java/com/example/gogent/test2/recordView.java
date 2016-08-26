package com.example.gogent.test2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

/**
 * Created by Gogent on 2015/12/21.
 */
public class recordView extends View {

    private static int screenWidth = Screen.screenWidth;
    private static int screenHeight = Screen.screenHeight;

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
    private Stack<Step> next = new Stack<Step>();   //下一步
    private Stack<Step> back = new Stack<Step>();   //上一步
    private String filename = null;

    private ChessType[][] chessMap = new ChessType[ROWS][COLS];

    //构造函数
    public recordView(Context context, String fn) {
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
        filename = new String(fn);

        initChess();
        loadFile();
    }

    public void loadFile() {
        FileInputStream input = null;
        BufferedReader reader = null;
        try {
            input = context.openFileInput(filename);
            reader = new BufferedReader(new InputStreamReader(input));
            String st = "";
            String number1 = "";
            String number2 = "";
            String number3 = "";
            int i = 0;
            while((st = reader.readLine()) != null) {
                number1 = "";
                number2 = "";
                number3 = "";
                if(st != "") {
                    char temp[] = st.toCharArray();
                    for (i = 0; i < temp.length; i++) {
                        if (temp[i] != ',')
                            number1 += temp[i];
                        else break;
                    }
                    i++;
                    for (; i < temp.length; i++) {
                        if (temp[i] != ',')
                            number2 += temp[i];
                        else break;
                    }
                    i++;
                    for (; i < temp.length; i++)
                        number3 += temp[i];
                    int r = Integer.parseInt(number1);
                    int c = Integer.parseInt(number2);
                    int typenumber = Integer.parseInt(number3);
                    ChessType type;
                    if (typenumber == 0)
                        type = ChessType.BLACK;
                    else
                        type = ChessType.WHITE;
                    next.push(new Step(r, c, type));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initChess() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                chessMap[i][j] = ChessType.NONE;
            }
        }
        invalidate();
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
        Bitmap exit = BitmapFactory.decodeResource(getResources(), R.drawable.exit2);
        Bitmap resizeExit = resizeImage(exit, 0.6f, 0.6f);
        Bitmap retract = BitmapFactory.decodeResource(getResources(), R.drawable.retract);
        Bitmap resizeRetract = resizeImage(retract, 0.6f, 0.6f);
        Bitmap last = BitmapFactory.decodeResource(getResources(), R.drawable.last);
        Bitmap source_next = BitmapFactory.decodeResource(getResources(), R.drawable.next);
        Bitmap resizelast = resizeImage(last, 0.6f, 0.6f);
        Bitmap resizenext = resizeImage(source_next, 0.6f, 0.6f);


        setBackgroundResource(R.drawable.bg5);

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
                if(!gameOver) {
                    if (chessMap[r][c] == ChessType.NONE)
                        continue;
                    if (chessMap[r][c] == ChessType.BLACK) {
                        canvas.drawBitmap(resizeBlack, r * MARGIN + PADDING_LEFT - MARGIN / 2, c * MARGIN + PADDING_TOP - MARGIN / 2, paint);
                    } else if (chessMap[r][c] == ChessType.WHITE) {
                            canvas.drawBitmap(resizeWhite, r * MARGIN + PADDING_LEFT - MARGIN / 2, c * MARGIN + PADDING_TOP - MARGIN / 2, paint);
                    }
                }
            }
        }
        canvas.drawBitmap(resizelast, PADDING_LEFT, (COLS - 1) * MARGIN + PADDING_TOP + 10, paint);
        canvas.drawBitmap(resizenext, PADDING_LEFT + resizelast.getWidth() + 10, (COLS - 1) * MARGIN + PADDING_TOP + 10, paint);
        canvas.drawBitmap(resizeExit, PADDING_LEFT, COLS * MARGIN + PADDING_TOP + 20, paint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Bitmap last = BitmapFactory.decodeResource(getResources(), R.drawable.last);
        Bitmap resizelast = resizeImage(last, 0.6f, 0.6f);
        Bitmap exit = BitmapFactory.decodeResource(getResources(), R.drawable.exit2);
        Bitmap resizeExit = resizeImage(exit, 0.6f, 0.6f);

        Step step;
        float x = event.getX();
        float y = event.getY();
        int r = Math.round((x - this.PADDING_LEFT) / this.MARGIN);
        int c = Math.round((y - this.PADDING_TOP) / this.MARGIN);

        //上一步
        if( (x >= PADDING_LEFT) && (x <= PADDING_LEFT + resizelast.getWidth()) &&
            (y > (COLS - 1) * MARGIN + PADDING_TOP + 10) && (y <= (COLS - 1) * MARGIN + PADDING_TOP + 10 + resizelast.getHeight()) ) {
            if(!back.empty()) {
                step = back.pop();
                next.push(step);
                chessMap[step.ROW][step.COL] = ChessType.NONE;
                this.invalidate();
            } else {
                Toast.makeText(context, "棋盘为空", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //下一步
        if( (x >= PADDING_LEFT + resizelast.getWidth() + 10) && (x <= PADDING_LEFT + resizelast.getWidth() * 2 + 10) &&
            (y > (COLS - 1) * MARGIN + PADDING_TOP + 10) && (y <= (COLS - 1) * MARGIN + PADDING_TOP + 10 + resizelast.getHeight()) ) {
            if(!next.empty()) {
                step = next.pop();
                back.push(step);
                chessMap[step.ROW][step.COL] = step.thisType;
                this.invalidate();
            } else {
                new AlertDialog.Builder(context).setTitle("回放已结束，是否退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).setNegativeButton("取消", null).show();
            }
        }
        //退出
        if( (x >= PADDING_LEFT) && (x <= PADDING_LEFT + resizeExit.getWidth()) &&
                (y > COLS * MARGIN + PADDING_TOP + 20) && (y <= COLS * MARGIN + PADDING_TOP + 20 + resizelast.getHeight()) ) {
            new AlertDialog.Builder(context).setTitle("是否退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            }).setNegativeButton("取消", null).show();
        } else return false;
        this.invalidate();
        //return true;
        return super.onTouchEvent(event);
    }
}
