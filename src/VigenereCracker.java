import java.util.ArrayList;

/**
 * Created by Seth on 2/10/2017.
 */
public class VigenereCracker extends AbstractCracker{
    private ArrayList<Integer> potentialPeriods;

    public VigenereCracker(CipherAnalytics analytics){
        super(analytics);
    }

    @Override
    public boolean testCipher(){

        //todo: see if kasiski test agrees with these values
        potentialPeriods = getPotentialPeriods();
        String msg = potentialPeriods.size() > 0 ?
                "Possible key lengths for the Vigenere cipher were found.":
                "No likely keys < 15 characters in length";
        CipherCracker.printStatusMessage(msg, null);
        return potentialPeriods.size() > 0;
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

        //add the first index of coincidence to the list (ic for key length = 1)
        averageICs.add(analytics.getIndexOfCoincidence());

        //get ic for each key length (up to 15)
        for (int keyLength = 2; keyLength<=15; keyLength++){
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
}
