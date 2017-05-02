package uci.mmmi.sdu.dk.contextawarenessproject.common;

import android.support.v7.app.ActionBar;

public interface IHostActivity {
    void popBackStack();
    void popBackStackTillTag(String tag);
    void addFragment(BaseFragment fragment);
    void setTitle(String text);
    void setUpNavigationEnabled(boolean upNavigationEnabled);
    void setSelectedFragment(BaseFragment basefragment);
    void setConsumeBackPresses(boolean consumeBackPresses);
    ActionBar getSupportActionBar();
}
