package com.example.finalproject;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String name;
    int skinType;
    EditText editName;
    EditText editSkin;
    Button saveButton;
    SharedPreferences userInfo;
    private Spinner skinTypeSpinner;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "name";
    private static final String KEY_SKIN_TYPE = "skinType";
    private static final String KEY_SPF = "spf";



    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        editName = view.findViewById(R.id.editName);
        editSkin = view.findViewById(R.id.editSkin);
        saveButton = view.findViewById(R.id.saveButton);
        skinTypeSpinner = view.findViewById(R.id.skinTypeSpinner);


        if (restorePrefData()) {
            name = userInfo.getString("name", "");
            skinType = userInfo.getInt("skinType", 0);
            editName.setText(name);
            editSkin.setText(String.valueOf(skinType));
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(editSkin) || isEmpty(editSkin)) {
                    Toast.makeText(getActivity(), "Please respond to every question", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Information Saved!", Toast.LENGTH_SHORT).show();
                    name = editName.getText().toString();
                    skinType = Integer.parseInt(editSkin.getText().toString());
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
        editor.putInt("skinType", skinType);
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