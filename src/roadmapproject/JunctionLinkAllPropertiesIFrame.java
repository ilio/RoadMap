package roadmapproject;

//import roadmapproject.HeadTable;

import roadmapproject.RowHeaderRenderer;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
/*
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
*/
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Vector;
//import java.util.Date;

/**
 * Created by Igor
 * Date: Dec 22, 2002
 * Time: 12:30:33 AM
 */
public class JunctionLinkAllPropertiesIFrame extends JInternalFrame{
    private JTable tableMain;
//    HeadTable dataModelMain;
    private SortableTableModel dm;
    private JScrollPane scrollpaneMain;
    private JunctionLink[] jls;
//    private int selected_row;
    private int[] columnWidth = {150,130,130,70,70};
    private static JunctionLinkAllPropertiesIFrame jlapf;
    public static JunctionLinkAllPropertiesIFrame activate_frame(JunctionLink[] jls){
        if(jlapf==null){
            jlapf=new JunctionLinkAllPropertiesIFrame(jls);
        }else{
            try {
//                jlapf.dataModelMain = new roadmapproject.HeadTable(jls);
                jlapf.setIcon(false);
                jlapf.setSelected(true);
                jlapf.moveToFront();
            }catch (java.beans.PropertyVetoException e){
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());
            }
        }
        return jlapf;
    }

    protected JunctionLinkAllPropertiesIFrame(JunctionLink[] jlks) {
        super("properties",
                false, //resizable
                true, //closable
                false, //maximizable
                true);//iconifiable
        //PSRender.onHold();
        this.jls = jlks;
        super.addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameActivated(InternalFrameEvent e){
                reload();
//                selected_row=tableMain.getSelectedRow();
                //SelectedObject.setSelectedObject(jl[selected_row]);
            }
            public void internalFrameClosed(InternalFrameEvent e) {
                jlapf=null;
                IFrameSupervisor.returnFocus();
            }
        });
        PSRender.onHold();
        setLocation(50,50);
        /*dataModelMain = new roadmapproject.HeadTable(jl);
        tableMain = new JTable(dataModelMain);
        //tableMain.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        tableMain.setPreferredScrollableViewportSize(new Dimension(500, 500));*/





//        int[] columnWidth = {100,150,100,50};


        dm = new SortableTableModel() {
            public Class getColumnClass(int col) {
                switch (col) {
                    case  0: return JunctionLink.class;
                    case  1: return Junction.class;
                    case  2: return Junction.class;
                    case  3: return String.class;
                    default: return Double.class;
                }
            }
            public boolean isCellEditable(int row, int col) {
                return true;
            }
            public void setValueAt(Object obj, int row, int col) {
                super.setValueAt(obj,row,col);
                switch(col){
                    case 0://code
                        System.out.println("set code "+getValueAt(row,0)+" code="+obj);
                        break;
                    case 3://type
                        ((JunctionLink)getValueAt(row,0)).setType(JunctionLinkRender.getRoadType((String)obj));
                        System.out.println("set type "+jls[row].toString());
                        break;
                    case 4://length
                        try {
//                            jl[row].setPathLenght(((Double)obj).doubleValue());
                            ((JunctionLink)getValueAt(row,0)).setPathLenght(((Double)obj).doubleValue());
                        }
                        catch (Exception ex) {
                        }
                        break;
                }
                PSRender.repaint_anywhere();
            }
            public void load(){

            }
        };
        Object[][] data;
        String[] headerStr = {"Code","Junction 1","Junction 2","Type","Length"};
        data = new Object[jls.length][5];
        for(int i=0;i<jls.length;i++){
            data[i][0]=jls[i];//JunctionLink//.getCode();//String
            data[i][1]=jls[i].firstJunction==null?null:jls[i].firstJunction;
            data[i][2]=jls[i].secondJunction==null?null:jls[i].secondJunction;
            data[i][3]=JunctionLinkRender.getRoadName(jls[i].getType());//String
            data[i][4]=new Double (jls[i].getPathLength());//Double
        }
        //super.setDataVector(data,names);
        dm.setDataVector( data ,headerStr);
//        reload();
        tableMain = new JTable(dm);
        tableMain.setShowVerticalLines(false);
        tableMain.setShowHorizontalLines(true);
        SortButtonRenderer renderer = new SortButtonRenderer();
        TableColumnModel model = tableMain.getColumnModel();
        int n = tableMain.getColumnCount();
        for (int i=0;i<n;i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
            model.getColumn(i).setPreferredWidth(columnWidth[i]);
        }
        JTableHeader header = tableMain.getTableHeader();
        header.addMouseListener(new SortedTableHeaderListener(header,renderer));

        //code
//        TableColumn typeCode = tableMain.getColumn("Code");
//        DefaultTableCellRenderer rendererCode=new DefaultTableCellRenderer(){
//            protected void setValue(Object value) {
//                if(value instanceof MultyLanguage){
//                    setText(((MultyLanguage)value).getCode());
//                    return;
//                }
//                setText((value == null) ? "" : value.toString());
//            }
//        };
//        rendererCode.setToolTipText("Enter new code for this path");
//        typeCode.setCellRenderer(rendererCode);
//        typeCode.setCellEditor(new TextFieldEditor());
        TableColumn typeCode = tableMain.getColumn("Code");
        MLButtonRenderer renderer_code=new MLButtonRenderer();
        renderer_code.setToolTipText("Code of Path, click for open properties");
        typeCode.setCellRenderer(renderer_code);
        MLButtonEditor editor_code=new MLButtonEditor(new JCheckBox());
        typeCode.setCellEditor(editor_code);
        //junction 1
        TableColumn typeJunc_1 = tableMain.getColumn("Junction 1");
        MLButtonRenderer rendererJunc_1=new MLButtonRenderer();
        rendererJunc_1.setToolTipText("Code of first Junction, click for open properties");
        typeJunc_1.setCellRenderer(rendererJunc_1);
        MLButtonEditor editorJunc=new MLButtonEditor(new JCheckBox());
        typeJunc_1.setCellEditor(editorJunc);
        //junction 2
        TableColumn typeJunc_2 = tableMain.getColumn("Junction 2");
        MLButtonRenderer rendererJunc_2=new MLButtonRenderer();
        rendererJunc_2.setToolTipText("Code of second Junction, click for open properties");
        typeJunc_2.setCellRenderer(rendererJunc_2);
        typeJunc_2.setCellEditor(editorJunc);
        //type
        JComboBox comboBoxType = new JComboBox();
        String roadNames[]=jls[0].getTypeNames();
        for(int i=0,len=roadNames.length;i<len;i++){
            comboBoxType.addItem(roadNames[i]);
        }
        TableColumn typeColumn = tableMain.getColumn("Type");
        DefaultTableCellRenderer rendererType=new DefaultTableCellRenderer();
        rendererType.setToolTipText("To change road type click for combo box");
        typeColumn.setCellRenderer(rendererType);
        typeColumn.setCellEditor(new DefaultCellEditor(comboBoxType));
        //legth
        TableColumn typeLen = tableMain.getColumn("Length");
        DefaultTableCellRenderer rendererLen=new DefaultTableCellRenderer();
        rendererLen.setToolTipText("The path length in meters");
        typeLen.setCellRenderer(rendererLen);

        ListModel lm = new AbstractListModel() {
            public int getSize() { return 5000;}
            public Object getElementAt(int index) {
                return ""+index;
            }
        };
        JList rowHeader = new JList(lm);
        rowHeader.setFixedCellWidth(30);

        rowHeader.setFixedCellHeight(tableMain.getRowHeight());
        //+ tablePoints.getRowMargin());

        rowHeader.setCellRenderer(new RowHeaderRenderer(tableMain));

        scrollpaneMain = new JScrollPane(tableMain);
        scrollpaneMain.setRowHeaderView(rowHeader);
        scrollpaneMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpaneMain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollpaneMain.setPreferredSize(new Dimension(600,150));

        Container contentPane = super.getContentPane();
        /*GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);

        c.ipady = 30;      //make this component tall
        c.weightx = 0.5;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 0;
        gridbag.setConstraints(scrollpaneMain, c);*/
        contentPane.add(scrollpaneMain);

        super.pack();
        super.setVisible(true);
    }
    public void reload(){
        Vector dv=dm.getDataVector();
        Vector cv;
        for(int i=0;i<jls.length;i++){
            cv = (Vector)dv.get(i);
            cv.removeAllElements();
        }
        for(int i=0;i<jls.length;i++){
            cv = (Vector)dv.get(i);
            cv.add(jls[i]);
            cv.add(jls[i].firstJunction==null?null:jls[i].firstJunction);
            cv.add(jls[i].secondJunction==null?null:jls[i].secondJunction);
            cv.add(JunctionLinkRender.getRoadName(jls[i].getType()));
            cv.add(new Double (jls[i].getPathLength()));
        }
        dm.fireTableDataChanged();
    }
    /*public void reload(){
    dataModelMain.execute();
    }*/
}
