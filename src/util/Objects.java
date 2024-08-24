package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

public class Objects {

    public static void sendObject(Object object, Socket socket) throws Exception {
        byte[] objectBA = Util.objectToByteArray(object);
        BufferedOutputStream toNetwork = new BufferedOutputStream(socket.getOutputStream());

        // Enviar el tamaño del objeto primero
        toNetwork.write(Util.intToByteArray(objectBA.length));
        toNetwork.flush();

        // Pausa para simular latencia de red
        Files.pause(500);

        // Enviar el objeto en bytes
        toNetwork.write(objectBA);
        toNetwork.flush();
        Files.pause(50);
    }

    public static Object receiveObject(Socket socket) throws Exception {
        BufferedInputStream fromNetwork = new BufferedInputStream(socket.getInputStream());

        // Leer el tamaño del objeto
        byte[] sizeBuffer = new byte[4];
        fromNetwork.read(sizeBuffer);
        int objectSize = Util.byteArrayToInt(sizeBuffer);

        // Leer el objeto en bytes
        byte[] objectBuffer = new byte[objectSize];
        fromNetwork.read(objectBuffer);

        // Convertir los bytes del objeto de nuevo a un objeto
        Object object = Util.byteArrayToObject(objectBuffer);
        return object;
    }
}
