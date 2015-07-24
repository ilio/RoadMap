package roadmapproject;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * @version 1.0 11/09/98
 */
public class MLButtonRenderer extends JButton implements TableCellRenderer {
  public MLButtonRenderer() {
    setOpaque(true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                   boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());
    } else{
      setForeground(table.getForeground());
      setBackground(UIManager.getColor("Button.background"));
    }
    setText( (value ==null) ? "not present" : value.toString());

    return this;
  }

}