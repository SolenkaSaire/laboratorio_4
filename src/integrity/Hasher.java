package integrity;
import util.Util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static util.Util.byteArrayToHexString;

public class Hasher {


    public static String getHash(String input, String algorithm) throws NoSuchAlgorithmException {
        byte[] inputBA = input.getBytes();

        MessageDigest hasher = MessageDigest.getInstance(algorithm);
        hasher.update(inputBA);

        return byteArrayToHexString(hasher.digest(), "");
    }


    public static String getHashFile(String filename, String algorithm) throws Exception {
        MessageDigest hasher = MessageDigest.getInstance(algorithm);

        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];

        int in;
        while ((in = fis.read(buffer)) != -1) {
            hasher.update(buffer, 0, in);
        }
        fis.close();
        return byteArrayToHexString(hasher.digest(), "");
    }

    public static void generateIntegrityCheckerFile(String inputFile, String outputFileName) throws Exception {
        System.out.println("generating INTEGRITY file");
        System.out.println("FOLDERNAME: "+inputFile);
        System.out.println("OUTPUTFILENAME: "+outputFileName);
      /*  MessageDigest hasher = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(inputFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            hasher.update(buffer, 0, bytesRead);
        }
        fis.close();

        byte[] hash = hasher.digest();
        String hashHex = byteArrayToHexString(hash, "");

        FileOutputStream fos = new FileOutputStream(outputFileName);
        fos.write(hashHex.getBytes());
        fos.close();

       */
        // Creación del PrintWriter para escribir en el archivo de salida
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
            File file = new File(inputFile);
            if (file.isFile()) {
                // Obtención del hash en SHA-256
                String hash = getHashFile(file.getPath(), "SHA-256");
                // Determinación del indicador según si el archivo es de texto o binario
                String fileIndicator = isTextFile(file) ? " " : "*";
                // Escritura en el archivo .hash
                writer.println(hash + " " + fileIndicator + file.getName());
            }
        }
    }

    private static boolean isTextFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int character;
            while ((character = reader.read()) != -1) {
                if (Character.isISOControl(character) && character != '\n' && character != '\r') {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void generateIntegrityFile(String folderName, String integrityFileName) throws Exception {
        System.out.println("checking INTEGRITY FILE");
        System.out.println("FOLDERNAME: "+folderName);
        System.out.println("INTEGRITYFILENAME: "+integrityFileName);
        File folder = new File(folderName);
        File integrityFile = new File(integrityFileName);

//        if (!folder.exists() || !folder.isDirectory()) {
//            throw new IllegalArgumentException("La carpeta no existe o no es un directorio.");
//        }

        if (!integrityFile.exists()) {
            throw new IllegalArgumentException("El archivo de integridad no existe.");
        }

        int missingFilesCount = 0;
        int checksumMismatchCount = 0;
        int improperlyFormattedLinesCount = 0;
        List<String> missingFiles = new ArrayList<>();
        List<String> checksumMismatchFiles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(integrityFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.contains("*") ? line.split("\\*", 2) : line.split("  ", 2);

                if (parts.length < 2) {
                    improperlyFormattedLinesCount++;
                    System.out.println(line + ": improperly formatted");
                    continue;
                }

                String expectedHash = parts[0].trim();
                String fileName = parts[1].trim();

                // Validaciones del hash
                if (expectedHash.length() != 64 || !Util.isHexadecimal(expectedHash)) {
                    improperlyFormattedLinesCount++;
                    checksumMismatchCount++;
                    System.out.println(line + ": improperly formatted");
                    continue;
                }

                fileName="";
                //System.out.println("sea verifica archivo de carpeta "+folderName+" con nombre "+fileName);
                File file = new File(folder, fileName);
                System.out.println("ruta del archivo: "+file.getPath()) ;
                if (!file.exists()) {
                    missingFilesCount++;
                    missingFiles.add(fileName);
                    System.out.println(fileName + ": No such file or directory");
                    System.out.println(fileName + ": FAILED open or read");
                    continue;
                }

                String actualHash = getHashFile(file.getPath(), "SHA-256");
                if (!actualHash.equals(expectedHash)) {
                    checksumMismatchCount++;
                    checksumMismatchFiles.add(fileName);
                    System.out.println(fileName + ": FAILED");
                } else {
                    System.out.println(fileName + ": OK");
                }
            }
        }

        // Mostrar errores resumidos al final del proceso
        if (improperlyFormattedLinesCount > 0) {
            System.out.println("WARNING: " + improperlyFormattedLinesCount + (improperlyFormattedLinesCount == 1 ? " line is improperly formatted" : " lines are improperly formatted"));
        }
        if (missingFilesCount > 0) {
            System.out.println("WARNING: " + missingFilesCount + (missingFilesCount == 1 ? " listed file could not be read" : " listed files could not be read"));
        }
        if (checksumMismatchCount > 0) {
            System.out.println("WARNING: " + checksumMismatchCount + (checksumMismatchCount == 1 ? " computed checksum did NOT match" : " computed checksums did NOT match"));
        }

        if (missingFilesCount == 0 && checksumMismatchCount == 0 && improperlyFormattedLinesCount == 0) {
            System.out.println("Todos los archivos han pasado la verificación.");
        }
    }


}