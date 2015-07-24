package roadmapproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by Igor
 * Date: 27.02.2003
 * Time: 21:37:22
 */
public class TracePanel extends JPanel{
    private TraceTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;
    private static TracePanel instance;
    private JLabel labelDistance;
//    private JList list;

    public TracePanel() {
        instance=this;
        super.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Trace"),
                        BorderFactory.createEmptyBorder(0,5,5,5)));
        table=new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        table.setPreferredScrollableViewportSize(new Dimension(500, 20));
        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(190,220));
        labelDistance=new JLabel(" ");
        super.add(labelDistance);
        super.add(scrollPane);
//        list=new JList();
//        super.add(list);
//        super.setBackground(Color.WHITE);
    }
    public static void setTrace(RoadContainer trace){
        if(instance!=null){
            if(trace!=null){
                instance.tableModel=new TraceTableModel(trace);
                instance.table.setModel(instance.tableModel);
                instance.labelDistance.setText("Total distance="+(int)(trace.getDistance()/1000)+"km");
                instance.table.repaint();

//                ArrayList traceList;
//                JunctionLink currentJL;
//                Junction prevJ;
//                traceList=trace.getTrace();
//                String[] junction_names;
//                junction_names=new String[traceList.size()+1];
//                prevJ=trace.getFirstJunction();
//                int i;
//                for(i = 0;i<traceList.size();i++){
//                    currentJL=(JunctionLink) traceList.get(i);
//                    junction_names[i]=prevJ.getCurrName();
//                    prevJ=currentJL.getSecondHalfJunction(prevJ);
//                }
//                junction_names[i]=prevJ.getCurrName();
//                instance.list.setListData(junction_names);
//            instance.list.
            }else{
                instance.table.setModel(new DefaultTableModel());
                instance.labelDistance.setText(" ");
            }
        }
    }
}
