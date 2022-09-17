package com.yoeld99apps.axegrl.components;

public class IlegalContainerException extends Exception {

    public IlegalContainerException(String itemName) {
        System.err.println("Can not asign a non Item as parent container of item: "+itemName);
    }
    
}
