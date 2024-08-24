package symmetriccipher;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.Util;

public class SymmetricCipherTester04 {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {
        // se incializa la llave secreta con el algoritmo DES
        SecretKey secretKey = null;
        secretKey = KeyGenerator.getInstance("DES").generateKey();

        // se crea el objeto de cifrado simetrico con la llave secreta y el algoritmo
        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");

        ArrayList<String> clearObject = new ArrayList<String>();
        byte[] encrypedObject = null;

        clearObject.add("Ana");
        clearObject.add("Bety");
        clearObject.add("Carolina");
        clearObject.add("Daniela");
        clearObject.add("Elena");

        System.out.println(clearObject);

        encrypedObject = cipher.encryptObject(clearObject);

        System.out.println(Util.byteArrayToHexString(encrypedObject, " "));

        clearObject = (ArrayList<String>) cipher.decryptObject(encrypedObject);
        System.out.println(clearObject);
    }
}

