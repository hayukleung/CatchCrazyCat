package com.hayukleung.catchcrazycat.ui;

import android.os.Bundle;
import android.view.View;
import com.hayukleung.catchcrazycat.R;

/**
 * 无Toolbar的Fragment
 */
public abstract class BaseFragmentNoToolbar extends BaseFragment {

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getToolBarShadow().setBackgroundColor(getResources().getColor(R.color.transparent));
    getToolBar().setVisibility(View.GONE);
  }

  @Override public int getHeadBarHeight() {
    return super.getHeadBarHeight() - getToolBarHeight();
  }
}
