package roadmapproject;

import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.Arrays;
import java.lang.reflect.Array;

/**
 * Created by Igor
 * Date: 25.03.2003
 * Time: 19:23:06
 */
public class CurveContainer{
    private double[] points;
    private int index;
    private static final int DEFAULT_SIZE=6;
    private boolean cutted;
    private MultyLanguage ml;
    public CurveContainer(MultyLanguage ml){
        this.ml=ml;
        cutted=false;
        index=0;
    }
    public void moveTo(double x,double y){
        if(points!=null){
            System.out.println(" удаление массива в curvecontainer");
        }
        points=new double[DEFAULT_SIZE];
        points[index++]=x;
        points[index++]=y;
    }
    public void quadTo(double ctrlX,double ctrlY,double p2X,double p2Y){
        double x1;
        double y1;
        double centerX;
        double centerY;
        double x2 ;
        double y2 ;
        x1 = (points[index-2] + ctrlX) / 2.0;
        y1 = (points[index-1] + ctrlY) / 2.0;
        x2 = (p2X + ctrlX) / 2.0;
        y2 = (p2Y + ctrlY) / 2.0;
        centerX = (x1 + x2) / 2.0;
        centerY = (y1 + y2) / 2.0;
        if(index+4>points.length){
            double[] newPoints=new double[index+4+DEFAULT_SIZE];
            System.arraycopy(points,0,newPoints,0,points.length);
            points=newPoints;
        }
        points[index++]=centerX;
        points[index++]=centerY;
        points[index++]=p2X;
        points[index++]=p2Y;

    }
    public synchronized boolean intersects(CurveContainer cc) {
        if(equals(cc)){
            return false;
        }
        Line2D currentLine;
        cut();
        cc.cut();
        Iterator thisIterator=getIterator();
        Iterator otherIterartor;
        while(thisIterator.hasNext()){
            currentLine=(Line2D)thisIterator.next();
            otherIterartor=cc.getIterator();
            while(otherIterartor.hasNext()){
                if(currentLine.intersectsLine((Line2D)otherIterartor.next())){
                    return true;
                }
            }
        }
        return false;
    }
    private void cut(){
        if(cutted==true){
            return;
        }
        cutted=true;
        double x,y;
        x=(points[0]+points[2])/2.0;
        y=(points[1]+points[3])/2.0;
        points[0]=x;
        points[1]=y;
        x=(points[index-2]+points[index-4])/2.0;
        y=(points[index-1]+points[index-3])/2.0;
        points[index-2]=x;
        points[index-1]=y;
    }
    public Iterator getIterator(){
        return new CurveContainerIterator(points,index);
    }
    public void setNoCutted(){
        cutted=true;
    }
    public boolean equals(Object obj){
        if(obj instanceof CurveContainer){
            CurveContainer ccobj=(CurveContainer)obj;
            if(ml.equals(ccobj.ml)){
                if(Arrays.equals(points,ccobj.points)
                        &&index==ccobj.index){
                }else{
                    System.out.println("this ml="+ml+" ml="+ccobj.ml);
                    for(int i=0;i<points.length;i++){
                        System.out.println(i+"-"+points[i]+"<>"+ccobj.points[i]);
                    }
                    System.out.println("index="+index+" i="+ccobj.index);
                    System.out.println("cutted="+cutted+" c="+ccobj.cutted);
                }
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }
}
class CurveContainerIterator implements Iterator{

    private int nextIndex;
    private int size;
    private double[] points;
    public CurveContainerIterator(double[] points,int size){
        this.points=points;
        this.size=size;
        nextIndex=0;
    }
    public boolean hasNext(){
        return nextIndex+4<=size;
    }
    public Object next(){
        nextIndex+=2;
        return new Line2D.Double(points[nextIndex-2]
                ,points[nextIndex-1]
                ,points[nextIndex]
                ,points[nextIndex+1]);

    }
    public void remove(){
    }

}
