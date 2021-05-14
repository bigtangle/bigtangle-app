package net.bigtangle.wallet.activity.wallet.model;

import net.bigtangle.apps.data.Certificate;

public class CertificateVO {
    private Certificate certificate;
    private String tokenid;

    public CertificateVO(Certificate certificate, String tokenid) {
        super();
        this.certificate = certificate;
        this.tokenid = tokenid;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }
}
