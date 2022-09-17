package com.yoeld99apps.axegrl.displayer;

import com.yoeld99apps.axegrl.components.Item;
import com.yoeld99apps.axegrl.painters.ConectorPainter;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public class DConector extends Item implements DElement {

    private final ConectorPainter painter;
    private String text;
    private int selectionIndex;

    private boolean isBiconected;
    private boolean isSelected;
    private boolean isEnable;
    
    private ConectorPainter.Style disableStyle;
    private ConectorPainter.Style enableStyle;
    private ConectorPainter.Style selectedStyle;
    
    private DShape shapeA;
    private DShape shapeB;

    public DConector(DShape itemA, DShape itemB, String text) {

        this.painter = new ConectorPainter();
        this.painter.setComponent(this);
        this.text = text;

        this.disableStyle = new ConectorPainter.Style();
        this.enableStyle = new ConectorPainter.Style();
        this.selectedStyle = new ConectorPainter.Style();
        this.painter.setStyle(disableStyle);
        
        this.isBiconected = false;
        this.isSelected = false;
        this.selectionIndex = -1;
        
        if(itemA != null && itemB != null){
            this.shapeA = itemA;
            painter.setGeometryA(itemA.getGeometry());
            
            this.shapeB = itemB;
            painter.setGeometryB(itemB.getGeometry());
            
            initialize();
        }
        
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        if (this.text != null ? this.text.compareTo(text) > 0 : text != null) {
            this.text = text;
            repaint();
        }
    }
    
    @Override
    public String toString(){
        return isSelected ? text + "[" + selectionIndex + "] "+(isEnable? "E" : "D") : text;
    }
    
    public DShape getShapeA() {
        return shapeA;
    }

    public DShape getShapeB() {
        return shapeB;
    }
    
    public ConectorPainter.Style getDisableStyle() {
        return disableStyle;
    }

    public ConectorPainter.Style setDisableStyle(ConectorPainter.Style style) {
        if (style != null) {
            this.disableStyle = style;

            if (!isSelected && !isEnable) {
                painter.setStyle(style);
                repaint();
            }
        }
        
        return this.disableStyle;
    }

    public ConectorPainter.Style getEnableStyle() {
        return enableStyle;
    }

    public ConectorPainter.Style setEnableStyle(ConectorPainter.Style style) {
        if (style != null) {
            this.enableStyle = style;

            if (isEnable & !isSelected) {
                // repaint un-selected shape with new disableStyle
                painter.setStyle(style);
                repaint();
            }
        }
        
        return this.enableStyle;
    }
    
    public ConectorPainter.Style getSelectedStyle() {
        return selectedStyle;
    }

    public ConectorPainter.Style setSelectedStyle(ConectorPainter.Style style) {
        if (style != null) {
            this.selectedStyle = style;

            if (isSelected) {
                // repaint selected shape with new disableStyle
                painter.setStyle(style);
                repaint();
            }
        }
        
        return this.selectedStyle;
    }
    
    public void biconect(){
        isBiconected = true;
        painter.setBiconected(true);
        repaint();
    }
    
    public void unbiconect(){
        isBiconected = false;
        painter.setBiconected(false);
        repaint();
    }
    
    public boolean isBiconected(){
        return this.isBiconected;
    }
    
    public void invert(){
        DShape t = shapeA;
        
        if(shapeA != null && shapeB != null){
            shapeA = shapeB;
            painter.setGeometryA(shapeB.getGeometry());
            
            shapeB = t;
            painter.setGeometryB(t.getGeometry());
            
            repaint();
        }
    }
    
    @Override
    public boolean isPointedBy(Point point) {
        return painter.getGeometry().intersects(new Rectangle(point.x-5, point.y-5, 10, 10));
    }

    @Override
    public boolean isInsideOf(Rectangle area) {
        return painter.getGeometry().intersects(area);
    }

    @Override
    public void enable() {
        if (!isEnable) {
            isEnable = true;

            if (!isSelected) {
                painter.setStyle(enableStyle);
            }

            repaint();
        }
    }

    @Override
    public void disable() {
        if (isEnable) {
            isEnable = false;

            if (!isSelected) {
                painter.setStyle(disableStyle);
            }

            repaint();
        }
    }

    @Override
    public boolean isEnable() {
        return isEnable;
    }

    @Override
    public void select() {
        select(0);
    }

    @Override
    public void select(int selectionIndex) {
        if (!isSelected) {
            isSelected = true;
            selectionIndex = selectionIndex;

            painter.setStyle(selectedStyle);
            repaint();
        }
    }

    @Override
    public void dispose() {
        if (isSelected) {
            isSelected = false;
            isEnable = false;
            selectionIndex = -1;

            painter.setStyle(disableStyle);
            repaint();
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public int getSelectionIndex() {
        return selectionIndex;
    }

    @Override
    public boolean isUpdated() {
        return shapeA.isUpdated || shapeB.isUpdated;
    }

    @Override
    public boolean isExportable() {
        return true;
    }

    @Override
    public Shape getGeometry() {
        return painter.getGeometry();
    }

    @Override
    public void onPaint(Graphics2D g) {
        
        if (shapeA.isUpdated() || shapeB.isUpdated()) {

            // update shape geometries
            painter.setGeometryA(shapeA.getGeometry());
            painter.setGeometryB(shapeB.getGeometry());

            // prepare link painter
            painter.prepare(g, text);

        } else if (painter.isPrepared()) {
            // prepare link painter
            painter.prepare(g, text);
        }
        
        //System.out.println("Painting Link between "+shapeA.getSource()+" AND "+shapeB.getSource());
        
        painter.paint(g);
    }

}
