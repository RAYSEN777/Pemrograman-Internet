import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader extends JFrame {
    private JTextField urlField;
    private JComboBox<String> formatBox;
    private JButton btnDownload, btnPause, btnResume, btnCancel;
    private DefaultTableModel tableModel;
    private JTable table;
    private DownloadTask currentTask;

    public FileDownloader() {
        initUI();
    }

    private void initUI() {
        setTitle("File Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 400);
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
        c.gridwidth = 3;
        urlField = new JTextField("");
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
        c.gridwidth = 4;
        c.weightx = 1.0;
        JPanel btnPanel = new JPanel();
        btnDownload = new JButton("Download");
        btnPause = new JButton("Pause");
        btnResume = new JButton("Resume");
        btnCancel = new JButton("Cancel");

        btnPause.setEnabled(false);
        btnResume.setEnabled(false);
        btnCancel.setEnabled(false);

        btnPanel.add(btnDownload);
        btnPanel.add(btnPause);
        btnPanel.add(btnResume);
        btnPanel.add(btnCancel);

        panel.add(btnPanel, c);

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
        btnPause.addActionListener(e -> {
            if (currentTask != null) currentTask.pause();
        });
        btnResume.addActionListener(e -> {
            if (currentTask != null) currentTask.resumeTask();
        });
        btnCancel.addActionListener(e -> {
            if (currentTask != null) currentTask.cancelTask();
        });

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Log"));

        tableModel = new DefaultTableModel(
            new Object[]{"URL", "Size", "Status", "Finished At"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        loadLogsFromDatabase();

        return panel;
    }

    private void loadLogsFromDatabase() {
        tableModel.setRowCount(0); 
        for (String[] row : DatabaseHelper.getLogs()) {
            tableModel.addRow(row);
        }
    }

    private class DownloadClick implements ActionListener {
        private JTextField customField;

        public DownloadClick(JTextField customField) {
            this.customField = customField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String link = urlField.getText().trim();
            String format;

            if ("custom".equals(formatBox.getSelectedItem())) {
                format = customField.getText().trim();
                if (format.isEmpty()) {
                    JOptionPane.showMessageDialog(FileDownloader.this,
                            "Please enter a custom format", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                format = formatBox.getSelectedItem().toString();
            }

            if (link.isEmpty() || (!link.startsWith("http://") && !link.startsWith("https://"))) {
                JOptionPane.showMessageDialog(FileDownloader.this,
                        "Please enter a valid URL (http/https)", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save File As");
            chooser.setSelectedFile(new File("downloaded_file." + format));

            int res = chooser.showSaveDialog(FileDownloader.this);
            if (res != JFileChooser.APPROVE_OPTION) return;

            File outFile = chooser.getSelectedFile();
            if (!outFile.getName().toLowerCase().endsWith("." + format.toLowerCase())) {
                outFile = new File(outFile.getAbsolutePath() + "." + format);
            }

            btnDownload.setEnabled(false);
            btnPause.setEnabled(true);
            btnCancel.setEnabled(true);

            currentTask = new DownloadTask(link, outFile);
            currentTask.execute();
        }
    }

    private class DownloadTask extends SwingWorker<Void, Integer> {
        private String link;
        private File outFile;
        private int rowIndex;
        private volatile boolean paused = false;
        private final Object lock = new Object();

        public DownloadTask(String link, File outFile) {
            this.link = link;
            this.outFile = outFile;
            rowIndex = tableModel.getRowCount();
            tableModel.addRow(new Object[]{link, "0 B", "Downloading", ""});
        }

        @Override
        protected Void doInBackground() {
            long total = 0;
            long fileSize = -1;

            try {
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                fileSize = conn.getContentLengthLong();

                try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                     BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {

                    byte[] buf = new byte[8192];
                    int n;

                    while ((n = in.read(buf)) != -1) {
                        if (isCancelled()) {
                            updateTable(rowIndex, "-", "Canceled", "");
                            outFile.delete();
                            DatabaseHelper.insertLog(link, "-", "Canceled", new java.util.Date().toString());
                            return null;
                        }

                        synchronized (lock) {
                            while (paused) lock.wait();
                        }

                        out.write(buf, 0, n);
                        total += n;

                        updateTable(rowIndex, formatSize(total), "Downloading", "");
                    }

                    long finalSize = (fileSize > 0) ? fileSize : total;
                    String sizeStr = formatSize(finalSize);

                    updateTable(rowIndex, sizeStr, "Completed", new java.util.Date().toString());
                    DatabaseHelper.insertLog(link, sizeStr, "Completed", new java.util.Date().toString());
                }
            } catch (Exception ex) {
                updateTable(rowIndex, "-", "Failed: " + ex.getMessage(), "");
                DatabaseHelper.insertLog(link, "-", "Failed", new java.util.Date().toString());
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnDownload.setEnabled(true);
                    btnPause.setEnabled(false);
                    btnResume.setEnabled(false);
                    btnCancel.setEnabled(false);
                });
            }
            return null;
        }

        public void pause() {
            paused = true;
            btnPause.setEnabled(false);
            btnResume.setEnabled(true);
            tableModel.setValueAt("Paused", rowIndex, 2);
        }

        public void resumeTask() {
            synchronized (lock) {
                paused = false;
                lock.notifyAll();
            }
            btnPause.setEnabled(true);
            btnResume.setEnabled(false);
            tableModel.setValueAt("Downloading", rowIndex, 2);
        }

        public void cancelTask() {
            cancel(true);
            btnPause.setEnabled(false);
            btnResume.setEnabled(false);
            btnCancel.setEnabled(false);
            btnDownload.setEnabled(true);
        }

        private void updateTable(int row, String size, String status, String finished) {
            tableModel.setValueAt(size, row, 1);
            tableModel.setValueAt(status, row, 2);
            tableModel.setValueAt(finished, row, 3);
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
