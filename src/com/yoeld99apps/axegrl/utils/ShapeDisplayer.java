package com.yoeld99apps.axegrl.utils;

import com.yoeld99apps.axegrl.components.Item;
import java.awt.Color;
import java.awt.Graphics2D;

public class ShapeDisplayer extends Item {

    public ShapeDisplayer() {
        super();
        setSize(500, 300);
        initialize();
    }

    @Override
    public void onPaint(Graphics2D g) {

        double lineA[] = {0, 20, width, height};
        double lineB[] = {50, 50, 100, 0};
        double point[];

        g.setColor(Color.RED);
        g.drawLine((int) lineA[0], (int) lineA[1], (int) lineA[2], (int) lineA[3]);
        
        g.setColor(Color.BLUE);
        g.drawLine((int) lineB[0], (int) lineB[1], (int) lineB[2], (int) lineB[3]);

        point = computeSegementIntersection(lineA, lineB);

        if (point != null) {
            g.setColor(contain(lineB, point, 0.1) ? Color.GREEN : Color.RED);
            g.drawRect((int) point[0] - 5, (int) point[1] - 5, 10, 10);
        }

    }
    
    public static double[] computeSegementIntersection(double[] l1, double[] l2) {
        double[] point = {0, 0};

        double dxA = l1[2] - l1[0];
        double dxB = l2[2] - l2[0];
        double dyA, dyB;
        double mA, nA, mB, nB;

        if (dxA == 0 && dxB == 0) {
            point[0] = l1[0];
            point[1] = l1[1];
        } else if (dxB == 0) {

            dyA = l1[3] - l1[1];
            mA = dyA / dxA;
            nA = -mA * l1[0] + l1[1];

            point[0] = l2[0];
            point[1] = mA * l2[0] + nA;

        } else if (dxB == 0) {

            dyB = l2[3] - l2[1];
            mB = dyB / dxB;
            nB = -mB * l2[0] + l2[1];

            point[0] = l1[0];
            point[1] = mB * l1[0] + nB;

        } else {
            dyA = l1[3] - l1[1];
            dyB = l2[3] - l2[1];

            mA = dyA / dxA;
            nA = -mA * l1[0] + l1[1];

            mB = dyB / dxB;
            nB = -mB * l2[0] + l2[1];

            if (mA != mB) {
                point = new double[2];

                point[0] = (nA - nB) / (mB - mA);
                point[1] = mA * point[0] + nA;

            } else {
                point = null;
            }

        }

        return point;
    }

    public static boolean contain(double[] l, double[] p, double r) {
        
        double dxAB = Math.abs(l[0] - l[2]); 
        double dxAP = Math.abs(l[0] - p[0]); 
        double dxBP = Math.abs(l[2] - p[0]);
        
        double dyAB = Math.abs(l[1] - l[3]);
        double dyAP = Math.abs(l[1] - p[1]); 
        double dyBP = Math.abs(l[3] - p[1]); 
        
        double rX = Math.abs(dxAB - dxAP - dxBP); //dx
        double rY = Math.abs(dyAB - dyAP - dyBP); //dy
        
        return rX <= r && rY <= r;
    }
    
    
    private String toString(double[] d){
        StringBuilder build = new StringBuilder();
        int i = 0;
        
        for(double v : d){
            build.append((i++ % 2 != 0 ? "x: " : "y: " )+v);
        }
        
        return build.toString();
    }

}
