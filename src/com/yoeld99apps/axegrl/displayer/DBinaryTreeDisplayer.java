package com.yoeld99apps.axegrl.displayer;

import com.yoeld99apps.axegrl.painters.ConectorPainter;

public class DBinaryTreeDisplayer extends Displayer {

    public String[] values = new String[15];
    public DNode[] nodes;
    
    public DBinaryTreeDisplayer() {
        super(SELECT_ITEMS | CUSTOMIZE_ITEMS | CONFIG_ITEMS);
        
        for (int i = 0; i < values.length; i++) {
            values[i] = "" + i;
        }

        setValues(values);
    }

    public void setValues(String[] values) {
        this.values = values;
        this.nodes = new DNode[values.length];

        super.removeAll();
        for (int i = 0; i < values.length; i++) {
            nodes[i] = new DNode(values[i]);

            // set custom node states
            nodes[i].setSelectedStyle(getShapeStyles().get(0));
            nodes[i].setDisableStyle(getShapeStyles().get(1));
            nodes[i].setEnableStyle(getShapeStyles().get(2));
            
            super.addShape(nodes[i]);
        }

        buildGraphicTree();
    }

    private void buildGraphicTree() {

        int levelNode = 0;
        int levelNodesNumber = 1;

        int level = 1;
        float levelXInterval = 1f;

        int i = 0;

        while (i < values.length) {

            // compute relative position X and Y level of node
            nodes[i].setRelativeCenter(levelXInterval * levelNode + levelXInterval / 2, 0.1 * level);
            levelNode++;

            if (levelNode == levelNodesNumber) {
                // jump to next herarchy level
                level++;
                levelNode = 0;

                // compute level ranges
                levelNodesNumber = (int) Math.pow(2, level - 1);
                levelXInterval = levelXInterval / 2.0f;

            }

            // conect node to left children if exists
            if (i * 2 + 1 < nodes.length) {
                conectShapes(nodes[i], nodes[i * 2 + 1], ConectorPainter.Style.LINE).setText(null);
            }
            
            // conect node to rigth children if exists
            if (i * 2 + 2 < nodes.length) {
                conectShapes(nodes[i], nodes[i * 2 + 2], ConectorPainter.Style.LINE).setText(null);
            }

            i++;
        }

        // compute relative interval sizes
        float levelYInterval = 1.0f / level;
        float relativeSize = (float) Math.min(0.1, Math.min(levelXInterval * 2, levelYInterval));

        // compute real position of nodes on Displayer Layout 
        for (DNode node : nodes) {
            node.setSizeRelative(true);
            node.setRelativeCenter(node.getRelativeX(), node.getRelativeY() * 10 * levelYInterval - levelYInterval / 2);
            node.setRelativeSize(relativeSize, relativeSize);
            node.setRelativeSpace(0, 0, width, height);
        }

        update();
    }

}
