package uci.mmmi.sdu.dk.contextawarenessproject.common;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public abstract class AbstractHostActivity extends AppCompatActivity implements IHostActivity {

    protected BaseFragment selectedFragment;

    private boolean consumeBackPresses = false;

    protected abstract @IdRes
    int getBaseFragmentFrameResource();
    protected abstract @Nullable
    @DrawableRes
    Integer getActionbarIconResource();

    @Override
    public void onBackPressed() {
        if(!selectedFragment.onBackPressed() && !consumeBackPresses) {
            finish();
        }
    }

    protected void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public void popBackStack() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void popBackStackTillTag(String tag) {
        getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void addFragment(BaseFragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(getBaseFragmentFrameResource(), fragment, fragment.getTagText());
        ft.addToBackStack(fragment.getTagText());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(selectedFragment != null) {
            selectedFragment.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(selectedFragment != null) {
            Integer resId = selectedFragment.getMenuResId();
            if(resId != null) {
                getMenuInflater().inflate(resId, menu);
                for(int i = 0; i < menu.size(); i++) {
                    Drawable icon = menu.getItem(i).getIcon();
                    if(icon != null) {
                        icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    }
                }
                selectedFragment.onOptionsMenuCreated(menu);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setTitle(String text) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(text);
        }
    }

    @Override
    public void setUpNavigationEnabled(boolean upNavigationEnabled) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(!upNavigationEnabled);
            getSupportActionBar().setDisplayHomeAsUpEnabled(upNavigationEnabled);
            if(!upNavigationEnabled && getActionbarIconResource() != null) {
                getSupportActionBar().setIcon(getActionbarIconResource());
            }
        }
    }

    @Override
    public void setSelectedFragment(BaseFragment basefragment) {
        this.selectedFragment = basefragment;
    }

    public void setConsumeBackPresses(boolean consumeBackPresses) {
        this.consumeBackPresses = consumeBackPresses;
    }
}
