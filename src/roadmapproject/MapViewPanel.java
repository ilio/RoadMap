package roadmapproject;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
//import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
//import java.awt.geom.Rectangle2D;

/**
 * Created by Igor
 * Date: Jan 18, 2003
 * Time: 3:12:49 PM
 */
public class MapViewPanel extends JPanel{
    private BufferedImage img;
    private AffineTransform at;
    private double width,heigth;
    private boolean mode;
    private static boolean mode_moved=true;
    private static boolean mode_setted=false;
    private boolean drag_mode;
    private Rectangle2D selected_area;
    private Point first_pressed_point;
    private static MapViewPanel instance;
    private int offset_x=8,offset_y=20;
    public MapViewPanel() {
        super();
        instance=this;
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("View"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        PSRender.add(this);
        first_pressed_point=new Point();
        selected_area=new Rectangle2D.Double();
        at=new AffineTransform();
        resize();
//        recalc();

        super.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent me) {
                Point e=new Point();

                if(mode==mode_setted){
                    e.setLocation(me.getX(),me.getY());
//                    double rect_width,rect_heigth;
//                    rect_width=Math.abs(e.getX()-first_pressed_point.getX());
//                    rect_heigth=Math.abs(e.getY()-first_pressed_point.getY());
                    Point dst=new Point((int)selected_area.getX()-offset_x,(int)selected_area.getY()-offset_y);
                    Point dimensions=new Point((int)selected_area.getWidth(),(int)selected_area.getHeight());
                    try {
                        at.createInverse().transform(dst,dst);
                        at.createInverse().transform(dimensions,dimensions);
                    } catch (NoninvertibleTransformException e1) {
                        e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    }
                    Rectangle2D rect=new Rectangle2D.Double(dst.getX(),dst.getY(),dimensions.getX(),dimensions.getY());
                    PSRender.moveToTop(rect,1.0);
                }else{
                    double x,y;
                    e.setLocation(me.getX()-offset_x,me.getY()-offset_y);
                    x=e.getX();
                    y=e.getY();
                    if(x>0&&x<width&&y>0&&y<heigth){
                        Point dst=new Point();
                        System.out.println("in image");
                        try {
                            at.createInverse().transform(e,dst);
                        } catch (NoninvertibleTransformException e1) {
                            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                        }
//                        Point2DSerializable pdst=new Point2DSerializable(dst,true);
                        PSRender.setFrameOffset(-(dst.getX()-RoadPanel.getPanelWidth()/PSRender.getScale()/2.0),
                                -(dst.getY()-RoadPanel.getPanelHeigth()/PSRender.getScale()/2.0));
//                    PSRender.moveToTop(new Rectangle2D.Double(pdst.getX(),pdst.getY(),1500,1500),0.5);
                        System.out.println("trns: "+dst);
                    }
                }
                drag_mode=false;
                PSRender.drawMainView((Graphics2D)img.getGraphics(),at,true);
                selected_area.setFrame(0,0,0,0);
                instance.repaint();
            }
            public void mousePressed(MouseEvent me) {
                System.out.println("map view mouse pressed "+me.getPoint());
                Point e=new Point();

                if((MouseEvent.BUTTON1_MASK&me.getModifiers())!=0){
                    mode=mode_moved;
                    e.setLocation(me.getX()-offset_x,me.getY()-offset_y);
                    first_pressed_point.setLocation(e);
                }else{
                    mode=mode_setted;
                    e.setLocation(me.getX(),me.getY());
                    first_pressed_point.setLocation(e);
                }
                drag_mode=true;
                instance.repaint();
            }
        });
        super.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent me) {
                Point e=new Point();

                if(mode==mode_setted){
                    e.setLocation(me.getX(),me.getY());
                    double fpx,fpy,w,h;
                    if(first_pressed_point.getX()<e.getX()){
                        fpx=first_pressed_point.getX();
                        w=e.getX()-first_pressed_point.getX();
                    }else{
                        fpx=e.getX();
                        w=first_pressed_point.getX()-e.getX();
                    }
                    if(first_pressed_point.getY()<e.getY()){
                        fpy=first_pressed_point.getY();
                        h=e.getY()-first_pressed_point.getY();
                    }else{
                        fpy=e.getY();
                        h=first_pressed_point.getY()-e.getY();
                    }
                    selected_area.setFrame(fpx,fpy,w,h);
                }else{
                    double x,y;
                    e.setLocation(me.getX()-offset_x,me.getY()-offset_y);
                    x=e.getX();
                    y=e.getY();
                    if(x>0&&x<width&&y>0&&y<heigth){
                        Point dst=new Point();
                        System.out.println("in image");
                        try {
                            at.createInverse().transform(e,dst);
                        } catch (NoninvertibleTransformException e1) {
                            e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                        }
//                        Point2DSerializable pdst=new Point2DSerializable(dst,true);
                        PSRender.setFrameOffset(-(dst.getX()-RoadPanel.getPanelWidth()/PSRender.getScale()/2.0),
                                -(dst.getY()-RoadPanel.getPanelHeigth()/PSRender.getScale()/2.0));
//                    PSRender.moveToTop(new Rectangle2D.Double(pdst.getX(),pdst.getY(),1500,1500),0.5);
                        System.out.println("trns: "+dst);
                    }
                }
                instance.repaint();
            }
        });
        super.setOpaque(false);
    }
    public static void resize(){
        if(instance!=null){
            double maxSize=Math.max(PSRender.getGridHeigth(),PSRender.getGridWidth());
            instance.at.setToScale(200/maxSize,200.0/maxSize);
            instance.img= new BufferedImage((int)(200*PSRender.getGridWidth()/maxSize) //width
                    ,(int)(200*PSRender.getGridHeigth()/maxSize) //heigth
                    ,BufferedImage.TYPE_4BYTE_ABGR);
            instance.width=instance.img.getWidth();
            instance.heigth=instance.img.getHeight();
            instance.setPreferredSize(new Dimension((int)instance.width+16,(int)instance.heigth));
            PSRender.repaint_anywhere();
            instance.repaint();
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d=(Graphics2D)g;
//        PSRender.drawMainView(g2d,at);
        PSRender.drawMainView((Graphics2D)img.getGraphics(),at,drag_mode);
        g2d.drawImage(img,offset_x,offset_y,this);
        if(drag_mode&&mode==mode_setted){
            g2d.setStroke(new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,0.0f,new float[]{1.0f,1.0f},0.0f));
            g2d.setPaint(Color.GRAY);
            g2d.draw(selected_area);
        }
    }
//    public static void recalc(){
//        if(instance!=null){
//            instance.at.setToScale(200/PSRender.getGridWidth(),200.0/PSRender.getGridWidth());
//            instance.img= new BufferedImage(200,(int)(200*PSRender.getGridHeigth()/PSRender.getGridWidth()),BufferedImage.TYPE_4BYTE_ABGR);
//            instance.repaint();
//        }
//    }
}
