package com.yoeld99apps.axegrl.displayer;

import com.yoeld99apps.axegrl.components.ContainerItem;
import com.yoeld99apps.axegrl.painters.ShapePainter;
import java.awt.Graphics2D;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class DShape extends ContainerItem implements DElement {

    protected ShapePainter painter;
    protected ShapePainter.Style disableStyle;
    protected ShapePainter.Style enableStyle;
    protected ShapePainter.Style selectedStyle;

    protected String text;
    protected DShape cotainer;

    protected int selectionIndex;
    protected boolean isSelected;
    protected boolean isEnable;
    protected boolean isUpdated;
    protected boolean isSizeRelative;

    private double relativeCenterX;
    private double relativeCenterY;
    private double relativeWidth;
    private double relativeHeight;

    public DShape() {
        this(null);
    }

    public DShape(String text) {
        super();

        this.painter = new ShapePainter();
        this.painter.setComponent(this);

        // styles
        this.disableStyle = new ShapePainter.Style();
        this.enableStyle = new ShapePainter.Style();
        this.selectedStyle = new ShapePainter.Style();
        this.painter.setStyle(disableStyle);

        this.isSelected = false;
        this.isEnable = false;
        this.isUpdated = false;
        this.isSizeRelative = false;

        this.relativeCenterX = 0;
        this.relativeCenterY = 0;
        this.relativeHeight = 0.1;
        this.relativeWidth = 0.1;

        setText(text);
        setBounds(0, 0, 50, 50);

    }

    public void setText(String text) {
        this.text = text != null ? text : null;
        update();

    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return isSelected ? text + "[" + selectionIndex + "] " + (isEnable ? "E" : "D") : text;
    }

    public void setContainer(DShape container) {
        this.cotainer = container;
    }

    public DShape getContainer() {
        return this.cotainer;
    }

    // Style management methoods
    public ShapePainter.Style getDisableStyle() {
        return disableStyle;
    }

    public ShapePainter.Style setDisableStyle(ShapePainter.Style style) {
        if (style != null) {
            this.disableStyle = style;

            // update rendered layout style settings
            if (isSelected) {
                preserveStateStyle();
            } else if (!isEnable) {
                painter.setStyle(style);

            }

            update();
        }

        return this.disableStyle;
    }

    public ShapePainter.Style getEnableStyle() {
        return enableStyle;
    }

    public ShapePainter.Style setEnableStyle(ShapePainter.Style style) {
        if (style != null) {
            this.enableStyle = style;

            // update rendered layout style settings
            if (isSelected) {
                preserveStateStyle();
            } else if (isEnable) {
                painter.setStyle(style);
            }

            update();
        }

        return this.enableStyle;
    }

    public ShapePainter.Style getSelectedStyle() {
        return selectedStyle;
    }

    public ShapePainter.Style setSelectedStyle(ShapePainter.Style style) {
        if (style != null) {
            this.selectedStyle = style;

            // update render style settings
            if (isSelected) {
                preserveStateStyle();
                painter.setStyle(style);
                update();
            }

        }

        return this.selectedStyle;
    }

    public void preserveStateStyle() {

        // preserve geometry
        if (selectedStyle.getGeometryType() == ShapePainter.Style.GEOMETRY_FREE) {
            selectedStyle.setGeometryType(isEnable ? enableStyle.getGeometryType() : disableStyle.getGeometryType());
        }

        // preserve text alignement
        if (selectedStyle.getTextAlignement() == ShapePainter.Style.TEXT_FREE) {
            selectedStyle.setTextAlignement(isEnable ? enableStyle.getTextAlignement() : disableStyle.getTextAlignement());
        }

        // preserve figure image
        selectedStyle.setImage(isEnable ? enableStyle.getImage() : disableStyle.getImage());

    }

    //Geometry management methoods
    public void moveTo(int x, int y) {
        super.setLocation(x, y);

    }

    public void displace(int dx, int dy) {
        super.setLocation(x + dx, y + dy);

    }

    public Point getCenter() {
        Rectangle bounds = super.getBounds();
        return new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
    }

    public void centerTo(int cx, int cy) {
        super.setLocation(cx - width / 2, cy - height / 2);
    }

    public double getRelativeX() {
        return relativeCenterX;
    }

    public double getRelativeY() {
        return relativeCenterY;
    }

    public double getRelativeWidth() {
        return relativeWidth;
    }

    public double getRelativeHeight() {
        return relativeHeight;
    }

    public void setSizeRelative(boolean isSizeRelative) {
        this.isSizeRelative = isSizeRelative;
    }

    public boolean isSizeRelative() {
        return isSizeRelative;
    }

    public void computeRelativeCenter(int spaceWidth, int spaceHeight) {

        if (spaceWidth > 0 && spaceHeight > 0) {
            Rectangle bounds = getBounds();

            // compute relative center coords
            relativeCenterX = bounds.getCenterX() / (double) spaceWidth;
            relativeCenterY = bounds.getCenterY() / (double) spaceHeight;
        }

    }

    public void computeRelativeSize(int spaceWidth, int spaceHeight) {

        // compute relative dimensions
        if (spaceWidth > 0 && spaceHeight > 0) {
            relativeWidth = (double) getWidth() / (double) spaceWidth;
            relativeHeight = (double) getHeight() / (double) spaceHeight;
        }
    }

    public void setRelativeCenter(double rcx, double rcy) {
        this.relativeCenterX = rcx;
        this.relativeCenterY = rcy;
    }

    public void setRelativeSize(double rwidth, double rheight) {
        this.relativeWidth = rwidth;
        this.relativeHeight = rheight;
    }

    public void setRelativeSpace(int relativeX, int relativeY, int spaceWidth, int spaceHeight) {

        int cx, cy, w, h;

        if (spaceWidth > 0 && spaceHeight > 0) {

            double ratio = spaceWidth / 1000;

            // computed last normalized center coords on new space
            cx = (int) (spaceWidth * relativeCenterX) + relativeX;
            cy = (int) (spaceHeight * relativeCenterY) + relativeY;
            
            if (isSizeRelative) {

                // compute relative size
                w = (int) (spaceWidth * relativeWidth);
                h = (int) (spaceHeight * relativeHeight);

                super.reshape(cx - w / 2, cy - h / 2, w, h);
                
            } else {
                w = (int) (width);
                h = (int) (height);

                super.reshape(cx - w / 2, cy - h / 2, w, h);
                
            }

        }

    }

    // State management methoods
    @Override
    public boolean isPointedBy(Point point) {
        return getGeometry().contains(point);
    }

    @Override
    public boolean isInsideOf(Rectangle area) {
        return getGeometry().intersects(area);
    }

    @Override
    public Shape getGeometry() {
        return painter != null ? painter.getGeometry() : getBounds();
    }

    @Override
    public void enable() {
        isEnable = true;

        if (!isSelected) {
            painter.setStyle(enableStyle);
        } else {
            preserveStateStyle();
        }

        update();
    }

    @Override
    public void disable() {
        isEnable = false;

        if (!isSelected) {
            painter.setStyle(disableStyle);
        } else {
            preserveStateStyle();
        }

        update();

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
        isSelected = true;
        this.selectionIndex = selectionIndex;

        preserveStateStyle();

        painter.setStyle(selectedStyle);
        update();
    }

    @Override
    public void dispose() {
        isSelected = false;
        selectionIndex = -1;

        if (isEnable) {
            painter.setStyle(enableStyle);
        } else {
            painter.setStyle(disableStyle);
        }

        update();
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public boolean isUpdated() {
        return isUpdated;
    }

    @Override
    public void update() {
        isUpdated = true;
        repaint();
    }

    @Override
    public int getSelectionIndex() {
        return selectionIndex;
    }

    // Item events handlers
    @Override
    public void onPaint(Graphics2D g) {

        if (this.isUpdated) {
            painter.prepare(g, toString());
        }

        // draw painter
        painter.paint(g);

    }

    @Override
    public void onResized(int width, int height) {
        isUpdated = true;
    }

    @Override
    public void onMoved(int x, int y) {
        isUpdated = true;
    }

}
