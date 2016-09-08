package com.hayukleung.catchcrazycat.ui.main;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.hayukleung.catchcrazycat.R;
import com.hayukleung.catchcrazycat.ui.FullScreenActivity;

/**
 * chargerlink_v2
 * com.hayukleung.catchcrazycat.ui.main
 * GameActivity.java
 *
 * by hayukleung
 * at 2016-09-08 18:20
 */
public class GameActivity extends FullScreenActivity {

  private Playground mPlayground;

  @Override protected void onCreate(Bundle savedInstanceState) {

    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);

    // setContentView(new Playground(MainActivity.this));
    setContentView(R.layout.activity_game);

    mPlayground = (Playground) findViewById(R.id.playground);

    ViewGroup.LayoutParams layoutParams = mPlayground.getLayoutParams();
    layoutParams.height =
        (int) ((double) getApplicationContext().getResources().getDisplayMetrics().widthPixels
            / ((double) Playground.COL + 0.5) * ((double) Playground.ROW));
    mPlayground.setLayoutParams(layoutParams);

    findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {
        // TODO Auto-generated method stub
        mPlayground.initGame();
        mPlayground.redraw();
      }
    });
  }
}
