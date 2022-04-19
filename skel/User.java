import java.io.IOException;

public class User {
    int numberOfWorkers;
    String inFileName;
    String outFileName;
    Coordinator coordinator;


    public User(int numberOfWorkers, String inFileName, String outFileName) throws IOException {
        this.numberOfWorkers = numberOfWorkers;
        this.inFileName = inFileName;
        this.outFileName = outFileName;
        this.coordinator = new Coordinator(inFileName);
    }

}
