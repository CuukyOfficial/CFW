package de.cuuky.cfw.inventory;

public interface InventoryInfoProvider {

    void refreshContent();

    int getSize();

    String getTitle();

}