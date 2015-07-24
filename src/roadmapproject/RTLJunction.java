package roadmapproject;

import java.awt.*;
import java.awt.geom.*;
public class RTLJunction implements RTextLabel{
  private String text;
  private RoundRectangle2D rect;
  private Line2D line;
  private Point2DSerializable point;
  private static final int FONTSIZE=10;
  private static final Font FONT=new Font("courier",Font.BOLD,FONTSIZE);;
  private static FontMetrics fm;
  private Stroke stroke=new BasicStroke(FONTSIZE/10);
  private static final Color TRANSPARENCY_WHITE=new Color(200,200,200,100);
  private Color colorTxt;


  public RTLJunction(){
    text="";
    rect=new RoundRectangle2D.Double();
    line=new Line2D.Double();
    point=new Point2DSerializable(true);
    colorTxt=Color.DARK_GRAY;

  }
  public RTLJunction(String label,Point2DSerializable point){
    this();
    this.text=label;
    this.point.setLocation(point);
  }
  public void setLabel(String labelName){
    text=labelName;
  }
  public void setLocation(Point2DSerializable p){
    point.setLocation(p);
  }
  public void showLabel(Graphics2D g2d){
    if(text==null){
      return;
    }
    g2d.setFont(FONT);
    fm=g2d.getFontMetrics();
    Rectangle2D bnd=fm.getStringBounds(text,g2d);
    g2d.setStroke(stroke);
    line.setLine(point.getX(),point.getY(),point.getX()-bnd.getWidth()*0.05
                 ,point.getY()-bnd.getHeight()*0.2);
    g2d.setPaint(Color.LIGHT_GRAY);
    g2d.draw(line);

    rect.setRoundRect(point.getX()-bnd.getWidth()*0.1
                      ,point.getY()-bnd.getHeight()*1.3
                      ,bnd.getWidth()*1.1
                      ,bnd.getHeight()
                      ,bnd.getHeight()/1.5
                      ,bnd.getHeight()/1.5);
    g2d.setPaint(TRANSPARENCY_WHITE);
    g2d.fill(rect);
    g2d.setPaint(Color.GRAY);
    g2d.draw(rect);

    g2d.setPaint(colorTxt);
    g2d.drawString(text,(float)(point.getX()-bnd.getWidth()*0.1+bnd.getWidth()*0.03)
                   ,(float)(point.getY()-bnd.getHeight()*0.25-bnd.getHeight()/3.0));
    //g2d.drawString("width="+bounds.getWidth()+" heigth="+bounds.getHeight(),20,100);
  }
  public void setTextColor(Color textColor){
    colorTxt=textColor;
  }
}