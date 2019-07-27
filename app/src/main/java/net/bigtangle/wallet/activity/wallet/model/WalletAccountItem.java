package net.bigtangle.wallet.activity.wallet.model;

import net.bigtangle.core.Coin;
import net.bigtangle.core.Token;

import java.util.Map;

public class WalletAccountItem {

    public static WalletAccountItem build(Coin coin, Map<String, Token> tokennames) {
        WalletAccountItem walletAccountItem = new WalletAccountItem();
        walletAccountItem.setTokenId(coin.getTokenHex());
        walletAccountItem.setValue(coin.toPlainString());
        Token token = tokennames.get(coin.getTokenHex());
        if (token != null) {
            walletAccountItem.setTokenName(token.getTokenname());
        } else {
            walletAccountItem.setTokenName(coin.getTokenHex());
        }
        return walletAccountItem;
    }

    private String value;

    private String tokenId;

    private String tokenName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}
