package com.yoeld99apps.toolsTDAs;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class DataGraphDisplayer extends DrawableComponent {

    private DataGraph<GraphicNode> dataGraph;
    private GraphicNode selectedNode;

    private int rows = 5;
    private int cols = 15;

    private ElementStyle defaultStyle;
    private ElementStyle selectedStyle;

    private MouseListener onclickListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            selectNode(e.getX(), e.getY());
        }

    };

    public DataGraphDisplayer() {
        super();
        
        this.dataGraph = new DataGraph(true);
        this.selectedNode = null;

        this.defaultStyle = new ElementStyle(Color.BLUE, Color.WHITE, Color.WHITE, ElementStyle.SHAPE_QUAD);
        this.selectedStyle = new ElementStyle(Color.BLUE, Color.YELLOW, Color.GREEN, ElementStyle.SHAPE_CIRCLE);

        this.defaultStyle.shapeImage = ElementStyle.loadImage("media/PC.jpg");
        this.selectedStyle.shapeImage = this.defaultStyle.shapeImage;
        this.defaultStyle.shapeImage.flush();
        
        this.addMouseListener(onclickListener);

    }

    public void setGraph(DataGraph<?> graph) {
        this.dataGraph = new DataGraph(graph.isDigraph);

        // add nodes to graph
        for (Object o : graph.getVertexs()) {
            GraphicNode gnode = new GraphicNode(o.toString());

            // recompute coords
            gnode.row = (int) Math.round(Math.random() * (rows - 1));
            gnode.column = (int) Math.round(Math.random() * (cols - 1));

            this.dataGraph.add(gnode);
        }

        // conect linkeds nodes
        int nodeI = 0;
        for (GraphicNode gnode : this.dataGraph.getVertexs()) {
            this.dataGraph.conect(gnode, graph.getLinks().get(nodeI));
            nodeI++;
        }

        super.paint(null);
    }

    @Override
    protected void renderize(Graphics g, int width, int height) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        int colSize = width / cols;
        int rowSize = height / rows;
        int nodeI = 0;
        
        // renderize any links
        for (GraphicNode gnode1 : this.dataGraph.getVertexs()) {
            for(int otherI : this.dataGraph.getLinks().get(nodeI)){
                GraphicNode gnode2 = this.dataGraph.getVertexs().get(otherI);
                
                this.renderizeLink(g,
                        gnode1.column * colSize,
                        gnode1.row * rowSize,
                        gnode2.column * colSize,
                        gnode2.row * rowSize,
                        colSize, rowSize,
                        gnode1.selected ? this.selectedStyle : this.defaultStyle
                );
            }
            
            nodeI++;
        }
        
        // renderize any nodes
        for (GraphicNode gnode : this.dataGraph.getVertexs()) {
            DrawableElement.drawTextElement(g,
                    colSize * gnode.column,
                    rowSize * gnode.row,
                    colSize, rowSize,
                    gnode.value,
                    gnode.selected ? this.selectedStyle : this.defaultStyle
            );
        }
        
        
    }

    protected void renderizeLink(Graphics g, int x1, int y1, int x2, int y2, int shapeWidth, int shapeHeight, ElementStyle elementStyle){
        
        int cx1 = x1 + shapeWidth / 2;
        int cy1 = y1 + shapeHeight / 2;
        
        int cx2 = x2 + shapeWidth / 2;
        int cy2 = y2 + shapeHeight / 2;
        
        g.setColor(elementStyle.borderColor);
        g.drawLine(cx1, cy1, cx2, cy2);
        
    }
    
    protected void selectNode(int clientX, int clientY) {
        int colSize = this.getWidth() / cols;
        int rowSize = this.getHeight() / rows;

        GraphicNode lastClicked = null;

        // renderize any nodes
        for (GraphicNode gnode : this.dataGraph.getVertexs()) {
            if (DrawableElement.hasClicked(this.graphics,
                    clientX, clientY,
                    colSize * gnode.column,
                    rowSize * gnode.row,
                    colSize, rowSize,
                    gnode.value,
                    gnode.selected ? this.selectedStyle : this.defaultStyle
            )) {
                lastClicked = gnode;
            }
        }

        if (lastClicked != null) {

            if (this.selectedNode != null && lastClicked != this.selectedNode) {
                this.selectedNode.selected = false;
            }

            // change node state
            this.selectedNode = lastClicked;
            this.selectedNode.selected = !this.selectedNode.selected;

            this.transferFocusUpCycle();
            super.paint(null);
        }
    }
    
    
    public void onKeyCommand(int key) {

        if (this.selectedNode != null) {
            switch (key) {
                case KeyEvent.VK_UP:
                    if (this.selectedNode.row > 0) {
                        this.selectedNode.row--;
                        super.paint(null);
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (this.selectedNode.row < this.rows - 1) {
                        this.selectedNode.row++;
                        super.paint(null);
                    }
                    break;

                case KeyEvent.VK_LEFT:
                    if (this.selectedNode.column > 0) {
                        this.selectedNode.column--;
                        super.paint(null);
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (this.selectedNode.column < cols - 1) {
                        this.selectedNode.column++;
                        super.paint(null);
                    }
                    break;
                 
                case KeyEvent.VK_S:
                    if(this.selectedNode != null){
                        super.exportAsImage();
                    }
                    
                    break;

            }
        }
    }

    protected class GraphicNode {

        protected int row;
        protected int column;
        protected boolean focussed;
        protected boolean selected;
        protected ElementStyle style;
        protected String value;

        public GraphicNode(String value) {

            this.value = value;
            this.row = 0;
            this.column = 0;
            this.style = null;
            this.focussed = false;
            this.selected = false;

        }

    }

}
