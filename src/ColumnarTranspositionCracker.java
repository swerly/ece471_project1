/**
 * Created by Seth on 2/11/2017.
 */
public class ColumnarTranspositionCracker extends AbstractCracker {
    public ColumnarTranspositionCracker(CipherAnalytics analytics) {
        super(analytics);
    }

    @Override
    public boolean testCipher() {
        AnalysisUtils utils = new AnalysisUtils();
        boolean monogramsMatch = utils.monogramsMatchCommonEnglish(analytics);
        String msg;
        if (monogramsMatch){
            msg = "Top monograms in this ciphertext match top monograms in the English language.\n"+
                    "This was most likely encrypted with Columnar Transposition, but we will confirm later.";
        } else {
            msg = "Top monograms in this ciphertext don't match top monograms in the English language.\n"+
                    "This was most likely not encrypted using Columnar Transposition, but may be bruteforced later.";
        }
        CipherCracker.printStatusMessage(msg, null);
        return monogramsMatch;
    }
}
