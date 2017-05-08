package uci.mmmi.sdu.dk.contextawarenessproject.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import uci.mmmi.sdu.dk.contextawarenessproject.R;

public class SettingsFragment extends Fragment {

    private TextView hello;
    private EditText name;
    private CheckBox hiddenMode;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        hello = (TextView) rootView.findViewById(R.id.fragment_settings_text);
        name = (EditText) rootView.findViewById(R.id.fragment_settings_name);
        hiddenMode = (CheckBox) rootView.findViewById(R.id.fragment_settings_hidden);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String currentName = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("name", "Unnamed");
        name.setText(currentName);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("name", s.toString()).apply();
            }
        });

        boolean currentIsHidden = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("hidden", false);
        hiddenMode.setChecked(currentIsHidden);
        hiddenMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("hidden", isChecked).apply();
            }
        });
    }
}
