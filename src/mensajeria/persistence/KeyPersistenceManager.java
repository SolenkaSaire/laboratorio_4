package mensajeria.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KeyPersistenceManager {

    private static final String PERSISTENCE_PATH = "src/mensajeria/persistence/";

    public static void guardarLlave(String nombreArchivo, byte[] llave) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(PERSISTENCE_PATH + nombreArchivo)) {
            fos.write(llave);
        }
    }

    public static byte[] leerLlave(String nombreArchivo) throws IOException {
        File archivo = new File(PERSISTENCE_PATH + nombreArchivo);
        byte[] llaveBytes = new byte[(int) archivo.length()];
        try (FileInputStream fis = new FileInputStream(archivo)) {
            fis.read(llaveBytes);
        }
        return llaveBytes;
    }

    /*
     * public static boolean existeLlave(String nombreArchivo) {
     * File archivo = new File(PERSISTENCE_PATH + nombreArchivo);
     * return archivo.exists();
     * }
     */

    public static boolean existeLlave(String username) {
        File publicKeyFile = new File(PERSISTENCE_PATH + username + ".public");
        File privateKeyFile = new File(PERSISTENCE_PATH + username + ".private");
        return publicKeyFile.exists() && privateKeyFile.exists();
    }
}
