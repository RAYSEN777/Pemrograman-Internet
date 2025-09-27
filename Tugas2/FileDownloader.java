import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader extends JFrame {
private JTextField urlField;
private JComboBox<String> formatBox;
private JButton btnDownload;
private JTextArea logArea;

    public FileDownloader() {
        initUI();
    }

    private void initUI() {
        setTitle("File Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Download Settings"));
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridx = 0; c.gridy = 0;
        c.weightx = 0.2;
        panel.add(new JLabel("File URL:"), c);
        
        c.gridx = 1; c.gridy = 0;
        c.weightx = 0.8;
        c.gridwidth = 2;
        urlField = new JTextField("https://filesamples.com/samples/image/jpg/sample_5184%C3%973456.jpg");
        panel.add(urlField, c);
        
        c.gridx = 0; c.gridy = 1;
        c.weightx = 0.2;
        c.gridwidth = 1;
        panel.add(new JLabel("Format:"), c);
        
        c.gridx = 1; c.gridy = 1;
        c.weightx = 0.3;
        String[] formats = {"jpg", "png", "pdf", "mp3", "mp4", "txt", "zip", "custom"};
        formatBox = new JComboBox<>(formats);
        panel.add(formatBox, c);
        
        c.gridx = 2; c.gridy = 1;
        c.weightx = 0.5;
        JTextField customField = new JTextField();
        customField.setEnabled(false);
        panel.add(customField, c);
        
        c.gridx = 0; c.gridy = 2;
        c.gridwidth = 3;
        c.weightx = 1.0;
        btnDownload = new JButton("Download File");
        btnDownload.setForeground(Color.black);
        btnDownload.setBackground(new Color(70, 130, 180));
        btnDownload.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(btnDownload, c);
        
        formatBox.addActionListener(e -> {
            if ("custom".equals(formatBox.getSelectedItem())) {
                customField.setEnabled(true);
                customField.setText("");
            } else {
                customField.setEnabled(false);
                customField.setText(formatBox.getSelectedItem().toString());
            }
        });
        
        btnDownload.addActionListener(new DownloadClick(customField));
        
        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(240, 240, 240));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(logArea);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }

    private class DownloadClick implements ActionListener {
        private JTextField customField;
        
        public DownloadClick(JTextField customField) {
            this.customField = customField;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String link = urlField.getText().trim();
            String ext;
            
            if ("custom".equals(formatBox.getSelectedItem())) {
                ext = customField.getText().trim();
                if (ext.isEmpty()) {
                    JOptionPane.showMessageDialog(FileDownloader.this, 
                        "Please enter a custom format", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                ext = formatBox.getSelectedItem().toString();
            }
            
            if (link.isEmpty()) {
                JOptionPane.showMessageDialog(FileDownloader.this, 
                    "Please enter a URL", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!link.startsWith("http://") && !link.startsWith("https://")) {
                JOptionPane.showMessageDialog(FileDownloader.this, 
                    "Invalid URL (must start with http:// or https://)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save File As");
            chooser.setSelectedFile(new File("downloaded_file." + ext));
            
            int res = chooser.showSaveDialog(FileDownloader.this);
            if (res != JFileChooser.APPROVE_OPTION) {
                logArea.append("Download canceled by user.\n");
                return;
            }
            
            File outFile = chooser.getSelectedFile();
            
            if (!outFile.getName().toLowerCase().endsWith("." + ext.toLowerCase())) {
                outFile = new File(outFile.getAbsolutePath() + "." + ext);
            }
            
            btnDownload.setEnabled(false);
            new DownloadTask(link, outFile).execute();
        }
    }

    private class DownloadTask extends SwingWorker<Void, String> {
        private String link;
        private File outFile;
        
        public DownloadTask(String link, File outFile) {
            this.link = link;
            this.outFile = outFile;
        }
        
        @Override
        protected Void doInBackground() {
            try {
                publish("Starting download...");
                publish("URL: " + link);
                
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                
                try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
                    
                    byte[] buf = new byte[8192];
                    int n;
                    long total = 0;
                    
                    while ((n = in.read(buf)) != -1) {
                        out.write(buf, 0, n);
                        total += n;
                    }
                    
                    publish("Download completed!");
                    publish("File saved as: " + outFile.getAbsolutePath());
                    publish("Total downloaded: " + formatSize(total));
                }
                
            } catch (Exception ex) {
                publish("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
            return null;
        }
        
        @Override
        protected void process(java.util.List<String> logs) {
            for (String msg : logs) {
                logArea.append(msg + "\n");
            }
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        
        @Override
        protected void done() {
            try {
                get();
                JOptionPane.showMessageDialog(FileDownloader.this, 
                    "Download finished!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                logArea.append("Download failed: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(FileDownloader.this, 
                    "Download failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                btnDownload.setEnabled(true);
            }
        }
        
        private String formatSize(long b) {
            if (b < 1024) return b + " B";
            if (b < 1024 * 1024) return String.format("%.2f KB", b / 1024.0);
            if (b < 1024 * 1024 * 1024) return String.format("%.2f MB", b / (1024.0 * 1024.0));
            return String.format("%.2f GB", b / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new FileDownloader().setVisible(true));
    }
}
