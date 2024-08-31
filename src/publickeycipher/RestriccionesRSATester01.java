package publickeycipher;

import util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class RestriccionesRSATester01 {

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

        int[] rsaKeySizes = {1024, 2048, 3072, 4096};
        for (int size : rsaKeySizes) {
            keyPairGenerator.initialize(size);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            System.out.println("Testing RSA key size: " + size);

            int maxSize = size / 8 - 11; // Tamaño máximo de datos que se pueden cifrar con el tamaño de la clave RSA
            int currentSize = 0;
            String clearText = "";

            while (true) {
                try {
                    clearText = generateRandomString(currentSize);
                    byte[] encryptedText = cipher.encryptMessage(clearText, publicKey);
                    String decryptedText = cipher.decryptMessage(encryptedText, privateKey);

                    if (!clearText.equals(decryptedText)) {
                        System.out.println("Desencriptacion Fallida!");
                    }

                    currentSize += 10; // Incrementa el tamaño de la cadena en 10 caracteres
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    System.out.println("Maximum size for key size " + size + " is: " + (currentSize - 10));
                    System.out.println(clearText);
                    break;
                }
            }
            System.out.println();
        }
    }
}
