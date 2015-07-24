package roadmapproject;

import java.util.*;
//import java.awt.*;
import java.awt.geom.*;

public class JunctionLabel implements PreferencedShapeInterface {

    public static final int NOT_KNOWN_IMPORTANCE=0;
    public static final int HIGHT_IMPORTANCE    =1;
    public static final int MEDIUM_IMPORTANCE   =2;
    public static final int NORMAL_IMPORTANCE   =3;
    public static final int LOW_IMPORTANCE      =4;

    private PreferencedShape shape_main;
    private PreferencedShape back_shape;
    private JunctionLabelShape label;
    private RoundRectangle2D rect;
    private Junction j;
    private ArrayList shape_array;
    private boolean junction_changed=true;

//    private Color back_color;
    public JunctionLabel(Junction j) {
        this.j = j;
        label = new JunctionLabelShape(j.getCurrName(), j.getPn());
        rect=label.getRoundRectangle();
        shape_array=new ArrayList();
    }

    public synchronized void initializeShapes() {
        int priority;
        shape_array.clear();
        switch(j.getType()){
            case HIGHT_IMPORTANCE:
                priority=LAYER_LABEL_9_HI_IMPORTANCE_FILL;
                break;
            case MEDIUM_IMPORTANCE:
                priority=LAYER_LABEL_7_MEDIUM_IMPORTANCE_FILL;
                break;
            case NORMAL_IMPORTANCE:
                priority=LAYER_LABEL_5_NORMAL_IMPORTANCE_FILL;
                break;
            case LOW_IMPORTANCE:
                priority=LAYER_LABEL_3_LOW_IMPORTANCE_FILL;
                break;
            case NOT_KNOWN_IMPORTANCE:
            default:
                priority=LAYER_LABEL_1_NOT_KNOWN_IMPORTANCE_FILL;
                System.out.println("какая то хуйня с типом в "+j);
        }
        shape_main = new PreferencedShape(label,
//                new BasicStroke(1),
                priority+1);

        shape_array.add(shape_main);


        back_shape=new PreferencedShape(rect,
//                new BasicStroke(1),
                priority);
        shape_array.add(back_shape);
    }

    public synchronized ArrayList getShapes() {
        return shape_array;
    }

    public synchronized boolean setShapes() {
        if(PSRender.isRepaintOn()||junction_changed){
            Point2DSerializable pn=null;
            pn = new Point2DSerializable(j.getPn(),false);
            label.setLabel(j.getCurrName(),pn);
            junction_changed=false;
            return true;
        }
        return false;
    }

    public synchronized void translate(Point2DSerializable p) {
    }

    public void setJunctionChanged(){
        junction_changed=true;
    }
    public String toString(){
        String st=super.toString();
        int ind=st.indexOf("@");
        return "Label "+st.substring(ind)+j.getType()+" "+j;
    }
    public boolean contains(Point2DSerializable p){
        Point2DSerializable pt=new Point2DSerializable(p,false);
        return rect.contains(pt.getX(),pt.getY());
    }
}