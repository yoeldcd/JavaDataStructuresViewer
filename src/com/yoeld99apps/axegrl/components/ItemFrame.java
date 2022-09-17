package com.yoeld99apps.axegrl.components;

import java.awt.Container;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ItemFrame extends JInternalFrame {

    // main frame parentItem
    private final Component item;
    public final Toolkit toolkit;
    
    // graphics elements
    private ItemFrame(Component item) {
        super();
        
        // set internal-frame alocation
        setSize(item.getWidth(), item.getHeight() + 50);
        setDoubleBuffered(true);
        
        // set internal-frame UI capabilities
        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setIconifiable(true);
        
        // make UI parentItem pane
        this.item = item;
        this.toolkit = Toolkit.getDefaultToolkit();
        getContentPane().add(item);
        
    }
    
    public Component retainItem(){
        this.getContentPane().setIgnoreRepaint(true);
        return item;
    }
    
    public Component recoverItem(){
        this.getContentPane().setIgnoreRepaint(false);
        return item;
    }
    
    public void export() {
        ItemExporter.exporter.export(this.item);
    }

    public static ItemFrame makeFrom(Component item) {
        if (item == null) {
            return null;
        }
        
        return new ItemFrame(item);
    }
    
}
