package signature;


import util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.util.Base64;
import java.util.Objects;

public class Signer {

    final static String  firmasDigitales = "src/signature/firmas-digitales.txt";

    public static void main(String[] args) throws Exception {
        String inputDirectoryPath = "src/signature/files"; // Cambia esto por la ruta del directorio que deseas recorrer
        File inputDirectory = new File(inputDirectoryPath);

        if (inputDirectory.isDirectory()) {
            for (File inputFile : Objects.requireNonNull(inputDirectory.listFiles())) {
                if (inputFile.isFile()) {

                    generateSignaturesFile(inputFile);
                }
            }
        } else {
            System.err.println("El directorio de entrada especificado no es válido.");
        }
    }

    public static void generateSignaturesFile(File archivo ) throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {

        String algorithm = "SHA256withRSA";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        String filename = archivo.getName();
        byte[] digitalSignature = signFile(archivo, algorithm, privateKey);
       // String certificado = filename + " => " + Util.byteArrayToHexStringLine(digitalSignature);
        String encodedSignature =  filename + " => " +Base64.getEncoder().encodeToString(digitalSignature);

        appendToFile(firmasDigitales, encodedSignature);
        boolean isVerified = Signer.verifyFileSignature(archivo, algorithm, publicKey, digitalSignature);
        System.out.println("Firma verificada: " + isVerified);

    }

    public static void appendToFile(String filename, String text) {
        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write(text);
            fw.write(System.lineSeparator()); // Añade una nueva línea después del texto
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] signMessage(String message, String algorithm, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }
    public static boolean verifyMessageSignature(String message, String algorithm, PublicKey publicKey, byte[] digitalSignature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        return signature.verify(digitalSignature);
    }

    // Parte 2 - firma de archivos binarios y verificacion de firma digital

    public static byte[] signFile(File file, String algorithm, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(fileBytes);
        return signature.sign();
    }

    public static boolean verifyFileSignature(File file, String algorithm, PublicKey publicKey, byte[] digitalSignature)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {

        byte[] fileBytes = Files.readAllBytes(file.toPath());
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(fileBytes);
        return signature.verify(digitalSignature);
    }

}