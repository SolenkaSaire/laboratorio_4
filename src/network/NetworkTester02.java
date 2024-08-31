package network;

import static util.Util.decryptFile;
import static util.Util.encryptFile;

public class NetworkTester02 {

    public static void main(String[] args) throws Exception {
        //usar metodos encryptFile y decryptFile
        encryptFile("test2.txt");
        decryptFile("test2.txt.encrypted");
    }
}
