package roadmapproject;

import java.awt.*;

public class PreferencedShape implements DrawCONSTANTS {
  private Shape shape;
  private int drawPriority;
  public PreferencedShape(){
    this(null,LAYER_0);
  }
  public PreferencedShape(Shape sh,int drwPriority){
    setShapePreferences(sh,drwPriority);
  }
  public final void setShape(Shape sh){
    shape=sh;
  }


  public final void setDrawPriority(int drwPriority){
    drawPriority=drwPriority;
  }

  public final void setShapePreferences(Shape sh
				  ,int drwPriority){
    this.shape=sh;
    this.drawPriority=drwPriority;
  }
  public final Shape getShape(){
    return shape;
  }


  public final int getDrawPriority(){
    return drawPriority;
  }
}