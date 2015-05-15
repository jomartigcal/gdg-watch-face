package org.gdgph.watchface;

public class WearableConfiguration {

    private final String title;
    private int icon;

    public WearableConfiguration(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
