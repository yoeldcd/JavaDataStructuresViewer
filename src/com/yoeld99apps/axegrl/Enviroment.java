package com.yoeld99apps.axegrl;

import com.yoeld99apps.axegrl.utils.ImageDisplayer;
import com.yoeld99apps.axegrl.components.Item;
import com.yoeld99apps.axegrl.components.ItemFrame;

import com.yoeld99apps.axegrl.displayer.Displayer;
import com.yoeld99apps.axegrl.utils.ShapeDisplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Enviroment extends JFrame {

    private final Toolkit tools;
    private final Container container;
    private final JDesktopPane internals;

    private final MenuBar menubar;
    private final Menu toolsmenu;
    private final Menu helpmenu;

    // create list of makeable-items-types
    private static final ArrayList<Class> TYPED_ITEMS_CLASS = new ArrayList();
    private static final ArrayList<String> TYPED_ITEMS_NAMES = new ArrayList();

    private ActionListener newItemActionlistener;

    public Enviroment() {
        tools = getToolkit();
        int maxW = tools.getScreenSize().width;
        int maxH = tools.getScreenSize().height;

        // configure enviroment main-window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("AXEGRL Envirment v1.0");
        setSize(maxW, maxH);
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);

        // create main-window-menubar 
        menubar = new MenuBar();
        setMenuBar(menubar);

        // create menus
        toolsmenu = new Menu("tools");
        toolsmenu.add(createNewItemMenu());

        helpmenu = new Menu("help");
        menubar.add(toolsmenu);
        menubar.add(helpmenu);

        // create main-window-container
        container = new Container();
        container.setBounds(5, 5, maxW - 10, maxH - 50);
        getContentPane().add(container);

        // create sub-frames-container
        internals = new JDesktopPane();
        internals.getGraphicsConfiguration();
        internals.setSize(maxW - 10, maxH - 60);
        internals.setBackground(Color.BLACK);
        container.add(internals);

    }

    public ItemFrame addItemFrame(ItemFrame itemFrame) {

        // compute enviroment screen center
        int cx = (getWidth() - itemFrame.getWidth()) / 2;
        int cy = (getHeight() - itemFrame.getHeight()) / 2;

        // add item frame
        internals.add(itemFrame);
        itemFrame.setLocation(cx, cy);
        itemFrame.setVisible(true);

        return itemFrame;
    }
    
    public static String[] getMakeablesTypesNames(){
        return TYPED_ITEMS_NAMES.toArray(new String[TYPED_ITEMS_NAMES.size()]);
    }
    
    public static void addMakeableItem(Class typeClass, String typeName) {
        TYPED_ITEMS_CLASS.add(typeClass);
        TYPED_ITEMS_NAMES.add(typeName);
    }

    private MenuItem createNewItemMenu() {
        Menu submenu = new Menu("Add");
        MenuItem subitem;

        newItemActionlistener = (e) -> {
            createTypedFrame(e.getActionCommand());
        };
        
        // add each sub-menu-items option
        for (int i = 0; i < TYPED_ITEMS_CLASS.size(); i++) {
            // set type command and label
            subitem = new MenuItem("NEW " + TYPED_ITEMS_NAMES.get(i));
            subitem.setActionCommand(TYPED_ITEMS_NAMES.get(i));

            // define eventlistener
            subitem.addActionListener(newItemActionlistener);
            submenu.add(subitem);
        }

        return submenu;
    }

    private ItemFrame getItem(int index) {
        if (internals.getComponent(index) instanceof ItemFrame) {
            return (ItemFrame) internals.getComponent(index);
        }

        return null;
    }

    public ItemFrame createTypedFrame(String typeName) {
        
        int typeIndex = TYPED_ITEMS_NAMES.indexOf(typeName);
        Item item;
        
        if (typeIndex != -1) {

            //make and add new item-frame instance
            try {
                item = (Item) TYPED_ITEMS_CLASS.get(typeIndex).newInstance();
                item.setVisible(true);
                
                return addItemFrame(ItemFrame.makeFrom(item));
            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("Invalid class contruction");
            }

        }

        return null;
    }

    public static void main(String[] args) {
        //registre makeable types
        Enviroment.addMakeableItem(ShapeDisplayer.class, "SHAPE");
        Enviroment.addMakeableItem(ImageDisplayer.class, "IMAGE");
        Enviroment.addMakeableItem(Displayer.class, "GRAPH");
        
        Enviroment e = new Enviroment();
        
        //Graph item1 = new Displayer();
        //Graph item2 = new Displayer();
        //Graph item3 = new Displayer();
        
        //ItemFrame f1 = ItemFrame.makeFrom(item1);
        //f1.setSize(700, 500);
        //e.addItemFrame(f1);
        
        //item2.setSize(500, 300);
        //item1.addItem(item2);
        
        //ImageDisplayer displayer = new ImageDisplayer();
        //displayer.setSize(300, 300);
        //item1.addItem(displayer);
        
        //item3.setSize(200, 200);
        //item2.addItem(item3);
        
    }

}
