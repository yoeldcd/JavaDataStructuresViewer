package com.yoeld99apps.axegrl.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class ConectorPainter extends ObjectPainter {

    private Shape geometryA;
    private Shape geometryB;
    
    private Path2D.Double conectorPath;
    private Shape edgeAShape;
    private Shape edgeBShape;
    private Point textPosition;
    
    private double[] sideA;
    private double[] sideB;
    
    private Style style;
    private String text;
    
    private boolean isBiconected;
    private boolean isPrepared;
    
    public ConectorPainter() {
        super();
        
        this.style = ConectorPainter.Style.DEFAULT;
        
        this.geometryA = null;
        this.geometryB = null;
        this.conectorPath = new Path2D.Double();
        this.textPosition = new Point();
        
        // initialize path side segments
        this.sideA = new double[4];
        this.sideB = new double[4];
        
        this.isBiconected = false;
        this.isPrepared = false;
    }
    
    public void setGeometryA(Shape geometry) {
        if (geometry != null) {
            geometryA = geometry;
            isPrepared = false;
        }
    }
    
    public Shape getGeometryA() {
        return geometryA;
    }
    
    public void setGeometryB(Shape geometry) {
        if (geometry != null) {
            geometryB = geometry;
            isPrepared = false;
        }
    }
    
    public Shape getGeometryB() {
        return geometryB;
    }
    
    public void setBiconected(boolean isBiconected){
        this.isBiconected = isBiconected;
    }
    
    public boolean isBiconected(){
        return isBiconected;
    }
    
    public void setStyle(Style style) {
        
        if(style != null){
            this.style = style;
        }
        
        isPrepared = false;
    }
    
    public Style getStyle(){
        return style;
    }
    
    @Override
    public Shape getGeometry() {
        return conectorPath;
    }

    @Override
    public boolean isPrepared() {
        return this.isPrepared & component != null;
    }
    
    @Override
    public void prepare(Graphics2D g, String text) {
        
        isPrepared = false;
        
        if (geometryA == null || geometryB == null) {
            return;
        }
        
        Rectangle boundsA = geometryA.getBounds();
        Rectangle boundsB = geometryB.getBounds();
        
        // define drawed text bounds
        this.text = text;
        int textWidth = g.getFontMetrics(style.textFont).stringWidth(text);
        int textHeight = g.getFontMetrics(style.textFont).getHeight();
        
        double[] segmentA2B = {boundsA.getCenterX(), boundsA.getCenterY(), boundsB.getCenterX(), boundsB.getCenterY()};
        double[] intersA = {segmentA2B[0], segmentA2B[1]};
        double[] intersB = {segmentA2B[2], segmentA2B[3]};
        double[] textCenter = {0, 0, 0, 0};
        double   alpha = 0;
        
        double   sideRatio = 50;
        double   shapeRatio = 20;
        
        // compute A2B direction vector
        double dX = segmentA2B[2] - segmentA2B[0];
        double dY = segmentA2B[3] - segmentA2B[1];
        double dist = Math.sqrt(dX * dX + dY * dY);
        
        double dirSideAX = 0;
        double dirSideAY = 0;
        double dirSideBX = 0;
        double dirSideBY = 0;
        double sidesDistance = 0;
        double collideRatio = 0;
        
        boolean intersectsA = false;
        boolean intersectsB = false;
        boolean collideAB = false;
            
        // configure loop
        if(geometryA != geometryB){
            
            switch(style.getPathType()){
                case ConectorPainter.Style.POLYLINE:
                    
                    if(Math.abs(dX) > (boundsA.width + boundsB.width) / 2 + sideRatio){
                        // define A side direction as horizontal
                        dirSideAX = Math.signum(dX);
                        dirSideAY = 0;
                        
                        if(Math.abs(dX) > Math.abs(dY)){
                            // define B side orientation as horizontal
                            dirSideBX = -dirSideAX;
                            dirSideBY = 0;
                        } else{
                            // define B side orientation as vertical
                            dirSideBX = 0;
                            dirSideBY = - Math.signum(dY);
                        }
                        
                        // compute edge min distance
                        sidesDistance = Math.abs(sideA[2] - sideB[2]);
                        
                    } else if(Math.abs(dX) >= Math.abs(dY)) {
                        
                        // define A side orientation as horizontal
                        dirSideAX = - Math.signum(dX);
                        dirSideAY = 0;
                        
                        // define B side orientation as horizontal
                        dirSideBX = - dirSideAX;
                        dirSideBY = 0;
                        
                        // compute edge min distance
                        sidesDistance = Math.abs(sideA[2] - sideB[2]);
                        collideAB = true;
                        collideRatio = Math.max(boundsA.height, boundsB.height);
                        
                    } else {
                        
                        // define A side orientation as vertical
                        dirSideAX = 0;
                        
                        if(Math.abs(dY) > (boundsA.height + boundsB.height) / 2 + sideRatio){
                            dirSideAY = Math.signum(dY);
                        }else{
                            dirSideAY = - Math.signum(dY);
                            collideAB = true;
                            collideRatio = Math.max(boundsA.width, boundsB.width);
                        }
                        
                        // define B side orientation as vertical
                        dirSideBX = 0;
                        dirSideBY = -dirSideAY;
                        
                        // compute edge min distance
                        sidesDistance = Math.abs(sideA[3] - sideB[3]);
                        
                    }
                    
                    // define A side back point
                    if(dirSideAX != 0){
                        sideA[2] = (dirSideAX >= 0 ? boundsA.getMaxX() : boundsA.getMinX());
                        sideA[3] = segmentA2B[1];
                    } else {
                        sideA[2] = segmentA2B[0];
                        sideA[3] = (dirSideAY >= 0 ? boundsA.getMaxY() : boundsA.getMinY());
                    }
                    
                    // define B side back point
                    if(dirSideBX != 0){
                        sideB[2] = (dirSideBX >= 0 ? boundsB.getMaxX() : boundsB.getMinX());
                        sideB[3] = segmentA2B[3];
                    } else {
                        sideB[2] = segmentA2B[2];
                        sideB[3] = (dirSideBY >= 0 ? boundsB.getMaxY() : boundsB.getMinY());
                    }
                    
                    // adjust optime edge ratio to min distance
                    if(sidesDistance <= sideRatio * 3){
                        if(sidesDistance / 3 > sideRatio * 0.5){
                            sideRatio = Math.floor(sidesDistance / 3);
                        } else {
                            sideRatio *= 0.5;
                        }
                    }
                    
                    // compute side B start point
                    sideA[0] = sideA[2] + dirSideAX * sideRatio;
                    sideA[1] = sideA[3] + dirSideAY * sideRatio;
                    intersectsA = true;
                    
                    // compute side B start point
                    sideB[0] = sideB[2] + dirSideBX * sideRatio;
                    sideB[1] = sideB[3] + dirSideBY * sideRatio;
                    intersectsB = true;
                    
                    break;
                case ConectorPainter.Style.LINE:
                default:
                    
                    // compute A and B shapes intersesction with segment AB 
                    intersectsA = computeLine2ShapeIntersection(segmentA2B, geometryA, intersA);
                    intersectsB = computeLine2ShapeIntersection(segmentA2B, geometryB, intersB);
                    
                    // normalize direction vector
                    if(dist != 0){
                        dirSideAX = dX / Math.abs(dist);
                        dirSideAY = dY / Math.abs(dist);
                    }
                    
                    // reverse A direction and set to B
                    dirSideBX = - dirSideAX;
                    dirSideBY = - dirSideAY;
                    
                    // define side A back point
                    sideA[2] = intersA[0];
                    sideA[3] = intersA[1];
                    
                    // define side B back point
                    sideB[2] = intersB[0];
                    sideB[3] = intersB[1];
                    
                    // compute edge distance
                    sidesDistance = Math.sqrt(Math.pow(sideA[2]-sideB[2],2)+Math.pow(sideA[3]-sideB[3],2));
                    
                    // adjust optime edge ratio to min distance
                    if(sidesDistance <= sideRatio * 3){
                        if(sidesDistance / 3 > sideRatio * 0.5){
                            sideRatio = Math.floor(sidesDistance / 3);
                        } else {
                            sideRatio *= 0.5;
                        }
                    }
                    
                    // compute side A start point
                    sideA[0] = sideA[2] + dirSideAX * sideRatio;
                    sideA[1] = sideA[3] + dirSideAY * sideRatio;
                    
                    // compute side B start point
                    sideB[0] = sideB[2] + dirSideBX * sideRatio;
                    sideB[1] = sideB[3] + dirSideBY * sideRatio;
                    
            }
            
        } else {
            
        }
        
        // transform A edge geometry to make his side edge shape
        if(intersectsA){
            alpha = Math.atan2(sideA[2] - sideA[0], sideA[3] - sideA[1]);
            edgeAShape = makeEdgeShape(!isBiconected ? style.getEdgeAGeometryType() : style.getEdgeBGeometryType(), sideA, alpha, shapeRatio); 
            
        } else {
            edgeAShape = null;
        }
        
        // transform B edge geometry to make his side edge shape 
        if(intersectsB){
            alpha = Math.atan2(sideB[2] - sideB[0], sideB[3] - sideB[1]);
            edgeBShape = makeEdgeShape(style.getEdgeBGeometryType(), sideB, alpha, shapeRatio);
            
        } else {
            edgeBShape = null;
        }
        
        // make conector path
        switch(style.pathType){
            case ConectorPainter.Style.POLYLINE:
                conectorPath = makePolylinePath(sideA, sideB, textCenter, collideAB, collideRatio);
                break;
                
            default:
            conectorPath = makeLinePath(sideA, sideB, textCenter);
            
        }
        
        // compute text position
        textPosition.x = (int) (textCenter[0] + textWidth * textCenter[2]);
        textPosition.y = (int) (textCenter[1] + textHeight * textCenter[3]);
        
        isPrepared = true;
        
    }
    
    // path genertion functions 
    protected Path2D.Double makeLinePath(double[] sideA, double[] sideB, double[] center){
        Path2D.Double path = new Path2D.Double();
        
        path.moveTo(sideA[2], sideA[3]);
        path.lineTo(sideA[0], sideA[1]);
        path.lineTo(sideB[0], sideB[1]);
        path.lineTo(sideB[2], sideB[3]);
        
        // compute generated path center
        center[0] = (sideA[0] + sideB[0])/2;
        center[1] = (sideA[1] + sideB[1])/2;
        
        return path;
    }
    
    protected Path2D.Double makePolylinePath(double[] sideA, double sideB[], double[] center, boolean collideAB, double collideRatio) {
        
        double dx, dy;
        double dirAX, dirAY, dirBX, dirBY;
        double x, y;
        
        Path2D.Double path = new Path2D.Double();
        
        // make edges A line
        path.moveTo(sideA[0], sideA[1]);
        path.lineTo(sideA[2], sideA[3]);
        
        // make edge B line
        path.moveTo(sideB[0], sideB[1]);
        path.lineTo(sideB[2], sideB[3]);
        
        // make path
        dx = sideB[0] - sideA[0];
        dy = sideB[1] - sideA[1];
        
        x = sideA[0];
        y = sideA[1];
        
        // compute side direction
        dirAX = Math.signum(sideA[0] - sideA[2]);
        dirAY = Math.signum(sideA[1] - sideA[3]);
        dirBX = Math.signum(sideB[0] - sideB[2]);
        dirBY = Math.signum(sideB[1] - sideB[3]);
        
        path.moveTo(x, y);
        
        // use default text left alignement
        center[2] = -0.5;
        
        if(collideAB){
            if(dirAX != 0){
                y += (dy < 0 ? collideRatio : - collideRatio) * 1.2;
                path.lineTo(x, y);
                
                // use segment center as path center
                center[0] = x + dx/2;
                center[1] = y;
                center[3] = dy < 0 ? 1.05 : -0.33;
                    
                x += dx;
                path.lineTo(x, y);
                
                path.lineTo(sideB[0], sideB[1]);
                
            } else {
                
                x += (dx < 0 ? collideRatio : - collideRatio) * 1.2;
                path.lineTo(x, y);
                
                // use segment center as path center
                center[0] = x;
                center[1] = y + dy/2;
                center[2] = dx < 0 ? 0.05 : -1.05;
                
                y += dy;
                path.lineTo(x, y);
                
                path.lineTo(sideB[0], sideB[1]);
                
            }
            
        } else if(dirAX != 0){
            
            if(dirBX != 0){
                x += dx/2;
                path.lineTo(x, y);
                
                // use segment center as path center
                center[0] = x;
                center[1] = y + dy/2;
                
                y += dy;
                path.lineTo(x, y);
                
                x += dx/2;
                path.lineTo(x, y);
                
            } else {
                x += dx;
                path.lineTo(x, y);
                
                // use intersection as path center
                center[0] = x;
                center[1] = y;
                
                y += dy;
                path.lineTo(x, y);
            }
            
            
        } else {
            if(dirBY != 0){
                y += dy/2;
                path.lineTo(x, y);
                
                x += dx;
                path.lineTo(x, y);

                // use segment center as path center
                center[0] = x + dx/2;
                center[1] = y;
                
                y += dy/2;
                path.lineTo(x, y);
            } else{
                y += dy;
                path.lineTo(x, y);
                
                // use intersection as path center
                center[0] = x;
                center[1] = y;
                
                x += dx;
                path.lineTo(x, y);
                
            }
            
        }
        
        return path;
    }
    
    protected Shape makeEdgeShape(int geometryType, double[] side, double alpha, double size){
        
        double cx = side[2];
        double cy = side[3];
        
        int numPoints = 0;
        double[] points = new double[30];
        
        switch(geometryType){
            case ConectorPainter.Style.EDGE_GEOMETRY_NONE:
                return null;
                
            case ConectorPainter.Style.EDGE_GEOMETRY_ARROW:
                numPoints = 4;
                System.arraycopy(Style.EDGE_GEOMETRY_ARROW_POINTS, 0, points, 0, numPoints * 2);
                
                break;
                
            case ConectorPainter.Style.EDGE_GEOMETRY_TAIL:  
                numPoints = 6;
                System.arraycopy(Style.EDGE_GEOMETRY_TAIL_POINTS, 0, points, 0, numPoints * 2);
                
                break;
            
            case ConectorPainter.Style.EDGE_GEOMETRY_QUAD:
                
                break;
            case ConectorPainter.Style.EDGE_GEOMETRY_DYAMOND:
                
                break;
            default:
                return null;
        }
        
        if(numPoints > 0){
            
            // tarnsform all geometry points
            transformPoints(points, size, alpha, cx, cy, numPoints);
            
            // define edge shape start point
            side[2] = points[0*2];
            side[3] = points[0*2 + 1];
            
            return makePolygon(points, numPoints);
        }
        
        return null;
    }
    
    protected static void transformPoints(double[] points, double scale, double rotAlpha, double cx, double cy, int numPoints){
        
        double x, y;
        
        // validate
        if(points.length < numPoints*2)
            return;
        
        // transform any points
        for(int i = 0; i < numPoints * 2; i += 2){
            
            // scale points
            x = points[i] * scale;
            y = points[i + 1] * scale;
            
            // rotate points 
            points[i] = x * Math.sin(rotAlpha) + y * Math.cos(rotAlpha);
            y = x * Math.cos(rotAlpha) - y * Math.sin(rotAlpha);
            
            // translate to center position and store point
            points[i] += cx;
            points[i + 1] = y + cy;
            
        }
        
    }
    
    protected static Polygon makePolygon(double[] points, int numPoints){
        
        Polygon poly = new Polygon();
        
        // validate
        if(points.length < numPoints * 2)
            return poly;
        
        // add polygon points using coordinates values pair's
        for(int i = 0; i < numPoints * 2; i += 2){
            poly.addPoint((int) points[i], (int) points[i+1]);
        }
        
        return poly;
    }
    
    @Override
    public void paint(Graphics2D g) {
        
        if (!isPrepared()) {
            return;
        }
        
        // draw conector path
        g.setStroke(style.getStroke());
        g.setColor(style.pathColor);
        g.draw(conectorPath);
        
        // paint conector side A shape
        if(edgeAShape != null){
            g.setColor(Color.RED);
            g.fill(edgeAShape);
            
            g.setColor(style.pathColor);
            g.draw(edgeAShape);
            
        }
        
        // paint conector side B shape
        if(edgeBShape != null){
            g.setColor(Color.BLUE);
            g.fill(edgeBShape);
            
            g.setColor(style.pathColor);
            g.draw(edgeBShape);
            
        }
        
        // paint conector label
        if(text != null){
            g.setFont(style.textFont);
            g.setColor(style.textColor);
            g.drawString(text, textPosition.x, textPosition.y);
        }
        
    }
    
    // miscelaneous geometry math functions
    private boolean computeLine2ShapeIntersection(double[] lineA, Shape shape, double[] output){
        
        int segmentState;
        double[] pathv = new double[6]; // here store path iterator data sequences
        double[] lineB = new double[4]; // here store segmentB points
        double[] point = null;          // here store line2line intersection point
        double inix = 0, iniy = 0;      // initial geometry path point
        
        PathIterator iterator = null;
        
        if(shape instanceof Arc2D){
            
        } else {
            iterator = shape.getPathIterator(new AffineTransform());
                
            // trave all segments of shape
            do{
                
                segmentState = iterator.currentSegment(pathv);
                iterator.next();
               
                switch(segmentState){
                    case PathIterator.SEG_MOVETO:       // define first polyline point
                        inix = lineB[0] = pathv[0];
                        iniy = lineB[1] = pathv[1];
                        
                        break;
                    case PathIterator.SEG_CLOSE:        // return to first point
                        pathv[0] = inix;
                        pathv[1] = iniy;
                        
                    case PathIterator.SEG_LINETO:       // eval next segment
                        lineB[2] = pathv[0];
                        lineB[3] = pathv[1];
                        
                        point = computeLine2LineIntersection(lineA, lineB);
                        
                        // verify point interception with 2 segments
                        if(point != null && (!isPointContainedBySegment(point, lineA, 1) || !isPointContainedBySegment(point, lineB, 1))){
                            point = null;
                        }
                        
                        lineB[0] = lineB[2];
                        lineB[1] = lineB[3];
                        break;
                }
                
            } while(segmentState != PathIterator.SEG_CLOSE && point == null);
            
        }
        
        // store result coordinates on output point vector
        if(point != null){
            output[0] = point[0];
            output[1] = point[1];
            
            return true;
        }
        
        return false;
    }
    
    private double[] computeLine2LineIntersection(double[] l1, double[] l2){
        double[] point = {0, 0};
        
        double dxA = l1[2] - l1[0];
        double dxB = l2[2] - l2[0];
        double dyA, dyB;
        double mA, nA, mB, nB;
        
        if(dxA !=0 && dxB != 0){
            dyA = l1[3] - l1[1];
            dyB = l2[3] - l2[1];
            
            mA = dyA / dxA;
            nA = - mA * l1[0] + l1[1];
            
            mB = dyB / dxB;
            nB = - mB * l2[0] + l2[1];
            
            if(mA != mB) {
                point[0] = (nA - nB) / (mB - mA);
                point[1] = mA * point[0] + nA;
                
            } else {
                point = null;
            }
            
        } else if (dxB == 0 && dxA == 0) {
            point[0] = l1[0];
            point[1] = l1[1];
            
        } else if(dxB == 0) {
            
            dyA = l1[3] - l1[1];
            mA = dyA / dxA;
            nA = - mA * l1[0] + l1[1];
            
            point[0] = l2[0];
            point[1] = mA * l2[0] + nA;
            
        } else {
            
            dyB = l2[3] - l2[1];
            mB = dyB / dxB;
            nB = - mB * l2[0] + l2[1];
            
            point[0] = l1[0];
            point[1] = mB * l1[0] + nB;
            
        } 
        
        return point;
    };
    
    private static boolean isPointContainedBySegment(double[] p, double[] l, double rTo) {
        
        boolean contained = Math.abs(Math.abs(l[0] - l[2]) - Math.abs(l[0] - p[0]) - Math.abs(l[2] - p[0])) <= rTo;  //rX <= rTo
        
        if(contained)
            contained = Math.abs(Math.abs(l[1] - l[3]) - Math.abs(l[1] - p[1]) - Math.abs(l[3] - p[1])) <= rTo;      //ry <= rTo
        
        return contained;
    }
    
    
    private static String toString(double[] d){
        StringBuilder build = new StringBuilder();
        int i = 0;
        
        for(double v : d){
            build.append((i++ % 2 != 0 ? " x: " : " y: " ));
            build.append(v);
        }
        
        return build.toString();
    }
    
    public static class Style extends ObjectPainter.Style<Style> {

        // avaliables path representation
        public static final int LINE = 0x6267000;
        public static final int POLYLINE = 0x6267001;

        // avaliables path dot representations
        public static final int EDGE_GEOMETRY_NONE = 0x6267010;
        public static final int EDGE_GEOMETRY_DOT = 0x6267011;
        public static final int EDGE_GEOMETRY_QUAD = 0x6267012;
        public static final int EDGE_GEOMETRY_DYAMOND = 0x6267013;
        public static final int EDGE_GEOMETRY_ARROW = 0x6267014;
        public static final int EDGE_GEOMETRY_TAIL = 0x6267015;
        
        // dot representation geometry points mesh
        public static final double[] EDGE_GEOMETRY_ARROW_POINTS = {-0.5, 0, -1, 0.25, -1,0,-1,-0.25};
        public static final double[] EDGE_GEOMETRY_TAIL_POINTS = { -1, 0, -0.75, 0.25, -0.5, 0.25, -0.65, 0, -0.5, -0.25, -0.75, -0.25};
        
        // default style configuration values
        public static final Color DEFAULT_PATH_COLOR = Color.WHITE;
        public static final Color DEFAULT_TEXT_COLOR = Color.GRAY;
        public static final Color DEFAULT_DOT_COLOR = Color.WHITE;
        public static final int DEFAULT_PATH_TYPE = LINE;
        public static final int DEFAULT_EDGE_A_GEOMETRY_TYPE = EDGE_GEOMETRY_TAIL;
        public static final int DEFAULT_EDGE_B_GEOMETRY_TYPE = EDGE_GEOMETRY_ARROW;
        public static final int DEFAULT_LINE_SIZE = 2;

        public static final Style DEFAULT = new Style();

        // draw colors
        Stroke stroke;
        Color pathColor;
        Color dotAColor;
        Color dotBColor;
        Color textColor;
        Font textFont;
        String name;
        
        int lineSize;
        int pathType;
        int edgeAGeometryType;
        int edgeBgeometryType;
        
        private boolean permitOverlay;
        private boolean hasStrokeModified;

        public Style() {
            this(0, null, null, null, null);
        }

        public Style(int pathType, Color pathColor) {
            this(pathType, pathColor, null, null, null);
        }
        
        public Style(int pathType, Color pathColor, Color textColor) {
            this(pathType, pathColor, textColor, null, null);
        }
        
        public Style(int pathType, Color pathColor, Color textColor, Color dotAColor, Color dotBColor) {
            super();

            this.pathType = DEFAULT_PATH_TYPE;
            this.edgeAGeometryType = DEFAULT_EDGE_A_GEOMETRY_TYPE;
            this.edgeBgeometryType = DEFAULT_EDGE_B_GEOMETRY_TYPE;
            this.lineSize = DEFAULT_LINE_SIZE;

            setPathType(pathType);

            this.pathColor = pathColor != null ? pathColor : DEFAULT_PATH_COLOR;
            this.textColor = textColor != null ? textColor : DEFAULT_TEXT_COLOR;
            this.dotAColor = dotAColor != null ? dotAColor : DEFAULT_DOT_COLOR;
            this.dotBColor = dotBColor != null ? dotBColor : this.dotAColor;

            this.permitOverlay = false;
            
            this.stroke = new BasicStroke(this.lineSize);
            this.textFont = new Font(Font.MONOSPACED, Font.BOLD, 18);
            this.hasStrokeModified = false;
            
            this.name = "default";
        }
        
        @Override
        public Style copyStyleRulesTo(Style other) {
            other.pathType = this.pathType;
            other.edgeAGeometryType = this.edgeAGeometryType;
            other.edgeBgeometryType = this.edgeBgeometryType;
            
            other.lineSize = this.lineSize;
            other.pathType = this.pathType;
            
            other.pathColor = this.pathColor;
            other.textColor = this.textColor;
            other.dotAColor = this.dotAColor;
            other.dotBColor = this.dotBColor;

            other.permitOverlay = false;
            
            other.stroke = new BasicStroke(this.lineSize);
            other.textFont = this.textFont.deriveFont(this.textFont.getSize());
            other.hasStrokeModified = false;
            
            
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
        
        public void setPathType(int pathType) {
            if (pathType == LINE || pathType == POLYLINE) {
                this.pathType = pathType;
            }

        }
        
        public int getPathType() {
            return pathType;
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
        
        public void setEdgeAGeometryType(int dotType) {
            if (dotType >= EDGE_GEOMETRY_NONE & dotType <= EDGE_GEOMETRY_ARROW) {
                this.edgeAGeometryType = dotType;
            }
        }
        
        public int getEdgeAGeometryType() {
            return edgeAGeometryType;
        }
        
        public void setEdgeBGeometryType(int dotType) {
            if (dotType >= EDGE_GEOMETRY_NONE & dotType <= EDGE_GEOMETRY_ARROW) {
                this.edgeBgeometryType = dotType;
            }
        }
        
        public int getEdgeBGeometryType() {
            return edgeBgeometryType;
        }
        
        public void permitOverlay(boolean permit){
            this.permitOverlay = permit;
        }
        
        public boolean isPermitedEdgesOverlay(){
            return permitOverlay;
        }
        
        @Override
        public Stroke getStroke() {
            if (hasStrokeModified) {
                hasStrokeModified = false;
                this.stroke = new BasicStroke(lineSize);

            }

            return this.stroke;
        }

        

    }
    
}
