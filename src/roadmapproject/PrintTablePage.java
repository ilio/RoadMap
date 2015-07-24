package roadmapproject;

import javax.swing.table.TableModel;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

/**
 * Created by Igor
 * Date: 05.03.2003
 * Time: 1:51:51
 */
public class PrintTablePage  implements Printable {
    private TableModel tableModel;
    private int from;
    private int to;
    private String[] column_names;
    private Rectangle2D tableBounds;
    private Font font;
    private static BasicStroke dashStroke=new BasicStroke(0.5f
            ,BasicStroke.CAP_BUTT
            ,BasicStroke.JOIN_BEVEL,0.0f
            ,new float[]{1.0f,1.0f}
            ,0.0f);
    public PrintTablePage(TableModel tableModel,int from,int to) {
        this.tableModel = tableModel;
        this.from=from;
        this.to=to;
        column_names=new String[tableModel.getColumnCount()];
        for(int i=0;i<column_names.length;i++){
            column_names[i]=tableModel.getColumnName(i);
        }
        tableBounds=new Rectangle2D.Double(73,180,450,510);
        font=new Font("Arial",Font.PLAIN,14);
    }
    private void drawTable(Graphics2D g2d){
        int column_size;
        int x,y;
        int column_width=(int)(tableBounds.getWidth()/column_names.length);
        g2d.setFont(font);
        FontMetrics fm=g2d.getFontMetrics();
        y=(int)tableBounds.getY()+fm.getHeight()+5;
        x=(int)tableBounds.getX()+10;
        Rectangle2D bound=new Rectangle2D.Double(tableBounds.getX(),tableBounds.getY(),tableBounds.getWidth(),fm.getHeight()+15);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(bound);
        for(int i=0;i<column_names.length;i++){
            g2d.setColor(Color.BLACK);
            g2d.drawString(column_names[i],x,y);
            x+=column_width;
            g2d.setColor(Color.GRAY);
            g2d.draw(new Line2D.Double(x-10,tableBounds.getMinY(),x-10,tableBounds.getMaxY()));
        }
        g2d.draw(new Line2D.Double(tableBounds.getX(),y+10,tableBounds.getMaxX(),y+10));
        g2d.setColor(Color.BLACK);
        y+=fm.getHeight()+10;
        g2d.setStroke(dashStroke);
        for(int i=from;i<to;i++){
            x=(int)tableBounds.getX()+5;
            for(int j=0;j<column_names.length;j++){
                g2d.setColor(Color.BLACK);
                g2d.drawString(tableModel.getValueAt(i,j).toString(),x,y);
                x+=column_width;
                g2d.setColor(Color.GRAY);
                g2d.draw(new Line2D.Double(tableBounds.getX(),y+5,tableBounds.getMaxX(),y+5));
            }
            y+=fm.getHeight()+2;
        }
        g2d.setStroke(new BasicStroke());
        g2d.setColor(Color.BLACK);
        g2d.draw(tableBounds);
    }
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        drawTable((Graphics2D)graphics);
        return Printable.PAGE_EXISTS;
    }
}
