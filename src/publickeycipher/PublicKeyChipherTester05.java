package publickeycipher;

import persistencia.Person;
import util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class PublicKeyChipherTester05 {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException {
        // Recuperar las llaves desde los archivos
        String algorithm = "RSA";
        PublicKey publicKey = Util.loadPublicKeyFromFile("publicKey.pem");
        PrivateKey privateKey = Util.loadPrivateKeyFromFile("privateKey.key");
        PublicKeyCipher cipher = new PublicKeyCipher(algorithm);

        Person person = new Person("Cristian", 23, "172");

        byte[] encryptedPerson = cipher.encryptObject(person, publicKey);
        System.out.println(Util.byteArrayToHexString(encryptedPerson, " "));


        Person decryptedPerson = (Person) cipher.decryptObject(encryptedPerson, privateKey);
        System.out.println(decryptedPerson);
    }
}
