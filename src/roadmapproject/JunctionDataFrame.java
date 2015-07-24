package roadmapproject;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.beans.*;

public class JunctionDataFrame extends JInternalFrame implements ActionListener,IFrameTableReload{
    JTable tableMain;
    JTable tablePoints;
    PathsTableModel dataModelPaths;
    JHeadTable dataModelHead;
    JScrollPane scrollpaneHead;
    JScrollPane scrollPanePoints;
    JButton button_ok;
    MultyLanguage ml;
    private static Color back_color=new Color(0,0,180,10);
    public JunctionDataFrame(final Junction j) {
        super(j.getCurrName()+" properties",
                false, //resizable
                false, //closable
                false, //maximizable
                true);//iconifiable
        //PSRender.onHold();
        super.setBackground(back_color);
        super.addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameActivated(InternalFrameEvent e){
                reload();
                SelectedObject.setSelectedObject(j);
            }
        });
        ml=j;
        dataModelHead = new JHeadTable(j);
        tableMain = new JTable(dataModelHead);
        tableMain.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        tableMain.setPreferredScrollableViewportSize(new Dimension(500, 20));

        TableColumn col_code = tableMain.getColumn("Code");
        DefaultTableCellRenderer renderer_code=new DefaultTableCellRenderer();
        renderer_code.setToolTipText("Enter new code for this junction");
        col_code.setCellRenderer(renderer_code);
        col_code.setCellEditor(new TextFieldEditor());
        //name
        TableColumn column_name=tableMain.getColumn("Name");
        LanguageButtonRender render_name=new LanguageButtonRender();
        render_name.setToolTipText("Path name. Press for change name");
        column_name.setCellRenderer(render_name);
        LanguageButtonEditor editor_name=new LanguageButtonEditor(new JCheckBox());
        column_name.setCellEditor(editor_name);

        TableColumn col_px = tableMain.getColumn("Point x");
        DefaultTableCellRenderer renderer_px=new DefaultTableCellRenderer();
        renderer_px.setToolTipText("Enter new x for this junction");
        col_px.setCellRenderer(renderer_px);

        TableColumn col_py = tableMain.getColumn("Point y");
        DefaultTableCellRenderer renderer_py=new DefaultTableCellRenderer();
        renderer_py.setToolTipText("Enter new y for this junction");
        col_py.setCellRenderer(renderer_py);

        JComboBox comboBoxType = new JComboBox();
        String junction_types[]=j.getTypeNames();
        for(int i=0,len=junction_types.length;i<len;i++){
            comboBoxType.addItem(junction_types[i]);
        }
        TableColumn typeColumn = tableMain.getColumn("Type");
        DefaultTableCellRenderer rendererType=new DefaultTableCellRenderer();
        rendererType.setToolTipText("To change road type click for combo box");
        typeColumn.setCellRenderer(rendererType);
        typeColumn.setCellEditor(new DefaultCellEditor(comboBoxType));

        scrollpaneHead = new JScrollPane(tableMain);
        scrollpaneHead.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpaneHead.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollpaneHead.setPreferredSize(new Dimension(500,50));

        Container contentPane = super.getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);

        //c.ipady = 30;      //make this component tall
        c.ipadx=100;
        c.weightx = 0.5;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 0;
        gridbag.setConstraints(scrollpaneHead, c);
        contentPane.add(scrollpaneHead);



        dataModelPaths=new PathsTableModel(j);
        tablePoints = new JTable(dataModelPaths);
        tablePoints.setPreferredScrollableViewportSize(
                new Dimension(200
                        ,dataModelPaths.getRowCount()
                *tablePoints.getRowHeight()));
        //path
        TableColumn col_path = tablePoints.getColumn("Path");
        MLButtonRenderer renderer_path=new MLButtonRenderer();
        renderer_path.setToolTipText("Code of path, click for open properties");
        col_path.setCellRenderer(renderer_path);
        MLButtonEditor editor_path=new MLButtonEditor(new JCheckBox());
        col_path.setCellEditor(editor_path);
        //length*/
        TableColumn col_length = tablePoints.getColumn("Length");
        DefaultTableCellRenderer renderer_length=new DefaultTableCellRenderer();
        renderer_length.setToolTipText("Enter new code for this path");
        col_length.setCellRenderer(renderer_length);
        col_length.setMaxWidth(40);
        //col_length.setCellEditor(new TextFieldEditor());
        //second*/
        TableColumn col_second = tablePoints.getColumn("Second half");
        MLButtonRenderer renderer_second=new MLButtonRenderer();
        renderer_second.setToolTipText("Second junction, click for open properties");
        col_second.setCellRenderer(renderer_second);
        MLButtonEditor editor_second=new MLButtonEditor(new JCheckBox());
        col_second.setCellEditor(editor_second);
//        //delete*/
//        TableColumn col_delete = tablePoints.getColumn("Delete");
//        ButtonRenderer renderer_delete=new ButtonRenderer();
//        renderer_delete.setToolTipText("Click for delete this path");
//        col_delete.setCellRenderer(renderer_delete);
//        ButtonEditor editor_detele=new ButtonEditor(new JCheckBox());
//        col_delete.setCellEditor(editor_detele);
//        col_delete.setMaxWidth(70);
//        //*/
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
        if(dataModelPaths.getRowCount()*tablePoints.getRowHeight()>100){
            mh = 100;
        }else{
            mh=dataModelPaths.getRowCount()*tablePoints.getRowHeight();
        }
        scrollPanePoints.setPreferredSize(new Dimension(200,mh));
        scrollPanePoints.setRowHeaderView(rowHeader);
        c.ipady=20;
        c.ipadx =350;
        c.weightx = 0.5;
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 1;
        gridbag.setConstraints(scrollPanePoints, c);
        contentPane.add(scrollPanePoints);
////////////////////////////////////////////////////////////////////////////////////////////
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
        dataModelHead.load();
        dataModelPaths.load();
    }
}
class PathsTableModel extends AbstractTableModel{
    Junction j;
    ArrayList ajls;
    JunctionLink[] jls;
    Double[] lens;
    Junction[] seconds;

    PathsTableModel(Junction j){
        this.j=j;
        load();
    }
    Object[][] data;
    String[] names={"Path","Length","Second half"};
    public void load(){
        ajls=j.getPaths();
        jls=new JunctionLink[ajls.size()];
        lens=new Double[jls.length];
        seconds=new Junction[jls.length];
        ajls.toArray(jls);
        for(int i=0,len=lens.length;i<len;i++){
            lens[i]=new Double(jls[i].getPathLength());
            seconds[i]=jls[i].getSecondHalfJunction(j);
        }
        data=new Object[][]{jls,lens,seconds};
    }
    public int getColumnCount()			{return names.length;}
    public int getRowCount()				{return data[0].length;}
    public Object getValueAt(int row, int col)	{return data[col][row];}
    public String getColumnName(int column)		{return names[column];}
    public Class getColumnClass(int c)		{return getValueAt(0, c).getClass();}
    public boolean isCellEditable(int row, int col)	{return true;}//col!=4;}
    public void setValueAt(Object aValue, int row, int column){
        data[column][row] = aValue;
        switch(column){
            case 0://path

                break;
            case 1://length
                JunctionLink jl=(JunctionLink)data[0][row];
                jl.setPathLenght(((Double)aValue).doubleValue());
                break;
            case 2://second

                break;
            case 3://delete

                break;
        }
        PSRender.repaint_anywhere();
    }
}
class JHeadTable extends AbstractTableModel{
    Object[][] data;
    String[] names={"Code","Name","Point x","Point y","Type"};
    Junction j;
    public JHeadTable(Junction j){
        this.j=j;
        load();
    }
    public void load(){
        data=new Object[][]{{j.getCode()
                             ,j
                             ,new Double(j.getPn().getX())
                             ,new Double(j.getPn().getY()),j.getTypeNames()[j.getType()-1]}};
    }
    public int getColumnCount()			{return names.length;}
    public int getRowCount()				{return data.length;}
    public Object getValueAt(int row, int col)	{return data[row][col];}
    public String getColumnName(int column)		{return names[column];}
    public Class getColumnClass(int c) 		{return getValueAt(0, c).getClass();}
    public boolean isCellEditable(int row, int col)	{return true;}//col!=1&&col!=2;}
    public void addTableModelListener(TableModelListener l){
        super.addTableModelListener(l);
    }
    public void setValueAt(Object aValue, int row, int column) {
        data[row][column] = aValue;
        switch(column){
            case 0://code
                j.setCode((String)aValue);
                data[0][0]=j.getCode();
                break;
            case 1:
                break;
            case 2://px
                double y=j.getPn().getY();
                j.setpN(new Point2DSerializable(((Double)aValue).doubleValue(),y,true));
                break;
            case 3://py
                double x=j.getPn().getX();
                j.setpN(new Point2DSerializable(x,((Double)aValue).doubleValue(),true));
                break;
            case 4:
                for(int i=0,len=j.getTypeNames().length;i<len;i++){
                    if(j.getTypeNames()[i].equalsIgnoreCase((String)aValue)){
                        j.setType(i+1);
                        break;
                    }
                }
                System.out.println("object="+aValue);
                break;
        }
        PSRender.repaint_anywhere();
    }
};

