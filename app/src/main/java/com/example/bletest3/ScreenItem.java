package com.example.bletest3;
// This handles the introduction screen seen in MainActivity.
public class ScreenItem {
    String Title, Description;
    int ScreenImg;

    //
    public ScreenItem(String title, String description, int screenImg) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    // Sets the Title of a screen.
    public void setTitle(String title) {
        Title = title;
    }

    // Sets the Description of a screen.
    public void setDescription(String description) {
        Description = description;
    }

    // Sets the Image to display for a screen.
    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    // Returns the Title of a screen
    public String getTitle() {
        return Title;
    }

    // Returns the Description of a screen
    public String getDescription() {
        return Description;
    }

    // Returns the Image of a screen
    public int getScreenImg() {
        return ScreenImg;
    }
}