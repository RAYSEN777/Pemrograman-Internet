package thread;

public class RunnableHelloCount implements Runnable {
    private Thread thread1, thread2;
    public static void main(String[] args) {
        new RunnableHelloCount();
    }
    
    public RunnableHelloCount() {   
        thread1 = new Thread(this, "Thread-1");
        thread2 = new Thread(this, "Thread-2");

        thread1.start();
        thread2.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {                
                System.out.println(Thread.currentThread().getName() + " sedang dieksekusi.");
                
                int pause = (int) (Math.random() * 3000);
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " diinterupsi: " + e);
            }
        }
    }
}

