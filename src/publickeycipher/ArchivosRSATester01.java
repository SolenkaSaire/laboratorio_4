package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class ArchivosRSATester01 {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            String algorithm = "RSA";
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(2048); // Clave de 2048 bits
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Crear instancia de PublicKeyCipher
            PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

            // Archivo de prueba para cifrar y descifrar
            String inputFilePath = "test.txt";

            // Cifrar archivo
            System.out.println("Cifrando archivo...");
            cipher.encryptTextFile(inputFilePath, publicKey);
            System.out.println("Archivo cifrado generado: " + inputFilePath + ".rsa");

            // Descifrar archivo
            System.out.println("Descifrando archivo...");
            cipher.decryptTextFile(inputFilePath + ".rsa", privateKey);
            System.out.println("Archivo descifrado generado: " + inputFilePath + ".plain.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
