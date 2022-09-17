package com.yoeld99apps.toolsTDAs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BinaryTreeDisplayer<T extends Comparable> extends DrawableComponent {

    private static final long serialVersionUID = 1L;

    // control values
    protected boolean isMenuLocked;
    protected boolean isSpaceSaved;
    protected int iterationDelay;

    // used trees
    private Stack<IBinaryTree> treeStack;
    private IBinaryTree paintedTree;
    private IBinaryTree focussedTree;
    private IBinaryTree focussedChildTree;
    private IBinaryTree selectedTree;

    //shape geometry values
    private int[] poligonXpoints;
    private int[] poligonYpoints;
    private int steepHeight;

    // style values
    private Color backColor;
    private ElementStyle defaultStyle;
    private ElementStyle focussedStyle;
    private ElementStyle focussedChildStyle;

    // text style values
    private Font textFont;
    private Font rootFont;
    private FontMetrics textFontMetrics;
    private FontMetrics rootFontMetrics;

    private PopupMenu menu;

    private final MouseAdapter mouseListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            // get and focused selected tree
            selectedTree = selectTree(x, y);

            // show options menu
            if (e.getButton() != MouseEvent.BUTTON1) {
                showPopUpMenu(x, y);
                focussedTree = selectedTree;

            } else if (selectedTree == paintedTree) {
                paintSubTree(null); // go back
            } // on
            // herarchy
            else if (selectedTree != null) {
                paintSubTree(selectedTree); // paint tree herarchy
            } else
		;

        }

    };

    public BinaryTreeDisplayer() {
        this(null);
    }

    public BinaryTreeDisplayer(IBinaryTree paintedTree) {
        super();

        // initaialize used tree values
        this.treeStack = new Stack<IBinaryTree>();
        this.paintedTree = paintedTree;
        this.focussedTree = null;
        this.focussedChildTree = null;
        this.selectedTree = null;

        // define default color styles
        this.rootFont = new Font("rootFont", Font.BOLD, 25);
        this.backColor = Color.BLACK;
        this.defaultStyle = new ElementStyle(Color.BLUE, Color.YELLOW, Color.WHITE, ElementStyle.SHAPE_CIRCLE);
        this.focussedStyle = new ElementStyle(Color.YELLOW, Color.BLUE, Color.CYAN, ElementStyle.SHAPE_CIRCLE);
        this.focussedChildStyle = new ElementStyle(Color.GREEN, Color.BLACK, Color.WHITE, ElementStyle.SHAPE_CIRCLE);

        // shape complex poligons points
        this.poligonXpoints = new int[10];
        this.poligonYpoints = new int[10];

        // set mouse listener
        this.addMouseListener(mouseListener);

        // make option menu
        this.buildPopUpMenu();
        this.isMenuLocked = true;
        this.isSpaceSaved = false;

        // define default iterations delay ot (ms)
        this.iterationDelay = 24;

    }

    public void setTree(IBinaryTree paintedTree) {
        this.paintedTree = paintedTree;
        this.focussedTree = null;
        this.treeStack.clear();

    }

    public IBinaryTree getTree() {
        return this.paintedTree;
    }

    private IBinaryTree selectTree(int mouseX, int mouseY) {
        int avaliableSpace = super.getWidth();
        int height = super.getHeight();
        int depth = 0;
        int chases = 0;
        int fontSize = 0;

        IBinaryTree selectedTree = null;

        // select one tree
        if (this.paintedTree != null) {

            depth = this.paintedTree.getDepth();
            chases = this.paintedTree.getChases();

            // reconfigure metrics
            fontSize = (avaliableSpace < height ? avaliableSpace : height) / chases / 4;

            // limite font size
            if (fontSize > 25) {
                fontSize = 25;
            }

            // generate new font metrics
            this.textFont = new Font("monospaced", Font.BOLD, fontSize);
            this.textFontMetrics = this.graphics.getFontMetrics(textFont);
            this.rootFontMetrics = this.graphics.getFontMetrics(rootFont);
            this.steepHeight = height / depth;

            // reduce avaliable space
            avaliableSpace /= 2;
            selectedTree = this.selectTreeElement(this.paintedTree, mouseX, mouseY, avaliableSpace, avaliableSpace, 25);

        }

        return selectedTree;
    }

    public void paintSubTree(IBinaryTree tree) {

        if (tree != null && tree != this.paintedTree) {
            // save last parent tree
            this.treeStack.push(this.paintedTree);
            this.paintedTree = tree;

        } else {

            // try restore last parent tree
            if (!this.treeStack.isEmpty()) {
                this.paintedTree = this.treeStack.pop();
            }

        }

        this.repaint();
    }

    public void paintTree() {
        this.paintTree(null, null);
    }

    public void paintTree(IBinaryTree focussedTree) {
        this.paintTree(focussedTree, null);
    }

    public void paintTree(IBinaryTree focussedTree, IBinaryTree focussedChildTree) {
        this.focussedTree = focussedTree;
        this.focussedChildTree = focussedChildTree;

        super.paint(null);

        // pause render temporally
        if (this.iterationDelay > 10) {
            try {
                Thread.sleep(this.iterationDelay);

            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
    }

    public void generateAndAnimateTreeFromDataList(ArrayList<T> elements, DataListDisplayer listDisplayer) {

        int iparent = 0;
        int ileft, iright;
        int size = elements.size();

        Stack<BinaryTree> herarchy = new Stack();
        Stack<Integer> states = new Stack();
        int state = 0;
        boolean goBack;

        BinaryTree<T> parent = new BinaryTree(elements.get(0));
        BinaryTree<T> root = parent;
        BinaryTree<T> child;

        // set main rendered tree
        this.paintedTree = root;

        int savedDelay = 0;
        // point root element
        if (listDisplayer != null) {
            // save list displayer delay value
            savedDelay = this.iterationDelay;
            this.iterationDelay = 0;

            listDisplayer.pointElement1(iparent, "PARENT", null, null);
            listDisplayer.paintList();
        }

        do {

            //System.out.print(iparent + " state: "+state+" ");
            this.paintTree(parent);

            // restore backward FLAG
            goBack = false;

            // compute childs alocations
            ileft = iparent * 2 + 1;
            iright = iparent * 2 + 2;

            switch (state) {
                case 0:
                    // serve left child
                    if (ileft < size) {

                        // point left child on list displayer
                        if (listDisplayer != null) {
                            listDisplayer.pointElement2(ileft, "L", null, null);
                            listDisplayer.paintList();
                        }

                        // add left children tree to parent
                        child = new BinaryTree(elements.get(ileft));
                        parent.setLeft(child);

                        // store next state and parent tree on stack
                        herarchy.push(parent);
                        states.push(++state);

                        // move to new tree and point it index on list
                        parent = child;
                        iparent = ileft;
                        state = 0;

                    } else {
                        goBack = true;
                    }
                    break;

                case 1:
                    // serve right children
                    if (iright < size) {

                        // point right child on list displayer
                        if (listDisplayer != null) {
                            listDisplayer.pointElement2(iright, "R", null, null);
                            listDisplayer.paintList();
                        }

                        // add left children tree to parent
                        child = new BinaryTree(elements.get(iright));
                        parent.setRight(child);

                        // store next state and parent tree on stack
                        herarchy.push(parent);
                        states.push(++state);

                        // move to new tree and point it index on list
                        parent = child;
                        iparent = iright;
                        state = 0;

                    } else {
                        goBack = true;
                    }
                    break;
                default:
                    goBack = true;
                    break;
            }

            if (goBack) {
                //System.out.println("BACK");

                if (!herarchy.isEmpty()) {
                    // restore last parent state
                    parent = herarchy.pop();
                    state = states.pop();

                    // go to parent position
                    iparent = (iparent - 1) / 2;

                } else {
                    parent = null;
                }

            }

        } while (parent != null);

        // restore list displayer delay
        if (listDisplayer != null) {
            listDisplayer.pointElement1(-1, null, null, null);
            listDisplayer.pointElement2(-1, null, null, null);
            listDisplayer.paintList();

            this.iterationDelay = savedDelay;
        }

    }

    @Override
    protected void renderize(Graphics g, int width, int height) {

        int avaliableSpace = width;
        int depth = 0;
        int chases = 0;
        int fontSize = 0;

        // clear dispaly area
        g.setColor(this.backColor);
        g.fillRect(0, 0, avaliableSpace, height);

        // paint tree begin on top-center position
        if (this.paintedTree != null) {

            // get tree propertes
            depth = this.paintedTree.getDepth();
            chases = this.paintedTree.getChases();

            // compute text font metrics
            fontSize = (avaliableSpace < height ? avaliableSpace : height) / chases / 4;

            // limite font size
            if (fontSize > 25) {
                fontSize = 25;
            }

            // generate new font metrics
            this.textFont = new Font("monospaced", Font.BOLD, fontSize);
            this.textFontMetrics = g.getFontMetrics(textFont);
            this.rootFontMetrics = g.getFontMetrics(rootFont);
            this.steepHeight = height / depth;

            // reduce avaliable space to middle
            avaliableSpace /= 2;
            this.paintTreeElement(g, paintedTree, avaliableSpace, avaliableSpace, 25);

        }

    }

    private IBinaryTree selectTreeElement(IBinaryTree tree, int mouseX, int mouseY, int avaliableSpace, int x, int y) {

        boolean isPointed;

        IBinaryTree selectedTree = null;
        IBinaryTree left = tree.getLeft();
        IBinaryTree right = tree.getRight();
        String text = tree.getData() != null ? tree.getData().toString() : "NULL";

        // compute element shape dimensions
        FontMetrics usedFontMetrics = tree != this.paintedTree ? this.textFontMetrics : this.rootFontMetrics;
        int shapeHeight = usedFontMetrics.getFont().getSize();
        int shapeWidth = text.length() > 1 ? usedFontMetrics.stringWidth(text) : shapeHeight * 2;

        // compare shapes colitions
        isPointed = Math.abs(mouseX - x) <= shapeWidth / 2;
        isPointed &= Math.abs(mouseY - y) <= shapeHeight;

        if (isPointed) {
            selectedTree = tree;

        } else {

            // reduce avaliable space
            avaliableSpace /= 2;

            // try to select a right children
            if (right != null) {
                if (left == null && this.isSpaceSaved) {
                    selectedTree = this.selectTreeElement(right, mouseX, mouseY, avaliableSpace * 2, x,
                            y + this.steepHeight);
                } else {
                    selectedTree = this.selectTreeElement(right, mouseX, mouseY, avaliableSpace, x + avaliableSpace,
                            y + this.steepHeight);
                }
            }

            // try to select a left children
            if (selectedTree == null && left != null) {
                if (right == null && this.isSpaceSaved) {
                    selectedTree = this.selectTreeElement(left, mouseX, mouseY, avaliableSpace * 2, x,
                            y + this.steepHeight);
                } else {
                    selectedTree = this.selectTreeElement(left, mouseX, mouseY, avaliableSpace, x - avaliableSpace,
                            y + this.steepHeight);
                }
            }

        }

        return selectedTree;
    }

    private void paintTreeElement(Graphics g, IBinaryTree tree, int avaliableSpace, int x, int y) {

        boolean isFocused = tree == this.focussedTree;
        ElementStyle elementStyle = null;

        IBinaryTree left = tree.getLeft();
        IBinaryTree right = tree.getRight();
        String text = tree.getData() != null ? tree.getData().toString() : "NULL";

        // compute element shape dimensions
        FontMetrics usedFontMetrics = tree != this.paintedTree ? this.textFontMetrics : this.rootFontMetrics;
        int shapeHeight = usedFontMetrics.getFont().getSize();
        int shapeWidth = text.length() > 0 ? usedFontMetrics.stringWidth(text) : shapeHeight;

        // reduce avaliable space of next tree level
        avaliableSpace /= 2;

        // paint left children
        if (left != null) {

            // draw left link
            if (isFocused) {
                if (right == this.focussedChildTree) {
                    g.setColor(this.focussedChildStyle.borderColor);
                } else {
                    g.setColor(this.focussedStyle.borderColor);
                }
            } else {
                g.setColor(this.defaultStyle.borderColor);
            }

            if (right == null && this.isSpaceSaved) {
                g.drawLine(x, y, x, y + this.steepHeight);
                g.setFont(this.textFont);
                g.drawString("L", x - shapeHeight, y + this.steepHeight / 2);
                this.paintTreeElement(g, left, avaliableSpace * 2, x, y + this.steepHeight);

            } else {
                g.drawLine(x, y, x - avaliableSpace, y + this.steepHeight);
                this.paintTreeElement(g, left, avaliableSpace, x - avaliableSpace, y + this.steepHeight);

            }

        }

        // paint right children
        if (right != null) {

            // draw right link
            if (isFocused) {
                if (right == this.focussedChildTree) {
                    g.setColor(this.focussedChildStyle.borderColor);
                } else {
                    g.setColor(this.focussedStyle.borderColor);
                }
            } else {
                g.setColor(this.defaultStyle.borderColor);
            }

            if (left == null && this.isSpaceSaved) {
                g.drawLine(x, y, x, y + this.steepHeight);
                g.setFont(this.textFont);
                g.drawString("R", x + shapeHeight, y + this.steepHeight / 2);
                this.paintTreeElement(g, right, avaliableSpace * 2, x, y + this.steepHeight);

            } else {
                g.drawLine(x, y, x + avaliableSpace, y + this.steepHeight);
                this.paintTreeElement(g, right, avaliableSpace, x + avaliableSpace, y + this.steepHeight);

            }

        }

        if (tree instanceof IStyleable) {
            // get customized element style
            elementStyle = ((IStyleable) tree).getStyle(isFocused);

        }

        if (elementStyle == null) {
            // select applicable element style
            if (!isFocused) {
                elementStyle = this.defaultStyle;
            } else {
                elementStyle = this.focussedStyle;
            }

        }

        // select and draw defined element shape
        switch (elementStyle.shapeType) {
            case ElementStyle.SHAPE_QUAD:
                g.setColor(elementStyle.shapeColor);
                g.fillRect(x - shapeWidth / 2 - shapeHeight / 4, y - shapeHeight / 2 - shapeHeight / 4,
                        shapeWidth + shapeHeight / 2, shapeHeight + shapeHeight / 2);
                break;

            case ElementStyle.SHAPE_CIRCLE:
                g.setColor(elementStyle.shapeColor);
                g.fillArc(x - shapeHeight, y - shapeHeight, shapeHeight * 2, shapeHeight * 2, 0, 360);
                break;

            case ElementStyle.SHAPE_DYAMOND:

                // configure poligons points coords
                this.poligonXpoints[0] = x - shapeHeight;
                this.poligonYpoints[0] = y;

                this.poligonXpoints[1] = x;
                this.poligonYpoints[1] = y + shapeHeight;

                this.poligonXpoints[2] = x + shapeHeight;
                this.poligonYpoints[2] = y;

                this.poligonXpoints[3] = x;
                this.poligonYpoints[3] = y - shapeHeight;

                g.setColor(elementStyle.shapeColor);
                g.fillPolygon(this.poligonXpoints, this.poligonYpoints, 4);

                break;

        }

        // draw tree data as text
        if (usedFontMetrics.getFont().getSize() >= 5) {
            g.setFont(usedFontMetrics.getFont());
            g.setColor(elementStyle.textColor);
            g.drawString(text, x - shapeWidth / 2, y + shapeHeight / 3);
        }

    }

    private void buildPopUpMenu() {
        MenuItem item = null;

        this.menu = new PopupMenu("OPTIONS...");

        // add remove menu option
        item = new MenuItem("REMOVE");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedTree != null) {

                    // ask first, user remove confirmation
                    if (JOptionPane.showConfirmDialog(BinaryTreeDisplayer.this,
                            "YOU WANT TO REMOVE NODE: " + selectedTree.getData(), "DATOS", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {

                        // remove selected tree and repaint
                        paintedTree.removeTree(selectedTree);
                        paintTree();

                    }

                }
            }

        });
        this.menu.insert(item, 0);

        // add insert left children option
        item = new MenuItem("ADD LEFT CHILDREN");
        item.addActionListener(new ActionListener() {
            String parsedString;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (selectedTree != null && selectedTree.getLeft() == null) {

                    // get a node value descriptive text
                    parsedString = JOptionPane.showInputDialog("WRITE VALUE OF LEFT CHILDREN:", "");

                    if (parsedString != null) {

                        try {

                            // show warning message
                            if (parsedString.length() == 0) {
                                JOptionPane.showMessageDialog(BinaryTreeDisplayer.this,
                                        "INPUT EMPTY TEXT DATA\nIS USE NULL VALUE", "DATOS",
                                        JOptionPane.WARNING_MESSAGE);
                            }

                            // try to make left children tree and add to herarchy
                            selectedTree.setLeft(selectedTree.parse(parsedString));

                            // repaint modified tree displayer
                            repaint();

                        } catch (Exception exc) {
                            // show a error message
                            JOptionPane.showMessageDialog(BinaryTreeDisplayer.this,
                                    "CAN NOT ADDED LEFT NEW NODE BECAUSE:\n" + exc.getMessage(), "ERROR",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }

                }
            }

        });
        this.menu.insert(item, 1);

        // add insert right children option
        item = new MenuItem("ADD RIGHT CHILDREN");
        item.addActionListener(new ActionListener() {
            String parsedString;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (selectedTree != null && selectedTree.getRight() == null) {
                    parsedString = JOptionPane.showInputDialog("WRITE VALUE OF RIGHT CHILDREN:", "");

                    if (parsedString != null) {

                        try {

                            // show warning message
                            if (parsedString.length() == 0) {
                                JOptionPane.showMessageDialog(BinaryTreeDisplayer.this, "INPUT EMPTY TEXT DATA",
                                        "DATOS", JOptionPane.WARNING_MESSAGE);
                            }

                            // try to make right children tree and add to herarchy
                            selectedTree.setRight(selectedTree.parse(parsedString));

                            // repaint modified tree displayer
                            repaint();

                        } catch (Exception exc) {
                            // show a error message
                            JOptionPane.showMessageDialog(BinaryTreeDisplayer.this,
                                    "CAN NOT ADDED RIGHT NEW NODE BECAUSE:\n" + exc.getMessage(), "ERROR",
                                    JOptionPane.ERROR_MESSAGE);

                        }

                    }

                }
            }

        });
        this.menu.insert(item, 2);

        // add restore herarchy option
        item = new MenuItem("RESTORE VIEW");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                IBinaryTree tree;

                // get last tree on stack
                do {
                    tree = treeStack.pop();
                } while (!treeStack.isEmpty());

                // restore last stack state
                treeStack.push(tree);

                // paint last store tree
                paintSubTree(null);
            }

        });
        this.menu.insert(item, 3);

        // add export as image option
        item = new MenuItem("EXPORT AS IMAGE");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exportAsImage();
            }

        });
        this.menu.insert(item, 4);

        // add pop-up-menu to displayer
        this.add(this.menu);

    }

    private void showPopUpMenu(int x, int y) {
        boolean hasSelectedTree;

        if (!this.isMenuLocked) {
            hasSelectedTree = this.selectedTree != null;

            // enable or disable menu options
            menu.getItem(0).setEnabled(hasSelectedTree && this.selectedTree != this.paintedTree);
            menu.getItem(1).setEnabled(hasSelectedTree && this.selectedTree.getLeft() == null);
            menu.getItem(2).setEnabled(hasSelectedTree && this.selectedTree.getRight() == null);
            menu.getItem(3).setEnabled(!this.treeStack.isEmpty());
            menu.getItem(4).setEnabled(!hasSelectedTree);

            // show pop-up-menu on user mouse coords
            menu.show(this, x, y);

        }
    }

    public void setMenuLock(boolean isMenuLocked) {
        this.isMenuLocked = isMenuLocked;
    }

    public boolean isMenuLock() {
        return this.isMenuLocked;
    }

    public void setDelay(int iterationDelay) {
        // define draw interval to wait
        this.iterationDelay = iterationDelay > 10 ? iterationDelay : 10;

    }

    public int getDelay() {
        return this.iterationDelay;
    }

    public void setSpaceSave(boolean isSpaceSaved) {
        // enable or disable children LEFT or RIGTH displacement 
        this.isSpaceSaved = isSpaceSaved;

    }

    public boolean isSpaceSaved() {
        return this.isSpaceSaved;
    }

    public void setBackgroundColor(Color backColor) {
        this.backColor = backColor;

    }

    public void setDefaultStyle(Color... colors) {
        ElementStyle modified = this.defaultStyle;

        if (colors.length > 0) {
            modified.shapeColor = colors[0];
        }
        if (colors.length > 1) {
            modified.textColor = colors[1];
        }
        if (colors.length > 2) {
            modified.borderColor = colors[2];
        }

    }

    public void setFocussedStyle(Color... colors) {
        ElementStyle modified = this.focussedStyle;

        if (colors.length > 0) {
            modified.shapeColor = colors[0];
        }
        if (colors.length > 1) {
            modified.textColor = colors[1];
        }
        if (colors.length > 2) {
            modified.borderColor = colors[2];
        }

    }

    public void setFocussedChildStyle(Color... colors) {
        ElementStyle modified = this.focussedChildStyle;

        if (colors.length > 0) {
            modified.shapeColor = colors[0];
        }
        if (colors.length > 1) {
            modified.textColor = colors[1];
        }
        if (colors.length > 2) {
            modified.borderColor = colors[2];
        }

    }

    public void setTreeShape(int shapeType) {
        this.defaultStyle.shapeType = shapeType;
        this.focussedStyle.shapeType = shapeType;
        this.focussedChildStyle.shapeType = shapeType;

    }

}
