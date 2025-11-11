package MariMain;

import java.io.*;
import java.net.*;
import java.util.*;

public class VotingServer {

    private static final int PORT = 1234;
    private static final Map<String, Integer> hasilVoting = new LinkedHashMap<>();
    private static final Set<String> sudahVote = new HashSet<>(); 
    private static volatile boolean isRunning = true; 

    public static void main(String[] args) {
        System.out.println("=== SERVER VOTING ONLINE ===");
        System.out.println("Menunggu client terhubung...\n");

        
        hasilVoting.put("A", 0);
        hasilVoting.put("B", 0);
        hasilVoting.put("C", 0);
        hasilVoting.put("D", 0);


        
        new Thread(() -> {
            try (Scanner adminInput = new Scanner(System.in)) {
                while (true) {
                    System.out.print("\nKetik 'STOP' untuk menutup voting: ");
                    String cmd = adminInput.nextLine().trim().toUpperCase();
                    if (cmd.equals("STOP")) {
                        System.out.println("\nVoting dihentikan oleh admin!");
                        tampilkanHasilAkhir();
                        isRunning = false;
                        break;
                    }
                }
            }
        }).start();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (isRunning) {
                try {
                    Socket client = serverSocket.accept();
                    System.out.println("Client baru dari " + client.getInetAddress().getHostAddress());
                    new Thread(new ClientHandler(client)).start();
                } catch (SocketException se) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== Server Voting Dimatikan ===");
    }

    
    private static class ClientHandler implements Runnable {
        private final Socket client;

        public ClientHandler(Socket socket) {
            this.client = socket;
        }

        @Override
        public void run() {
            String ipClient = client.getInetAddress().getHostAddress();

            try (
                Scanner input = new Scanner(client.getInputStream());
                PrintWriter output = new PrintWriter(client.getOutputStream(), true)
            ) {
                output.println("Selamat datang di Sistem Voting Online!");
                output.println("Kandidat tersedia: A. Mas Anies");
                output.println("Kandidat tersedia: B. Pak Prabowo");
                output.println("Kandidat tersedia: C. Om Ganjar");
                output.println("Kandidat tersedia: D. Raysen");

                synchronized (sudahVote) {
                    
                    if (sudahVote.contains(ipClient)) {
                        output.println("\n Nggak boleh vote 2 kali.");
                        return;
                    } else {
                        output.println("Ketik Huruf Kandidat:");
                    }
                }

                String pilihan = input.nextLine().trim().toUpperCase();

                synchronized (hasilVoting) {
                    if (hasilVoting.containsKey(pilihan)) {
                        hasilVoting.put(pilihan, hasilVoting.get(pilihan) + 1);
                        synchronized (sudahVote) {
                            sudahVote.add(ipClient); 
                        }
                        output.println("Pilihan " + pilihan + " telah dicatat.");
                    } else {
                        output.println("Pilihan tidak valid.");
                    }

                    
                    output.println("\n=== Hasil Voting Saat Ini ===");
                    for (Map.Entry<String, Integer> entry : hasilVoting.entrySet()) {
                        output.println("Kandidat " + entry.getKey() + " : " + entry.getValue() + " suara");
                    }

                    
                    System.out.println("\nHasil Voting Diperbarui:");
                    for (Map.Entry<String, Integer> entry : hasilVoting.entrySet()) {
                        System.out.println(" - Kandidat " + entry.getKey() + " : " + entry.getValue() + " suara");
                    }
                    System.out.println("--------------------------------------");
                }

            } catch (IOException e) {
                System.out.println("Koneksi client terputus (" + ipClient + ")");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void tampilkanHasilAkhir() {
        System.out.println("\nHASIL AKHIR VOTING:");
        for (Map.Entry<String, Integer> entry : hasilVoting.entrySet()) {
            System.out.println(" - Kandidat " + entry.getKey() + " : " + entry.getValue() + " suara");
        }
        System.out.println("--------------------------------------");
        System.out.println("Jumlah total pemilih unik: " + sudahVote.size());
    }
}
