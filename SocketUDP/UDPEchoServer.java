package SocketUDP;

import java.net.*;
import java.io.*;

public class UDPEchoServer {
    private static final int PORT = 1234;
    private static DatagramSocket dtgramSocket;
    private static DatagramPacket inPacket, outPacket;
    private static byte[] buffer;

    public static void main(String[] args) {
        System.out.println("Membuka port...\n");

        try {
            dtgramSocket = new DatagramSocket(PORT); 
            System.out.println("Server UDP berjalan di port " + PORT);
        } catch (SocketException sockEx) {
            System.out.println("Tidak bisa terhubung ke port!");
            System.exit(1);
        }

        handleClient();
    }

    private static void handleClient() {
        try {
            String messageIn, messageOut;
            int numMessages = 0;
            InetAddress clientAddress;
            int clientPort;

            do {
                buffer = new byte[256]; 
                inPacket = new DatagramPacket(buffer, buffer.length); 

                dtgramSocket.receive(inPacket); 

                clientAddress = inPacket.getAddress(); 
                clientPort = inPacket.getPort();       

                messageIn = new String(inPacket.getData(), 0, inPacket.getLength()); 
                System.out.println("Pesan diterima dari " + clientAddress + ": " + messageIn);

                numMessages++;
                messageOut = "Pesan " + numMessages + ": " + messageIn;

                outPacket = new DatagramPacket(
                        messageOut.getBytes(),
                        messageOut.length(),
                        clientAddress,
                        clientPort
                ); 

                dtgramSocket.send(outPacket); 

            } while (true);

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            System.out.println("\n* Menutup koneksi... *");
            dtgramSocket.close(); 
        }
    }
}

