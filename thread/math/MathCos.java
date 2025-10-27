package thread.math;

import java.lang.Math;

class MathCos extends Thread {
    public double deg;
    public double res;

    public MathCos(int degree) {
        deg = degree;
    }

    public void run() {
        System.out.println("Mengeksekusi cos dari " +deg);
        double Deg2Rad = Math.toRadians(deg);
        res = Math.cos(Deg2Rad);
        System.out.println("Keluar dari MathCos. Hasil = "+res);
    }
}

