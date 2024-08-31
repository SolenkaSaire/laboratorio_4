package publickeycipher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
}
