package net.bigtangle.wallet.core.utils;

import net.bigtangle.core.Coin;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.wallet.core.HttpService;

import java.util.ArrayList;
import java.util.List;

public class CoinbaseUtil {

    public static Coin calculateTotalUTXOList(byte[] pubKeyHash, String tokenid) throws Exception {
        List<String> pubKeyHashs = new ArrayList<String>();
        pubKeyHashs.add(Utils.HEX.encode(pubKeyHash));

        List<UTXO> listUTXO = HttpService.getUTXOWithPubKeyHash(pubKeyHashs, tokenid);
        Coin amount = Coin.valueOf(0, tokenid);
        if (listUTXO == null || listUTXO.isEmpty()) {
            return amount;
        }
        for (UTXO utxo : listUTXO) {
            amount = amount.add(utxo.getValue());
        }
        return amount;
    }
}
