package net.bigtangle.wallet.activity.market.model;

import net.bigtangle.core.Coin;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.core.OrderRecord;
import net.bigtangle.wallet.core.WalletContextHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarketOrderItem implements java.io.Serializable {

    public static MarketOrderItem build(OrderRecord orderRecord) {
        MarketOrderItem marketOrderItem = new MarketOrderItem();
        if (NetworkParameters.BIGTANGLE_TOKENID_STRING.equals(orderRecord.getOfferTokenid())) {
            marketOrderItem.setType("BUY");
            marketOrderItem.setAmount(orderRecord.getTargetValue());
            marketOrderItem.setTokenId(orderRecord.getTargetTokenid());
            marketOrderItem.setPrice(Coin.toPlainString(orderRecord.getOfferValue() / orderRecord.getTargetValue()));
        } else {
            marketOrderItem.setType("SELL");
            marketOrderItem.setAmount(orderRecord.getOfferValue());
            marketOrderItem.setTokenId(orderRecord.getOfferTokenid());
            marketOrderItem.setPrice(Coin.toPlainString(orderRecord.getTargetValue() / orderRecord.getOfferValue()));
        }
        marketOrderItem.setOrderId(orderRecord.getInitialBlockHashHex());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        marketOrderItem.setValidateTo(dateFormat.format(new Date(orderRecord.getValidToTime() * 1000)));
        marketOrderItem.setValidateFrom(dateFormat.format(new Date(orderRecord.getValidFromTime() * 1000)));
        marketOrderItem.setAddress(ECKey.fromPublicOnly(orderRecord.getBeneficiaryPubKey()).toAddress(WalletContextHolder.networkParameters).toString());
        marketOrderItem.setInitialBlockHashHex(orderRecord.getInitialBlockHashHex());
        return marketOrderItem;
    }

    private String type;

    private Long amount;

    private String tokenId;

    private String price;

    private String orderId;

    private String validateTo;

    private String validateFrom;

    private String address;

    private String initialBlockHashHex;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getValidateTo() {
        return validateTo;
    }

    public void setValidateTo(String validateTo) {
        this.validateTo = validateTo;
    }

    public String getValidateFrom() {
        return validateFrom;
    }

    public void setValidateFrom(String validateFrom) {
        this.validateFrom = validateFrom;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInitialBlockHashHex() {
        return initialBlockHashHex;
    }

    public void setInitialBlockHashHex(String initialBlockHashHex) {
        this.initialBlockHashHex = initialBlockHashHex;
    }
}
