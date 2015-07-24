package roadmapproject;

import java.util.*;
import java.awt.geom.Rectangle2D;

public class RoadContainer{
    private ArrayList route;
    private  double distance;
    private Junction firstJunction;
//    public RoadContainer(JunctionLink jl){
//        this();
//        add(jl);
//    }
    public RoadContainer(Junction firstJunction){
        this.firstJunction=firstJunction;
        route=new ArrayList();
        distance=0;
    }
    public static RoadContainer copy(RoadContainer rc){
        RoadContainer rc_1=new RoadContainer(rc.getFirstJunction());
        rc_1.merge(rc);
        return rc_1;
    }
    public void add(JunctionLink jl){
        distance+=jl.getPathLength();
        route.add(jl);
    }
    public void merge(RoadContainer rc){
        distance+=rc.distance;
        route.addAll(rc.route);
    }
    public boolean changeIfMin(RoadContainer rc){
        if(route.size()==0||distance>rc.distance){
            route=rc.route;
            distance=rc.distance;
            firstJunction=rc.firstJunction;
            return true;
        }
        return false;
    }
    public double getDistance(){
        return distance;
    }
    public String toString(){
        String st="distance="+distance+"trace="+route;
        return st;
    }
    public ArrayList getTrace(){
        return route;
    }
    public boolean isLongestFor(RoadContainer rc){
        if(route.size()==0){
            return false;
        }else {
            return distance>rc.distance;
        }
    }
    public int getSize(){
        return route.size();
    }
    public Rectangle2D getBounds(){
        Rectangle2D rect=new Rectangle2D.Double();
        for(int i=0;i<route.size();i++){
//            rect.union();
            Rectangle2D.union(rect,((JunctionLink)route.get(i)).getBound(),rect);
        }
        return rect;
    }
    public boolean contains(Object obj){
        if(obj instanceof JunctionLink){
            return route.contains(obj);
        }
        System.out.println("CONTAINS работает только с JL ");
        return false;
    }
    public Junction getFirstJunction() {
        return firstJunction;
    }
    public Junction get(int index){
        int i=0;
        Junction currentJunction=firstJunction;
        JunctionLink currentJunctionLink=(JunctionLink)route.get(i);
        while(i++!=index){
            currentJunction=currentJunctionLink.getSecondHalfJunction(currentJunction);
            if(i==index){
                break;
            }
            currentJunctionLink=(JunctionLink)route.get(i);
        }
        return currentJunction;
    }
    public int size(){
        return route.size();
    }
}