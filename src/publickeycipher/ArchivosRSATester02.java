package publickeycipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class ArchivosRSATester02 {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            String algorithm = "RSA";
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            keyPairGenerator.initialize(2048); // Clave de 2048 bits
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Crear instancia de PublicKeyCipher
            PublicKeyCipher cipher = new PublicKeyCipher(algorithm, 2048);

            // Archivo binario de prueba para cifrar y descifrar
            String inputFilePath = "scan.pdf"; // Puedes reemplazar este archivo con el que desees

            // Cifrar archivo binario
            System.out.println("Cifrando archivo binario...");
            String encryptFileName= cipher.encryptFile(inputFilePath, publicKey);
            System.out.println("Archivo cifrado generado: " + encryptFileName);

            // Descifrar archivo binario
            System.out.println("Descifrando archivo binario...");
            String decryptFileName = cipher.decryptFile(inputFilePath + ".rsa", privateKey);
            System.out.println("Archivo descifrado generado: " + decryptFileName);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
