package networkRSA;

import publickeycipher.PublicKeyCipher;
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
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
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
        String filename = "test.txt";
        sendEncryptedFile(filename, socket);
    }

    public static void sendEncryptedFile(String filename, Socket socket) throws Exception {
        // Generar clave pública y privada del servidor
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Cifrar archivo con la clave privada
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
        cipher.encryptTextFile(filename, privateKey);

        // Enviar archivo cifrado
        String encryptedFilename = filename + ".rsa";
        Files.sendFile(encryptedFilename, socket);

        // Enviar la clave pública del servidor al cliente
        Objects.sendObject(publicKey.getEncoded(), socket);

        // Generar y enviar el archivo hash
        String hashFilename = encryptedFilename + ".hash";
        Hasher.generateIntegrityCheckerFile(filename, hashFilename);
        Files.sendFile(hashFilename, socket);
    }


    public static void receiveEncryptedFile(Socket socket) throws Exception {
        // Recibir archivo cifrado desde el cliente
        String encryptedFilename = Files.receiveFile("serverReceiver", socket);

        // Recibir la clave pública del cliente
        byte[] publicKeyBytes = (byte[]) Objects.receiveObject(socket);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        // Recibir archivo hash
        String hashFilename = Files.receiveFile("serverReceiver", socket);

        // Verificar integridad
        String decryptedFilename = encryptedFilename.replace(".rsa", ".plain.txt");

        Hasher.generateIntegrityFile(decryptedFilename, hashFilename);
        System.out.println("Client: File received and integrity verified.");
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
