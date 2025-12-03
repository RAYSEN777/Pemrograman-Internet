
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;


public class AbsensiClientGUI extends JFrame {
    private static final String HOST = "road-adapter.gl.at.ply.gg"; 
    private static final int PORT = 44388;

    private JTextField namaField;
    private JTextField nimField;
    private JRadioButton hadirRadio;
    private JRadioButton sakitRadio;
    private JRadioButton izinRadio;
    private ButtonGroup statusGroup;
    private JTextArea hasilArea;
    private JButton submitButton;
    private JButton resetButton;
    private JLabel statusLabel;
    private JPanel formPanel;
    private JPanel hasilPanel;

    private Socket socket;
    private Scanner networkInput;
    private PrintWriter networkOutput;

    public AbsensiClientGUI() {
        setTitle("Aplikasi Absensi Mahasiswa");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 250));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 150, 243));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("SISTEM ABSENSI MAHASISWA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusLabel = new JLabel("Status: Belum Terhubung");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(255, 200, 200));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Form Absensi",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(15, 15, 15, 15)));

        
        JPanel namaPanel = new JPanel(new BorderLayout(10, 5));
        namaPanel.setBackground(Color.WHITE);
        namaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel namaLabel = new JLabel("Nama Lengkap:");
        namaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        namaField = new JTextField();
        namaField.setFont(new Font("Arial", Font.PLAIN, 13));
        namaField.setPreferredSize(new Dimension(0, 35));
        namaPanel.add(namaLabel, BorderLayout.NORTH);
        namaPanel.add(namaField, BorderLayout.CENTER);
        
        JPanel nimPanel = new JPanel(new BorderLayout(10, 5));
        nimPanel.setBackground(Color.WHITE);
        nimPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel nimLabel = new JLabel("NIM:");
        nimLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nimField = new JTextField();
        nimField.setFont(new Font("Arial", Font.PLAIN, 13));
        nimField.setPreferredSize(new Dimension(0, 35));
        nimPanel.add(nimLabel, BorderLayout.NORTH);
        nimPanel.add(nimField, BorderLayout.CENTER);

        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Pilih Status:"),
                new EmptyBorder(5, 10, 5, 10)));
        hadirRadio = new JRadioButton("Hadir");
        hadirRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        hadirRadio.setBackground(Color.WHITE);
        hadirRadio.setSelected(true);

        sakitRadio = new JRadioButton("Sakit");
        sakitRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        sakitRadio.setBackground(Color.WHITE);

        izinRadio = new JRadioButton("Izin");
        izinRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        izinRadio.setBackground(Color.WHITE);

        statusGroup = new ButtonGroup();
        statusGroup.add(hadirRadio);
        statusGroup.add(sakitRadio);
        statusGroup.add(izinRadio);

        statusPanel.add(hadirRadio);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(sakitRadio);
        statusPanel.add(Box.createVerticalStrut(5));
        statusPanel.add(izinRadio);

        formPanel.add(namaPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(nimPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(statusPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 250));

        submitButton = new JButton("Kirim Absensi");
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setBackground(new Color(76, 175, 80));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(180, 45));
        submitButton.addActionListener(e -> kirimAbsensi());

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(244, 67, 54));
        resetButton.setForeground(Color.BLACK);
        resetButton.setFocusPainted(false);
        resetButton.setPreferredSize(new Dimension(120, 45));
        resetButton.addActionListener(e -> resetForm());

        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        
        hasilPanel = new JPanel(new BorderLayout(5, 5));
        hasilPanel.setBackground(Color.WHITE);
        hasilPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "Hasil Absensi",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                new EmptyBorder(10, 10, 10, 10)));
        hasilPanel.setVisible(false);

        hasilArea = new JTextArea();
        hasilArea.setEditable(false);
        hasilArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        hasilArea.setBackground(new Color(250, 250, 250));
        hasilArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(hasilArea);
        hasilPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(245, 245, 250));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(hasilPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void kirimAbsensi() {
        String nama = namaField.getText().trim();
        String nim = nimField.getText().trim();

        if (nama.isEmpty() || nim.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama dan NIM harus diisi!",
                    "Validasi Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = "1";
        if (sakitRadio.isSelected())
            status = "2";
        else if (izinRadio.isSelected())
            status = "3";
        final String finalStatus = status;

        submitButton.setEnabled(false);
        statusLabel.setText("Status: Menghubungi server...");
        statusLabel.setForeground(Color.YELLOW);

        new Thread(() -> {
            try {
                socket = new Socket(HOST, PORT);
                networkInput = new Scanner(socket.getInputStream());
                networkOutput = new PrintWriter(socket.getOutputStream(), true);

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Status: Terhubung ke server");
                    statusLabel.setForeground(new Color(100, 255, 100));
                });

                StringBuilder hasil = new StringBuilder();
                
                while (networkInput.hasNextLine()) {
                    String serverMessage = networkInput.nextLine();

                    if (serverMessage.contains("Masukkan nama")) {
                        networkOutput.println(nama);
                    } else if (serverMessage.contains("Masukkan NIM")) {
                        networkOutput.println(nim);
                    } else if (serverMessage.contains("Pilih")) {
                        networkOutput.println(finalStatus);
                    } else {
                        hasil.append(serverMessage).append("\n");
                    }
                }

                final String hasilFinal = hasil.toString();
                SwingUtilities.invokeLater(() -> {
                    hasilArea.setText(hasilFinal);
                    hasilPanel.setVisible(true);
                    revalidate();
                    repaint();

                    if (hasilFinal.contains("Data tersimpan")) {
                        JOptionPane.showMessageDialog(this,
                                "Absensi berhasil dikirim!",
                                "Sukses",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Absensi gagal! Kuota penuh.",
                                "Gagal",
                                JOptionPane.WARNING_MESSAGE);
                    }

                    statusLabel.setText("Status: Selesai");
                    statusLabel.setForeground(Color.LIGHT_GRAY);
                });

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Gagal terhubung ke server!\n" + e.getMessage(),
                            "Koneksi Error",
                            JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Status: Koneksi gagal");
                    statusLabel.setForeground(new Color(255, 100, 100));
                });
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> submitButton.setEnabled(true));
            }
        }).start();
    }

    private void resetForm() {
        namaField.setText("");
        nimField.setText("");
        hadirRadio.setSelected(true);
        hasilArea.setText("");
        hasilPanel.setVisible(false);
        statusLabel.setText("Status: Belum Terhubung");
        statusLabel.setForeground(new Color(255, 200, 200));
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            AbsensiClientGUI client = new AbsensiClientGUI();
            client.setVisible(true);
        });
    }
}
