import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.zip.*;

public class ExtractZipUI extends JFrame {
    private JTextField zipPathField, outDirField;
    private JButton cariZipBtn, cariOutBtn, startExtractBtn;

    private JTextField folderPathField;
    private JButton browseFolderBtn, startCompressBtn;

    public ExtractZipUI() {
        setTitle("zip-in aja");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Dekompresi", extractP());
        tabs.add("Kompresi", compressP());

        add(tabs);
    }

    private JPanel extractP() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        zipPathField = new JTextField();
        outDirField = new JTextField();
        cariZipBtn = new JButton("Cari");
        cariOutBtn = new JButton("Cari");
        startExtractBtn = new JButton("Dekompresi");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Zip File:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(zipPathField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        cariZipBtn.setPreferredSize(new Dimension(70, 25));
        panel.add(cariZipBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Output Folder:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(outDirField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        cariOutBtn.setPreferredSize(new Dimension(70, 25));
        panel.add(cariOutBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        startExtractBtn.setPreferredSize(new Dimension(120, 30));
        panel.add(startExtractBtn, gbc);
        
        cariZipBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                zipPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        cariOutBtn.addActionListener(e -> {
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

    private JPanel compressP() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        folderPathField = new JTextField();
        browseFolderBtn = new JButton("Cari");
        startCompressBtn = new JButton("Kompres Folder");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Folder ke Zip:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(folderPathField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        browseFolderBtn.setPreferredSize(new Dimension(70, 25));
        panel.add(browseFolderBtn, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        startCompressBtn.setPreferredSize(new Dimension(150, 30));
        panel.add(startCompressBtn, gbc);

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
                File outZip = new File(folder.getParentFile(), folder.getName() + "_Kompres.zip");
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
                        while ((len = zInStream.read(buffer)) != -1) {
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