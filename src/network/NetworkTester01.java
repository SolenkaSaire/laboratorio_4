package network;

import static util.Util.decryptTextFile;
import static util.Util.encryptTextFile;

public class NetworkTester01 {
     public static void main(String[] args) throws Exception {

            encryptTextFile("test.txt");
            decryptTextFile("test.txt.encrypted");
        }
}
