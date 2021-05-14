package net.bigtangle.wallet.activity.wallet.model;

import net.bigtangle.apps.data.IdentityData;

public class IdentityVO {

    private IdentityData identityData;
    private String tokenid;

    public IdentityVO(IdentityData identityData, String tokenid) {
        super();
        this.identityData = identityData;
        this.tokenid = tokenid;
    }

    public IdentityData getIdentityData() {
        return identityData;
    }

    public void setIdentityData(IdentityData identityData) {
        this.identityData = identityData;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }
}
