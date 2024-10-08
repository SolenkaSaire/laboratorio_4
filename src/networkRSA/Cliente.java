package networkRSA;

import publickeycipher.PublicKeyCipher;
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
import java.security.*;
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
        System.out.println("Client is running ... connecting the server in " + this.server + ":" + this.port);
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
        String filename = "test.txt";
        sendEncryptedFile(filename, socket);

        System.out.println("Client: Receiving encrypted file from server...");
        receiveEncryptedFile(socket);
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
        String encryptedFilename = Files.receiveFile("clientReceiver", socket);

        // Recibir clave pública del transmisor
        byte[] publicKeyBytes = (byte[]) Objects.receiveObject(socket);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        String hashFilename = Files.receiveFile("clientReceiver", socket);

        // Descifrar el archivo usando la clave pública
        PublicKeyCipher cipher = new PublicKeyCipher("RSA");
       String fileNameDecrypted =  cipher.decryptFile(encryptedFilename , publicKey);

     //   Hasher.generateIntegrityFile(decryptedFilename, hashFilename);
        Hasher.generateIntegrityFile(fileNameDecrypted, hashFilename);
        System.out.println("Client: File received and integrity verified.");
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
