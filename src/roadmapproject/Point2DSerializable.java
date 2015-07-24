package roadmapproject;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class Point2DSerializable
        implements java.io.Serializable {
    private static final int X=0;
    private static final int Y=1;
    private double[] pnts;
    private boolean isFrameCoords,isAbsCoords;
    private int recursionLevel=10;
    public static final AffineTransform at=new AffineTransform();
    public Point2DSerializable(boolean isAbs){
        this(0,0,isAbs);
    }
    public Point2DSerializable(double x, double y,boolean isAbs) {
        this.pnts = new double[2];
        this.pnts[X] = x;
        this.pnts[Y] = y;
        isAbsCoords=isAbs;
        isFrameCoords=isAbs==false;
    }
    public Point2DSerializable(Point2DSerializable p){
        this(p.getX(),p.getY(),p.isAbsCoords);
//        isAbsCoords=p.isAbsCoords;
//        isFrameCoords=p.isFrameCoords;
    }
//    public Point2DSerializable(Point2D p){
//        this(p.getX(),p.getY(),true);
//    }
    public Point2DSerializable(Point2D p,boolean trueToAbs_falseToFrm) {
        this(p.getX(),p.getY(),trueToAbs_falseToFrm==false);
        if(trueToAbs_falseToFrm==true){
            cnvFrmToAbs();
        }else{
            cnvAbsToFrm();
        }
    }
    public Point2DSerializable(Point2DSerializable p,boolean trueToAbs_falseToFrm)  {
        this(p);
        isAbsCoords=p.isAbsCoords;
        isFrameCoords=p.isFrameCoords;
        if(trueToAbs_falseToFrm==true){
            cnvFrmToAbs();
        }else{
            cnvAbsToFrm();
        }
    }
    public String toString() {
        String st=super.toString();
        int ind=st.indexOf("@");
        return  "pnt"+st.substring(ind)+" [x="+pnts[X]+",y="+pnts[Y]+"] type="+(isAbsCoords?"abs":(isFrameCoords?"frame":"no type"));
    }
    public final double getX() {
        return pnts[X];
    }
    public final double getY() {
        return pnts[Y];
    }
    public Point2DSerializable setLocation(double x, double y) {
        this.pnts[X] = x;
        this.pnts[Y] = y;
        isFrameCoords=false;
        isAbsCoords=false;
        return this;
    }
    public Point2DSerializable setLocation(Point2DSerializable sp){
        setLocation(sp.getX(),sp.getY());
        isAbsCoords=sp.isAbsCoords;
        isFrameCoords=sp.isFrameCoords;
        return this;
    }
    public boolean checkInsideFrame(){
        double[] pntsMap=new double[]{0,0
                                      ,PSRender.getGridWidth(),0
                                      ,PSRender.getGridWidth(),PSRender.getGridHeigth()
                                      ,0,PSRender.getGridHeigth()};
        Rectangle2D mapRect;
        if(isFrameCoords){
            at.transform(pntsMap,0,pntsMap,0,4);
        }
        mapRect=new Rectangle2D.Double(pntsMap[0],pntsMap[1],pntsMap[2]-pntsMap[0],pntsMap[7]-pntsMap[1]);
        if(mapRect.contains(pnts[X],pnts[Y])==false){
            int ans;
            ans=JOptionPane.showConfirmDialog(MapApplication.getInstance()
                    , "process?"
                    , "point outside of map"
                    ,JOptionPane.YES_NO_OPTION);
            if (ans == 0) {
                return true;
            }
            return false;
        }
        return true;
    }
    public Point2D getPoint2D(){
        return new Point2D.Double(pnts[X],pnts[Y]);
    }
    public double distance(Point2DSerializable pt) {
        double PX = pt.getX() - this.getX();
        double PY = pt.getY() - this.getY();
        return Math.sqrt(PX * PX + PY * PY);
    }
    public boolean equals(Object obj) {
        if (obj instanceof Point2DSerializable) {
            Point2DSerializable p2d = (Point2DSerializable) obj;
            return (getX() == p2d.getX()) && (getY() == p2d.getY());
        }
        return super.equals(obj);
    }
    public void multiply(double numX,double numY){
        pnts[X]*=numX;
        pnts[Y]*=numY;
    }
    public void multiply(double num){
        multiply(num,num);
    }
    public void addNums(double numX,double numY){
        pnts[X]+=numX;
        pnts[Y]+=numY;
    }
    public void addNum(double num){
        addNums(num,num);
    }
    public void addNumsFromPoint(Point2DSerializable p){
        if(isAbsCoords==p.isAbsCoords
                &&isFrameCoords==p.isFrameCoords){
            addNums(p.getX(),p.getY());
        }else{
            throw new Error("нельзя слаживать разные системы координат");
        }
    }
    public void divide(double numX,double numY){
        pnts[X]/=numX;
        pnts[Y]/=numY;
    }
    public void divide(double num){
        divide(num,num);
    }
    public void subNums(double numX,double numY){
        pnts[X]-=numX;
        pnts[Y]-=numY;
    }
    public void subNum(double num){
        subNums(num,num);
    }
    public void subNumsFromPoint(Point2DSerializable p){
        if(isAbsCoords==p.isAbsCoords
                &&isFrameCoords==p.isFrameCoords){
            subNums(p.getX(),p.getY());
        }else{
            throw new Error(" нельзя вычитать числа из разных"
                    +" систем координат "+
                    " 1 isAbs="+isAbsCoords+
                    " 2 isAbs="+p.isAbsCoords+
                    " 1 isFrm="+isFrameCoords+
                    " 2 isFrm="+isFrameCoords);
        }
    }
    public void subNums(double x,double y,boolean isAbs){
        if(this.isAbsCoords!=isAbs){
            throw new Error(" нельзя вычитать числа из разных"
                    +" систем координат "+
                    " 1 isAbs="+isAbsCoords+
                    " 2 isAbs="+isAbs+
                    " 1 isFrm="+isFrameCoords+
                    " 2 isFrm="+isFrameCoords);
        }
        this.pnts[X]-=x;
        this.pnts[Y]-=y;
    }
    public final Point2DSerializable cnvFrmToAbs(){
        if(isAbsCoords==false){
            try {
                at.inverseTransform(pnts,0,pnts,0,1);
            } catch (NoninvertibleTransformException e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }else{
            throw new Error("Point2DSerializable.cnvFrmToAbs:"
                    +"нет возможности конвертировать AbsToAbs"+this);
        }
        isAbsCoords=true;
        isFrameCoords=false;
        return this;
    }
    public final Point2DSerializable cnvAbsToFrm(){

        double[] oldPnts=new double[2];
        oldPnts[X]=pnts[X];
        oldPnts[Y]=pnts[Y];
        if(isFrameCoords==false){
            at.transform(pnts,0,pnts,0,1);
            if(Double.isNaN(pnts[X])||Double.isNaN(pnts[Y])||Double.isInfinite(pnts[X])||Double.isInfinite(pnts[Y])){
                if(recursionLevel--==0){
                    recursionLevel=10;
                    at.setTransform(AffineTransform.getTranslateInstance(0.0,0.0));
                    at.transform(pnts,0,pnts,0,1);
                    System.out.println("recursion ended");
                    return this;
                }
                System.out.println("out of range "+this
                        +" old x="+oldPnts[X]
                        +" old y="+oldPnts[Y]
                        +" scale x="+at.getScaleX()
                        +" scale y="+at.getScaleY()
                        +" translate x="+at.getTranslateX()
                        +" translate y="+at.getTranslateY()
                );
                pnts[X]=oldPnts[X];
                pnts[Y]=oldPnts[Y];
                PSRender.moveToTop(new Rectangle2D.Double(0.0,0.0,PSRender.getGridWidth(),PSRender.getGridHeigth()),1.0);
                cnvAbsToFrm();
            }
        }else{
            throw new Error("Point2DSerializable.cnvAbsToFrm:"
                    +"нет возможности конвертировать FrmToFrm"+this);
        }
        isAbsCoords=false;
        isFrameCoords=true;
        return this;
    }
    public static void translate(double x,double y){
        at.translate(x,y);
    }
    public static void setToTransform(double x,double y){
        double s=at.getScaleX();
        at.setTransform(s,0.0,0.0,s,x*s,y*s);
    }
    public static void scale(double s){
        double[] tps=new double[2],dps=new double[2];
        tps[X]=at.getTranslateX();
        tps[Y]=at.getTranslateY();
        dps[X]=PSRender.center.getX();
        dps[Y]=PSRender.center.getY();
        try {
            at.inverseTransform(dps,0,dps,0,1);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        at.setTransform(s,0.0,0.0,s,tps[X],tps[Y]);
        tps[X]=PSRender.center.getX();
        tps[Y]=PSRender.center.getY();
        try {
            at.inverseTransform(tps,0,tps,0,1);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        at.translate((tps[X]-dps[X]),(tps[Y]-dps[Y]));
    }
    public final boolean isFrameCoords(){
        return isFrameCoords;
    }
    public final boolean isAbsCoords(){
        return isAbsCoords;
    }
    public final Point2DSerializable setCoordsAsAbs(){
        if(isFrameCoords==false){
            isAbsCoords=true;
        }else{
            throw new Error(" нельзя установить координаты как"+
                    " Abs, которые являются Frm");
        }
        return this;
    }
    public final Point2DSerializable setCoordsAsFrm(){
        if(isAbsCoords==false){
            isFrameCoords=true;
        }else{
            throw new Error(" нельзя установить координаты как"+
                    " Frm, которые являются Abs");
        }
        return this;
    }
    public Point2DSerializable newInstance(){
        return new Point2DSerializable(this);
    }
    public void translate(Point2DSerializable p){
        if(isAbsCoords!=p.isAbsCoords||isFrameCoords!=p.isFrameCoords){
            throw new Error("нельзя транслировать разные типы "+this+" p="+p);
        }
        pnts[X]+=p.pnts[X];
        pnts[Y]+=p.pnts[Y];
    }
}