package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by figaw on 20/12/2015.
 */
public class HashingHelper {
    static HashingHelper instance = null;
    MessageDigest digest = null;


    public static HashingHelper getInstance() {
        if (instance == null) {
            instance = new HashingHelper();
        }

        return instance;
    }

    public HashingHelper() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getBits(int i, int numBits, int shift) {
        return getBits(i + "", numBits, shift);
    }

    // Returns numBits bits of SHA-256 hash of obj-string
    public String getBits(String obj, int numBits, int shift) {


        byte[] hash = digest.digest(obj.getBytes(StandardCharsets.UTF_8));

        return toBinary(hash).substring(shift, shift + numBits);
    }

    // SO: http://stackoverflow.com/questions/11528898/convert-byte-to-binary-in-java
    private String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }
}
