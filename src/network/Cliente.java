package network;

import integrity.Hasher;
import persistencia.Usuario;
import symmetriccipher.SecretKeyManager;
import util.Files;
import util.Base64;
import util.Objects;
import util.Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static util.Util.*;

public class Cliente {

    public static final int PORT = 4001;
    public static final String SERVER = "localhost";

    private static final int BLOCK_SIZE = 512;
    private Socket clientSideSocket;

    private String server;
    private int port;

    public Cliente() {
        this.server = SERVER;
        this.port = PORT;
        System.out.println("Client started ");
        System.out.println("Client is running  ... connecting to the server in " + this.server + ":" + this.port);
    }

    public Cliente(String server, int port) {
        this.server = server;
        this.port = port;
        System.out.println("Client started");
        System.out.println("Client is running ... connecting the server in "+ this.server + ":" + this.port);
    }

    public void init() throws Exception {
        clientSideSocket = new Socket(server, port);
        System.out.println("Connected to the server");
        //iniciar comunicacion para enviar archivos cifrados a traves de la red
        protocol(clientSideSocket);
        clientSideSocket.close();
    }

    public void protocol(Socket socket) throws Exception {
        System.out.println("Client: Sending encrypted file to server...");
        String filename = "Eiffel.jpg";
        sendEncryptedFile(filename, socket);

        System.out.println("Client: Receiving encrypted file from server...");
        receiveEncryptedFile(socket);
    }

    public static void sendEncryptedFile(String filename, Socket socket) throws Exception {
        SecretKey secretKey = SecretKeyManager.loadKey();
        String encryptedFilename = encryptTextFile(filename, secretKey);

        Files.sendFile(encryptedFilename, socket);
        Objects.sendObject(secretKey.getEncoded(), socket);

        String hashFilename = encryptedFilename + ".hash";
        Hasher.generateIntegrityCheckerFile(filename, hashFilename);
        Files.sendFile(hashFilename, socket);
    }

    public static void receiveEncryptedFile(Socket socket) throws Exception {
        String encryptedFilename = Files.receiveFile("clientReceiver", socket);
        byte[] keyBytes = (byte[]) Objects.receiveObject(socket);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "DES");

        // Adding a small delay to ensure the next file is fully sent
        Thread.sleep(1000);

        String hashFilename = Files.receiveFile("clientReceiver", socket);

        String decryptedFilename = decryptTextFile(encryptedFilename, secretKey);
        Hasher.generateIntegrityFile(decryptedFilename, hashFilename);
        System.out.println("Client: File received and integrity verified.");
    }

    public static String encryptTextFile(String filename, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        String encryptedFilename = filename + ".encrypted";
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(encryptedFilename))) {

            byte[] buffer = new byte[512];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] encryptedData = cipher.update(buffer, 0, bytesRead);
                if (encryptedData != null) {
                    outputStream.write(encryptedData);
                }
            }
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                outputStream.write(finalBlock);
            }
        }

        return encryptedFilename;
    }

    public static String decryptTextFile(String filename, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        String decryptedFilename = "clientReceiver/" + new File(filename).getName().replace(".encrypted", "");
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(decryptedFilename))) {

            byte[] buffer = new byte[512 + 8];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byte[] decryptedData = cipher.update(buffer, 0, bytesRead);
                if (decryptedData != null) {
                    outputStream.write(decryptedData);
                }
            }
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                outputStream.write(finalBlock);
            }
        }
        return decryptedFilename;
    }

        public static void main(String[] args) throws Exception {
        Cliente ftc;
        if (args.length == 0) {
            ftc = new Cliente();
        } else {
            String server = args[0];
            int port = Integer.parseInt(args[1]);
            ftc = new Cliente(server, port);
        }
        ftc.init();
    }

}
