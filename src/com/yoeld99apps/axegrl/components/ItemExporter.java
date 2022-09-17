package com.yoeld99apps.axegrl.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ItemExporter {
    // singleton exporter
        public static final ItemExporter exporter = new ItemExporter();

        // action butons comand identifiers
        private static final String SELECT_BUTTON_COMAND = "@SELECT";
        private static final String EXPORT_BUTTON_COMAND = "@EXPORT";
        private static final String CANCEL_BUTTON_COMAND = "@CANCEL";

        // export dialog configuration fields
        final JDialog exportDialog;
        final JTextField exportOutputField;
        final JLabel exportWidthLabel;
        final JLabel exportHeightLabel;

        final JFileChooser chooser;
        final FileFilter extensionFilter;
        final ActionListener buttonsListener;

        // exporting path values
        static String outputPath;
        Component exportedItem;
        String fileName;
        File selected;

        private ItemExporter() {

            chooser = new JFileChooser();
            extensionFilter = new FileNameExtensionFilter("Portable Network Graphics (PNG)", "png");

            // meke export settings dialog
            exportDialog = new JDialog();
            exportDialog.setSize(500, 220);
            exportDialog.setResizable(false);
            exportDialog.setLocationRelativeTo(null);

            Container dcontainer = new Container();
            dcontainer.setBounds(0, 0, 500, 210);
            exportDialog.getContentPane().add(dcontainer);

            exportOutputField = new JTextField();
            JLabel outputlb = new JLabel("OUTPUT FILE:");
            JLabel widthlb = new JLabel("WIDTH:");
            JLabel heightlb = new JLabel("HEIGHT:");

            JButton selectb = new JButton("...");
            JButton exportb = new JButton("EXPORT");
            JButton cancelb = new JButton("CANCEL");

            // output file selection
            outputlb.setBounds(10, 30, 90, 20);
            dcontainer.add(outputlb);
            exportOutputField.setBounds(100, 30, 310, 20);
            dcontainer.add(exportOutputField, 1);
            selectb.setBounds(420, 30, 40, 20);
            selectb.setActionCommand(SELECT_BUTTON_COMAND);
            dcontainer.add(selectb);

            // render width selection
            widthlb.setBounds(10, 90, 100, 20);
            dcontainer.add(widthlb);
            exportWidthLabel = widthlb;

            // render height selection
            heightlb.setBounds(10, 120, 100, 20);
            dcontainer.add(heightlb);
            exportHeightLabel = heightlb;

            // action buttons
            exportb.setBounds(270, 160, 100, 25);
            exportb.setActionCommand(EXPORT_BUTTON_COMAND);
            dcontainer.add(exportb);

            cancelb.setBounds(380, 160, 100, 25);
            cancelb.setActionCommand(CANCEL_BUTTON_COMAND);
            dcontainer.add(cancelb);

            buttonsListener = (ActionEvent mouseEvent) -> {
                JButton btn = (JButton) mouseEvent.getSource();

                switch (btn.getActionCommand()) {
                    case SELECT_BUTTON_COMAND:
                        selectFile();
                        break;
                    case EXPORT_BUTTON_COMAND:
                        exportFile();
                        break;
                    case CANCEL_BUTTON_COMAND:
                        exportDialog.dispose();
                        break;
                }

            };

            selectb.addActionListener(buttonsListener);
            exportb.addActionListener(buttonsListener);
            cancelb.addActionListener(buttonsListener);

        }

        private void selectFile() {

            // choice output directory
            chooser.setDialogTitle("Select image file to export.");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(extensionFilter);

            if (outputPath == null) {
                // get current file name on field
                fileName = exportOutputField.getText();
            }

            // use current name as default optional
            String optFileName = fileName;

            // show selection file dialog
            if (chooser.showDialog(exportDialog, "SELECT") == JFileChooser.APPROVE_OPTION) {
                selected = chooser.getSelectedFile();

                if (selected.isDirectory()) {
                    // Selected directory and compose path with optName
                    outputPath = selected.getAbsolutePath() + File.separator + optFileName;

                } else {
                    // Selected file
                    outputPath = selected.getAbsolutePath();

                }
            }

            if (outputPath != null) {
                // update selected file path on field
                exportOutputField.setText(outputPath);
            }

        }

        private void exportFile() {

            //get item export dimensions
            int width = exportedItem.getWidth();
            int height = exportedItem.getHeight();

            outputPath = exportOutputField.getText();

            // create output-buffer to store
            File output = new File(outputPath);
            BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            buffer.setAccelerationPriority(1.0f);
            
            try {

                if (!output.exists()) {
                    // make a new output file
                    output.createNewFile();
                    System.out.println("Create new file");

                } else {
                    //select override-case action
                    switch (JOptionPane.showConfirmDialog(exportDialog, "You want to overryde File on Path: " + output.getAbsolutePath())) {
                        case JOptionPane.NO_OPTION:
                            // return to re-configure output path
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            // close exportation dialog
                            exportDialog.dispose();
                            return;
                        default:
                        // continue exportation and override output file
                    }
                }

            } catch (IOException ioe) {
                // show error dialog with message 
                JOptionPane.showMessageDialog(exportDialog, "Can't be create file on Path: " + output.getAbsolutePath(), "ERROR EXPORTING ...", JOptionPane.ERROR_MESSAGE);

            }

            try {
                // renderize item content on output graphic buffer
                exportedItem.paint((Graphics2D) buffer.getGraphics());
                
                // save image data of graphic buffer into file
                ImageIO.write(buffer, "png", output);

                // show susses dialog with info
                JOptionPane.showMessageDialog(exportDialog, "Path: " + output.getCanonicalPath() + "\nName: " + output.getName() + "\nResolution: " + width + "x" + height, "EXPORTED FILE.", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ioe) {
                // show error dialog with message 
                JOptionPane.showMessageDialog(exportDialog, "Can't be renderize file on Path: " + output.getAbsolutePath(), "ERROR EXPORTING ...", JOptionPane.ERROR_MESSAGE);

            }

            // close exportation dialog
            exportDialog.dispose();
        }

        public void export(Component item) {

            // se defauld exortation values
            this.exportedItem = item;
            this.fileName = "image" + System.currentTimeMillis() + ".png";
            this.outputPath = null;

            // set dialog default values
            exportOutputField.setText(fileName);
            exportWidthLabel.setText("WIDTH " + item.getWidth());
            exportHeightLabel.setText("HEIGHT " + item.getHeight());
            
            // show export dialog
            exportDialog.setVisible(true);

        }
        
}
