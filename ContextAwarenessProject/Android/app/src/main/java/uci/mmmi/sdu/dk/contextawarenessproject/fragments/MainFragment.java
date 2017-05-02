package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import uci.mmmi.sdu.dk.contextawarenessproject.R;
import uci.mmmi.sdu.dk.contextawarenessproject.adapters.MapsViewPagerAdapter;
import uci.mmmi.sdu.dk.contextawarenessproject.common.BaseFragment;

public class MainFragment extends BaseFragment {

    private MapsViewPagerAdapter mapsViewPagerAdapter;
    private ViewPager mapsViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mapsViewPagerAdapter = new MapsViewPagerAdapter(getChildFragmentManager());
        mapsViewPager = (ViewPager) rootView.findViewById(R.id.fragment_maps_viewpager);
        mapsViewPager.setAdapter(mapsViewPagerAdapter);

        return rootView;
    }

    @Override
    public String getTagText() {
        return "Main Fragment";
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Integer getMenuResId() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Main Fragment Title";
    }

    @Override
    public void onOptionsMenuCreated(Menu menu) {

    }
}
