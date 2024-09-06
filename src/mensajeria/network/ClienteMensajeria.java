package mensajeria.network;

import mensajeria.persistence.KeyPersistenceManager;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ClienteMensajeria {
    public static final String SERVER = "localhost";
    public static final int PORT = 3500;

    private static final Scanner SCANNER = new Scanner(System.in);

    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;
    private Socket clientSideSocket;

    private String server;
    private int port;

    public ClienteMensajeria() {
        this.server = SERVER;
        this.port = PORT;
        System.out.println("Client is running ... Connecting the server in " + this.server + ":" + this.port);

    }

    public ClienteMensajeria(String server, int port) {
        this.server = server;
        this.port = port;
        System.out.println("Echo client is running ... connecting the server in " + this.server + ":" + this.port);
    }

    private void createStreams(Socket socket) throws IOException {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void registerUser(String username) throws Exception {
        if (KeyPersistenceManager.existeLlave(username + ".public")) {
            System.out.println("[Client] El usuario " + username + " ya existe.");
            return;
        }

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        KeyPersistenceManager.guardarLlave(username + ".public", publicKey.getEncoded());
        KeyPersistenceManager.guardarLlave(username + ".private", privateKey.getEncoded());

        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String message = "REGISTRAR " + username + " " + publicKeyBase64;
        toNetwork.println(message);

        String fromServer = fromNetwork.readLine();
        System.out.println("[Client] From server: " + fromServer);
    }


    private String getPublicKey(String username) throws IOException {
        String message = "OBTENER_LLAVE_PUBLICA " + username;
        toNetwork.println(message);

        String fromServer = fromNetwork.readLine();

        System.out.println("[Client] From server: " + fromServer);
        return fromServer;
    }

    private void sendMessage(String toUsername, String message, String key) throws Exception {
        if (key.contains("ERROR")) {
            System.out.println("[Client] From server: " + key);
            return;
        }
        System.out.println("1");

        String[] parts = key.split(":");
        String publicKeyBase64 = parts[1];
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        System.out.println("2");

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        String encryptedMessageBase64 = Base64.getEncoder().encodeToString(encryptedMessage);


        String sendMessage = "ENVIAR " + toUsername + " " + encryptedMessageBase64;
        toNetwork.println(sendMessage);


        key = fromNetwork.readLine();
        System.out.println("[Client] From server: " + key);
    }

    private void readMessages(String username) throws Exception {

        String message = "LEER " + username;
        toNetwork.println(message);

        String fromServer = fromNetwork.readLine();
        System.out.println("[Client] From server: " + fromServer);

        if (fromServer.startsWith("ERROR") || fromServer.contains("0")) {
            return;
        }

        byte[] privateKeyBytes = KeyPersistenceManager.leerLlave(username + ".private");
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        //System.out.println(privateKey);
        String[] cantidad = fromServer.split(" ");
        int i = 0;
        int max = Integer.parseInt(cantidad[cantidad.length - 2]);
        while (i < max) {
            fromServer = fromNetwork.readLine();
            System.out.println(fromServer);
            byte[] encryptedMessage = Base64.getDecoder().decode(fromServer);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedMessage = cipher.doFinal(encryptedMessage);
            System.out.println("[Client] Decrypted message: " + new String(decryptedMessage));
            i++;
        }
        System.out.println("se acabo :)");
    }

    private void protocol() throws Exception {


        System.out.println("Commands: ");
        System.out.println("1. Register user: REGISTER <username>");
        System.out.println("2. Get public key: GET_PUBLIC_KEY <username>");
        System.out.println("3. Send message: SEND_MESSAGE <to_username> <message>");
        System.out.println("4. Read messages: READ <username>");


            System.out.print("Enter command: ");
            String command = SCANNER.nextLine();
            clientSideSocket = new Socket(this.server, this.port);
            createStreams(clientSideSocket);

            if (command.startsWith("REGISTER ")) {
                String username = command.split(" ")[1];
                registerUser(username);
            } else if (command.startsWith("GET_PUBLIC_KEY ")) {
                String username = command.split(" ")[1];
                getPublicKey(username);
            } else if (command.startsWith("SEND_MESSAGE ")) {
                String[] parts = command.split(" ", 3);
                String toUsername = parts[1];
                String message = parts[2];
                String key = getPublicKey(toUsername);
                clientSideSocket = new Socket(this.server, this.port);
                createStreams(clientSideSocket);
                sendMessage(toUsername, message,key );
            } else if (command.startsWith("READ ")) {
                String username = command.split(" ")[1];
                readMessages(username);
            } else {
                toNetwork.println(command);
                String fromServer = fromNetwork.readLine();
                System.out.println("[Client] From server: " + fromServer);
            }
        clientSideSocket.close();


        System.out.println("[Client] Finished.");
    }

    public void init() throws Exception {


            while (true) {
                protocol();
            }

        }

    public static void main(String args[]) throws Exception {
        ClienteMensajeria ec = null;
        if (args.length == 0) {
            ec = new ClienteMensajeria();
        } else {
            String server = args[0];
            int port = Integer.parseInt(args[1]);
            ec = new ClienteMensajeria(server, port);
        }
        ec.init();
    }
}