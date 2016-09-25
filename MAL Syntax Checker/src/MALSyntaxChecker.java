import java.util.ArrayList;

/**
 * Static utility class that deals with checking MAL
 * code for errors. These errors include
 * <li><b>ill-formed label</b> - the first token ends in a colon 
 * but doesn't follow the rules for a label name</i>
 * <li><b>invalid opcode</b> - the first non-comment string (token)
 * found on a line is not one of the valid opcodes; exception: if 
 * the line of code contains a label then the opcode should be 
 * the second string</i>
 * <li><b>too many operands</b> - for the specific opcode, 
 * there are more operands than expected. Assume that any operands 
 * on the line are in a valid, comma-separated list</i>
 * <li><b>too few operands</b> - for the specific opcode, 
 * there are fewer operands than expected. Assume that any operands 
 * on the line are in a valid, comma-separated list</i>
 * <li><b>ill-formed operands</b> - an operand name (id or register) 
 * is invalid, an immediate value is invalid for octal</i>
 * <li><b>wrong operand type</b> - number where id expected, 
 * or vice versa</i>
 * <li><b>label problems</b> - a branch goes to a non-existent 
 * label, or there is a label that is never branched to</i>
 * @author Trever Mock
 * @version 1.0
 */
public class MALSyntaxChecker {

    /**
     * Checks for an ill-formed label, that is
     * the first token ends in a colon but doesn't follow 
     * the rules for a label name.
     * <p>
     * <b>RULES FOR A LABEL NAME:</b>
     * <li> terminated by a colon </i>
     * <li> composed of letters only </i>
     * <li> max length of 5 letters <i>
     * @param line - the line to check the syntax for
     * @return a string description of the error found.
     * Returns an empty string if no error.
     */
    public static String illFormedLabel(String line) {
        String label = "";
        if (line.contains(":")) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == ':') {
                    label = line.substring(0, i);
                    break;
                }
                if (line.charAt(i) == ' ') {
                    return "** error: ill-formed label - label cannot "
                            + "contain spaces and must be the first "
                            + "token on the line";
                }
            }
            if (label.length() > 5) {
                return "** error: ill-formed label - label is more "
                        + "than 5 characters long";
            }
            if (!isAlphabetical(label)) {
                return "** error: ill-formed label - label must be "
                        + "composed of letters only";
            }
        }   
        return "";
    }

    /**
     * Checks for an invalid opcode, that is
     * the first non-comment string (token) found on
     * a line is not one of the valid opcodes; exception:
     * if the line contains a label, the the opcode should be
     * the second string.
     * <p>
     * <b>VALID OPCODES:</b>
     * <li> MOVEI </i> <li> MOVE </i> <li> ADD <i> <li> INC <i>
     * <li> BGT </i> <li> BR </i> <li> SUB <i> <li> MUL <i>
     * <li> DIV </i> <li> DEC </i> <li> BEQ <i> <li> BLT <i>
     * <p>
     * <b>PSUEDO OPCODES:</b>
     * <li> END </i>
     * @param line - the line to check the syntax for
     * @return a string description of the error found.
     * Returns an empty string if no error.
     */
    public static String invalidOpcode(String line) {
        String opcode = "";
        opcode = getOpcode(line);
        if (opcode == null) {
            return "";
        }
        if (isValidOpcode(opcode)) {
            return "";
        } else {
            String suggestion = null;
            ArrayList<String> closeWords = SpellingED.closeStrings(opcode);
            for (String s : closeWords) {
                if (isValidOpcode(s)) {
                    suggestion = s;
                    break;
                }
            }
            if (suggestion == null) {
                return "**error: invalid opcode " + opcode;
            }
            return "** error: invalid opcode " + opcode 
                    + " - did you mean '" + suggestion + "'?";
        }
    }
    
    /**
     * Checks for ill formed operands, that is
     * an operand name (id or register) that is invalid
     * or an immediate value is invalid for octal.
     * @param line - the line to check the syntax for
     * @return a string description of the error found.
     * Returns an empty string if no error.
     */
    public static String illFormedOperands(String line) {    
        ArrayList<String> operands = new ArrayList<String>();
        operands = getOperands(line);
        if (operands == null) {
            return "";
        }
        for (String op : operands) {
            if (!isValidRegister(op) && !isValidOctal(op) 
                    && !isValidIdentifier(op)) {
                return "** error: ill-formed operands - "
                        + "an operand has to be a valid "
                        + "register, valid octal number, "
                        + "or valid identifier.";
            }
        }
        return "";
    }
    
    /**
     * Checks for an invalid operand type, for example
     * a number where an id is expected; or vice versa.
     * <p>
     * Also checks for if there are too many or too few
     * operands for the opcode in the line.
     * @param line - the line to check the syntax for
     * @return a string description of the error found.
     * Returns an empty string if no error.
     */
    public static String invalidOperands(String line) {
        String opcode = getOpcode(line);
        if (opcode == null) {
            return "";
        }
        ArrayList<String> operands = getOperands(line);
        if (operands == null) {
            return "";
        }
        switch (opcode) {
        case "MOVE":
            if (!(operands.size() == 2) || !isValidSrcOrDest(operands.get(0)) 
                    || !isValidSrcOrDest(operands.get(1))) {
                if (operands.size() > 2) {
                    return "** error: too many operands. " + opcode 
                            + " requires 2 operands";
                } else if (operands.size() < 2) {
                    return "** error: too few operands. " + opcode 
                            + " requires 2 operands";
                }
                return "** error: invalid operand type - " + opcode 
                        + " requires operands 1 and 2 to each be a valid source"
                        + " or destination";
            }
            break;
        case "MOVEI":
            if (!(operands.size() == 2) || !isValidOctal(operands.get(0))
                    || !isValidSrcOrDest(operands.get(1))) {
                if (operands.size() > 2) {
                    return "** error: too many operands. " + opcode 
                            + " requires 2 operands";
                } else if (operands.size() < 2) {
                    return "** error: too few operands. " + opcode 
                            + " requires 2 operands";
                }
                return "** error: invalid operand type - " + opcode 
                        + " requires operand 1 to be a number in octal form"
                        + " and operand 2 to each be a valid source"
                        + " or destination";
            }
            break;
        case "MUL":
        case "DIV":
        case "SUB":
        case "ADD":
            if (!(operands.size() == 3) || !isValidSrcOrDest(operands.get(0))
                        || !isValidSrcOrDest(operands.get(1)) 
                        || !isValidSrcOrDest(operands.get(2))) {
                if (operands.size() > 3) {
                    return "** error: too many operands. " + opcode 
                            + " requires 3 operands";
                } else if (operands.size() < 3) {
                    return "** error: too few operands. " + opcode 
                            + " requires 3 operands";
                }
                return "** error: invalid operand type - " + opcode 
                        + " requires operands 1, 2, and 3 to each be"
                        + " a valid source or destination";
            }
            break;
        case "DEC":
        case "INC":
            if (!(operands.size() == 1) || !isValidSrcOrDest(operands.get(0))) {
                if (operands.size() > 1) {
                    return "** error: too many operands. " + opcode 
                            + " requires 1 operand";
                } else if (operands.size() < 1) {
                    return "** error: too few operands. " + opcode 
                            + " requires 1 operand";
                }
                return "** error: invalid operand type - " + opcode 
                        + " requires it's operand to be a valid"
                        + " source or destination";
            }
            break;
        case "BLT":
        case "BEQ":
        case "BGT":
            if (!(operands.size() == 3) || !isValidSrcOrDest(operands.get(0))
                        || !isValidSrcOrDest(operands.get(1))
                        || !isValidLabel(operands.get(2))) {
                if (operands.size() > 3) {
                    return "** error: too many operands. " + opcode 
                            + " requires 3 operands";
                } else if (operands.size() < 3) {
                    return "** error: too few operands. " + opcode 
                            + " requires 3 operands";
                }
                return "** error: invalid operand type - " + opcode 
                        + " requires operands 1 and 2 to each be a valid source"
                        + " or destination. Operand 3 must be a valid label";
            }
            break;
        case "BR":
            if (!(operands.size() == 1) || !isValidLabel(operands.get(0))) {
                if (operands.size() > 1) {
                    return "** error: too many operands. " + opcode 
                            + " requires 1 operand";
                } else if (operands.size() < 1) {
                    return "** error: too few operands. " + opcode 
                            + " requires 1 operand";
                }
                return "** error: invalid operand type - " + opcode 
                        + " requires it's operand to be a valid label";
            }
            break;
        case "END":
            if (!(operands.size() == 0)) {
                if (operands.size() > 0) {
                    return "** error: too many operands. " + opcode 
                            + " requires 0 operands";
                } else if (operands.size() < 0) {
                    return "** error: too few operands. " + opcode 
                            + " requires 0 operands";
                }
            }
            break;
    }
        return "";
    }
    
    /**
     * Checks for label problems, that is
     * if a branch goes to a non-existent label,
     * or there is a label that is never branched to.
     * @param fullText - the full text in the form of a string
     * on one line with the token '**n**' in areas
     * where a new line would have been in the code.
     * @return a list of string descriptions of any errors 
     * or warnings found. Returns an empty ArrayList<string> 
     * if no errors or warnings are found.
     */
    public static ArrayList<String> labelProblems(String fullText) {
        ArrayList<String> errors = new ArrayList<String>();
        ArrayList<String> labels = getAllLabels(fullText);
        String text = removeAllLabels(fullText);
        for (String s : labels) {
            if (!text.contains(s) && isValidLabel(s)) {
                errors.add("** warning: the label '" + s 
                        + "' is not being branched to\n");
            }
        }
        String token = getFirstToken(text);
        while (!token.equals("")) {
            boolean invalidOperandAmount = false;
            if (token.equals("BGT") || token.equals("BLT") 
                    || token.equals("BEQ")) {
                for (int i = 0; i < 3; i++) {
                    if (!getFirstToken(text).equals(";")) {
                        text = deleteFirstToken(text);
                    } else {
                        invalidOperandAmount = true;
                        break;
                    }
                }
                if (getFirstToken(text).equals(";")) {
                    invalidOperandAmount = true;
                } else {
                    token = getFirstToken(text);
                }
                if (!labels.contains(token) && !invalidOperandAmount 
                        && isValidLabel(token)) {
                    errors.add("** error: the token '" + token 
                            + "' has no label to branch to\n");
                }
            }
            if (token.equals("BR")) {
                if (!getFirstToken(text).equals(";")) {
                    text = deleteFirstToken(text);
                } else {
                    invalidOperandAmount = true;
                }
                if (getFirstToken(text).equals(";")) {
                    invalidOperandAmount = true;
                } else {
                    token = getFirstToken(text);
                }
                if (!labels.contains(token) && !invalidOperandAmount 
                        && isValidLabel(token)) {
                    errors.add("** error: the token '" + token 
                            + "' has no label to branch to\n"); 
                }
            }
            text = deleteFirstToken(text);
            token = getFirstToken(text);
        }
        return errors;
    }
    
    /**
     * Checks for any problems with the END instruction.
     * <li>The END instruction should appear exactly once<i>
     * <li>The END instruction should be the last instruction in the
     * MAL code<i>
     * @param fullText - a String of text representing the entire
     * MAL program on one line with ';' separating new lines.
     * @return - A String list of any errors found involving
     * the END instruction. Returns an empty list if no errors
     * are found
     */
    public static ArrayList<String> endProblems(String fullText) {
        ArrayList<String> errors = new ArrayList<String>();
        boolean found = false;
        String text = fullText;
        String tempText = "";
        String token = getFirstToken(text);
        if (!text.contains("END")) {
            errors.add("** warning: this program does not contain "
                    + "the END instruction at all");
            return errors;
        }
        while (!token.equals("")) {
            if (getFirstToken(text).equals("END") && !found) {
                found = true;
                text = deleteFirstToken(text);
                if (getFirstToken(text).equals(";")) {
                    tempText = deleteFirstToken(text);
                    if (!getFirstToken(tempText).isEmpty()) {
                        errors.add("** warning: the END opcode is not "
                                + "the last instruction for this program");
                    }
                }
            } else if (getFirstToken(text).equals("END") && found) {
                text = deleteFirstToken(text);
                if (getFirstToken(text).equals(";")) {
                    tempText = deleteFirstToken(text);
                    if (getFirstToken(tempText).isEmpty()) {
                        errors.clear();
                    }
                }
                errors.add("** warning: the END opcode appears more than once");
            }
            text = deleteFirstToken(text);
            token = getFirstToken(text);
        }
        return errors;
    }
    
    /**
     * Gets the identifiers in a given line of MAL code
     * and returns them in the form of an ArrayList<String>
     * @param line - the line to be examined
     * @return the identifiers that the line of MAL code
     * contains. Returns an empty list if the line contains
     * no identifiers.
     */
    public static ArrayList<String> getIdentifiers(String line) {
        ArrayList<String> identifiers = new ArrayList<String>();
        String opcode = getOpcode(line);
        if (opcode == null || opcode.equals("BEQ") || opcode.equals("BLT")
                || opcode.equals("BGT") || opcode.equals("BR")) {
            return identifiers;
        }
        ArrayList<String> operands = getOperands(line);
        if (operands == null) {
            return identifiers;
        }
        for (String s : operands) {
            if (isValidIdentifier(s) && !s.trim().equals("")) {
                identifiers.add(s);
            }
        }
        return identifiers;
    }
    
    /**
     * Private static helper method that removes all 
     * of the labels in a String of text.
     * That is, all tokens that end with a ':'
     * @param text - the text to be modified
     * @return the text with all of the labels removed
     */
    private static String removeAllLabels(String text) {
        StringBuffer sbText = new StringBuffer(text);
        String label = "";
        for (int i = 0; i < text.length(); i++) {
            label = label + text.charAt(i);
            if (text.charAt(i) == ' ') {
                label = "";
            }
            if (text.charAt(i) == ':') {
                label = label.substring(0, label.length() - 1);
                sbText.delete(i - label.length(), i + 1);
                break;
            }
        }
        if (sbText.toString().contains(":")) {
            return removeAllLabels(sbText.toString());
        }
        return sbText.toString();
    }
    
    /**
     * Private static helper method that deletes the first token 
     * in a line, that is all of the content from the 
     * beginning of the line up to the first space.
     * The line is trimmed of trailing white space upon completion.
     * @param line - the line to have it's first token deleted
     * @return the line without the first token. If no first token,
     * returns the empty string.
     */
    private static String deleteFirstToken(String line) {
        line = line.trim() + " ";
        if (line.equals(" ")) {
            return "";
        }
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                line = line.substring(i, line.length());
                break;
            }
        }
        return line.trim();
    }
    
    /**
     * Private static helper method that deletes the label, 
     * if there is one, on a String
     * line and returns the modified string
     * @param line - the String line to be modified
     * @return the new String with the label removed.
     */
    private static String deleteLabel(String line) {
        if (line.contains(":")) {
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == ':') {
                    line = line.substring(i + 1, line.length()).trim();
                    break;
                }
            }
        }
        return line;
    }
    
    /**
     * Private static helper method that creates an 
     * ArrayList<String> of the labels contained
     * in a block of text that resembles an entire piece 
     * of MAL code.
     * <p>
     * The text passed in should be one line of text with 
     * no new lines.
     * @param text - the text to find labels from
     * @return the list of labels found in the text
     */
    private static ArrayList<String> getAllLabels(String text) {
        ArrayList<String> labels = new ArrayList<String>();
        String label = "";
        for (int i = 0; i < text.length(); i++) {
            label = label + text.charAt(i);
            if (text.charAt(i) == ' ') {
                label = "";
            }
            if (text.charAt(i) == ':') {
                label = label.trim().substring(0, label.length() - 1);
                labels.add(label);
            }
        } 
        return labels;  
    }
    
    /**
     * Private static helper method that gets the 
     * operands from the passed String
     * line and puts them into an ArrayList.
     * @param line - the line to be examined
     * @return an ArrayList of the operands in the line;
     * returns null if no operands exist
     */
    private static ArrayList<String> getOperands(String line) {
        String currentOperand = "";
        ArrayList<String> operands = new ArrayList<String>();
        line = deleteLabel(line);
        if (line.equals("")) {
            return null;
        }
        line = deleteFirstToken(line);
        for (int i = 0; i < line.length(); i++) {
            if ((line.charAt(i) == ',' || line.charAt(i) == ' ') 
                    && !currentOperand.equals("")) {
                if (!currentOperand.trim().equals("")) {
                    operands.add(currentOperand.trim());
                }
                currentOperand = "";
            } else {
                currentOperand = currentOperand + line.charAt(i);
            }
        }
        if (!currentOperand.trim().equals("")) {
            operands.add(currentOperand.trim());
        }
        return operands;
    }
    
    /**
     * Private static helper method that
     * returns the opcode in the passed line. 
     * <p>
     * The opcode is always assumed to be the first token
     * (with the exception of having a label before,
     * making it the second token).
     * <p>
     * If there is no opcode in the line, this method
     * will return null.
     * @param line - the line to be checked
     * @return the opcode for the line
     */
    private static String getOpcode(String line) {
        line = deleteLabel(line);
        String opcode = null;
        for (int i = 0; i < line.length(); i++) {
            if (!line.contains(" ")) {
                opcode = line;
            } else if (line.charAt(i) == ' ') {
                opcode = line.substring(0, i);
                break;
            }
        }
        return opcode;
    }
    
    /**
     * Private static helper method that gets the first token 
     * in a String of text. The first
     * token includes all text up to the first space.
     * @param text - the text to take from
     * @return - the first token of that text. If there
     * is no first token, returns an empty String.
     */
    private static String getFirstToken(String line) {
        line = line.trim() + " ";
        if (line.equals(" ")) {
            return "";
        }
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                line = line.substring(0, i);
                break;
            }
        }
        return line.trim();
    }
    
    /**
     * Private static helper method that checks 
     * to see if the passed string is a valid register
     * @param operand - the String to be checked
     * @return true if the passed String is a valid register;
     * false otherwise
     */
    private static boolean isValidRegister(String operand) {
        if (operand.equals("R0") || operand.equals("R1") 
                || operand.equals("R2") || operand.equals("R3") 
                || operand.equals("R4") || operand.equals("R5")
                || operand.equals("R6") || operand.equals("R7")) {
            return true;
        }
        return false;
    }
    
    /**
     * Private static helper method that checks to see 
     * if an operand is represented in octal form.
     * @param operand - the String operand to be checked
     * @return true if the operand is in octal form;
     * false otherwise
     */
    private static boolean isValidOctal(String operand) {
        char c;
        if (isNumber(operand)) {
            for (int i = 0; i < operand.length(); i++) {
                c = operand.charAt(i);
                if (c == '8' || c == '9') {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
    /**
     * Private static helper method that 
     * checks to see if the passed String qualifies as
     * a valid opcode.
     * @param opcode - the opcode to be checked
     * @return true if valid; false otherwise
     */
    private static boolean isValidOpcode(String opcode) {
        if (opcode.equals("MOVEI") || opcode.equals("MOVE") 
                || opcode.equals("ADD")
                || opcode.equals("INC") || opcode.equals("BGT") 
                || opcode.equals("BR") || opcode.equals("SUB") 
                || opcode.equals("MUL") || opcode.equals("DIV") 
                || opcode.equals("DEC") || opcode.equals("BEQ") 
                || opcode.equals("BLT") || opcode.equals("END")) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Private static helper method that checks to see if 
     * the operand is a valid identifier, that
     * it is composed of letters only and has a max length of 5
     * @param operand
     * @return true if the identifier is valid; false otherwise
     */
    private static boolean isValidIdentifier(String operand) {
        if (!isAlphabetical(operand) || operand.length() > 5) {
            return false;
        }
        return true;
    }
    
    /**
     * Private static helper method that checks 
     * to see if a label is valid. Labels
     * have the same rules as valid identifiers.
     * <p>
     * A passed label can contain the ':' but
     * does not have to.
     * @param label - the label to be checked
     * @return true if valid; false otherwise
     */
    private static boolean isValidLabel(String label) {
        if (label.contains(":")) {
            label = label.substring(0, label.length() - 1);
        }
        return isValidIdentifier(label);
    }
    
    /**
     * Private static helper method that checks to see 
     * if the passed String is a valid
     * source or destination. A source and destination
     * may be either a valid identifier or valid register.
     * @param operand - the operand to check
     * @return true if the String is a valid source or
     * destination; false otherwise
     */
    private static boolean isValidSrcOrDest(String operand) {
        if (isValidIdentifier(operand) || isValidRegister(operand)) {
            return true;
        }
        return false;
    }
    
    /**
     * Private static helper method that 
     * checks through a string for
     * if it is completely alphabetical.
     * @param s - the string to be checked
     * @return true if the string contains only letters;
     * false otherwise.
     */
    private static boolean isAlphabetical(String s) {
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Private static helper method 
     * that checks through a String
     * to see if it is completely numerical.
     * @param s - the string to be checked
     * @return true if the string contains only numbers;
     * false otherwise
     */
    private static boolean isNumber(String s) {
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
}
