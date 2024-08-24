package util;

import java.util.ArrayList;

public class Base64Tester01 {
    public static void main(String[] args) throws Exception {
        ArrayList<String> names = new ArrayList<String>();
        names.add("Ana");
        names.add("Bibiana");
        names.add("Carolina");
        names.add("Diana");
        names.add("Elena");

        System.out.println(names);

        byte[] nameBA= Util.objectToByteArray(names);
        String nameB64 = Base64.encode(nameBA);
        System.out.println(nameB64);

        byte[] nameBA2 = Base64.decode(nameB64);
        ArrayList<String> names2 = (ArrayList<String>)  Util.byteArrayToObject(nameBA2);
        System.out.println(names2);
    }
}
