package com.yoeld99apps.axegrl.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.util.HashMap;
import java.util.Map;

/*That class represent one generic individual
renderable element contained from a GUI Container

Provide predefined mechanisms to manage events and
optimize Layouts rendering
 */
public abstract class Item extends Container implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

    protected static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

    private Container parentItem;
    private HashMap<String, PopupMenu> menus;

    // displayable properties
    private VolatileImage volatileImage;
    private Graphics graphics;

    // item bounds
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    // state values
    private boolean isEventsEnable;
    private boolean isInitialized;
    private boolean isExportable;
    private boolean isDragEventStarted;

    public Item() {
        super();
        super.setFocusable(true);

        this.x = getX();
        this.y = getY();
        this.width = getWidth();
        this.height = getHeight();

        this.isEventsEnable = false;
        this.isInitialized = false;
        this.isExportable = false;

        this.menus = new HashMap<>();

        this.isDragEventStarted = false;
    }

    protected void initialize() {
        this.isInitialized = true;
        repaint();
    }

    public void setParentItem(Container parentItem) {

        if (parentItem != null) {
            this.parentItem = parentItem;
        }

    }

    public Container getParentItem() {
        return getParent();
    }

    public void setExportable(boolean isExportable) {
        this.isExportable = isExportable;
    }

    public boolean isExportable() {
        return isExportable;
    }

    public void enableEvents() {
        if (!isEventsEnable) {
            isEventsEnable = true;
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
        }
        
    }

    public void disableEvents() {
        if (isEventsEnable) {
            isEventsEnable = false;
            removeMouseListener(this);
            removeMouseMotionListener(this);
            removeKeyListener(this);
        }
    }

    public boolean isEventsEnable() {
        return isEventsEnable;
    }

    @Override
    public void setBackground(Color background) {
        super.setBackground(background);

    }

    @Override
    public void repaint() {
        if (isInitialized) {
            if (this.getParent() != null) {
                this.getParent().repaint();
            } else {
                super.repaint();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D gContext = null;

        if (width < 1 || height < 1) {
            return;
        }

        if (volatileImage == null || volatileImage.validate(null) == VolatileImage.IMAGE_INCOMPATIBLE) {
            // remake lost context back-buffer image state
            volatileImage = createVolatileImage(width, height);
        }

        // get rendering context
        if (volatileImage != null) {
            gContext = (Graphics2D) volatileImage.getGraphics();
            gContext.setPaintMode();

            if (getBackground() != null) {
                // renderize component background
                gContext.setColor(getBackground());
                gContext.fillRect(0, 0, width, height);
            } else {
                // clear component
                gContext.clearRect(0, 0, width, height);
            }

            // call rendering methood's
            if (isInitialized) {
                onPaint(gContext);
            }

            // draw back-buffer image on output buffer
            g.drawImage(volatileImage, 0, 0, width, height, this);
        }

    }


    /*Events listeners configuration methods*/
    protected void makeMenu(String name) {
        PopupMenu menu = new PopupMenu();
        menu.addActionListener(this);
        menus.put(name, menu);
    }
    
    protected PopupMenu getMenu(String menuName){
        return menus.get(menuName);
    }
    
    protected PopupMenu addMenu(String menuName, PopupMenu menu){
        return menus.putIfAbsent(menuName, menu);
    }
    
    protected MenuItem addMenuItem(String menuName, MenuItem item){
        PopupMenu menu;
        
        if (!menus.containsKey(menuName)) {
            // make parent menu
            menu = new PopupMenu();
            menu.addActionListener(this);
            menus.put(menuName, menu);
        } else {
            menu = menus.get(menuName);
        }
        
        return menu.add(item);
    }
    
    protected MenuItem addMenuItem(String menuName, String label, String actionComand) {

        PopupMenu menu;
        
        if (!menus.containsKey(menuName)) {
            // make parent menu
            menu = new PopupMenu();
            menu.addActionListener(this);
            menus.put(menuName, menu);
        } else {
            menu = menus.get(menuName);
        }
        
        MenuItem item = new MenuItem(label);
        item.setActionCommand(actionComand);
        
        return menu.add(item);
    }

    protected MenuItem addSubmenu(String menuName, String subMenuName, String label) {
        
        PopupMenu submenu;
        PopupMenu menu;
        
        if (!menus.containsKey(menuName)) {
            // make parent menu
            menu = new PopupMenu();
            menu.addActionListener(this);
            menus.put(menuName, menu);
        } else {
            menu = menus.get(menuName);
        }
        
        if (!menus.containsKey(subMenuName)) {
            // make sub-menu
            submenu = new PopupMenu(label);
            submenu.addActionListener(this);
            menus.put(subMenuName, submenu);
        } else {
            submenu = menus.get(subMenuName);
        }
        
        return menu.add(submenu);
    }
    
    protected void showMenu(String menuName, Point point) {
        if (menus.containsKey(menuName)) {
            menus.get(menuName).show(this, point.x, point.y);
        }
    }
    
    protected void enableMenu(String menuName) {
        if (menus.containsKey(menuName)) {
            this.add(menus.get(menuName));
        }
    }

    protected void disableMenu(String menuName) {
        if (menus.containsKey(menuName)) {
            this.remove(menus.get(menuName));
        }
    }
    
    /*Events listeners callbacks methoods*/
    @Override
    public void reshape(int x, int y, int width, int height) {

        boolean isResized = getWidth() != width || getHeight() != height;
        boolean isMoved = getX() != x | getY() != y;

        if (isResized) {
            // nullify render back-buffer to re-make a new 
            volatileImage = null;

            // update new dimensions
            this.width = width;
            this.height = height;

            // notify dimensions changes
            if (isInitialized) {
                onResized(width, height);
            }
        }

        if (isMoved) {
            // update new coordinates
            this.x = x;
            this.y = y;

            if (isInitialized) {
                onMoved(x, y);
            }
        }

        if (isResized | isMoved) {
            super.reshape(x, y, width, height);
        }
    }

    @Override
    public boolean imageUpdate(Image img, int infoFlags, int x, int y, int width, int height) {

        if ((infoFlags & ImageObserver.ALLBITS) != 0) {
            //System.out.println("Image full loaded");
            // image full loaded
            repaint();

            return true;
        } else if ((infoFlags & ImageObserver.SOMEBITS) != 0) {
            // image semi loaded
            repaint();

            return true;
        } else if ((infoFlags & ImageObserver.ERROR) != 0) {
            System.err.println("Can be load image " + img.hashCode());

        } else {
            repaint();

        }

        return true;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.onMouseClicked(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.onMouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.onMouseDragged(e, isDragEventStarted);
        isDragEventStarted = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragEventStarted = true;
        onMouseReleased(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // send event to dispatcher meethod
        this.onMenuItemSelected((MenuItem) e.getSource(), e.getActionCommand());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // send event to dispatcher meethod
        onTypedKey(e, e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // send event to dispatcher meethod
        onTypedKey(e, e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /*Events dispatcher methods*/
    public void onResized(int width, int height) {
        // default component redimension event dispatcher
    }

    public void onMoved(int x, int y) {
        // default component realocation event dispatcher
    }

    public void onMouseClicked(MouseEvent me) {
        // default mouse click event dispatcher
    }

    public void onMouseMoved(MouseEvent me) {
        // default mouse motion event dispatcher
    }

    public void onMouseDragged(MouseEvent me, boolean started) {
        // default mouse drag event dispatcher
    }

    public void onMouseReleased(MouseEvent me) {
        // default mouse release event dispatcher
    }

    public void onTypedKey(KeyEvent ke, char key) {
        // default key event dispatcher
    }

    public void onMenuItemSelected(MenuItem item, String command) {
        // default menu item selection event dispatcher
    }

    public void onPaint(Graphics2D g) {
        // default draw call event dispatcher
        super.paint(g);
    }

}
