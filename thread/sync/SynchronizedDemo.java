package thread.sync;

class SharedResource {
    public synchronized void printMessage(String threadName) {
        System.out.println(threadName + " akses");
        
        for (int i = 1; i <= 5; i++) {
            System.out.println(threadName + " proses " + i);
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " selesai");
    }
}

class T extends Thread {
    private SharedResource resource;
    private String name;

    public T(SharedResource resource, String name) {
        this.resource = resource;
        this.name = name;
    }

    @Override
    public void run() {
        resource.printMessage(name);
    }
}

public class SynchronizedDemo {
    public static void main(String[] args) {
        SharedResource shared = new SharedResource();
        
        T t1 = new T(shared, "Thread-1");
        T t2 = new T(shared, "Thread-2");
        
        t1.start();
        t2.start();
    }
}

