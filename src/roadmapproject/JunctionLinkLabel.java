package roadmapproject;

import java.awt.geom.RoundRectangle2D;
//import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Igor
 * Date: Dec 19, 2002
 * Time: 1:05:35 AM
 */
public class JunctionLinkLabel implements PreferencedShapeInterface{
    private PreferencedShape shape_main;
    private PreferencedShape back_shape;
    private JunctionLinkLabelShape label;
    private RoundRectangle2D rect;
    private JunctionLink jl;
    private ArrayList shape_array;
    private boolean junction_changed=true;
//    private Color back_color;
//    private Color fore_color;
    private int point;
    public JunctionLinkLabel(JunctionLink jl,int point) {
        this.jl = jl;
        this.point = point;
        label = new JunctionLinkLabelShape(jl.getCurrName(), jl.getPPoint(point));
        rect=label.getRoundRectangle();
        shape_array=new ArrayList();
    }
    public synchronized void initializeShapes() {
        int priority;
        shape_array.clear();
        switch(jl.getType()){
            case JunctionLinkRender.HIGH_WAY:
//                fore_color=Color.GREEN;
//                back_color=Color.RED;
                priority=LAYER_LABEL_9_HI_IMPORTANCE_FILL;
                break;
            case JunctionLinkRender.MAIN_ROAD:
//                fore_color=Color.BLUE;
//                back_color=Color.YELLOW;
                priority=LAYER_LABEL_7_MEDIUM_IMPORTANCE_FILL;
                break;
            case JunctionLinkRender.BY_WAY:
//                fore_color=Color.YELLOW;
//                back_color=Color.BLUE;
                priority=LAYER_LABEL_5_NORMAL_IMPORTANCE_FILL;
                break;
            case JunctionLinkRender.EARTH_ROAD:
//                fore_color=Color.WHITE;
//                back_color=Color.GRAY;
                priority=LAYER_LABEL_3_LOW_IMPORTANCE_FILL;
                break;
            default:
//                fore_color=Color.RED;
//                back_color=Color.YELLOW;
                priority=LAYER_LABEL_3_LOW_IMPORTANCE_FILL;
                System.out.println("какая то хуйня с типом в "+jl);
        }
        shape_main = new PreferencedShape(label,
//                new BasicStroke(1),
//                fore_color,
                priority+1);//,
//                true,
//                false);

        shape_array.add(shape_main);


        back_shape=new PreferencedShape(rect,
//                new BasicStroke(1),
//                back_color,
                priority);//,
//                true,
//                false);
        shape_array.add(back_shape);
//        return shape_array;
    }

    public synchronized ArrayList getShapes() {
        return shape_array;
    }

    public synchronized boolean setShapes() {
        if(PSRender.isRepaintOn()||junction_changed){
            Point2DSerializable pn=new Point2DSerializable(jl.getPPoint(point),false);
            label.setLabel(jl.getCurrName(),pn);
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
        return "Label "+st.substring(ind)+jl.getType()+" "+jl;
    }
}
