package symmetriccipher;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.Util;

public class SymmetricCipherTester03 {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException, InterruptedException {

        SecretKey secretKey = (SecretKey) Util.loadObject ("secretKey.key");
        System.out.println("La llave simétrica ha sido recuperada");

        byte[] encryptedText = (byte[]) Util.loadObject ("text.encrypted");
        System.out.println("El texto cifrado ha sido recuperado");

        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");

        String clearText2 = cipher.decryptMessage(encryptedText);


        System.out.println("El texto descencriptado es:\n"+clearText2);
    }
}




