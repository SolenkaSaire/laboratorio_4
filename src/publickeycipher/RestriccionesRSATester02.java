package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Arrays;

public class RestriccionesRSATester02 {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    // Método para generar una cadena de caracteres aleatorios
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        byte[] clearText = generateRandomString(1025).getBytes();

        //crear matriz de bytes
       byte[][] matrixBytes = Util.split(clearText, 110);
       byte[][] matrixBytesEncrypted = new byte[matrixBytes.length][];

       for (int i = 0; i < matrixBytes.length; i++) {
           System.out.println("Bq " + i + ": " + new String(matrixBytes[i]).length());
           byte[] encryptedText = cipher.encryptMessage(new String(matrixBytes[i]), publicKey);
           System.out.println(encryptedText.length);
           matrixBytesEncrypted[i] = encryptedText;
       }

        //unir los bloques descifrados
        byte[] decrypted = Util.join(matrixBytesEncrypted);
        byte[][] matrixBytesDecrypted = Util.split(decrypted, 110);

        String dencryptedText = "";

        for (int i = 0; i < matrixBytesDecrypted.length; i++) {
            System.out.println("Bq " + i + ": " + matrixBytesDecrypted[i].length);
            //dencryptedText += cipher.decryptMessage(matrixBytesDecrypted[i], privateKey);
        }

        System.out.println(dencryptedText);

        //imprimir
        System.out.println("Texto descifrado: " + new String(decrypted));



    }
}
