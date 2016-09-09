package com.hayukleung.catchcrazycat.ui.main;

import android.os.Bundle;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hayukleung.catchcrazycat.R;
import com.hayukleung.catchcrazycat.Toost;
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

  @Bind(R.id.playground) Playground mPlayground;

  @Override protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);
    ButterKnife.bind(this);

    mPlayground.setGameCallback(new GameCallback() {

      @Override public void onWin() {
        Toost.message("赢了");
      }

      @Override public void onLose() {
        Toost.message("输了");
      }
    });
  }

  @Override protected void onDestroy() {
    ButterKnife.unbind(this);
    super.onDestroy();
  }

  @OnClick(R.id.reset) public void onClick() {
    mPlayground.initGame();
    mPlayground.redraw();
  }
}
