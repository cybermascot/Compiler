package ott;

import java.io.*;
import java.util.*;

public class FileIO {
    public static String readFile(File f) throws IOException {
        Scanner scan = new Scanner(f);
        String input = "";
        while (scan.hasNext()) {
            input += scan.nextLine() + "\r\n";
        }
        return input;
    }

    public static void writeFile(String text, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        for (char c : text.toCharArray()) {
            out.write(c);
        }
        out.flush();
        out.close();
    }
}
