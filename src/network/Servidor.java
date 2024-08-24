package network;

import persistencia.Person;
import persistencia.Usuario;
import util.Base64;
import util.Files;
import util.Objects;
import util.Util;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

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
        }
    }

    public void protocol(Socket socket) throws Exception {
        //Usuario usuario = (Usuario) Objects.receiveObject(socket);
        //recibir cadena codificada en base64
        String usuarioB64 = (String) Objects.receiveObject(socket);

        System.out.println("[Server]: se recibió en RAW B64: " + usuarioB64 );

        byte[] nameBA2 = Base64.decode(usuarioB64);
        Usuario usuario = (Usuario)  Util.byteArrayToObject(nameBA2);

        System.out.println("[Server]: se recibió: " + usuario.getNombre() + " " + usuario.getMonto());

        String fromUser = transaccion(usuario);

        //encriptar cadena de fromUser a B64
        byte[] respuestaBA = Util.objectToByteArray(fromUser);
        String respuestaB64 = Base64.encode(respuestaBA);

        System.out.println("[Server]: se envió: " + respuestaB64);
        Objects.sendObject(respuestaB64, socket);
    }


    public static String realizarTranssacion(Usuario usuario) {
        if (usuarios.containsKey(usuario.getNombre())) {
            Double moonto = usuarios.get(usuario.getNombre());
            moonto += usuario.getMonto();
            usuarios.put(usuario.getNombre(), moonto);
            return "Transsación realziada . saldo: " + moonto;
        }
        usuarios.put(usuario.getNombre(), usuario.getMonto());
        return "Cuenta creada exitosamente :) . saldo:  " + usuario.getMonto();
    }

    public static String transaccion(Usuario usuario) {

        if (usuarios.containsKey(usuario.getNombre())) {
            Double monto = usuarios.get(usuario.getNombre());
            monto += usuario.getMonto();
            usuarios.put(usuario.getNombre(), monto);
            return "Transacción realizada . saldo: " + monto;
        } else {
            usuarios.put(usuario.getNombre(), usuario.getMonto());
            return "Cuenta creada exitosamente :) . saldo:  " + usuario.getMonto();
        }
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
