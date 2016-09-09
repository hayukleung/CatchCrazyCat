/**
 *
 */
package com.hayukleung.catchcrazycat.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author hayukleung
 */
public class Playground extends SurfaceView implements OnTouchListener {

  private static int WIDTH = 0;
  public static final int ROW = 9;
  public static final int COL = 9;
  private static final int BLOCKS = 8;

  private static final double HEXAGON = Math.sqrt(3f) / 2f;

  public static final int RESULT_LOSE = -1;
  public static final int RESULT_WIN = 0;
  public static final int RESULT_UNKNOWN = 1;
  private int mResult = RESULT_UNKNOWN;

  private Dot mMatrix[][];
  private Dot mCat;

  private Callback mCallback = new Callback() {

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override public void surfaceCreated(SurfaceHolder holder) {
      redraw();
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

      // 根据SurfaceView的宽度，调整高度
      ViewGroup.LayoutParams layoutParams = getLayoutParams();
      // layoutParams.height = (int) (width / ((float) Playground.COL + 0.5) * ((float) Playground.ROW));
      layoutParams.height = (int) ((width / ((float) COL + 0.5f)) * ((ROW - 1f) * HEXAGON + 1f));
      setLayoutParams(layoutParams);

      // 根据SurfaceView的大小，调整单元的尺寸
      WIDTH = 2 * width / (2 * COL + 1);
      redraw();
    }
  };

  private GameCallback mGameCallback;

  private Vibrator mVibrator;

  /**
   * @param context
   * @param attrs
   * @param defStyle
   */
  public Playground(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    getHolder().addCallback(mCallback);
    setOnTouchListener(this);
    initGame();
  }

  /**
   * @param context
   * @param attrs
   */
  public Playground(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  /**
   * @param context
   */
  public Playground(Context context) {
    this(context, null);
  }

  public void setGameCallback(GameCallback gameCallback) {
    this.mGameCallback = gameCallback;
  }

  private Dot getDot(int x, int y) {
    return mMatrix[y][x];
  }

  /**
   * 该点是否处于边缘
   *
   * @param dot
   * @return
   */
  private boolean isAtEdge(Dot dot) {
    return 0 == dot.getX() * dot.getY() || COL == dot.getX() + 1 || ROW == dot.getY() + 1;
  }

  /**
   * 返回邻居
   *
   * @param dot
   * @param direction
   * @return
   */
  private Dot getNeighbor(Dot dot, int direction) {
    switch (direction) {
      case 1:
        // 左
        return getDot(dot.getX() - 1, dot.getY());
      case 2:
        // 左上
        if (0 == dot.getY() % 2) {
          return getDot(dot.getX() - 1, dot.getY() - 1);
        } else {
          return getDot(dot.getX(), dot.getY() - 1);
        }
      case 3:
        // 右上
        if (0 == dot.getY() % 2) {
          return getDot(dot.getX(), dot.getY() - 1);
        } else {
          return getDot(dot.getX() + 1, dot.getY() - 1);
        }
      case 4:
        // 右
        return getDot(dot.getX() + 1, dot.getY());
      case 5:
        // 右下
        if (0 == dot.getY() % 2) {
          return getDot(dot.getX(), dot.getY() + 1);
        } else {
          return getDot(dot.getX() + 1, dot.getY() + 1);
        }
      case 6:
        // 左下
        if (0 == dot.getY() % 2) {
          return getDot(dot.getX() - 1, dot.getY() + 1);
        } else {
          return getDot(dot.getX(), dot.getY() + 1);
        }
      default:
        throw new RuntimeException("direction out of range");
    }
  }

  private int getDistance(Dot dot, int direction) {
    int distance = 0;
    if (isAtEdge(dot)) {
      return distance + 1;
    }
    Dot original = dot;
    Dot next;
    while (true) {
      next = getNeighbor(original, direction);
      if (Dot.STATUS_ON == next.getStatus()) {
        return distance * -1;
      }
      if (isAtEdge(next)) {
        return distance + 1;
      }
      distance++;
      original = next;
    }
  }

  /**
   * 移动神经猫
   *
   * @param dot
   */
  private void moveTo(Dot dot) {
    dot.setStatus(Dot.STATUS_IN);
    getDot(mCat.getX(), mCat.getY()).setStatus(Dot.STATUS_OFF);
    mCat.setXY(dot.getX(), dot.getY());
    mVibrator.vibrate(10);
  }

  /**
   * 神经猫移动
   */
  private void moveCat() {

    Vector<Dot> available = new Vector<Dot>();
    Vector<Dot> positive = new Vector<Dot>();
    Map<Dot, Integer> lines = new HashMap<Dot, Integer>();
    for (int i = 1; i < 7; i++) {
      Dot neighbor = getNeighbor(mCat, i);
      if (Dot.STATUS_OFF == neighbor.getStatus()) {
        available.add(neighbor);
        lines.put(neighbor, i);
        if (0 < getDistance(neighbor, i)) {
          positive.add(neighbor);
        }
      }
    }
    if (0 == available.size()) {
      win();
    } else if (1 == available.size()) {
      moveTo(available.get(0));
    } else {
      Dot best = null;
      if (0 < positive.size()) {
        // 可以到达屏幕边缘
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < positive.size(); i++) {
          int distance = getDistance(positive.get(i), lines.get(positive.get(i)));
          if (distance <= min) {
            min = distance;
            best = positive.get(i);
          }
        }
      } else {
        // 都有路障
        int max = 0;
        for (int i = 0; i < available.size(); i++) {
          int distance = getDistance(available.get(i), lines.get(available.get(i)));
          if (distance <= max) {
            max = distance;
            best = available.get(i);
          }
        }
      }
      moveTo(best);
    }

    if (isAtEdge(mCat)) {
      // 神经猫已在边界，游戏失败
      lose();
    }
  }

  private void lose() {
    mResult = RESULT_LOSE;
    if (null != mGameCallback) {
      mGameCallback.onLose();
    }
  }

  private void win() {
    mResult = RESULT_WIN;
    if (null != mGameCallback) {
      mGameCallback.onWin();
    }
  }

  public void redraw() {
    Canvas canvas = getHolder().lockCanvas();
    canvas.drawColor(0xFF727272);
    Paint paint = new Paint();
    paint.setFlags(Paint.ANTI_ALIAS_FLAG);

    for (int i = 0; i < ROW; i++) {

      int offset = (i % 2) * WIDTH / 2;

      for (int j = 0; j < COL; j++) {
        Dot dot = getDot(j, i);
        switch (dot.getStatus()) {
          case Dot.STATUS_ON:
            // 路障颜色
            paint.setColor(0xFF4CAF50);
            break;
          case Dot.STATUS_OFF:
            // 空地颜色
            paint.setColor(0xFFB6B6B6);
            break;
          case Dot.STATUS_IN:
            // 神经猫颜色
            paint.setColor(0xFF983844);
            break;
          default:
            break;
        }

        float left = dot.getX();
        float top = (float) (HEXAGON * dot.getY());

        canvas.drawOval(new RectF(
            left * WIDTH + offset,
            top * WIDTH,
            (left + 1) * WIDTH + offset,
            (top + 1) * WIDTH),
            paint);
      }
    }

    getHolder().unlockCanvasAndPost(canvas);
  }

  public void initGame() {

    if (null == mVibrator) {
      mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    mResult = RESULT_UNKNOWN;

    mMatrix = new Dot[ROW][COL];

    for (int i = 0; i < ROW; i++) {
      for (int j = 0; j < COL; j++) {
        mMatrix[i][j] = new Dot(j, i);
      }
    }

    for (int i = 0; i < ROW; i++) {
      for (int j = 0; j < COL; j++) {
        mMatrix[i][j].setStatus(Dot.STATUS_OFF);
      }
    }

    mCat = new Dot(COL / 2, ROW / 2);
    getDot(ROW / 2, COL / 2).setStatus(Dot.STATUS_IN);

    for (int i = 0; i < BLOCKS; ) {
      int x = (int) (Math.random() * 1000) % COL;
      int y = (int) (Math.random() * 1000) % ROW;
      if (Dot.STATUS_OFF == getDot(x, y).getStatus()) {
        getDot(x, y).setStatus(Dot.STATUS_ON);
        i++;
      }
    }


  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    if (MotionEvent.ACTION_UP == event.getAction()) {
      switch (mResult) {
        case RESULT_LOSE: {
          lose();
          return false;
        }
        case RESULT_WIN: {
          win();
          return false;
        }
        case RESULT_UNKNOWN:
        default: {
          int x, y;
          y = (int) (event.getY() / (WIDTH * HEXAGON));
          if (0 == y % 2) {
            x = (int) event.getX() / WIDTH;
          } else {
            x = (int) (event.getX() - WIDTH / 2) / WIDTH;
          }
          if (COL < x + 1 || ROW < y + 1) {
            return false;
          } else if (Dot.STATUS_OFF == getDot(x, y).getStatus()) {
            // 设置路障
            getDot(x, y).setStatus(Dot.STATUS_ON);
            moveCat();
          }
          redraw();
        }
      }
    }
    return true;
  }
}
