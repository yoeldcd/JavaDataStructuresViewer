package com.yoeld99apps.axegrl.displayer;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public interface DElement {
    
    public void enable();
    
    public void disable();
    
    public boolean isEnable();
    
    public void select();
    
    public void select(int index);
    
    public void dispose();
    
    public boolean isSelected();
    
    public int getSelectionIndex();
    
    public boolean isPointedBy(Point pointer);
    
    public boolean isInsideOf(Rectangle area);
    
    public default void update(){}
    
    public boolean isUpdated();
    
    public Shape getGeometry();
    
    @Override
    public String toString();
    
}