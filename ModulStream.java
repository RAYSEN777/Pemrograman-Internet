import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ModulStream {
    class InputOutputStream {
        FileInputStream file = null;

        public void printInput() {
            try {
                file = new FileInputStream("C:\\Users\\ASUS\\Documents\\Pemrograman-Internet\\abc.txt");
                int byteV;
                while ((byteV = file.read()) != -1) {
                    System.out.print((char) byteV);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception el) {
                        el.printStackTrace();
                    }
                }
            }
        }

        public void writeOutput() {
            String text = "qwerty";
            FileOutputStream file = null;

            try {
                file = new FileOutputStream("C:\\Users\\ASUS\\Documents\\Pemrograman-Internet\\abcOut.txt");
                file.write(text.getBytes());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception el) {
                        el.printStackTrace();
                    }
                }
            }
        }

        public void bufferIn(){
            FileInputStream file = null;
            BufferedInputStream buff = null;

            try{
                file = new FileInputStream("C:\\Users\\ASUS\\Documents\\Pemrograman-Internet\\abc.txt");
                buff = new BufferedInputStream(file);

                int byteV;

                while ((byteV = buff.read()) != -1){
                    System.out.print((char)byteV);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception el) {
                        el.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ModulStream ms = new ModulStream(); 
        InputOutputStream io = ms.new InputOutputStream(); 

        io.printInput();   
        // io.writeOutput();  
        System.out.println("\n=================");
        io.bufferIn();
    }
}
