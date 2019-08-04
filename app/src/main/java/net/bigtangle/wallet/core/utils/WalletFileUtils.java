package net.bigtangle.wallet.core.utils;

import net.bigtangle.core.Context;
import net.bigtangle.core.NetworkParameters;
import net.bigtangle.params.MainNetParams;
import net.bigtangle.wallet.KeyChainGroup;
import net.bigtangle.wallet.Wallet;
import net.bigtangle.wallet.WalletProtobufSerializer;
import net.bigtangle.wallet.core.LocalStorageContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WalletFileUtils {

    public static byte[] createWallet(NetworkParameters params) throws IOException {
        return createWallet(params, 0);
    }

    public static byte[] createWallet(NetworkParameters params, int size) throws IOException {
        KeyChainGroup keyChainGroup = new KeyChainGroup(params);
        keyChainGroup.setLookaheadSize(size);

        Context context = new Context(params);
        Context.propagate(context);
        Wallet wallet = new Wallet(params, keyChainGroup); // default

        wallet.freshReceiveKey();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        new WalletProtobufSerializer().writeWallet(wallet, outStream);
        return outStream.toByteArray();
    }

    public static void createWalletFile(String walletDirectory, String walletFilename) throws IOException {
        byte[] b = createWallet(MainNetParams.get());
        File file = new File(walletDirectory + walletFilename);
        FileOutputStream fileInputStream = new FileOutputStream(file);
        fileInputStream.write(b);
        fileInputStream.flush();
        fileInputStream.close();
    }

    public static void createWalletFileAndLoad() throws IOException {
        String walletDirectory = LocalStorageContext.get().readWalletDirectory();
        String walletFilename = LocalStorageContext.get().readWalletFilePrefix();
        createWalletFile(walletDirectory, walletFilename + ".wallet");
    }
}
