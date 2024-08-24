package symmetriccipher;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.Util;

public class SymmetricCipherTester02 {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();
        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");

//        String clearText = "In symmetric key cryptography, the same key is used to encrypt and decrypt the clear text.";
        String clearText = "Iiteralmente ryan gosling";

        System.out.println(clearText);
        byte[] encryptedText = cipher.encryptMessage(clearText);

        System.out.println(new String(encryptedText));
        System.out.println(Util.byteArrayToHexString(encryptedText, " "));

        Util.saveObject(secretKey, "secretKey.key");
        System.out.println("The secret key has been saved");

        Util.saveObject(encryptedText, "text.encrypted");
        System.out.println("The encrypted text has been saved");
    }
}

