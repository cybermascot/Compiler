package ott.optimizer;

import ott.*;
import ott.assembler.parsables.types.*;
import ott.compiler.compileable.*;

import java.io.*;
import java.util.*;

public class Optimizer {

    public static void main(String[] args) throws Exception {
        Optimizer o = new Optimizer();
        File f = new File("assembly.asm");
        File op = new File("optimized.asm");
        o.optimize(f, op);
    }

    public void optimize(File f) throws IOException {
        optimize(f, f);
    }

    public void optimize(File in, File out) throws IOException {
        String optimized = optimize(FileIO.readFile(in));
        FileIO.writeFile(optimized, out);
    }

    public String optimize(String s) {
        Object[] registersState = new Object[16];
        Scanner scan = new Scanner(s);
        StringBuilder result = new StringBuilder();
        while (scan.hasNext()) {
            String next = scan.nextLine();
            String commandWithoutLabel = next.substring(next.indexOf(":") + 1).trim();
            if (commandWithoutLabel.startsWith("PUSH")) {
                // do something fancy
            }
            if (isNeeded(commandWithoutLabel, registersState)) {
                Helper.appendLine(result, next);
            }
        }
        scan.close();
        return result.toString();
    }

    private boolean isNeeded(String command, Object[] registerState) {

        if (command.startsWith("MOVI")) {
            String withoutMOV = command.substring(4).trim();
            String[] parts = withoutMOV.split(",");
            int register = Integer.parseInt(parts[0].replace("R", "").trim()); // strip 'R' off 'R0' and then parse the remaining int
            String value;
            try {
                value = String.valueOf(Helper.getNumber(parts[1].trim()));
            } catch (NumberFormatException nfe) {
                value = parts[1].trim(); // if its not a number
            }
            if (value.equals(registerState[register]))
                return false;
            registerState[register] = value;
        }

        if (command.startsWith("B")) {
            for (int i = 0; i < registerState.length; i++) {
                registerState[i] = null; // null out for branch - who know swhat dat branch call is doing!
            }
        } else {
            int reg = changesRegister(command);
            if (reg != -1)
                registerState[reg] = null;
        }

        return true;
    }

    private int changesRegister(String command) {
        // most dataproc
        // any ldr command
        // pop peek
        // MSR command

        String[] parts = command.split("\\s|,");
        String identifier = parts[0];
        boolean mutative = identifier.matches("LDR");
        mutative = mutative || identifier.matches(DataProcType.getRegex()) && !identifier.matches("CMP");
        mutative = mutative || identifier.matches("POP|PEEK");
        mutative = mutative || identifier.matches("MSR");
        if (mutative) {
            String params = command.substring(3);
            params = params.substring(params.indexOf("R"));
            if (params.contains(","))
                params = params.substring(0, params.indexOf(","));
            if (params.contains("-"))
                params = params.substring(0, params.indexOf("-"));
            return Integer.parseInt(params.replace("R", "").trim());
        }
        return -1;
    }

    public Program optimize(Program p) {

        // TODO? maybe

        return p;
    }
}
