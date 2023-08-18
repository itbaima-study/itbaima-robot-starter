package net.itbaima.robot.autoconfigure.util;

public enum LoginType {
    QR_CODE("qr_code"), PASSWORD("password");

    private final String name;

    LoginType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
