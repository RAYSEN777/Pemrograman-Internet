package thread;

public class RunnableDemo implements Runnable {
    private Thread t;
    private String threadName;
    
    public RunnableDemo(String name) {
        threadName = name;
        System.out.println("Membuat " + threadName);
    }
    
    @Override
    public void run() {
        System.out.println("Menjalankan " + threadName);
        try {
            for (int i = 4; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);
                
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + threadName + " diinterupsi.");
        }
        System.out.println("Thread " + threadName + " keluar.");
    }

    public void start() {
        System.out.println("Memulai " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
    
    public static void main(String[] args) {
        RunnableDemo R1 = new RunnableDemo("Thread-1");
        R1.start();

        RunnableDemo R2 = new RunnableDemo("Thread-2");
        R2.start();
    }
}

