import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CombinedTask {
    File file;
    ArrayList<HashMap<Integer, Integer>> hashMaps;
    ArrayList<String> maximumWords;

    public CombinedTask(File file) {
        this.file = file;
        this.hashMaps = new ArrayList<>();
        this.maximumWords = new ArrayList<>();
    }

    public void setFile(File file) {
        this.file = file;
    }

}

