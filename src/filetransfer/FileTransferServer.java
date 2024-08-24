package filetransfer;

import java.net.ServerSocket;
import java.net.Socket;

import echo.EchoServer;
import integrity.Hasher;
import util.Files;

public class FileTransferServer {
    public static final int PORT = 4000;

    private ServerSocket listener;
    private Socket serverSideSocket;

    private int port;

    public FileTransferServer() {
        System.out.println("Carlos E. Gomez - May 20/2024");
        System.out.println("File transfer server is running on port: " + this.port);
    }

    public FileTransferServer(int port) {
        this.port = port;
        System.out.println("Carlos E. Gomez - May 20/2024");
        System.out.println("File transfer server is running on port: " + this.port);
    }

    private void init() throws Exception {
        listener = new ServerSocket(PORT);

        while (true) {
            serverSideSocket = listener.accept();

            protocol(serverSideSocket);
        }
    }

    public void protocol(Socket socket) throws Exception {
        String folderNameToSend = "prueba1";
        String folderNameToReceive = "Docs";

       Files.receiveFolder(folderNameToReceive, socket);
        Files.sendFolder(folderNameToSend, socket);

        Files.pause(500);

        Hasher.generateIntegrityFile( folderNameToSend, "Download/sha256sum.txt");
    }

    public static void main(String[] args) throws Exception{

        FileTransferServer fts = null;
        if (args.length == 0) {
            fts = new FileTransferServer();
        } else {
            int port = Integer.parseInt(args[0]);
            fts = new FileTransferServer(port);
        }
        fts.init();


    }
}
