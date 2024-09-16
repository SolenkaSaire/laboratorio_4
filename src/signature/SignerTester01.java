package signature;

import util.Util;

import java.security.*;
import java.util.Base64;

public class SignerTester01 {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //se define el algoritmo de firma
        String algorithm = "SHA256withRSA";

        //se genera un par de claves RSA con una longitud de 2048 bits
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);

        //se obtiene el par de claves en un objeto KeyPair
        KeyPair pair = keyPairGen.generateKeyPair();

        //se obtiene la clave privada y la clave publica del par de claves
        PrivateKey privateKey = pair.getPrivate();//clave privada del emisor
        PublicKey publicKey = pair.getPublic();//clave publica del emisor

        String message = "fundamentos de seguridad digital";
        //se firma el mensaje con la clave privada del emisor y se obtiene la firma digital en bytes
        byte[] digitalSignature = Signer.signMessage(message, algorithm, privateKey);
        //System.out.println(Util.byteArrayToHexString(digitalSignature));
        System.out.println(Base64.getEncoder().encodeToString(digitalSignature));

        boolean isVerified = Signer.verifyMessageSignature(message, algorithm, publicKey, digitalSignature);
        System.out.println("Firma verificada: " + isVerified);
    }

}