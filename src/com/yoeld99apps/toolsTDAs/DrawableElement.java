package com.yoeld99apps.toolsTDAs;

import java.awt.*;

public class DrawableElement {

    private static final int[] poligonXpoints = new int[10];
    private static final int[] poligonYpoints = new int[10];

    // draw one generic text element
    protected static void drawTextElement(Graphics g, int x, int y, int shapeWidth, int shapeHeight, String text, ElementStyle elementStyle) {

        int cx = x + shapeWidth / 2;
        int cy = y + shapeHeight / 2;
        int radius = shapeWidth > shapeHeight ? shapeHeight : shapeWidth;

        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getHeight();

        if (elementStyle.shapeImage != null) {

            // draw representative shape image
            g.drawImage(elementStyle.shapeImage, cx - radius / 2, y, radius, shapeHeight - textHeight, null);
            
            if(elementStyle.shapeType == ElementStyle.SHAPE_IMAGE){
                g.setColor(elementStyle.borderColor);
                g.drawRect(cx - radius / 2, y, radius, shapeHeight - textHeight);
            }
            
            // draw index text aligned to center at down
            g.setColor(elementStyle.textColor);
            g.drawString(text, cx - textWidth / 2, y + shapeHeight - textHeight / 4);

        } else {

            // select element draw shape
            switch (elementStyle.shapeType) {
                case ElementStyle.SHAPE_QUAD:
                    g.setColor(elementStyle.shapeColor);
                    g.fillRect(cx - radius / 2, cy - radius / 2, radius, radius);
                    g.setColor(elementStyle.borderColor);
                    g.drawRect(cx - radius / 2, cy - radius / 2, radius, radius);

                    break;

                case ElementStyle.SHAPE_RECTANGLE:
                    g.setColor(elementStyle.shapeColor);
                    g.fillRect(cx - textWidth / 2, cy - textHeight, textWidth + 10, textHeight + 10);
                    g.setColor(elementStyle.borderColor);
                    g.drawRect(cx - textWidth / 2, cy - textHeight, textWidth + 10, textHeight + 10);

                    break;

                case ElementStyle.SHAPE_CIRCLE:
                    g.setColor(elementStyle.shapeColor);
                    g.fillArc(cx - radius / 2, cy - radius / 2, radius, radius, 0, 360);
                    g.setColor(elementStyle.borderColor);
                    g.drawArc(cx - radius / 2, cy - radius / 2, radius, radius, 0, 360);

                    break;

                case ElementStyle.SHAPE_DYAMOND:

                    // configure poligons points coords
                    poligonXpoints[0] = cx;
                    poligonYpoints[0] = cy - radius / 2;

                    poligonXpoints[1] = cx + radius / 2;
                    poligonYpoints[1] = cy;

                    poligonXpoints[2] = cx;
                    poligonYpoints[2] = cy + radius / 2;

                    poligonXpoints[3] = cx - radius / 2;
                    poligonYpoints[3] = cy;

                    g.setColor(elementStyle.shapeColor);
                    g.fillPolygon(poligonXpoints, poligonYpoints, 4);
                    g.setColor(elementStyle.borderColor);
                    g.drawPolyline(poligonXpoints, poligonYpoints, 4);

                    break;

            }

            // draw index text aligned to center
            g.setColor(elementStyle.textColor);
            g.drawString(text, cx - textWidth / 2, cy + textHeight / 2);

        }

    }

    protected static boolean hasClicked(Graphics g, int clientX, int clientY, int x, int y, int shapeWidth, int shapeHeight, String text, ElementStyle elementStyle) {

        int cx = x + shapeWidth / 2;
        int cy = y + shapeHeight / 2;
        int radius = shapeWidth > shapeHeight ? shapeHeight : shapeWidth;

        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getHeight();

        if (elementStyle.shapeImage != null) {
            return (cx - radius/2 <= clientX && cx + radius/2 >= clientX) && (y <= clientY && y + shapeHeight - textHeight >= clientY);
        
        } else {

            // select element draw shape
            switch (elementStyle.shapeType) {
                case ElementStyle.SHAPE_QUAD:
                case ElementStyle.SHAPE_DYAMOND:
                    return (cx - radius/2 <= clientX && cx + radius/2 >= clientX) && (cy - radius/2 >= clientY && cy + radius/2 >= clientY);
                    
                case ElementStyle.SHAPE_RECTANGLE:
                    return (cx - textWidth/2 - 5 <= clientX && cx + textWidth/2 + 5 >= clientX) && (cy - textHeight/2 >= clientY && cy + textHeight/2 >= clientY);
                    
                case ElementStyle.SHAPE_CIRCLE:
                    return (int) Math.sqrt((double) Math.pow(cx - clientX, 2) + (double) Math.pow(cy - clientY, 2)) <= radius/2;
                    
            }
            
        }
        
        return false;
    }

}
