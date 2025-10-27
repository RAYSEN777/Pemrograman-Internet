package thread;

public class ThreadHelloCount {
    public static void main(String[] args) {
        
        HelloThread hello = new HelloThread();
        CountThread count = new CountThread();
        
        hello.start();
        count.start();
    }
}

class HelloThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                System.out.println("Hello!");
                
                int pause = (int) (Math.random() * 3000);
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                System.out.println("HelloThread diinterupsi: " + e);
            }
        }
    }
}

class CountThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println(i);
                
                int pause = (int) (Math.random() * 3000);
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                System.out.println("CountThread diinterupsi: " + e);
            }
        }
    }
}
