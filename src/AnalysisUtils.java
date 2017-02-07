import java.util.*;

/**
 * Created by Seth on 2/7/2017.
 */
public class AnalysisUtils {
    private static int DIGRAMS_TO_RETURN = 15;
    private static int LETTER_FREQUENCIES_TO_RETURN = 15;
    private String cipherText;

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

        //calculate frequency of each letter in s
        int ch;
        for(i=0; i<s.length(); i++){
            ch = s.charAt(i)-65;
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

        //return the result
        return total;
    }
}
