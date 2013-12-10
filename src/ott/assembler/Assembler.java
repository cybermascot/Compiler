package ott.assembler;

import ott.assembler.parsables.*;
import ott.io.*;
import ott.parsing.*;
import ott.util.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class Assembler {

    public static void main(String[] args) throws Exception {
//        keyboardTest();

        Stopwatch watch = new Stopwatch();
        watch.start();
//        File source = new File("program.assm");
//        File source = new File("helloworld.assm");
//        File source = new File("echoCharsWithoutInterrupts.assm");
        File source = new File("program.asm");
        File out = new File("kernel.img");
        assemble(source, out);

        System.out.println("Read  file: " + source.getName());
        System.out.println("Write file: " + out.getName());
        System.out.println("At: " + new SimpleDateFormat("YYYY-MM-dd'T'hh-mm-ss:S").format(new Date()));
        watch.stop();
        System.out.println("Time Taken in Millis: " + watch.getMillis());
    }

    public static void keyboardTest() {
        while (true) {
            String input = Input.promptForString(">");
            try {
                int[] result = assemble(input);
                for (int res : result) {
                    if (res == 0)
                        continue;
                    System.out.print("\t");
                    System.out.print(Integer.toHexString(res & 0xFF));
                    System.out.print(" ");
                    System.out.print(Integer.toHexString((res & 0xFF00) >> 8));
                    System.out.print(" ");
                    System.out.print(Integer.toHexString((res & 0xFF0000) >> 16));
                    System.out.print(" ");
                    System.out.print(Integer.toHexString((res & 0xFF000000) >>> 24));
                    System.out.println();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void assemble(String source, File output) throws IOException {
        int[] ramImage = assemble(source);
        writeFile(output, ramImage);
    }

    public static void assemble(File source, File output) throws IOException {
        String input = readFile(source);
        int[] ramImage = assemble(input);
        writeFile(output, ramImage);
    }

    public static int[] assemble(String input) {
        Queue<Token> tokens = Tokenizer.tokenize(input, AssemblerTokens.values());
        tokens.removeIf(t -> t.type() == AssemblerTokens.COMMENT); // remove all comments
        return new Program().parse(tokens);
    }

    private static void writeFile(File out, int[] img) throws IOException {
        FileOutputStream outStream = new FileOutputStream(out);
        for (int c : img) {
            write32bits(outStream, c);
        }
        outStream.flush();
        outStream.close();
    }


    private static void write32bits(OutputStream streamOut, int bits) throws IOException {
        streamOut.write(bits & 0xFF);
        streamOut.write((bits & 0xFF00) >> 8);
        streamOut.write((bits & 0xFF0000) >> 16);
        streamOut.write((bits & 0xFF000000) >>> 24);
    }

    private static String readFile(File f) throws IOException {
        Scanner scan = new Scanner(f);
        String input = "";
        while (scan.hasNext()) {
            input += scan.nextLine() + "\r\n";
        }
        return input;
    }
}
