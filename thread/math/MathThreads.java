package thread.math;

public class MathThreads {
    public static void main(String args[]) {
        MathSin st = new MathSin(45);
        MathCos ct = new MathCos(60);
        MathTan tt = new MathTan(30);
        st.start();
        ct.start();
        tt.start();

        try {
            st.join();
            ct.join();
            tt.join();
            double z = st.res + ct.res + tt.res;
            System.out.println("Jumlah dari sin, cos, tan = " +z);
        } catch(InterruptedException IntExp) {}
    }
}
