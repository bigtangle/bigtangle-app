package net.bigtangle.wallet.activity.market.model;

public class MarketPrice {
    private  String tokenid;
    private  String tokenname;
    private  String price ;
    private  String executedQuantity;

    public MarketPrice(String tokenid, String tokenname, String price, String executedQuantity) {
        this.tokenid = tokenid;
        this.tokenname = tokenname;
        this.price = price;
        this.executedQuantity = executedQuantity;
    }

    public String getTokenid() {
        return tokenid;
    }

    public void setTokenid(String tokenid) {
        this.tokenid = tokenid;
    }

    public String getTokenname() {
        return tokenname;
    }

    public void setTokenname(String tokenname) {
        this.tokenname = tokenname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(String executedQuantity) {
        this.executedQuantity = executedQuantity;
    }
}
