package thread;

class ThreadDemo extends Thread {
    private Thread t;
    private String threadName;
    
    ThreadDemo(String name) {
        threadName = name;
        System.out.println("Membuat " + threadName);
    }
    
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
        ThreadDemo T1 = new ThreadDemo("Thread-1");
        T1.start();

        ThreadDemo T2 = new ThreadDemo("Thread-2");
        T2.start();
    }
}
