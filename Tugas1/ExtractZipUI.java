import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.zip.*;

public class ExtractZipUI extends JFrame {
    private JTextField zipPathField, outDirField;
    private JButton browseZipBtn, browseOutBtn, startExtractBtn;

    private JTextField folderPathField;
    private JButton browseFolderBtn, startCompressBtn;

    public ExtractZipUI() {
        setTitle("Compress dan Decompress");
        setSize(600, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Decompress", createExtractPanel());
        tabs.add("Compress", createCompressPanel());

        add(tabs);
    }

    private JPanel createExtractPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 5, 5));

        zipPathField = new JTextField();
        outDirField = new JTextField();
        browseZipBtn = new JButton("Browse");
        browseOutBtn = new JButton("Browse");
        startExtractBtn = new JButton("Decompress");

        panel.add(new JLabel("Zip File:"));
        panel.add(zipPathField);
        panel.add(browseZipBtn);

        panel.add(new JLabel("Output Folder:"));
        panel.add(outDirField);
        panel.add(browseOutBtn);

        panel.add(new JLabel());
        panel.add(startExtractBtn);
        panel.add(new JLabel());

        
        browseZipBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                zipPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        browseOutBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                outDirField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        startExtractBtn.addActionListener(e -> {
            String zipFile = zipPathField.getText();
            String outDir = outDirField.getText();

            if (zipFile.isEmpty() || outDir.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih Filenya");
                return;
            }

            try {
                File inputZip = new File(zipFile);
                File outputDir = new File(outDir);

                extractZip(inputZip, outputDir);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return panel;
    }

    private JPanel createCompressPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));

        folderPathField = new JTextField();
        browseFolderBtn = new JButton("Browse");
        startCompressBtn = new JButton("Compress Folder");

        panel.add(new JLabel("Folder to Zip:"));
        panel.add(folderPathField);
        panel.add(browseFolderBtn);

        panel.add(new JLabel());
        panel.add(startCompressBtn);
        panel.add(new JLabel());

        browseFolderBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                folderPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        startCompressBtn.addActionListener(e -> {
            String folderPath = folderPathField.getText();

            if (folderPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih Foldernya");
                return;
            }

            try {
                File folder = new File(folderPath);
                File outZip = new File(folder.getParentFile(), folder.getName() + "_Compressed.zip");

                compressFolder(folder, outZip);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return panel;
    }

    private static void extractZip(File zipFile, File outDir) throws IOException {
        try (
            FileInputStream fInStream = new FileInputStream(zipFile);
            BufferedInputStream bInStream = new BufferedInputStream(fInStream);
            ZipInputStream zInStream = new ZipInputStream(bInStream)
        ) {
            ZipEntry e;
            while ((e = zInStream.getNextEntry()) != null) {
                File outFile = new File(outDir, e.getName());

                if (e.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fOutStream = new FileOutputStream(outFile);
                         BufferedOutputStream bOutStream = new BufferedOutputStream(fOutStream)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zInStream.read(buffer)) > 0) {
                            bOutStream.write(buffer, 0, len);
                        }
                    }

                    if (outFile.getName().toLowerCase().endsWith(".zip")) {
                        extractZip(outFile, outFile.getParentFile());
                        outFile.delete();
                    }
                }
                zInStream.closeEntry();
            }
        }
    }

    private static void compressFolder(File folder, File zipFile) throws IOException {
        try (
            FileOutputStream fOut = new FileOutputStream(zipFile);
            BufferedOutputStream bOut = new BufferedOutputStream(fOut);
            ZipOutputStream zOut = new ZipOutputStream(bOut)
        ) {
            compressRecursive(folder, folder.getName() + "/", zOut);
        }
    }

    private static void compressRecursive(File file, String entryName, ZipOutputStream zOut) throws IOException {
        if (file.isDirectory()) {
            zOut.putNextEntry(new ZipEntry(entryName));
            zOut.closeEntry();

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    compressRecursive(child, entryName + child.getName() + (child.isDirectory() ? "/" : ""), zOut);
                }
            }
        } else {
            try (FileInputStream fIn = new FileInputStream(file);
                 BufferedInputStream bIn = new BufferedInputStream(fIn)) {
                zOut.putNextEntry(new ZipEntry(entryName));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = bIn.read(buffer)) > 0) {
                    zOut.write(buffer, 0, len);
                }
                zOut.closeEntry();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExtractZipUI().setVisible(true));
    }
}