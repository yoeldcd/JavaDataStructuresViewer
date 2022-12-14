package com.yoeld99apps.axegrl.displayer;

import java.util.List;
import java.util.function.Function;

public class DListSelectionDialog<T> extends javax.swing.JDialog {

    private final List<T> values;
    private int selectionOffset;
    private int selectedIndex;

    public DListSelectionDialog(List<T> values) {
        super();
        setModal(true);
        setResizable(false);
        setLocationRelativeTo(null);
        initComponents();
        
        this.values = values;
        this.selectionOffset = 0;
    }

    public void updateListElements(Function<T, String> parser, int offset) {
        
        final String[] items = new String[values.size() - offset];
        selectionOffset = offset;
        
        // maping objects of T to String
        values.stream().skip(offset).map(parser).toList().toArray(items);
        selectionList.setListData(items);
    }

    public void showDialog(int defaultIndex) {
        defaultIndex -= selectionOffset;
        
        if (defaultIndex >= 0 & defaultIndex < values.size()) {
            selectionList.setSelectedIndex(defaultIndex);
        } else {
            selectionList.setSelectedIndex(-1);
        }
        
        setVisible(true);
    }

    public void hideDialog() {
        setVisible(false);
    }

    public boolean hasSelectedItem() {
        return selectedIndex != -1;
    }

    public T getSelectedItem() {
        return values.get(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        selectionList = new javax.swing.JList<>();
        cancelButton = new javax.swing.JButton();
        selectButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setViewportView(selectionList);

        cancelButton.setText("Cancel");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });

        selectButton.setText("Select");
        selectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectButtonMouseClicked(evt);
            }
        });

        jLabel1.setText("Select an Item:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addGap(103, 103, 103)
                        .addComponent(selectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelButton)
                    .addComponent(selectButton))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        // define not item selected
        selectedIndex = -1;
        setVisible(false);
    }//GEN-LAST:event_cancelButtonMouseClicked

    private void selectButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectButtonMouseClicked
        
        // define last item selected
        if(selectionList.getSelectedIndex() != -1){
            selectedIndex = selectionList.getSelectedIndex() + selectionOffset;
        } else {
            selectedIndex = -1;
        }
        
        setVisible(false);
    }//GEN-LAST:event_selectButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton selectButton;
    private javax.swing.JList<String> selectionList;
    // End of variables declaration//GEN-END:variables

}
