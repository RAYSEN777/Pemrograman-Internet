package thread.math;

import java.lang.Math;

class MathTan extends Thread {
    public double deg;
    public double res;

    public MathTan(int degree) {
      deg = degree;
    }

    public void run() {
        System.out.println("Mengeksekusi tan dari " +deg);
        double Deg2Rad = Math.toRadians(deg);
        res = Math.tan(Deg2Rad);
        System.out.println("Keluar dari MathTan. Hasil = " +res);
    }
}
