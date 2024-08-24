package symmetriccipher;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import util.Util;

public class SymmetricCipher {
    private SecretKey secretKey;
    private Cipher cipher;

    //constructor de llave simetrica con llave secreta y transformacion
    public SymmetricCipher(SecretKey secretKey, String transformation)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.secretKey = secretKey;
        cipher = Cipher.getInstance(transformation);
    }

    //metodo para encriptar mensaje recibe mensaj a cifrar y devuelve arreglo de bytes
    public byte[] encryptMessage(String input)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        //1. se convierte el mensaje a bytes
        byte[] clearText = input.getBytes();
        byte[] cipherText = null;

        //2. se inicializa el cifrado en modo cifrado con la llave secreta
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        //3. se cifra el mensaje
        cipherText = cipher.doFinal(clearText);

        return cipherText;
    }

    //metodo para desencriptar mensaje recibe arreglo de bytes y devuelv e el mensaje descifrado
    public String decryptMessage(byte[] input)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String output = "";

        //1. se inicializa el cifrado en modo descifrado con la llave secreta
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        //2. se descifra el mensaje
        byte[] clearText = cipher.doFinal(input);
        //3. se convierte el mensaje descifrado a String
        output = new String(clearText);

        return output;
    }

    // este metodo recibe un objeto y lo cifra
    public byte[] encryptObject(Object input)
            throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // arreglo que contendra el objeto cifrado
        byte[] cipherObject = null;
        // arreglo que contendra el objeto en claro
        byte[] clearObject = null;

        // se convierte el objeto a arreglo de bytes
        clearObject = Util.objectToByteArray(input);

        // se inicializa el cifrado en modo cifrado con la llave secreta
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        // se cifra el objeto
        cipherObject = cipher.doFinal(clearObject);

        return cipherObject;
    }

    // metodo que recibe un arreglo de bytes y lo descifra
    public Object decryptObject(byte[] input) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, ClassNotFoundException, IOException {
        Object output = null;

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] clearObject = cipher.doFinal(input);

        output = Util.byteArrayToObject(clearObject);

        return output;
    }
//	public SecretKey getKey() {
//		return secretKey;
//	}
}
