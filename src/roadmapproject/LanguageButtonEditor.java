package roadmapproject;

/**
 * Created by Igor
 * Date: Dec 22, 2002
 * Time: 9:48:28 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class LanguageButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private MultyLanguage ml;
    private boolean   isPushed;
    public LanguageButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else{
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        ml = (value ==null) ? null : ((MultyLanguage)value);
        String st="NOT PRESENT";
        if(value!=null){
            st=((MultyLanguage)value).getCurrName();
            if(st.length()<1||"null".equalsIgnoreCase(st)){
                 st="NOT PRESENT";
            }
        }
        button.setText(st);//setText( ml==null?"NOT PRESENT":ml.getCurrName());
        isPushed = true;
        return button;
    }
    public Object getCellEditorValue() {
        if (isPushed)  {
            System.out.println("ml="+ml);
            if(ml!=null){
                IFrameSupervisor.showJInternalFrame(ml,IFrameSupervisor.LANGUAGE_FRAME);
            }
        }
        isPushed = false;
        return ml;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}