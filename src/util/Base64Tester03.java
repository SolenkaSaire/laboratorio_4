package util;
import persistencia.Person;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Base64Tester03 {
    public static void main(String[] args) throws Exception {
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("Sol",29,"1.68"));
        personList.add(new Person("Cristian",28,"1.77"));
        personList.add(new Person("Allison",27,"1.75"));

        System.out.println(personList);

        byte[] personBA = Util.objectToByteArray(personList);
        String personB64 = Base64.encode(personBA);
        System.out.println(personB64);

        byte[] namesBA2 = Base64.decode(personB64);
        List<Person> names2 = (List<Person>) Util.byteArrayToObject(namesBA2);
        System.out.println(names2);
    }
}