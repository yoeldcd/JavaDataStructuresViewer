package com.yoeld99apps.axegrl.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class ShapePainter extends ObjectPainter implements ImageObserver {

    // shape geometrys
    private final Rectangle imageBounds;
    private final Point textPosition;
    
    private boolean isPrepared;

    private Shape geometry;
    private String text;
    private Style style;
    
    private AffineTransform transform;
    
    public ShapePainter() {
        super();

        this.text = null;
        this.geometry = new Rectangle();
        this.style = Style.DEFAULT;

        this.imageBounds = new Rectangle();
        this.textPosition = new Point();
        this.transform = new AffineTransform();

        this.isPrepared = false;
    }

    @Override
    public Shape getGeometry() {
        return geometry;
    }

    public void rotate(double angle) {
        transform.setToRotation(angle / 180.0 * Math.PI);
    }

    public void reset() {
        transform.setToRotation(0);
    }

    public void setStyle(Style style) {
        if (style != null) {
            this.style = style;
            isPrepared = false;
        }
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public void prepare(Graphics2D g, String text) {
        
        if(component == null || g == null){
            this.isPrepared = false;
            return;
        }
        
        Rectangle bounds = component.getBounds();
        
        // compute auxiliar values (center, normalized dimensions)
        int width = bounds.width;
        int height = bounds.height;
        int cx = (int) bounds.getCenterX();
        int cy = (int) bounds.getCenterY();

        // get value text dimmens
        int textWidth = 0;
        int textHeight = 0;
        int textDown = 0;
        int textUp = 0;

        // define render elements
        this.text = text;

        // configure text values
        if (text != null) {
            textWidth = g.getFontMetrics(style.textFont).stringWidth(text);
            textHeight = g.getFontMetrics().getHeight();
            textDown = g.getFontMetrics().getDescent();
            textUp = g.getFontMetrics().getAscent();
        }

        // compute text position
        switch (style.textAlignement) {
            case Style.TEXT_UP:
                textPosition.setLocation(cx - textWidth / 2, bounds.y + textUp);
                
                height -= textHeight * 1.2;
                cy += textHeight * 0.6;
                break;
                
            case Style.TEXT_DOWN:
                textPosition.setLocation(cx - textWidth / 2, bounds.getMaxY() - textDown);
                
                height -= textHeight * 1.2;
                cy -= textHeight * 0.6;    
                break;
                
            case Style.TEXT_OVER:
                textPosition.setLocation(cx - textWidth / 2, bounds.y - textDown * 1.2);
                break;
                
            case Style.TEXT_LOWER:
                textPosition.setLocation(cx - textWidth / 2, bounds.getMaxY() + textUp * 1.2);    
                break;
            
            default:
                textPosition.setLocation(cx - textWidth / 2, cy + textDown);
                
        }

        // select geometry minor ratio
        int minor = width > height ? height : width;
        int minorDiv2 = minor / 2;
        
        // compute image conatiner geometry
        if (style.image != null) {

            if (style.loockImageAspect) {
                // compute normalized dimensions
                computeNormalizedImageShape(style.image, minor, minor, imageBounds);
                imageBounds.setLocation(cx - imageBounds.width / 2, cy - imageBounds.height / 2);

            } else {
                // use complete shape bounds dimensions
                imageBounds.setBounds(bounds);
            }

        }

        // compute element shape geometry
        switch (style.geometryType) {
            case Style.GEOMETRY_QUAD:
                geometry = new Rectangle2D.Double(cx - minorDiv2, cy - minorDiv2, minor, minor);
                break;

            case Style.GEOMETRY_TEXTBOX:
                geometry = new Rectangle2D.Double(cx - textWidth / 2 - 5, cy - textHeight + 5, textWidth + 10, textHeight + 10);

                break;

            case Style.GEOMETRY_CIRCLE:
                geometry = new Arc2D.Double(cx - minorDiv2, cy - minorDiv2, minor, minor, 0, 360, Arc2D.CHORD);

                break;

            case Style.GEOMETRY_DYAMOND:

                geometry = new Polygon();

                // add poligon points
                ((Polygon) geometry).addPoint(cx, cy - minorDiv2);
                ((Polygon) geometry).addPoint(cx + minorDiv2, cy);
                ((Polygon) geometry).addPoint(cx, cy + minorDiv2);
                ((Polygon) geometry).addPoint(cx - minorDiv2, cy);

                break;

            case Style.GEOMETRY_IMAGE:
                if (style.image != null) {
                    geometry = imageBounds;
                }
                break;

            case Style.GEOMETRY_BOX:
                geometry = bounds;
                break;

            case Style.GEOMETRY_RECTANGLE:
            default:
                geometry = new Rectangle2D.Double(cx - width / 2, cy - height / 2, width, height);
                
        }
        
        geometry.contains(cy, cy, cy, cy);
        
        this.isPrepared = true;
    }

    @Override
    public boolean isPrepared() {
        return isPrepared & this.component != null;
    }
    
    @Override
    public void paint(Graphics2D g) {
        
        if(!isPrepared()){
            return;
        }
        
        /**@deprecated:
        g.setColor(Color.BLACK);
        g.draw(component.getBounds());
        /**/
        
        // transform and clip grafic space
        AffineTransform savedTransform = g.getTransform();
        Stroke stroke = style.getStroke();
        g.setTransform(transform);
        g.clip(geometry);
        
        if (style.image != null) {
            
            // draw image
            g.drawImage(style.image,
                    imageBounds.x,
                    imageBounds.y,
                    imageBounds.width,
                    imageBounds.height,
                    component);

        } else if(style.shapeColor != null){
                // draw shape area
                g.setColor(style.shapeColor);
                g.fill(geometry);
        } else{
            
        }

        // restore grafic psace
        g.setClip(null);

        // draw shape border
        if(style.borderColor != null){
            g.setStroke(stroke);
            g.setColor(style.borderColor);
            g.draw(stroke.createStrokedShape(geometry));
        }
        
        // restore transform
        g.setTransform(savedTransform);

        // draw shape text
        if (text != null && style.textColor != null) {
            Font savedFont = g.getFont();
            
            g.setFont(style.textFont);
            g.setColor(style.textColor);
            g.drawString(text, textPosition.x, textPosition.y);
            
            g.setFont(savedFont);
        }

        
        /**/
    }

    @Override
    public boolean imageUpdate(Image img, int infoFlags, int x, int y, int width, int height) {

        if (this.component != null) {
            if ((infoFlags & ImageObserver.ALLBITS) != 0) {
                System.out.println("Image full loaded");
                // image full loaded
                component.repaint();

                return true;
            } else if ((infoFlags & ImageObserver.SOMEBITS) != 0) {
                // image semi loaded
                component.repaint();

                return true;
            } else if ((infoFlags & ImageObserver.ERROR) != 0) {
                System.err.println("Can be load ShapeImage on shape with ID ");

            } else {
                component.repaint();

            }
        }

        return true;
    }

    public static Rectangle computeNormalizedImageShape(Image image, int width, int height, Rectangle rectangle) {

        if (image == null) {
            return null;
        }

        if (rectangle == null) {
            rectangle = new Rectangle();
        }

        // compute image and frame aspect relation 
        double ratios = (double) width / (double) height;
        double ratioi = (double) image.getWidth(null) / (double) image.getHeight(null);
        double scale = 0.99;

        // compute normalized IN X shape dimensions
        int sizeW = (int) (width / ratios * ratioi);
        int sizeH = (int) height;

        if (sizeW >= width) {

            // compute normalized IN Y shape dimensions
            sizeW = width;
            sizeH = (int) (height * ratios / ratioi);

            if (sizeH >= height) {
                // compute reduction scale
                scale = 1.0 - ((double) sizeH - height) / height;

                // normalize scale bethween 1.0 and 0.1
                if (scale < 0.1) {
                    scale = 0.1;
                }

            }

        }

        // compute final scaled shape dimensions
        sizeW *= scale;
        sizeH *= scale;

        // set bounds around {0;0} center point x
        rectangle.setBounds(
                -sizeW / 2, // corner point x
                -sizeH / 2, // corner point y
                sizeW, // drawable width
                sizeH // drawable height
        );

        return rectangle;
    }
    
    public static class Style extends ObjectPainter.Style<Style> {

        // avaliables shape representation
        public static final int GEOMETRY_QUAD = 0x6267100;
        public static final int GEOMETRY_RECTANGLE = 0x6267101;
        public static final int GEOMETRY_CIRCLE = 0x6267102;
        public static final int GEOMETRY_DYAMOND = 0x6267103;
        public static final int GEOMETRY_IMAGE = 0x6267104;
        public static final int GEOMETRY_BOX = 0x6267105;
        public static final int GEOMETRY_CUSTOM = 0x6267106;
        public static final int GEOMETRY_TEXTBOX = 0x6267107;
        public static final int GEOMETRY_FREE = 0x6267108;
        
        // avaliables text disposition
        public static final int TEXT_FREE = 0x6267200;
        public static final int TEXT_OVER = 0x6267201;
        public static final int TEXT_UP = 0x6267202;
        public static final int TEXT_CENTER = 0x6267203;
        public static final int TEXT_DOWN = 0x6267204;
        public static final int TEXT_LOWER = 0x6267205;
        
        // default style configuration values
        public static final Color DEFAULT_SHAPE_COLOR = Color.WHITE;
        public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
        public static final Color DEFAULT_BORDER_COLOR = Color.GRAY;
        public static final int DEFAULT_GEOMETRY_TYPE = GEOMETRY_BOX;
        public static final int DEFAULT_TEXT_ALIGNEMENT = TEXT_FREE;
        public static final int DEFAULT_BORDER_SIZE = 2;
        
        private static final Style DEFAULT = new Style();

        // draw colors and icons
        Stroke stroke;
        Color shapeColor;
        Color textColor;
        Color borderColor;
        Image image;
        Font textFont;
        Shape customShape;
        String name;
        String imagePath;
        
        // configuration
        int geometryType;
        int textAlignement;
        double borderSize;
        boolean loockImageAspect;

        private boolean hasStrokeModified;

        public Style() {
            this(GEOMETRY_BOX, DEFAULT_SHAPE_COLOR, DEFAULT_TEXT_COLOR, DEFAULT_BORDER_COLOR, DEFAULT_TEXT_ALIGNEMENT);
        }

        public Style(int geometryType) {
            this(geometryType, DEFAULT_SHAPE_COLOR, DEFAULT_TEXT_COLOR, DEFAULT_BORDER_COLOR, DEFAULT_GEOMETRY_TYPE);
        }
        
        public Style(int geometryType, Color shapeColor, Color textColor, Color borderColor, int textAlignement) {
            super();

            this.geometryType = geometryType;
            this.textAlignement = textAlignement;
            this.loockImageAspect = true;
            this.borderSize = DEFAULT_BORDER_SIZE;

            this.textColor = textColor;
            this.shapeColor = shapeColor;
            this.borderColor = borderColor;
            this.image = null;

            this.stroke = new BasicStroke((float) this.borderSize);
            this.textFont = new Font(Font.MONOSPACED, Font.BOLD, 18);
            this.hasStrokeModified = false;
            
            this.name = "default";
            this.imagePath = null;
        }
        
        public Style copyStyleRulesTo(Style other){
            
            other.shapeColor = shapeColor;
            other.textColor = textColor;
            other.borderColor = borderColor;
            other.image = image;
            other.textFont = textFont;
            other.customShape = customShape;
            other.stroke = stroke;
            
            other.geometryType = geometryType;
            other.textAlignement = textAlignement;
            other.borderSize = borderSize;
            other.loockImageAspect = loockImageAspect;
            
            other.textFont = textFont.deriveFont(textFont.getSize());
            other.name = name;
            
            return other;
        }
        
        @Override
        public Style duplicate(){
            Style clon = new Style();
            copyStyleRulesTo(clon);
            
            return clon;
        }
        
        public String getName(){
            return this.name;
        }
        
        public void setName(String name){
            this.name = name;
        }
        
        public int getGeometryType() {
            return geometryType;
        }

        public void setGeometryType(int geometryType) {
            this.geometryType = geometryType;
        }

        public Color getShapeColor() {
            return shapeColor;
        }

        public void setShapeColor(Color shapeColor) {
            this.shapeColor = shapeColor;
        }

        public Color getTextColor() {
            return textColor;
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }
        
        public Font getTextFont(){
            return this.textFont;
        }
        
        public void setTextFont(Font textFont){
            this.textFont = textFont;
        }
        
        public void setTextFontSize(float size){
            this.textFont = this.textFont.deriveFont(size);
        }
        
        public Color getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
        }

        public static Image loadImageOnPath(String imagePath) {
            return Toolkit.getDefaultToolkit().getImage(imagePath);
        }
        
        public Image getImage() {
            return image;
        }

        public String getImagePath(){
            return this.imagePath;
        }
        
        public void setImageOnPath(String imagePath){
            this.image = Toolkit.getDefaultToolkit().getImage(imagePath);
            this.imagePath = imagePath;
        }
        
        public void setImage(Image shapeImage) {
            this.image = shapeImage;
            this.imagePath = null;
            
            if(shapeImage != null && textAlignement == TEXT_CENTER){
                textAlignement = TEXT_DOWN;
                System.out.println("NOTICE: Text moved down");
            }
            
        }

        public int getTextAlignement() {
            return textAlignement;
        }

        public void setTextAlignement(int alignement) {
            this.textAlignement = alignement;
        }

        public double getBorderSize() {
            return borderSize;
        }

        public void setBorderSize(double borderSize) {
            this.borderSize = borderSize > 0 ? borderSize : DEFAULT_BORDER_SIZE;
        }

        public boolean isLoockImageAspect() {
            return loockImageAspect;
        }

        public void setLoockImageAspect(boolean loockImageAspect) {
            this.loockImageAspect = loockImageAspect;
        }

        public Shape getCustomShape() {
            return customShape;
        }

        public void setCustomShape(Shape customShape) {
            this.customShape = customShape;
        }

        @Override
        public Stroke getStroke() {
            if (hasStrokeModified) {
                hasStrokeModified = false;
                this.stroke = new BasicStroke((float) borderSize);

            }

            return this.stroke;
        }

    }
    
}
