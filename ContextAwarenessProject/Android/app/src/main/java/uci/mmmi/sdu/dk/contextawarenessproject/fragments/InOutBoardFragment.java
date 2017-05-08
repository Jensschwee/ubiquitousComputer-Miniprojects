package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import uci.mmmi.sdu.dk.contextawarenessproject.adapters.InOutBoardArrayAdapter;
import uci.mmmi.sdu.dk.contextawarenessproject.pojos.InOutBoardListItem;

import java.util.LinkedList;
import java.util.List;

public class InOutBoardFragment extends ListFragment {

    private ArrayAdapter<InOutBoardListItem> adapter;

    public InOutBoardFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<InOutBoardListItem> testList = new LinkedList<>();
        testList.add(new InOutBoardListItem("Peter", "U175", 1, true));
        testList.add(new InOutBoardListItem("Hal", "U176", 2, true));
        testList.add(new InOutBoardListItem("Jens", "U174", 1232131, false));

        if(adapter == null) {
            adapter = new InOutBoardArrayAdapter(getContext());
        }
        adapter.clear();
        adapter.addAll(testList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setListShown(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setAdapter(adapter);
        setListShown(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
