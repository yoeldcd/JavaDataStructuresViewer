package com.yoeld99apps.axegrl.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerItem extends Item {

    public ContainerItem() {
        super();
    }

    public Component addItem(Component item) {

        if (item != null) {
            if (item instanceof Item) {
                ((Item) item).setParentItem(null);
            }

            super.add(item);
        }

        return item;
    }

    public Component getItem(int index) {
        Component item = getComponent(index);
        return item;
    }

    public Component removeItem(Component item) {
        super.remove(item);
        repaint();
        return item;
    }

    public void removeAll() {
        for(int i = 0; i < getComponentCount(); i++){
            super.remove(i);
        }
        repaint();
    }
    
}
