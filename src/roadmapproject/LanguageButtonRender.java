package roadmapproject;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * Created by Igor
 * Date: Dec 22, 2002
 * Time: 9:52:32 PM
 */
public class LanguageButtonRender  extends JButton implements TableCellRenderer {
    public LanguageButtonRender() {
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
        String st="NOT PRESENT";
        if(value!=null){
            st=((MultyLanguage)value).getCurrName();
            if(st.length()<1||"null".equalsIgnoreCase(st)){
                 st="NOT PRESENT";
            }
        }
        setText(st);
        return this;
    }

}