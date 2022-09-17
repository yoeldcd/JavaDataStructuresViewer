package com.yoeld99apps.toolsTDAs;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataListDisplayer<T extends Comparable> extends DrawableComponent {

    // rendered elements
    private ArrayList<T> paintedList;
    private ArrayList<Integer> proporsionList;

    // pointed elements
    private int pointedElement1;
    private int pointedElement2;
    private int pointedElement3;
    private String namePointer1;
    private String namePointer2;
    private String namePointer3;

    // rendered elements styles
    private FontMetrics textFontMetrics;
    private Font textFont;
    private Color backColor;
    private ElementStyle defaultStyle;
    private ElementStyle focussedStyle1;
    private ElementStyle focussedStyle2;
    private ElementStyle focussedStyle3;
    private ElementStyle pointedStyle1;
    private ElementStyle pointedStyle2;
    private ElementStyle pointedStyle3;

    //shape geometry values
    private int[] poligonXpoints;
    private int[] poligonYpoints;
    private int steepWidth;

    // iterations delay time at (ms)
    protected int iterationDelay;
    protected boolean showProporsionSize;

    public DataListDisplayer() {
        this(null);
    }

    public DataListDisplayer(ArrayList<T> dataList) {

        // define painted data structure
        this.paintedList = dataList;
        this.proporsionList = null;
        this.pointedElement1 = -1;
        this.pointedElement2 = -1;
        this.pointedElement3 = -1;

        // define pointers names
        this.namePointer1 = "i";
        this.namePointer2 = "j";
        this.namePointer3 = "k";

        // define initial background color
        this.textFont = new Font("rootFont", Font.BOLD, 25);
        this.backColor = Color.BLACK;
        this.defaultStyle = new ElementStyle(Color.BLUE, Color.YELLOW, Color.WHITE, ElementStyle.SHAPE_CIRCLE);
        this.focussedStyle1 = new ElementStyle(Color.YELLOW, Color.BLUE, Color.WHITE, ElementStyle.SHAPE_CIRCLE);
        this.focussedStyle2 = new ElementStyle(Color.RED, Color.WHITE, Color.WHITE, ElementStyle.SHAPE_CIRCLE);
        this.focussedStyle3 = new ElementStyle(Color.GREEN, Color.BLACK, Color.WHITE, ElementStyle.SHAPE_CIRCLE);
        this.pointedStyle1 = new ElementStyle(Color.YELLOW, Color.WHITE, Color.GRAY, ElementStyle.SHAPE_CIRCLE);
        this.pointedStyle2 = new ElementStyle(Color.RED, Color.WHITE, Color.GRAY, ElementStyle.SHAPE_CIRCLE);
        this.pointedStyle3 = new ElementStyle(Color.GREEN, Color.BLACK, Color.GRAY, ElementStyle.SHAPE_CIRCLE);
        
        // shape complex poligons points
        this.poligonXpoints = new int[32];
        this.poligonYpoints = new int[32];
        this.steepWidth = 10;

        // define default iterations delay ot (ms)
        this.iterationDelay = 24;

        // define default element display mode to (NO PROPORSION) 
        showProporsionSize = false;
    }

    public void setList(ArrayList<T> dataList) {
        this.paintedList = dataList;
    }

    public ArrayList<T> getList() {
        return this.paintedList;
    }

    public void setDelay(int iterationDelay) {
        // define draw interval to wait
        this.iterationDelay = iterationDelay > 0 ? iterationDelay : 10;

    }

    public int getDelay() {
        return this.iterationDelay;
    }

    public void setProporsions(ArrayList<Integer> proporsionList) {
        this.proporsionList = proporsionList;
    }

    public void paintList() {
        this.paintList(null);
    }

    public void paintList(ArrayList<T> list) {

        if (list != null) {
            this.paintedList = list;
        }

        super.paint(null);
        
        // pause render temporally
        if (this.iterationDelay > 10) {
            try {
                Thread.currentThread().sleep(this.iterationDelay);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataListDisplayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public void pointElement1(int indx, String pointerName, ElementStyle focussedStyle, ElementStyle pointerStyle) {

        this.pointedElement1 = indx;

        if (pointerName != null) {
            this.namePointer1 = pointerName;
        } else {
            this.namePointer1 = "i";
        }

        if (focussedStyle != null) {
            this.focussedStyle1 = focussedStyle;
        }

        if (pointerStyle != null) {
            this.pointedStyle1 = pointerStyle;
        }

    }

    public void pointElement2(int indx, String pointerName, ElementStyle focussedStyle, ElementStyle pointerStyle) {

        this.pointedElement2 = indx;

        if (pointerName != null) {
            this.namePointer2 = pointerName;
        } else {
            this.namePointer2 = "j";
        }

        if (focussedStyle != null) {
            this.focussedStyle2 = focussedStyle;
        }

        if (pointerStyle != null) {
            this.pointedStyle2 = pointerStyle;
        }

    }

    public void pointElement3(int indx, String pointerName, ElementStyle focussedStyle, ElementStyle pointerStyle) {

        this.pointedElement3 = indx;

        if (pointerName != null) {
            this.namePointer3 = pointerName;
        } else {
            this.namePointer3 = "k";
        }

        if (focussedStyle != null) {
            this.focussedStyle3 = focussedStyle;
        }

        if (pointerStyle != null) {
            this.pointedStyle3 = pointerStyle;
        }

    }
    
    @Override
    protected void renderize(Graphics g, int width, int height) {

        int numElem;
        int minValue = 0;
        int maxValue = 0;

        int elemWidth;
        int elemHeight;

        int x, y;

        // clear dispaly area
        g.setColor(this.backColor);
        g.fillRect(0, 0, width, height);

        if (this.paintedList != null) {
            numElem = this.paintedList.size();

            if (numElem > 0) {

                if (this.showProporsionSize && this.proporsionList != null) {
                    maxValue = this.proporsionList.get(0);
                    minValue = maxValue;

                    // get max and min value on proporsion list
                    for (Integer a : this.proporsionList) {

                        if (a > maxValue) {
                            maxValue = a;
                        }

                        if (a < minValue) {
                            minValue = a;
                        }

                    }

                }

                // compute elements dimensions
                elemWidth = (width - (numElem + 1) * this.steepWidth) / numElem;
                elemHeight = height;

                // normalize to max avaliable space
                if (elemWidth >= height / 4) {
                    elemWidth = height / 4;
                }

                // configure text parameters
                this.textFont = new Font("rootFont", Font.BOLD, elemWidth / 2);
                g.setFont(this.textFont);
                this.textFontMetrics = g.getFontMetrics(this.textFont);

                // start on corner
                x = width / 2 - numElem * (this.steepWidth + elemWidth) / 2;
                y = 0;

                // renderize any list elements
                for (int i = 0; i < numElem; i++) {

                    // renderize using mode
                    if (this.showProporsionSize && maxValue != minValue) {
                        this.drawProporcionedElementShape(g, x, y, elemWidth, elemHeight, i, minValue, maxValue);
                    } else {
                        this.drawUnproporcionedElementShape(g, x, y, elemWidth, elemHeight, i);
                    }

                    x += elemWidth + this.steepWidth;
                }

            }
        }

    }

    private void drawUnproporcionedElementShape(Graphics g, int x, int y, int shapeWidth, int shapeHeight, int idx) {

        String value;
        int textWidth, textHeight;
        y += shapeHeight / 5;
        
        int cx = x + shapeWidth / 2;
        int cy = y + shapeWidth / 2;

        boolean pointed = true;
        String pointerName = null;

        ElementStyle elementStyle;
        ElementStyle pointerStyle;

        // select rendered element style
        if (idx == this.pointedElement1) {
            elementStyle = this.focussedStyle1;
            pointerStyle = this.pointedStyle1;
            pointerName = this.namePointer1;

        } else if (idx == this.pointedElement2) {
            elementStyle = this.focussedStyle2;
            pointerStyle = this.pointedStyle2;
            pointerName = this.namePointer2;

        } else if (idx == this.pointedElement3) {
            elementStyle = this.focussedStyle3;
            pointerStyle = this.pointedStyle3;
            pointerName = this.namePointer3;

        } else {
            elementStyle = this.defaultStyle;
            pointerStyle = null;
            pointed = false;
        }
        
        // compute text dimmensions
        value = ""+idx;
        textWidth = this.textFontMetrics.stringWidth(value);
        textHeight = this.textFontMetrics.getHeight();

        // draw index text aligned to center
        g.setColor(defaultStyle.textColor);
        g.drawString(value, cx - textWidth / 2, y - textHeight / 2);
        
        // select and draw defined element shape
        switch (elementStyle.shapeType) {
            case ElementStyle.SHAPE_QUAD:
                g.setColor(elementStyle.shapeColor);
                g.fillRect(x, y, shapeWidth, shapeWidth);
                g.setColor(elementStyle.borderColor);
                g.drawRect(x, y, shapeWidth, shapeWidth);

                break;

            case ElementStyle.SHAPE_CIRCLE:
                g.setColor(elementStyle.shapeColor);
                g.fillArc(x, y, shapeWidth, shapeWidth, 0, 360);
                g.setColor(elementStyle.borderColor);
                g.drawArc(x, y, shapeWidth, shapeWidth, 0, 360);

                break;

            case ElementStyle.SHAPE_DYAMOND:

                // configure poligons points coords
                this.poligonXpoints[0] = cx;
                this.poligonYpoints[0] = y;

                this.poligonXpoints[1] = x + shapeWidth;
                this.poligonYpoints[1] = cy;

                this.poligonXpoints[2] = cx;
                this.poligonYpoints[2] = y + shapeWidth;

                this.poligonXpoints[3] = x;
                this.poligonYpoints[3] = cy;

                g.setColor(elementStyle.shapeColor);
                g.fillPolygon(this.poligonXpoints, this.poligonYpoints, 4);
                g.setColor(elementStyle.borderColor);
                g.drawPolyline(this.poligonXpoints, this.poligonYpoints, 4);

                break;

        }
        
        // compute text dimmensions
        value = this.paintedList.get(idx).toString();
        textWidth = this.textFontMetrics.stringWidth(value);
        textHeight = this.textFontMetrics.getHeight();

        // draw value text aligned to center
        g.setColor(elementStyle.textColor);
        g.drawString(value, cx - textWidth / 2, cy + textHeight / 3);

        // draw pointer
        if (pointed) {
            y += shapeWidth + shapeWidth / 4;

            cy = y + shapeWidth / 2;

            this.poligonXpoints[0] = cx;
            this.poligonYpoints[0] = y;

            this.poligonXpoints[1] = x + shapeWidth;
            this.poligonYpoints[1] = cy;

            this.poligonXpoints[2] = cx + shapeWidth / 4;
            this.poligonYpoints[2] = cy;

            this.poligonXpoints[3] = cx + shapeWidth / 4;
            this.poligonYpoints[3] = y + shapeWidth;

            this.poligonXpoints[4] = cx - shapeWidth / 4;
            this.poligonYpoints[4] = y + shapeWidth;

            this.poligonXpoints[5] = cx - shapeWidth / 4;
            this.poligonYpoints[5] = cy;

            this.poligonXpoints[6] = x;
            this.poligonYpoints[6] = cy;

            // draw pointer arrow
            g.setColor(pointerStyle.shapeColor);
            g.fillPolygon(this.poligonXpoints, this.poligonYpoints, 7);
            g.setColor(pointerStyle.borderColor);
            g.drawPolyline(this.poligonXpoints, this.poligonYpoints, 7);

            cy += shapeWidth;

            textWidth = this.textFontMetrics.stringWidth(pointerName);
            textHeight = this.textFontMetrics.getHeight();

            // draw value text aligned to center
            g.setColor(pointerStyle.textColor);
            g.drawString(pointerName, cx - textWidth / 2, cy + textHeight / 4);

        }

    }

    private void drawProporcionedElementShape(Graphics g, int x, int y, int shapeWidth, int shapeHeight, int idx, int vmin, int vmax) {

        String value;
        int textWidth;
        int proporcion;

        ElementStyle elementStyle;

        // select rendered element style
        if (idx == this.pointedElement1) {
            elementStyle = this.focussedStyle1;

        } else if (idx == this.pointedElement2) {
            elementStyle = this.focussedStyle2;

        } else if (idx == this.pointedElement3) {
            elementStyle = this.focussedStyle3;

        } else {
            elementStyle = this.defaultStyle;
        }

        // compute text dimmens and get proporsion
        value = this.paintedList.get(idx).toString();
        textWidth = this.textFontMetrics.stringWidth(value);
        proporcion = this.proporsionList.get(idx);

        double py = ((proporcion + vmin) / (vmax - vmin));
        y += py * shapeHeight;

        if (proporcion > 0) {
            // draw text on grafic context
            g.setColor(elementStyle.textColor);
            g.drawString(value, x + shapeWidth / 2 - textWidth / 2, y + shapeHeight / 2 + this.textFontMetrics.getHeight() / 2);

            shapeHeight = (int) ((1 - py) * shapeHeight);

        } else {
            // draw text on grafic context
            g.setColor(elementStyle.textColor);
            g.drawString(value, x + shapeWidth / 2 - textWidth / 2, y + shapeHeight / 2 - this.textFontMetrics.getHeight() / 2);
        }

    }

}
