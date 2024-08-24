package symmetriccipher;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.Util;

public class SymmetricCipherTester01 {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        // se crea la llave secreta y se inicializa con el algoritmo DES
        SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();
        // se crea el objeto de cifrado simetrico con la llave secreta y el algoritmo
        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");

       // String clearText = "In symmetric key cryptography, the same key is used to encrypt and decrypt the clear text.";
        String clearText = "literalmente ryan gosling";

        System.out.println(clearText);

        // se cifra el mensaje claro
        byte[] encryptedText = cipher.encryptMessage(clearText);

        //se imprime el mensaje cifraddo
        System.out.println(new String(encryptedText));
        // se imprime el mensaje cifrado en hexadecimal
        System.out.println(Util.byteArrayToHexString(encryptedText, " "));

        String clearText2 = cipher.decryptMessage(encryptedText);
        System.out.println(clearText2);
    }
}

