package publickeycipher;

import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class PublicKeyChipherTester04 {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Recuperar las llaves desde los archivos
        PublicKey publicKey = Util.loadPublicKeyFromFile("publicKey.pem");
        PrivateKey privateKey = Util.loadPrivateKeyFromFile("privateKey.key");

        // Recuperar el mensaje cifrado desde el archivo
        byte[] encryptedText = Util.loadEncryptedMessageFromFile("encryptedText.txt.asc");

        // Desencriptar el mensaje
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
        String decryptedText = cipher.decryptMessage(encryptedText, privateKey);

        // Imprimir el mensaje desencriptado
        System.out.println("Mensaje desencriptado: " + decryptedText);
    }
}
