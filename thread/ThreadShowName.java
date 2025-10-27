package thread;

public class ThreadShowName extends Thread {
    public static void main(String[] args) {
        
        ThreadShowName thread1 = new ThreadShowName();
        ThreadShowName thread2 = new ThreadShowName();
        
        thread1.start();  
        thread2.start();  
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {                
                System.out.println(getName() + " sedang dieksekusi");

                int pause = (int) (Math.random() * 3000);
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                System.out.println("Thread diinterupsi: " + e);
            }
        }
    }
}
