package com.example.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    String name;
    int spf = 0;
    int skinTypeNumber;
    int selectedType = 0;
    EditText editName;
    EditText editSPF;
    Button saveButton;
    SharedPreferences userInfo;
    private Spinner skinTypeSpinner;
    private skinTypeAdapter adapter;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "name";
    private static final String KEY_SKIN_TYPE = "skinType";
    private static final String KEY_SPF = "spf";



    public AboutFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        editName = view.findViewById(R.id.editName);
        editSPF = view.findViewById(R.id.editSPF);
        saveButton = view.findViewById(R.id.saveButton);
        skinTypeSpinner = view.findViewById(R.id.skinTypeSpinner);
        adapter = new skinTypeAdapter(getContext(), skinTypeData.getskinTypeList());  // MainActivity.this
        skinTypeSpinner.setAdapter(adapter);

        if (restorePrefData()) {
            name = userInfo.getString("name", "");
            spf = userInfo.getInt("spf", 0);
            skinTypeNumber = userInfo.getInt("skinType", 0);
            editName.setText(name);
            editSPF.setText(String.valueOf(spf));
            skinTypeSpinner.setSelection(skinTypeNumber - 1);
        }

        skinTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                skinType type = (skinType) adapter.getItem(i);
                selectedType = type.getTypeNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(editName) || isEmpty(editSPF)) {
                    Toast.makeText(getActivity(), "Please respond to every prompt", Toast.LENGTH_SHORT).show();
                }
                else if (Integer.parseInt(editSPF.getText().toString()) == 0) {
                    Toast.makeText(getActivity(), "Please respond to every prompt correctly", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(getActivity(), "Information Saved!", Toast.LENGTH_SHORT).show();
                    name = editName.getText().toString();
                    spf = Integer.parseInt(editSPF.getText().toString());
                    skinTypeNumber = selectedType;
                    savePrefsData();
                }
            }
        });
        return view;
    }

    private void savePrefsData() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putBoolean("hasData", true);
        editor.putString("name", name);
        editor.putInt("spf", spf);
        editor.putInt("skinType", skinTypeNumber);
        editor.apply();
    }

    private boolean restorePrefData() {
        userInfo = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        boolean hasData = userInfo.getBoolean("hasData", false);
        return hasData;
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}