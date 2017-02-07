import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by Seth on 2/7/2017.
 */
public class CipherCracker {
    private String cipherText;

    public CipherCracker(String cipherLocation) {
        this.cipherText = getCipherTextFromFile(cipherLocation);
        System.out.println(cipherText.charAt(1));
    }

    public void run(){
        AnalysisUtils analysisUtils = new AnalysisUtils(cipherText);
        HashMap<Character, Float> characterFrequencies = analysisUtils.getTopCharacterFrequencies();
        HashMap<String, Integer> topDigrams = analysisUtils.getTopDigrams();
        double ic = analysisUtils.getIndexOfCoincidence(cipherText);
        System.out.println("Test");
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
