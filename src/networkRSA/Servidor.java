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
        String filename = "scan.pdf";
        sendEncryptedFile(filename, socket);
    }

    public static void sendEncryptedFile(String filename, Socket socket) throws Exception {
        String algorithm = "RSA";
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(2048); // Clave de 2048 bits
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Crear instancia de PublicKeyCipher
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm, 2048);

        // Cifrar archivo binario
        System.out.println("Cifrando archivo binario...");
        String fileNameEncrypted = cipher.encryptFile(filename, privateKey);
        System.out.println("Archivo cifrado generado: " + fileNameEncrypted);

        // Enviar archivo cifrado
        Files.sendFile(fileNameEncrypted, socket);

        // Enviar clave pública
        Objects.sendObject(publicKey.getEncoded(), socket);

        // Generar y enviar archivo hash
        String hashFilename = fileNameEncrypted+".hash";
        Hasher.generateIntegrityCheckerFile(filename, hashFilename);
        Files.sendFile(hashFilename, socket);

    }

    public static void receiveEncryptedFile(Socket socket) throws Exception {
        // Recibir archivo cifrado
        String encryptedFilename = Files.receiveFile("serverReceiver", socket);

        // Recibir clave pública del transmisor
        byte[] publicKeyBytes = (byte[]) Objects.receiveObject(socket);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        String hashFilename = Files.receiveFile("serverReceiver", socket);

        // Descifrar el archivo usando la clave pública
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
        String fileNameDecrypted =  cipher.decryptFile(encryptedFilename , publicKey);

        //   Hasher.generateIntegrityFile(decryptedFilename, hashFilename);
        Hasher.generateIntegrityFile(fileNameDecrypted, hashFilename);
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
