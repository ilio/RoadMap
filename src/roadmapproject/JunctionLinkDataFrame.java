package roadmapproject;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

public class JunctionLinkDataFrame extends JInternalFrame implements ActionListener,IFrameTableReload{
    JTable tableMain;
    JTable tablePoints;
    HeadTable dataModelMain;
    JScrollPane scrollpaneMain;
    JScrollPane scrollPanePoints;
    JButton button_ok;
    MultyLanguage ml;
    private static Color back_color=new Color(0,180,0,10);
    //static final int xOffset = 30, yOffset = 30;

    public JunctionLinkDataFrame(final JunctionLink jl) {
        super(jl.getCurrName()+" properties",
                false, //resizable
                false, //closable
                false, //maximizable
                true);//iconifiable
        //PSRender.onHold();
        super.setBackground(back_color);
        super.addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameActivated(InternalFrameEvent e){
                reload();
                SelectedObject.setSelectedObject(jl);
            }
        });
        PSRender.moveToTop(jl.getBound(),0.5);
        ml=jl;
        setLocation(50,50);
        dataModelMain = new HeadTable(jl);
        tableMain = new JTable(dataModelMain);
        tableMain.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        tableMain.setPreferredScrollableViewportSize(new Dimension(500, 20));
//        tableMain.setBackground(back_color);

        //code
        TableColumn column_code = tableMain.getColumn("Code");
        DefaultTableCellRenderer rendererCode=new DefaultTableCellRenderer();
        rendererCode.setToolTipText("Enter new code for this path");
        column_code.setCellRenderer(rendererCode);
        column_code.setCellEditor(new TextFieldEditor());
        //name
        TableColumn column_name=tableMain.getColumn("Name");
        LanguageButtonRender render_name=new LanguageButtonRender();
        render_name.setToolTipText("Path name. Press for change name");
        column_name.setCellRenderer(render_name);
        LanguageButtonEditor editor_name=new LanguageButtonEditor(new JCheckBox());
        column_name.setCellEditor(editor_name);
        //junction 1
        TableColumn column_junc_1 = tableMain.getColumn("Junction 1");
        MLButtonRenderer rendererJunc_1=new MLButtonRenderer();
        rendererJunc_1.setToolTipText("Code of first Junction, click for open properties");
        column_junc_1.setCellRenderer(rendererJunc_1);
        MLButtonEditor editorJunc=new MLButtonEditor(new JCheckBox());
        column_junc_1.setCellEditor(editorJunc);
        //junction 2
        TableColumn column_junc_2 = tableMain.getColumn("Junction 2");
        MLButtonRenderer rendererJunc_2=new MLButtonRenderer();
        rendererJunc_2.setToolTipText("Code of second Junction, click for open properties");
        column_junc_2.setCellRenderer(rendererJunc_2);
        column_junc_2.setCellEditor(editorJunc);
        //type
        JComboBox comboBoxType = new JComboBox();
        String roadNames[]=jl.getTypeNames();
        for(int i=0,len=roadNames.length;i<len;i++){
            comboBoxType.addItem(roadNames[i]);
        }
        TableColumn column_type = tableMain.getColumn("Type");
        DefaultTableCellRenderer rendererType=new DefaultTableCellRenderer();
        rendererType.setToolTipText("To change road type click for combo box");
        column_type.setCellRenderer(rendererType);
        column_type.setCellEditor(new DefaultCellEditor(comboBoxType));
        //legth
        TableColumn column_length = tableMain.getColumn("Length");
        DefaultTableCellRenderer rendererLen=new DefaultTableCellRenderer();
        rendererLen.setToolTipText("The path length in meters");
        column_length.setCellRenderer(rendererLen);

        scrollpaneMain = new JScrollPane(tableMain);
        scrollpaneMain.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpaneMain.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollpaneMain.setPreferredSize(new Dimension(500,50));

        Container contentPane = super.getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);

        c.ipady = 0;      //make this component tall
        c.weightx = 0.5;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 0;
        gridbag.setConstraints(scrollpaneMain, c);
//        scrollpaneMain.setBackground(back_color);
//        scrollpaneMain.setForeground(back_color);
        contentPane.add(scrollpaneMain);

        TableModel dataModelPoints=new PointTableModel(jl);
        tablePoints = new JTable(dataModelPoints);
        tablePoints.setPreferredScrollableViewportSize(new Dimension(200,dataModelPoints.getRowCount()*tablePoints.getRowHeight()));
        ListModel lm = new AbstractListModel() {
            public int getSize() { return 1000;}
            public Object getElementAt(int index) {
                return ""+index;
            }
        };
        JList rowHeader = new JList(lm);
        rowHeader.setFixedCellWidth(20);

        rowHeader.setFixedCellHeight(tablePoints.getRowHeight());
        //+ tablePoints.getRowMargin());

        rowHeader.setCellRenderer(new RowHeaderRenderer(tablePoints));

        scrollPanePoints = new JScrollPane(tablePoints);
        scrollPanePoints.setWheelScrollingEnabled(true);
        scrollPanePoints.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanePoints.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        int mh;
        if(dataModelPoints.getRowCount()*tablePoints.getRowHeight()>100){
            mh = 100;
        }else{
            mh=dataModelPoints.getRowCount()*tablePoints.getRowHeight();
        }
        scrollPanePoints.setPreferredSize(new Dimension(200,mh));
        scrollPanePoints.setRowHeaderView(rowHeader);

        c.ipadx =200;
        c.weightx = 0.5;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 1;
        gridbag.setConstraints(scrollPanePoints, c);
//        scrollPanePoints.setBackground(back_color);
        contentPane.add(scrollPanePoints);

        button_ok=new JButton("Ok");
        button_ok.addActionListener(this);
        JPanel buttonPane=new JPanel(null);
        buttonPane.setPreferredSize(new Dimension(500,100));
        button_ok.setBounds(205,40,90,40);
        buttonPane.add(button_ok);
        c.ipady = 10;      //make this component tall
        c.ipadx =10;
        c.weightx = 0.5;
        c.gridwidth = 4;
        c.gridx = 2;
        c.gridy = 2;
        gridbag.setConstraints(buttonPane, c);
        buttonPane.setBackground(back_color);
        contentPane.add(buttonPane);

        super.pack();
        super.setVisible(true);

        //MapApplication.getDesktop().setSelectedFrame(this);

    }
    public void actionPerformed(ActionEvent e){
        if(button_ok==e.getSource()){
            IFrameSupervisor.removeJInternalFrame(ml);
            try {
                super.setClosed(true);
            }
            catch (PropertyVetoException ex) {
            }
            //PSRender.outHold();
        }
    }
    public void reload(){
        dataModelMain.load();
    }
}
class RowHeaderRenderer extends JLabel implements ListCellRenderer {

    RowHeaderRenderer(JTable table) {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
    }

    public Component getListCellRendererComponent( JList list,
                                                   Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
class PointTableModel extends AbstractTableModel{
    Point2DSerializable[] ctrl;
    Double[] p2x;
    Double[] p2y;
    Double[] ctrlx;
    Double[] ctrly;
    JunctionLink jl;
    //JButton[] row_num=new JButton[ctrl.length];

    PointTableModel(JunctionLink jl){
        this.jl=jl;
        load();
    }
    Object[][] data;
    String[] names={"Control X points","Control Y points","P2 X points","P2 Y points"};
    public void load(){
        Point2DSerializable[] p2=jl.getP2Points();
        ctrl=jl.getCtrlPoints();
        p2x=new Double[p2.length];
        p2y=new Double[p2.length];
        ctrlx=new Double[ctrl.length];
        ctrly=new Double[ctrl.length];
        if(p2.length!=ctrl.length){
            System.out.println("ERROR: хуйня какая то в JunctionLinkDataFrame "+jl);
            throw new Error("ERROR: хуйня какая то в JunctionLinkDataFrame "+jl);
        }
        for(int i=0,len=p2.length;i<len;i++){
            p2x[i]=new Double(p2[i].getX());
            p2y[i]=new Double(p2[i].getY());
            ctrlx[i]=new Double(ctrl[i].getX());
            ctrly[i]=new Double(ctrl[i].getY());
        }
        data=new Object[][]{ctrlx,ctrly,p2x,p2y};
    }
    public int getColumnCount()			{return names.length;}
    public int getRowCount()				{return data[0].length;}
    public Object getValueAt(int row, int col)	{return data[col][row];}
    public String getColumnName(int column)		{return names[column];}
    public Class getColumnClass(int c)		{return getValueAt(0, c).getClass();}
    public boolean isCellEditable(int row, int col)	{return col!=4;}
    public void setValueAt(Object aValue, int row, int column){
        data[column][row] = aValue;
        switch(column){
            case 0://code
                double x=((Double)aValue).doubleValue();
                double y=((Double)getValueAt(row,1)).doubleValue();
                Point2DSerializable p=new Point2DSerializable(x,y,true);
                jl.moveCtrlPoint(row,p);
                break;
            case 1:
                x=((Double)getValueAt(row,0)).doubleValue();
                y=((Double)aValue).doubleValue();
                p=new Point2DSerializable(x,y,true);
                jl.moveCtrlPoint(row,p);
                break;
            case 2://type
                x=((Double)aValue).doubleValue();
                y=((Double)getValueAt(row,3)).doubleValue();
                p=new Point2DSerializable(x,y,true);
                System.out.println("2 p="+p);
                jl.moveP2Point(row,p);
                break;
            case 3://length
                x=((Double)getValueAt(row,2)).doubleValue();
                y=((Double)aValue).doubleValue();
                p=new Point2DSerializable(x,y,true);
                System.out.println("3 p="+p);
                jl.moveP2Point(row,p);
                break;
        }
        PSRender.repaint_anywhere();
    }
}
class HeadTable extends AbstractTableModel{
    Object[][] data;
    String[] names={"Code","Name","Junction 1","Junction 2","Type","Length"};
    JunctionLink jl;
    HeadTable(JunctionLink jl){
        this.jl=jl;
        load();
    }
    public void load(){
            data=new Object[][]{{jl.getCode(),
                                 jl
                                 ,jl.firstJunction==null?null:jl.firstJunction
                                 ,jl.secondJunction==null?null:jl.secondJunction
                                 ,JunctionLinkRender.getRoadName(jl.getType())
                                 ,new Double (jl.getPathLength())}};
        /*data=new Object[jl.length][5];
        for(int i=0;i<jl.length;i++){
        data[i][0]=jl[i].getCode();//String
        data[i][1]=jl[i].firstJunc==null?null:Global.getJunction(jl[i].firstJunc);//Junction
        data[i][2]=jl[i].secondJunc==null?null:Global.getJunction(jl[i].secondJunc);//Junction
        data[i][3]=JunctionLinkRender.getRoadName(jl[i].getType());//String
        data[i][4]=new Double (jl[i].getPathLength());//Double
        }
        super.setDataVector(data,names);*/
    }
    public int getColumnCount()			{return names.length;}
    public int getRowCount()				{return data.length;}
    public Object getValueAt(int row, int col)	{return data[row][col];}
    public String getColumnName(int column)		{return names[column];}
    public Class getColumnClass(int col) 		{
        switch (col) {
            case 0: return String.class;
            case 1: return JunctionLink.class;
            case 2: return Junction.class;
            case 3: return Junction.class;
            case 4: return String.class;
            case 5: return Double.class;
            default:
                throw new Error("вылез за границы таблицы");
        }
    }
    public boolean isCellEditable(int row, int col)	{return true;}//col!=1&&col!=2;}
    public void setValueAt(Object aValue, int row, int column) {
        data[row][column] = aValue;
        switch(column){
            case 0://code
                jl.setCode((String)aValue);
                break;
            case 4://type
                jl.setType(JunctionLinkRender.getRoadType((String)aValue));
                break;
            case 5://length
                try {
                    jl.setPathLenght(((Double)aValue).doubleValue());
                }
                catch (Exception ex) {
                }
                break;
        }
        PSRender.repaint_anywhere();

    }
};
