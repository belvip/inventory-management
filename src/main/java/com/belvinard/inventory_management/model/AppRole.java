package com.belvinard.inventory_management.model;

public enum AppRole {
    ROLE_USER("User", "Limited access to the system"),
    ROLE_ADMIN("Administrator", "Complete management of the system and users"),
    ROLE_MANAGER("Manager", "Complete supervision of stock movements"),
    ROLE_SALES("Sales", "Management of sales and customer relationships");

    private final String displayName;
    private final String description;

    AppRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}