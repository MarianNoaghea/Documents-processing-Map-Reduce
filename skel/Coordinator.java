import java.io.*;
import java.util.*;

public class Coordinator {
    int fragmentSize;
    int numberOfFiles;
    ArrayList<File> files;
    ArrayList<MapTask> mapTasks;
    Set<FinalResult> finalResults;

    public Coordinator(String fileName) throws FileNotFoundException {
        File file = new File((fileName));
        Scanner scanner = new Scanner(file);

        this.files = new ArrayList<>();
        this.fragmentSize = Integer.parseInt(scanner.nextLine());
        this.numberOfFiles = Integer.parseInt(scanner.nextLine());
        this.mapTasks = new ArrayList<>();
        this.finalResults = new TreeSet<>(new Comparator<>() {
            @Override
            public int compare(FinalResult o1, FinalResult o2) {
                return o2.rang.compareTo(o1.rang);
            }
        });

        getFiles(scanner);
    }

    public void getFiles(Scanner scanner) {
        int i = 0;
        while (i < numberOfFiles) {
            String line = scanner.nextLine();
            this.files.add(new File((line)));
            i++;
        }

    }

    public void coordinate() throws IOException {
        for (File file : files) {
            int i = 0;
            while(i < file.length()) {
                // creez un mapTask pentru fiecare fragment
                MapTask newMapTask;
                if (i + fragmentSize < file.length()) {
                    newMapTask = new MapTask(file, i, i + fragmentSize, new MapTaskResult(file));
                } else {
                    newMapTask = new MapTask(file, i, (int) file.length(), new MapTaskResult(file));
                }

                mapTasks.add(newMapTask);

                i += fragmentSize;
            }
        }

        // recalibrez indecsii de start si de stop pentru fragmentele
        // care contin portiuni din cuvinte
        for (MapTask t : mapTasks) {
            // daca nu ma aflu la inceputul fisierului
            if (t.indexStart != 0) {
                // verific daca in stanga indexului se afla o litera sau un separator
                InputStream inputFile = new FileInputStream(t.file);
                inputFile.skip(t.indexStart - 1);

                if (!isSeparator((char)inputFile.read())) {
                    int c = 0;
                    while (!isSeparator((char)c) && c != -1) {
                        c = inputFile.read();
                        // avansez cu indexul pana trec de cuvant
                        t.indexStart++;
                    }
                }
            }

            InputStream inputFile = new FileInputStream(t.file);
            inputFile.skip(t.indexStop - 1);
            if (!isSeparator((char)inputFile.read())) {
                int c = 0;
                while(!isSeparator((char)c) && c != -1) {
                    c = inputFile.read();
                    t.indexStop++;
                }
            }
        }
    }

    boolean isSeparator(char c) {
        String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
        for (int i = 0; i < separators.length(); i++) {
            if (separators.charAt(i) == c) {
                return true;
            }
        }

        return false;
    }
}
