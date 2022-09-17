package com.yoeld99apps.axegrl.displayer;

import com.yoeld99apps.axegrl.painters.ShapePainter;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author yoeldcd
 * 
 * This class is an JDialog used to modify
 * the selected style in a list with elements
 * of type ShapePainter.Style.
 * 
 */
public class DShapeStyleEditorDialog extends javax.swing.JDialog {
    
    private final JFileChooser imageFileChooser;
    private final FileFilter extensionFilter;

    // editable values
    private final DShape previewShape;
    private int currentStyleIndex;
    private File lastImageFile;
    private ShapePainter.Style previewStyle;

    // data colections
    private final HashMap<String, Integer> geometryTypes;
    private final HashMap<String, Integer> textAlignementTypes;
    private final ArrayList<String> stylesNames;
    private final ArrayList<ShapePainter.Style> styles;
    private final ArrayList<ShapePainter.Style> loockedStyles;

    public DShapeStyleEditorDialog(ArrayList<ShapePainter.Style> styles) {
        super();

        // configure GUI Components
        setModal(true);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();

        this.styles = styles;
        this.loockedStyles = new ArrayList<>();
        this.stylesNames = new ArrayList<>();
        
        // define avaliables border geometrys config's
        this.geometryTypes = new HashMap<>();
        defineAvaliablesGeometries();

        // define avaliables text alignement config's
        this.textAlignementTypes = new HashMap<>();
        defineAvaliablesAlignments();

        // make and configure image file selector
        imageFileChooser = new JFileChooser();
        extensionFilter = new FileNameExtensionFilter("Image", "png", "jpg", "gif");
        
        imageFileChooser.setDialogTitle("Select image file to export.");
        imageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        imageFileChooser.setFileFilter(extensionFilter);
        imageFileChooser.setCurrentDirectory(lastImageFile);
        
        // add preview shape layout
        previewShape = new DNode("preview");
        previewShape.setLocation(0, 0);
        previewShape.setSize(imageLayout.getWidth() - 5, imageLayout.getHeight() - 5);
        previewShape.setVisible(true);
        imageLayout.add(previewShape);

        // add choiceable items
        geometryTypes.forEach((s, i) -> shapeGeometry.addItem(s));
        textAlignementTypes.forEach((s, i) -> textAlignement.addItem(s));
        
    }

    private void defineAvaliablesGeometries() {
        this.geometryTypes.put("QUAD", ShapePainter.Style.GEOMETRY_QUAD);
        this.geometryTypes.put("RECTANGLE", ShapePainter.Style.GEOMETRY_RECTANGLE);
        this.geometryTypes.put("DYAMOND", ShapePainter.Style.GEOMETRY_DYAMOND);
        this.geometryTypes.put("CIRCLE", ShapePainter.Style.GEOMETRY_CIRCLE);
        this.geometryTypes.put("BOX", ShapePainter.Style.GEOMETRY_BOX);
        this.geometryTypes.put("FIXED IMAGE", ShapePainter.Style.GEOMETRY_IMAGE);
        this.geometryTypes.put("FREE", ShapePainter.Style.GEOMETRY_FREE);

    }

    private void defineAvaliablesAlignments() {
        this.textAlignementTypes.put("OVER", ShapePainter.Style.TEXT_OVER);
        this.textAlignementTypes.put("UP", ShapePainter.Style.TEXT_UP);
        this.textAlignementTypes.put("CENTERED", ShapePainter.Style.TEXT_CENTER);
        this.textAlignementTypes.put("DOWN", ShapePainter.Style.TEXT_DOWN);
        this.textAlignementTypes.put("LOWER", ShapePainter.Style.TEXT_LOWER);
        
    }
    
    public void loockStyle(ShapePainter.Style lockedStyle) {
        if(loockedStyles.indexOf(lockedStyle) == -1){
           loockedStyles.add(lockedStyle); 
        }
    }
    
    public void unloockStyle(ShapePainter.Style lockedStyle) {
        if(loockedStyles.indexOf(lockedStyle) != -1){
           loockedStyles.remove(lockedStyle); 
        }
    }
    
    public void showDialog() {
        updateStylesList();
        previewStyle(-1);
        setVisible(true);
    }

    public void hideDialog() {
        setVisible(false);
    }
    
    public int getSelectedStyleIndex() {
        return stylesList.getSelectedIndex();
    }

    private void previewStyle(int styleIndex) {
        if (styleIndex >= 0 && styleIndex < styles.size()) {
            if(currentStyleIndex == -1){
                enableControls();
            }
            
            // make a preview copy of selected style
            previewStyle = styles.get(styleIndex).duplicate();
            previewShape.setDisableStyle(previewStyle);
            
            currentStyleIndex = styleIndex;
            stylesList.setSelectedIndex(currentStyleIndex);
            
            // define text field's values
            shapeName.setText(previewStyle.getName());
            imagePath.setText(previewStyle.getImagePath());
            
            // define color field's values
            borderColorButton.setBackground(previewStyle.getBorderColor());
            textColorButton.setBackground(previewStyle.getShapeColor());
            shapeColorButton.setBackground(previewStyle.getShapeColor());
            
            // define seting field's values
            shapeGeometry.setSelectedItem(getMapKey(geometryTypes, previewStyle.getGeometryType()));
            textAlignement.setSelectedItem(getMapKey(textAlignementTypes, previewStyle.getTextAlignement()));
            textSize.setValue(previewStyle.getTextFont().getSize());
            
            // disable remove option
            if(loockedStyles.indexOf(styles.get(styleIndex)) != -1){
                removeButton.setVisible(false);
            }
            
        } else {
            currentStyleIndex = -1;
            disableControls();
        }
    }
    
    private void enableControls(){
        shapeColorButton.setVisible(true);
        borderColorButton.setVisible(true);
        textColorButton.setVisible(true);
        removeimageButton.setVisible(true);
        removeButton.setVisible(true);
        
        shapeGeometry.setVisible(true);
        textAlignement.setVisible(true);
        textSize.setVisible(true);
        
        imageLayout.setVisible(true);
    }
    
    private void disableControls(){
        shapeColorButton.setVisible(false);
        borderColorButton.setVisible(false);
        textColorButton.setVisible(false);
        removeimageButton.setVisible(false);
        removeButton.setVisible(false);
        
        shapeGeometry.setVisible(false);
        textAlignement.setVisible(false);
        textSize.setVisible(false);
        
        imageLayout.setVisible(false);
        
    }
    
    private void renameStyle(String name) {
        previewStyle.setName(name);
    }

    private void selectImage() {

        if (lastImageFile == null) {
            lastImageFile = new File(".");
        }

        // show file selection dialog
        imageFileChooser.setCurrentDirectory(lastImageFile);
        int result = imageFileChooser.showDialog(this, "Select Shape Image");

        // change style image
        if (result == JFileChooser.APPROVE_OPTION) {
            lastImageFile = imageFileChooser.getSelectedFile();

            previewStyle.setImageOnPath(lastImageFile.getPath());
            imagePath.setText(previewStyle.getImagePath());
            previewShape.update();

        }

    }

    private void removeImage() {
        previewStyle.setImage(null);
        previewShape.update();
    }

    private void selectBackgroundColor() {
        Color output = JColorChooser.showDialog(this, "Select color.", previewStyle.getShapeColor());

        if (output != null) {
            previewStyle.setShapeColor(output);
            shapeColorButton.setBackground(output);
            previewShape.update();
        }
    }

    private void selectTextColor() {
        Color output = JColorChooser.showDialog(this, "Select color.", previewStyle.getTextColor());

        if (output != null) {
            previewStyle.setTextColor(output);
            textColorButton.setBackground(output);
            previewShape.update();
        }
    }

    private void selectBorderColor() {
        Color output = JColorChooser.showDialog(this, "Select color.", previewStyle.getBorderColor());

        if (output != null) {
            previewStyle.setBorderColor(output);
            borderColorButton.setBackground(output);
            previewShape.update();
        }
    }

    private void selectGeometry(String geomName) {
        if (previewStyle == null) {
            return;
        }

        // redefine shape geometry
        previewStyle.setGeometryType(geometryTypes.get(geomName));
        previewShape.update();
    }

    private void selectAlignmenet(String alignName) {
        if (previewStyle == null) {
            return;
        }

        // alocate shape text on new position
        previewStyle.setTextAlignement(textAlignementTypes.get(alignName));
        previewShape.update();
    }

    private void selectFontSize(float value) {
        // resize shape text
        previewStyle.setTextFontSize(value);
        previewShape.update();
    }

    private void addNewStyle() {
        
        // add a new style instance to list
        styles.add(new ShapePainter.Style());
        updateStylesList();
        previewStyle(styles.size() - 1);
        
    }
    
    private void removeStyle(){
           
        // remove current selected style if is unloock
        styles.remove(currentStyleIndex);
        updateStylesList();
        previewStyle(-1);
           
        
    }
    
    private void resetChanges(){
        // restore original style rules to preview
        styles.get(currentStyleIndex).copyStyleRulesTo(previewStyle);
    }
    
    private void saveChanges() {
        // save current style config into original style instance
        previewStyle.copyStyleRulesTo(styles.get(currentStyleIndex));
        updateStylesList();
        previewStyle(currentStyleIndex);
    }
    
    // Miscellaneous methods
    private void updateStylesList() {

        stylesNames.clear();

        // get any styles names of index
        for (ShapePainter.Style style : styles) {
            if(loockedStyles.indexOf(style) == -1){
                stylesNames.add(style.getName());
            } else {
                stylesNames.add(style.getName()+" (LOOCK)");
            }
        }
        
        stylesList.setListData((String[]) stylesNames.toArray(new String[stylesNames.size()]));

    }

    private String getMapKey(HashMap<String, Integer> map, Integer value) {

        // search a first HashMap entry to math with value
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return "null";
    }
    
    
    
    //
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stylesList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        shapeName = new javax.swing.JTextField();
        shapeGeometry = new javax.swing.JComboBox<>();
        borderColorButton = new javax.swing.JButton();
        shapeColorButton = new javax.swing.JButton();
        textSize = new javax.swing.JSpinner();
        textColorButton = new javax.swing.JButton();
        textAlignement = new javax.swing.JComboBox<>();
        addStyleButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        imageLayout = new javax.swing.JPanel();
        removeimageButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        imagePath = new javax.swing.JLabel();
        applyButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        stylesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                stylesListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(stylesList);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 28, 178, 372));

        jLabel1.setText("Select Style");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 6, 134, -1));

        jLabel2.setText("Name:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 6, -1, -1));

        jLabel3.setText("Border:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 74, -1, -1));

        jLabel5.setText("Shape");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 147, -1, -1));

        jLabel6.setText("Text:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 331, -1, -1));

        shapeName.setToolTipText("");
        shapeName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                shapeNameKeyReleased(evt);
            }
        });
        jPanel1.add(shapeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 28, 206, -1));

        shapeGeometry.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                shapeGeometryItemStateChanged(evt);
            }
        });
        jPanel1.add(shapeGeometry, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 102, 137, -1));

        borderColorButton.setText(" ");
        borderColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borderColorButtonActionPerformed(evt);
            }
        });
        jPanel1.add(borderColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 101, 57, -1));

        shapeColorButton.setText(" ");
        shapeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapeColorButtonActionPerformed(evt);
            }
        });
        jPanel1.add(shapeColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 230, 57, -1));

        textSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                textSizeStateChanged(evt);
            }
        });
        jPanel1.add(textSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 353, 131, -1));

        textColorButton.setText(" ");
        textColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textColorButtonActionPerformed(evt);
            }
        });
        jPanel1.add(textColorButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 353, 57, -1));

        textAlignement.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                textAlignementItemStateChanged(evt);
            }
        });
        jPanel1.add(textAlignement, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 415, 212, -1));

        addStyleButton.setText("Add Style");
        addStyleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStyleButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addStyleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 400, 150, -1));

        jLabel4.setText("Alignement:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 393, -1, -1));

        imageLayout.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        imageLayout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imageLayout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageLayoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout imageLayoutLayout = new javax.swing.GroupLayout(imageLayout);
        imageLayout.setLayout(imageLayoutLayout);
        imageLayoutLayout.setHorizontalGroup(
            imageLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 140, Short.MAX_VALUE)
        );
        imageLayoutLayout.setVerticalGroup(
            imageLayoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );

        jPanel1.add(imageLayout, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, 140, 120));

        removeimageButton.setText("X");
        removeimageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeimageButtonActionPerformed(evt);
            }
        });
        jPanel1.add(removeimageButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 260, 57, -1));

        resetButton.setText("Reset");
        resetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetButtonMouseClicked(evt);
            }
        });
        jPanel1.add(resetButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 470, 70, -1));

        closeButton.setText("Close");
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeButtonMouseClicked(evt);
            }
        });
        jPanel1.add(closeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(361, 470, 71, -1));

        imagePath.setText("jLabel7");
        imagePath.setAutoscrolls(true);
        jPanel1.add(imagePath, new org.netbeans.lib.awtextra.AbsoluteConstraints(222, 303, 210, -1));

        applyButton.setText("Apply");
        applyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                applyButtonMouseClicked(evt);
            }
        });
        jPanel1.add(applyButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 470, 70, -1));

        removeButton.setText("Remove");
        removeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                removeButtonMouseClicked(evt);
            }
        });
        jPanel1.add(removeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 150, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeButtonMouseClicked
        hideDialog();
    }//GEN-LAST:event_closeButtonMouseClicked

    private void resetButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resetButtonMouseClicked
        resetChanges();
    }//GEN-LAST:event_resetButtonMouseClicked

    private void removeimageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeimageButtonActionPerformed
        removeImage();
    }//GEN-LAST:event_removeimageButtonActionPerformed

    private void imageLayoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageLayoutMouseClicked
        selectImage();
    }//GEN-LAST:event_imageLayoutMouseClicked

    private void addStyleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStyleButtonActionPerformed
        addNewStyle();
    }//GEN-LAST:event_addStyleButtonActionPerformed

    private void textAlignementItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_textAlignementItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            selectAlignmenet((String) evt.getItem());
        }
    }//GEN-LAST:event_textAlignementItemStateChanged

    private void textColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textColorButtonActionPerformed
        selectTextColor();
    }//GEN-LAST:event_textColorButtonActionPerformed

    private void shapeColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapeColorButtonActionPerformed
        selectBackgroundColor();
    }//GEN-LAST:event_shapeColorButtonActionPerformed

    private void borderColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borderColorButtonActionPerformed
        selectBorderColor();
    }//GEN-LAST:event_borderColorButtonActionPerformed

    private void shapeGeometryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_shapeGeometryItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            selectGeometry((String) evt.getItem());
        }
    }//GEN-LAST:event_shapeGeometryItemStateChanged

    private void shapeNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_shapeNameKeyReleased
        renameStyle(((JTextField) evt.getSource()).getText());
    }//GEN-LAST:event_shapeNameKeyReleased

    private void stylesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_stylesListValueChanged
        previewStyle(stylesList.getSelectedIndex());
    }//GEN-LAST:event_stylesListValueChanged

    private void textSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_textSizeStateChanged
        selectFontSize(Math.abs(Float.parseFloat("" + ((JSpinner) evt.getSource()).getValue())));
    }//GEN-LAST:event_textSizeStateChanged

    private void applyButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_applyButtonMouseClicked
        saveChanges();
    }//GEN-LAST:event_applyButtonMouseClicked

    private void removeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeButtonMouseClicked
        removeStyle();
    }//GEN-LAST:event_removeButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addStyleButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JButton borderColorButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel imageLayout;
    private javax.swing.JLabel imagePath;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removeimageButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton shapeColorButton;
    private javax.swing.JComboBox<String> shapeGeometry;
    private javax.swing.JTextField shapeName;
    private javax.swing.JList<String> stylesList;
    private javax.swing.JComboBox<String> textAlignement;
    private javax.swing.JButton textColorButton;
    private javax.swing.JSpinner textSize;
    // End of variables declaration//GEN-END:variables

}
