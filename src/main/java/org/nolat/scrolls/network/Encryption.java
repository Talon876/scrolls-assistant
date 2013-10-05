package org.nolat.scrolls.network;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class Encryption {

    private static final Logger log = Logger.getLogger(Encryption.class);
    private static final String EXPONENT = "AQAB";
    private static final String MODULUS = "mFCubVhPGG+euHuVQbNObqod/Ji0kRe+oh2OCFR7aV09xYiOklqFQ8jgIgAHvyCcM1JowqfFeJ5jV9up0Lh0eIiv3FPRu14aQS35kMdBLMebSW2DNBkfVsOF3l498WWQS9/THIqIaxbqwRDUxba5btBLTN0/A2y6WWiXl05Xu1c=";

    /**
     * Performs RSA encryption using Mojang's Scrolls public key in the form of a modulus and exponent.
     * 
     * @param message
     *            The message to be encrypted (email or password)
     * @return The base64 encoding representation of the message, ready to be sent to the Scrolls server
     */
    public static String encrypt(String message) {
        log.info("Encrypting some stuff");
        byte[] modBytes = Base64.decodeBase64(MODULUS);
        byte[] expBytes = Base64.decodeBase64(EXPONENT);

        BigInteger mod = new BigInteger(1, modBytes);
        BigInteger exp = new BigInteger(1, expBytes);

        byte[] encrypted = null;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            Cipher cipher = Cipher.getInstance("RSA");

            RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(mod, exp);
            PublicKey pubKey = factory.generatePublic(pubSpec);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encrypted = cipher.doFinal(message.getBytes("ASCII"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(encrypted));
    }
}
