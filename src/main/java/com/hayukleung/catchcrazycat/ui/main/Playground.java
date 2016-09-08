/**
 *
 */
package com.hayukleung.catchcrazycat.ui.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
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
  private Dot matrix[][];
  private Dot cat;

  /**
   * @param context
   * @param attrs
   * @param defStyleAttr
   * @param defStyleRes
   */
  @TargetApi(Build.VERSION_CODES.LOLLIPOP) public Playground(Context context, AttributeSet attrs,
      int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    getHolder().addCallback(callback);
    setOnTouchListener(this);
    initGame();
  }

  /**
   * @param context
   * @param attrs
   * @param defStyle
   */
  public Playground(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    getHolder().addCallback(callback);
    setOnTouchListener(this);
    initGame();
  }

  /**
   * @param context
   * @param attrs
   */
  public Playground(Context context, AttributeSet attrs) {
    super(context, attrs);
    getHolder().addCallback(callback);
    setOnTouchListener(this);
    initGame();
  }

  /**
   * @param context
   */
  public Playground(Context context) {
    super(context);
    getHolder().addCallback(callback);
    setOnTouchListener(this);
    initGame();
  }

  private Dot getDot(int x, int y) {
    return matrix[y][x];
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
        return null;
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
    getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
    cat.setXY(dot.getX(), dot.getY());
  }

  /**
   * 神经猫移动
   */
  private void moveCat() {
    if (isAtEdge(cat)) {
      // 神经猫已在边界，游戏失败
      lose();
      return;
    }
    Vector<Dot> available = new Vector<Dot>();
    Vector<Dot> positive = new Vector<Dot>();
    Map<Dot, Integer> lines = new HashMap<Dot, Integer>();
    for (int i = 1; i < 7; i++) {
      Dot neighbor = getNeighbor(cat, i);
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
      //
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
  }

  private void lose() {
    Toast.makeText(getContext(), "lose", Toast.LENGTH_SHORT).show();
  }

  private void win() {
    Toast.makeText(getContext(), "win", Toast.LENGTH_SHORT).show();
  }

  public void redraw() {
    Canvas canvas = getHolder().lockCanvas();
    canvas.drawColor(0xFF727272);
    Paint paint = new Paint();
    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    for (int i = 0; i < ROW; i++) {
      int offset = 0;
      if (0 != i % 2) {
        offset = WIDTH / 2;
      }
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
        canvas.drawOval(new RectF(dot.getX() * WIDTH + offset, dot.getY() * WIDTH,
            (dot.getX() + 1) * WIDTH + offset, (dot.getY() + 1) * WIDTH), paint);
      }
    }

    getHolder().unlockCanvasAndPost(canvas);
  }

  Callback callback = new Callback() {

    @Override public void surfaceDestroyed(SurfaceHolder holder) {
      // TODO Auto-generated method stub

    }

    @Override public void surfaceCreated(SurfaceHolder holder) {
      // TODO Auto-generated method stub
      redraw();
    }

    @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      // TODO Auto-generated method stub
      WIDTH = 2 * width / (2 * COL + 1);
      redraw();
    }
  };

  public void initGame() {

    matrix = new Dot[ROW][COL];

    for (int i = 0; i < ROW; i++) {
      for (int j = 0; j < COL; j++) {
        matrix[i][j] = new Dot(j, i);
      }
    }

    for (int i = 0; i < ROW; i++) {
      for (int j = 0; j < COL; j++) {
        matrix[i][j].setStatus(Dot.STATUS_OFF);
      }
    }

    cat = new Dot(COL / 2, ROW / 2);
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

  /*
   * (non-Javadoc)
   *
   * @see android.view.View.OnTouchListener#onTouch(android.view.View,
   * android.view.MotionEvent)
   */
  @Override public boolean onTouch(View v, MotionEvent event) {
    if (MotionEvent.ACTION_UP == event.getAction()) {
      int x, y;
      y = (int) event.getY() / WIDTH;
      if (0 == y % 2) {
        x = (int) event.getX() / WIDTH;
      } else {
        x = (int) (event.getX() - WIDTH / 2) / WIDTH;
      }
      if (COL < x + 1 || ROW < y + 1) {
        // initGame();
        return false;
      } else if (Dot.STATUS_OFF == getDot(x, y).getStatus()) {
        // 设置路障
        getDot(x, y).setStatus(Dot.STATUS_ON);
        moveCat();
      }
      redraw();
    }
    return true;
  }
}
