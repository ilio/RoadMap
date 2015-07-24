package roadmapproject;

import java.util.*;
import java.awt.geom.*;
public class PathData implements java.io.Serializable,SelectedTypeCONSTANTS,BoundRecalcer{
    private ArrayList pointsP2;
    private ArrayList pointsCtrl;
    protected Rectangle2D bound;
    protected boolean changed=false;
    protected boolean pointAddest=false;
    //private boolean alwaysRepaint=false;
    public PathData() {
        this(null);
    }
    public PathData(PathData npd){
        setPath(npd);
    }
    /**
     * возвращает массив точек P2
     * @return массив точек
     */
    protected Point2DSerializable[] getArrayP2(){
        int size=pointsP2.size();
        Point2DSerializable array[]=new Point2DSerializable[size];
        pointsP2.toArray(array);
        return array;
    }
    /**
     * возвращает массив точек Ctrl
     * @return массив точек
     */
    protected Point2DSerializable[] getArrayCtrl(){
        int size=pointsCtrl.size();
        //if(size>0){
        Point2DSerializable array[]=new Point2DSerializable[size];
        pointsCtrl.toArray(array);
        return array;
        /*}else{
        return null;
        }*/
    }

    protected ArrayList getArrayListP2(){
        return pointsP2;
    }
    protected ArrayList getArrayListCtrl(){
        return pointsCtrl;
    }
    /**
     * добавляет точку P2 в "конце" Path
     * @param np точка которая будет добавлена в конец Path
     */
    public synchronized void addPointP22End(Point2DSerializable np){
        if(np.isFrameCoords()){
            throw new Error("addPointP22End: занесены координаты не переведенные из фреймовых!!!"+np);
        }
        np.setCoordsAsAbs();
        pointsP2.add(np);
        calcBounds();
        pointAddest=true;
    }
    /**
     * добавляет точку P2 в "начале" Path
     * @param np точка которая будет добавлена в конец Path
     */
    public synchronized void addPointP22Start(Point2DSerializable np){
        if(np.isFrameCoords()){
            throw new Error("addPointP22Start: занесены координаты не переведенные из фреймовых!!!"+np);
        }
        np.setCoordsAsAbs();
        pointsP2.add(0,np);
        calcBounds();
        pointAddest=true;
    }
    /**
     * добавляет точку Ctrl в "конце" Path
     * @param np точка которая будет добавлена в конец Path
     */
    public synchronized void addPointCtrl2End(Point2DSerializable np){
        if(np.isFrameCoords()){
            throw new Error("addPointCtrl2End: занесены "
                    +"координаты не переведенные из фреймовых!!!"+np);
        }
        np.setCoordsAsAbs();
        pointsCtrl.add(np);
        calcBounds();
        pointAddest=true;
    }
    /**
     * добавляет точку Ctrl в "начале" Path
     * @param np точка которая будет добавлена в конец Path
     */
    public synchronized void addPointCtrl2Start(Point2DSerializable np){
        if(np.isFrameCoords()){
            throw new Error("addPointCtrl2Start: занесены координаты не переведенные из фреймовых!!!"+np);
        }
        np.setCoordsAsAbs();
        pointsCtrl.add(0,np);
        calcBounds();
        pointAddest=true;
    }
    /**
     * изменяет значение точки Ctrl по индексу index на значение точки mp
     * @param op индекс точки
     * @param mp новая точка
     */
    public void movePoint(Point2DSerializable op,Point2DSerializable mp){
        if(op!=null){
            op.translate(mp);
            calcBounds();
        }
    }
    public static void moveSelectedPoint(Point2DSerializable op,Point2DSerializable mp){
        op.translate(mp);
    }
    /**
     * изменяет значение выбранной точки P2 на значение точки mp
     * @param mp новая точка
     */
    public static void moveSelectedPoint(Point2DSerializable mp){
        if(Global.selectedObject.isPoint()){
            moveSelectedPoint(Global.selectedObject.getPoint(),mp);
            Global.selectedBR.calcBounds();
            if((Global.selectedObject.getType()&JUNCTION_POINT)!=0){
                Global.selectedObject.getJunction().pointMoved();
            }
        }
    }
    public void moveCtrlPoint(int index,Point2DSerializable p){
        ((Point2DSerializable)pointsCtrl.get(index)).setLocation(p);
        calcBounds();
    }
    public void moveP2Point(int index ,Point2DSerializable p){
        Point2DSerializable[] pnts=((JunctionLink)this).getP2Points();
        pnts[index].setLocation(p);
        calcBounds();
    }



    public synchronized void setPath(PathData npd){
        if(npd!=null){
            pointsP2=new ArrayList(npd.getArrayListP2());
            pointsCtrl=new ArrayList(npd.getArrayListCtrl());
            bound=new Rectangle2D.Double(npd.getBound().getX()
                    ,npd.getBound().getY()
                    ,npd.getBound().getWidth()
                    ,npd.getBound().getHeight());
        }else{
            pointsP2=new ArrayList();
            pointsCtrl=new ArrayList();
            bound=new Rectangle2D.Double();
        }
        calcBounds();
        pointAddest=true;
    }
    public synchronized Point2DSerializable removeP2(int index){
        if(pointsP2.size()>0){
            Point2DSerializable p;
            if(index==pointsP2.size()){
                if(((JunctionLink)this).removeSecondJunction());
            }
            p=(Point2DSerializable)pointsP2.remove(index);

            calcBounds();
            pointAddest=true;
            return p;
        }else{
            calcBounds();
            pointAddest=true;
            return null;
        }
    }
    public synchronized Point2DSerializable removeLastP2(){
        if(((JunctionLink)this).getSecondJunction()==null){
            if(pointsCtrl.size()-1>=0){
                return removeP2(pointsP2.size()-1);
            }else{
                System.out.println("нет возможности удалить lastP2 в "+this);
                return null;
            }
        }else{
            System.out.println("опасная ситуация, удаление последнего P2"+
                    " когда существует second");
            Junction j=null;
            j=((JunctionLink)this).getSecondJunction();
            Point2DSerializable pnt=j.getPn().newInstance();
            j.setpN(removeP2(pointsP2.size()-1));
            return pnt;
        }
    }
    public synchronized Point2DSerializable removeCtrl(int index){
        Point2DSerializable p=(Point2DSerializable)pointsCtrl.remove(index);
        calcBounds();
        pointAddest=true;
        return p;
    }
    public synchronized Point2DSerializable removeLastCtrl(){
        return removeCtrl(pointsCtrl.size()-1);
    }
    public synchronized final boolean removePair(int index){
        Point2DSerializable ctrl=removeCtrl(index);
        Point2DSerializable p2=removeP2(index);
        return p2!=null&&ctrl!=null;
    }
    public synchronized void addPairToEnd(Point2DSerializable ctrl,Point2DSerializable p2){
        if(ctrl.isFrameCoords()||p2.isFrameCoords()){
            throw new Error("addPairToEnd: занесены координаты не переведенные из фреймовых!!!"+ctrl+p2);
        }
        ctrl.setCoordsAsAbs();
        p2.setCoordsAsAbs();
        pointsCtrl.add(ctrl);
        pointsP2.add(p2);
        calcBounds();
        pointAddest=true;
    }
    public void calcBounds(){
        if(((JunctionLink)this).isDead()){
            return;
        }
        changed=true;
        Point2DSerializable pntsCtrl[]=((JunctionLink)this).getCtrlPoints();
        Point2DSerializable pntsP2[]=((JunctionLink)this).getP2Points();
        if(pntsCtrl==null||pntsP2==null||pntsCtrl.length!=pntsP2.length){
            return;
        }
        int i;
        double minX,minY,maxX,maxY;
        Point2DSerializable points[]=
                JunctionLink.getPathPoints(((JunctionLink)this).getP1()
                        ,pntsCtrl
                        ,pntsP2);
        if(points.length==1){
            return;
        }
        minX=maxX=points[0].getX();
        minY=maxY=points[0].getY();
        for(i=1;i<points.length;i++){
            if(minX>points[i].getX()){
                minX=points[i].getX();
            }else if(maxX<points[i].getX()){
                maxX=points[i].getX();
            }
            if(minY>points[i].getY()){
                minY=points[i].getY();
            }else if(maxY<points[i].getY()){
                maxY=points[i].getY();
            }
        }
        bound.setFrame(minX-5.0//PSRender.scale*0.6
                ,minY-5.0//PSRender.scale*0.6
                ,maxX-minX+10.0//PSRender.scale*1.2
                ,maxY-minY+10.0);//PSRender.scale*1.2);

    }

    public Rectangle2D getBound(){
        return bound;
    }
    public void insert(Point2DSerializable P2,Point2DSerializable Ctrl,int index){
        pointsP2.add(index,P2);
        pointsCtrl.add(index,Ctrl);
        calcBounds();
        pointAddest=true;
    }
    public Point2DSerializable getPPoint(int index){
        if(index==0){
            return ((JunctionLink)this).getP1();
        }
        if(index-1<pointsP2.size()){
            return (Point2DSerializable)pointsP2.get(index-1);
        }
        if(index-1==pointsP2.size()){
            return ((JunctionLink)this).getLastP2();
        }
        return null;
    }
    public Point2DSerializable getCtrlPoint(int index){
        return (Point2DSerializable)pointsCtrl.get(index);
    }
    public boolean isEmpty(){
        if(pointsCtrl.size()==0&&pointsP2.size()==0){
            return true;
        }
        return false;
    }
    public boolean isChanged(){
        if(changed==true){
            changed=false;
            return true;
        }
        return false;
    }
    public boolean hasNewPoint(){
        if(pointAddest==true){
            pointAddest=false;
            return true;
        }
        return false;
    }
    public boolean equals(Object obj){
        if(obj instanceof PathData){
            return pointsCtrl.equals(((PathData)obj).pointsCtrl)&&pointsP2.equals(((PathData)obj).pointsP2);
        }
        return super.equals(obj);
    }
}