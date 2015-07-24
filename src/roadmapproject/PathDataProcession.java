package roadmapproject;

import java.awt.geom.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class PathDataProcession {

    public static void subdivide(JunctionLink jl_sub
                                 ,Junction j_sub){

        SelectedObject.setNoBlink();
        Point2DSerializable point=j_sub.getPn();
        Point2DSerializable tmpP2,tmpCtrl;
        Junction junc_2;
        JunctionLink jlink_2= new JunctionLink(j_sub);
        jlink_2.setType(jl_sub.getType());
        if(jl_sub.contains(point)==false){
            throw new Error(" не возможно поделить JunctionLink:"+
                    jl_sub+" в точке "+point+" junction="+j_sub);
        }
        junc_2=null;
        if(jl_sub.getSecondJunction()!=null){
            junc_2=jl_sub.getSecondJunction();
        }
        jl_sub.removeSecondJunction();

        while((tmpP2=jl_sub.removeLastP2()).equals(point)==false){
            tmpCtrl=jl_sub.removeLastCtrl();
            jlink_2.addPointCtrl2Start(tmpCtrl);
            jlink_2.addPointP22Start(tmpP2);
        }
        if(junc_2!=null){
            junc_2.addPath(jlink_2);
        }
        if(jlink_2.isEmpty()){
            jlink_2.remove();
        }
        jl_sub.addPointP22End(tmpP2);
        j_sub.addPath(jl_sub);
        SelectedObject.setBlink();
    }
    public static final void insert(JunctionLink jl,Point2DSerializable p){
        Point2DSerializable pathPoints[]=JunctionLink.getPathPoints(jl);
        int size=(pathPoints.length-1)/2;
        double scale=1/PSRender.getScale();
        Ellipse2D ell=new Ellipse2D.Double(p.getX()-5.0*scale//-scale/2.0
                ,p.getY()-5.0*scale//-scale/2.0
                ,10.0*scale,10.0*scale);
        for(int i=0;i<size;i++){
            if(CurveContains.isOnLine(JunctionLink.getCurveOnIndex(pathPoints,i),ell)){
                Point2DSerializable prevP=jl.getPPoint(i);
                Point2DSerializable next=jl.getPPoint(i+1);
                Point2DSerializable ctrl=jl.getCtrlPoint(i);
                Point2DSerializable left,rigth;
                left=new Point2DSerializable(true);
                rigth=new Point2DSerializable(true);
                getCtrlPoints(prevP,ctrl,next,p,left,rigth);
                jl.moveCtrlPoint(i,rigth);
                jl.insert(p,left,i);

                break;
            }
        }
    }
    public static final Point2DSerializable getCurveCenter(Point2DSerializable P1
                                                           ,Point2DSerializable Ctrl
                                                           ,Point2DSerializable P2){
        double x1 = P1.getX();
        double y1 =P1.getY();
        double ctrlx = Ctrl.getX();
        double ctrly = Ctrl.getY();
        double x2 = P2.getX();
        double y2 = P2.getY();
        double ctrlx1 = (x1 + ctrlx) / 2.0;
        double ctrly1 = (y1 + ctrly) / 2.0;
        double ctrlx2 = (x2 + ctrlx) / 2.0;
        double ctrly2 = (y2 + ctrly) / 2.0;
        ctrlx = (ctrlx1 + ctrlx2) / 2.0;
        ctrly = (ctrly1 + ctrly2) / 2.0;
        return new Point2DSerializable(ctrlx,ctrly,true);
    }
    public static final void getCtrlPoints(Point2DSerializable P1
                                           ,Point2DSerializable Ctrl
                                           ,Point2DSerializable P2
                                           ,Point2DSerializable PCut
                                           ,Point2DSerializable left
                                           ,Point2DSerializable right){
        Point2DSerializable cent=getCurveCenter(P1,Ctrl,P2);

        double d1=PCut.distance(P1);
        double d2=PCut.distance(cent);
        double d3=PCut.distance(P2);
        double d4=cent.distance(P1);
        double d5=cent.distance(P2);
        double d;
        if(d4/d1>d5/d3){
            d=d2/(d1+d2);
        }else{
            d=-d2/(d3+d2);
        }
        getCtrlPoints(P1,Ctrl,P2,d,left,right);
    }
    public static final void getCtrlPoints(Point2DSerializable P1
                                           ,Point2DSerializable Ctrl
                                           ,Point2DSerializable P2
                                           ,double d
                                           ,Point2DSerializable left
                                           ,Point2DSerializable right){
        double p1x=P1.getX(),p1y=P1.getY();
        double p2x=P2.getX(),p2y=P2.getY();
        double ctrlx1=Ctrl.getX()+((p1x-Ctrl.getX())*(d+1))/2;
        double ctrly1=Ctrl.getY()+((p1y-Ctrl.getY())*(d+1))/2;
        double ctrlx2=Ctrl.getX()+((p2x-Ctrl.getX())*(1-d))/2;
        double ctrly2=Ctrl.getY()+((p2y-Ctrl.getY())*(1-d))/2;
        left.setLocation(ctrlx1,ctrly1);
        left.setCoordsAsAbs();
        right.setLocation(ctrlx2,ctrly2);
        right.setCoordsAsAbs();
    }
    public static final void remove(JunctionLink jl,Point2DSerializable p){
//    SelectedObject.setNoBlink();
        PSRender.onHold();
        Point2DSerializable p2Points[]=jl.getP2Points();
        double scale=1.0/PSRender.getScale();
        Ellipse2D ell=new Ellipse2D.Double(p.getX()-5.0*scale//-scale/2.0
                ,p.getY()-5.0*scale//-scale/2.0
                ,10.0*scale,10.0*scale);
        for(int i=0;i<p2Points.length;i++){
            if(ell.contains(p2Points[i].getX(),p2Points[i].getY())){
                if(jl.removePair(i)==false||jl.getArrayListP2().size()==0){
                    jl.remove();
                }
                break;
            }
        }
        PSRender.outHold();
        PSRender.repaint_anywhere();
//    SelectedObject.setBlink();
    }
    public static final void remove(JunctionLink jl,QuadCurve2D quad){
        remove(jl,new Point2DSerializable(quad.getP2().getX(),quad.getP2().getY(),true));
    }
}