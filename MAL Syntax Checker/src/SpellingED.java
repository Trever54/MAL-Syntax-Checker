import java.util.ArrayList;
import java.util.List;

/**
 * Uses Edit Distance to give a suggestion on a misspelled
 * word, specifically an opcode in MAL.
 * <p>
 * These suggestions are only within 1 Edit Distance operation.
 * <p>
 * Edit Distance operations include deletion, insertion, replacement,
 * and transposition of a single character.
 * @author Trever Mock
 * @version 1.0
 */
public class SpellingED {
    
    /**
     * Creates a list of strings that are within one edit distance of the parameter.
     * Strings within 'one edit distance' are to be obtained by a single operation 
     * of one of the following:
     *  <li>single character deletion</li>
     *  <li>single character insertion</li>
     *  <li>single character replacement</li>
     *  <li>single contiguous-pair transposition</li>
     * @param seq - the sequence of characters that potentially
     * represent an opcode
     * @return a list of all strings obtained by a single edit distance operation
     */
    public static ArrayList<String> closeStrings(String seq) {
        ArrayList<String> closeWords = new ArrayList<String>();
        // deletion
        for (int i = 0; i < seq.length(); i++) {
            StringBuffer sb = new StringBuffer(seq);
            closeWords.add(sb.deleteCharAt(i).toString());
        }
        // insertion
        closeWords.addAll(insertionOperation(seq));
        // subsitution
        closeWords.addAll(subsitutionOperation(seq));
        // transposition
        for (int i = 0; i < seq.length() - 1; i++) {
            StringBuffer sb = new StringBuffer(seq);
            Character first;
            Character second;
            first = sb.charAt(i);
            second = sb.charAt(i + 1);
            sb.setCharAt(i, second);
            sb.setCharAt(i + 1, first);
            closeWords.add(sb.toString());
        }
        return closeWords;
    }
    
    /**
     * Private static helper method that creates a list of all 1-Edit
     * subsitutions for a given string.
     * @param seq - the word to be operated on
     * @return a list of all Strings 1 Edit Distance away using the
     * subsitution operation.
     */
    private static List<String> subsitutionOperation(String seq) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < seq.length(); i++) {
            StringBuffer sb = new StringBuffer(seq);
            sb.setCharAt(i, 'A');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'B');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'C');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'D');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'E');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'F');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'G');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'H');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'I');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'J');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'K');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'L');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'M');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'N');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'O');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'P');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'Q');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'R');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'S');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'T');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'U');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'V');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'W');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'X');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'Y');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
            sb.setCharAt(i, 'Z');
            if (!sb.toString().equals(seq)) {
                list.add(sb.toString());
            }
        }
        return list;
    }
    
    /**
     * Private static helper method that creates a list of all 1-Edit
     * insertions for a given string.
     * @param seq - the word to be operated on
     * @return a list of all Strings 1 Edit Distance away using the
     * insertion operation.
     */
    private static List<String> insertionOperation(String seq) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <= seq.length(); i++) {
            StringBuffer sb = new StringBuffer(seq);
            list.add(sb.insert(i, 'A').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'B').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'C').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'D').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'E').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'F').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'G').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'H').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'I').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'J').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'K').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'L').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'M').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'N').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'O').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'P').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'Q').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'R').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'S').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'T').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'U').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'V').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'W').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'X').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'Y').toString());
            sb.deleteCharAt(i);
            list.add(sb.insert(i, 'Z').toString());
            sb.deleteCharAt(i);
        }
        return list;
    }
}
