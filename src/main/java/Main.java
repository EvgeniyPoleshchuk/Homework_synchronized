import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    private static final String LETTER = "RLRFR";
    private static final int LENGTH = 100;
    private static final int THREAD_ITERATION = 1000;

    public static void main(String[] args) throws InterruptedException {
        Thread thread2 = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    synchronized (sizeToFreq) {
                        sizeToFreq.wait();
                        Map.Entry<Integer, Integer> maxEntry = sizeToFreq.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .orElse(null);
                        assert maxEntry != null;
                        System.out.printf("Самое частое количество повторений %s (встретилось %s раз)\n",
                                maxEntry.getKey(), maxEntry.getValue());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread2.start();
        Thread thread = null;
        for (int i = 0; i < THREAD_ITERATION; i++) {
            thread = new Thread(() -> {
                int count = 0;
                String text = generateRoute(LETTER, LENGTH);
                for (int i1 = 0; i1 < text.length(); i1++) {
                    if (text.charAt(i1) == 'R') {
                        count++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(count)) {
                        sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                    } else {
                        sizeToFreq.put(count, 1);
                    }
                    sizeToFreq.notify();
                }
            });
            thread.start();
            thread.join();
        }
        thread.interrupt();


    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

}
