/**
 * Created by Seth on 2/11/2017.
 */

import java.util.*;

public class SubstitutionCracker extends AbstractCracker {

    private String plainText;
    private ArrayList<Boolean> unusedCipherLetters = new ArrayList<Boolean>();
    private ArrayList<Boolean> unusedPlainLetters = new ArrayList<Boolean>();
    private ArrayList<Integer> mappedCipher = new ArrayList<Integer>();
    Scanner reader = new Scanner(System.in);

    public SubstitutionCracker(CipherAnalytics analytics) {
        super(analytics);
    }

    @Override
    public boolean testCipher() {
        return false;
    }

    @Override
    public void runCracker() {
        //todo: does this text look decrypted?
        //if not, try just shifting by e
    }

    public void run( String cipherText ) {

        Boolean goal = false;
        int goalChecker = 0;
        int tempChar = 0;
        char EOA = ' ';
        char inputCipherLetter = ' ';
        char inputPlainLetter = ' ';
        char yORn = ' ';
        String tempPT = plainText;
        LinkedHashMap<Character, Float> currcharFrequencies = analytics.getCharacterFreqencies();
        int numSingleDigits = 0;

        LinkedHashMap<Character, Float> charFreqMap = analytics.getCharacterFreqencies();
        List<Float> frequenciesList = new ArrayList<>(charFreqMap.values());
        List<Character> charList = new ArrayList<>(charFreqMap.keySet());

        plainText = cipherText;

        for( int i = 0; i < 26; i++ ){
            unusedCipherLetters.add(i,false);
            unusedPlainLetters.add(i, false);
            mappedCipher.add(i, -1);
        }

        while( goal == false ) {

            //Print Template for potential matchings (freq of english and freq of ctext)//////////
            System.out.printf("\n+---------------------------------------------------------------------------------------------------------------------------+\n");
            System.out.printf("|                                                    Letter Frequencies:                                                    |\n");
            System.out.printf("+---------------+-----------------------------------------------------------------------------------------------------------+\n");
            System.out.printf("|  In Message:  | ");

            for( int i = 0; i < 15; i++){

                System.out.printf(" %c=%.1f", charList.get( i ), frequenciesList.get( i ) * 100 ); //replace 'a' with char and 5.432 with float

                if( ( frequenciesList.get( i ) * 100 ) < 10.0 ){ //replace 5 with float
                    System.out.printf(" ");
                }

            }

            System.out.printf(" |\n");
            System.out.printf("+---------------+-----------------------------------------------------------------------------------------------------------+\n");
            System.out.printf("|  In English:  |  e=12.7 t=9.1 a=8.2  o=7.5  i=7.0  n=6.8  s=6.3  h=6.1  r=6.0  d=4.3  l=4.0  u=2.8  c=2.8  w=2.4  m=2.4   |\n");
            System.out.printf("+---------------+-----------------------------------------------------------------------------------------------------------+\n");


            tempPT = plainText;

            System.out.printf("\nChoose a letter from the ciphertext to convert (Uppercase Notation Required): ");
            do{
                inputCipherLetter = reader.next().charAt(0);

                if( mappedCipher.get( inputCipherLetter - 65 ) != -1 ){
                    System.out.printf("\nThis letter in the ciphertext has already been decrypted, would you like to change it (y/n): ");
                    do{
                        yORn = reader.next().charAt(0);
                    }while( yORn != 'y' && yORn != 'n' );
                    if( yORn == 'y' ){
                        tempChar = mappedCipher.get( inputCipherLetter - 65 );
                        plainText = plainText.replace( (char)tempChar, inputCipherLetter );
                        unusedCipherLetters.set( inputCipherLetter - 65, false );
                        unusedPlainLetters.set( tempChar - 97 , false );
                        mappedCipher.set( inputCipherLetter - 65, -1 );
                        tempPT = plainText;
                    }
                    else{
                        System.out.printf("\nChoose a different letter from the ciphertext to conver (Uppercase Notation Required): ");
                        inputCipherLetter = 'a';
                    }
                }

            }while(inputCipherLetter < 65 || inputCipherLetter > 90);
            yORn = ' ';
            System.out.printf("Choose a letter from the plaintext to convert (Lowercase Notation Required): ");
            do{
                inputPlainLetter = reader.next().charAt(0);

                for( int i = 0; i < mappedCipher.size(); i++ ){
                    if( mappedCipher.get( i ) == inputPlainLetter ){

                        unusedCipherLetters.set( i, false );
                        unusedPlainLetters.set( inputPlainLetter - 97 , false );//maybe keep
                        mappedCipher.set( i, -1 );
                        plainText = plainText.replace( inputPlainLetter, (char)(i+65) );
                        tempPT = plainText;

                        break;
                    }
                }

            }while(inputPlainLetter < 97 || inputPlainLetter > 122);

            unusedCipherLetters.set( inputCipherLetter - 65, true );
            unusedPlainLetters.set( inputPlainLetter - 97, true );
            mappedCipher.set( inputCipherLetter - 65, (int)inputPlainLetter );

            System.out.printf("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

            plainText = plainText.replace( inputCipherLetter, inputPlainLetter );
            System.out.printf("\nCiphertext snippet:\n\n");
            printDecryptedText(cipherText);
            System.out.printf("\nPlaintext snippet:\n\n");
            printDecryptedText(plainText);
            System.out.printf("\n" + "Would you like to keep this change (y/n): ");

            do{
                yORn = reader.next().charAt(0);
            }while( yORn != 'y' && yORn != 'n' );

            if( yORn == 'n' ){ //fix?
                unusedCipherLetters.set( inputCipherLetter - 65, false );
                unusedPlainLetters.set( inputPlainLetter - 97, false );
                mappedCipher.set( inputCipherLetter - 97, -1 );
                plainText = tempPT;
            }

            for( int i = 0; i < 26; i++ ){
                if( mappedCipher.get( i ) == -1 ){
                    goalChecker++;
                }
            }
            if( goalChecker == 0 ) {
                System.out.printf("\nAll letters have been substituted, would you like to end (e), reset (r), or continue (c): ");
                do {
                    EOA = reader.next().charAt(0);
                } while (EOA != 'e' && EOA != 'r' && EOA != 'c');
                if (EOA == 'e') {
                    System.out.printf("\nFinal plaintext:\n\n");
                    printDecryptedTextEnd(plainText);
                    goal = true; //end algorithm
                }
                else if ( EOA == 'r' ){
                    plainText = cipherText; //resets plaintext
                    for( int i = 0; i < 26; i++ ){
                        unusedCipherLetters.set( i, false );
                        unusedPlainLetters.set( i, false );
                        mappedCipher.set( i, -1 );
                    }
                }
            }

            //reset inputs/checkers

            goalChecker = 0;
            EOA = ' ';
            inputCipherLetter = ' ';
            inputPlainLetter = ' ';
            yORn = ' ';

        }

    }

    public void printDecryptedText(String plainT){
        int lineSize = 60;
        int lineCount = 0;

        for (int i = 0; i<180; i++){
            System.out.printf("%c", plainT.charAt( i ));

            lineCount++;
            if (lineCount >= lineSize){
                System.out.println();
                lineCount=0;
            }
        }
    }

    public void printDecryptedTextEnd(String plainT){
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