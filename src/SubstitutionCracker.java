/**
 * Created by Seth on 2/11/2017.
 */
public class SubstitutionCracker extends AbstractCracker {
    public SubstitutionCracker(CipherAnalytics analytics) {
        super(analytics);
    }

    @Override
    public boolean testCipher() {
        return false;
    }

    @Override
    public void runCracker() {

    }
}
