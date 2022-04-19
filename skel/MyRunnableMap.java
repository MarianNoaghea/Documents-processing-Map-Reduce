import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


class MyRunnableMap implements Runnable {
    Coordinator coordinator;
    private final ExecutorService tpe;
    private final AtomicInteger inQueue;
    int step;

    public MyRunnableMap(Coordinator coordinator, ExecutorService tpe, AtomicInteger inQueue, int step) {
        this.coordinator = coordinator;
        this.tpe = tpe;
        this.inQueue = inQueue;
        this.step = step;

    }

    @Override
    public void run() {
        // un thread pentru fiecare map task
        if (step < coordinator.mapTasks.size()) {

            MapTask currentMapTask = coordinator.mapTasks.get(step);
            InputStream inputFile = null;
            try {
                inputFile = new FileInputStream(currentMapTask.file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                inputFile.skip(currentMapTask.indexStart);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String currentWord = "";
            String maximumWord = "";

            HashMap<Integer, Integer> taskHashMap = new HashMap<>();

            boolean endOfWord;
            char c = 0;
            for (int i = currentMapTask.indexStart; i < currentMapTask.indexStop - 1; i++) {
                try {
                    // parcurg caracter cu caracter
                    c = (char) inputFile.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // cand gasesc un separator inseamna ca am terminat de construit cuvantul
                if (Tema2.isSeparator(c)) {
                    endOfWord = true;

                    // updatez hasmapul
                    if (currentWord.length() != 0) {
                        if (taskHashMap.get(currentWord.length()) == null) {
                            taskHashMap.put(currentWord.length(), 1);
                        } else {
                            taskHashMap.put(currentWord.length(), taskHashMap.get(currentWord.length()) + 1);
                        }
                    }
                    // retin cuvantul maxim
                    if (currentWord.length() > maximumWord.length()) {
                        maximumWord = currentWord;
                    }

//                    if (currentMapTask.file.getName().compareTo("sonnets_all") == 0 && currentWord.length() != 0) {
//                        System.out.println(currentWord);
//                    }

                    currentWord = "";

                } else {
                    endOfWord = false;
                }

                if (!endOfWord) {
                    currentWord += c;
                }

            }

            // tratez si ultimul
            if (currentWord.length() != 0) {
                if (taskHashMap.get(currentWord.length()) == null) {
                    taskHashMap.put(currentWord.length(), 1);
                } else {
                    taskHashMap.put(currentWord.length(), taskHashMap.get(currentWord.length()) + 1);
                }
            }

            if (currentWord.length() > maximumWord.length()) {
                maximumWord = currentWord;
            }

            // adaug rezultatele pentru fiecare task de Map
            currentMapTask.mapTaskResult.maxWord = maximumWord;
            currentMapTask.mapTaskResult.hashMap = taskHashMap;

            inQueue.incrementAndGet();
            tpe.submit(new MyRunnableMap(coordinator, tpe, inQueue, step + 1));
        }

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }
    }
}

