package roadmapproject;

import java.awt.geom.*;
public class ObjectSerializable implements java.io.Serializable,SelectedTypeCONSTANTS,Cloneable{
  Point2DSerializable curve[];
  Point2DSerializable point;
  Junction junction;
  int type;
  public ObjectSerializable(){
    reset();
  }
  public synchronized Object getObject(){
    if(point!=null){
      return point;
    }
    if(curve!=null){
      return new QuadCurve2D.Double(curve[0].getX()
                                    ,curve[0].getY()
                                    ,curve[1].getX()
                                    ,curve[1].getY()
                                    ,curve[2].getX()
                                    ,curve[2].getY());
    }
    return null;
  }
  public synchronized void setObject(Object o,int type){
    reset();
    if(o instanceof Point2DSerializable){
      point=(Point2DSerializable)o;
      if((type&(P2_POINT|CTRL_POINT|OTHER_OBJECT))!=0){
        this.type=type;
      }else{
        throw new Error("не допустимый тип точки тип="+type+" в "+o);
      }
    }else if(o instanceof QuadCurve2D){
      curve=new Point2DSerializable[3];
      curve[0]=new Point2DSerializable(((QuadCurve2D)o).getP1().getX(),((QuadCurve2D)o).getP1().getY(),true);
      curve[1]=new Point2DSerializable(((QuadCurve2D)o).getCtrlPt().getX(),((QuadCurve2D)o).getCtrlPt().getY(),true);
      curve[2]=new Point2DSerializable(((QuadCurve2D)o).getP2().getX(),((QuadCurve2D)o).getP2().getY(),true);
      if(type==CURVE||type==(CURVE|OTHER_OBJECT)){
        this.type=type;
      }else{
        throw new Error("не допустимый тип кривой "+type+" в "+o);
      }
    }else if(o instanceof Junction){
      junction=(Junction)o;
      point=junction.getPn();
      if((type&(JUNCTION_POINT|P1_POINT))!=0){
        this.type=type;
      }else{
        throw new Error("не допустимый тип точки тип="+type+" в "+o);
      }
    }else{
      this.type=NO_SELECTED_TYPE;
      if(o!=null){
        System.out.println("не допустимый класс в ObjectSerializable "+o);
      }
    }
  }
    public synchronized void setObject(ObjectSerializable o){
        this.curve=o.curve;
        this.point=o.point;
        this.junction=o.junction;
        this.type=o.type;
    }
  public synchronized void setJunction(Junction j){
    if(j instanceof Junction){
      this.junction=j;
    }else{
      this.junction=null;
    }
  }
  public final Point2DSerializable[] getCurvePoints(){
    return this.curve;
  }
  public final Point2DSerializable getPoint(){
    return this.point;
  }
  public final Junction getJunction(){
    return this.junction;
  }
  public final int getType(){
    return this.type;
  }
  public boolean isPoint(){
    return point!=null;
  }
  public boolean isCurve(){
    return curve!=null;
  }
  public boolean isJunction(){
    return junction!=null;
  }
  public synchronized void reset(){
    this.point=null;
    this.curve=null;
    this.junction=null;
    this.type=NO_SELECTED_TYPE;
  }
    public ObjectSerializable newInstance(){
//        ObjectSerializable newObj;
//        newObj = new ObjectSerializable();
//        newObj.curve=this.curve;
//        newObj.point=this.point;
//        newObj.junction=this.junction;
//        newObj.type=this.type;
        try {
            return (ObjectSerializable) this.clone();//newObj;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return null;
    }
}