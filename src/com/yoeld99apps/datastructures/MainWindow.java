package com.yoeld99apps.datastructures;

import java.awt.*;
import javax.swing.*;
import com.yoeld99apps.toolsTDAs.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private final int WIDTH = 700;
    private final int HEIGHT = 600;

    private final Container container;
    private final BinaryTreeDisplayer<Integer> treeDisplayer;
    private final DataListDisplayer<Integer> listDisplayer;
    private final DataGraphDisplayer graphDisplayer;
    
    private final JComboBox<String> selector1;
    private final JButton start;

    private ArrayList<Integer> valuesI;

    private final ElementStyle OFFSET = new ElementStyle(Color.GREEN, Color.YELLOW, Color.WHITE, -1);
    private final ElementStyle FOCUSED_I = new ElementStyle(Color.YELLOW, Color.BLUE, Color.BLUE, ElementStyle.SHAPE_CIRCLE);
    private final ElementStyle FOCUSED_J = new ElementStyle(Color.RED, Color.WHITE, Color.WHITE, ElementStyle.SHAPE_CIRCLE);
    private final ElementStyle SWAPEDS = new ElementStyle(Color.CYAN, Color.BLACK, Color.BLUE, ElementStyle.SHAPE_QUAD);
    
    private KeyListener onkeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            graphDisplayer.onKeyCommand(e.getKeyCode());
            
        }

    };

    public MainWindow() {

        // cuztominze window
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("DATA STRUCTURES AND ALGORITHMS ");

        // make main elements container
        this.container = new Container();
        this.container.setBounds(0, 0, WIDTH, HEIGHT);
        this.setContentPane(this.container);

        // make data structure displayers
        this.treeDisplayer = new BinaryTreeDisplayer();
        this.treeDisplayer.setBounds(20, 50, WIDTH - 40, 250);
        
        this.graphDisplayer = new DataGraphDisplayer();
        this.graphDisplayer.setBounds(20, 50, WIDTH - 40, 250);
        
        this.listDisplayer = new DataListDisplayer();
        this.listDisplayer.setBounds(20, 310, WIDTH - 40, 150);
        
        // make algoritm selector
        this.selector1 = new JComboBox<>();
        this.selector1.setBounds(20, 15, 200, 30);
        this.selector1.addItem("BUBLE SORT");
        this.selector1.addItem("INSERTION SORT");
        this.selector1.addItem("SELECTION SORT");
        this.selector1.addItem("QUICK SORT");
        this.selector1.addItem("HEAP SORT");
        this.selector1.addItem("SHELL SORT");

        this.start = new JButton("MAKE DATA LIST");
        this.start.setBounds(530, 15, 150, 30);
        this.start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                start.setEnabled(false);
                buildList();
                
                // select used method
                switch (selector1.getSelectedIndex()) {
                    case 0:
                        bubbleSort();
                        break;
                    case 1:
                        insertionSort();
                        break;
                    case 2:
                        selectionSort();
                        break;
                    case 3:
                        quickSort();
                        break;
                    case 4:
                        heapSort();
                        break;
                    case 5:
                        sellSort();
                        break;
                }
                
                //buildTree();
                buildGraph();
                start.setEnabled(true);
            }
        });
        
        super.addKeyListener(onkeyListener);
        
        this.container.add(this.graphDisplayer);
        this.container.add(this.listDisplayer);
        this.container.add(this.selector1);
        this.container.add(this.start);

        // configure and show window
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setVisible(true);

    }

    public void buildList() {

        // prompt values
        String data = JOptionPane.showInputDialog("write a list: \n value1,value2,value2,...");

        this.valuesI = new ArrayList<>();

        // configure displayer delay
        this.listDisplayer.setDelay(150);
        this.listDisplayer.setList(this.valuesI);

        if (data != null) {
            String[] valuesS = data.split(",");

            // parse any values
            for (String valuesS1 : valuesS) {
                valuesI.add(Integer.parseInt(valuesS1));
                this.listDisplayer.paintList();
            }

        } else {
            int n = (int) Math.round(Math.random() * 10) + 10;

            // randomize n values
            for (int i = 0; i < n; i++) {
                valuesI.add((int) Math.round(Math.random() * 40) - 20);
                this.listDisplayer.paintList();
            }
        }

    }

    public void buildTree() {

        // cnfigure reder delayed
        if (this.valuesI.size() > 0) {
            this.listDisplayer.setDelay(10000 / this.valuesI.size());
        }

        this.treeDisplayer.generateAndAnimateTreeFromDataList(this.valuesI, this.listDisplayer);

        BinaryTree<Integer> tree = (BinaryTree<Integer>) treeDisplayer.getTree();

    }
    
    public void buildGraph(){
        
        DataGraph<Integer> igraph = new DataGraph(true);
        
        // add any nodes
        for(int i : this.valuesI){
            igraph.add(i);
            
        }
        
        for(int i : this.valuesI){
            for(int j = 0, s = (int) Math.round(Math.random() * 5); j < s; j++){
                igraph.conect(i, this.valuesI.get((int) Math.round(Math.random() * (this.valuesI.size() - 1))));
            }
        }
        
        this.graphDisplayer.setGraph(igraph);
        
    }
    
    public void bubbleSort(){
        
    }
    
    public void insertionSort(){
        
    }
    
    public void selectionSort(){
        
    }
    
    public void quickSort(){
        
    }
    
    public void heapSort(){
        
    }
    
    public void sellSort() {

        int offset = 0;
        int i;
        int j;

        int vi;
        int vj;

        int size = this.valuesI.size();
        int steeps = size / 2;

        boolean end = false;
        boolean hasSwaped;

        // cnfigure reder delayed
        if (size > 0) {
            this.listDisplayer.setDelay(10000 / size);
        }

        while (!end) {
            i = offset;
            j = i + steeps;

            hasSwaped = false;
            this.listDisplayer.pointElement3(offset, "OFFSET", null, OFFSET);

            // run any steeps
            while (j < size) {
                vi = this.valuesI.get(i);
                vj = this.valuesI.get(j);

                // show pointers
                this.listDisplayer.pointElement1(i, "i", FOCUSED_I, null);
                this.listDisplayer.pointElement2(j, "i + " + steeps, FOCUSED_J, null);
                this.listDisplayer.paintList();

                if (vi > vj) {
                    // show swap stylisheds pointers
                    this.listDisplayer.pointElement1(i, "i", SWAPEDS, null);
                    this.listDisplayer.pointElement2(j, "i + " + steeps, SWAPEDS, null);
                    this.listDisplayer.paintList();

                    // swap elements
                    this.valuesI.set(i, vj);
                    this.valuesI.set(j, vi);

                    // show swapeds pointers
                    this.listDisplayer.pointElement1(i, "i", FOCUSED_I, null);
                    this.listDisplayer.pointElement2(j, "i + " + steeps, FOCUSED_J, null);
                    this.listDisplayer.paintList();

                    hasSwaped |= true;
                }

                // jump to nexts
                i += steeps;
                j += steeps;
            }

            // show last i index
            this.listDisplayer.pointElement2(i, "END", FOCUSED_J, null);
            this.listDisplayer.pointElement1(-1, null, null, null);
            this.listDisplayer.paintList();

            // controle offset pointer
            if (hasSwaped) {
                offset++;
            } else {
                offset = 0;

                if (steeps > 2) {
                    steeps--;
                } else {
                    end = true;
                }
            }

        }

        // unpoint any elements
        this.listDisplayer.pointElement1(-1, null, null, null);
        this.listDisplayer.pointElement2(-1, null, null, null);
        this.listDisplayer.pointElement3(-1, null, null, null);
        this.listDisplayer.paintList();

    }
    
    
}
