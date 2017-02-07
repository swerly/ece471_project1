/**
 * Created by Seth on 2/7/2017.
 */
public class Main {
    public static void main(String[] args){
        if (args.length <= 0){
            System.out.println("No filename entered, exiting...");
        }
        CipherCracker cipherCracker = new CipherCracker(args[0]);
        cipherCracker.run();
    }
}