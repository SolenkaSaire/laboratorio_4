package network;

import integrity.Hasher;
import persistencia.Usuario;
import symmetriccipher.SecretKeyManager;
import util.Base64;
import util.Objects;
import util.Util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

import static util.Util.pathToDecrypted;
import static util.Util.pathToEncrypted;

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
        Usuario usuario = new Usuario("cristian",10000.0);
        //codificar objeto usuario en base64 con nombre de llave y saldo de valor
        byte[] userBA= Util.objectToByteArray(usuario);
        String userB64 = Base64.encode(userBA);
        System.out.println(userB64);        Objects.sendObject(userB64, clientSideSocket);

        //recibir mensaje del servidor
        String mensajeB64 = (String) Objects.receiveObject( clientSideSocket);

        System.out.println("[Server]: " + mensajeB64 );
        byte[] mensajeBA2 = Base64.decode(mensajeB64);
        String mensaje = (String)  Util.byteArrayToObject(mensajeBA2);

        System.out.println(mensaje);
        clientSideSocket.close();
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


    /*LAB 4 punto 2*/
    public static String encryptBinaryFile(String filename) throws Exception {
        SecretKey secretKey = SecretKeyManager.loadKey();
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathToEncrypted(filename)))) {

            byte[] buffer = new byte[BLOCK_SIZE];
            int bytesRead;

            // se lee el archivo por bloques de 512 bytes para encriptar
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                //se encripta el bloque en base 64
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

        return filename + ".encrypted";
    }


    public static String decryptBinaryFile(String filename) throws Exception {
        SecretKey secretKey = SecretKeyManager.loadKey();
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);



        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));
             BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathToDecrypted(filename)))) {

            byte[] buffer = new byte[BLOCK_SIZE + 8];
            int bytesRead;

            // se lee el archivo por bloques de 512 bytes para desencriptar
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // se desencripta el bloque
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

        return pathToDecrypted(filename);
    }

    /**/

}
