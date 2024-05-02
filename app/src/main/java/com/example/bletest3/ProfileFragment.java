package com.example.bletest3;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = "ProfileFragment";
    String name;
    int spf = 0;
    int skinTypeNumber;
    int selectedType = 0;
    EditText editName, editSPF;
    Button saveButton;
    SharedPreferences userInfo;
    private Spinner skinTypeSpinner;
    private skinTypeAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editName = view.findViewById(R.id.editName);
        editSPF = view.findViewById(R.id.editSPF);
        saveButton = view.findViewById(R.id.saveButton);
        skinTypeSpinner = view.findViewById(R.id.skinTypeSpinner);
        adapter = new skinTypeAdapter(getContext(), skinTypeData.getskinTypeList());  // MainActivity.this
        skinTypeSpinner.setAdapter(adapter);

        /*
        if (restorePrefData()) {
            name = userInfo.getString("name", "");
            spf = userInfo.getInt("spf", 0);
            skinTypeNumber = userInfo.getInt("skinType", 0);
            editName.setText(name);
            editSPF.setText(String.valueOf(spf));
            skinTypeSpinner.setSelection(skinTypeNumber - 1);
            bluetoothView.setText("Connected to: Nano 33 BLE (Temp Name)"); // Remove
        }

         */

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