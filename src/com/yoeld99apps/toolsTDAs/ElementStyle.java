package com.yoeld99apps.toolsTDAs;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

public class ElementStyle {

    public static final int SHAPE_QUAD = 0x6267100;
    public static final int SHAPE_RECTANGLE = 0x6267101;
    public static final int SHAPE_CIRCLE = 0x6267102;
    public static final int SHAPE_DYAMOND = 0x6267103;
    public static final int SHAPE_IMAGE = 0x6267103;
    
    public static final int TEXT_CENTER = 0x6267200;
    public static final int TEXT_UP = 0x6267201;
    public static final int TEXT_DOWN = 0x6267202;
    
    public static final ElementStyle DEFAULT = new ElementStyle();
    
    public Color shapeColor;
    public Color textColor;
    public Color borderColor;
    public Image shapeImage;
    
    public int shapeType;
    public int textPosition;
    
    public ElementStyle() {
        this.shapeColor = Color.WHITE;
        this.textColor = Color.BLACK;
        this.borderColor = Color.BLACK;
        this.shapeType = SHAPE_QUAD;
        this.textPosition = TEXT_CENTER;
        this.shapeImage = null;
    }
    
    public ElementStyle(Color shapeColor, Color textColor, Color borderColor, int shapeType) {
        this.textColor = textColor != null ? textColor : Color.BLACK;
        this.shapeColor = shapeColor != null ? shapeColor : Color.WHITE;
        this.borderColor = borderColor != null ? borderColor : Color.BLACK;
        this.shapeType = shapeType;
        this.textPosition = TEXT_CENTER;
        this.shapeImage = null;
    }
    
    public static Image loadImage(String imagePath){
        return Toolkit.getDefaultToolkit().getImage(imagePath);        
    }
    
}