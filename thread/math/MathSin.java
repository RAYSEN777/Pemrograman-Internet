package thread.math;

import java.lang.Math;

class MathSin extends Thread {
    public double deg;
    public double res;

    public MathSin(int degree) {
        deg = degree;
    }

    public void run() {
        System.out.println("Mengeksekusi sin dari "+deg);
        double Deg2Rad = Math.toRadians(deg);
        res = Math.sin(Deg2Rad);
        System.out.println("Keluar dari MathSin. Hasil = "+res);
    }
}
