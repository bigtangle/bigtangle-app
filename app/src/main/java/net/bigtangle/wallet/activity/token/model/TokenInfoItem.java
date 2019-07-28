package net.bigtangle.wallet.activity.token.model;

public class TokenInfoItem {

    private String tokenName;

    private String tokenId;

    private String amount;

    private Boolean confirmed;

    private int tokenIndex;

    private String description;

    private String domainMame;

    private Integer signNumber;

    private Integer tokenType;

    private Boolean tokenStop;

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public void setTokenIndex(int tokenIndex) {
        this.tokenIndex = tokenIndex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomainMame() {
        return domainMame;
    }

    public void setDomainMame(String domainMame) {
        this.domainMame = domainMame;
    }

    public Integer getSignNumber() {
        return signNumber;
    }

    public void setSignNumber(Integer signNumber) {
        this.signNumber = signNumber;
    }

    public Integer getTokenType() {
        return tokenType;
    }

    public void setTokenType(Integer tokenType) {
        this.tokenType = tokenType;
    }

    public Boolean getTokenStop() {
        return tokenStop;
    }

    public void setTokenStop(Boolean tokenStop) {
        this.tokenStop = tokenStop;
    }
}
