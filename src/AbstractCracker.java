/**
 * Created by Seth on 2/10/2017.
 */
public abstract class AbstractCracker {
    protected CipherAnalytics analytics;

    public AbstractCracker(CipherAnalytics analytics){
        this.analytics = analytics;
    }

    public abstract boolean testCipher();
    public abstract void runCracker();
}
