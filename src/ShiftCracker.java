import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Seth on 2/8/2017.
 */
public class ShiftCracker extends AbstractCracker{
    private static float TESTING_DFFERENCE = 1f;
    private static float LETTERS_TO_TEST = 3;
    private int cipherShamt;

    public ShiftCracker(CipherAnalytics analytics){
        super(analytics);
    }

    @Override
    public boolean testCipher(){
        if(!testFrequencies()){
            String msg = "Top cipher letter frequencies don't match common English letter frequencies.\n" +
                    "This was most likely not encrypted using shifts";
            CipherCracker.printStatusMessage(msg, null);
            return false;
        }

        if (!testShiftAmt()){
            String msg = "When comparing cipher letter frequencies to common English letter frequencies, the shifts do not match up." +
                    "This was most likely not encrypted using shifts";
            CipherCracker.printStatusMessage(msg, null);
            return false;
        }

        String msg = "When comparing cipher letter frequencies to common English letter frequencies, the shifts seem to match!\n" +
                "This was most likey encrypted using a shift of " + this.cipherShamt + ", but we will confirm later";
        CipherCracker.printStatusMessage(msg, null);
        return true;
    }

    @Override
    public void runCracker() {
        while(true) {
            Scanner scanner = new Scanner(System.in);
            String shifted = getShiftedText(analytics.getCipherText(), cipherShamt);
            System.out.println("\n\nHere is a snippet of the text decrypted with a shift of " + cipherShamt);
            System.out.println("    " + shifted.substring(0, 60));
            System.out.println("Please select an option (1-4):\n    1. Print Full Text\n    2. Print Shift Amount\n    3. Try another cipher\n    4. Exit Program");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    printDecryptedText(analytics.getCipherText(), cipherShamt);
                    break;
                case 2:
                    System.out.println("Shift amount used: " + cipherShamt);
                    break;
                case 3:
                    return;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Please select a choice from the list");
                    break;
            }
        }
    }

    public boolean testFrequencies(){
        LinkedHashMap<Character, Float> charFreqMap = analytics.getCharacterFreqencies();
        LinkedHashMap<Character, Float> commonCharFreqMap = AnalysisUtils.ENGLISH_LETTER_FREQUENCIES;

        List<Float> commonFrequenciesList = new ArrayList<>(commonCharFreqMap.values());
        List<Float> frequenciesList = new ArrayList<>(charFreqMap.values());

        //check the top 5 english letter frequencies with the top 5 in this file
        for (int i=0; i<commonFrequenciesList.size(); i++){
            //if they are within a specified range TESTING_DIFFERENCE
            if (!inRange(commonFrequenciesList.get(i), frequenciesList.get(i))){
                return false;
            }
        }

        return true;
    }

    private boolean testShiftAmt(){
        LinkedHashMap<Character, Float> charFreqMap = analytics.getCharacterFreqencies();
        LinkedHashMap<Character, Float> commonCharFreqMap = AnalysisUtils.ENGLISH_LETTER_FREQUENCIES;

        List<Character> commonFrequenciesList = new ArrayList<>(commonCharFreqMap.keySet());
        List<Character> frequenciesList = new ArrayList<>(charFreqMap.keySet());

        //get the initial shift amount
        int shamt = getShiftAmt(commonFrequenciesList.get(0), frequenciesList.get(0));

        //check the shift amts
        for (int i=1; i < LETTERS_TO_TEST; i++){
            int currentShamt = getShiftAmt(commonFrequenciesList.get(i), frequenciesList.get(i));

            //if they are within a specified range TESTING_DIFFERENCE
            if (currentShamt != shamt){
                return false;
            }
        }

        this.cipherShamt = shamt;
        return true;
    }

    private boolean inRange(float commonFreq, float freq){
        float freqToTest = freq*100;
        return (freqToTest <= (commonFreq+TESTING_DFFERENCE)) && (freqToTest >= (commonFreq-TESTING_DFFERENCE));
    }

    public static int getShiftAmt(char p, char c){
        char shifter = AnalysisUtils.isLowercase(p) ? 'a' : 'A';
        char pNorm = (char) (p-shifter);
        char cNorm = (char) (c-shifter);
        return cNorm - pNorm < 0 ? cNorm - pNorm + 26: cNorm - pNorm;
    }

    public void printDecryptedText(String cipher){

        printDecryptedText(cipher, cipherShamt);
    }

    public static void printDecryptedText(String cipher, int shamt){
        int lineSize = 60;
        int lineCount = 0;

        for (int i = 0; i<cipher.length(); i++){
            char shifted = getShiftedChar(cipher.charAt(i), shamt);
            System.out.printf("%c", shifted);

            lineCount++;
            if (lineCount >= lineSize){
                System.out.println();
                lineCount=0;
            }
        }
    }

    public String getShiftedText(String cipher, int shamt){
        int lineSize = 60;
        int lineCount = 0;
        String ret = "";

        for (int i = 0; i<cipher.length(); i++){
            char shifted = getShiftedChar(cipher.charAt(i), shamt);
            ret+=shifted;
        }
        return ret;
    }

    public static char getShiftedChar(char c, int shift){
        char shifter = AnalysisUtils.isLowercase(c) ? 'a' : 'A';
        char working = (char) (c-shifter);

        int newVal = working - shift < 0 ? working-shift+26 : working-shift;

        return (char) (newVal+shifter);
    }
}
