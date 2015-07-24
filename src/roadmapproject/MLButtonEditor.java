package roadmapproject;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * @version 1.0 11/09/98
 */
public class MLButtonEditor extends DefaultCellEditor {
  protected JButton button;
  protected MultyLanguage ml;
  private boolean   isPushed;
  public MLButtonEditor(JCheckBox checkBox) {
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
    button.setText( ml==null?"null":ml.getCode());
    isPushed = true;
    return button;
  }
  public Object getCellEditorValue() {
    if (isPushed)  {
      if(ml!=null){
        IFrameSupervisor.showJInternalFrame(ml,IFrameSupervisor.PROPERTIES_FRAME);
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