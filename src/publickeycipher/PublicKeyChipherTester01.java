package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class PublicKeyChipherTester01 {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
        String clearText = "In public key cryptography, one key is used to encrypt the text. " +
                "The order key is used to decrypt the text.";
        System.out.println(clearText);
        byte[] encryptedText = cipher.encryptMessage(clearText, publicKey);
        System.out.println(Util.byteArrayToHexString(encryptedText, " "));

        clearText = cipher.decryptMessage(encryptedText, privateKey);
        System.out.println(clearText);

        encryptedText = cipher.encryptMessage(clearText, privateKey);
        System.out.println(Util.byteArrayToHexString(encryptedText, " "));

        clearText = cipher.decryptMessage(encryptedText, publicKey);
        System.out.println(clearText);
    }
}
