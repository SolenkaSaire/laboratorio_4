package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
    public static final String SERVER = "localhost";
    public static final int PORT = 3400;

    private static final Scanner SCANNER = new Scanner(System.in);

    private PrintWriter toNetwork;
    private BufferedReader fromNetwork;
    private Socket clientSideSocket;
    private String server;
    private int port;

    public EchoClient() {
        this.server = SERVER;
        this.port = PORT;
        System.out.println("Carlos E. Gomez - Apr 1/2024");
        System.out.println("Echo client is running ... connecting the server in "+ this.server + ":" + this.port);
        System.out.println("Other usage: EchoClient host port. Ex: EchoClient localhost 3600");
    }

    public EchoClient(String server, int port) {
        this.server = server;
        this.port = port;
        System.out.println("Carlos E. Gomez - Apr 1/2024");
        System.out.println("Echo client is running ... connecting the server in "+ this.server + ":" + this.port);
    }


    private void createStreams(Socket socket) throws IOException {
        toNetwork = new PrintWriter(socket.getOutputStream(), true);
        fromNetwork = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void protocol(Socket socket) throws Exception {
        createStreams(socket);

        System.out.print("Enter a message: ");
        String fromUser = SCANNER.nextLine();

        toNetwork.println(fromUser);

        String fromServer = fromNetwork.readLine();
        System.out.println("[Client] From server: " + fromServer);

        System.out.println("[Client] Finished.");
    }

    public void init() throws Exception {
        clientSideSocket = new Socket(this.server, this.port);

        protocol(clientSideSocket);

        clientSideSocket.close();
    }

    public static void main(String args[]) throws Exception {
        EchoClient ec = null;
        if (args.length == 0) {
            ec = new EchoClient();

        } else {
            String server = args[0];
            int port = Integer.parseInt(args[1]);
            ec = new EchoClient(server, port);
        }
        ec.init();
    }
}