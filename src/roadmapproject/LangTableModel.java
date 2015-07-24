package roadmapproject;
import javax.swing.table.*;
public class LangTableModel extends AbstractTableModel {
    Object[][] data;
    String[] names ;
    MultyLanguage ml;
    final int INITIAL_ROWHEIGHT = 33;
    public LangTableModel(String[] column_names,MultyLanguage ml){
        this.ml=ml;
        this.names=column_names;
        data= new Object[1][4];
        data[0][0]="";
        data[0][1]="";
        data[0][2]="";
        data[0][3]=new Boolean(false);
//    load();

    }
    public void save(){
        DBService.putNamesFor(ml,data);
        ml.setCurrName((String)data[0][DBService.getCurrLang()]);
        ml.setVisibilityName(((Boolean)data[0][3]).booleanValue());
    }
    public void load(){
        this.data = DBService.getNamesFor(ml);
    }
    public int getColumnCount()			{return names.length;}
    public int getRowCount()			{return data.length;}
    public Object getValueAt(int row, int col)	{return data[row][col];}
    public String getColumnName(int column)	{return this.names[column];}
    public Class getColumnClass(int c)		{
        switch(c){
            case 0:
            case 1:
            case 2:
                return String.class;
            case 3:
                return Boolean.class;
            default:
                throw new Error("вылезла за пределы");
        }
    }
    public boolean isCellEditable(int row, int col){return true;}
    public void setValueAt(Object aValue, int row, int column) {
        data[row][column] = aValue	;
        if(column==names.length-1){
            ml.setVisibilityName(((Boolean)aValue).booleanValue());
        }else if(column==DBService.getCurrLang()){
            ml.setCurrName((String)aValue);
        }
        PSRender.repaint_anywhere();
    }
}