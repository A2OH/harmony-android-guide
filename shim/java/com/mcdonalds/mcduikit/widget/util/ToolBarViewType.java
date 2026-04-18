package com.mcdonalds.mcduikit.widget.util;

public enum ToolBarViewType {
    ARCH_LOGO("arch_logo"),
    BACK("back"),
    BASKET("basket"),
    BASKET_TEXT("basket_text"),
    CANCEL("cancel"),
    CLOSE("close"),
    HEADER_TITLE("header_title"),
    LEFT_ICON("left_icon"),
    LOCATION_SEARCH("location_search"),
    RIGHT_ICON("right_icon"),
    SAVE("save");

    private final String viewTypeName;

    ToolBarViewType(String viewTypeName) {
        this.viewTypeName = viewTypeName;
    }

    public String getViewTypeName() {
        return viewTypeName;
    }
}
