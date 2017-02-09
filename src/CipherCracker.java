import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Seth on 2/7/2017.
 */
public class CipherCracker {
    private String cipherText;
    private CipherAnalytics analytics;

    public CipherCracker(String cipherLocation) {
        this.cipherText = getCipherTextFromFile(cipherLocation);
    }

    public void run(){
        //run some analysis on the cipher text and store the results in this analytics object
        analytics = new CipherAnalytics();
        analytics.setAnalyticsFromCipherText(cipherText);

        //create a shift cracker to work with the cipher text
        ShiftCracker shiftCracker = new ShiftCracker(analytics);
        if (shiftCracker.testShift()){
            shiftCracker.printDecryptedText(cipherText);
        } else {
            System.out.println("Cipher cannot be cracked by currently implemented methods");
        }

        return;

    }

    private String getCipherTextFromFile(String path )
    {
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
}
