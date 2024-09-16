package signature;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.util.Base64;

public class Signer2 {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, SignatureException, InvalidKeyException {
        String inputDirectoryPath = "src/signature/files";

        String algorithm = "SHA256withRSA";
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        generateSignaturesFile(inputDirectoryPath, algorithm, privateKey);
    }

    public static byte[] signMessage(String message, String algorithm, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }

    public static boolean verifyMessageSignature(String message, String algorithm,
                                                 PublicKey publicKey, byte[] digitalSignature)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        return signature.verify(digitalSignature);
    }


    public static byte[] signFile(String filename, String algorithm, PrivateKey privateKey) throws
            IOException,
            NoSuchAlgorithmException,
            SignatureException,
            InvalidKeyException {
        File file = new File(filename);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update(fileBytes);
        return signature.sign();
    }

    public static boolean verifyFileSignature(String filename, String algorithm, PublicKey publicKey, byte[] digitalSignature)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        File file = new File(filename);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(fileBytes);
        return signature.verify(digitalSignature);
    }

    public static void generateSignaturesFile(String directoryPath,
                                              String algorithm, PrivateKey privateKey)
            throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files == null) {
            throw new IOException("No se encontraron archivos en el directorio.");
        }

        // Crear archivo de firmas digitales dentro del mismo directorio
        File signaturesFile = new File(directoryPath, "signatures.txt");
        if (signaturesFile.exists()) {
            System.out.println("El archivo de firmas digitales ya existe. Se sobrescribirá.");
        }

        try (FileWriter writer = new FileWriter(signaturesFile)) {
            for (File file : files) {
                // Excluir el archivo de firmas digitales de la generación de firmas
                if (file.isFile() && !file.getName().equals("signatures.txt")) {
                    byte[] signature = Signer.signFile(new File(file.getAbsolutePath()), algorithm, privateKey);
                    String encodedSignature = Base64.getEncoder().encodeToString(signature);
                    writer.write(file.getName() + "::" + encodedSignature + System.lineSeparator());
                }
            }
        }
    }
    public static boolean verifySignaturesFile(String directoryPath,
                                               String algorithm, PublicKey publicKey)
            throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        File directory = new File(directoryPath);
        File signaturesFile = new File(directory, "signatures.txt");
        if (!signaturesFile.exists()) {
            throw new IOException("El archivo de firmas digitales no existe.");
        }

        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("No se encontraron archivos en el directorio.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(signaturesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length != 2) {
                    throw new IOException("Formato inválido en el archivo de firmas.");
                }
                String fileName = parts[0];
                String encodedSignature = parts[1];
                byte[] digitalSignature = Base64.getDecoder().decode(encodedSignature);

                File file = new File(directoryPath, fileName);
                if (!file.isFile()) {
                    System.out.println("Archivo no encontrado: " + fileName);
                    return false;
                }

                if (!Signer.verifyFileSignature(new File(file.getAbsolutePath()), algorithm, publicKey, digitalSignature)) {
                    System.out.println("Verificación de firma fallida para el archivo: " + fileName);
                    return false;
                }
            }
        }
        return true;
    }


}