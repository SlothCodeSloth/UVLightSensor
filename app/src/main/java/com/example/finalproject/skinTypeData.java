package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

public class skinTypeData {
    public static List<skinType> getskinTypeList() {
        List<skinType> skinTypes = new ArrayList<>();

        skinType type1 = new skinType();
        type1.setType("Skin Type 1");
        type1.setImage(R.drawable.type1);
        type1.setTypeNumber(1);
        skinTypes.add(type1);

        skinType type2 = new skinType();
        type2.setType("Skin Type 2");
        type2.setImage(R.drawable.type2);
        type1.setTypeNumber(2);
        skinTypes.add(type2);

        skinType type3 = new skinType();
        type3.setType("Skin Type 3");
        type3.setImage(R.drawable.type3);
        type1.setTypeNumber(3);
        skinTypes.add(type3);

        skinType type4 = new skinType();
        type4.setType("Skin Type 4");
        type4.setImage(R.drawable.type4);
        type1.setTypeNumber(4);
        skinTypes.add(type4);

        skinType type5 = new skinType();
        type5.setType("Skin Type 5");
        type5.setImage(R.drawable.type5);
        type1.setTypeNumber(5);
        skinTypes.add(type5);

        skinType type6 = new skinType();
        type6.setType("Skin Type 6");
        type6.setImage(R.drawable.type6);
        type1.setTypeNumber(6);
        skinTypes.add(type6);

        return skinTypes;
    }
}
