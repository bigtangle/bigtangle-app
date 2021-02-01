package net.bigtangle.wallet.activity.wallet.model;

import android.graphics.Bitmap;

public class WalletAccountIdentiyItem {

    private String identitynumber;

    private String name;

    private String sex;

    private String homeaddress;

    private byte[] photo;
    private String idtoken;

    public String getIdtoken() {
        return idtoken;
    }

    public void setIdtoken(String idtoken) {
        this.idtoken = idtoken;
    }

    public String getIdentitynumber() {
        return identitynumber;
    }

    public void setIdentitynumber(String identitynumber) {
        this.identitynumber = identitynumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHomeaddress() {
        return homeaddress;
    }

    public void setHomeaddress(String homeaddress) {
        this.homeaddress = homeaddress;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
