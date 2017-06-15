package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Alec on 2/27/2017.
 * Contains useful shared functions
 */
public class Util {
    public static String SHA1FromBytes(byte[] data) {
        MessageDigest digest;
        BigInteger hashInt;

        try {
            digest = MessageDigest.getInstance("SHA1");
            byte[] hash = digest.digest(data);
            hashInt = new BigInteger(1, hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        return hashInt.toString(16);
    }
}
