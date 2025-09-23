package com.example.bletest3;

import android.content.Context;
import android.security.keystore.SecureKeyImportUnavailableException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
// Handles the methods to use the skinTypeData.
public class skinTypeAdapter extends BaseAdapter {
    private Context context;
    private List<skinType> skinTypeList;

    public skinTypeAdapter(Context context, List<skinType> skinTypeList) {
        this.context = context;
        this.skinTypeList = skinTypeList;
    }

    // Returnsd how many skinType objects are in the list. If null; returns 0
    @Override
    public int getCount() {
        return skinTypeList != null ? skinTypeList.size() : 0;
    }

    // Retrieves a skinType from a specified location in the list.
    @Override
    public Object getItem(int i) {
        return skinTypeList.get(i);
    }

    // Retrieves the id of a specified skinType.
    @Override
    public long getItemId(int i) {
        return i;
    }

    // Prepares the Spinner Object. Retrieves the data depending on what skinType is being used.
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_skintype, viewGroup, false);
        TextView txtname = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        txtname.setText(skinTypeList.get(i).getType());
        image.setImageResource(skinTypeList.get(i).getImage());

        return rootView;
    }
}
