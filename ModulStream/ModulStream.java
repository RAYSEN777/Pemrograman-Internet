import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModulStream {
    class InputOutputStream {
        FileInputStream file = null;

        public void printInput() {
            try {
                file = new FileInputStream("abc.txt");
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
                file = new FileOutputStream("abcOut.txt");
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
                file = new FileInputStream("abc.txt");
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
                        buff.close();
                    } catch (Exception el) {
                        el.printStackTrace();
                    }
                }
            }
        }

        public void bufferOut(){
            FileOutputStream file = null;
            BufferedOutputStream buff = null;

            String text = "qwerty";

            try{
                file = new FileOutputStream("abc.txt");
                buff = new BufferedOutputStream(file);

                buff.write(text.getBytes());
                buff.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                        buff.close();
                    } catch (Exception el) {
                        el.printStackTrace();
                    }
                }
            }
        }

        public void fileCopyNoBuffer() {
            String inFile = "image.png";
            String outFile = "imageCopy.png";
            FileInputStream fileIn = null;
            FileOutputStream fileOut = null;
            long startTime, endTime;

            try {
                fileIn = new FileInputStream(inFile);
                fileOut = new FileOutputStream(outFile);

                startTime = System.nanoTime();

                int byteV;
                while ((byteV = fileIn.read()) != -1) {
                    fileOut.write(byteV);
                }

                endTime = System.nanoTime();
                System.out.println("Waktu No Buff: " + (endTime - startTime) + " ns");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                try {
                    if (fileIn != null) fileIn.close();
                    if (fileOut != null) fileOut.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void fileCopyWithBuffer() {
            String inFile = "image.png";
            String outFile = "imageCopyBuffered.png";
            FileInputStream fileIn = null;
            FileOutputStream fileOut = null;
            BufferedInputStream buffIn = null;
            BufferedOutputStream buffOut = null;
            long startTime, endTime;

            try {
                fileIn = new FileInputStream(inFile);
                buffIn = new BufferedInputStream(fileIn);
                fileOut = new FileOutputStream(outFile);
                buffOut = new BufferedOutputStream(fileOut);

                startTime = System.nanoTime();

                int byteV;
                while ((byteV = buffIn.read()) != -1) {
                    buffOut.write(byteV);
                }

                endTime = System.nanoTime();
                System.out.println("Waktu Buff: " + (endTime - startTime) + " ns");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } finally {
                try {
                    if (buffIn != null) buffIn.close();
                    if (buffOut != null) buffOut.close();
                    if (fileIn != null) fileIn.close();
                    if (fileOut != null) fileOut.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void BufferReaderWriter(){
            String file = "abc.txt";
            String msg = "Awas Ada Raysen";

            System.out.println(java.nio.charset.Charset.defaultCharset());

            try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                out.write(msg);
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try(BufferedReader in = new BufferedReader(new FileReader(file))){
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    System.out.println(inLine);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        ModulStream ms = new ModulStream(); 
        InputOutputStream io = ms.new InputOutputStream(); 

        io.printInput();   
        // io.writeOutput();  
        // io.bufferIn();
        // io.bufferOut();
        // io.fileCopyNoBuffer();
        // io.fileCopyWithBuffer();
        // io.BufferReaderWriter();
    }
}
