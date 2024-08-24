package util;

import java.io.*;

public class Util {
    public static String byteArrayToHexString(byte[] bytes, String separator){
        String resultado = "";
        for (int i = 0; i < bytes.length; i++) {
            resultado += String.format("%02x", bytes[i]) + separator;
        }
        return resultado;
    }

    //metodo para verificar si un hash es o no hexadecimal
    public static boolean isHexadecimal(String hash){
        return hash.matches("[0-9a-fA-F]+");
    }

    //metodo para guardar un objeto en un archivo
    public static void saveObject(Object o, String fileName) throws IOException {
        FileOutputStream fileOut;
        ObjectOutputStream out;

        fileOut = new FileOutputStream(fileName);
        out = new ObjectOutputStream(fileOut);

        out.writeObject(o);

        out.flush();
        out.close();
    }

    public static Object loadObject (String fileName) throws IOException, ClassNotFoundException, InterruptedException {
        FileInputStream fileIn;
        ObjectInputStream in;

        fileIn = new FileInputStream(fileName);
        in = new ObjectInputStream(fileIn);

        Thread.sleep(100);
//		try {
//
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

        Object o = in.readObject();

        fileIn.close();
        in.close();

        return o;
    }


    public static void WriteToFile(String filename, String line, boolean append) throws Exception {
        File file = new File(filename);

        if (file.exists() == false) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, append);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(line);
        bw.newLine();

        bw.close();
    }

    public static void printByteArrayInt(byte[] byteArray) {
        System.out.println("{" + byteArrayIntToString(byteArray) + "}");
    }

    public static String byteArrayIntToString(byte[] byteArray) {
        String out = "";
        int i = 0;
        for (; i < byteArray.length - 1; i++) {
            if (i % 10 == 0 && i != 0)
                out += "\n";
            out += byteArray[i] + 128 + "\t";
        }
        out += byteArray[i] + 128;

        return out;
    }

    public static Object byteArrayToObject(byte[] byteArray) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray));
        Object o = in.readObject();
        in.close();

        return o;
    }

    public static byte[] objectToByteArray(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(o);
        out.close();
        byte[] buffer = bos.toByteArray();

        return buffer;
    }


}
