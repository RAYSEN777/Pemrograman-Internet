
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;

class SharedData {
    private int kuotaMaksimal;
    private int jumlahHadir;

    public SharedData(int kuota) {
        this.kuotaMaksimal = kuota;
        this.jumlahHadir = 0;
    }

    public synchronized boolean tambahKehadiran(String nama) {
        if (jumlahHadir < kuotaMaksimal) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jumlahHadir++;
            return true;
        } else {
            return false;
        }
    }

    public synchronized int getSisaKuota() {
        return kuotaMaksimal - jumlahHadir;
    }

    public synchronized int getJumlahHadir() {
        return jumlahHadir;
    }

    public int getKuotaMaksimal() {
        return kuotaMaksimal;
    }
}

class ClientHandler extends Thread {
    private Socket client;
    private SharedData sharedData;
    private AbsensiServerGUI serverGUI;
    private Scanner input;
    private PrintWriter output;

    public ClientHandler(Socket socket, SharedData sharedData, AbsensiServerGUI gui) {
        this.client = socket;
        this.sharedData = sharedData;
        this.serverGUI = gui;
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    @Override
    public void run() {
        String nama = "";
        String nim = "";
        String status = "";

        try {
            output.println("=== SISTEM ABSENSI (Sisa Kuota: " + sharedData.getSisaKuota() + ") ===");
            output.println("Masukkan nama: ");
            nama = input.nextLine();

            output.println("Masukkan NIM: ");
            nim = input.nextLine();

            boolean valid = false;
            boolean isSuccess = false;

            while (!valid) {
                output.println("Pilih (1 = Hadir, 2 = Sakit, 3 = Izin): ");
                String pilihan = input.nextLine();

                switch (pilihan) {
                    case "1":
                    case "2":
                    case "3":
                        String tempStatus = "";
                        if (pilihan.equals("1"))
                            tempStatus = "Hadir";
                        else if (pilihan.equals("2"))
                            tempStatus = "Sakit";
                        else
                            tempStatus = "Izin";

                        if (sharedData.tambahKehadiran(nama)) {
                            status = tempStatus;
                            isSuccess = true;
                            valid = true;
                        } else {
                            status = "Gagal (Kuota Penuh)";
                            output.println("MAAF! Kuota absensi hari ini sudah penuh.");
                            valid = true;
                        }
                        break;

                    default:
                        output.println("Input tidak valid! Harap masukkan 1, 2, atau 3.");
                        break;
                }
            }

            output.println("\n--- BUKTI ABSENSI ---");
            output.println("Nama   : " + nama);
            output.println("NIM    : " + nim);
            output.println("Status : " + status);
            if (isSuccess) {
                output.println("Pesan  : Data tersimpan.");
            } else {
                output.println("Pesan  : Mohon hubungi dosen.");
            }

            if (isSuccess) {
                serverGUI.addLog("✓ DITERIMA - " + nama + " (" + nim + ") - " + status);
            } else {
                serverGUI.addLog("✗ DITOLAK - " + nama + " (" + nim + ") - Kuota Penuh");
            }
            
            serverGUI.updateKuota();

        } catch (Exception e) {
            serverGUI.addLog("✗ Koneksi client terputus: " + e.getMessage());
        } finally {
            try {
                if (client != null)
                    client.close();
            } catch (IOException ex) {
            }
        }
    }
}

public class AbsensiServerGUI extends JFrame {
    private static ServerSocket serverSocket;
    private static final int PORT = 1222;
    private static SharedData dataAbsensi = new SharedData(10);
    
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel kuotaLabel;
    private JLabel hadirLabel;
    private JProgressBar kuotaBar;
    private JButton startButton;
    private JButton stopButton;
    private boolean serverRunning = false;
    private Thread serverThread;

    public AbsensiServerGUI() {
        setTitle("Server Absensi - Monitoring");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
    }

    private void initComponents() {
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 245));
        
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("SERVER ABSENSI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Server Mati");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(255, 100, 100));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        kuotaLabel = new JLabel("Kuota Tersedia: " + dataAbsensi.getSisaKuota() + "/" + dataAbsensi.getKuotaMaksimal());
        kuotaLabel.setFont(new Font("Arial", Font.BOLD, 16));

        hadirLabel = new JLabel("Total Hadir: 0");
        hadirLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        kuotaBar = new JProgressBar(0, dataAbsensi.getKuotaMaksimal());
        kuotaBar.setValue(0);
        kuotaBar.setStringPainted(true);
        kuotaBar.setForeground(new Color(76, 175, 80));
        kuotaBar.setPreferredSize(new Dimension(0, 30));

        infoPanel.add(kuotaLabel);
        infoPanel.add(hadirLabel);
        infoPanel.add(kuotaBar);
        
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Log Aktivitas"),
            new EmptyBorder(5, 5, 5, 5)
        ));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(new Color(0, 255, 0));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.setBackground(new Color(240, 240, 245));

        startButton = new JButton("Start Server");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.BLACK);
        startButton.setPreferredSize(new Dimension(150, 40));
        startButton.addActionListener(e -> startServer());

        stopButton = new JButton("Stop Server");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.setBackground(new Color(244, 67, 54));
        stopButton.setForeground(Color.BLACK);
        stopButton.setPreferredSize(new Dimension(150, 40));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopServer());

        JButton clearButton = new JButton("Clear Log");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(100, 181, 246));
        clearButton.setForeground(Color.BLACK);
        clearButton.setPreferredSize(new Dimension(120, 40));
        clearButton.addActionListener(e -> logArea.setText(""));

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 240, 245));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(logPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        addLog("Server siap dijalankan. Port: " + PORT);
    }

    private void startServer() {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                serverRunning = true;
                
                SwingUtilities.invokeLater(() -> {
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    statusLabel.setText("Server Berjalan - Port: " + PORT);
                    statusLabel.setForeground(new Color(100, 255, 100));
                    addLog("Server berhasil dijalankan pada port " + PORT);
                });

                while (serverRunning) {
                    try {
                        Socket client = serverSocket.accept();
                        String clientIP = client.getInetAddress().getHostAddress();
                        addLog("Client terhubung dari: " + clientIP);
                        new ClientHandler(client, dataAbsensi, this).start();
                    } catch (IOException e) {
                        if (serverRunning) {
                            addLog("Error menerima koneksi: " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                addLog("Gagal memulai server: " + e.getMessage());
            }
        });
        serverThread.start();
    }

    private void stopServer() {
        serverRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            statusLabel.setText("Server Mati");
            statusLabel.setForeground(new Color(255, 100, 100));
            addLog("Server dihentikan");
        } catch (IOException e) {
            addLog("Error menghentikan server: " + e.getMessage());
        }
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + time + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void updateKuota() {
        SwingUtilities.invokeLater(() -> {
            int hadir = dataAbsensi.getJumlahHadir();
            int sisa = dataAbsensi.getSisaKuota();
            int max = dataAbsensi.getKuotaMaksimal();
            
            kuotaLabel.setText("Kuota Tersedia: " + sisa + "/" + max);
            hadirLabel.setText("Total Hadir: " + hadir);
            kuotaBar.setValue(hadir);
            
            if (sisa == 0) {
                kuotaBar.setForeground(new Color(244, 67, 54));
            } else if (sisa <= 3) {
                kuotaBar.setForeground(new Color(255, 152, 0));
            } else {
                kuotaBar.setForeground(new Color(76, 175, 80));
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            AbsensiServerGUI server = new AbsensiServerGUI();
            server.setVisible(true);
        });
    }
}