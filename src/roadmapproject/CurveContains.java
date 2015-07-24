package roadmapproject;
import java.awt.geom.*;
class CurveContains {

  private static Rectangle2D rect=new Rectangle2D.Double();
//  private static Ellipse2D ell=new Ellipse2D.Double();

  public static boolean isOnLine(QuadCurve2D curve,Ellipse2D tstCirc){
//    double scale=1.0/PSRender.getScale();
    /*ell.setFrame(curve.getP1().getX()-5.0*scale//-PSRender.scale/2
                 ,curve.getP1().getY()-5.0*scale//-PSRender.scale/2
                 ,10.0*scale//PSRender.scale
                 ,10.0*scale);//PSRender.scale);
    if(ell.contains(cp.getPoint2D())){
      return false;
    }
    ell.setFrame(curve.getP2().getX()-PSRender.scale/2
                 ,curve.getP2().getY()-PSRender.scale/2
                 ,PSRender.scale
                 ,PSRender.scale);
    if(ell.contains(cp.getPoint2D())){
      return false;
    }*/
    if(tstCirc.contains(curve.getP1())||tstCirc.contains(curve.getP2())){
      return false;
    }
    rect.setFrame(tstCirc.getBounds());
    //Line2D tstLine=new Line2D.Double();
    QuadCurve2D curves[]=new QuadCurve2D[4];
    int i;
    for(i=0;i<4;i++){
      curves[i]=new QuadCurve2D.Double();
    }
    curve.subdivide(curves[0],curves[1]);
    curves[0].subdivide(curves[2],curves[3]);
    if(curves[2].intersects(rect)){
      return true;
    }
    if(curves[3].intersects(rect)){
      return true;
    }
    curves[1].subdivide(curves[2],curves[3]);
    if(curves[2].intersects(rect)){
      return true;
    }
    if(curves[3].intersects(rect)){
      return true;
    }
    return false;
  }
}