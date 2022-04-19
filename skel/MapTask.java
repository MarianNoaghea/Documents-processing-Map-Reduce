import java.io.File;

public class MapTask {
    File file;
    int indexStart;
    int indexStop;
    MapTaskResult mapTaskResult;

    public MapTask(File file, int indexStart, int indexStop, MapTaskResult mapTaskResult) {
        this.file = file;
        this.indexStart = indexStart;
        this.indexStop = indexStop;
        this.mapTaskResult = mapTaskResult;
    }

}
