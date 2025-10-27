package thread;

public class RunnableShowName implements Runnable {
    public static void main(String[] args) {
        RunnableShowName runnable1 = new RunnableShowName();
        RunnableShowName runnable2 = new RunnableShowName();

        Thread thread1 = new Thread(runnable1, "Thread-1");
        Thread thread2 = new Thread(runnable2, "Thread-2");
        
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

