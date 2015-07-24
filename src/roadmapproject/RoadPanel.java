package roadmapproject;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Iterator;
import javax.swing.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class RoadPanel extends JLayeredPane implements
        MouseWheelListener,SelectedTypeCONSTANTS,MapApplicationListener{//implements Runnable
    CurveContainer cc;
    JunctionLink jl1;//,jl2,jl3;
    Junction junc1,junc2;
    static Point2DSerializable mousePosition=new Point2DSerializable(true);
    static SelectedObject so;
    private PSRender psr;
    private Object antialiasingValue=RenderingHints.VALUE_ANTIALIAS_ON;
    private DBService dbs;
    static RoadPanel lastRoadPanel;
    //private static JDesktopPane desktop=MapApplication.getDesktop();
    private static Dimension frameSize=new Dimension();
    private Point2DSerializable oldPos=new Point2DSerializable(true);
    private static Rectangle2D clear=new Rectangle2D.Double();
    private static Cursor custom_cursor=new Cursor(Cursor.CROSSHAIR_CURSOR);
    private static final Stroke selectedStroke=new BasicStroke(5.0f);
//    private static final Ellipse2D testCircle=new Ellipse2D.Double();
    boolean left_button_and_contains = false;
    String st="";
    private EditorPopupListener editorPopupListener;
    private UserPopupListener userPopupListener;
    private OtherObjects oo;

    public RoadPanel() {
        lastRoadPanel=this;
        MapApplication.addMAListener(this);
        super.addMouseWheelListener(this);
        super.addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                so.suspendFlash();
                mousePosition.setLocation(e.getX(), e.getY());
                mousePosition.cnvFrmToAbs();

//                MouseContains.contains(mousePosition);
                if(left_button_and_contains&&PSRender.isEditMode()) {
                    oldPos.subNumsFromPoint(new Point2DSerializable(e.getPoint(),true));
                    //oldPos.cnvFrmToAbs();
//                    PSRender.moveFrameOffset(oldPos);
                    Point2DSerializable p=new Point2DSerializable(-oldPos.getX(),-oldPos.getY(),true);
                    p.setCoordsAsAbs();
                    PathData.moveSelectedPoint(p);
                    oldPos.setLocation(e.getX(), e.getY());
                    oldPos.cnvFrmToAbs();

                }else{
                    //oldPos.cnvAbsToFrm();
                    if(PSRender.getMode()==PSRender.EDIT_MODE){
                        setCursor("hand");
                    }
                    oldPos.subNumsFromPoint(new Point2DSerializable(e.getPoint(),true));
                    //oldPos.cnvFrmToAbs();
                    PSRender.moveFrameOffset(oldPos);
                    oldPos.setLocation(e.getX(), e.getY());
                    oldPos.cnvFrmToAbs();
                }
            }
            public void mouseMoved(MouseEvent e){
                so.resumeFlash();
                mousePosition.setLocation(new Point2DSerializable(e.getPoint(),true));
                MouseContains.contains(mousePosition);
                if(Global.selectedObject.isPoint()){
                    Point2DSerializable selLoc=(Point2DSerializable)(Global.selectedObject.getObject());
                    st="x="+selLoc.getX()+"y="+selLoc.getY();
                }else{
                    st="x="+e.getX()+"y="+e.getY();
                }
            }
        });
        super.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                left_button_and_contains = false;
                oldPos.setLocation(e.getX(),e.getY());
                oldPos.cnvFrmToAbs();
                MouseContains.contains(mousePosition);
                if((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0){
                    if((Global.selectedObject.type&SelectedTypeCONSTANTS.MASK_POINT)!=0){
                        left_button_and_contains=true;
                    }
                }
            }
            public void mouseReleased(MouseEvent e){
                so.resumeFlash();

                if(PSRender.getMode()==PSRender.EDIT_MODE){
                    setCursor("cross");
                }
                left_button_and_contains=false;
            }
        });
        editorPopupListener = new EditorPopupListener();
        userPopupListener = new UserPopupListener();
        enableDoubleBuffering(this);


        junc1=new Junction(null);
        jl1=new JunctionLink(junc1);
//        junc1.addPath(jl1);

        jl1.setType(JunctionLinkRender.HIGH_WAY);

        Point2DSerializable startPointsCtrl[]=new Point2DSerializable[6];
        startPointsCtrl[0]=new Point2DSerializable(55,15,true);
        startPointsCtrl[1]=new Point2DSerializable(25,100,true);
        startPointsCtrl[2]=new Point2DSerializable(105,130,true);
        startPointsCtrl[3]=new Point2DSerializable(70,270,true);
        startPointsCtrl[4]=new Point2DSerializable(270,250,true);
        startPointsCtrl[5]=new Point2DSerializable(180,110,true);
        Point2DSerializable startPointsP2[]=new Point2DSerializable[6];
        startPointsP2[0]=new Point2DSerializable(50,60,true);
        startPointsP2[1]=new Point2DSerializable(70,130,true);
        startPointsP2[2]=new Point2DSerializable(90,210,true);
        startPointsP2[3]=new Point2DSerializable(190,270,true);
        startPointsP2[4]=new Point2DSerializable(200,170,true);
        startPointsP2[5]=new Point2DSerializable(330,180,true);

        int i;
        for(i=0;i<6;i++){
            jl1.addPointCtrl2End(startPointsCtrl[i]);
            jl1.addPointP22End(startPointsP2[i]);
        }
//        junc1.addPath(jl1);
        junc1.setpN(new Point2DSerializable(20,20,true));
        junc2 = editorPopupListener.createJunction(jl1,startPointsP2[5],editorPopupListener.P2_POINT);
        cc=jl1.getOutline();
//        cc.cut();
//        cc.cut();
        //        RoadTracer rc=new RoadTracer();
//        rc.setEndJunction(junc1);
//        rc.trace(new RoadContainer(junc2));
//        popupListener.optTrace=rc.getOptimalTrace();
//        PSRender.moveToTop(popupListener.optTrace);

        oo=new OtherObjects(new Point2DSerializable(100,50,true),OtherObjects.POND);
        oo.addPair(new Point2DSerializable(150,100,true),new Point2DSerializable(120,60,true));
        oo.addPair(new Point2DSerializable(140,150,true),new Point2DSerializable(145,140,true));
        oo.addPair(new Point2DSerializable(50,200,true),new Point2DSerializable(80,180,true));
//        oo.setPath_closed(true);


        psr=new PSRender(this);
        dbs=new DBService();

        /* double start=System.currentTimeMillis();
        System.out.println("execute...");
        PSRender.paintOff();
        SelectedObject.setNoBlink();
        //dbs.loadDBData();
        SelectedObject.setBlink();
        PSRender.paintOn();
        System.out.println("successful loaded. time="+(System.currentTimeMillis()-start)+" ms");*/


        so=new SelectedObject(this);
        super.setLayout(null);
        super.setOpaque(false);
        new IFrameSupervisor(this);
        repaint();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d=(Graphics2D)g;
        Stroke old_stroke=g2d.getStroke();
        Paint old_paint=g2d.getPaint();
        double start=System.currentTimeMillis();
        frameSize.setSize(getSize());
        Shape selShape=so.getSelectedShape();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,antialiasingValue);
        g2d.setPaint(Color.white);
        clear.setFrame(0,0,getWidth(),getHeight());
        g2d.fill(clear);
//        cursorGross.
        setCursor(custom_cursor);
//        try{
        try {
            psr.drawPreferencedShapes(g2d);
        } catch(Exception e){
            System.out.println("Exeption in paint");
            e.printStackTrace();
            DBService.getErrorStream().println(e);
        }
        if(selShape!=null){
            g2d.setPaint(so.getSelectedColor());
            if(selShape instanceof Ellipse2D){
                g2d.fill(selShape);
            }else{
                g2d.setStroke(selectedStroke);
                g2d.draw(selShape);
            }
        }

        /*g2d.setPaint(Color.BLACK);
        switch(Global.selectedObject.getType()){
            case P1_POINT:
                st="P1";
                break;
            case CTRL_POINT:
                st="Ctrl";
                break;
            case P2_POINT:
                st="P2";
                break;
            case CURVE:
                st="Curve"+Global.selectedObject.toString().
                        substring(Global.selectedObject.toString().indexOf("@"));
                break;
            case P1_POINT|END_POINT:
                st="P1 and End point";
                break;
            case P2_POINT|END_POINT:
                st="P2 and End point";
                break;
            case P1_POINT|END_POINT|JUNCTION_POINT:
                st="P1 and End point and Junction = "+
                        ((JunctionLink)Global.selectedBR).getFirstJunction();
                break;
            case P2_POINT|END_POINT|JUNCTION_POINT:
                st="P2 and End point and Junction = "+
                        ((JunctionLink)Global.selectedBR).getSecondJunction();
                break;
            default:
                st="нету выбраного";
        }
        if(Global.selectedObject.isPoint()){
            st+=" "+Global.selectedObject.getPoint().toString();
            if(Global.selectedObject.isJunction()==false){
                if(Global.selectedBR instanceof JunctionLink){
                    st+=" "+((JunctionLink)Global.selectedBR).getCurrName();
                }
            }else{
                st+=" "+Global.selectedObject.getJunction().getCurrName();
            }
        }else{
            st+=" "+mousePosition.toString();
        }*/

        if(UserPopupListener.optTrace!=null){
            JunctionLink jncs[];
            Object tmpJ[]=UserPopupListener.optTrace.getTrace().toArray();
            jncs=new JunctionLink[tmpJ.length];
            System.arraycopy(tmpJ,0,jncs,0,tmpJ.length);
            g2d.setPaint(Color.RED);
            g2d.setStroke(new BasicStroke(3.0f));
            GeneralPath trace_path=new GeneralPath();
            for(int i=0;i<jncs.length;i++){
                trace_path.append(jncs[i].getRender().getPath(),false);
            }
            trace_path.transform(Point2DSerializable.at);
            try {
                g2d.draw(trace_path);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
//            g2d.drawString(UserPopupListener.optTrace.getDistance()+"",10,280);
        }
        if(UserPopupListener.startFlag!=null){
            g2d.setPaint(Color.GREEN);
            g2d.fill(UserPopupListener.startFlag.getTransformedRect(Point2DSerializable.at));
            g2d.setPaint(Color.BLUE);
            g2d.draw(UserPopupListener.startFlag.getTransformedOutline(Point2DSerializable.at));
        }
        if(UserPopupListener.endFlag!=null){
            g2d.setPaint(Color.RED);
            g2d.fill(UserPopupListener.endFlag.getTransformedRect(Point2DSerializable.at));
            g2d.setPaint(Color.BLUE);
            g2d.draw(UserPopupListener.endFlag.getTransformedOutline(Point2DSerializable.at));
        }
        FlagShape traceFlag=SelectedObject.getCurrentFlag();
        if(traceFlag!=null){
            g2d.setPaint(Color.YELLOW);
            g2d.fill(traceFlag.getTransformedRect(Point2DSerializable.at));
            g2d.setPaint(Color.PINK);
            g2d.draw(traceFlag.getTransformedOutline(Point2DSerializable.at));
        }
//        st+=" jl="+Global.selectedBR;

//        g2d.setStroke(selectedStroke);
//        cc=jl1.getOutline();
//        if(cc.intersects(oo.getOutline())){
//            g2d.setPaint(Color.RED);
//            g2d.drawString("yes",20,100);
//        }
//        dbs.initOutlines();
//        g2d.drawString(jl1.selfCheck().toString(),150,30);
//        Iterator iterator=cc.getIterator();
//        g2d.setPaint(Color.GREEN);
//        while(iterator.hasNext()){
//            g2d.draw((Line2D)iterator.next());
//        }
//        iterator=oo.getOutline().getIterator();
//        g2d.setPaint(Color.BLUE);
//        while(iterator.hasNext()){
//            g2d.draw((Line2D)iterator.next());
//        }
//        MapApplication.statusBar.setText(st+" render time="+(System.currentTimeMillis()-start));
        g2d.setStroke(old_stroke);
        g2d.setPaint(old_paint);

    }
//    public void itemStateChanged(ItemEvent e) {}
    public void mouseWheelMoved(MouseWheelEvent mwe){
        //PSRender.setCenter();
        double direction=mwe.getWheelRotation();
        if(direction>0){
            PSRender.setScale(PSRender.getScale()*1.1);/*,mousePosition);*/
        }else{
            PSRender.setScale(PSRender.getScale()*0.9);/*,mousePosition);*/
        }
    }
    public static Dimension getFrameSize(){
        return frameSize;
    }
    public static double getPanelWidth(){
        return frameSize.getWidth();
    }
    public static double getPanelHeigth(){
        return frameSize.getHeight();
    }
    public void loadDB(){
        dbs.execute(DBService.DB_LOAD);
    }
    public void saveDB(boolean wait){
        dbs.execute(DBService.DB_SAVE);
        if(wait){
            try {
                dbs.t.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());
            }
        }
    }

    public void setCursor(String img_url){
        if("cross".equalsIgnoreCase(img_url)){
            custom_cursor=new Cursor(Cursor.CROSSHAIR_CURSOR);
            return;
        }
        if("hand".equalsIgnoreCase(img_url)){
            custom_cursor=new Cursor(Cursor.MOVE_CURSOR);
            return;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(img_url);//"images\\img_moved_cursor.gif");
        custom_cursor= toolkit.createCustomCursor(image, new Point(16,16), "custom");
    }
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager =
                RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
    public void mapApplicationActionPerformed(MapApplicationEvent e){
        switch(e.getEventType()){
            case MapApplicationEvent.SET_EDIT_MODE_MAP_MOVE:
                super.removeMouseListener(editorPopupListener);
                setCursor("images\\map_moved_cursor.gif");
                break;
            case MapApplicationEvent.SET_EDIT_MODE_PAINT:
                super.removeMouseListener(editorPopupListener);
                super.addMouseListener(editorPopupListener);
                setCursor("cross");
                break;
            case MapApplicationEvent.SET_EDIT_MODE_PICTURE_MOVE:
                super.removeMouseListener(editorPopupListener);
                setCursor("images\\img_moved_cursor.gif");
                break;
            case MapApplicationEvent.SET_EDIT_MODE:
                super.removeMouseListener(userPopupListener);
                super.removeMouseListener(editorPopupListener);
                super.addMouseListener(editorPopupListener);
                userPopupListener.resetRoad();
                break;
            case MapApplicationEvent.SET_USER_MODE:
                super.removeMouseListener(editorPopupListener);
                super.removeMouseListener(userPopupListener);
                super.addMouseListener(userPopupListener);
                break;
            case MapApplicationEvent.PRINT:
                new PrintFrame(userPopupListener.optTrace);
                break;
        }
    }
//    public void find_no_lenght(){
//        dbs.execute(DBService.DB_FIND_LENGTH_NO_SETTED);
    public Object getAntialiasingValue(){
        return antialiasingValue;
    }
    public void setAntialiasingValue(Object antialiasingValue){
        this.antialiasingValue=antialiasingValue;
        PSRender.repaint_anywhere();
    }
    public void deleteAll(){
        dbs.execute(DBService.DB_DELETE_ALL);
    }
}
