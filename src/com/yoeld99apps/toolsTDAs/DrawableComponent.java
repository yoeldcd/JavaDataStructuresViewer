
package com.yoeld99apps.toolsTDAs;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;    
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public abstract class DrawableComponent extends Component {
    
    // graphics elements
    private Image image;
    protected Graphics graphics;
    
    // option menu
    private JMenu menu;
    
    protected abstract void renderize(Graphics g, int width, int height);
    
    public JDialog showAsWindow(int width, int height) {
        JDialog mainFrame = new JDialog();
        
        // configure one container window
        mainFrame = new JDialog();

        mainFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        mainFrame.setSize(width, height);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.add(this);
        
        return mainFrame;
    }
    
    @Override
    public void paint(Graphics g){
        
        int width = getWidth();
        int height = getHeight();
        
        if (width > 0 && height > 0) {
            
            // create back-buffer image to optimize draw call
            this.image = this.createVolatileImage(width, height);
            this.image.setAccelerationPriority(1);
            this.graphics = image.getGraphics();
            
            // renderize list content on grafic buffer
            this.renderize(graphics, width, height);

            // draw back-buffer image on output buffer
            if (g == null) {
                this.getGraphics().drawImage(this.image, 0, 0, width, height, this);
            } else {
                g.drawImage(this.image, 0, 0, width, height, this);
            }

            // freeze VRAM resources
            this.graphics.dispose();

        }

    }
    
    public void exportAsImage() {

        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Portable Network Graphics (PNG)", "png");

        BufferedImage buffer = null;
        File output = null;
        String name = null;
        
        boolean writeOnFile = false;

        // get output directory
        chooser.setDialogTitle("Select image file to export.");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(filter);

        int responseCode = chooser.showDialog(this, "EXPORT");

        if (responseCode == JFileChooser.APPROVE_OPTION) {
            output = chooser.getSelectedFile();

            if (output.isDirectory()) {

                // get name of new file
                name = JOptionPane.showInputDialog("WRITE FILE NAME:", "" + System.currentTimeMillis());

                if (name.length() < 1) // use default file-name
                {
                    name = "" + System.currentTimeMillis();
                }

                // generate output file
                output = new File(output.getAbsolutePath() + File.separator + name + ".png");

            }

            if (output.exists()) {
                // get override option
                writeOnFile = JOptionPane.showConfirmDialog(this, "You want to overryde File on Path: " + output.getAbsolutePath()) == JOptionPane.OK_OPTION;

            } else {

                // make output file on directory
                try {
                    output.createNewFile();
                    writeOnFile = true;

                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(this, "Can't by create file on Path: " + output.getAbsolutePath(), "ERROR EXPORTING ...", JOptionPane.ERROR_MESSAGE);
                    ioe.printStackTrace();

                }

            }

            if (writeOnFile) {

                // create output-buffer to store (MAX SCALE)
                buffer = new BufferedImage(2048, 1024, BufferedImage.TYPE_3BYTE_BGR);
                buffer.setAccelerationPriority(1);
                this.renderize(buffer.getGraphics(), 2048, 1024);

                try {
                    // save image data of buffer on file
                    ImageIO.write(buffer, "png", output);
                    JOptionPane.showMessageDialog(this, "Path: " + output.getCanonicalPath() + "\nName: " + output.getName() + "\nResolution: 2048x1024", "EXPORTED FILE.", JOptionPane.INFORMATION_MESSAGE);

                } catch (IOException ioe) {
                    // show error message
                    JOptionPane.showMessageDialog(this, "Can't by create file on Path: " + output.getAbsolutePath(), "ERROR EXPORTING ...", JOptionPane.ERROR_MESSAGE);
                    ioe.printStackTrace();

                }

            }
        }

    }
    
}
