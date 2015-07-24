package roadmapproject;

import java.awt.geom.*;
public class PathBounds implements java.io.Serializable {
  private double x,y,w,h;
  public PathBounds(){
  }
  public PathBounds(double x,double y,double w,double h){
    this.x=x;
    this.y=y;
    this.w=w;
    this.h=h;
  }
  public PathBounds(PathBounds pb){
    this(pb.x,pb.y,pb.w,pb.h);
  }
  public void setFrame(double x,double y,double w,double h){
    this.x=x;
    this.y=y;
    this.w=w;
    this.h=h;
  }
  public double getX(){
    return x;
  }
  public double getY(){
    return y;
  }
  public double getWidth(){
    return w;
  }
  public double getHeight(){
    return h;
  }
  public Rectangle2D getRectangle2D(){
    return new Rectangle2D.Double(x,y,w,h);
  }
  public boolean contains(double x, double y) {
        return (x >= this.x &&
                y >= this.y &&
                x < this.x + this.w &&
                y < this.y + this.h);
    }
}