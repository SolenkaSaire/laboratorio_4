package util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;

import symmetriccipher.SecretKeyManager;
import symmetriccipher.SymmetricCipher;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Util {


    /*LAB 4 encriptando y descencriptando archivos de texto*/
    public static String encryptTextFile(String filename) throws Exception {
        String contenido = readFileAsString(filename);
       SecretKey secretKey = SecretKeyManager.loadKey();
        //SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();

        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");
        String[] linea = contenido.split("\n");
        StringBuilder encryptedB64 = new StringBuilder();
        for (int i = 0; i < linea.length; i++) {
            byte[] encryptedText = cipher.encryptMessage(linea[i]);
            encryptedB64.append(Base64.encode(encryptedText));
            if (i < linea.length - 1) {
                encryptedB64.append("\n");
            }
        }
        String path = pathToEncrypted(filename);
        //writeStringToFile(encryptedB64.toString(), path);
        WriteToFile(path, encryptedB64.toString(), false);
        return path;
    }

    private static String pathToEncrypted(String path) {
        return path + ".encrypted";
    }


    public static String decryptTextFile(String filename) throws Exception {
        String contenido = readFileAsString(filename);
        SecretKey secretKey = SecretKeyManager.loadKey();
        //SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();

        SymmetricCipher cipher = new SymmetricCipher(secretKey, "DES/ECB/PKCS5Padding");
        System.out.println(contenido);
        String[] linea = contenido.split("\n");
        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < linea.length; i++) {
            System.out.println(linea[i]);
            byte[] decryptedBytes = Base64.decode(linea[i]);
            decryptedText.append(cipher.decryptMessage(decryptedBytes));
            if (i < linea.length - 1) {
                decryptedText.append("\n");
                    
            }
            System.out.println(decryptedText.toString()) ;
        }
        String path = pathToDecrypted(filename);
        //writeStringToFile(decryptedText.toString(), path);
        WriteToFile(path, decryptedText.toString(), false);
        return path;
    }

    private static String pathToDecrypted(String path) {
        String[] r = path.split("\\.");
        return r[0] + ".plain." + r[r.length - 2];
    }

    public static String readFileAsString(String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /*FIN LAB4*/
    public static String byteArrayToHexString(byte[] bytes, String separator) {
        String resultado = "";
        for (int i = 0; i < bytes.length; i++) {
            resultado += String.format("%02x", bytes[i]) + separator;
        }
        return resultado;
    }

    //metodo para verificar si un hash es o no hexadecimal
    public static boolean isHexadecimal(String hash) {
        return hash.matches("[0-9a-fA-F]+");
    }

    //metodo para guardar un objeto en un archivo
    public static void saveObject(Object o, String fileName) throws IOException {
        FileOutputStream fileOut;
        ObjectOutputStream out;

        fileOut = new FileOutputStream(fileName);
        out = new ObjectOutputStream(fileOut);

        out.writeObject(o);

        out.flush();
        out.close();
    }

    public static Object loadObject(String fileName) throws IOException, ClassNotFoundException, InterruptedException {
        FileInputStream fileIn;
        ObjectInputStream in;

        fileIn = new FileInputStream(fileName);
        in = new ObjectInputStream(fileIn);

        Thread.sleep(100);
//		try {
//
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

        Object o = in.readObject();

        fileIn.close();
        in.close();

        return o;
    }


    public static void WriteToFile(String filename, String line, boolean append) throws Exception {
        File file = new File(filename);

        if (file.exists() == false) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, append);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(line);
        bw.newLine();

        bw.close();
    }

    public static void printByteArrayInt(byte[] byteArray) {
        System.out.println("{" + byteArrayIntToString(byteArray) + "}");
    }

    public static String byteArrayIntToString(byte[] byteArray) {
        String out = "";
        int i = 0;
        for (; i < byteArray.length - 1; i++) {
            if (i % 10 == 0 && i != 0)
                out += "\n";
            out += byteArray[i] + 128 + "\t";
        }
        out += byteArray[i] + 128;

        return out;
    }

    public static Object byteArrayToObject(byte[] byteArray) throws IOException, ClassNotFoundException {
        // se crea un objeto de tipo ByteArrayInputStream que es el que se encarga de leer los bytes
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        Object o = in.readObject();
        in.close();

        return o;
    }

    public static byte[] objectToByteArray(Object o) throws IOException {
        // se crea un objeto de tipo ByteArrayOutputStream que es el que se encarga de almacenar los bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // se crea un objeto de tipo ObjectOutputStream que es el que se encarga de escribir el objeto en el arreglo de bytes
        ObjectOutputStream out = new ObjectOutputStream(bos);
        // se escribe el objeto en el arreglo de bytes
        out.writeObject(o);
        out.close();
        byte[] buffer = bos.toByteArray();

        return buffer;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value
        };
    }

    public static int byteArrayToInt(byte[] byteArray) {
        return byteArray[0] << 24 | (byteArray[1] & 0xFF) << 16 | (byteArray[2] & 0xFF) << 8 | (byteArray[3] & 0xFF);
    }


}
