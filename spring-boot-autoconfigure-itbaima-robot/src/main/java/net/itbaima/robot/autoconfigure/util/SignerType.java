package net.itbaima.robot.autoconfigure.util;

public enum SignerType {
    FUQIULUO("fuqiuluo/unidbg-fetch-qsign"),
    KILIOKUARA("kiliokuara/magic-signer-guide");

    String type;

    SignerType(String type) {
        this.type = type;
    }

    public String toName() {
        return type;
    }
}
