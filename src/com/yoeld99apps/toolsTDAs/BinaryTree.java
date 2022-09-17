package com.yoeld99apps.toolsTDAs;

import java.util.ArrayList;
import java.util.Stack;

public class BinaryTree<T extends Comparable> implements IBinaryTree {

    private T data;
    private BinaryTree<T> left;
    private BinaryTree<T> right;

    public BinaryTree() {
        this.data = null;
        this.left = null;
        this.right = null;

    }

    public BinaryTree(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    ;

	public void setData(T data) {
        this.data = data;
    }

    @Override
    public void setLeft(IBinaryTree left) {
        this.left = (BinaryTree<T>) left;
    }

    @Override
    public void setRight(IBinaryTree right) {
        this.right = (BinaryTree<T>) right;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    @Override
    public IBinaryTree getLeft() {
        return this.left;
    }

    @Override
    public IBinaryTree getRight() {
        return this.right;
    }

    @Override
    public int getDepth() {
        int depthLeft = 0;
        int depthRight = 0;
        int depth = 1;

        if (this.left != null) {
            depthLeft = this.left.getDepth();
        }

        if (this.right != null) {
            depthRight = this.right.getDepth();
        }

        depth += depthRight >= depthLeft ? depthRight : depthLeft;

        return depth;
    }

    @Override
    public int getChases() {
        int chases = 0;

        if (this.right == null && this.left == null) {
            chases = 1;

        } else {
            if (this.left != null) {
                chases += this.left.getChases();
            }

            if (this.right != null) {
                chases += this.right.getChases();
            }

        }

        return chases;
    }

    @Override
    public boolean removeTree(IBinaryTree removedTree) {
        boolean isRemoved = false;

        // try to remove on left side
        if (this.left != null) {
            if (this.left == removedTree) {
                this.left = null;
                isRemoved = true;
            } else {
                isRemoved = this.left.removeTree(removedTree);
            }
        }

        // try to remove on right side
        if (!isRemoved && this.right != null) {
            if (this.right == removedTree) {
                this.right = null;
                isRemoved = true;
            } else {
                isRemoved = this.right.removeTree(removedTree);
            }
        }

        return isRemoved;
    }

    @Override
    public IBinaryTree parse(String string) {
        return new BinaryTree((T) string);
    }
    
    public BinaryTree<T> generateFromDataList(ArrayList<T> elements) {

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

        do {

            //System.out.print(iparent + " state: "+state+" ");

            // restore backward FLAG
            goBack = false;

            // compute childs alocations
            ileft = iparent * 2 + 1;
            iright = iparent * 2 + 2;

            switch (state) {
                case 0:
                    // serve left child
                    if (ileft < size) {
                        //System.out.println("LEFT");
                        
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
                        //System.out.println("RIGHT");
                        
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

        return root;
    }
    
    
}
