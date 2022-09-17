package com.yoeld99apps.toolsTDAs;

public interface IBinaryTree {
	
	public Object getData();
	public void setLeft(IBinaryTree left);
	public void setRight(IBinaryTree right);
	public IBinaryTree getLeft();
	public IBinaryTree getRight();
	public int getChases();
	public int getDepth();
	public boolean removeTree(IBinaryTree tree);
	public IBinaryTree parse(String description);
	
}
