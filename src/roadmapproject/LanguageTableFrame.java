package roadmapproject;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.beans.*;

public class LanguageTableFrame extends JInternalFrame implements ActionListener,IFrameTableReload{

    MultyLanguage ml;
    JButton save;
    JButton cancel;
    JPanel jpButtons;
    JPanel desk;
    LangTableModel table ;
    static LanguageTableFrame prev=null;

    public LanguageTableFrame(MultyLanguage mls) {
        super("Change name for "+mls.getCode(),
                false, //resizable
                false, //closable
                false, //maximizable
                true);//iconifiable
        //PSRender.onHold();
        this.ml=mls;
        super.addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameActivated(InternalFrameEvent e){
                reload();
                SelectedObject.setSelectedObject(ml);
            }
        });
        super.setSize(new Dimension(500, 70));

        save=new JButton("Save");
        cancel=new JButton("Cancel");
        save.addActionListener(this);
        cancel.addActionListener(this);
        jpButtons=new JPanel();
        jpButtons.add(save);
        jpButtons.add(cancel);
        this.table= new LangTableModel(new String[]{"Russian"
                                                    ,"English"
                                                    ,"Hebrew","Visible"},ml);
        JTable jTable = new JTable(this.table);
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setPreferredSize(new Dimension(500, 70));
        desk=new JPanel(new BorderLayout());
        desk.add(scrollPane, BorderLayout.CENTER);
        desk.add(jpButtons,BorderLayout.SOUTH);

        TableColumn colRus = jTable.getColumn("Russian");
        DefaultTableCellRenderer rendererRus=new DefaultTableCellRenderer();
        rendererRus.setToolTipText("Enter name in russian language");
        colRus.setCellRenderer(rendererRus);

        TableColumn colEng = jTable.getColumn("English");
        DefaultTableCellRenderer rendererEng=new DefaultTableCellRenderer();
        rendererEng.setToolTipText("Enter name in english language");
        colEng.setCellRenderer(rendererEng);

        TableColumn colHeb = jTable.getColumn("Hebrew");
        DefaultTableCellRenderer rendererHeb=new DefaultTableCellRenderer();
        rendererHeb.setToolTipText("Enter name in hebrew language");
        colHeb.setCellRenderer(rendererHeb);

        TableColumn typeVis = jTable.getColumn("Visible");
        DefaultTableCellRenderer rendererVis=new DefaultTableCellRenderer(){
            JCheckBox checkBox = new JCheckBox();
            public Component getTableCellRendererComponent(JTable table
                                                           ,Object value
                                                           ,boolean isSelected
                                                           ,boolean hasFocus
                                                           ,int row
                                                           ,int column) {
                if (value instanceof Boolean) {                    // Boolean
                    checkBox.setToolTipText("Set name visibility");
                    checkBox.setSelected(((Boolean)value).booleanValue());
                    checkBox.setHorizontalAlignment(JCheckBox.CENTER);
                    return checkBox;
                }

                System.out.println("ERROR!!!");
                String str = (value == null) ? "" : value.toString();
                return super.getTableCellRendererComponent(
                        table,str,isSelected,hasFocus,row,column);
            }
        };
        typeVis.setCellRenderer(rendererVis);
        super.getContentPane().add(desk);
        super.pack();
        super.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(save==e.getSource()){
            System.out.println("save");
            table.fireTableDataChanged();
            table.save();
            IFrameSupervisor.removeJInternalFrame(ml);
            try {
                super.setClosed(true);
            }
            catch (PropertyVetoException ex) {
                System.out.println("фрейм не может быть закрыт"+ex);
            }
            //PSRender.outHold();
        }else if(cancel==e.getSource()){
            IFrameSupervisor.removeJInternalFrame(ml);
            try {
                super.setClosed(true);
                DBService.loadLanguageMl(ml);
            }
            catch (PropertyVetoException ex) {
                System.out.println("фрейм не может быть закрыт"+ex);
            }
            //PSRender.outHold();
            System.out.println("cancel");
        }
    }
    public void reload(){
        table.load();
    }
    public void internalFrameClosing(InternalFrameEvent e){}
    public void internalFrameClosed(InternalFrameEvent e){}
    public void internalFrameOpened(InternalFrameEvent e){}
    public void internalFrameIconified(InternalFrameEvent e){}
    public void internalFrameDeiconified(InternalFrameEvent e){}
    public void internalFrameActivated(InternalFrameEvent e){}
    public void internalFrameDeactivated(InternalFrameEvent e){}
}


