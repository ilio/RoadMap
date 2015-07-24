package roadmapproject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by Igor
 * Date: 26.03.2003
 * Time: 12:05:21
 */
public class ErrorTableFrame{
    private JDialog modalFrame;
    private ErrorTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;
    public ErrorTableFrame(CheckReport[] reports){
        tableModel=new ErrorTableModel(reports);
        table=new JTable(tableModel);
        TableColumn objColumn=table.getColumn("Object");
        MLButtonRenderer rendererObjColumn=new MLButtonRenderer();
        rendererObjColumn.setToolTipText("");
        objColumn.setCellRenderer(rendererObjColumn);
        MLButtonEditor editorObjColumn=new MLButtonEditor(new JCheckBox()){
            public Object getCellEditorValue(){
                IFrameSupervisor.showJInternalFrame(ml,IFrameSupervisor.PROPERTIES_FRAME);
//                modalFrame.dispose();
                return ml;
            }
        };
        objColumn.setCellEditor(editorObjColumn);

        TableColumn col_delete=table.getColumn("Refresh");
        ButtonRenderer renderer_delete=new ButtonRenderer();
        renderer_delete.setToolTipText("Click for refresh");
        col_delete.setCellRenderer(renderer_delete);
        ButtonEditor editor_detele=new ButtonEditor(new JCheckBox()){
            public Object getCellEditorValue(){
                int row=tableModel.find_ml(ml);
                DBService.initOutlines();
                CheckReport report=((DBAccess)ml).selfCheck();
                tableModel.setValueAt(report.getObject(),row,0);
                tableModel.setValueAt(report.toString(),row,1);
                System.out.println("refresh for "+ml+" row"+row+" report:"+report);
                isPushed=false;
                return ml;
            }
        };
        col_delete.setCellEditor(editor_detele);
        col_delete.setMaxWidth(70);

        scrollPane=new JScrollPane(table);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        if(tableModel.getRowCount()==0){
            JOptionPane.showMessageDialog(RoadPanel.lastRoadPanel,"No found","Congratulation",JOptionPane.WARNING_MESSAGE);
            return;
        }
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel panel=new JPanel(new BorderLayout());
        panel.add(scrollPane,BorderLayout.CENTER);
        JButton closeButton=new JButton("Close");
        closeButton.setBorder(BorderFactory.createEmptyBorder(5,100,5,100));
        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                modalFrame.dispose();
            }
        });
        panel.add(closeButton,BorderLayout.SOUTH);
        modalFrame=new JDialog(MapApplication.getInstance(),"warnings",false);
        modalFrame.setContentPane(panel);
        modalFrame.pack();
        modalFrame.setLocationRelativeTo(MapApplication.getInstance());
        modalFrame.setVisible(true);
    }

}
class ErrorTableModel extends AbstractTableModel{

    Object[][] data;
    String[] names={"Object","Warning","Refresh"};
    CheckReport[] reports;
    int size;
    ErrorTableModel(CheckReport[] reports){
        this.reports=reports;
        load();
    }
    public void load(){
        data=new Object[reports.length][];
        size=0;
        for(int i=0;i<reports.length;i++){
            if(reports[i].hasError()){
                data[size++]=new Object[]{
                    reports[i].getObject()
                    ,reports[i].toString()
                    ,reports[i].getObject()};
            }
        }
    }
    public int find_ml(MultyLanguage ml){
        for(int i=0;i<size;i++){
            if(data[i][0].equals(ml)){
                return i;
            }
        }
        System.out.println("ml="+ml);
        for(int i=0;i<size;i++){
            System.out.println(i+"-"+data[i][0]);
        }
        System.out.println("size="+size);
        return -1;
    }
    public int getColumnCount(){
        return names.length;
    }
    public int getRowCount(){
        return size;
    }
    public Object getValueAt(int row,int col){
        return data[row][col];
    }
    public String getColumnName(int column){
        return names[column];
    }
    public Class getColumnClass(int col){
        switch(col){
            case 0:
                return MultyLanguage.class;
            case 1:
                return String.class;
            case 2:
                return MultyLanguage.class;
            default:
                throw new Error("вылез за границы таблицы");
        }
    }
    public boolean isCellEditable(int row,int col){
        return col!=1;
    }
    public void setValueAt(Object aValue,int row,int column){
        data[row][column]=aValue;
    }
}