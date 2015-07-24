package roadmapproject;

import java.util.Date;

/**
 * Created by Igor
 * Date: Dec 22, 2002
 * Time: 2:55:23 AM
 */
public class TableSorter {
    SortableTableModel model;

    public TableSorter(SortableTableModel model) {
        this.model = model;
    }


    //n2 selection
    public void sort(int column, boolean isAscent) {
        int n = model.getRowCount();
        int[] indexes = model.getIndexes();

        for (int i=0; i<n-1; i++) {
            int k = i;
            for (int j=i+1; j<n; j++) {
                if (isAscent) {
                    if (compare(column, j, k) < 0) {
                        k = j;
                    }
                } else {
                    if (compare(column, j, k) > 0) {
                        k = j;
                    }
                }
            }
            int tmp = indexes[i];
            indexes[i] = indexes[k];
            indexes[k] = tmp;
        }
    }


    // comparaters

    public int compare(int column, int row1, int row2) {
        Object o1 = model.getValueAt(row1, column);
        Object o2 = model.getValueAt(row2, column);
        if (o1 == null && o2 == null) {
            return  0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return  1;
        } else {
            Class type = model.getColumnClass(column);
            if (type.getSuperclass() == Number.class) {
                return compare((Number)o1, (Number)o2);
            } else if (type == String.class) {
                return ((String)o1).compareTo((String)o2);
            } else if (o1 instanceof MultyLanguage) {
                return compare((MultyLanguage)o1, (MultyLanguage)o2);
            } else if (type == Boolean.class) {
                return compare((Boolean)o1, (Boolean)o2);
            } else {
                return ((String)o1).compareTo((String)o2);
            }
        }
    }
    public int compare(MultyLanguage ml_1,MultyLanguage ml_2){
        return ml_1.getCode().compareToIgnoreCase(ml_2.getCode());
    }
    public int compare(Number o1, Number o2) {
        double n1 = o1.doubleValue();
        double n2 = o2.doubleValue();
        if (n1 < n2) {
            return -1;
        } else if (n1 > n2) {
            return 1;
        } else {
            return 0;
        }
    }

    public int compare(Boolean o1, Boolean o2) {
        boolean b1 = o1.booleanValue();
        boolean b2 = o2.booleanValue();
        if (b1 == b2) {
            return 0;
        } else if (b1) {
            return 1;
        } else {
            return -1;
        }
    }

}