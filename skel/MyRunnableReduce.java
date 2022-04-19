import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

class MyRunnableReduce implements Runnable {
    ArrayList<MapTask> mapTasks;
    ArrayList<File> files;
    ArrayList<Double> fibonacci;
    Set<FinalResult> finalResults;
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;

    int step;

    public MyRunnableReduce(ArrayList<MapTask> mapTasks, ArrayList<File> files, ArrayList<Double> fibonacci, Set<FinalResult> finalResults, ExecutorService tpe, AtomicInteger inQueue, int step) {
        this.mapTasks = mapTasks;
        this.files = files;
        this.fibonacci = fibonacci;
        this.finalResults = finalResults;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.step = step;

    }

    @Override
    public void run() {

        if (step < files.size()) {

            // combine
            CombinedTask combinedTask = new CombinedTask((files.get(step)));
            for (int i = 0; i < mapTasks.size(); i++) {
                if (files.get(step).equals(mapTasks.get(i).file)) {
                    combinedTask.hashMaps.add(mapTasks.get(i).mapTaskResult.hashMap);
                    combinedTask.maximumWords.add(mapTasks.get(i).mapTaskResult.maxWord);
                }
            }

            // process
            Double rang = 0.0;
            Double nrCuvTotal = 0.0;
            int crtFreq = 0;
            int crtMaxLen = -1;
            // calculez rangul parcurgand arrayul de hasmapuri
            for (int i = 0; i < combinedTask.hashMaps.size(); i++) {
                for (Map.Entry<Integer, Integer> set :
                        combinedTask.hashMaps.get(i).entrySet()) {
                    rang += fibonacci.get(set.getKey() + 1) * Double.valueOf(set.getValue());
                    nrCuvTotal += Double.valueOf(set.getValue());

                    if (crtMaxLen == -1) {
                        crtMaxLen = set.getKey();
                        crtFreq += set.getValue();
                    } else {
                        if (crtMaxLen == set.getKey()) {
                            crtFreq += set.getValue();
                        } else {
                            if (crtMaxLen < set.getKey()) {
                                crtMaxLen = set.getKey();
                                crtFreq = set.getValue();
                            }
                        }
                    }

                }
            }

            rang /= nrCuvTotal;

            FinalResult finalResult = new FinalResult(files.get(step).getName(), rang, crtMaxLen, crtFreq);
            finalResults.add(finalResult);
            inQueue.incrementAndGet();
            tpe.submit(new MyRunnableReduce(mapTasks, files, fibonacci, finalResults, tpe, inQueue, step + 1));


        }

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }
    }
}

