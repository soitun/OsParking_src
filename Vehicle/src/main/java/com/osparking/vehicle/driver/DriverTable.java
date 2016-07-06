/* 
 * Copyright (C) 2015, 2016  Open Source Parking, Inc.(www.osparking.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.osparking.vehicle.driver;

import static com.osparking.global.CommonData.numberCellRenderer;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.vehicle.driver.ManageDrivers.driverTable;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.Globals.emptyLastRowPossible;
import static com.osparking.global.Globals.removeEmptyRow;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DRIVER_UPDATE_QUEST_P1;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DRIVER_UPDATE_QUEST_P2;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.DRIVER_UPDATE_QUEST_P3;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.MODIFY_DAILOGTITLE;
import com.osparking.global.names.ControlEnums.FormMode;
import com.osparking.global.names.InnoComboBoxItem;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import com.osparking.global.names.PComboBox;
import static com.osparking.vehicle.driver.ManageDrivers.getPrompter;
import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Open Source Parking Inc.
 */
public class DriverTable extends JTable {

    /**
     * table model modRow index for the driver currently being modified
     * 
     */
    static int modifyingRowM = -1;    
    
    ManageDrivers parent = null;
    
    public DriverTable(Object[][] rowData, Object[] columnNames, ManageDrivers parent) {
        this.parent = parent;
        setModel(new DriverTableModel(rowData, columnNames, parent));
        
        ((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);        
        
        for (DriverCol col : DriverCol.values()) {
            if (col == DriverCol.RowNo ||
                    col == DriverCol.BuildingNo ||
                    col == DriverCol.UnitNo) 
            {
                getColumnModel().getColumn(col.getNumVal()).setCellRenderer(numberCellRenderer);
            } else if (col != DriverCol.SEQ_NO) {
                getColumnModel().getColumn(col.getNumVal()).setCellRenderer(centerRenderer);
            }
        }
        
        setRowHeight(tableRowHeight);        
    }
    
    public boolean isCellEditable(int modRow, int column) {
        if ((parent.getFormMode() == FormMode.CreateMode ||
                parent.getFormMode() == FormMode.UpdateMode)
                && column != DriverCol.RowNo.getNumVal()) 
        {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean userWantsToUpdateRow(final int rowV, final int colV) {
        int rowM = driverTable.convertRowIndexToModel(rowV);

        parent.setUpdateMode(true);
        modifyingRowM = rowM;
        return true;
    } 
    
    public int askUserOnUpdate(String name, int vCount) {
        
        String optionMessage = DRIVER_UPDATE_QUEST_P1.getContent() + System.getProperty("line.separator") 
                + DRIVER_UPDATE_QUEST_P2.getContent() + name 
                + DRIVER_UPDATE_QUEST_P3.getContent() + vCount + ")";
        JOptionPane optionPane = new JOptionPane(optionMessage,
                JOptionPane.QUESTION_MESSAGE, 
                JOptionPane.YES_NO_OPTION, null, null, null); // options[0]);
        JDialog dialog = optionPane.createDialog(MODIFY_DAILOGTITLE.getContent());
        
        dialog.addKeyListener(new KeyListener () {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("key pressed");
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        Set<AWTKeyStroke> focusTraversalKeys = new HashSet<AWTKeyStroke>(dialog.getFocusTraversalKeys(0));
        focusTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.VK_UNDEFINED));
        focusTraversalKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_LEFT, KeyEvent.VK_UNDEFINED));
        dialog.setFocusTraversalKeys(0, focusTraversalKeys);
        dialog.setVisible(true);
        dialog.dispose();
        return (Integer) optionPane.getValue();
    }

    public TableCellEditor getCellEditor(int row, int column) {   
        int modRow = ManageDrivers.driverTable.convertRowIndexToModel(row);
        int modCol = ManageDrivers.driverTable.convertColumnIndexToModel(column);
        
        Object keyObj = ManageDrivers.driverTable.getModel().getValueAt(modRow, 
                DriverCol.SEQ_NO.getNumVal());
        
        // if new driver is being created and the cell is outside the new driver, then quit
        int tabSize = driverTable.getRowCount();
        if (parent.getFormMode() == FormMode.UpdateMode && modRow != modifyingRowM 
                ||
                parent.getFormMode() == FormMode.CreateMode && modRow != tabSize -1)
        {
            return null;
        }

        TableCellEditor cellEditor = null; 
        
        // <editor-fold defaultstate="collapsed" desc="-- Make ComboBox for the Cell">                        
        if (modCol == DriverCol.AffiliationL2.getNumVal()) {
            //<editor-fold desc="-- Level 2 affiliation">
            Object objLevelOne = getValueAt(modRow, DriverCol.AffiliationL1.getNumVal() );
            int L1_NO = (Integer)(((ConvComboBoxItem)objLevelOne).getKeyValue());
            
            TableColumn cBxCol = driverTable.getColumnModel().getColumn(modCol);        
            PComboBox<InnoComboBoxItem> comboBox = (PComboBox<InnoComboBoxItem>)
                    ((DefaultCellEditor)cBxCol.getCellEditor()).getComponent();

            comboBox.removeAllItems();
            Object objL1 = driverTable.getValueAt(modRow, AffiliationL1.getNumVal());
            Object prompter = getPrompter(DriverCol.AffiliationL2, objL1);
            comboBox.addItem((InnoComboBoxItem)prompter);
            parent.loadComboBoxItems(comboBox, DriverCol.AffiliationL2, L1_NO);
            
            comboBox.setEditable(true); 
            Object item = driverTable.getValueAt(modRow, modCol);
            comboBox.setSelectedItem((InnoComboBoxItem)item);
            cellEditor = new DefaultCellEditor(comboBox);

            //</editor-fold>
        } else if (modCol == DriverCol.UnitNo.getNumVal()) {
            //<editor-fold desc="-- Building unit">
            Object itemObj = getValueAt(modRow, DriverCol.BuildingNo.getNumVal());
                int bldgSeqNo = (Integer)(((ConvComboBoxItem)itemObj).getKeyValue());
            
            TableColumn comboCol = driverTable.getColumnModel().getColumn(modCol);        
            PComboBox<InnoComboBoxItem> comboBox = (PComboBox<InnoComboBoxItem>)
                    ((DefaultCellEditor)comboCol.getCellEditor()).getComponent();

            comboBox.removeAllItems();
            comboBox.addItem( (InnoComboBoxItem) 
                    ManageDrivers.getPrompter (DriverCol.UnitNo, driverTable.getValueAt(
                            driverTable.getSelectedRow(), BuildingNo.getNumVal()) ) );    
            comboBox.setEditable(true);
            Object item = driverTable.getValueAt(driverTable.getSelectedRow(), modCol);
            comboBox.setSelectedItem((InnoComboBoxItem)item);            
            
            parent.loadComboBoxItems(comboBox, DriverCol.UnitNo, bldgSeqNo);
            
            cellEditor = new DefaultCellEditor(comboBox);
            //</editor-fold>
        } else if (modCol == DriverCol.AffiliationL1.getNumVal()) {
            //<editor-fold desc="-- Level 1 affiliation">    
            // Save current affili' key value.
            Object objLevelOne = driverTable.getValueAt(modRow, modCol);
            prevL1_NO = (Integer)(((ConvComboBoxItem)objLevelOne).getKeyValue());
            cellEditor = new DefaultCellEditor(ManageDrivers.affiliationL1CBox); 
            cellEditor.addCellEditorListener(new CellEditorListener() {

                @Override
                public void editingStopped(ChangeEvent e) {
                    JComboBox cBox = (JComboBox)(((DefaultCellEditor)(e.getSource())).getComponent());
                    int newL1_NO = (Integer)(((ConvComboBoxItem)cBox.getSelectedItem()).getKeyValue());

                    if (level2RefreshNeeded(newL1_NO)) {
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {                     
                                int rIdx = driverTable.getSelectedRow();

                                driverTable.setValueAt(ManageDrivers.getPrompter(AffiliationL2, 
                                        driverTable.getValueAt(rIdx, AffiliationL1.getNumVal())),
                                        rIdx, AffiliationL2.getNumVal());   
                            }
                        });
                    }
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                }
            });
            //</editor-fold>
        } else if (modCol == DriverCol.BuildingNo.getNumVal()) {
            //<editor-fold desc="-- Building">    
            // Save current affili' key value.
            Object objBldg = driverTable.getValueAt(modRow, modCol);
            prevBldgKey = (Integer)(((ConvComboBoxItem)objBldg).getKeyValue());            
            cellEditor = new DefaultCellEditor(ManageDrivers.buildingCBox); 
            cellEditor.addCellEditorListener(new CellEditorListener() {

                @Override
                public void editingStopped(ChangeEvent e) {
                    JComboBox cBox = (JComboBox)(((DefaultCellEditor)(e.getSource())).getComponent());
                    int newBldgKey = (Integer)(((ConvComboBoxItem)cBox.getSelectedItem()).getKeyValue());

                    if (unitRefreshNeeded(newBldgKey)) {                    
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {                     
                                int rIdx = driverTable.getSelectedRow();

                                driverTable.setValueAt(ManageDrivers.getPrompter(UnitNo,
                                        driverTable.getValueAt(rIdx, BuildingNo.getNumVal())),
                                        rIdx, UnitNo.getNumVal());                               
                            }
                        });
                    }
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                }
            });
            //</editor-fold>
        } else {
            cellEditor = super.getCellEditor(modRow, modCol);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="-- Enter Key Handler ">            
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                
                if (e.getSource().getClass() == PComboBox.class) {
                    PComboBox comboBox = (PComboBox)e.getSource();
                    if (comboBox.isPopupVisible()) {
                        comboBox.setSelectedItem(comboBox.getHighlightedCbxItem());
                    } else {
                        // finalize update or insert operation here
                        int curRow = driverTable.getSelectedRow();
                        if (curRow < driverTable.getRowCount() - 1)
                            driverTable.setRowSelectionInterval(curRow + 1, curRow + 1);
                        finalizeDataEntry(parent);
                    }
                } else {
                    // finalize update or insert operation here
                    finalizeDataEntry(parent);
                }                           
            }
        };
        
        JComponent compo = (JComponent)((DefaultCellEditor)cellEditor).getComponent();
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);  
        // </editor-fold>
        
        if (modCol == AffiliationL1.getNumVal() 
                || modCol == AffiliationL2.getNumVal() 
                || modCol == BuildingNo.getNumVal()
                || modCol == UnitNo.getNumVal())
        {
            Component comp = ((DefaultCellEditor)cellEditor).getComponent();
            comp.addMouseListener(new MouseAdapter() {
                public void mousePressed (MouseEvent me) { 
                   
                    if (me.getClickCount() == 2 && !me.isConsumed()) {
                        me.consume();
                    
                        if (emptyLastRowPossible(parent.insertSave_Button, driverTable))
                        {
                            removeEmptyRow(parent.insertSave_Button, driverTable);
                        }                        

                        if (parent.getFormMode() != FormMode.UpdateMode) {
                            Point cBoxLoc = ((PComboBox)me.getSource()).getLocation();
                            int rowV = driverTable.rowAtPoint(cBoxLoc);                                    
                            int colV = driverTable.columnAtPoint(cBoxLoc);
                            ((DriverTable)driverTable).userWantsToUpdateRow(rowV, colV);
                        }
                    }
                }
            });
        }
        
        return cellEditor;
    }
        
    public static void finalizeDataEntry(ManageDrivers parent){
        System.out.println("finalizeDataEntry called");
        if (parent.getFormMode() == FormMode.CreateMode) {
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
                parent.finalizeDriverCreation();
            }
            parent.setFormMode(FormMode.NormalMode);
        } else if (parent.getFormMode() == FormMode.UpdateMode) {
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
                int rowV = driverTable.getSelectedRow();
                parent.finalizeDriverUpdate(rowV);
            }
            parent.setFormMode(FormMode.NormalMode);
        }        
    }

    static int prevL1_NO = -2;
    
    /**
     * Check if affiliation level 2 combobox needs change.
     * @param L1_NO key value of the selected affiliation level 1 item.
     * @return true when refresh is needed, false otherwise.
     */
    private boolean level2RefreshNeeded(int L1_NO) {
        boolean result = true;
        
        if (prevL1_NO == L1_NO) {
            result = false;
        }
        prevL1_NO = L1_NO;
        return result;
    }
    
    static int prevBldgKey = -2;

    /**
     * Check if (building) unit combobox needs change.
     * @param newBldgKey key value of the selected building item.
     * @return true when refresh is needed, false otherwise.
     */
    private boolean unitRefreshNeeded(int newBldgKey) {
        boolean result = true;
        
        if (prevBldgKey == newBldgKey) {
            result = false;
        }
        prevBldgKey = newBldgKey;
        return result;
    }
}