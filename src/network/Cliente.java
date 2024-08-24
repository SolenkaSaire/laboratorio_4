package network;

import integrity.Hasher;
import persistencia.Usuario;
import util.Base64;
import util.Objects;
import util.Util;

import java.net.Socket;

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
        System.out.println(userB64);

        Objects.sendObject(userB64, clientSideSocket);
        String mensaje = (String) Objects.receiveObject( clientSideSocket);
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

}
