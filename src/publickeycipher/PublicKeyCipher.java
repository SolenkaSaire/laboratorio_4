package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class PublicKeyCipher {

    private Cipher cipher;

    public PublicKeyCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance(algorithm);
    }

    public byte[] encryptMessage(String input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ciphertext = null;
        byte[] cleartext = input.getBytes();

        cipher.init(Cipher.ENCRYPT_MODE, key);
        ciphertext = cipher.doFinal(cleartext);

        return ciphertext;
    }

    public String decryptMessage(byte[] input, Key key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String output = null;

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] ciphertext = cipher.doFinal(input);
        output = new String(ciphertext);

        return output;
    }

    public byte[] encryptObject(Object input, Key key) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] clearObject = Util.objectToByteArray(input);
        byte[] cipherObject = cipher.doFinal(clearObject);

        return cipherObject;
    }

    public Object decryptObject(byte[] input, Key key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException, IOException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] clearText = cipher.doFinal(input);
        Object output = Util.byteArrayToObject(clearText);

        return output;
    }
}