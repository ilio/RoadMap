package roadmapproject;

import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Igor
 * Date: Dec 22, 2002
 * Time: 3:01:19 AM
 */
public class SortedTableHeaderListener extends MouseAdapter {
    JTableHeader   header;
    SortButtonRenderer renderer;

    SortedTableHeaderListener(JTableHeader header,SortButtonRenderer renderer) {
      this.header   = header;
      this.renderer = renderer;
    }

    public void mousePressed(MouseEvent e) {
      int col = header.columnAtPoint(e.getPoint());
      int sortCol = header.getTable().convertColumnIndexToModel(col);
      renderer.setPressedColumn(col);
      renderer.setSelectedColumn(col);
      header.repaint();

      if (header.getTable().isEditing()) {
        header.getTable().getCellEditor().stopCellEditing();
      }

      boolean isAscent;
      if (SortButtonRenderer.DOWN == renderer.getState(col)) {
        isAscent = true;
      } else {
        isAscent = false;
      }
      ((SortableTableModel)header.getTable().getModel())
        .sortByColumn(sortCol, isAscent);
    }

    public void mouseReleased(MouseEvent e) {
      header.columnAtPoint(e.getPoint());
      renderer.setPressedColumn(-1);                // clear
      header.repaint();
    }
  }
