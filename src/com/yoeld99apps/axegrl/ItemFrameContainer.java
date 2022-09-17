package com.yoeld99apps.axegrl;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;

public class ItemFrameContainer extends Container implements ImageObserver {

    private VolatileImage volatileImage;
    private Graphics graphics;
    private Color background;

    private final ItemFrame itemFrame;
    
    public ItemFrameContainer(ItemFrame frame) {
        background = null;
        itemFrame = frame;
        
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
        if (itemFrame.isIcon() | itemFrame.isClosed()) {
            return false;
        }
        
        switch (infoflags) {
            case ImageObserver.FRAMEBITS:
                repaint(1000);
                break;

            case ImageObserver.ALLBITS:
                System.out.println("Image Full loaded " + img.getProperty("url", null));
                repaint(1000);
                break;

            case ImageObserver.ERROR:
                System.err.println("Error Loading Image");
                break;
        }

        return true;
    }

    @Override
    public void setBackground(Color nBackground) {
        background = nBackground;
        repaint();
    }

    @Override
    public void reshape(int x, int y, int width, int height) {
        super.reshape(x, y, width, height);
        
        if (width > 0 && height > 0) {
            //make back-buffer image to optimize draw calls
            volatileImage = createVolatileImage(width, height);
            
        }
    }
    
    @Override
    public void paint(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        
        super.paint(g);
        
        if (width > 0 && height > 0) {

            // validate back-buffer image state
            if (volatileImage == null) {
                volatileImage = createVolatileImage(width, height);
            } else if (volatileImage.validate(null) == VolatileImage.IMAGE_INCOMPATIBLE) {
                volatileImage = createVolatileImage(width, height);
            }

            // get graphic context to draw
            graphics = volatileImage.getGraphics();
            graphics.setPaintMode();
            
            if (background != null) {
                // renderize component background
                graphics.setColor(background);
                graphics.fillRect(0, 0, width, height);
            } else {
                // clear component
                graphics.clearRect(0, 0, width, height);
            }

            // renderize displayable content on grafic buffer
            itemFrame.renderize((Graphics2D) graphics, width, height);
            
            // draw back-buffer image on output buffer
            if (g == null) {
                getGraphics().drawImage(volatileImage, 0, 0, width, height, this);
            } else {
                g.drawImage(volatileImage, 0, 0, width, height, this);
            }
            
        }
        
    }

}
