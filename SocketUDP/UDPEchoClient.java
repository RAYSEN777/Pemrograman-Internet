package SocketUDP;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDPEchoClient {
    private static InetAddress host;
    private static final int PORT = 1234;
    private static DatagramSocket dtgramSocket;
    private static DatagramPacket inPacket, outPacket;
    private static byte[] buffer;

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
        try {   
            dtgramSocket = new DatagramSocket();
            System.out.println("Terhubung ke server UDP pada port " + PORT);

            try (Scanner inputUser = new Scanner(System.in)) {
                String pesan = "", respons = "";

                do {
                    System.out.print("Masukkan pesan: ");
                    pesan = inputUser.nextLine();

                    if (!pesan.equals("**CLOSE**")) {                    
                        outPacket = new DatagramPacket(
                                pesan.getBytes(),
                                pesan.length(),
                                host,
                                PORT
                        );

                        dtgramSocket.send(outPacket);
                        
                        buffer = new byte[256];
                        inPacket = new DatagramPacket(buffer, buffer.length);
                        
                        dtgramSocket.receive(inPacket);
                        
                        respons = new String(inPacket.getData(), 0, inPacket.getLength());
                        System.out.println("Server> " + respons);
                    }

                } while (!pesan.equals("**CLOSE**"));
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            System.out.println("\n* Menutup koneksi... *");
            if (dtgramSocket != null && !dtgramSocket.isClosed()) {
                dtgramSocket.close(); 
            }
        }
    }
}

