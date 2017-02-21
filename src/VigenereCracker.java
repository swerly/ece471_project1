import com.sun.xml.internal.fastinfoset.util.CharArray;

import java.util.*;

/**
 * Created by Seth on 2/10/2017.
 */
public class VigenereCracker extends AbstractCracker{
    public static int KEYS_TO_TEST = 100;
    private ArrayList<Integer> potentialPeriods;
    private HashMap<Integer, String[]> sequences;

    private static char[] upperAlpha = {'A','B','C','D','E','F','G','H','I','J','K',
            'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static char[] lowerAlpha = {'a','b','c','d','e','f','g','h','i','j','k',
            'l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private static char[][]tableUpper;
    private static char[][]tableLower;

    static{
        //populate decryption tables
        tableLower = new char[26][26];
        tableUpper = new char[26][26];
        int a;
        for (int i = 0; i < 26; i++){
            for (int j = 0; j < 26; j++){
                a = i+j;
                if (a >= 26) a-=26;
                tableLower[i][j] = lowerAlpha[a];
                tableUpper[i][j] = upperAlpha[a];
            }
        }
    }

    public VigenereCracker(CipherAnalytics analytics){
        super(analytics);
    }

    @Override
    public boolean testCipher(){

        //todo: see if kasiski test agrees with these values
        potentialPeriods = getPotentialPeriods();
        String msg = potentialPeriods.size() > 0 ?
                "Possible key lengths for the Vigenere cipher were found.":
                "No likely keys < " + KEYS_TO_TEST + " characters in length";
        CipherCracker.printStatusMessage(msg, null);
        return potentialPeriods.size() > 0;
    }

    @Override
    public void runCracker(){
        String msg = "Using frequency analysis to solve for key length " ;
        for (int period : potentialPeriods){
            CipherCracker.printStatusMessage(msg + period + "...", null);
            String key = getKeyFromSequences(sequences.get(period));

            System.out.println();
            String msg2 = "I have generated a key, but it may not be correct.\nI need your input to determine the correctness of the key";
            String msg3 = "Potential key: " + key;
            String msg4 = "Snippet of decrypted text: " + decryptVigenere(key, analytics.getCipherText()).substring(0, 40);
            String msg5 = "";
            String msg6 = "If this key looks incorrect, type in a new key.    \n    Enter \"full text\" to view full cipher text\n" +
                    "    Enter \"info\" to display decryption information\n    Enter \"try next\" to try another potential key length\n    Enter \"q\" to exit";

            ArrayList<String> messages = new ArrayList<>();
            messages.add(msg3);
            messages.add(msg4);
            messages.add(msg5);
            messages.add(msg6);

            CipherCracker.printStatusMessage(msg2, messages);
            String currentKey = key;
            while (true) {
                System.out.print("\n    Enter your response: ");
                Scanner scanner = new Scanner(System.in);
                String scanInput = scanner.nextLine();
                if (scanInput.equals("q") || scanInput.equals("Q")) return;
                else if (scanInput.length() == 0) continue;
                else if (scanInput.equals("full text")){
                    System.out.printf("\nDecrypted Message:\n\n");
                    printDecryptedTextEnd( decryptVigenere(currentKey, analytics.getCipherText()) );
                    System.out.printf("\n");
                    //System.out.println(decryptVigenere(currentKey, analytics.getCipherText()));
                }
                else if (scanInput.equals("info")){
                    String m0 = ("Cipher Analysis Information");
                    ArrayList<String> infoBlock = new ArrayList<>();
                    infoBlock.add("Cipher Type: " + CipherCracker.VIGENERE_CIPHER);
                    infoBlock.add("Guessed Key Length: " + period);
                    infoBlock.add("Guessed Key: " + key);
                    CipherCracker.printStatusMessage(m0, infoBlock);
                }
                else if (scanInput.equals("try next")){
                    if (period == potentialPeriods.get(potentialPeriods.size()-1)){
                        CipherCracker.printStatusMessage("There were no more potential keys found (<=" + KEYS_TO_TEST + " characters)\n\nExiting...\n\n", null);
                    }
                    break;
                }
                else {
                    currentKey = scanInput;
                    String msg7 = "Snippet of decrypted text: " + decryptVigenere(currentKey, analytics.getCipherText()).substring(0, 40);
                    String msg9 = "If this key is incorrect, type in the new key. Leave blank to view full cipher text or enter q to quit";
                    ArrayList<String> messages2 = new ArrayList<>();
                    messages2.add(msg7);
                    messages2.add(msg9);
                    CipherCracker.printStatusMessage("", messages2);
                }
            }
        }
    }

    public static String decryptVigenere(String key, String encrypted){
        char[] datKey = key.toCharArray();
        char[][] currentTable = AnalysisUtils.isLowercase(key.charAt(0)) ? tableLower : tableUpper;
        char shifter = AnalysisUtils.isLowercase(key.charAt(0)) ? 'a' : 'A';
        String decrypted = "";

        for (int i = 0; i<encrypted.length(); i++){

            char decryptor = datKey[i%key.length()];
            char currentCipherChar = encrypted.charAt(i);
            int normalizedDecryptor = decryptor-shifter;
            int normalizedCipherChar = currentCipherChar-shifter;

            int decryptedLetter = normalizedCipherChar-normalizedDecryptor < 0 ?
                    normalizedCipherChar-normalizedDecryptor+26 : normalizedCipherChar-normalizedDecryptor;
            decrypted += (char) (decryptedLetter + shifter);
        }

        return decrypted;
    }

    public String getKeyFromSequences(String[] currentSequences){
        String key = "";
        for (String currentSequence : currentSequences){
            key += getShiftedLetterFromSequence(currentSequence);
        }

        return key;
    }

    public char getShiftedLetterFromSequence(String currentSequence){
        int shift;
        AnalysisUtils au = new AnalysisUtils(currentSequence);
        LinkedHashMap<Character, Float> currentFreq = au.getTopCharacterFrequencies();

        List<Character> frequenciesList = new ArrayList<>(currentFreq.keySet());


        return getShiftedByMostCommon(frequenciesList.get(0));
    }

    private char getShiftedByMostCommon(char c){
        char shifter = AnalysisUtils.isLowercase(c) ? 'a' : 'A';
        char mostCommon = AnalysisUtils.isLowercase(c) ? 'e' : 'E';

        int working = c-shifter;
        int mostCommonNormalized = mostCommon-shifter;
        int newChar = working - mostCommonNormalized < 0 ? working-mostCommonNormalized+26 : working-mostCommonNormalized;
        return (char) (newChar+shifter);

    }

    private ArrayList<Integer> getPotentialPeriods(){
        ArrayList<Integer> potentialPeriods = new ArrayList<>();
        ArrayList<Double> averageICs = getAverageICs();
        for (int i = 0; i < averageICs.size(); i++){
            double curIC = averageICs.get(i);

            if (curIC > .06) potentialPeriods.add(i+1);
        }

        return potentialPeriods;
    }

    private ArrayList<Double> getAverageICs(){
        ArrayList<Double> averageICs = new ArrayList<>();
        sequences = new HashMap<>();

        //add the first index of coincidence to the list (ic for key length = 1)
        averageICs.add(analytics.getIndexOfCoincidence());
        sequences.put(1, new String[]{analytics.getCipherText()});

        //get ic for each key length (up to KEYS_TO_TEST)
        for (int keyLength = 2; keyLength<=KEYS_TO_TEST; keyLength++){
            //our key length determines the number of strings we split the cipher text into
            String[] sequences = new String[keyLength];
            //initialize all the strings to be empty
            initializeSequences(sequences);

            String cipherText = analytics.getCipherText();
            //add each character to the respective sequence
            for (int j = 0; j < cipherText.length(); j++){
                sequences[j%keyLength] += cipherText.charAt(j);
            }
            double ic = getSequenceAverage(sequences);
            this.sequences.put(keyLength, sequences);
            averageICs.add(ic);
        }
        //printAverageICs(averageICs);
        return averageICs;
    }

    private void initializeSequences(String[] s){
        for (int i = 0; i< s.length; i++){
            s[i] = "";
        }
    }

    private double getSequenceAverage(String[] sequences){
        double avg = 0;
        int i = 0;
        AnalysisUtils analysisUtils = new AnalysisUtils();

        for (; i < sequences.length; i++){
            avg += analysisUtils.getIndexOfCoincidence(sequences[i]);
        }
        avg /= i;
        return avg;
    }

    private void printAverageICs(ArrayList<Double> averageICs){
        for (int i = 0; i < averageICs.size(); i++){
            System.out.printf("%d: %.7f\n", i+1, averageICs.get(i));
        }
    }

    public static void printDecryptedTextEnd(String plainT){
        int lineSize = 60;
        int lineCount = 0;

        for (int i = 0; i<plainT.length(); i++){
            System.out.printf("%c", plainT.charAt( i ));

            lineCount++;
            if (lineCount >= lineSize){
                System.out.println();
                lineCount=0;
            }
        }
    }

}
