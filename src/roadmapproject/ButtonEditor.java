package roadmapproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * @version 1.0 11/09/98
 */
public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    protected MultyLanguage ml;
    protected boolean   isPushed;
    public ButtonEditor(JCheckBox checkBox) {
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
        ml = (MultyLanguage)((value ==null) ? null : value);
        setText();
        isPushed = true;
        return button;
    }
    protected void setText(){
        button.setText("refresh");
    }
    public Object getCellEditorValue() {
        if (isPushed)  {
            JOptionPane.showMessageDialog(button ,ml + ": Ouch!");
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