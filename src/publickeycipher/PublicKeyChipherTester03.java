package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class PublicKeyChipherTester03 {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        Util.printKey(publicKey);
        Util.printKey(privateKey);

        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);
        String clearText = "In public key cryptography, one key is used to encrypt the text. " +
                "The order key is used to decrypt the text.";
        System.out.println(clearText);
        byte[] encryptedText = cipher.encryptMessage(clearText, publicKey);
        String encryptedHexString = Util.getPGPMessageString(encryptedText);
        System.out.println(encryptedHexString);

        Util.saveToFile(Util.getKeyString(publicKey), "publicKey.pem");
        Util.saveToFile(Util.getKeyString(privateKey), "privateKey.key");
        Util.saveToFile(encryptedHexString, "encryptedText.txt.asc");
    }
}
