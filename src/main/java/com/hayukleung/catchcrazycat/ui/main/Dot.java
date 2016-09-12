/**
 *
 */
package com.hayukleung.catchcrazycat.ui.main;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hayukleung
 */
public class Dot {

  private int x, y;
  private int status;

  /**
   * TODO
   *
   * 保存六个方向的策略分值
   * 以方向为key，分值为value存值
   */
  private Map<Integer, Integer> strategy = new HashMap<>(6);

  /** 已点 */
  public static final int STATUS_ON = 0x0001;
  /** 未点 */
  public static final int STATUS_OFF = 0x0002;
  /** 神经猫站立位置 */
  public static final int STATUS_IN = 0x0003;

  /** 正左 */
  public static final int DIRECTION_L = 1;
  /** 左上 */
  public static final int DIRECTION_L_T = 2;
  /** 右上 */
  public static final int DIRECTION_R_T = 3;
  /** 正右 */
  public static final int DIRECTION_R = 4;
  /** 右下 */
  public static final int DIRECTION_R_B = 5;
  /** 左下 */
  public static final int DIRECTION_L_B = 6;

  /**
   * @param x
   * @param y
   */
  public Dot(int x, int y) {
    super();
    this.x = x;
    this.y = y;
    this.status = STATUS_OFF;
  }

  public int getX() {
    return this.x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return this.y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getStatus() {
    return this.status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;
  }
}
