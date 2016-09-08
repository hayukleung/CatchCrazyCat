package com.hayukleung.catchcrazycat.ui;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import com.hayukleung.catchcrazycat.EventBus;
import com.hayukleung.catchcrazycat.eventbus.NotifyType;
import com.mdroid.app.BasicFragment;

public abstract class BaseFragment extends BasicFragment implements NotifyType.INotify {

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EventBus.bus().register(this);
  }

  @Override public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
    return super.onCreateAnimation(transit, enter, nextAnim);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBus.bus().unregister(this);
  }

  @Override public void onNotify(NotifyType type) {
    switch (type.getType()) {
    }
  }

  /**
   * @return Return a simple name
   */
  protected abstract String getName();
}
