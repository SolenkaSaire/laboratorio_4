package util;

import persistencia.Person;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Base64Tester02 {
    public static void main(String[] args) throws Exception {
        Person person = new Person("John", 25,"1.83" );

        System.out.println(person);

        byte[] nameBA= Util.objectToByteArray(person);
        String nameB64 = Base64.encode(nameBA);
        System.out.println(nameB64);

        byte[] nameBA2 = Base64.decode(nameB64);
        Person names2 = (Person)  Util.byteArrayToObject(nameBA2);
        System.out.println(names2);
    }


}
