/**
 * Created by Seth on 2/11/2017.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

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

        plainText = cipherText;

        for( int i = 0; i < 26; i++ ){
            unusedCipherLetters.add(i,false);
            unusedPlainLetters.add(i, false);
            mappedCipher.add(i, -1);
        }

        while( goal == false ) {

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
                        System.out.printf("\nThis letter in the plaintext has already been used, please try again: ");
                        inputPlainLetter = 'A';
                        break;
                    }
                }

            }while(inputPlainLetter < 97 || inputPlainLetter > 122);

            unusedCipherLetters.set( inputCipherLetter - 65, true );
            unusedPlainLetters.set( inputPlainLetter - 97, true );
            mappedCipher.set( inputCipherLetter - 65, (int)inputPlainLetter );

            plainText = plainText.replace( inputCipherLetter, inputPlainLetter );
            System.out.printf("\nCurrent decoded scheme:\n\n");
            printDecryptedText(plainText);
            System.out.printf("\n\n" + "Would you like to keep this change (y/n): ");

            do{
                yORn = reader.next().charAt(0);
            }while( yORn != 'y' && yORn != 'n' );

            if( yORn == 'n' ){
                unusedCipherLetters.set( inputCipherLetter - 65, false );
                unusedPlainLetters.set( inputPlainLetter - 97, false );
                mappedCipher.set( inputPlainLetter - 97, -1 );
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
        int lineSize = 150;
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