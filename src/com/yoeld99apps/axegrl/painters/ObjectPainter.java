package com.yoeld99apps.axegrl.painters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

public abstract class ObjectPainter {

    protected Component component;

    public ObjectPainter() {
        this.component = null;
    }
    
    public void setComponent(Component component) {
        if (component != null) {
            this.component = component;
        }
    }
    
    public abstract Shape getGeometry();

    public abstract boolean isPrepared();

    public abstract void prepare(Graphics2D g, String text);

    public abstract void paint(Graphics2D g);

    public static abstract class Style<T extends Style> {
    
        Style(){
            
        }
        
        public abstract T copyStyleRulesTo(T other);
        
        public abstract Style duplicate();
        
        public abstract Stroke getStroke();
        
    }
    
    public class NoComponentPainterException extends Exception{

        public NoComponentPainterException() {
            super("Not provided component to ObjectPainter");
        }
        
    } 
}
