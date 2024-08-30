package symmetriccipher;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class SecretKeyManager  {

    private static final String KEY_FILE = "secretKey.key";

    public static void generateAndStoreKey() throws IOException, NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        SecretKey secretKey = keyGen.generateKey();
        try (FileOutputStream keyOut = new FileOutputStream(KEY_FILE)) {
            keyOut.write(secretKey.getEncoded());
        }
    }

    public static SecretKey loadKey() throws IOException {
        byte[] keyBytes = new byte[8];
        try (FileInputStream keyIn = new FileInputStream(KEY_FILE)) {
            keyIn.read(keyBytes);
        }
        return new SecretKeySpec(keyBytes, "DES");
    }

    public static void main(String[] args) throws Exception {
        generateAndStoreKey();  // Ejecutar una vez para generar y almacenar la clave
    }
}