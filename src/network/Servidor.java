package network;

import integrity.Hasher;
import persistencia.Person;
import persistencia.Usuario;
import symmetriccipher.SecretKeyManager;
import util.Base64;
import util.Files;
import util.Objects;
import util.Util;
import javax.crypto.Cipher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import static util.Util.decryptFile;
import static util.Util.encryptFile;

public class Servidor {
    public static final int PORT = 4001;

    private ServerSocket listener;
    private Socket serverSideSocket;

    private PrintWriter toNetwork;
    static HashMap<String, Double> usuarios = new HashMap<>();
    private int port;

    public Servidor() {
        this.port = PORT;
        System.out.println("Server started");
        System.out.println("Server is running on port: " + this.port);
    }

    public Servidor(int port) {
        this.port = port;
        System.out.println("Server started");
        System.out.println("Server is running on port: " + this.port);
    }

    private void init() throws Exception {
        listener = new ServerSocket(PORT);
        while (true) {
            serverSideSocket = listener.accept();
            protocol(serverSideSocket);
            serverSideSocket.close();
        }
    }

    public void protocol(Socket socket) throws Exception {
        System.out.println("Server: Receiving encrypted file from client...");
        receiveEncryptedFile(socket);

        System.out.println("Server: Sending encrypted file to client...");
        String filename = "scan.pdf";
        sendEncryptedFile(filename, socket);
    }

    public static void sendEncryptedFile(String filename, Socket socket) throws Exception {
        SecretKey secretKey = SecretKeyManager.loadKey();
        String encryptedFilename = encryptFile(filename, secretKey);

        Files.sendFile(encryptedFilename, socket);
        Objects.sendObject(secretKey.getEncoded(), socket);

        String hashFilename = encryptedFilename + ".hash";
        Hasher.generateIntegrityCheckerFile(filename, hashFilename);
        Files.sendFile(hashFilename, socket);
    }

    public static void receiveEncryptedFile(Socket socket) throws Exception {
        String encryptedFilename = Files.receiveFile("serverReceiver", socket);
        byte[] keyBytes = (byte[]) Objects.receiveObject(socket);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "DES");

        Thread.sleep(1000);

        String hashFilename = Files.receiveFile("serverReceiver", socket);
        String decryptedFilename = decryptFile(encryptedFilename, secretKey);

        Hasher.generateIntegrityFile(decryptedFilename, hashFilename);
        System.out.println("Server: File received and integrity verified.");
    }

    public static void main(String[] args) throws Exception {
        Servidor fts = null;
        if (args.length == 0) {
            fts = new Servidor();
        } else {
            int port = Integer.parseInt(args[0]);
            fts = new Servidor(port);
        }
        fts.init();
    }

}
