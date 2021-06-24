package de.cuuky.cfw.inventory;

public interface InventoryInfoProvider {

    int getSize();

    String getTitle();

    void refreshContent();

}