package com.example.bletest3;

import java.util.ArrayList;
import java.util.List;
// This class holds the data for use in the Spinner Selector.
// In other words, it determines skinType to show when selecting .
public class skinTypeData {
    public static List<skinType> getskinTypeList() {
        List<skinType> skinTypes = new ArrayList<>();

        // Skin Type 1
        skinType type1 = new skinType();
        type1.setType("Skin Type 1");
        type1.setImage(R.drawable.img_type1);
        type1.setTypeNumber(1);
        skinTypes.add(type1);

        // Skin Type 2
        skinType type2 = new skinType();
        type2.setType("Skin Type 2");
        type2.setImage(R.drawable.img_type2);
        type2.setTypeNumber(2);
        skinTypes.add(type2);

        // Skin Type 3
        skinType type3 = new skinType();
        type3.setType("Skin Type 3");
        type3.setImage(R.drawable.img_type3);
        type3.setTypeNumber(3);
        skinTypes.add(type3);

        // Skin Type 4
        skinType type4 = new skinType();
        type4.setType("Skin Type 4");
        type4.setImage(R.drawable.img_type4);
        type4.setTypeNumber(4);
        skinTypes.add(type4);

        // Skin Type 5
        skinType type5 = new skinType();
        type5.setType("Skin Type 5");
        type5.setImage(R.drawable.img_type5);
        type5.setTypeNumber(5);
        skinTypes.add(type5);

        // Skin Type 6
        skinType type6 = new skinType();
        type6.setType("Skin Type 6");
        type6.setImage(R.drawable.img_type6);
        type6.setTypeNumber(6);
        skinTypes.add(type6);

        return skinTypes;
    }
}
