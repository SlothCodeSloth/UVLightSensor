package com.example.bletest3;
// SkinType Object. For use in the Spinner (Selection)
public class skinType {
    private String type;
    private int image;
    private int typeNumber;

    public skinType() {

    }

    // Returns "Skin Type #"
    public String getType() {
        return type;
    }

    // Sets the "Skin Type #"
    public void setType(String type) {
        this.type = type;
    }

    // Returns the image associated from a skinType.
    public int getImage() {
        return image;
    }

    // Sets the image associated with a skinType.
    public void setImage(int image) {
        this.image = image;
    }

    // Returns the "#" of the skin type
    public int getTypeNumber() {
        return typeNumber;
    }

    // Sets the "#" of the skin type
    public void setTypeNumber(int num) {
        this.typeNumber = num;
    }
}
