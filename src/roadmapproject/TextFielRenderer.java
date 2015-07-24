package roadmapproject;


  import java.awt.*;
  import javax.swing.*;
  import javax.swing.table.*;
  /**
   * @version 1.0 11/09/98
   */
  public class TextFielRenderer extends JTextField implements TableCellRenderer {
    static final Color back_color=new Color(255,0,0,50);
    public TextFielRenderer(){
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                     boolean isSelected, boolean hasFocus, int row, int column) {
      /*if (isSelected) {

      } else{
        setForeground(table.getForeground());
        setBackground(UIManager.getColor("Button.background"));
      }*/
      if(table.isFocusOwner()==false){
        setForeground(back_color);
        setBackground(back_color);
      }else{
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
      }
      setText( (value ==null) ? "null" : value.toString() );
      return this;
    }
}