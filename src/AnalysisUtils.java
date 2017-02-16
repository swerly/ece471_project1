import java.util.*;

/**
 * Created by Seth on 2/7/2017.
 */
public class AnalysisUtils {

    private static float TESTING_DFFERENCE = 1f;

    public static LinkedHashMap<Character, Float> ENGLISH_LETTER_FREQUENCIES;
    public static LinkedHashMap<Character, Float> ENGLISH_LOWERCASE_LETTER_FREQUENCIES;
    static{
        ENGLISH_LETTER_FREQUENCIES = new LinkedHashMap<>();
        ENGLISH_LETTER_FREQUENCIES.put('E', 12.10f);
        ENGLISH_LETTER_FREQUENCIES.put('T', 8.94f);
        ENGLISH_LETTER_FREQUENCIES.put('A', 8.55f);
        ENGLISH_LETTER_FREQUENCIES.put('O', 7.47f);
        ENGLISH_LETTER_FREQUENCIES.put('I', 7.33f);
        ENGLISH_LOWERCASE_LETTER_FREQUENCIES = new LinkedHashMap<>();
        ENGLISH_LOWERCASE_LETTER_FREQUENCIES.put('e', 12.10f);
        ENGLISH_LOWERCASE_LETTER_FREQUENCIES.put('t', 8.94f);
        ENGLISH_LOWERCASE_LETTER_FREQUENCIES.put('a', 8.55f);
        ENGLISH_LOWERCASE_LETTER_FREQUENCIES.put('o', 7.47f);
        ENGLISH_LOWERCASE_LETTER_FREQUENCIES.put('i', 7.33f);
    }
    private static int DIGRAMS_TO_RETURN = 15;
    private static int MONOGRAMS_TO_TEST = 4;
    private String cipherText;

    //TODO: some kind of error checking / input protection

    public AnalysisUtils(){
        this(null);
    }

    public AnalysisUtils(String cipherText){
        this.cipherText = cipherText;
    }

    public LinkedHashMap<String, Integer> getTopDigrams(){
        //don't really need to convert to a char array here, could have just used String.charAt(), but whatever
        char[] cipher = cipherText.toCharArray();
        //make a hasmap to hold the digrams
        HashMap<String, Integer> digramMap = new HashMap<>();
        //go through all letter pairs, and add them to the map / increment their count
        for (int i = 0; (i+1) < cipher.length; i++){
            char[] curDigramChars = new char[2];
            curDigramChars[0] = cipher[i];
            curDigramChars[1] = cipher[i+1];
            String curDigram = new String(curDigramChars);
            int count = digramMap.containsKey(curDigram) ? digramMap.get(curDigram) : 0;
            digramMap.put(curDigram, count+1);
        }

        //some hashmap sorting function from Stack overflow
        Object[] a = digramMap.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });

        //get the top elements from the array and create a hashmap to return
        int totalToReturn = a.length > DIGRAMS_TO_RETURN ? DIGRAMS_TO_RETURN : a.length;
        LinkedHashMap<String, Integer> mostCommon = new LinkedHashMap<>();
        for (int i = 0; i < totalToReturn; i++){
            Map.Entry<String, Integer> current = (Map.Entry<String, Integer>) a[i];
            mostCommon.put(current.getKey(), current.getValue());
        }

        return mostCommon;
    }

    public LinkedHashMap<Character, Float>  getTopCharacterFrequencies(){
        //initialize our counts array
        HashMap<Character, Integer> characterCount = new HashMap<>();

        for (int i = 0; i < cipherText.length(); i++){
            //get the character we are currently looking at
            char currentChar = cipherText.charAt(i);
            int count = characterCount.containsKey(currentChar) ? characterCount.get(currentChar) : 0;
            characterCount.put(currentChar, count+1);
        }
        //some hashmap sorting function from Stack overflow
        //converts entries to an array
        Object[] a = characterCount.entrySet().toArray();
        //sorts the array
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<Character, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<Character, Integer>) o1).getValue());
            }
        });

        //get the top elements from the array and create a hashmap to return
        int totalToReturn = a.length > DIGRAMS_TO_RETURN ? DIGRAMS_TO_RETURN : a.length;
        LinkedHashMap<Character, Float> mostCommon = new LinkedHashMap<>();
        for (int i = 0; i < totalToReturn; i++){
            Map.Entry<Character, Integer> current = (Map.Entry<Character, Integer>) a[i];
            mostCommon.put(current.getKey(), ((float)current.getValue()/cipherText.length()));
        }

        return mostCommon;
    }

    public double getIndexOfCoincidence(String s){
        int i;
        int N = 0;
        double sum = 0.0;
        double total = 0.0;
        int[] values = new int[26];

        boolean isLower = s.charAt(0) >= 'a' && s.charAt(0) <= 'z';

        //calculate frequency of each letter in s
        int ch;
        for(i=0; i<s.length(); i++){
            ch = isLower ? s.charAt(i)-'a' : s.charAt(i)-'A';
            if(ch>=0 && ch<26){
                values[ch]++;
                N++;
            }
        }

        //calculate the sum of each frequency
        for(i=0; i<26; i++){
            ch = values[i];
            sum = sum + (ch * (ch-1));
        }

        //divide by N(N-1)
        total = sum/(N*(N-1));

        return total;
    }

    public void printLetterFreq(LinkedHashMap<Character, Float> map){
        for(char c : map.keySet()){
            System.out.printf("%c: %.3f%%\n", c, 100.0*map.get(c));
        }
    }

    public boolean monogramsMatchCommonEnglish(CipherAnalytics analytics){
        //create a list out of the keys in our character frequency map
        LinkedHashMap<Character, Float> charFreqMap = analytics.getCharacterFreqencies();
        List<Character> frequenciesList = new ArrayList<>(charFreqMap.keySet());

        //create a list for the common characters from one of the maps (lowercase mainly used for testing)
        boolean isLower = frequenciesList.get(0) >= 'a' && frequenciesList.get(0) <= 'z';
        LinkedHashMap<Character, Float> commonCharFreqMap =
                isLower ? ENGLISH_LOWERCASE_LETTER_FREQUENCIES : AnalysisUtils.ENGLISH_LETTER_FREQUENCIES;

        List<Character> commonFrequenciesList = new ArrayList<>(commonCharFreqMap.keySet());

        //check the top x characters to see if they match the top x common characters
        for (int i = 0; i < MONOGRAMS_TO_TEST; i++){
            char common = commonFrequenciesList.get(i);
            char test = frequenciesList.get(i);

            //if i == 0 we want to make sure that the letter is 'e' (e will almost always be the top letter)
            if (i == 0){
                if(common != test) return false;
            } else if (!checkAcceptablePosition(commonFrequenciesList, test, i)){
                return false;
            }
        }

        return true;
    }

    private boolean checkAcceptablePosition(List<Character> commonFrequenciesList, char c, int i){
        //start index won't go lower than 0
        int startIndex = i-1 < 0 ? 0 : i-1;
        //end index wont go out of bounds
        int endIndex = i+1 > commonFrequenciesList.size()-1 ? commonFrequenciesList.size()-1 : i+1;

        //check and see if our letter is withing +-1 of the common letters
        boolean flag = false;
        for (int j = startIndex; j < endIndex; j++){
            if (commonFrequenciesList.get(j).equals(c)) flag = true;
        }
        return flag;
    }

    private boolean inAcceptableNumberRange(float commonFreq, float freq){
        float freqToTest = freq*100;
        return (freqToTest <= (commonFreq+TESTING_DFFERENCE)) && (freqToTest >= (commonFreq-TESTING_DFFERENCE));
    }

    public String getText(String s){
        String s1 = "";

        for (char c : s.toCharArray()){
            if ((c >= 'a' && c<='z') || (c>='A' && c<='Z')){
                s1 += c;
            }
        }
        return s1;
    }

    public static boolean isLowercase(char c){
        return c>='a' && c<='z';
    }
}
