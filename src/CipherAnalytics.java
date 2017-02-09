import java.util.LinkedHashMap;

/**
 * Created by Seth on 2/8/2017.
 */
public class CipherAnalytics{
    private LinkedHashMap<Character, Float> characterFrequencies;
    private LinkedHashMap<String, Integer> topDigrams;
    private double indexOfCoincidence;

    public CipherAnalytics(){
        this(null, null, 0);
    }
    public CipherAnalytics(LinkedHashMap<Character, Float> characterFreqencies, LinkedHashMap<String, Integer> topDigrams, double indexOfCoincidence) {
        this.characterFrequencies = characterFreqencies;
        this.topDigrams = topDigrams;
        this.indexOfCoincidence = indexOfCoincidence;
    }

    public void setAnalyticsFromCipherText(String cipherText){
        AnalysisUtils analysisUtils = new AnalysisUtils(cipherText);
        characterFrequencies = analysisUtils.getTopCharacterFrequencies();
        topDigrams = analysisUtils.getTopDigrams();
        indexOfCoincidence = analysisUtils.getIndexOfCoincidence(cipherText);
    }

    public LinkedHashMap<Character, Float> getCharacterFreqencies() {
        return characterFrequencies;
    }

    public LinkedHashMap<String, Integer> getTopDigrams() {
        return topDigrams;
    }

    public double getIndexOfCoincidence() {
        return indexOfCoincidence;
    }
}
