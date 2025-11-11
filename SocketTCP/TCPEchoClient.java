package SocketTCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPEchoClient {
    private static InetAddress host;
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("ID host tidak ditemukan!");
            System.exit(1);
        }
        accessServer();
    }

    private static void accessServer() {
        Socket link = null; 

        try {
            link = new Socket(host, PORT); 
            System.out.println("Terhubung ke server pada port " + PORT);

            try (Scanner input = new Scanner(link.getInputStream())) {
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);
   
                try (Scanner masukanUser = new Scanner(System.in)) {
                    String pesan, response;

                    do {
                        System.out.print("Masukkan pesan (atau ketik **CLOSE** untuk keluar): ");
                        pesan = masukanUser.nextLine();

                        output.println(pesan); 
                        response = input.nextLine(); 

                        System.out.println("SERVER> " + response);
                    } while (!pesan.equals("**CLOSE**"));
                }
            }

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                System.out.println("\n* Menutup koneksi... *");
                if (link != null) link.close(); 
            } catch (IOException ioEx) {
                System.out.println("Tidak dapat menutup koneksi!");
                System.exit(1);
            }
        }
    }
}

