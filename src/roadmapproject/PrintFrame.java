package roadmapproject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.ArrayList;

/**
 * Created by Igor
 * Date: 23.02.2003
 * Time: 19:54:55
 */
public class PrintFrame {
    private JDialog modalFrame;
    private PrintTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;
    private JButton printButton;
    private JButton cancelButton;

    public PrintFrame(RoadContainer trace) throws HeadlessException {
        modalFrame=new JDialog(MapApplication.getInstance(),"print",true);
        tableModel=new PrintTableModel(trace);
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        table.setPreferredScrollableViewportSize(new Dimension(500,50));
        table.setBackground(Color.WHITE);
//        setBackground(Color.WHITE);
        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600,400));
        scrollPane.setBackground(Color.WHITE);
        JPanel contentPanel=new JPanel(new BorderLayout());
        modalFrame.setContentPane(contentPanel);
//        super.getContentPane().setLayout(new BorderLayout());
//        super.getContentPane().add(scrollPane,BorderLayout.CENTER);
        contentPanel.add(scrollPane,BorderLayout.CENTER);
        printButton=new JButton("Print");
        cancelButton=new JButton("Cancel");
        JPanel southPanel=new JPanel();
        southPanel.add(printButton);
        southPanel.add(cancelButton);
        contentPanel.add(southPanel,BorderLayout.SOUTH);
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                PSRender.paintOn();
                modalFrame.dispose();
            }
        });
        printButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // Get a PrinterJob
                modalFrame.dispose();
                PSRender.paintOn();
                PrinterJob job = PrinterJob.getPrinterJob();
                // Create a landscape page format
                PageFormat landscape = job.defaultPage();
                landscape.setOrientation(PageFormat.LANDSCAPE);
                // Set up a book
                Book bk = new Book();
                int i=0;
                while(i+25<tableModel.getRowCount()){
                    bk.append(new PrintTablePage(tableModel,i,i+25), job.defaultPage());
                    i+=25;
                }
                bk.append(new PrintTablePage(tableModel,i,tableModel.getRowCount()), job.defaultPage());
//                bk.append(new PrintTablePage(tableModel,0,1), job.defaultPage());
//                bk.append(new PaintContent(), landscape);
                // Pass the book to the PrinterJob
                job.setPageable(bk);
                // Put up the dialog box
                if (job.printDialog()) {
                    // Print the job if the user didn't cancel printing
                    try { job.print(); }
                    catch (Exception exc) { /* Handle Exception */
                        exc.printStackTrace();
                    }
                }

            }
        });
        PSRender.paintOff();
        modalFrame.pack();
        modalFrame.setLocationRelativeTo(MapApplication.getInstance());
        modalFrame.setVisible(true);
    }
}
class PrintTableModel extends AbstractTableModel{
    private String[] from;
    private String[] to;
    private Double[] length;
    private RoadContainer trace;
    PrintTableModel(RoadContainer trace){
        this.trace=trace;
        load();
    }
    Object[][] data;
    String[] names={"length","to","from"};
    public void load(){
        ArrayList traceList;
        JunctionLink currentJL;
        Junction prevJ;
        traceList=trace.getTrace();
        from=new String[traceList.size()];
        to=new String[traceList.size()];
        length=new Double[traceList.size()];
        prevJ=trace.getFirstJunction();
        for(int i=0;i<traceList.size();i++){
            currentJL=(JunctionLink) traceList.get(i);
            from[i]=prevJ.getCurrName();
            prevJ=currentJL.getSecondHalfJunction(prevJ);
            to[i]=prevJ.getCurrName();
            length[i]=new Double(currentJL.getPathLength());
        }
        data=new Object[][]{length,to,from};
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
