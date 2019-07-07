package net.bigtangle.wallet.activity.wallet.model;

public class WalletSecretkeyItem {

    private String address;

    private String pubKeyHex;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPubKeyHex() {
        return pubKeyHex;
    }

    public void setPubKeyHex(String pubKeyHex) {
        this.pubKeyHex = pubKeyHex;
    }
}
