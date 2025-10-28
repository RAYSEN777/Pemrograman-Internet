package thread.sync;

public class SyncronizationDemo1 {
    public static void main(String[] args) {
        FinTrans1 ft = new FinTrans1();
        TransThread1 tt1 = new TransThread1(ft, "Deposit Thread");
        TransThread1 tt2 = new TransThread1(ft, "Withdrawal Thread");
        tt1.start();
        tt2.start();
    }
}

class FinTrans1 {
    public static String transName;
    public static double amount;
}

class TransThread1 extends Thread {
    private FinTrans1 ft;

    TransThread1(FinTrans1 ft, String name) {
        super(name);
        this.ft = ft;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            if (getName().equals("Deposit Thread")) {
                synchronized (ft) {
                    ft.transName = "Deposit";
                    try {
                        Thread.sleep((int) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                    }
                    ft.amount = 2000.0;
                    System.out.println(ft.transName + " " + ft.amount);
                }
            } else {
                synchronized (ft) {
                    ft.transName = "Withdrawal";
                    try {
                        Thread.sleep((int) (Math.random() * 1000));
                    } catch (InterruptedException e) {
                    }
                    ft.amount = 250.0;
                    System.out.println(ft.transName + " " + ft.amount);
                }
            }
        }
    }
}

