package net.bigtangle.wallet.activity.shoping.model;

public class PaymentGoogsItem implements java.io.Serializable {

    public static PaymentGoogsItem build( String goodsId,String name, String num,boolean isSelect) {
        PaymentGoogsItem paymentGoogsItem = new PaymentGoogsItem();
        paymentGoogsItem.setGoodsId(goodsId);
        paymentGoogsItem.setName(name);
        paymentGoogsItem.setNum(num);
        paymentGoogsItem.setSelect(isSelect);
        return paymentGoogsItem;
    }

    private String goodsId;

    private String name;

    private String num;

    private boolean isSelect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

}
