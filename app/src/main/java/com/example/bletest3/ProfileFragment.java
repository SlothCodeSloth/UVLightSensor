package com.example.bletest3;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
    private static final String TAG = "ProfileFragment";
    String name, skinTitle, skinDesc;
    int spfVal = 0, skinTypeVal, selectedType = 0;
    EditText editName, editSPF;
    TextView titleTextView, descTextView;
    Button saveButton;
    private Spinner skinTypeSpinner;
    private skinTypeAdapter adapter;
    private DataPassListener dataPassListener;
    private boolean savedData = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // Fragment Creator that allows for inputs. This lets the MainActivity generate a fragment if
    // it has savedData.
    public ProfileFragment(String name, int spfVal, int skinTypeVal) {
        this.name = name;
        this.spfVal = spfVal;
        this.skinTypeVal = skinTypeVal;
        savedData = true;
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
        // Assigns the UI Variables to their respective elements in the XML File
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        editName = view.findViewById(R.id.editName);
        editSPF = view.findViewById(R.id.editSPF);
        titleTextView = view.findViewById(R.id.titleTextView);
        descTextView = view.findViewById(R.id.descTextView);
        saveButton = view.findViewById(R.id.saveButton);
        skinTypeSpinner = view.findViewById(R.id.skinTypeSpinner);
        adapter = new skinTypeAdapter(getContext(), skinTypeData.getskinTypeList());  // MainActivity.this
        skinTypeSpinner.setAdapter(adapter);

        // If a user has saved data before: Update the Textboxes to show that data
        if (savedData) {
            editName.setText(name);
            editSPF.setText(String.valueOf(spfVal));
            skinTypeSpinner.setSelection(skinTypeVal - 1);
            editTextView(skinTypeVal);
        }

        // Prepares the Spinner, which allows the user to select a skin type
        skinTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                skinType type = (skinType) adapter.getItem(i);
                selectedType = type.getTypeNumber();
                editTextView(type.getTypeNumber());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Allows the user to press a button to save the data to the MainActivity2
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensures that all the data has been filled out
                if (isEmpty(editName) || isEmpty(editSPF)) {
                    Toast.makeText(getActivity(), "Please respond to every prompt", Toast.LENGTH_SHORT).show();
                }
                // Ensures that the SPF value is greater than 0
                else if (Integer.parseInt(editSPF.getText().toString()) == 0) {
                    Toast.makeText(getActivity(), "SPF must be greater than 0", Toast.LENGTH_SHORT).show();
                }
                // Ensures that the SPF value is less than 101.
                else if (Integer.parseInt(editSPF.getText().toString()) > 100) {
                    Toast.makeText(getActivity(), "SPF cannot exceed 100", Toast.LENGTH_SHORT).show();
                }
                // Passes the data to the MainActivity2 for it to save.
                // Also passes the data to HomeFragment in order to update it for use in the formula
                else {
                    Toast.makeText(getActivity(), "Information Saved!", Toast.LENGTH_SHORT).show();
                    name = editName.getText().toString();
                    spfVal = Integer.parseInt(editSPF.getText().toString());
                    skinTypeVal = selectedType;
                    if (dataPassListener != null) {
                        dataPassListener.onDataPassedHome(name, spfVal, skinTypeVal);
                    }
                }
            }
        });
        return view;
    }

    // Updates the user on information about their selected skin type.
    private void editTextView(int skinTypeVal) {
        // Assigns values to the Title and Description Strings
        switch (skinTypeVal) {
            case 1: // Skin Type 1
                skinTitle = "Very Fair";
                skinDesc = "Always Burns\nCannot Tan";
                break;
            case 2: // Skin Type 2
                skinTitle = "Fair";
                skinDesc = "Usually Burns\nSometimes Tans";
                break;
            case 3: // Skin Type 3
                skinTitle = "Medium";
                skinDesc = "Sometimes Burns\nUsually Tans";
                break;
            case 4: // Skin Type 4
                skinTitle = "Olive";
                skinDesc = "Rarely Burns\nTans Easily";
                break;
            case 5: // Skin Type 5
                skinTitle = "Brown";
                skinDesc = "Rarely Burns\nAlways Easily";
                break;
            case 6:// Skin Type 6
                skinTitle = "Dark Brown";
                skinDesc = "Never Burns\nAlways Tans";
                break;
        }
        // Updates the visible TextBoxes with these strings.
        titleTextView.setText(skinTitle);
        descTextView.setText(skinDesc);
    }

    // Checks if a User Input is empty.
    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    // Passes data to the MainActivity2, where it is then sent to the HomeFragment.
    public interface DataPassListener {
        void onDataPassedHome(String name, int spf, int skinType);
    }

    // Ensures that the data is capable of being passed onto MainActivity2.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DataPassListener) {
            dataPassListener = (DataPassListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement DataPassListener");
        }
    }

    // Detatches when no longer passing data to avoid memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        dataPassListener = null;
    }
}