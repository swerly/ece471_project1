import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Seth on 2/7/2017.
 */
public class CipherCracker {
    public static final String SHIFT_CIPHER = "Shift Cipher";
    public static final String SUBSTITUTION_CIPHER = "Substitution Cipher";
    public static final String VIGENERE_CIPHER = "Vigenere Cipher";
    public static final String COLUMNAR_TRANSPOSITION_CIPHER = "Columnar Transposition Cipher";
    public static final String ONE_TIME_PAD_CIPHER = "One-Time Pad Cipher";
    private String cipherText;
    private CipherAnalytics analytics;
    private ArrayList<String> possibleCipherTypes;
    private ArrayList<String> narrowedDownCipherTypes;
    private int cipherChoice;
    private boolean autoORnot;
    Scanner reader = new Scanner(System.in);

    private HashMap<String, AbstractCracker> crackers;

    public static void printStatusMessage(String msg, List<String> list) {
        System.out.println("\n" + msg);

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                System.out.println("    " + list.get(i));
            }
        }
    }

    public CipherCracker(String cipherLocation) {
        this.cipherText = getCipherTextFromFile(cipherLocation);
    }

    public void run() {
        //run some analysis on the cipher text and store the results in this analytics object
        analytics = new CipherAnalytics();
        analytics.setCipherText(cipherText);
        analytics.runAnalysis();
        crackers = new HashMap<>();

        //put here
        beginProgram();



    }

    private String getCipherTextFromFile(String path) {
        Charset encoding = Charset.defaultCharset();
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Could not find input file " + path);
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    private ArrayList<String> determineTypeOfCipher() {
        ArrayList<String> possibleTypes = new ArrayList<>();

        /*if (utils.monogramsMatchCommonEnglish(analytics) && analytics.getIndexOfCoincidence() > .06){
            possibleTypes.add(COLUMNAR_TRANSPOSITION_CIPHER);
        } else */
        if (analytics.getIndexOfCoincidence() > .06) {
            possibleTypes.add(COLUMNAR_TRANSPOSITION_CIPHER);
            possibleTypes.add(SHIFT_CIPHER);
            possibleTypes.add(SUBSTITUTION_CIPHER);
        } else {
            possibleTypes.add(VIGENERE_CIPHER);
            possibleTypes.add(ONE_TIME_PAD_CIPHER);
        }
        possibleCipherTypes = possibleTypes;
        printStatusMessage("Based of the Index of Coincidence, this cipher could be enycrypted with: ", possibleTypes);
        return narrowDown(possibleTypes);
    }

    private AbstractCracker getCipherCracker(String cipher) {
        return crackers.get(cipher);
    }

    private void createCracker(String type) {
        AbstractCracker cracker = null;
        switch (type) {
            case SHIFT_CIPHER:
                cracker = new ShiftCracker(analytics);
                break;
            case SUBSTITUTION_CIPHER:
                cracker = new SubstitutionCracker(analytics);
                break;
            case VIGENERE_CIPHER:
                cracker = new VigenereCracker(analytics);
                break;
            case COLUMNAR_TRANSPOSITION_CIPHER:
                cracker = new ColumnarTranspositionCracker(analytics);
                break;
        }
        crackers.put(type, cracker);
    }

    private ArrayList<String> narrowDown(ArrayList<String> possibleTypes) {
        ArrayList<String> narrowed = new ArrayList<>();

        printStatusMessage("Narrowing down the cipher types...", null);

        for (String type : possibleTypes) {
            if (!type.equals(ONE_TIME_PAD_CIPHER)) {
                createCracker(type);
            }
        }

        for (String type : possibleTypes) {
            AbstractCracker cracker = crackers.get(type);
            if (cracker != null) {
                printStatusMessage("Checking if " + type + " could have been used...", null);
                if (cracker.testCipher()) {
                    narrowed.add(type);
                }
                if (cracker instanceof SubstitutionCracker && narrowed.size() == 0) {
                    printStatusMessage("This may be a substitution cipher, will confirm or deny later", null);
                    narrowed.add(type);
                } else if (cracker instanceof SubstitutionCracker) {
                    printStatusMessage("This may be a substitution cipher, will confirm or deny later", null);
                }
            }
        }

        if (narrowed.size() == 0 && possibleTypes.contains(ONE_TIME_PAD_CIPHER)) {
            narrowed.add(ONE_TIME_PAD_CIPHER);
            printStatusMessage("Having a hard time cracking this one. One-time pad may have been used...", null);
        }

        return narrowed;
    }

    private void printNarrowedDownTypes() {
        if (narrowedDownCipherTypes.size() != 0) {
            System.out.println("\n\nI think the cipher is encrypted with:");

            for (String s : narrowedDownCipherTypes) {
                System.out.println("    -" + s);
            }

            System.out.println("Let's see if I was correct!");
        }
    }

    public void beginProgram() {

        int typeOfCipher;
        Scanner reader = new Scanner(System.in);

        System.out.printf("\nDo you know the type of cpher used to encrypt (1-6)?\n");
        System.out.printf("    1: Shift Cipher\n    2: Substitution Cipher\n    3: Vigenere Cipher\n    4: Columnar Transposition Cipher\n    5: One-Time Pad Cipher\n    6: I don't know the cipher\n\nYour Choice: ");
        do{
            typeOfCipher = reader.nextInt();
            if( typeOfCipher < 1 || typeOfCipher > 6 ){
                System.out.printf("\nInvalid choice, please choose again: ");
            }
        }while( typeOfCipher < 1 || typeOfCipher > 6 );

        switch ( typeOfCipher ){

            case 1: beginShift();
                    break;
            case 2: beginSubstitution();
                    break;
            case 3: beginVeg();
                    break;
            case 4: beginCT();
                    break;
            case 5: beginOTP();
                    break;
            case 6: beginAuto();

        }
    }

    public void beginShift(){
        ShiftCracker sc = new ShiftCracker(analytics);
        Scanner reader = new Scanner(System.in);
        System.out.printf("\nEnter in shift amount: ");
        int shamt = reader.nextInt();

        System.out.println("Your decrypted text: ");
        ShiftCracker.printDecryptedText(analytics.getCipherText(), shamt);

    }
    public void beginSubstitution(){
        SubstitutionCracker sc = new SubstitutionCracker(analytics);
        Scanner reader = new Scanner(System.in);
        String subs;
        while (true) {
            System.out.printf("\nEnglish Letters:     ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            System.out.printf("\nSubstituted Letters: ");
            subs = reader.nextLine();
            if (subs.length() != 26){
                System.out.println("Please enter in a substitution for all the characters.");
            } else break;
        }

        //SubstitutionCracker.runManual(analytics.getCipherText(), subs);


    }
    public void beginCT(){
        cipherChoice = 3;
        autoORnot = false;

    }
    public void beginVeg(){
        cipherChoice = 4;
        autoORnot = false;

    }
    public void beginOTP(){
        cipherChoice = 5;
        autoORnot = false;

    }
    public void beginAuto(){

        System.out.printf("\nRunning auto-decryptor...\n");

        narrowedDownCipherTypes = determineTypeOfCipher();
        printNarrowedDownTypes();

        if (narrowedDownCipherTypes.size() == 1) {
            AbstractCracker cracker = crackers.get(narrowedDownCipherTypes.get(0));

            if (cracker instanceof ShiftCracker) {
                System.out.printf("\nDecrytped Message:\n\n");
                ((ShiftCracker) cracker).printDecryptedText(cipherText);
            } else if (narrowedDownCipherTypes.get(0) == "Substitution Cipher") {
                ((SubstitutionCracker) cracker).run(cipherText);
            } else if (narrowedDownCipherTypes.get(0) == "Vigenere Cipher") {
                ((VigenereCracker) cracker).runCracker();
            } else if (narrowedDownCipherTypes.get(0) == "Columnar Transposition Cipher") {
                System.out.printf("\nWe have found that none of the provided ciphertexts used the\n");
                System.out.printf("Columnar-Transposition Cipher, so we never created the cracker.\n");
            } else if (narrowedDownCipherTypes.get(0) == "One-Time Pad Cipher") {
                System.out.printf("\nWe have found the One-Time Pad Cipher to be perfectly secure.\n");
            }
        }
    }
}
