package net.bigtangle.wallet.core.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

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
import net.bigtangle.wallet.R;
import net.bigtangle.wallet.activity.BackupActivity;
import net.bigtangle.wallet.activity.wallet.model.CertificateVO;
import net.bigtangle.wallet.activity.wallet.model.IdentityVO;
import net.bigtangle.wallet.core.MySQLiteOpenHelper;
import net.bigtangle.wallet.core.WalletContextHolder;
import net.bigtangle.wallet.core.constant.HttpConnectConstant;
import net.bigtangle.wallet.core.constant.LogConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static String getIdtoken(ECKey userKey) {
        try {
            Map<String, String> param = new HashMap<String, String>();
            param.put("toaddress", userKey.toAddress(WalletContextHolder.networkParameters).toString());
            Log.i(LogConstant.TAG, "getIdtoken start");
            byte[] response = OkHttp3Util.postString(HttpConnectConstant.HTTP_SERVER_URL + ReqCmd.getOutputsHistory.name(),
                    Json.jsonmapper().writeValueAsString(param));
            Log.i(LogConstant.TAG, "getIdtoken end==" + response);
            GetBalancesResponse balancesResponse = Json.jsonmapper().readValue(response, GetBalancesResponse.class);
            Map<String, Token> tokennames = new HashMap<>();
            tokennames.putAll(balancesResponse.getTokennames());
            String idtoken = "";
            for (UTXO utxo : balancesResponse.getOutputs()) {
                if (checkIdentity(utxo, tokennames)) {
                    idtoken = utxo.getTokenId();
                    break;
                }
            }
            return idtoken;
        } catch (Exception e) {
            return "";
        }
    }


    public static boolean checkIdentity(UTXO utxo, Map<String, Token> tokennames) {
        return TokenType.identity.ordinal() == tokennames.get(utxo.getTokenId()).getTokentype();

    }

    public static boolean checkCertificate(UTXO utxo, Map<String, Token> tokennames) {
        return TokenType.certificate.ordinal() == tokennames.get(utxo.getTokenId()).getTokentype();

    }

    public static byte[] urlTobyte(InputStream in) throws Exception {
        ByteArrayOutputStream out = null;
        try {

            out = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] content = out.toByteArray();
        return content;
    }

    public static InputStream loadFromDB(String un, Context context) {
        Log.i("loadFromDB", "un==" + un);
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select  * from walletdata where username=?", new String[]{"bigtangle"});
        if (cursor.moveToFirst()) {
            Log.i("loadFromDBcursor", cursor.getString(0));
            if (cursor.getBlob(1) == null) {
                Log.i("loadFromDBcursor", "inputStream==null");
            }
            ByteArrayInputStream stream = new ByteArrayInputStream(cursor.getBlob(1));
            cursor.close();
            db.close();
            dbHelper.close();
            return stream;
        } else {
            cursor.close();
            db.close();
            dbHelper.close();
            return null;
        }
    }

    public static void saveDB(String signin, byte[] bytes, Context context) {
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", "bigtangle");
        if (bytes == null)
            Log.i("saveDB", "inputStream==null");
        cv.put("file_data", bytes);
        long result = db.insert("walletdata", null, cv);
        db.close();
        dbHelper.close();


    }

    public static void updateDB(String signin, byte[] bytes, Context context) {
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (bytes == null)
            Log.i("updateDB", "inputStream==null");
        cv.put("file_data", bytes);
        db.update("walletdata", cv, "username=?", new String[]{"bigtangle"});
        db.close();
        dbHelper.close();


    }

    public static void deleteDB(Context context) {
        MySQLiteOpenHelper dbHelper = new MySQLiteOpenHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("walletdata", "", new String[]{});
        db.close();
        dbHelper.close();


    }

    public static void backupFile(String un, Context context) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String now = dateFormat.format(new Date());
        String filename = "backup-" + now;
        InputStream is = loadFromDB("bigtangle", context);
        if (is == null) return;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;

        File dir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, filename + ".wallet");
        try {
            long total = is.available();
            fos = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / total * 100);
                //下载中更新进度条
                //listener.onDownloading(progress);
            }
            fos.flush();
            new LovelyInfoDialog(context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_error_white_24px)
                    .setTitle("")
                    .setMessage(R.string.save + file.getAbsolutePath())
                    .show();
        } catch (Exception e) {
            Log.e(LogConstant.TAG, "backup file", e);
            new LovelyInfoDialog(context)
                    .setTopColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_error_white_24px)
                    .setTitle(R.string.dialog_title_error)
                    .setMessage(R.string.current_selection_file_error
                            + "\n " + e.getLocalizedMessage())
                    .show();
        } finally {

            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {

            }
        }
    }

}
