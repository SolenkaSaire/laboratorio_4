package publickeycipher;

import util.Base64;
import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

public class PublicKeyCipher {

    private Cipher cipher;
    private int keySize;

    public PublicKeyCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance(algorithm);
        this.keySize = 0;
    }

    public PublicKeyCipher(String algorithm, int keySize) throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance(algorithm);
        this.keySize = keySize;
    }

    // Método para obtener el tamaño máximo de caracteres que se pueden cifrar
    private int getMaxBlockSize() {
        // Depende del tamaño de la clave
        if (keySize == 1024) {
            return 110;
        } else if (keySize == 2048) {
            return 240;
        } else if (keySize == 3072) {
            return 370;
        } else if (keySize == 4096) {
            return 500;
        } else {
            // Si no es un tamaño estándar, devuelve un valor por defecto
            return keySize / 8 - 11;  // Esto tiene en cuenta el relleno PKCS#1
        }
    }

    public byte[] encryptMessage(String input, Key key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] ciphertext = null;
        byte[] cleartext = input.getBytes();

        cipher.init(Cipher.ENCRYPT_MODE, key);
        ciphertext = cipher.doFinal(cleartext);

        return ciphertext;
    }

    public String decryptMessage(byte[] input, Key key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String output = null;

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] ciphertext = cipher.doFinal(input);
        output = new String(ciphertext);

        return output;
    }

    public byte[] encryptObject(Object input, Key key) throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] clearObject = Util.objectToByteArray(input);
        byte[] cipherObject = cipher.doFinal(clearObject);

        return cipherObject;
    }

    public Object decryptObject(byte[] input, Key key) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException, IOException {
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] clearText = cipher.doFinal(input);
        Object output = Util.byteArrayToObject(clearText);

        return output;
    }

    // Método para encriptar un archivo de texto línea por línea
    public void encryptTextFile(String inputFilePath, PublicKey publicKey) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePath + ".rsa"))) {

            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            String line;

            while ((line = reader.readLine()) != null) {
                byte[] encryptedBytes = cipher.doFinal(line.getBytes());
                String encodedString = Base64.getEncoder().encodeToString(encryptedBytes);
                writer.write(encodedString);
                writer.newLine();
            }
        }
    }

    // Método para desencriptar un archivo de texto línea por línea
    public void decryptTextFile(String inputFilePath, PrivateKey privateKey) throws Exception {
        String outputFilePath = inputFilePath.replace(".rsa", ".plain.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            String line;

            while ((line = reader.readLine()) != null) {
                byte[] decodedBytes = Base64.decode(line);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                writer.write(new String(decryptedBytes));
                writer.newLine();
            }
        }
    }

}