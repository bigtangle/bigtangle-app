package net.bigtangle.wallet.activity.shoping.model;

import android.content.Context;

import com.google.common.math.LongMath;

import net.bigtangle.core.ECKey;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.core.OrderRecord;
import net.bigtangle.core.Token;
import net.bigtangle.utils.MonetaryFormat;
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.core.WalletContextHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ShopGoogsItem implements java.io.Serializable {

    public static ShopGoogsItem build(String name,String price,String num) {
        ShopGoogsItem shopGoogsItem = new ShopGoogsItem();
        shopGoogsItem.setName(name);
        shopGoogsItem.setPrice(price);
        shopGoogsItem.setNum(num);
        return shopGoogsItem;
    }

    private String name;

    private String price;

    private String num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
