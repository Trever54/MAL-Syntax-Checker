import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * This is the main class for the program. It does all of the basic 
 * logic handling for the program. This includes:
 * <li>the Main method to use as a command line driver using one argument</i>
 * <li>reading and writing to files</i>
 * <li>stripping the file of blank lines and comments</i>
 * <li>formatting the output log with an appropriate header and footer</i>
 * <li>keeping track of how many errors occured and for what type</i>
 * <li>writing errors and warnings to the output log</i>
 * <p>
 * The main method requires one argument that is the name of the .mal
 * file to read in (without the .mal extension). The program will
 * save the output log to the same name with a .log extension.
 * @author Trever Mock
 * @version 1.0
 */
public class MainLogicHandler {

    /**
     * Constructor that handles all of the logic behind reading from the input file
     * and writing to the output file. It strips the file of
     * any comments and blank spaces and keeps track of the appropriate information to
     * write an appropriate header and footer to the output log.
     * @param inputPath - the path to the .mal input file
     * @param outputPath - the path to the .log output file
     */
    @SuppressWarnings("static-access")
    public MainLogicHandler(String inputPath, String outputPath) {
        createOutputFile(outputPath);
        try {
            MALSyntaxChecker msc = new MALSyntaxChecker();
            BufferedReader reader = new BufferedReader(new FileReader(inputPath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            writeHeader(writer, inputPath, outputPath);
            StringBuffer fullText = new StringBuffer();
            String line;
            String error;
            ArrayList<String> labelErrors;
            ArrayList<String> endErrors;
            ArrayList<String> identifiers = new ArrayList<String>();
            int lineNumber = 1;
            int illFormedLabelCount = 0;
            int invalidOpcodeCount = 0;
            int illFormedOperandsCount = 0;
            int invalidOperandTypeCount = 0;
            int tooManyOperandsCount = 0;
            int tooFewOperandsCount = 0;
            int labelProblemsCount = 0;
            int labelWarningsCount = 0;
            int endWarningCount = 0;
            while ((line = reader.readLine()) != null) {
                line = deleteComments(line);
                line = line.trim();
                if (!line.isEmpty()) {
                    fullText.append(line + " ; ");
                    writer.write(lineNumber + ". " + line);
                    writer.newLine();
                    lineNumber++;
                    // Store Identifiers
                    identifiers.addAll(msc.getIdentifiers(line));
                    // Ill-Formed Label Error
                    error = msc.illFormedLabel(line);
                    if (writeError(writer, error)) {
                        illFormedLabelCount++;
                    }
                    // Invalid Opcode Error
                    error = msc.invalidOpcode(line);
                    if (writeError(writer, error)) {
                        invalidOpcodeCount++;
                    }
                    // Ill-Formed Operands
                    error = msc.illFormedOperands(line);
                    if (writeError(writer, error)) {
                        illFormedOperandsCount++;
                    }
                    // Invalid Operand Types, too many operands, too few operands
                    error = msc.invalidOperands(line);
                    if (writeError(writer, error)) {
                        if (error.contains("too many")) {
                            tooManyOperandsCount++;
                        } else if (error.contains("too few")) {
                            tooFewOperandsCount++;
                        } else {
                            invalidOperandTypeCount++;
                        }
                    }
                }
            }
            // Errors with label branching
            labelErrors = msc.labelProblems(fullText.toString());
            if (labelErrors != null) {
                writer.newLine();
                for (String s : labelErrors) {
                    if (s.contains("error")) {
                        labelProblemsCount++;
                    } else {
                        labelWarningsCount++;
                    }
                    writer.write(s);
                    writer.newLine();
                }
            }
            // END instruction problems
            endErrors = msc.endProblems(fullText.toString());
            endErrors = deleteDuplicates(endErrors);
            for (String err : endErrors) {
                if (writeError(writer, err)) {
                    endWarningCount++;
                }
            }
            writer.newLine();
            lineNumber--;
            identifiers = deleteDuplicates(identifiers);
            writeFooter(writer, lineNumber, illFormedLabelCount, invalidOpcodeCount, 
                    illFormedOperandsCount, invalidOperandTypeCount, 
                    tooManyOperandsCount, tooFewOperandsCount, labelProblemsCount, 
                    labelWarningsCount, endWarningCount, identifiers);
            reader.close();
            writer.close();
            System.out.println("Finished Creating the Log for " + inputPath);
        } catch (FileNotFoundException e) {
            System.err.println("Failed to find file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to write to output log.");
            e.printStackTrace();
        } 
    }
    
    /**
     * Private helper method that writes an error or warning message to 
     * the output file. If the error is an empty string nothing is 
     * written to the output file.
     * @param error - the error or warning to be written
     * @param writer - the BufferedWriter used to write to the output file
     * @return true if something is written to the output; false otherwise
     */
    private boolean writeError(BufferedWriter writer, String error) {
        if (!error.equals("")) {
            try {
                writer.write(error);
                writer.newLine();
                return true;
            } catch (IOException e) {
                System.err.println("Error writing error or warning messege to file");
                e.printStackTrace();
            }
        }
        return false;
    }
    
    /**
     * Private helper method that deletes any duplicates in a given
     * list and returns a copy of the list without those duplicates.
     * @param identifiers - the list to have duplicates deleted from
     * @return the list with those duplicates removed
     */
    private ArrayList<String> deleteDuplicates(List<String> identifiers) {
        ArrayList<String> temp = new ArrayList<String>();
        Iterator<String> itr = identifiers.iterator();
        String current = "";
        while (itr.hasNext()) {
            current = itr.next();
            if (!temp.contains(current)) {
                temp.add(current);
            }
        }
        return temp;
    }
    
    /**
     * Deletes any comments that the string may have.
     * Comments are designated by a ';'. Anything after the
     * ';' will be deleted on a single line.
     * @param line - the string to be checked, which should
     * represent one line of MAL code
     * @return the string with comments removed.
     */
    private String deleteComments(String line) {
        StringBuffer sb = new StringBuffer(line);
        for (int i = 0; i < line.length(); i++) {
            if (sb.charAt(i) == ';') {
                sb.delete(i, sb.length());
                break;
            }
        }
        return sb.toString();  
    }
    
    /**
     * Checks to see if an output file exists at the
     * passed String Path. If there is not one, a
     * file is created based on the passed String. 
     * Nothing happens if the file already exists.
     * @param outputPath - the output path to create a file
     * with
     */
    private void createOutputFile(String outputPath) {
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create the output log " + outputPath);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Writes the footer to the output log
     * @param writer - the BufferedWriter to write to the output log
     * @param linesOfCode - the amount of lines of code there are in the MAL code
     * @param illFormedLabelCount - the total count of ill-formed label errors found
     * @param invalidOpcodeCount - the total count of invalid opcode errors found
     * @param illFormedOperandsCount - the total count of ill-formed operand 
     * errors found
     * @param invalidOperandTypeCount - the total count of invalid operand type 
     * errors found
     * @param tooManyOperandsCount - the total count of too many operand errors found
     * @param tooFewOperandsCount - the total count of too few operand errors found
     * @param labelProblemsCount - the total count of label problem errors found
     * @param labelWarningsCount - the total count of label problem warnings found
     * @param endWarningsCount - the total count of warnings involving the END 
     * operation
     * @param identifiers - an ArrayList of all of the identifiers found in the 
     * MAL code
     */
    private void writeFooter(BufferedWriter writer, int linesOfCode, 
            int illFormedLabelCount, int invalidOpcodeCount, 
            int illFormedOperandsCount, int invalidOperandTypeCount, 
            int tooManyOperandsCount, int tooFewOperandsCount, 
            int labelProblemsCount, int labelWarningsCount, 
            int endWarningsCount, ArrayList<String> identifiers) {
        try {
            writer.write("----------");
            writer.newLine();
            writer.newLine();
            writer.write("Total Lines of Code: " + linesOfCode);
            writer.newLine();
            writer.newLine();
            int totalWarnings = labelWarningsCount + endWarningsCount;
            int totalErrors = illFormedLabelCount + invalidOpcodeCount 
                    + illFormedOperandsCount + invalidOperandTypeCount 
                    + tooManyOperandsCount + tooFewOperandsCount 
                    + labelProblemsCount;
            if (labelWarningsCount != 0) {
                writer.write("Total Warnings = " + totalWarnings);
                writer.newLine();
            } else {
                writer.write("No Warnings!");
                writer.newLine();
            }
            if (labelWarningsCount != 0) {
                writer.write(labelWarningsCount + " label problem warning(s)");
                writer.newLine();
            }
            if (endWarningsCount != 0) {
                writer.write(endWarningsCount + " problem(s) with END opcode");
                writer.newLine();
            }
            writer.newLine();
            if (totalErrors != 0) {
                writer.write("Total Errors = " + totalErrors);
                writer.newLine();
            } else {
                writer.write("No Errors Found!");
                writer.newLine();
            }
            if (illFormedLabelCount != 0) {
                writer.write(illFormedLabelCount + " ill formed label error(s)");
                writer.newLine();
            }
            if (invalidOpcodeCount != 0) {
                writer.write(invalidOpcodeCount + " invalid opcode error(s)");
                writer.newLine();
            }
            if (illFormedOperandsCount != 0) {
                writer.write(illFormedOperandsCount + " ill formed operand error(s)");
                writer.newLine();
            }
            if (invalidOperandTypeCount != 0) {
                writer.write(invalidOperandTypeCount + " invalid operand type error(s)");
                writer.newLine();
            }
            if (tooManyOperandsCount != 0) {
                writer.write(tooManyOperandsCount + " too many operands error(s)");
                writer.newLine();
            }
            if (tooFewOperandsCount != 0) {
                writer.write(tooFewOperandsCount + " too few operands error(s)");
                writer.newLine();
            }
            if (labelProblemsCount != 0) {
                writer.write(labelProblemsCount + " label problem error(s)");
                writer.newLine();
            }
            if (!identifiers.isEmpty()) {
                writer.newLine();
                writer.write("Identifiers:");
                writer.newLine();
                for (String s : identifiers) {
                    writer.write(s);
                    writer.newLine();
                }
            }
            if (totalErrors == 0) {
                writer.newLine();
                writer.write("Processing Complete - MAL Program is valid");
            } else {
                writer.newLine();
                writer.write("Processing Complete - MAL Program is not valid");
            }
        } catch (IOException e) {
            System.err.println("Failed to write to output log");
            e.printStackTrace();
        }
    }
    
    /**
     * Writes the header for the log file.
     * @param writer - the BufferedWriter used to write to the output log
     * @param malFileName - the name of the .mal input file with the .mal extension
     * @param logFileName - the name of the .log output file with the .log extension
     */
    private void writeHeader(BufferedWriter writer, String malFileName, 
            String logFileName) {
        try {
            writer.write("MAL Syntax Checker Results");
            writer.newLine();
            writer.write(malFileName);
            writer.newLine();
            writer.write(logFileName);
            writer.newLine();
            writer.write(
                    new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
            writer.newLine();
            writer.write("Trever Mock");
            writer.newLine();
            writer.write("CS3210");
            writer.newLine();
            writer.write("----------");
            writer.newLine();
            writer.newLine();
            writer.write("MAL Program Listing:");
            writer.newLine();
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing the Header for the log file.");
            e.printStackTrace();
        }
    }
    
    
    /**
     * Main method that takes in one argument as a String path.
     * It creates a MainLogicHandler object using that path as
     * the basis for an input and output file.
     * @param args - path to a .mal file without the .mal extension
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments passed. Exiting program");
            System.exit(0);
        }
        if (args.length > 1) {
            System.out.println("Too many arguments. Using only "
                    + "the first argument.");
        }
        String input = args[0] + ".mal";
        String output = args[0] + ".log";
        if (new File(input).exists()) {
            MainLogicHandler mlh = new MainLogicHandler(input, output);  
        } else {
            System.out.println("Invalid Argument. The file " + input 
                    + " does not exist.");
        }
    }
}
