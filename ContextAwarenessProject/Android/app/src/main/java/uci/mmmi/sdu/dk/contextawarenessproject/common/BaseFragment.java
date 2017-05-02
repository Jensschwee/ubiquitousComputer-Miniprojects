package uci.mmmi.sdu.dk.contextawarenessproject.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

public abstract class BaseFragment extends Fragment {
    public IHostActivity hostActivity;

    public abstract String getTagText();

    public abstract boolean onBackPressed();

    public abstract Integer getMenuResId();

    public abstract String getTitle();

    public abstract void onOptionsMenuCreated(Menu menu);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(getActivity() instanceof IHostActivity)) {
            throw new ClassCastException("Hosting activity must implement IHostActivity.");
        } else {
            hostActivity = (IHostActivity) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        hostActivity.setSelectedFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        hostActivity.setTitle(getTitle());
        getActivity().invalidateOptionsMenu();
    }
}
