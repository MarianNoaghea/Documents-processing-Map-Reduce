import java.io.File;
import java.util.HashMap;

public class MapTaskResult {
    File file;
    HashMap<Integer, Integer> hashMap;
    String maxWord;

    public MapTaskResult(File file) {
        this.file = file;
        this.hashMap = new HashMap<>();
        this.maxWord = "";
    }
}
