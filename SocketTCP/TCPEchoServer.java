package SocketTCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPEchoServer {
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;

    public static void main(String[] args) {
        System.out.println("Membuka port...\n");

        try {
            serverSocket = new ServerSocket(PORT); 
            System.out.println("Server berjalan di port " + PORT);
        } catch (IOException ioEx) {
            System.out.println("Tidak dapat mengakses port!");
            System.exit(1);
        }

        do {
            handleClient();
        } while (true);
    }

    private static void handleClient() {
        Socket link = null; 

        try {
            link = serverSocket.accept(); 
            System.out.println("Client terhubung: " + link.getInetAddress().getHostAddress());

            try (Scanner input = new Scanner(link.getInputStream())) {
                PrintWriter output = new PrintWriter(link.getOutputStream(), true); 

                int jmlPesan = 0;
                String pesan = input.nextLine(); 

                while (!pesan.equals("**CLOSE**")) {
                    System.out.println("Pesan diterima: " + pesan);
                    jmlPesan++;
                    output.println("Pesan " + jmlPesan + ": " + pesan); 
                    pesan = input.nextLine();
                }

                output.println(jmlPesan + " pesan diterima.");
            } 
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                System.out.println("\n* Menutup koneksi... *");
                if (link != null) link.close(); 
            } catch (IOException ioEx) {
                System.out.println("Tidak bisa menutup koneksi!");
                System.exit(1);
            }
        }
    }
}

