package net.bigtangle.wallet.activity.wallet.model;

import net.bigtangle.core.Coin;
import net.bigtangle.core.Token;

import java.util.Map;

public class WalletAccountItem {

    public static WalletAccountItem build(Coin coin, Map<String, Token> tokennames) {
        WalletAccountItem walletAccountItem = new WalletAccountItem();
        walletAccountItem.setTokenid(coin.getTokenHex());
        walletAccountItem.setValue(coin.toPlainString());
        Token token = tokennames.get(coin.getTokenHex());
        if (token != null) {
            walletAccountItem.setTokenname(token.getTokenname());
        } else {
            walletAccountItem.setTokenname(coin.getTokenHex());
        }
        return walletAccountItem;
    }

    private String value;

    private String tokenid;

    private String tokenname;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
