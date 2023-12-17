package com.pandev.service.enums;

public enum ServiceCommand {
    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start"),
    VIEW_TREE("/viewTree"),
    ADD_ELEMENT("/addElement"),
    REMOVE_ELEMENT("/removeElement"),
    DOWNLOAD("/download"),
    UPLOAD("/upload");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static ServiceCommand fromValue(String v) {
        for (ServiceCommand c : ServiceCommand.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
