package roadmapproject;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.DefaultCellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

class TextFieldEditor implements TableCellEditor {
    DefaultCellEditor cellEditors;
    public TextFieldEditor() {
        JTextField textField = new JTextField();
        cellEditors = new DefaultCellEditor(textField);
    }
    public Component getTableCellEditorComponent(JTable table
                                                 ,Object value
                                                 ,boolean isSelected
                                                 ,int row
                                                 ,int column){
        if (value instanceof String) {
            return cellEditors.getTableCellEditorComponent(table
                    ,value
                    ,isSelected
                    ,row
                    ,column);
        }
        if(value instanceof MultyLanguage){
            System.out.println("text field editor report: component "+value+" edited");
            return cellEditors.getTableCellEditorComponent(table
                    ,((MultyLanguage)value).getCode()
                    ,isSelected
                    ,row
                    ,column);
        }
        return null;
    }
    public Object getCellEditorValue()				{return cellEditors.getCellEditorValue();}
    public Component getComponent()				{return cellEditors.getComponent();}
    public boolean stopCellEditing()				{return cellEditors.stopCellEditing();}
    public void cancelCellEditing()				{cellEditors.cancelCellEditing();}
    public boolean isCellEditable(EventObject anEvent)		{return true;}
    public boolean shouldSelectCell(EventObject anEvent)		{return cellEditors.shouldSelectCell(anEvent);}
    public void addCellEditorListener(CellEditorListener l)	{cellEditors.addCellEditorListener(l);}
    public void removeCellEditorListener(CellEditorListener l)	{cellEditors.removeCellEditorListener(l);}
    public void setClickCountToStart(int n)			{cellEditors.setClickCountToStart(n);}
    public int getClickCountToStart()				{return cellEditors.getClickCountToStart();}
}