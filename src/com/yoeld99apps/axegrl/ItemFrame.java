package com.yoeld99apps.axegrl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class ItemFrame extends JInternalFrame implements ActionListener {

    private static int itemID = 0;

    private static final String EXPORT_ACTION_COMAND = "EXPORT";

    // main frame container
    private ItemFrameContainer container;

    // frame mouse event dispatchers
    private final MouseAdapter rigthClicklistener;

    // export dialog configuration fields
    private JDialog exportDialog;
    private JTextField exportOutputField;
    private JLabel exportWidthLabel;
    private JLabel exportHeightLabel;

    // exporting path values
    private String outputPath;
    private String fileName;

    // frame generic popupMenu with actions
    private PopupMenu optionMenu;
    private boolean isMenuEnable;

    // frame state flags
    private boolean isExportable;

    // graphics elements
    public ItemFrame() {
        this(300, 400);
    }

    public ItemFrame(int width, int height) {
        super();

        // set internal-frame alocation
        setSize(width, height);
        setLocation(100, 100);
        setDoubleBuffered(true);

        // set internal-frame UI capabilities
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);

        //make mouse events listeners
        rigthClicklistener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (isMenuEnable && me.getButton() == MouseEvent.BUTTON3) {
                    optionMenu.show(container, me.getX(), me.getY());
                }
            }
        };

        // make UI container pane
        container = new ItemFrameContainer(this);
        container.addMouseListener(rigthClicklistener);
        getContentPane().add(container);

        // make main option menu
        optionMenu = new PopupMenu();
        container.add(optionMenu);

        // add export option
        addMenuItem("Save as Image", EXPORT_ACTION_COMAND);
        isMenuEnable = true;
        isExportable = true;

    }

    @Override
    public void reshape(int x, int y, int width, int height) {

        if(getWidth() != width || getHeight() != height){
            // notify UI size changes to top-level herarchy class
            changedSize(width, height);
        }

        // reshape frame window
        super.reshape(x, y, width, height);

    }

    protected Container getContainer() {
        return container;
    }

    protected abstract void changedSize(int width, int height);
    
    protected abstract void renderize(Graphics2D g, int width, int height);

    protected void setExportable(boolean isExportable) {
        this.isExportable = isExportable;

        // enable OR disable export option on menu 
        optionMenu.getItem(0).setEnabled(isExportable);
    }

    public boolean isExportable() {
        return this.isExportable;
    }

    protected MenuItem addMenuItem(MenuItem mItem) {
        if (mItem == null) {
            return null;
        }

        mItem.addActionListener(this);

        return optionMenu.add(mItem);
    }

    protected MenuItem addMenuItem(String label, String comand) {
        if (label == null | comand == null) {
            return null;
        }

        MenuItem item = new MenuItem(label);
        item.setActionCommand(comand);
        item.addActionListener(this);

        return optionMenu.add(item);
    }

    protected void loockMenu() {
        isMenuEnable = false;
    }

    protected void unloockMenu() {
        isMenuEnable = true;
    }

    protected boolean isMenuLoocked() {
        return isMenuEnable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MenuItem item = (MenuItem) e.getSource();

        switch (item.getActionCommand()) {
            case EXPORT_ACTION_COMAND:
                exportAsImage();
                break;
            default:
                System.out.println(item.getActionCommand());
                onItemSelected(item);
        }

    }

    protected abstract void onItemSelected(MenuItem item);

    public void exportAsImage() {

        fileName = "image" + System.currentTimeMillis() + ".png";
        outputPath = null;

        if (exportDialog == null) {

            // meke export settings dialog
            exportDialog = new JDialog();
            exportDialog.setSize(500, 220);
            exportDialog.setResizable(false);
            exportDialog.setLocationRelativeTo(null);

            Container dcontainer = new Container();
            dcontainer.setBounds(0, 0, 500, 210);
            exportDialog.getContentPane().add(dcontainer);

            JLabel outputlb = new JLabel("OUTPUT FILE:");
            JLabel widthlb = new JLabel("WIDTH:");
            JLabel heightlb = new JLabel("HEIGHT:");

            final JTextField outputv = new JTextField();

            JButton selectb = new JButton("...");
            JButton exportb = new JButton("EXPORT");
            JButton cancelb = new JButton("CANCEL");

            // output file selection
            outputlb.setBounds(10, 30, 90, 20);
            dcontainer.add(outputlb);
            outputv.setBounds(100, 30, 310, 20);
            dcontainer.add(outputv, 1);
            exportOutputField = outputv;
            selectb.setBounds(420, 30, 40, 20);
            selectb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // get path-name
                    if (outputPath == null) {
                        fileName = outputv.getText();
                    }

                    selectFile(fileName, exportDialog);

                    // update selected new path
                    if (outputPath != null) {
                        outputv.setText(outputPath);
                    }

                }
            });
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
            exportb.addMouseListener(new MouseAdapter() {
                int value = 0;

                @Override
                public void mouseClicked(MouseEvent e) {

                    // use desgned name on root directory
                    outputPath = outputv.getText();

                    // export image and close dialog
                    if (1 == exportFile(outputPath, exportDialog)) {
                        exportDialog.dispose();
                    }

                }
            });
            dcontainer.add(exportb);
            
            cancelb.setBounds(380, 160, 100, 25);
            cancelb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    exportDialog.dispose();
                }
            });
            dcontainer.add(cancelb);
        }

        // show dialog with default values
        exportDialog.setVisible(true);
        exportOutputField.setText(fileName);
        exportWidthLabel.setText("WIDTH " + container.getWidth());
        exportHeightLabel.setText("HEIGHT " + container.getHeight());

    }

    private void selectFile(String optFileName, Component c) {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Portable Network Graphics (PNG)", "png");
        File selected;

        // choice output directory
        chooser.setDialogTitle("Select image file to export.");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(filter);
        int responseCode = chooser.showDialog(c, "SELECT");

        if (responseCode == JFileChooser.APPROVE_OPTION) {
            selected = chooser.getSelectedFile();

            if (selected.isDirectory()) {
                // Selected directory
                outputPath = selected.getAbsolutePath() + File.separator + optFileName;

            } else {
                // Selected file
                outputPath = selected.getAbsolutePath();

            }
        }

    }

    private int exportFile(String outputPath, Component c) {

        int width = container.getWidth();
        int height = container.getHeight();

        // create output-buffer to store (MAX SCALE)
        File output = new File(outputPath);
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        buffer.setAccelerationPriority(1);

        try {
            // make a new output file
            if (!output.exists()) {
                output.createNewFile();
                System.out.println("Create new file");
            } else {
                //select override-case action
                switch (JOptionPane.showConfirmDialog(c, "You want to overryde File on Path: " + output.getAbsolutePath())) {
                    case JOptionPane.NO_OPTION:
                        return 0;   // return to exportation settings
                    case JOptionPane.CANCEL_OPTION:
                        return 1;   // cancel exportation
                    default:
                        ;           // continue exportationand override output file
                }
            }

        } catch (IOException ioe) {
            // show error dialog with message 
            JOptionPane.showMessageDialog(c, "Can't be create file on Path: " + output.getAbsolutePath(), "ERROR EXPORTING ...", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();

        }

        try {
            // renderize image on output graphic buffer
            container.paint(buffer.getGraphics());

            // save image data on graphic buffer into file
            ImageIO.write(buffer, "png", output);

            // show susses dialog with info
            JOptionPane.showMessageDialog(c, "Path: " + output.getCanonicalPath() + "\nName: " + output.getName() + "\nResolution: " + width + "x" + height, "EXPORTED FILE.", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ioe) {
            // show error dialog with message 
            JOptionPane.showMessageDialog(c, "Can't be renderize file on Path: " + output.getAbsolutePath(), "ERROR EXPORTING ...", JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        }

        return 1;
    }

}
