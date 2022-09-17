/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoeld99apps.axegrl.utils;

import com.yoeld99apps.axegrl.components.Item;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageDisplayer extends Item {

    private static final String OPEN_FILE_ACTION_COMAND = "OPEN_FILE";
    private static final String SHOW_INFO_ACTION_COMAND = "SHOW_INFO";
    
    private Image drawableImage;
    private Rectangle drawableShape;
    private File selectedImageFile;

    public ImageDisplayer() {
        super();

        drawableImage = null;
        drawableShape = null;

        // add item menu options
        addMenuItem("MainMenu","Open Image", OPEN_FILE_ACTION_COMAND);
        addMenuItem("MainMenu","Show Info", SHOW_INFO_ACTION_COMAND);
        enableMenu("MainMenu");
        
        enableEvents();
        
        initialize();
        ImageDisplayer.this.setSize(500, 600);
        selectImageFile();
        
    }

    private void selectImageFile() {
        
        File selected = ImageFileChooser.chooser.chooseFile(this);
        
        if (selected != null) {
            
            selectedImageFile = selected;
            
            // reset drwable screen shape
            drawableShape = null;

            //load image from source 
            drawableImage = TOOLKIT.getImage(selectedImageFile.getPath());
            prepareImage(drawableImage, this);
            
            repaint();
        }

    }

    private void showImageInfo() {
        if (selectedImageFile != null) {
            ImageInfoDialog.dialog.showInfo(selectedImageFile, drawableImage);
        }
    }

    public static Rectangle computeNormalizedImageShape(Component comp, Image image, int width, int height) {

        if (image == null) {
            return null;
        }

        // check avaliable image width and height
        if ((comp.checkImage(image, null) & (ImageObserver.WIDTH | ImageObserver.HEIGHT)) == 0) {
            return null;
        }

        // compute image and frame aspect relation 
        double ratios = (double) width / (double) height;
        double ratioi = (double) image.getWidth(null) / (double) image.getHeight(null);
        double scale = 0.99;

        // compute normalized IN X shape dimensions
        int sizeW = (int) (width / ratios * ratioi);
        int sizeH = (int) height;

        if (sizeW >= width) {

            // compute normalized IN Y shape dimensions
            sizeW = width;
            sizeH = (int) (height * ratios / ratioi);

            if (sizeH >= height) {
                // compute reduction scale
                scale = 1.0 - ((double) sizeH - height) / height;

                // normalize scale bethween 1.0 and 0.1
                if (scale < 0.1) {
                    scale = 0.1;
                }

            }

        }

        // compute final scaled shape dimensions
        sizeW *= scale;
        sizeH *= scale;

        return new Rectangle(
                width / 2 - sizeW / 2, // corner point x
                height / 2 - sizeH / 2, // corner point y
                sizeW, // drawable width
                sizeH // drawable height
        );

    }

    @Override
    public boolean isExportable() {
        return false;
    }

    @Override
    public void onResized(int width, int height) {
        drawableShape = null;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (e.getButton() == e.BUTTON3) {
            showMenu("MainMenu", e.getPoint());
        }
    }

    @Override
    public void onMenuItemSelected(MenuItem item, String command) {

        // execute selected item action
        switch (command) {
            case OPEN_FILE_ACTION_COMAND:
                selectImageFile();
                break;

            case SHOW_INFO_ACTION_COMAND:
                showImageInfo();
                break;
        }
    }

    @Override
    public void onPaint(Graphics2D g) {

        // recomputed drawable rectangle with image
        if (drawableShape == null) {
            drawableShape = computeNormalizedImageShape(this, drawableImage, width, height);
        }

        // draw image if has completelly loaded
        if (drawableShape != null) {
            g.drawImage(drawableImage, drawableShape.x, drawableShape.y, drawableShape.width, drawableShape.height, this);
        }
        
    }
    
    private static class ImageInfoDialog extends JFrame {

        JTextArea infoPath;
        JLabel lbWidth;
        JLabel lbHeight;

        static ImageInfoDialog dialog = new ImageInfoDialog();

        public ImageInfoDialog() {
            super();

            // make dialog elements
            GridLayout layout = new GridLayout(4, 2);
            this.getContentPane().setLayout(layout);

            infoPath = new JTextArea();
            infoPath.setLineWrap(true);
            infoPath.setSize(300, 50);

            this.setTitle("image info");
            this.add(new JLabel("Path: "));
            this.add(infoPath);

            lbWidth = new JLabel();
            this.add(new JLabel("Width: "));
            this.add(lbWidth);

            lbHeight = new JLabel();
            this.add(new JLabel("Height: "));
            this.add(lbHeight);

            // configure dialog
            this.setSize(400, 250);
            this.setResizable(false);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
        }

        private void showInfo(File selectedImageFile, Image drawableImage) {

            // define showed image info
            infoPath.setText(selectedImageFile.getPath());
            lbWidth.setText(drawableImage.getWidth(null) + "px");
            lbHeight.setText(drawableImage.getHeight(null) + "px");

            // show dialog
            setLocationRelativeTo(null);
            this.setEnabled(true);
            this.setVisible(true);
            
        }

    }

    public static class ImageFileChooser extends JFileChooser {

        static ImageFileChooser chooser = new ImageFileChooser();

        File lastFile;

        public ImageFileChooser() {
            super();

            FileFilter filter = new FileNameExtensionFilter("Image File", "png", "jpg", "gif", "bmp", "ico");
            this.lastFile = new File(".");

            // choice source image file
            this.setDialogTitle("Select image file to export.");
            this.setFileSelectionMode(JFileChooser.FILES_ONLY);
            this.setFileFilter(filter);
            this.setFileHidingEnabled(false);
            
        }

        public File chooseFile(Component parent) {
            
            setCurrentDirectory(lastFile);
            
            int responseCode = this.showDialog(parent, "SELECT");
            
            if (responseCode != JFileChooser.APPROVE_OPTION) {
                return null;
            }
            
            // get selected file
            lastFile = getSelectedFile();
            return this.getSelectedFile();
        }

    }
    
}
