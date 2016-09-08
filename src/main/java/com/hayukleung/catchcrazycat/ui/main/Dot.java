/**
 *
 */
package com.hayukleung.catchcrazycat.ui.main;

/**
 * @author hayukleung
 */
public class Dot {

  int x, y;
  int status;

  /** 已点 */
  public static final int STATUS_ON = 0x0001;
  /** 未点 */
  public static final int STATUS_OFF = 0x0002;
  /** 神经猫站立位置 */
  public static final int STATUS_IN = 0x0003;

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
