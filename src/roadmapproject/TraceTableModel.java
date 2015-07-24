package roadmapproject;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by Igor
 * Date: 27.02.2003
 * Time: 21:32:55
 */
public class TraceTableModel extends AbstractTableModel{
    private String[] junction_names;
    private RoadContainer trace;
    TraceTableModel(RoadContainer trace){
        this.trace=trace;
        load();
    }
    Object[][] data;
    String[] names={"junction"};
    public void load(){
        ArrayList traceList;
        JunctionLink currentJL;
        Junction prevJ;
        traceList=trace.getTrace();
        junction_names=new String[traceList.size()+1];
        prevJ=trace.getFirstJunction();
        int i;
        for(i = 0;i<traceList.size();i++){
            currentJL=(JunctionLink) traceList.get(i);
            junction_names[i]=prevJ.getCurrName();
            prevJ=currentJL.getSecondHalfJunction(prevJ);
        }
        junction_names[i]=prevJ.getCurrName();
        data=new Object[][]{junction_names};
    }
    public int getColumnCount()			{return names.length;}
    public int getRowCount()				{return data[0].length;}
    public Object getValueAt(int row, int col)	{return data[col][row];}
    public String getColumnName(int column)		{return names[column];}
    public Class getColumnClass(int c)		{return getValueAt(0, c).getClass();}
    public boolean isCellEditable(int row, int col)	{return false;}
    public void setValueAt(Object aValue, int row, int column){
        data[column][row] = aValue;
        PSRender.repaint_anywhere();
    }
}
