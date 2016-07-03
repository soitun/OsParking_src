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

import static com.mysql.jdbc.MysqlErrorNumbers.ER_DUP_ENTRY;
import com.osparking.global.CommonData;
import static com.osparking.global.CommonData.buttonHeightNorm;
import static com.osparking.global.CommonData.buttonWidthNorm;
import static com.osparking.global.CommonData.normGUIheight;
import static com.osparking.global.CommonData.normGUIwidth;
import static com.osparking.global.CommonData.pointColor;
import static com.osparking.global.CommonData.tableRowHeight;
import static com.osparking.global.CommonData.tipColor;
import com.osparking.global.Globals;
import static com.osparking.vehicle.driver.DriverTable.modifyingRowM;
import static com.osparking.vehicle.driver.ODSReader.getWrongCellPointString;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import static java.awt.event.KeyEvent.VK_SHIFT;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import com.osparking.global.names.ConvComboBoxItem;
import static com.osparking.global.names.DB_Access.getRecordCount;
import static com.osparking.global.names.DB_Access.readSettings;
import static com.osparking.global.Globals.SetAColumnWidth;
import static com.osparking.global.Globals.attachCondition;
import static com.osparking.global.Globals.checkOptions;
import static com.osparking.global.Globals.closeDBstuff;
import static com.osparking.global.Globals.emptyLastRowPossible;
import static com.osparking.global.Globals.font_Size;
import static com.osparking.global.Globals.font_Style;
import static com.osparking.global.Globals.font_Type;
import static com.osparking.global.Globals.highlightTableRow;
import static com.osparking.global.Globals.OSPiconList;
import static com.osparking.global.Globals.head_font_Size;
import static com.osparking.global.Globals.initializeLoggers;
import static com.osparking.global.Globals.language;
import static com.osparking.global.Globals.logParkingException;
import static com.osparking.global.Globals.logParkingOperation;
import static com.osparking.global.Globals.rejectUserInput;
import static com.osparking.global.Globals.removeEmptyRow;
import static com.osparking.global.Globals.showLicensePanel;
import static com.osparking.global.names.ControlEnums.ButtonTypes.*;
import static com.osparking.global.names.ControlEnums.ComboBoxItemTypes.*;
import static com.osparking.global.names.ControlEnums.DialogMSGTypes.*;
import static com.osparking.global.names.ControlEnums.DialogTitleTypes.*;
import com.osparking.global.names.ControlEnums.FormMode;
import static com.osparking.global.names.ControlEnums.FormModeString.SEARCH;
import static com.osparking.global.names.ControlEnums.LabelContent.COUNT_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.CREATE_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.MODIFY_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.LabelContent.REQUIRE_FIELD_NOTE;
import static com.osparking.global.names.ControlEnums.LabelContent.SEARCH_MODE_LABEL;
import static com.osparking.global.names.ControlEnums.MenuITemTypes.META_KEY_LABEL;
import static com.osparking.global.names.ControlEnums.TitleTypes.DRIVER_LIST_FRAME_TITLE;
import static com.osparking.global.names.ControlEnums.TableTypes.*;
import static com.osparking.global.names.ControlEnums.TextType.*;
import static com.osparking.global.names.ControlEnums.ToolTipContent.CELL_PHONE_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.DRIVER_INPUT_TOOLTIP;
import static com.osparking.global.names.ControlEnums.ToolTipContent.LANDLINE_INPUT_TOOLTIP;
import com.osparking.global.names.InnoComboBoxItem;
import static com.osparking.global.names.JDBCMySQL.getConnection;
import com.osparking.global.names.OSP_enums;
import com.osparking.global.names.OSP_enums.DriverCol;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL1;
import static com.osparking.global.names.OSP_enums.DriverCol.AffiliationL2;
import static com.osparking.global.names.OSP_enums.DriverCol.BuildingNo;
import static com.osparking.global.names.OSP_enums.DriverCol.CellPhone;
import static com.osparking.global.names.OSP_enums.DriverCol.DriverName;
import static com.osparking.global.names.OSP_enums.DriverCol.LandLine;
import static com.osparking.global.names.OSP_enums.DriverCol.UnitNo;
import static com.osparking.global.names.OSP_enums.OpLogLevel.EBDsettingsChange;
import com.osparking.global.names.PComboBox;
import com.osparking.global.names.WrappedInt;
import com.osparking.vehicle.LabelBlinker;
import java.awt.Color;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;


/**
 *
 * @author Open Source Parking Inc.
 */
public class ManageDrivers extends javax.swing.JFrame {
    
    private FormMode formMode = FormMode.NormalMode; 
    private DriverSelection driverSelectionForm = null; 
    
    private boolean nameHintShown = true;
    private boolean cellHintShown = true;    
    private boolean phoneHintShown = true;    
    private String prevSearchCondition = null;    
    private String currSearchCondition = null;    

    /**
     * Creates new form ManageDrivers
     */
    public ManageDrivers(DriverSelection driverSelectionForm) {
        this.driverSelectionForm = driverSelectionForm;
        initComponents();
        setIconImages(OSPiconList);                
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width - this.getSize().width, 0);
        
        // -- attach enter key handler for text search fields
        attachEnterHandler(searchName);
        attachEnterHandler(searchCell);
        attachEnterHandler(searchPhone);
        attachEnterHandler(searchL1ComboBox);
        attachEnterHandler(searchL2ComboBox);
        attachEnterHandler(searchBuildingComboBox);
        attachEnterHandler(searchUnitComboBox);

        affiliationL1CBox.setFont(null);
        
        affiliationL1CBox.removeAllItems();
        affiliationL1CBox.addItem(ManageDrivers.getPrompter(AffiliationL1, null));             
        loadComboBoxItems(affiliationL1CBox, DriverCol.AffiliationL1, -1);
        
        buildingCBox.removeAllItems();
        buildingCBox.addItem(ManageDrivers.getPrompter(DriverCol.BuildingNo, null));             
        loadComboBoxItems(buildingCBox, DriverCol.BuildingNo, -1);
        
        initAffiliationComboBoxes(searchL1ComboBox, searchL2ComboBox, searchBuildingComboBox,
                searchUnitComboBox);
        
        changeDriverTable();
        driverTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        setupComboBoxColumn(DriverCol.AffiliationL2);
        setupComboBoxColumn(DriverCol.UnitNo);
        
        loadDriverData(UNKNOWN, "", "");
//        searchName.requestFocus();
    }
    
    static boolean checkIfUserWantsToSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static JComboBox affiliationL1CBox = new PComboBox();
    static JComboBox buildingCBox = new PComboBox();
    /**
     * @return the formMode
     */
    public FormMode getFormMode() {
        return formMode;
    }
    
    int releaseCount = 0;

    private void changeSearchButtonEnabled() {
        currSearchCondition = formSearchCondition();
        if (currSearchCondition.equals(prevSearchCondition)) {
            searchButton.setEnabled(false);
        } else {
            searchButton.setEnabled(true);
        }
        releaseCount = 0;
    }    
    
    /**
     * @param newMode the formMode to set
     */
    public void setFormMode(FormMode newMode) {
        switch (newMode) {
            case CreateMode:
                closeFormButton.setEnabled(false);
                
                searchButton.setEnabled(false);
//                clearButton.setEnabled(false);
                readSheet_Button.setEnabled(false);
                deleteAllDrivers.setEnabled(false);
                
                deleteDriver_Button.setText(CANCEL_BTN.getContent());
                deleteDriver_Button.setMnemonic('c');
                deleteDriver_Button.setEnabled(true);
                modifyDriver_Button.setEnabled(false);
                
                createDriver_Button.setText(SAVE_BTN.getContent());
                createDriver_Button.setMnemonic('s');
                requiredLabel.setVisible(true);
                
                searchKeyGroupEnabled(false);
                formModeLabel.setText(CREATE_MODE_LABEL.getContent());
                break;
                
            case UpdateMode:
                closeFormButton.setEnabled(false);
                
                searchButton.setEnabled(false);
//                clearButton.setEnabled(false);
                readSheet_Button.setEnabled(false);
                deleteAllDrivers.setEnabled(false);
                
                deleteDriver_Button.setText(CANCEL_BTN.getContent());
                deleteDriver_Button.setMnemonic('c');
                deleteDriver_Button.setEnabled(true);
                createDriver_Button.setEnabled(false);
                
                modifyDriver_Button.setText(SAVE_BTN.getContent());
                modifyDriver_Button.setMnemonic('s');
                requiredLabel.setVisible(true);
                
                searchKeyGroupEnabled(false);                
                formModeLabel.setText(MODIFY_MODE_LABEL.getContent());
                break;
                
            case NormalMode:
                closeFormButton.setEnabled(true);
                
//                searchButton.setEnabled(true);
//                clearButton.setEnabled(true);
                readSheet_Button.setEnabled(true);
                deleteAllDrivers.setEnabled(false);
                
                deleteDriver_Button.setText(DELETE_BTN.getContent());
                deleteDriver_Button.setMnemonic('d');
                deleteDriver_Button.setEnabled(false);
                
                modifyDriver_Button.setText(MODIFY_BTN.getContent());
                modifyDriver_Button.setMnemonic('m');
                modifyDriver_Button.setEnabled(false);
                
                createDriver_Button.setText(CREATE_BTN.getContent());
                createDriver_Button.setMnemonic('r');
                requiredLabel.setVisible(false);
                
                searchKeyGroupEnabled(true);
                formModeLabel.setText(SEARCH_MODE_LABEL.getContent());
                break;
            default:
                break;
        }
        formMode = newMode;
    }

    private void tableMouseClicked(MouseEvent evt) {
        int row = driverTable.rowAtPoint(evt.getPoint());

        ConvComboBoxItem item = (ConvComboBoxItem)(driverTable.getValueAt(row, 
                DriverCol.AffiliationL1.getNumVal()));
    }
    
    private static boolean selectionListenerDisabled = false;
    
    static int changeCount = 0;
    private void addDriverSelectionListener() {
        
        ListSelectionModel cellSelectionModel = driverTable.getSelectionModel();
        cellSelectionModel.addListSelectionListener (new ListSelectionListener ()
        {
            @Override
            public void valueChanged(ListSelectionEvent  e) {
                if (e.getValueIsAdjusting()) 
                    return;
                
//                System.out.println(++changeCount + ". manage drivers form : value changed");
                if (selectionListenerDisabled || getFormMode() == FormMode.NormalMode) 
                {
                    if (driverTable.getSelectedRowCount() > 0) {
                        modifyDriver_Button.setEnabled(true);
                        deleteAllDrivers.setEnabled(true);
                        deleteDriver_Button.setEnabled(true);
                    }                    
                    return;
                }
                
                if (getFormMode() == FormMode.CreateMode) {
                    if (driverTable.getSelectedRow() < driverTable.getRowCount() - 1) 
                        processSaveAction();
                }
                else 
                {   // now in update mode
                    if (driverTable.getCellEditor() != null) {
                        driverTable.getCellEditor().stopCellEditing(); // store user input
                    }
                    int rowSel = e.getFirstIndex();
                    if (driverTable.convertRowIndexToModel(rowSel) == modifyingRowM)
                        rowSel = e.getLastIndex();
                    finalizeDriverUpdate(rowSel);
                }
            }

        }); 
    }

    private void processSaveAction() {
        if (driverTable.getCellEditor() != null) {
            driverTable.getCellEditor().stopCellEditing(); // store user input
        }
        finalizeDriverCreation();
    }

    private int createNewDriver(String driverName, int rowIndex) {
            
        Connection conn = null;
        PreparedStatement createDriver = null;
        String excepMsg = "failed creation of a car driver: " + driverName;

        int result = 0;
        String landLine = null;
        String itemL2name = null;
        String itemUnitName = null;
        try {
            //<editor-fold desc="insert the new driver into the database">
            String sql = "Insert Into cardriver (name, CELLPHONE, PHONE, L2_NO" +
                            ", UNIT_SEQ_NO, CREATIONDATE)" + 
                            " Values (?, ?, ?, ?, ?, current_timestamp)";

            //<editor-fold desc="prepare property values for the new driver">
            TableModel model = driverTable.getModel();
            String cellPhone = ((String)model.getValueAt(rowIndex, 
                    DriverCol.CellPhone.getNumVal())).trim();
            landLine = ((String)model.getValueAt(rowIndex, 
                    DriverCol.LandLine.getNumVal())).trim();

            String itemL2_NO = null;
            InnoComboBoxItem itemL2 = (InnoComboBoxItem)(model.getValueAt(rowIndex, 
                    DriverCol.AffiliationL2.getNumVal()));
            
            if (itemL2.getKeys()[0] != -1) {
                itemL2_NO = String.valueOf(itemL2.getKeys()[0]);
                itemL2name = itemL2.getLabels()[0];
            }

            String itemUnitSEQ_NO = null;
            InnoComboBoxItem itemUnit = (InnoComboBoxItem)(model.getValueAt(rowIndex, 
                    DriverCol.UnitNo.getNumVal()));
            
            if (itemUnit.getKeys()[0] != -1) {
                itemUnitSEQ_NO = String.valueOf(itemUnit.getKeys()[0]);
                itemUnitName = itemUnit.getLabels()[0];
            }
            //</editor-fold>
                            
            conn = getConnection();
            createDriver = conn.prepareStatement(sql);
            createDriver.setString(DriverCol.DriverName.getNumVal(), driverName);
            createDriver.setString(DriverCol.CellPhone.getNumVal(), cellPhone);
            createDriver.setString(DriverCol.LandLine.getNumVal(), landLine);
            createDriver.setString(DriverCol.AffiliationL2.getNumVal() - 1, itemL2_NO);
            createDriver.setString(DriverCol.UnitNo.getNumVal() - 2, itemUnitSEQ_NO);

            result = createDriver.executeUpdate();
            //</editor-fold>
        } catch (SQLException ex) {
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                rejectUserInput(driverTable, rowIndex, "<Name, Cell Phone> pair");                     
            }
            else {
                logParkingException(Level.SEVERE, ex, excepMsg);
            }                 
        } finally {
            closeDBstuff(conn, createDriver, null, excepMsg);
            if (result == 1)
            {
                //<editor-fold desc="redisplay driver list with a new driver just added">     
                // if insertion was successful, then redisplay the list
                rowIndex = driverTable.getSelectedRow();
                int colM = driverTable.convertColumnIndexToModel(
                        DriverCol.DriverName.getNumVal());

                // conditions that make driver information insufficient: 1, 2
                driverName = ((String)driverTable.getModel().getValueAt(rowIndex, colM)).trim(); 
                colM = driverTable.convertColumnIndexToModel(
                        DriverCol.CellPhone.getNumVal());
                String cell = String.valueOf(driverTable.getValueAt(rowIndex, colM)).toLowerCase();                    

                setFormMode(FormMode.NormalMode);
                loadDriverData(UNKNOWN, driverName, cell); // insert > refresh list
                
                StringBuffer driverProperties = new StringBuffer();
            
                driverProperties.append("Driver Creation Summary: " + System.lineSeparator());
                
                getDriverProperties(driverName, cell, driverProperties, rowIndex, landLine, itemL2name, itemUnitName);  
                Globals.logParkingOperation(EBDsettingsChange, driverProperties.toString(), Globals.GENERAL_DEVICE);
                
                // Redisplay the skinny driver selection form which this driver manage form is invoked
                if (driverSelectionForm != null) {
                    driverSelectionForm.loadSkinnyDriverTable(0); // 0: highlight first row
                }      
                
                JOptionPane.showConfirmDialog(this, CREATION_SUCCESS_DIALOG.getContent() + driverName,
                        CREATION_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);   
                //</editor-fold>
            }else {
                JOptionPane.showConfirmDialog(null, DRIVER_CREATRION_FAIL_DIALOG.getContent(),
                        CREATION_RESULT_DIALOGTITLE.getContent(), 
                        JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);
            }            
        }
        return result;        
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
        jTable1 = new javax.swing.JTable();
        odsFileChooser = new javax.swing.JFileChooser();
        northPanel = new javax.swing.JPanel();
        westPanel = new javax.swing.JPanel();
        wholePanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        seeLicenseButton = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        titleLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jPanel1 = new javax.swing.JPanel();
        myMetaKeyLabel = new javax.swing.JLabel();
        topButtonPanel = new javax.swing.JPanel();
        topLTpanel = new javax.swing.JPanel();
        countLbl = new javax.swing.JLabel();
        countValue = new javax.swing.JLabel();
        topMid_1 = new javax.swing.JPanel();
        requiredLabel = new javax.swing.JLabel();
        topMid_2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        formModeLabel = new javax.swing.JLabel();
        topRHpanel = new javax.swing.JPanel();
        clearButton = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        filler15_6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(32767, 15));
        searchPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        filler66 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        searchName = new javax.swing.JTextField();
        searchCell = new javax.swing.JTextField();
        searchPhone = new javax.swing.JTextField();
        searchL1ComboBox = new PComboBox();
        searchL2ComboBox = new PComboBox<InnoComboBoxItem>();
        searchBuildingComboBox = new PComboBox();
        searchUnitComboBox = new PComboBox();
        driversScrollPane = new javax.swing.JScrollPane();
        driversTable = new javax.swing.JTable();
        filler15_5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 15), new java.awt.Dimension(0, 15), new java.awt.Dimension(32767, 15));
        bottomButtonPanel = new javax.swing.JPanel();
        leftButtons = new javax.swing.JPanel();
        createDriver_Button = new javax.swing.JButton();
        modifyDriver_Button = new javax.swing.JButton();
        deleteDriver_Button = new javax.swing.JButton();
        deleteDriver_Button2 = new javax.swing.JButton();
        rightButtons = new javax.swing.JPanel();
        deleteAllDrivers = new javax.swing.JButton();
        readSheet_Button1 = new javax.swing.JButton();
        readSheet_Button = new javax.swing.JButton();
        closeFormButton = new javax.swing.JButton();
        eastPanel = new javax.swing.JPanel();
        southPanel = new javax.swing.JPanel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(DRIVER_LIST_FRAME_TITLE.getContent());
        setMaximumSize(new java.awt.Dimension(32000, 24000));
        setMinimumSize(new Dimension(normGUIwidth, normGUIheight));
        setPreferredSize(new Dimension(normGUIwidth, normGUIheight));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        northPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        northPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        northPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        northPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 45, 15));
        getContentPane().add(northPanel, java.awt.BorderLayout.NORTH);

        westPanel.setMaximumSize(new java.awt.Dimension(40, 32767));
        westPanel.setMinimumSize(new java.awt.Dimension(40, 10));
        westPanel.setPreferredSize(new java.awt.Dimension(40, 100));
        getContentPane().add(westPanel, java.awt.BorderLayout.WEST);

        wholePanel.setLayout(new javax.swing.BoxLayout(wholePanel, javax.swing.BoxLayout.PAGE_AXIS));

        titlePanel.setMaximumSize(new java.awt.Dimension(2147483647, 40));
        titlePanel.setMinimumSize(new java.awt.Dimension(110, 40));
        titlePanel.setPreferredSize(new java.awt.Dimension(210, 40));
        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.LINE_AXIS));

        seeLicenseButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        seeLicenseButton.setText("About");
        seeLicenseButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        seeLicenseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seeLicenseButtonActionPerformed(evt);
            }
        });
        titlePanel.add(seeLicenseButton);
        titlePanel.add(filler2);

        titleLabel.setFont(new java.awt.Font(font_Type, font_Style, head_font_Size));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText(DRIVER_LIST_FRAME_TITLE.getContent());
        titleLabel.setMaximumSize(new java.awt.Dimension(110, 28));
        titleLabel.setMinimumSize(new java.awt.Dimension(110, 28));
        titleLabel.setPreferredSize(new java.awt.Dimension(110, 28));
        titlePanel.add(titleLabel);
        titlePanel.add(filler3);

        jPanel1.setMaximumSize(new java.awt.Dimension(90, 30));
        jPanel1.setMinimumSize(new java.awt.Dimension(90, 30));
        jPanel1.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        myMetaKeyLabel.setText(META_KEY_LABEL.getContent());
        myMetaKeyLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        myMetaKeyLabel.setForeground(tipColor);
        jPanel1.add(myMetaKeyLabel);

        titlePanel.add(jPanel1);

        wholePanel.add(titlePanel);

        topButtonPanel.setMaximumSize(new java.awt.Dimension(33095, 40));

        topLTpanel.setMaximumSize(new java.awt.Dimension(300, 40));
        topLTpanel.setMinimumSize(new java.awt.Dimension(159, 40));
        topLTpanel.setPreferredSize(new java.awt.Dimension(160, 40));
        topLTpanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 22));

        countLbl.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countLbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        countLbl.setText(COUNT_LABEL.getContent());
        JLabel tempLabel = new JLabel(COUNT_LABEL.getContent());
        tempLabel.setFont(countLbl.getFont());
        Dimension dim = tempLabel.getPreferredSize();
        countLbl.setMaximumSize(new java.awt.Dimension(110, 27));
        countLbl.setMinimumSize(new java.awt.Dimension(90, 27));
        countLbl.setPreferredSize(new Dimension(dim.width + 1, dim.height));
        topLTpanel.add(countLbl);

        countValue.setForeground(pointColor);
        countValue.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        countValue.setText("count");
        topLTpanel.add(countValue);

        topMid_1.setMaximumSize(new java.awt.Dimension(300, 40));
        topMid_1.setMinimumSize(new java.awt.Dimension(159, 40));
        topMid_1.setPreferredSize(new java.awt.Dimension(160, 40));
        topMid_1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 22));

        requiredLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        requiredLabel.setText(REQUIRE_FIELD_NOTE.getContent());
        topMid_1.add(requiredLabel);

        topMid_2.setMaximumSize(new java.awt.Dimension(300, 40));
        topMid_2.setMinimumSize(new java.awt.Dimension(159, 40));
        topMid_2.setPreferredSize(new java.awt.Dimension(210, 40));
        topMid_2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 10));

        jLabel2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel2.setText(MODE_LABEL.getContent());
        topMid_2.add(jLabel2);

        formModeLabel.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        formModeLabel.setForeground(pointColor);
        formModeLabel.setText(SEARCH.getContent());
        formModeLabel.setToolTipText("");
        topMid_2.add(formModeLabel);

        topRHpanel.setMaximumSize(new java.awt.Dimension(300, 40));
        topRHpanel.setMinimumSize(new java.awt.Dimension(159, 40));
        topRHpanel.setPreferredSize(new java.awt.Dimension(210, 40));
        topRHpanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        clearButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        clearButton.setMnemonic('l');
        clearButton.setText(CLEAR_BTN.getContent());
        clearButton.setEnabled(false);
        clearButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        topRHpanel.add(clearButton);

        searchButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchButton.setMnemonic('s');
        searchButton.setText(SEARCH_BTN.getContent());
        searchButton.setEnabled(false);
        searchButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        topRHpanel.add(searchButton);

        javax.swing.GroupLayout topButtonPanelLayout = new javax.swing.GroupLayout(topButtonPanel);
        topButtonPanel.setLayout(topButtonPanelLayout);
        topButtonPanelLayout.setHorizontalGroup(
            topButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topButtonPanelLayout.createSequentialGroup()
                .addComponent(topLTpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(topMid_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addComponent(topMid_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
                .addComponent(topRHpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        topButtonPanelLayout.setVerticalGroup(
            topButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topLTpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(topMid_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(topRHpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(topMid_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        wholePanel.add(topButtonPanel);
        wholePanel.add(filler15_6);

        searchPanel.setMaximumSize(new java.awt.Dimension(2095, 28));
        searchPanel.setLayout(new javax.swing.BoxLayout(searchPanel, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Search Key");
        jLabel3.setMaximumSize(new java.awt.Dimension(80, 27));
        jLabel3.setMinimumSize(new java.awt.Dimension(80, 27));
        jLabel3.setPreferredSize(new java.awt.Dimension(80, 27));
        searchPanel.add(jLabel3);
        jLabel3.getAccessibleContext().setAccessibleName("default");

        searchPanel.add(filler66);

        searchName.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchName.setForeground(tipColor);
        searchName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchName.setText(DRIVER_TF.getContent());
        searchName.setToolTipText(DRIVER_INPUT_TOOLTIP.getContent());
        searchName.setMaximumSize(new java.awt.Dimension(350, 28));
        searchName.setMinimumSize(new java.awt.Dimension(110, 28));
        searchName.setPreferredSize(new java.awt.Dimension(110, 28));
        searchName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchNameFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchNameFocusLost(evt);
            }
        });
        searchName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchNameMousePressed(evt);
            }
        });
        searchName.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                searchNameInputMethodTextChanged(evt);
            }
        });
        searchName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchNameKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchNameKeyTyped(evt);
            }
        });
        searchPanel.add(searchName);
        searchName.getAccessibleContext().setAccessibleName("");

        searchCell.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchCell.setForeground(tipColor);
        searchCell.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchCell.setText(CELL_PHONE_TF.getContent());
        searchCell.setToolTipText(CELL_PHONE_INPUT_TOOLTIP.getContent());
        searchCell.setMaximumSize(new java.awt.Dimension(140, 28));
        searchCell.setMinimumSize(new java.awt.Dimension(120, 28));
        searchCell.setPreferredSize(new java.awt.Dimension(120, 28));
        searchCell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchCellFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchCellFocusLost(evt);
            }
        });
        searchCell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchCellMousePressed(evt);
            }
        });
        searchCell.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchCellKeyTyped(evt);
            }
        });
        searchPanel.add(searchCell);
        searchCell.getAccessibleContext().setAccessibleName("");

        searchPhone.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchPhone.setForeground(tipColor);
        searchPhone.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        searchPhone.setText(LANDLINE_TF.getContent());
        searchPhone.setToolTipText(LANDLINE_INPUT_TOOLTIP.getContent());
        searchPhone.setMaximumSize(new java.awt.Dimension(140, 28));
        searchPhone.setMinimumSize(new java.awt.Dimension(120, 28));
        searchPhone.setPreferredSize(new java.awt.Dimension(120, 28));
        searchPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchPhoneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchPhoneFocusLost(evt);
            }
        });
        searchPhone.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                searchPhoneMousePressed(evt);
            }
        });
        searchPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchPhoneKeyTyped(evt);
            }
        });
        searchPanel.add(searchPhone);
        searchPhone.getAccessibleContext().setAccessibleName("");

        searchL1ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchL1ComboBox.setToolTipText("Search Affiliation2");
        searchL1ComboBox.setMaximumSize(new java.awt.Dimension(340, 30));
        searchL1ComboBox.setMinimumSize(new java.awt.Dimension(110, 28));
        searchL1ComboBox.setPreferredSize(new java.awt.Dimension(110, 28));
        searchL1ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchL1ComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchL1ComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchL1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchL1ComboBoxActionPerformed(evt);
            }
        });
        searchPanel.add(searchL1ComboBox);
        searchL1ComboBox.getAccessibleContext().setAccessibleName("");

        searchL2ComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchL2ComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchL2ComboBox.setToolTipText("Search Affiliation1");
        searchL2ComboBox.setMaximumSize(new java.awt.Dimension(340, 30));
        searchL2ComboBox.setMinimumSize(new java.awt.Dimension(110, 28));
        searchL2ComboBox.setPreferredSize(new java.awt.Dimension(110, 28));
        searchL2ComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchL2ComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchL2ComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchPanel.add(searchL2ComboBox);
        searchL2ComboBox.getAccessibleContext().setAccessibleName("");

        searchBuildingComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchBuildingComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchBuildingComboBox.setToolTipText("Search Building");
        searchBuildingComboBox.setMaximumSize(new java.awt.Dimension(340, 30));
        searchBuildingComboBox.setMinimumSize(new java.awt.Dimension(110, 28));
        searchBuildingComboBox.setPreferredSize(new java.awt.Dimension(110, 28));
        searchBuildingComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchBuildingComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchBuildingComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchBuildingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBuildingComboBoxActionPerformed(evt);
            }
        });
        searchPanel.add(searchBuildingComboBox);
        searchBuildingComboBox.getAccessibleContext().setAccessibleName("");

        searchUnitComboBox.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        searchUnitComboBox.setModel(
            new javax.swing.DefaultComboBoxModel(new String[] {}));
        searchUnitComboBox.setToolTipText("Search Unit");
        searchUnitComboBox.setMaximumSize(new java.awt.Dimension(340, 30));
        searchUnitComboBox.setMinimumSize(new java.awt.Dimension(110, 28));
        searchUnitComboBox.setPreferredSize(new java.awt.Dimension(110, 28));
        searchUnitComboBox.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                searchUnitComboBoxPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                searchUnitComboBoxPopupMenuWillBecomeVisible(evt);
            }
        });
        searchPanel.add(searchUnitComboBox);
        searchUnitComboBox.getAccessibleContext().setAccessibleName("");

        wholePanel.add(searchPanel);

        driversScrollPane.setPreferredSize(new java.awt.Dimension(452, 200));

        driversTable.setAutoCreateRowSorter(true);
        driversTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driversTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driversTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        driversTable.setInheritsPopupMenu(true);
        driversTable.setPreferredSize(new java.awt.Dimension(300, 0));
        driversTable.setRowHeight(tableRowHeight);
        driversTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                driversTableFocusGained(evt);
            }
        });
        driversTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                driversTableKeyReleased(evt);
            }
        });
        driversScrollPane.setViewportView(driversTable);

        wholePanel.add(driversScrollPane);
        wholePanel.add(filler15_5);

        bottomButtonPanel.setMaximumSize(new java.awt.Dimension(33727, 40));

        leftButtons.setMaximumSize(new java.awt.Dimension(32767, 40));
        leftButtons.setMinimumSize(new java.awt.Dimension(300, 40));
        leftButtons.setPreferredSize(new java.awt.Dimension(400, 40));
        leftButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

        createDriver_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        createDriver_Button.setMnemonic('r');
        createDriver_Button.setText("Create");
        createDriver_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        createDriver_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        createDriver_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        createDriver_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createDriver_ButtonActionPerformed(evt);
            }
        });
        leftButtons.add(createDriver_Button);

        modifyDriver_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        modifyDriver_Button.setMnemonic('m');
        modifyDriver_Button.setText("Modify");
        modifyDriver_Button.setEnabled(false);
        modifyDriver_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modifyDriver_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modifyDriver_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        modifyDriver_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyDriver_ButtonActionPerformed(evt);
            }
        });
        leftButtons.add(modifyDriver_Button);

        deleteDriver_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteDriver_Button.setMnemonic('d');
        deleteDriver_Button.setText("Delete");
        deleteDriver_Button.setEnabled(false);
        deleteDriver_Button.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteDriver_Button.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteDriver_Button.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteDriver_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDriver_ButtonActionPerformed(evt);
            }
        });
        leftButtons.add(deleteDriver_Button);

        deleteDriver_Button2.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteDriver_Button2.setMnemonic('d');
        deleteDriver_Button2.setText("취소");
        deleteDriver_Button2.setEnabled(false);
        deleteDriver_Button2.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteDriver_Button2.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteDriver_Button2.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        deleteDriver_Button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDriver_Button2ActionPerformed(evt);
            }
        });
        leftButtons.add(deleteDriver_Button2);

        rightButtons.setMaximumSize(new java.awt.Dimension(32767, 40));
        rightButtons.setMinimumSize(new java.awt.Dimension(350, 40));
        rightButtons.setPreferredSize(new java.awt.Dimension(470, 40));
        rightButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        deleteAllDrivers.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        deleteAllDrivers.setMnemonic('a');
        deleteAllDrivers.setText("Delete All");
        deleteAllDrivers.setEnabled(false);
        deleteAllDrivers.setMargin(new java.awt.Insets(0, 0, 0, 0));
        deleteAllDrivers.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAllDrivers.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAllDrivers.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        deleteAllDrivers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllDriversActionPerformed(evt);
            }
        });
        rightButtons.add(deleteAllDrivers);

        readSheet_Button1.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet_Button1.setMnemonic('u');
        readSheet_Button1.setText("Upload Sheet");
        readSheet_Button1.setEnabled(false);
        readSheet_Button1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        readSheet_Button1.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button1.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button1.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readSheet_Button1ActionPerformed(evt);
            }
        });
        rightButtons.add(readSheet_Button1);

        readSheet_Button.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        readSheet_Button.setMnemonic('u');
        readSheet_Button.setText("Upload Sheet");
        readSheet_Button.setEnabled(false);
        readSheet_Button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        readSheet_Button.setMaximumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button.setMinimumSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button.setPreferredSize(new Dimension(CommonData.buttonWidthWide, buttonHeightNorm));
        readSheet_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readSheet_ButtonActionPerformed(evt);
            }
        });
        rightButtons.add(readSheet_Button);

        closeFormButton.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        closeFormButton.setMnemonic('c');
        closeFormButton.setText("Close");
        closeFormButton.setMaximumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.setMinimumSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.setPreferredSize(new Dimension(buttonWidthNorm, buttonHeightNorm));
        closeFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFormButtonActionPerformed(evt);
            }
        });
        rightButtons.add(closeFormButton);

        javax.swing.GroupLayout bottomButtonPanelLayout = new javax.swing.GroupLayout(bottomButtonPanel);
        bottomButtonPanel.setLayout(bottomButtonPanelLayout);
        bottomButtonPanelLayout.setHorizontalGroup(
            bottomButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomButtonPanelLayout.createSequentialGroup()
                .addComponent(leftButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                .addComponent(rightButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        bottomButtonPanelLayout.setVerticalGroup(
            bottomButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(leftButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(rightButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        wholePanel.add(bottomButtonPanel);

        getContentPane().add(wholePanel, java.awt.BorderLayout.CENTER);

        eastPanel.setMaximumSize(new java.awt.Dimension(40, 32767));
        eastPanel.setMinimumSize(new java.awt.Dimension(40, 10));
        eastPanel.setPreferredSize(new java.awt.Dimension(40, 100));
        getContentPane().add(eastPanel, java.awt.BorderLayout.EAST);

        southPanel.setMaximumSize(new java.awt.Dimension(32767, 40));
        southPanel.setMinimumSize(new java.awt.Dimension(10, 40));
        southPanel.setPreferredSize(new java.awt.Dimension(100, 40));
        getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    int rowBeforeCreate = -1;
    int colBeforeCreate = -1;
    
    private void createDriver_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createDriver_ButtonActionPerformed
        int rowV = driverTable.getSelectedRow();
        
        if (getFormMode() == FormMode.CreateMode) {
            processSaveAction();
        } else {
            //<editor-fold desc="-- Process driver creation request">            
            if (rowV != -1)
                rowBeforeCreate = driverTable.convertRowIndexToModel(rowV);
            colBeforeCreate = driverTable.getSelectedColumn();  
            
            DefaultTableModel model = (DefaultTableModel)driverTable.getModel();
            model.addRow(new Object [] {null, "", "", "", 
                ManageDrivers.getPrompter(AffiliationL1, null), 
                ManageDrivers.getPrompter(AffiliationL2, null), 
                ManageDrivers.getPrompter(BuildingNo, null), 
                ManageDrivers.getPrompter(UnitNo, null), 
                null});

            int rowIndex = driverTable.getRowCount() - 1;
            boolean isNameEmpty = driverTable.getValueAt(rowIndex, 1).equals("");
            if (!isNameEmpty) 
            {
                rowIndex = 0;
            }

            setFormMode(FormMode.CreateMode);

            highlightTableRow(driverTable, rowIndex);
            if (driverTable.editCellAt(rowIndex, 1))
            {
                startEditingCell(rowIndex, 1);
            }
            //</editor-fold>
        }
    }//GEN-LAST:event_createDriver_ButtonActionPerformed

    private void deleteAllDriversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllDriversActionPerformed
        int driverCount = getRecordCount("cardriver", -1);
        int vehiclecount = getRecordCount("vehicles", -1);
        
        String dialogMessage = "";
        
        switch(language){
            case KOREAN:
                dialogMessage = driverCount + "명의 정보를 삭제 하시겟습니까?" + System.lineSeparator() 
                        + "총 " + vehiclecount +"대의 소유 차량정보가 함께 삭제됩니다!";
                break;
                
            case ENGLISH:
                dialogMessage = "Do you want to delete " + driverCount + " drivers all?" + System.lineSeparator() 
                        + "Total " + vehiclecount + " owned vehicles will also be destroyed!";
                break;
                
            default:
                break;
        }        
        
        int result = JOptionPane.showConfirmDialog(this, dialogMessage,
                DELETE_ALL_DAILOGTITLE.getContent(),
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement deleteDrivers = null;
            String excepMsg = "every driver deletion";

            result = 0;
            try {
                String sql = "Delete From cardriver";
                
                conn = getConnection();
                deleteDrivers = conn.prepareStatement(sql);
                result = deleteDrivers.executeUpdate();
            } catch (SQLException ex) {
                logParkingException(Level.SEVERE, ex, "(All Driver Deletion)");
            } finally {
                closeDBstuff(conn, deleteDrivers, null, excepMsg);
            }

            if (result >= 1) {
                loadDriverData(UNKNOWN, "", "");
                JOptionPane.showConfirmDialog(this, DRIVER_DELETE_ALL_RESULT_DAILOG.getContent(),
                        DELETE_ALL_RESULT_DIALOGTITLE.getContent(),
                        JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteAllDriversActionPerformed
   
    private void modifyDriver_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyDriver_ButtonActionPerformed
        int rowV = driverTable.getSelectedRow();
        int rowM = driverTable.convertRowIndexToModel(rowV);
        int colV = driverTable.getSelectedColumn();
        
        if (driverTable.getSelectedRows().length > 1) {
            JOptionPane.showConfirmDialog(this, DRIVER_MODIFY_FAIL_DAILOG.getContent(),
                    MODIFY_FAIL_DIALOGTITLE.getContent(),
                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);            
            return;
        }

        if (getFormMode() == FormMode.UpdateMode) {
            //<editor-fold desc="-- Process modification save request">
            if (driverTable.getCellEditor() != null) {
                driverTable.getCellEditor().stopCellEditing(); // store user input
            }
            finalizeDriverUpdate(rowM);            
            //</editor-fold>
        } else {        
            if (colV <= 0) {
                colV = 2;
            }

            int driverSeqNo = (Integer)driverTable.getModel().getValueAt(rowM, 
                    DriverCol.SEQ_NO.getNumVal());        

            int response = driverTable.askUserOnUpdate((String)
                    driverTable.getModel().getValueAt(rowM, DriverCol.DriverName.getNumVal()),
                    getRecordCount("vehicles", driverSeqNo));

            if (response == JOptionPane.YES_OPTION) {        
                setUpdateMode(true);   
                modifyingRowM = rowM;
                highlightTableRow(driverTable, rowV);
                if (driverTable.editCellAt(rowV, colV))
                {
                    startEditingCell(rowV, colV);
                }   
            }
        }
    }//GEN-LAST:event_modifyDriver_ButtonActionPerformed

    private void deleteDriver_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDriver_ButtonActionPerformed
        int rowV = driverTable.getSelectedRow();
        int rowM = -1;
        if (rowV != -1)
            rowM = driverTable.convertRowIndexToModel(rowV);
        int colV = driverTable.getSelectedColumn();        

        if (getFormMode() == FormMode.CreateMode) {
            //<editor-fold desc="-- Handle driver creation cancellation request">
            Object[] options = new Object[2];
            
            switch(language){
                case KOREAN:
                    options[0] = "예(종료)";
                    options[1] = "아니요(생성)";
                    break;
                    
                case ENGLISH:
                    options[0] = "Yes(quit)";
                    options[1] = "No(create)";
                    break;
                    
                default:
                    break;
            }
            
            int response = JOptionPane.showOptionDialog(this, DRIVER_CREATE_CANCEL_DIALOG.getContent(),
                                CANCEL_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, options[0]);
        
            if (response == JOptionPane.YES_OPTION) {
                // Confirmed cancel request
                setFormMode(FormMode.NormalMode);
                
                // remove last row which was prepared for the new driver information
                ((DefaultTableModel)driverTable.getModel()).setRowCount(driverTable.getRowCount() - 1);     
                
                // highlight originally selected row if existed.
                if (rowBeforeCreate != -1) {
                    if (colBeforeCreate != -1)
                        driverTable.changeSelection(rowBeforeCreate, colBeforeCreate, false, false);
                    highlightTableRow(driverTable, rowBeforeCreate);
                    driverTable.requestFocusInWindow();
                }
            } else {
                startEditingCell(rowM, colV);
            }         
            //</editor-fold>
        } else if (getFormMode() == FormMode.UpdateMode) {
            //<editor-fold desc="-- Process modification cancel request">
            
            int response = JOptionPane.showOptionDialog(this, DRIVER_MODIFY_CANCEL_DAILOG.getContent(),
                                CANCEL_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null); 
            
            if (response == JOptionPane.NO_OPTION) {
                startEditingCell(rowV, colV);
            } else {
                driverTable.getCellEditor().cancelCellEditing();
                setFormMode(FormMode.NormalMode);
                loadDriverData(rowM, "", "");
                driverTable.requestFocusInWindow();
            } 
            //</editor-fold>            
        } else {
            // delete a driver's record currently selected 
            int[] deleteIndice = driverTable.getSelectedRows();
            if (deleteIndice.length == 0)
            {
                return;
            }

            int result = -1;
            String driverName = (String)driverTable.getValueAt(deleteIndice[0], 1);
            int modal_Index = driverTable.convertRowIndexToModel(deleteIndice[0]);
            int CD_SEQ_NO = (int)driverTable.getModel().getValueAt(modal_Index, 
                    DriverCol.SEQ_NO.getNumVal());
            int count = getRecordCount("vehicles", CD_SEQ_NO);

            if (deleteIndice.length == 1) {
                String dialogMessage = "";
                
                switch(language){
                    case KOREAN:
                        dialogMessage = "운전자 정보와 운전자 차량에 대한 정보를 삭제하시겠습니까?" + System.getProperty("line.separator") +
                                "운전자 이름 : '" + driverName + "' (소유차량 " + count + "대)";
                        break;
                        
                    case ENGLISH:
                        dialogMessage = "Want to delete a driver and every owned cars?" + System.getProperty("line.separator")
                            + "Driver Name: '" + driverName + "' (" + count + " owned cars)";
                        break;
                        
                    default:
                        break;
                }                
                
                result = JOptionPane.showConfirmDialog(this, dialogMessage,
                            DELETE_DIALOGTITLE.getContent(), 
                            JOptionPane.YES_NO_OPTION);
            } else {
                String dialogMessage = "";
                
                switch(language){
                    case KOREAN:
                        dialogMessage = "선택 된 " + deleteIndice.length + "명의 운전자정보와 차량 정보를 삭제하시겠습니까?"
                                    + System.getProperty("line.separator")
                                    + "첫번째 운전자 : '" + driverName + "' (소유차량" + count + "대)";
                        break;

                    case ENGLISH:
                        dialogMessage = "Want to delete records of " + deleteIndice.length + " drivers and their car records?"
                                    + System.getProperty("line.separator")
                                    + "First Driver Name: '" + driverName + "' (" + count + " owned cars)";
                        break;
                        
                    default:
                        break;
                }                
                
                result = JOptionPane.showConfirmDialog(this, dialogMessage, 
                            DELETE_DIALOGTITLE.getContent(), 
                            JOptionPane.YES_NO_OPTION);
            }

            if (result == JOptionPane.YES_OPTION) {
                // <editor-fold defaultstate="collapsed" desc="-- delete driver and car information ">   
                Connection conn = null;
                PreparedStatement createBuilding = null;
                String excepMsg = "(while deleting a driver: " + driverName + ")";
                int totalDeletion = 0;

                result = -1;
                try {
                    String sql = "Delete From carDriver Where SEQ_NO = ?";

                    conn = getConnection();
                    createBuilding = conn.prepareStatement(sql);
                    for (int indexNo : deleteIndice) {
                        modal_Index = driverTable.convertRowIndexToModel(indexNo);
                        createBuilding.setInt(1, (int)driverTable.getModel().getValueAt(modal_Index, 
                                DriverCol.SEQ_NO.getNumVal()));
                        result = createBuilding.executeUpdate();
                        totalDeletion += result;
                    }
                } catch (SQLException ex) {
                    logParkingException(Level.SEVERE, ex, excepMsg);
                } finally {
                    closeDBstuff(conn, createBuilding, null, excepMsg);

                    if (result == 1) {
                        loadDriverData(deleteIndice[0], "", ""); // passes index of the deleted row

                        String dialogMessage = "";
                        
                        switch(language){
                            case KOREAN: 
                                dialogMessage = 
                                    driverName +"을 포함한 총 " + totalDeletion + "명의 운전자 정보가 삭제되었습니다.";  
                                break;
                                
                            case ENGLISH: 
                                dialogMessage = "Total " + totalDeletion + " driver(s)" + System.lineSeparator() +
                                                "(including '" + driverName + "') " + System.lineSeparator() +
                                                "deleted successfully.";
                                break;
                                
                            default:
                                break;
                        }                        
                        
                        JOptionPane.showConfirmDialog(this, dialogMessage,
                                DELETE_RESULT_DIALOGTITLE.getContent(),
                                JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
                    }
                }
                //</editor-fold>
            }
        }
    }//GEN-LAST:event_deleteDriver_ButtonActionPerformed

    private void driversTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_driversTableKeyReleased
        if (getFormMode() == FormMode.NormalMode 
                || evt.getKeyCode() == VK_SHIFT ) {
            return; // in view mode, don't need to save update or insertion.
        } else { 
            //<editor-fold defaultstate="collapsed" desc="-- return if cursor stays within a row">
            // just return if the curson is moving inside the same row
            int col = driverTable.getSelectedColumn();
            int row = driverTable.convertRowIndexToModel(driverTable.getSelectedRow());
            
            if (row == modifyingRowM)
            {
                if (driverTable.editCellAt(row, col)) {
                    startEditingCell(row, col);
                }
                return; // driver info is still being updated
            }
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {  
                if (getFormMode() == FormMode.CreateMode ) {
                    finalizeDriverCreation();
                    
                } else {
                    int row = driverTable.convertRowIndexToModel(driverTable.getSelectedRow());
                    finalizeDriverUpdate(row);
                }
            }
        });  
    }//GEN-LAST:event_driversTableKeyReleased

    private void driversTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_driversTableFocusGained

        int row = driverTable. getSelectedRow();
        int col = driverTable. getSelectedColumn();
        
        if (col == -1) {
            col = 2;
        }

        if (getFormMode() == FormMode.UpdateMode) {
            if (modifyingRowM == row) {
                if (driverTable.getValueAt(row, col).toString() != null) {
                    if (driverTable.editCellAt(row, col)) {
                        startEditingCell(row, col);
                    }
                }
            }
        } else {
            if (getFormMode() == FormMode.CreateMode) {
                if (driverTable.getModel().getValueAt(row, 0) == null) {
                    if (driverTable.editCellAt(row, col))
                    {
                        startEditingCell(row, col);
                    }
                }
            }
        }
    }//GEN-LAST:event_driversTableFocusGained

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        loadDriverData(FIRST_ROW, "", "");
    }//GEN-LAST:event_searchButtonActionPerformed

    private void readSheet_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_ButtonActionPerformed
        try {
            int returnVal = odsFileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {                
                File file = odsFileChooser.getSelectedFile();
                ArrayList<Point> wrongCells = new ArrayList<Point>();

                Sheet sheet = null;
                try {
                    sheet = SpreadSheet.createFromFile(file).getSheet(0);
                } catch (IOException ex) {
                    Logger.getLogger(ODSReader.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (sheet != null)
                {
                    ODSReader objODSReader = new ODSReader();

                    WrappedInt driverTotal = new WrappedInt();

                    if (objODSReader.checkDriverODS(sheet, wrongCells, driverTotal))
                    {
                        StringBuilder sb = new StringBuilder();

                        switch(language){
                            case KOREAN:
                                sb.append("자료를 불러오시겟습니까?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -자료 갯수: 운전자 정보 " + driverTotal.getValue() + " 개");
                                break;
                                
                            case ENGLISH:
                                sb.append("Following data has been recognized. Want to load these data?");
                                sb.append(System.getProperty("line.separator"));
                                sb.append(" -Data content: driver records " + driverTotal.getValue() + " rows");
                                break;
                                
                            default:
                                break;
                        }
                        
                        int result = JOptionPane.showConfirmDialog(null, sb.toString(),
//                                getTextFor(READ_ODS_DIALOG, sb, driverTotal.getValue()).toString(),
                                READ_ODS_DIALOGTITLE.getContent(), 
                                JOptionPane.YES_NO_OPTION);            
                        if (result == JOptionPane.YES_OPTION) {                
                            objODSReader.readDriverODS(sheet, this);
                        }
                    } else {
                        // display wrong cell points if existed
                        if (wrongCells.size() > 0) {
                            JOptionPane.showConfirmDialog(null, READ_ODS_FAIL_DIALOG.getContent() 
                                    + System.getProperty("line.separator") + getWrongCellPointString(wrongCells),
                                    READ_ODS_FAIL_DIALOGTITLE.getContent(),
                                    JOptionPane.PLAIN_MESSAGE, WARNING_MESSAGE);                      
                        }                        
                    }
                }
            }
        } catch (Exception ex) {
            logParkingException(Level.SEVERE, ex, 
                    "(User Operation: loading drivers records from an ods file)");
        }         
    }//GEN-LAST:event_readSheet_ButtonActionPerformed

    private void closeFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFormButtonActionPerformed
        closeFrameGracefully();
    }//GEN-LAST:event_closeFormButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeFrameGracefully();
    }//GEN-LAST:event_formWindowClosing

    private void searchL1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchL1ComboBoxActionPerformed
        /**
         * Check if parent combo box is opened by the user -- Patent Requested technology
         */
        if (searchL1ComboBox.isPopupVisible()) {
            // Change the prompter of the next lower level combobox and make it as the selected item
            MutableComboBoxModel model 
                    = (MutableComboBoxModel)searchL2ComboBox.getModel();
            model.removeElementAt(0);
            model.insertElementAt(getPrompter(AffiliationL2, searchL1ComboBox), 0);
            searchL2ComboBox.setSelectedIndex(0);            
        }
    }//GEN-LAST:event_searchL1ComboBoxActionPerformed

    private void searchBuildingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxActionPerformed
        if (searchBuildingComboBox.isPopupVisible()) {
            MutableComboBoxModel model 
                    = (MutableComboBoxModel)searchUnitComboBox.getModel();
            model.removeElementAt(0);
            model.insertElementAt(getPrompter(UnitNo, searchBuildingComboBox), 0);
            searchUnitComboBox.setSelectedIndex(0);            
        }
    }//GEN-LAST:event_searchBuildingComboBoxActionPerformed

    private void searchL1ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL1ComboBoxPopupMenuWillBecomeVisible
        Object selItem = searchL1ComboBox.getSelectedItem();

        searchL1ComboBox.removeAllItems();
        searchL1ComboBox.addItem(getPrompter(AffiliationL1, searchL1ComboBox));     
        loadComboBoxItems(searchL1ComboBox, AffiliationL1, -1);
        searchL1ComboBox.setSelectedItem(selItem);        
    }//GEN-LAST:event_searchL1ComboBoxPopupMenuWillBecomeVisible

    private void searchL2ComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeVisible
        Object selItem = searchL2ComboBox.getSelectedItem();
        
        ConvComboBoxItem l1Item = (ConvComboBoxItem)searchL1ComboBox.getSelectedItem(); 
        int L1No = (Integer) l1Item.getValue();        // normalize child combobox item 
        searchL2ComboBox.removeAllItems();
        searchL2ComboBox.addItem(getPrompter(AffiliationL2, searchL1ComboBox));     
        loadComboBoxItems(searchL2ComboBox, DriverCol.AffiliationL2, L1No); 
        searchL2ComboBox.setSelectedItem(selItem);        
    }//GEN-LAST:event_searchL2ComboBoxPopupMenuWillBecomeVisible

    private void searchBuildingComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxPopupMenuWillBecomeVisible
        Object selItem = searchBuildingComboBox.getSelectedItem();
        
        searchBuildingComboBox.removeAllItems();
        searchBuildingComboBox.addItem(getPrompter(BuildingNo, null));     
        loadComboBoxItems(searchBuildingComboBox, BuildingNo, -1);
        searchBuildingComboBox.setSelectedItem(selItem); 
    }//GEN-LAST:event_searchBuildingComboBoxPopupMenuWillBecomeVisible

    private void searchUnitComboBoxPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeVisible
        loadUnitComboBox(searchL1ComboBox, searchBuildingComboBox, searchUnitComboBox);            
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeVisible

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearButton.setEnabled(false);
        
        showNameTip();
        showCellTip();
        showPhoneTip();
        
        searchPhone.setText(LANDLINE_TF.getContent());
        searchL1ComboBox.setSelectedIndex(0);
        searchL2ComboBox.setSelectedIndex(0);
        searchBuildingComboBox.setSelectedIndex(0);
        searchUnitComboBox.setSelectedIndex(0);
        
        changeSearchButtonEnabled();
        driverTable.requestFocus();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void showNameTip() {
        searchName.setText(DRIVER_TF.getContent());
        nameHintShown = true;
        searchName.setForeground(tipColor);
    }
    
    private void searchNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameFocusLost
        if (searchName.getText().trim().length() == 0) {
            showNameTip();
        }           
    }//GEN-LAST:event_searchNameFocusLost

    private void searchNameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchNameMousePressed
        searchName.selectAll();
    }//GEN-LAST:event_searchNameMousePressed

    private void showCellTip() {
        searchCell.setText(CELL_PHONE_TF.getContent());
        cellHintShown = true;
        searchCell.setForeground(tipColor);
    }
            
    private void searchCellFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCellFocusLost
        if (searchCell.getText().trim().length() == 0) {
            showCellTip();
        }           
    }//GEN-LAST:event_searchCellFocusLost
    
    private void showPhoneTip() {
        searchPhone.setText(LANDLINE_TF.getContent());
        phoneHintShown = true;
        searchPhone.setForeground(tipColor);
    }

    private void searchCellMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchCellMousePressed
        searchCell.selectAll();
    }//GEN-LAST:event_searchCellMousePressed

    private void searchPhoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchPhoneFocusLost
        if (searchPhone.getText().trim().length() == 0) {
            showPhoneTip();
        }         
    }//GEN-LAST:event_searchPhoneFocusLost

    private void searchPhoneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchPhoneMousePressed
        searchPhone.selectAll();
    }//GEN-LAST:event_searchPhoneMousePressed

    private void deleteDriver_Button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDriver_Button2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteDriver_Button2ActionPerformed

    private void readSheet_Button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readSheet_Button1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_readSheet_Button1ActionPerformed

    private void seeLicenseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seeLicenseButtonActionPerformed
        showLicensePanel(this, "License Notice on Vehicle Manager");
    }//GEN-LAST:event_seeLicenseButtonActionPerformed

    private void searchNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchNameFocusGained
        if (searchName.getText().equals(DRIVER_TF.getContent())) {
            searchName.setText("");
            nameHintShown = false;
            searchName.setForeground(new Color(0, 0, 0));
        }
        searchName.getInputContext().selectInputMethod(Locale.KOREA);
    }//GEN-LAST:event_searchNameFocusGained

    private void searchNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNameKeyReleased
        if (++releaseCount >= 2) 
        {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    searchButton.setEnabled(true);
                    releaseCount = 0;
                }
            });        
        }
    }//GEN-LAST:event_searchNameKeyReleased

    private void searchNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchNameKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });   
    }//GEN-LAST:event_searchNameKeyTyped

    private void searchCellFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchCellFocusGained
        if (searchCell.getText().equals(CELL_PHONE_TF.getContent())) {
            searchCell.setText("");
            cellHintShown = false;            
            searchCell.setForeground(new Color(0, 0, 0));
        }        
    }//GEN-LAST:event_searchCellFocusGained

    private void searchCellKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchCellKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });           
    }//GEN-LAST:event_searchCellKeyTyped

    private void searchPhoneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_searchPhoneFocusGained
        if (searchPhone.getText().equals(LANDLINE_TF.getContent())) {
            searchPhone.setText("");
            phoneHintShown = false;            
            searchPhone.setForeground(new Color(0, 0, 0));
        }
    }//GEN-LAST:event_searchPhoneFocusGained

    private void searchPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchPhoneKeyTyped
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });
    }//GEN-LAST:event_searchPhoneKeyTyped

    private void searchNameInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_searchNameInputMethodTextChanged
        System.out.println("text changed");
    }//GEN-LAST:event_searchNameInputMethodTextChanged

    private void searchL1ComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL1ComboBoxPopupMenuWillBecomeInvisible
        changeSearchButtonEnabled();
    }//GEN-LAST:event_searchL1ComboBoxPopupMenuWillBecomeInvisible

    private void searchL2ComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchL2ComboBoxPopupMenuWillBecomeInvisible
    }//GEN-LAST:event_searchL2ComboBoxPopupMenuWillBecomeInvisible

    private void searchBuildingComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchBuildingComboBoxPopupMenuWillBecomeInvisible
        changeSearchButtonEnabled();
    }//GEN-LAST:event_searchBuildingComboBoxPopupMenuWillBecomeInvisible

    private void searchUnitComboBoxPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_searchUnitComboBoxPopupMenuWillBecomeInvisible
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                changeSearchButtonEnabled();
            }
        });        
    }//GEN-LAST:event_searchUnitComboBoxPopupMenuWillBecomeInvisible

    public static void loadUnitComboBox(JComboBox level1ComboBox, JComboBox buildingComboBox,
            JComboBox unitComboBox) {
        
        Object selItem = unitComboBox.getSelectedItem();
        
        ConvComboBoxItem bldgItem
                = (ConvComboBoxItem)buildingComboBox.getSelectedItem(); 
        int bldgNo = (Integer) bldgItem.getValue();        // normalize child combobox item 
        
        unitComboBox.removeAllItems();
        unitComboBox.addItem(getPrompter(UnitNo, buildingComboBox));     
        loadComboBoxItems(unitComboBox, UnitNo, bldgNo);
        unitComboBox.setSelectedItem(selItem);         
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageDrivers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        initializeLoggers();
        checkOptions(args);
        readSettings();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ManageDrivers mainForm = new ManageDrivers(null);
                mainForm.setVisible(true);
            }
        });
    }
    
    static ConvComboBoxItem prevItem = null;
    static int prevRow = -1;
    static int prevCol = -1;
    
    final int FIRST_ROW = 0;
    final int UNKNOWN = -1;
    
    /**
     * load driver list on the driver table view. Either viewIndex or driverName and cellPhone
     * pair is used to determine the row to select after the loading is finished.
     * 
     * @param viewIndex row number to highlight after loading done
     * @param driverName with cellPhone, part of record key to highlight after loading done
     * @param cellPhone with driverName, part of record key to highlight after loading done
     */
    public void loadDriverData(int viewIndex, String driverName, String cellPhone) {

        Connection conn = null;
        Statement selectStmt = null;
        ResultSet rs = null;
        String excepMsg = "(vehicle driver infomation loading)";

        DefaultTableModel model = (DefaultTableModel) driverTable.getModel();  
        int model_Index = 0;
        
        try {
            model.setRowCount(0);
            // <editor-fold defaultstate="collapsed" desc="-- load car driver list">     
            // <editor-fold defaultstate="collapsed" desc="-- construct SQL statement">  
            StringBuffer sb = new StringBuffer(); 
            sb.append("SELECT @ROWNUM := @ROWNUM + 1 recNo, TA.* ");
            sb.append("FROM (SELECT CD.NAME,  CD.CELLPHONE, CD.PHONE,"); 
            sb.append("    L1.PARTY_NAME AS L1_NAME, L2.PARTY_NAME L2_NAME,");
            sb.append("    BT.BLDG_NO, BU.UNIT_NO, CD.SEQ_NO CD_SEQ_NO,");
            sb.append("    L2.L1_NO, CD.L2_NO, BT.SEQ_NO B_SEQ_NO,");
            sb.append("    CD.UNIT_SEQ_NO U_SEQ_NO");
            sb.append("  FROM CARDRIVER CD");
            sb.append("  LEFT JOIN L2_affiliation L2 ON CD.L2_NO = L2.L2_NO");
            sb.append("  LEFT JOIN L1_affiliation L1 ON L2.L1_NO = L1.L1_NO");
            sb.append("  LEFT JOIN building_unit BU ON UNIT_SEQ_NO = BU.SEQ_NO");
            sb.append("  LEFT JOIN building_table BT ON BLDG_SEQ_NO = BT.SEQ_NO) TA,");
            sb.append("  (SELECT @rownum := 0) r ");
            prevSearchCondition = formSearchCondition();
            sb.append(prevSearchCondition);
            sb.append(" ORDER BY NAME, L1_NAME, TA.L2_NAME, TA.BLDG_NO, TA.UNIT_NO");
            //</editor-fold>
            
            conn = getConnection();
            selectStmt = conn.createStatement();
            rs = selectStmt.executeQuery(sb.toString());
            while (rs.next()) {
                if (viewIndex == UNKNOWN) // refreshing list after a new driver insertion
                {
                    if (driverName.equals(rs.getString("NAME")) &&
                            cellPhone.equals(rs.getString("CELLPHONE")))
                    {
                        model_Index = model.getRowCount();
                    }
                }
                //<editor-fold defaultstate="collapsed" desc="-- construct a driver info' to show"> 
                Object L1Item = null;
                if (rs.getString("L1_NAME") == null) 
                    L1Item = ManageDrivers.getPrompter(AffiliationL1, null);
                else 
                    L1Item = new ConvComboBoxItem(rs.getInt("L1_NO"), rs.getString("L1_NAME"));
                
                Object  L2Item = null;
                if (rs.getString("L2_NAME") == null) 
                    L2Item = ManageDrivers.getPrompter(AffiliationL2, L1Item);
                else
                    L2Item = new InnoComboBoxItem(
                            new int[]{rs.getInt("L2_NO")}, new String[]{rs.getString("L2_NAME")});
                
                Object bldgItem = null;
                if (rs.getString("BLDG_NO") == null)
                    bldgItem = ManageDrivers.getPrompter(BuildingNo, null);
                else
                    bldgItem = new ConvComboBoxItem(
                            rs.getInt("B_SEQ_NO"), rs.getString("BLDG_NO"));
                
                Object unitItem = null;
                if (rs.getString("UNIT_NO") == null)
                    unitItem = ManageDrivers.getPrompter(UnitNo, bldgItem);
                else
                    unitItem = new InnoComboBoxItem(
                            new int[]{rs.getInt("U_SEQ_NO")}, new String[] {rs.getString("UNIT_NO")});
                
                model.addRow(new Object[] {
                     rs.getInt("recNo"),   rs.getString("NAME"), rs.getString("CELLPHONE"),
                     rs.getString("PHONE"),  L1Item, L2Item, bldgItem, unitItem,
                     rs.getInt("CD_SEQ_NO")
                });
                //</editor-fold>
            }
            //</editor-fold>
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {            
            closeDBstuff(conn, selectStmt, rs, excepMsg);
            countValue.setText(String.valueOf(driverTable.getRowCount()));                        
        }
        setFormMode(FormMode.NormalMode);
            
        int numRows = model.getRowCount();
        
        if (numRows > 0) {  
            deleteAllDrivers.setEnabled(true);
            // <editor-fold defaultstate="collapsed" desc="-- Highlight a selected driver">                          
            if (driverName.length() > 0) {
                viewIndex = driverTable.convertRowIndexToView(model_Index);
            } else if (viewIndex == numRows) {
                // If the index of the deleted record is the same as the number of remaining records,
                // then it means that the record deleted were the last row within the list of records.
                // In this case, display(highlight) the row just above the deleted one.
                viewIndex--;
            }
            if (0 <= viewIndex && viewIndex < numRows)
                highlightTableRow(driverTable, viewIndex);
            //</editor-fold>
        }
        createDriver_Button.setEnabled(true);
        searchButton.setEnabled(false);        
    }
        
    // <editor-fold defaultstate="collapsed" desc="-- Netbeans Generated Control Item Variables ">                               
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomButtonPanel;
    private javax.swing.JButton clearButton;
    public javax.swing.JButton closeFormButton;
    private javax.swing.JLabel countLbl;
    private javax.swing.JLabel countValue;
    public javax.swing.JButton createDriver_Button;
    private javax.swing.JButton deleteAllDrivers;
    private javax.swing.JButton deleteDriver_Button;
    private javax.swing.JButton deleteDriver_Button2;
    private javax.swing.JScrollPane driversScrollPane;
    public static javax.swing.JTable driversTable;
    private javax.swing.JPanel eastPanel;
    private javax.swing.Box.Filler filler15_5;
    private javax.swing.Box.Filler filler15_6;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler66;
    private javax.swing.JLabel formModeLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel leftButtons;
    private javax.swing.JButton modifyDriver_Button;
    private javax.swing.JLabel myMetaKeyLabel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JFileChooser odsFileChooser;
    private javax.swing.JButton readSheet_Button;
    private javax.swing.JButton readSheet_Button1;
    private javax.swing.JLabel requiredLabel;
    private javax.swing.JPanel rightButtons;
    private javax.swing.JComboBox searchBuildingComboBox;
    public javax.swing.JButton searchButton;
    private javax.swing.JTextField searchCell;
    private javax.swing.JComboBox searchL1ComboBox;
    private javax.swing.JComboBox searchL2ComboBox;
    private javax.swing.JTextField searchName;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField searchPhone;
    private javax.swing.JComboBox searchUnitComboBox;
    private javax.swing.JButton seeLicenseButton;
    private javax.swing.JPanel southPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JPanel topButtonPanel;
    private javax.swing.JPanel topLTpanel;
    private javax.swing.JPanel topMid_1;
    private javax.swing.JPanel topMid_2;
    private javax.swing.JPanel topRHpanel;
    private javax.swing.JPanel westPanel;
    private javax.swing.JPanel wholePanel;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>       
    
    static DriverTable driverTable = (DriverTable) driversTable;

    private void addDoubleClickEventListener() {
        driverTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                Point p = me.getPoint();
                if (table.columnAtPoint(p) == 0) {
                    return;
                }
                else
                {
                    if (me.getClickCount() == 2) {
                        if (emptyLastRowPossible(createDriver_Button, driverTable)) {
                            removeEmptyRow(createDriver_Button, driverTable);
                        }                        
                        
                        if (getFormMode() != FormMode.UpdateMode) {
                            int rowV = table.rowAtPoint(p);
                            int colV = table.columnAtPoint(p);
                            ((DriverTable)driverTable).userWantsToUpdateRow(rowV, colV);
                            // Open the combobox here for easy use
                            Object obj2 = (driverTable.getCellEditor(rowV, colV));
                            DefaultCellEditor box = (DefaultCellEditor)obj2;
                            Object obj3 = box.getComponent();
                            ((PComboBox)obj3).setPopupVisible(true) ;
                        }
                    }
                }
            }
        });        
    }
    
    void setUpdateMode (boolean toModify) {
        setFormMode(toModify ? FormMode.UpdateMode : FormMode.NormalMode);
        if (toModify) {
            modifyDriver_Button.setText(SAVE_BTN.getContent());
            deleteDriver_Button.setText(CANCEL_BTN.getContent());
            deleteDriver_Button.setEnabled(true);
            createDriver_Button.setEnabled(false);        }
        else {
            modifyDriver_Button.setText(MODIFY_BTN.getContent());
            deleteDriver_Button.setText(DELETE_BTN.getContent());
            deleteDriver_Button.setEnabled(false);            
        }
        requiredLabel.setVisible(toModify);
    }

    public int saveModifiedDriverInfo(StringBuffer driverProperties) {
        TableModel drvModel = driverTable.getModel();
        int index = modifyingRowM;
        Object driverName = drvModel.getValueAt(index, 1);
        
        int result = 0;
        Object cellPhone = (String)drvModel.getValueAt(index, 2);

        Connection conn = null;
        PreparedStatement modifyDriver = null;
        String excepMsg = "modifying info for (<name, cellphone>: " + driverName + ", " 
                +  (String)cellPhone + ")";

        try {
            // <editor-fold defaultstate="collapsed" desc="-- Driver information update">                          
            StringBuffer sb = new StringBuffer("Update cardriver Set NAME = ?,");
            sb.append(" CELLPHONE = ?, phone = ?, L2_NO = ?, UNIT_SEQ_NO = ?");
            sb.append(" Where SEQ_NO = ?");

            conn = getConnection();
            modifyDriver = conn.prepareStatement(sb.toString());
            modifyDriver.setString(DriverName.getNumVal(), (String)driverName);
            
            driverProperties.append("Driver Update Summary: " + System.lineSeparator());
//            getDriverProperties((String)driverName, (String)cellPhone, driverProperties, modifyingRowM);  
            
            modifyDriver.setString(CellPhone.getNumVal(), (String)cellPhone); 
            modifyDriver.setString(LandLine.getNumVal(), (String)drvModel.getValueAt(index, LandLine.getNumVal()));

            // prepare affiliation level 2 key(L2_NO) value to store
            String item1 = drvModel.getValueAt(index, AffiliationL1.getNumVal()).toString();
            InnoComboBoxItem item = (InnoComboBoxItem)drvModel.getValueAt(index, AffiliationL2.getNumVal());

            if (item.toString().equals(LOWER_CB_ITEM.getContent())
                    || item.toString().equals(LOWER_HIGHER_CB_ITEM.getContent())) {
                modifyDriver.setString(AffiliationL1.getNumVal(), null); // level 1 not selected
            } else {
                int L2_NO = (Integer)item.getKeys()[0];
                if (L2_NO == -1) {
                    modifyDriver.setString(AffiliationL2.getNumVal(), null); // level 2 not selected
                } else {
                    modifyDriver.setInt(AffiliationL1.getNumVal(), L2_NO); // both level 1 and 2 selected
                }
            }
                
            // prepare building unit key(SEQ_NO) value to store
            item = (InnoComboBoxItem)drvModel.getValueAt(index, UnitNo.getNumVal());
            if (item.toString().equals(ROOM_CB_ITEM.getContent())
                    || item.toString().equals(ROOM_BUILDING_CB_ITEM.getContent())) {
                modifyDriver.setString(5, null); // building not selected
            } else {
                int SEQ_NO = (Integer)item.getKeys()[0];
                if (SEQ_NO == -1) {
                    modifyDriver.setString(BuildingNo.getNumVal(), null); // building unit not selected
                } else {
                    modifyDriver.setInt(BuildingNo.getNumVal(), SEQ_NO); // both building and its unit selected                        
                }
            }                
            modifyDriver.setInt(6, (Integer)drvModel.getValueAt(index, DriverCol.SEQ_NO.getNumVal()));

            result = modifyDriver.executeUpdate();
            //</editor-fold>
        } catch (SQLException ex) {
            // <editor-fold defaultstate="collapsed" desc="-- handle exception">                                          
            if (ex.getErrorCode() == ER_DUP_ENTRY) {
                rejectUserInput(driverTable, index, excepMsg);                     
            }
            else {
                logParkingException(Level.SEVERE, ex, excepMsg);
                JOptionPane.showConfirmDialog(null, "see log/exception folder for details.",
                        "Driver Update Failure", JOptionPane.PLAIN_MESSAGE, 
                        WARNING_MESSAGE);                
            }
            //</editor-fold>                
        } finally {
            closeDBstuff(conn, modifyDriver, null, excepMsg);                             
        }     
        return result;
    }


    private void changeDriverTable() {
        Object[][] data = { /*{1, "Henry Ford", "452-1234-5678", "567-1111-2222", {"Engineering", 2},
            {"Mechanical Engr.", 4}, {"1", 162}, {"101", 5024}, 3} */            
        };
        String[] columnNames = {
            ORDER_HEADER.getContent(), 
            NAME_HEADER.getContent(), 
            CELL_PHONE_HEADER.getContent(), 
            PHONE_HEADER.getContent(), 
            HIGHER_HEADER.getContent(), 
            LOWER_HEADER.getContent(), 
            BUILDING_HEADER.getContent(), 
            ROOM_HEADER.getContent(), 
            "CD_SEQ_NO"};
        driverTable = new DriverTable(data, columnNames, this);
        driverTable.setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driverTable.getTableHeader().setFont(new java.awt.Font(font_Type, font_Style, font_Size));
        driverTable.setAutoCreateRowSorter(true);
        
        driverTable.setSurrendersFocusOnKeystroke(true);
        
        driverTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                driversTableFocusGained(evt);
            }
        });
        driverTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                driversTableKeyReleased(evt);
            }
        });

        driversScrollPane.setViewportView(driverTable); 
        
        // Hide drivers table sequence number which is used by only inside the code
        TableColumnModel NumberTableModel = driverTable.getColumnModel();
        NumberTableModel.removeColumn(NumberTableModel.getColumn(
                DriverCol.SEQ_NO.getNumVal()));        
        
        driverTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);        
        
        // <editor-fold defaultstate="collapsed" desc="-- Adjust Column Width ">                    
        TableColumnModel tcm = driverTable.getColumnModel();
        
        // Adjust column width one by one
        SetAColumnWidth(tcm.getColumn(0), 10, 80, 80); // 0: row number
        SetAColumnWidth(tcm.getColumn(1), 10, 110, 350); // 1: driver name
        SetAColumnWidth(tcm.getColumn(2), 10, 120, 140); // 2: cell phone
        SetAColumnWidth(tcm.getColumn(3), 10, 120, 140); // 3: land line
        SetAColumnWidth(tcm.getColumn(4), 10, 110, 340); // 4: affiliation level 1
        SetAColumnWidth(tcm.getColumn(5), 10, 110, 340); // 5: affiliation level 2
        SetAColumnWidth(tcm.getColumn(6), 10, 110, 340); // 6: building number
        SetAColumnWidth(tcm.getColumn(7), 10, 110, 340); // 7: building unit number  
        //</editor-fold>
        
        addDriverSelectionListener();
//        addDoubleClickEventListener();        
    }

    /**
     * Handles driver information update trial in one of the following three ways
     * 1. requires the user enter driver name information
     * 2. requires the user enter driver cell phone number
     * 3. update driver information in the database in case everything is OK
     * @param nextRowV Row number to highlight after update operation
     */
    public void finalizeDriverUpdate(int nextRowV) {
        int row = modifyingRowM;
        int col1 = driverTable.convertColumnIndexToModel(DriverCol.DriverName.getNumVal());
        
        // conditions that make driver information insufficient: 1, 2        
        String name = ((String)driverTable.getModel().getValueAt(row, col1)).trim(); 
        int col2 = driverTable.convertColumnIndexToModel(DriverCol.CellPhone.getNumVal());        
        String cell = ((String)driverTable.getModel().getValueAt(row, col2)).trim();    
        String L1_item = driverTable.getValueAt(row, AffiliationL1.getNumVal()).toString();
        InnoComboBoxItem L2_item = (InnoComboBoxItem)driverTable.getValueAt(row, AffiliationL2.getNumVal());
        int L2_NO = (Integer) L2_item.getKeys()[0];
        String building_item = driverTable.getValueAt(row, BuildingNo.getNumVal()).toString();
        InnoComboBoxItem unit_item = (InnoComboBoxItem)driverTable.getValueAt(row, UnitNo.getNumVal());
        int SEQ_NO = (Integer)unit_item.getKeys()[0];

        Object[] options = new Object[2];
        
        switch(language){
            case KOREAN:
                options[0] = "예(입력)";
                options[1] = "아니요(종료)";
                break;
                
            case ENGLISH:
                options[0] = "Yes(input)";
                options[1] = "No(discard)";
                break;
                
            default:
                break;
        }
                
        // 1. driver name isn't provided
        if (name == null || name.trim().length() == 0)
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing driver name">   
            int response = JOptionPane.showOptionDialog(this, DRIVER_NAME_CHECK_DIALOG.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, options[0]);                    
        
            if (response == JOptionPane.YES_OPTION) 
            {
                if (driverTable.editCellAt(row, col1))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, col1);
                    selectionListenerDisabled = false;
                }                  
            } else {
                setUpdateMode(false);
                loadDriverData(nextRowV, "", "");
            }            
            //</editor-fold>    
        }   
        else if (cell == null || cell.trim().length() == 0) 
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing cell phone">   
            int response = JOptionPane.showOptionDialog(this, DRIVER_CELL_CHECK_DIALOG.getContent(),
//                    ((String[])Globals.DialogMSGList.get(DRIVER_CELL_CHECK_DIALOG.getContent(), 
                    WARING_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, options[0]);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                if (driverTable.editCellAt(row, col2))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, col2);
                    selectionListenerDisabled = false;
                }                  

            } else {
                setUpdateMode(false);
                loadDriverData(nextRowV, "", "");
            }            
            //</editor-fold>            
        } 
        else if(!L1_item.equals(HIGHER_CB_ITEM.getContent()) 
                        && L2_NO == -1)
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing L2 item"> 
            int respone = JOptionPane.showConfirmDialog(null, L2_INPUT_DIALOG.getContent(),
//                                    ((String[])Globals.DialogMSGList.get(L2_INPUT_DIALOG.getContent(),
                                    ERROR_DIALOGTITLE.getContent(),
                                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);
            if(respone == JOptionPane.YES_OPTION){
                //수정
                col2 = driverTable.convertColumnIndexToModel(
                            DriverCol.AffiliationL2.getNumVal());  
                if (driverTable.editCellAt(row, col2))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, col2);
                    selectionListenerDisabled = false;
                }
            }else{
                //L1을 초기값으로 바꾸고 저장
                col2 = driverTable.convertColumnIndexToModel(
                        DriverCol.AffiliationL1.getNumVal());  
                driverTable.setValueAt(ManageDrivers.getPrompter(AffiliationL1, null), row, col2);
                if (driverTable.editCellAt(row, col2))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, col2);
                    selectionListenerDisabled = false;
                }
            }
            
            //</editor-fold>   
        }
        else if(!building_item.equals(BUILDING_CB_ITEM.getContent()) 
                        && SEQ_NO == -1)
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing Unit item"> 
            int respone = JOptionPane.showConfirmDialog(null, UNIT_INPUTDIALOG.getContent(),
//                                    ((String[])Globals.DialogMSGList.get(UNIT_INPUTDIALOG.getContent(),
                                    ERROR_DIALOGTITLE.getContent(),
                                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);
            if(respone == JOptionPane.YES_OPTION){
                //수정
                col2 = driverTable.convertColumnIndexToModel(
                            DriverCol.UnitNo.getNumVal());  
                if (driverTable.editCellAt(row, col2))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, col2);
                    selectionListenerDisabled = false;
                }
            }else{
                //Building을 초기값으로 바꾸고 저장
                col2 = driverTable.convertColumnIndexToModel(
                        DriverCol.BuildingNo.getNumVal());    
                driverTable.setValueAt(ManageDrivers.getPrompter(BuildingNo, null), row, col2);
                if (driverTable.editCellAt(row, col2))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, col2);
                    selectionListenerDisabled = false;
                }
            }
            
            //</editor-fold>   
        }
        else // both driver name and his/her cell phone number exists
        {
            // <editor-fold defaultstate="collapsed" desc="-- save modified driver info">  
            int response = JOptionPane.showOptionDialog(this, 
                    USER_UPDATE_SUCCESS_DIALOG.getContent() + name,
                    SAVE_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, 
                    null, null, null);
            
            TableModel drvModel = driverTable.getModel();
            InnoComboBoxItem L2_Item = (InnoComboBoxItem)drvModel.getValueAt(
                    modifyingRowM, AffiliationL2.getNumVal());
            InnoComboBoxItem unit_Item = (InnoComboBoxItem)drvModel.getValueAt(
                    modifyingRowM, UnitNo.getNumVal());
            if (response == 0) {
                StringBuffer driverProperties = new StringBuffer();
                if (saveModifiedDriverInfo(driverProperties) == 1) {
                    // if insertion was successful, then redisplay the list
                    setFormMode(FormMode.NormalMode);

                    int rowM = driverTable.convertRowIndexToModel(nextRowV);
                    col1 = driverTable.convertColumnIndexToModel(
                            DriverCol.DriverName.getNumVal());

                    // conditions that make driver information insufficient: 1, 2        
                    name = ((String)driverTable.getModel().getValueAt(rowM, col1)).trim();
                    col2 = driverTable.convertColumnIndexToModel(
                            DriverCol.CellPhone.getNumVal());        
                    cell = ((String)driverTable.getModel().getValueAt(rowM, col2)).trim();                       
                    
                    loadDriverData(UNKNOWN, name, cell); // in case, name and/or cell changed
                    
                    // also redisplay driver selection form this form invoked from it
                    if (driverSelectionForm != null) {
                        driverSelectionForm.loadSkinnyDriverTable(0); // 0: highlight first row
                    }
                    logParkingOperation(OSP_enums.OpLogLevel.SettingsChange, driverProperties.toString());
                }
            } else {
                // load original data back again 
                setFormMode(FormMode.NormalMode);
                loadDriverData(UNKNOWN, name, cell);
            }
            driverTable.requestFocusInWindow();
            //</editor-fold>            
        }
    }    

//    private void removeRealEmptyRow() {
//        JOptionPane.showConfirmDialog(this, DRIVER_CREATE_FAIL_DIALOG.getContent(),
////                ((String[])Globals.DialogMSGList.get(DRIVER_CREATE_FAIL_DIALOG.getContent(), 
//                CREATTION_FAIL_DIALOGTITLE.getContent(), 
//                JOptionPane.PLAIN_MESSAGE, INFORMATION_MESSAGE);
//        
//        // remove last row which lacks driver name
//        ((DefaultTableModel)driverTable.getModel())
//                .setRowCount(driverTable.getRowCount() - 1); 
//    }       
    
    /**
     * Handles new driver information insertion trial in one of the following three ways
     * 1. removes new driver information row from the table when name is not provided
     * 2. asks driver cell phone number when it is not provided
     * 3. insert new driver information into the database table in case everything is fine.
     */
    public void finalizeDriverCreation() {
        int row = driverTable.getRowCount() - 1;
        int colName = driverTable.convertColumnIndexToModel(DriverCol.DriverName.getNumVal());
        
        // conditions that make driver information insufficient: 1, 2
        String name = ((String)driverTable.getModel().getValueAt(row, colName)).trim(); 
        int colCell = driverTable.convertColumnIndexToModel(DriverCol.CellPhone.getNumVal());
        String cell = String.valueOf(driverTable.getValueAt(row, colCell)).toLowerCase();
        String L1_item = driverTable.getValueAt(row, AffiliationL1.getNumVal()).toString();
        InnoComboBoxItem L2_item = (InnoComboBoxItem)driverTable.getValueAt(row, AffiliationL2.getNumVal());
        int L2_NO = (Integer) L2_item.getKeys()[0];
        String building_item = driverTable.getValueAt(row, BuildingNo.getNumVal()).toString();
        InnoComboBoxItem unit_item = (InnoComboBoxItem)driverTable.getValueAt(row, UnitNo.getNumVal());
        int SEQ_NO = (Integer)unit_item.getKeys()[0];
        // 1. driver name isn't provided
        if (name == null || name.trim().length() == 0)
        {
            // without driver name, system just discard such insertion request with a warning
//            setFormMode(FormMode.NormalMode);
//            removeRealEmptyRow();
            
            // <editor-fold defaultstate="collapsed" desc="-- handle missing cell phone">   
            // it has driver's name, but not his/her cell phone number  
            int response = JOptionPane.showConfirmDialog(null, MISSING_NAME_HANDLING.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION);                    
        
            if (response == JOptionPane.YES_OPTION) 
            {
                if (driverTable.editCellAt(row, colName))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, colName);
                    selectionListenerDisabled = false;
                }                  

            } else {
                setFormMode(FormMode.NormalMode);
                ((DefaultTableModel)driverTable.getModel())
                        .setRowCount(driverTable.getRowCount() - 1);             
            }            
            //</editor-fold>               
        } 
        // 2. driver cell phone isn't provided
        else if (cell == null || cell.trim().length() == 0) 
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing cell phone">   
            // it has driver's name, but not his/her cell phone number  
            int response = JOptionPane.showConfirmDialog(null, DRIVER_CREATE_CHECK_CELL_DIALOG.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION);                    
        
            if (response == JOptionPane.YES_OPTION) 
            {
                if (driverTable.editCellAt(row, colCell))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, colCell);
                    selectionListenerDisabled = false;
                }                  

            } else {
                setFormMode(FormMode.NormalMode);
                ((DefaultTableModel)driverTable.getModel())
                        .setRowCount(driverTable.getRowCount() - 1);             
            }            
            //</editor-fold>          
        } 
        else if(!L1_item.equals(HIGHER_CB_ITEM.getContent()) 
                        && L2_NO == -1)
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing L2 item"> 
            int respone = JOptionPane.showConfirmDialog(null, L2_INPUT_DIALOG.getContent(),
//                                    ((String[])Globals.DialogMSGList.get(L2_INPUT_DIALOG.getContent(),
                                    ERROR_DIALOGTITLE.getContent(),
                                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);
            if(respone == JOptionPane.YES_OPTION){
                //수정
                colName = driverTable.convertColumnIndexToModel(
                            DriverCol.AffiliationL2.getNumVal());  
                if (driverTable.editCellAt(row, colName))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, colName);
                    selectionListenerDisabled = false;
                }
            }else{
                //L1을 초기값으로 생성
                colName = driverTable.convertColumnIndexToModel(
                        DriverCol.AffiliationL1.getNumVal());  
                driverTable.setValueAt(ManageDrivers.getPrompter(AffiliationL1, null), row, colName);
                if (driverTable.editCellAt(row, colName))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, colName);
                    selectionListenerDisabled = false;
                }
            }
            
            //</editor-fold>   
        }
        else if(!building_item.equals(BUILDING_CB_ITEM.getContent()) 
                        && SEQ_NO == -1)
        {
            // <editor-fold defaultstate="collapsed" desc="-- handle missing Unit item"> 
            int respone = JOptionPane.showConfirmDialog(null, UNIT_INPUTDIALOG.getContent(),
//                                    ((String[])Globals.DialogMSGList.get(UNIT_INPUTDIALOG.getContent(),
                                    ERROR_DIALOGTITLE.getContent(),
                                    JOptionPane.YES_NO_OPTION, WARNING_MESSAGE);
            if(respone == JOptionPane.YES_OPTION){
                //수정
                colName = driverTable.convertColumnIndexToModel(
                            DriverCol.UnitNo.getNumVal());  
                if (driverTable.editCellAt(row, colName))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, colName);
                    selectionListenerDisabled = false;
                }
            }else{
                //Building을 초기값으로 바꾸고 저장
                colName = driverTable.convertColumnIndexToModel(
                        DriverCol.BuildingNo.getNumVal());    
                driverTable.setValueAt(ManageDrivers.getPrompter(BuildingNo, null), row, colName);
                if (driverTable.editCellAt(row, colName))
                {
                    selectionListenerDisabled = true;
                    highlightTableRow(driverTable, row);
                    startEditingCell(row, colName);
                    selectionListenerDisabled = false;
                }
            }
            
            //</editor-fold>   
        }
        else // both driver name and his/her cell phone number are supplied
        {
            int response = JOptionPane.showConfirmDialog(this, 
                    USER_CREATE_SUCCESS_DIALOG.getContent() + name,
                    SAVE_DIALOGTITLE.getContent(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION){
                createNewDriver(name, row);
            }
        }
        driverTable.requestFocusInWindow();
    }

    static boolean comboboxRippleEffectStop = false;
        
    public void addItemChangeListener(JComboBox comboBox) {
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (!((PComboBox)e.getSource()).isPopupVisible())
                        return;
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() { 
                            comboboxRippleEffectStop = true;

                            int rowV = driverTable.getSelectedRow();
                            int rowM = driverTable.convertRowIndexToModel(rowV);

                            int colV = driverTable.getSelectedColumn();
                            int colM = driverTable.convertColumnIndexToModel(colV);

                            if (rowV >= 0 && colV >= 0)
                                handleItemChange(rowV, rowM, colV, colM);                                
                            comboboxRippleEffectStop = false;
                        }
                    });  
                }
            }
        });          
    }    
    
    private void setupComboBoxColumn(DriverCol column) {
        TableColumn comboCol 
                = driverTable.getColumnModel().getColumn(column.getNumVal());
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("ComboBox pops up if clicked");
        renderer.setHorizontalTextPosition(SwingConstants.LEFT);
        comboCol.setCellRenderer(renderer);            
        
        JComboBox comboBox = new PComboBox();
        addItemChangeListener(comboBox);                
        comboBox.addItem(new ConvComboBoxItem(new Integer(-1), "(select)"));
    
        comboCol.setCellEditor(new DefaultCellEditor(comboBox));
    }

    /**
     * load item list for a comboBox given
     * @param comboBox box for which item list is created
     * @param column number that identifies comboBox type
     * @param keyValue key of the higher level comboBox item, in case the comboBox is 
     * lower in level. It will be used to select the lower level items from the DB table. 
     * keyValue of -1 means the comboBox is not a lower one.
     */
    public static void loadComboBoxItems(JComboBox comboBox, DriverCol column, int keyValue) 
    {
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;      
        String excepMsg = "(CBox Item Loading for : " + column + "column";

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            
            //<editor-fold defaultstate="collapsed" desc="-- make SQL statement">            
            switch (column) {
                case AffiliationL1: 
                    sb.append("select * from L1_affiliation order by PARTY_NAME");
                    break;
                    
                case BuildingNo: 
                    sb.append("select * from building_table order by bldg_no");
                    break;
                    
                case AffiliationL2: 
                    if (keyValue == -1) {
                        // Patent Requested SQL select statement
                        sb.append("SELECT L2.L2_NO, L2.PARTY_NAME AS L2_NAME, "
                                + "L1.L1_NO, L1.PARTY_NAME AS L1_NAME ");
                        sb.append("FROM L2_affiliation L2, L1_affiliation L1 "); 
                        sb.append("WHERE L2.L1_NO = L1.L1_NO "); 
                        sb.append("ORDER BY L2.PARTY_NAME, L1.PARTY_NAME"); 
                    } else {
                        sb.append("Select party_name as L2_NAME, L2_NO From L2_affiliation ");
                        sb.append("Where L1_NO = ").append(keyValue);
                        sb.append(" Order by PARTY_NAME");
                    }
                    break;
                    
                case UnitNo: 
                    if (keyValue == -1) {
                        // Patent Requested SQL select statement                        
                        sb.append("SELECT U.SEQ_NO AS UNIT_SN, U.UNIT_NO, ");
                        sb.append("  T.SEQ_NO BLDG_SN, T.BLDG_NO ");
                        sb.append("FROM building_unit U, building_table T ");
                        sb.append("WHERE U.BLDG_SEQ_NO = T.SEQ_NO ");
                        sb.append("ORDER BY U.UNIT_NO, T.BLDG_NO; ");
                    } else {
                        sb.append("SELECT UNIT_NO, SEQ_NO as UNIT_SN FROM building_UNIT ");
                        sb.append("WHERE BLDG_SEQ_NO = ").append(keyValue);
                        sb.append(" Order by UNIT_NO");                    
                    }
                    break;
                    
                default:
                    break;
            }
            //</editor-fold>
            rs = stmt.executeQuery(sb.toString());
            while (rs.next()) {
                //<editor-fold defaultstate="collapsed" desc="-- Add combobox items one by one">                            
                Object item = null;
                switch (column) {
                    case AffiliationL1: 
                        item = new ConvComboBoxItem(new Integer(rs.getInt("L1_NO")), rs.getString("PARTY_NAME"));
                        break;
                    case AffiliationL2: 
                        if (keyValue == -1) {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("L2_NO"), rs.getInt("L1_NO")},
                                    new String[] {rs.getString("L2_NAME"), rs.getString("L1_NAME")});
                        } else {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("L2_NO")}, new String[] {rs.getString("L2_NAME")});
                        }
                        break;
                    case BuildingNo: 
                        item = new ConvComboBoxItem(new Integer(rs.getInt("SEQ_NO")), rs.getString("BLDG_NO"));
                        break;
                    case UnitNo: 
                        if (keyValue == -1) {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("UNIT_SN"), rs.getInt("BLDG_SN")},
                                    new String[] {rs.getString("UNIT_NO"), rs.getString("BLDG_NO")});   
                        } else {
                            item = new InnoComboBoxItem(
                                    new int[] {rs.getInt("UNIT_SN")}, new String[] {rs.getString("UNIT_NO")});
                        }                             
                        break;
                    default:
                        break;
                }                
                comboBox.addItem(item);
            //</editor-fold>                
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }        
    }

    private void startEditingCell(int rowM, int columnIndex) {
        driverTable.requestFocusInWindow();
        driverTable.changeSelection(rowM, columnIndex, false, false);
        if (columnIndex == 1 || columnIndex == 2) {
            (new LabelBlinker()).displayHelpMessage(requiredLabel, 
                    REQUIRE_FIELD_NOTE.getContent(), true);  
        } else {
            requiredLabel.setText("");
        }
        driverTable.getEditorComponent().requestFocusInWindow();
    }

    private void handleItemChange(int rowV, int rowM, int colV, int colM) {
        // user wants to change driver info or continues changing it
        int colV2 = driverTable.convertColumnIndexToView(colM + 1); 

        if (colM == DriverCol.AffiliationL1.getNumVal())
        {
            int colM2 = DriverCol.AffiliationL2.getNumVal();
            driverTable.setValueAt(ManageDrivers.getPrompter(AffiliationL1, null), rowM, colM2);
        } 
        else if (colM == DriverCol.BuildingNo.getNumVal())
        {
            int colM2 = DriverCol.UnitNo.getNumVal();
            driverTable.setValueAt(ManageDrivers.getPrompter(BuildingNo, null), rowM, colM2); 
        }
        else
        {
            //<editor-fold defaultstate="collapsed" desc="-- propagate selection upward, etc.">            
            // get higher level category(affiliation level 1 or building) item
            ConvComboBoxItem l1Item = (ConvComboBoxItem)driverTable.getValueAt(rowV, colM - 1);
            
            if ((Integer) l1Item.getValue() == -1) 
            {
                // higher level item isn't selected yet, so select it here
                if (colM == AffiliationL2.getNumVal() || colM == UnitNo.getNumVal()) 
                {
                    InnoComboBoxItem innoItem = (InnoComboBoxItem)driverTable.getValueAt(rowV, colM);
                    int keyValue = innoItem.getKeys()[0];
                    if (keyValue != -1) {
                        ConvComboBoxItem pItem = null;
                        if (colM == AffiliationL2.getNumVal())
                        {
                            pItem = new ConvComboBoxItem(new Integer(innoItem.getKeys()[1]), innoItem.getLabels()[1]); 
                            try {
                            driverTable.setValueAt(new InnoComboBoxItem (new int[]{keyValue},
                                    new String[]{innoItem.getLabels()[0]}), rowV, colM);
                            } catch (Exception e) {
                                System.out.println("e: " + e.toString());
                            }
                        } else {
                            pItem = new ConvComboBoxItem(new Integer(innoItem.getKeys()[1]), innoItem.getLabels()[1]); 
                            driverTable.setValueAt(new InnoComboBoxItem (new int[]{keyValue},
                                    new String[]{innoItem.getLabels()[0]}), rowV, colM);
                        }
                        colV2 = driverTable.convertColumnIndexToView(colM - 1); 
                        driverTable.setValueAt(pItem, rowV, colV2);                        
                    }
                }
            }
            //</editor-fold>            
        }        
    }

    public static String getL2PartyName(int L2No) {
        Connection conn = null;
        Statement stmt = null; 
        ResultSet rs = null;         
        String result = null;
        String excepMsg = "(while fetching party name for L2_no : " + L2No;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("select party_name from l2_affiliation where L2_NO = " + L2No);
            rs = stmt.executeQuery(sb.toString());
            if (rs.next()) {
                result = rs.getString("party_name");
            }
        } catch (SQLException ex) {
            logParkingException(Level.SEVERE, ex, excepMsg);
        } finally {
            closeDBstuff(conn, stmt, rs, excepMsg);
        }         
        return result;
    }

    private void attachNumberCondition(StringBuffer cond, 
            String highCol, String lowCol, int highKey, int lowKey)
    {
        if (highKey != -1) {
            if (lowKey == -1)
            {
                if (cond.length() > 0) {
                    cond.append(" and ");
                }
                cond.append(highCol + " = " + highKey); 
            }
            else
            {
                if (cond.length() > 0) {
                    cond.append(" and ");
                }
                // putting high key value condition is redundant and causes inefficiency
                cond.append(lowCol + " = " + lowKey); 
            }
        } else {
            if (lowKey != -1) {
                logParkingException(Level.SEVERE, null, 
                        "(driver search error high Key: " + highKey + ", low Key: " + lowKey +")");
            }
        }
    }

    private void attachEnterHandler(JComponent compo) {
        Action handleEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().getClass() == PComboBox.class) {
                    PComboBox cBox = (PComboBox)e.getSource();
                    if (cBox.isPopupVisible()) {
                        ConvComboBoxItem item = (ConvComboBoxItem)cBox.getHighlightedCbxItem();
                        cBox.setSelectedItem(item);
                        cBox.setPopupVisible(false);
                        
                    } else
                        loadDriverData(FIRST_ROW, "", "");
                } else
                    loadDriverData(FIRST_ROW, "", "");
            }
        };
        compo.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "handleEnter");
        compo.getActionMap().put("handleEnter", handleEnter);
    }

    private void closeFrameGracefully() {
        if (formMode == FormMode.NormalMode) {
            dispose();
        } else {
            int response = JOptionPane.showConfirmDialog(null, DRIVER_CLOSE_FORM_DIALOG.getContent(),
                    WARING_DIALOGTITLE.getContent(), 
                    JOptionPane.YES_NO_OPTION);
        
            if (response == JOptionPane.YES_OPTION) 
            {
                dispose();
            }             
        }  
    }

    /**
     * Finds appropriate prompter for a combo box -- Patent Requested Technology Implementing method.
     * 
     * @param column table ComboBox column for which the prompter is to be calculated
     * @param parentObj 1st level affiliation ComboBox, should be not null when 
     *                      column is of the 2nd level affiliation
     * @param buildingComboBox building ComboBox, should not be null when
     *                      column is of the unit ComboBox
     * @return 
     */
    public static Object getPrompter(DriverCol column, Object parentObj) {
        boolean complexItem = false;
        
        if (column == AffiliationL2 || column == UnitNo) {
            if (parentObj == null)
                complexItem = true;
            else {
                if (parentObj.getClass().equals(PComboBox.class)) {
                    if (((PComboBox)parentObj).getSelectedIndex() <= 0)
                        complexItem = true;
                }
                else {
                    int parentIndex = (Integer) (((ConvComboBoxItem)parentObj).getValue());
                    if (parentIndex == -1)
                        complexItem = true;
                }
            }
        }
        
        String label = null;
        switch (column) {
            case AffiliationL1: 
                label = HIGHER_CB_ITEM.getContent();
                break;
            case BuildingNo: 
                label  = BUILDING_CB_ITEM.getContent();
                break;
            case AffiliationL2:  
                label = complexItem ? LOWER_HIGHER_CB_ITEM.getContent() 
                        : LOWER_CB_ITEM.getContent();
                break;
            case UnitNo: 
                label = complexItem ? ROOM_BUILDING_CB_ITEM.getContent() 
                        : ROOM_CB_ITEM.getContent();
                break;
            default:
                break;
        }
        if (column == AffiliationL1 || column == BuildingNo) {
            return new ConvComboBoxItem(new Integer(-1), label);
        } else {
            return new InnoComboBoxItem(new int [] {-1}, new String[] {label});
        }
    }
    
    public static void initAffiliationComboBoxes(JComboBox searchL1ComboBox, JComboBox searchL2ComboBox, 
            JComboBox searchBuildingComboBox, JComboBox searchUnitComboBox)
    {
        searchL1ComboBox.addItem(getPrompter(AffiliationL1, null));
        searchL2ComboBox.addItem(getPrompter(AffiliationL2, searchL1ComboBox));
        
        addParentBoxItemAddInvokeListener(AffiliationL2, searchL2ComboBox, searchL1ComboBox);

        searchBuildingComboBox.addItem(getPrompter(BuildingNo, null));
        searchUnitComboBox.addItem(getPrompter(UnitNo, searchBuildingComboBox));
        
        addParentBoxItemAddInvokeListener(UnitNo, searchUnitComboBox, searchBuildingComboBox);
    }

    private static void addParentBoxItemAddInvokeListener(final DriverCol currColumn, 
            final JComboBox currCBox, final JComboBox parentCBox) {
        currCBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (parentCBox.getSelectedIndex() > 0) { 
                        return; // For a regular order (=high to low) compobox hierarchy selection, nothing to do.
                    }
                    // Parent combo-box item isn't selected.
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() { // Create ans select item for the parent combo box.
                            propagateComplexItemUpward(currColumn, currCBox, parentCBox);
                        }
                    });        
                }
            }
        });        
    }
    
    /**
     * Selects an item from the parent combo box -- Patent Requested Technology Implementing method.
     * 
     * @param currColumn
     * @param currCBox
     * @param parentCBox 
     */
    public static void propagateComplexItemUpward(DriverCol currColumn, JComboBox currCBox, 
            JComboBox parentCBox) 
    {
        InnoComboBoxItem currItem = (InnoComboBoxItem)currCBox.getSelectedItem();
        int keyValue = (Integer)(currItem.getKeys()[0]);

        if (keyValue != -1) { // Child item is selected
            // Select parent combo box item, in a kind of reverse direction.
            parentCBox.removeAllItems();

            parentCBox.addItem(getPrompter((currColumn == AffiliationL2 ? AffiliationL1 : BuildingNo),
                    parentCBox));
            ConvComboBoxItem pItem 
                    = new ConvComboBoxItem(currItem.getKeys()[1], currItem.getLabels()[1]);
            parentCBox.addItem(pItem);
            parentCBox.setSelectedItem(pItem);

            // Make current combo box to have only two items.
            currCBox.removeAllItems();
            currCBox.addItem(getPrompter(currColumn, parentCBox));

            InnoComboBoxItem onlyItem = new InnoComboBoxItem(
                    new int[]{keyValue}, new String[]{currItem.getLabels()[0]});
            currCBox.addItem(onlyItem);
            currCBox.setSelectedItem(onlyItem); 
        }                            
    }    

    private void searchKeyGroupEnabled(boolean flag) {
        searchName.setEnabled(flag);
        searchCell.setEnabled(flag);
        searchPhone.setEnabled(flag);
        searchL1ComboBox.setEnabled(flag);
        searchL2ComboBox.setEnabled(flag);
        searchBuildingComboBox.setEnabled(flag);
        searchUnitComboBox.setEnabled(flag);
    }

    private void getDriverProperties(String name, String cell, StringBuffer driverProperties, int row,
            String landLine, String itemL2name, String itemUnitName) 
    {
        TableModel drvModel = driverTable.getModel();
        
        driverProperties.append("  name: " + (String)name + System.lineSeparator());
        driverProperties.append("  cell phone: " + (String)cell + System.lineSeparator());
        driverProperties.append("  phone: " + landLine + System.lineSeparator());    
        driverProperties.append("  2nd affiliation: " + itemL2name + System.lineSeparator());
        driverProperties.append("  Room#: " + itemUnitName + System.lineSeparator());
    }

    private String formSearchCondition() {
        StringBuffer cond = new StringBuffer();
        String searchStr = searchName.getText().trim();
        
        if (!nameHintShown && searchStr.length() > 0) {
            attachCondition(cond, "name", searchStr);
        }
        
        searchStr = searchCell.getText().trim();
        if (!cellHintShown && searchStr.length() > 0) {
            attachCondition(cond, "cellphone", searchStr);
        }
        
        searchStr = searchPhone.getText().trim();
        if (!phoneHintShown && searchStr.length() > 0) {
            attachCondition(cond, "phone", searchStr);
        }

        /**
         * Append affiliation condition if applicable.
         */
        InnoComboBoxItem lower_Item = (InnoComboBoxItem)searchL2ComboBox.getSelectedItem();
        int lower_Index = lower_Item.getKeys().length - 1;
        
        attachNumberCondition(cond, "L1_NO", "L2_NO", 
                (Integer)((ConvComboBoxItem)searchL1ComboBox.getSelectedItem()).getValue(),
                (Integer)(lower_Item.getKeys()[lower_Index]));

        /**
         * Append building-unit condition if applicable.
         */        
        lower_Item = (InnoComboBoxItem)searchUnitComboBox.getSelectedItem();
        lower_Index = lower_Item.getKeys().length - 1;
        attachNumberCondition(cond, "B_SEQ_NO", "U_SEQ_NO", (Integer)
                ((ConvComboBoxItem)searchBuildingComboBox.getSelectedItem()).getValue(),
                (Integer)(lower_Item.getKeys()[lower_Index]));   
        
        if (cond.length() > 0) {
            clearButton.setEnabled(true);
            return "Where " + cond.toString();
        } else {
            clearButton.setEnabled(false);
            return "";
        }
    }
}
