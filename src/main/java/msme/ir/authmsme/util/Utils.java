package msme.ir.authmsme.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;

public class Utils {

    private static final int AES_BLOCK_SIZE = 16;

    private static final String key = "Q4x15V5Hj8eG557Mq4QNwUwlxipU4wwJ";

    public static String encrypt(String token) {
        long timestamp = System.currentTimeMillis();
        String ivString = "nahal" + String.valueOf(timestamp / 1000) + String.valueOf(timestamp % 1000).charAt(0);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            IvParameterSpec iv = new IvParameterSpec(ivString.getBytes("ASCII"));
            SecretKey secretKey = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            ByteArrayOutputStream bous = new ByteArrayOutputStream(100);

            bous.write(cipher.doFinal(pad(token).getBytes("ASCII")));
            bous.write("I_".getBytes("ASCII"));
            bous.write(ivString.getBytes("ASCII"));
//            
            return Arrays.toString(Base64.encode(bous.toByteArray()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IOException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public static String pad(String message) {
        int x = AES_BLOCK_SIZE - message.length() % AES_BLOCK_SIZE;
        return message + StringUtils.repeat(Character.toString((char) x), x);
    }
}
