package mensajeria.network;

import mensajeria.controller.UsuarioController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMensajeria {
    private UsuarioController controller;

    public static final int PORT = 3500;

    private ServerSocket listener;
    private Socket serverSideSocket;

    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;

    private int port;

    public ServidorMensajeria() {
        this.port = PORT;
        System.out.println("Echo server is running on port: " + this.port);
    }

    public ServidorMensajeria(int port) {
        this.port = port;
        System.out.println("Echo server is running on port: " + this.port);
    }

    private void createStreams(Socket socket) throws IOException {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void protocol(Socket socket) throws IOException, InterruptedException {
        createStreams(socket);

        String request = fromNetwork.readLine();
        System.out.println("[Server] From client: " + request);
        String[] parts = request.split(" ");
        switch (parts[0]){
            case ("REGISTRAR"): {
                String answer = controller.registrarUsuario(parts[1], parts[2]);
                toNetwork.println(answer);
                break;
            }
            case ("OBTENER_LLAVE_PUBLICA"): {
                String answer = controller.obtenerLlavePublica(parts[1]);
                toNetwork.println(answer);
                break;
            }
            case ("ENVIAR"): {
                String answer = controller.enviarMensaje(parts[1], parts[2]);
                toNetwork.println(answer);
                break;
            }
            case ("LEER"): {
                String answer = controller.leerMensajes(parts[1]);
                toNetwork.println(answer);
                Thread.sleep(1000);
                for (String message: controller.obtenerBuzon(parts[1])) {
                    toNetwork.println(message);
                }
                break;
            }
            default: toNetwork.println("ERROR. COMANDO NO EXISTENTE");
        }
        System.out.println("[Server] Waiting for a new client.");
    }

    private void init() throws IOException, InterruptedException {
        controller = new UsuarioController();
        listener = new ServerSocket(this.port);

        while (true) {
            try{
                serverSideSocket = listener.accept();

                String ip = serverSideSocket.getInetAddress().getHostAddress();
                int port = serverSideSocket.getPort();
                System.out.println("Client IP addres: " + ip);
                System.out.println("Client number port: " + port);

                protocol(serverSideSocket);
            }catch (Exception e){
                System.out.println("Sesion caida");
            }
        }
    }

    public static void main(String args[]) throws Exception {
        ServidorMensajeria es = null;
        if (args.length == 0) {
            es = new ServidorMensajeria();
        } else {
            int port = Integer.parseInt(args[0]);
            es = new ServidorMensajeria(port);
        }
        es.init();
    }
}