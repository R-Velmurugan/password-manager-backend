package com.caput_draconis.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoUtils {

    /**
     *
     * @return a random 16byte length salt used for encryption
     */
    @Nonnull
    public static byte[] getSalt(){
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     *
     * @return The Initialization Vector- a random byte array that is used to make sure that encryption of same string yields a different output.
     *         This can be stored as is in the DB cause this carries no information on its own.
     */
    @Nonnull
    public static IvParameterSpec getIv(){
        return new IvParameterSpec(getSalt());
    }

    /**
     *
     * @param password the raw master password
     * @return a key that can be used with AES algo
     */
    @Nonnull
    public static SecretKey deriveKeyForEncryption(@Nonnull final String password , @Nonnull final byte[] salt){
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray() , salt , 100000 , 256);
            SecretKey temp = factory.generateSecret(spec);
            return new SecretKeySpec(temp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param rawPassword - the plaintext password entered by user
     * @param key - the key derived from the master password
     * @return The password encrypted by the following algos:
     * 1. AES - Advance Encryption Standard with symmetric key - same key for encryption and decryption
     * 2. CBC - Cipher Block Chaining where the current password is XORed with the result of previous cycle. The cycle is started using the IV
     * 3. PKCS5Padding - A padding algo that makes sure that all blocks are of 16bytes long (16 is for AES)
     */
    @Nullable
    public static String encryptPassword(@Nonnull final String rawPassword , @Nonnull final SecretKey key){
        try {
            IvParameterSpec iv = getIv();
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE , key , iv);
            byte[] encryptedPasswordBytes = cipher.doFinal(rawPassword.getBytes());

            //64 encoding to get clean data.
            String encryptedPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes);
            String encodedIV = Base64.getEncoder().encodeToString(iv.getIV());

            return encryptedPassword
                    .concat(".")
                    .concat(encodedIV);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    @Nullable
    public static String decryptPassword(@Nonnull final String encryptedPasswordWithIV, @Nonnull final SecretKey key){
        String[] passwordParts = encryptedPasswordWithIV.split("\\.");
        String encryptedPassword = passwordParts[0];
        String encryptedIV = passwordParts[1];

        byte[] decryptedPasswordBytes = Base64.getDecoder().decode(encryptedPassword);
        byte[] decryptedIVBytes = Base64.getDecoder().decode(encryptedIV);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(decryptedIVBytes);
            cipher.init(Cipher.DECRYPT_MODE , key , iv);
            byte[] decryptedPassword = cipher.doFinal(decryptedPasswordBytes);
            return new String(decryptedPassword);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }
}
