import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Tema2 {
    static ArrayList<Double> fibonacci = new ArrayList();


    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            return;
        }

        // creez sirul lui fibonacci
        fibonacci.add(0.0);
        fibonacci.add(1.0);
        for (int i = 2; i < 20; i++) {
            fibonacci.add(fibonacci.get(i - 1) + fibonacci.get(i - 2));
        }


        int numberOfWorkers = Integer.parseInt(args[0]);
        String inFileName = args[1];
        String outFileName = args[2];

        // creez un user
        User user = new User(numberOfWorkers, inFileName, outFileName);

        // coordonez impartirea paralela
        user.coordinator.coordinate();

        // Map
        doMapParallel(user.coordinator, numberOfWorkers);

        // Reduce
        doReduceParallel(user.coordinator.mapTasks, user.coordinator.files,user.coordinator.finalResults, numberOfWorkers);

        FileWriter fileWriter = new FileWriter(outFileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(FinalResult f : user.coordinator.finalResults) {
            printWriter.println(f.fileName + "," + String.format("%.02f", f.rang) + "," + f.maxLen + "," + f.maxLenFreq);
        }

        printWriter.close();

    }
    static void doMapParallel(Coordinator coordinator, int nrOfWorkers) {

        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(nrOfWorkers);

        inQueue.incrementAndGet();
        tpe.submit(new MyRunnableMap(coordinator, tpe, inQueue, 0));

        try {
            tpe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void doReduceParallel(ArrayList<MapTask> mapTasks, ArrayList<File> files,Set<FinalResult> finalResults, int nrOfWorkers) {

        AtomicInteger inQueue = new AtomicInteger(0);
        ExecutorService tpe = Executors.newFixedThreadPool(nrOfWorkers);

        inQueue.incrementAndGet();
        tpe.submit(new MyRunnableReduce(mapTasks, files, fibonacci,  finalResults, tpe, inQueue, 0));

        try {
            tpe.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static boolean isSeparator(char c) {
        String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
        for (int i = 0; i < separators.length(); i++) {
            if (separators.charAt(i) == c) {
                return true;
            }
        }

        return false;

    }
}
