package net.bigtangle.wallet.core.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.bigtangle.apps.data.Certificate;
import net.bigtangle.apps.data.IdentityData;
import net.bigtangle.apps.data.SignedData;
import net.bigtangle.core.ECKey;
import net.bigtangle.core.KeyValue;
import net.bigtangle.core.Token;
import net.bigtangle.core.TokenType;
import net.bigtangle.core.UTXO;
import net.bigtangle.core.Utils;
import net.bigtangle.core.response.GetBalancesResponse;
import net.bigtangle.encrypt.ECIESCoder;
import net.bigtangle.params.ReqCmd;
import net.bigtangle.utils.Json;
import net.bigtangle.utils.OkHttp3Util;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CommonUtil {
    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {


        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置

            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void identityList(ECKey signerKey, ECKey userKey, List<IdentityData> identityDatas, Map<String, Token> tokennames) throws Exception {
        Map<String, String> param = new HashMap<String, String>();
        param.put("toaddress", userKey.toAddress(WalletContextHolder.networkParameters).toString());
        Log.i(LogConstant.TAG, "identityList start");
        String response = OkHttp3Util.postString(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getOutputsHistory.name(),
                Json.jsonmapper().writeValueAsString(param));
        Log.i(LogConstant.TAG, "identityList end==" + response);
        GetBalancesResponse balancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);
        tokennames.putAll(balancesResponse.getTokennames());
        for (UTXO utxo : balancesResponse.getOutputs()) {
            if (checkIdentity(utxo, tokennames)) {
                //if (history) {
                //  identitiesAdd(utxo, signerKey,identityDatas,tokennames);
                //} else {
                if (!utxo.isSpent()) {
                    Log.i(LogConstant.TAG, "checkIdentity end");
                    identitiesAdd(utxo, signerKey, identityDatas, tokennames);
                }
                // }

            }
        }
    }
    public static void certificateList(ECKey signerKey, ECKey userKey, List<Certificate> certificates, Map<String, Token> tokennames) throws Exception {
        Map<String, String> param = new HashMap<String, String>();
        param.put("toaddress", userKey.toAddress(WalletContextHolder.networkParameters).toString());
        Log.i(LogConstant.TAG, "certificateList start");
        String response = OkHttp3Util.postString(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getOutputsHistory.name(),
                Json.jsonmapper().writeValueAsString(param));
        Log.i(LogConstant.TAG, "certificateList end==" + response);
        GetBalancesResponse balancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);
        tokennames.putAll(balancesResponse.getTokennames());
        for (UTXO utxo : balancesResponse.getOutputs()) {
            if (checkCertificate(utxo, tokennames)) {
                //if (history) {
                //  identitiesAdd(utxo, signerKey,identityDatas,tokennames);
                //} else {
                if (!utxo.isSpent()) {
                    Log.i(LogConstant.TAG, "checkCertificate end");
                    certificateAdd(utxo, signerKey, certificates, tokennames);
                }
                // }

            }
        }
    }
    public static boolean checkIdentity(UTXO utxo, Map<String, Token> tokennames) {
        return TokenType.identity.ordinal() == tokennames.get(utxo.getTokenId()).getTokentype();

    }
    public static boolean checkCertificate(UTXO utxo, Map<String, Token> tokennames) {
        return TokenType.certificate.ordinal() == tokennames.get(utxo.getTokenId()).getTokentype();

    }
    public static void identitiesAdd(UTXO utxo, ECKey signerKey, List<IdentityData> identityDatas, Map<String, Token> tokennames) throws Exception {
        Token token = tokennames.get(utxo.getTokenId());
        if (token == null || token.getTokenKeyValues() == null)
            return;
        for (KeyValue kvtemp : token.getTokenKeyValues().getKeyvalues()) {
            if (kvtemp.getKey().equals(signerKey.getPublicKeyAsHex())) {
                try {
                    byte[] decryptedPayload = ECIESCoder.decrypt(signerKey.getPrivKey(),
                            Utils.HEX.decode(kvtemp.getValue()));
                    SignedData sdata = new SignedData().parse(decryptedPayload);
                    IdentityData prescription = new IdentityData().parse(Utils.HEX.decode(sdata.getSerializedData()));
                    identityDatas.add(prescription);
                    Log.i(LogConstant.TAG, "identitiesAdd");
                    // sdata.verify();
                } catch (Exception e) {
                }
            }
        }
    }
    public static void certificateAdd(UTXO utxo, ECKey signerKey, List<Certificate> certificates, Map<String, Token> tokennames) throws Exception {
        Token token = tokennames.get(utxo.getTokenId());
        if (token == null || token.getTokenKeyValues() == null)
            return;
        for (KeyValue kvtemp : token.getTokenKeyValues().getKeyvalues()) {
            if (kvtemp.getKey().equals(signerKey.getPublicKeyAsHex())) {
                try {
                    byte[] decryptedPayload = ECIESCoder.decrypt(signerKey.getPrivKey(),
                            Utils.HEX.decode(kvtemp.getValue()));
                    SignedData sdata = new SignedData().parse(decryptedPayload);
                    Certificate certificate = new Certificate().parse(Utils.HEX.decode(sdata.getSerializedData()));
                    certificates.add(certificate);
                    Log.i(LogConstant.TAG, "certificateAdd");
                    // sdata.verify();
                } catch (Exception e) {
                }
            }
        }
    }

}
