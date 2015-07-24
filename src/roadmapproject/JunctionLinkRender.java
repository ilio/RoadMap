package roadmapproject;

//import java.awt.*;
import java.awt.geom.*;
import java.util.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class JunctionLinkRender implements PreferencedShapeInterface,SelectedTypeCONSTANTS{
// константы
    public static final int HIGH_WAY                                        =1;//магисраль road type
    public static final int MAIN_ROAD                                       =2;//основная дорога
    public static final int BY_WAY                                          =3;//второстепенная дорога
    public static final int EARTH_ROAD                                      =4;//грунтовая дорога
    private JunctionLink jl;
    private final ArrayList arrLst=new ArrayList();
    private final GeneralPath gp=new GeneralPath();
    private static Point2DSerializable tmpP=new Point2DSerializable(true);
    private PreferencedShape shapePathMain/*,shapePathIn*/,shape;
    private int type;
    public JunctionLinkRender(JunctionLink jl) {
        this.jl=jl;
        this.type=jl.getType();
    }
    public synchronized void remove(){
        jl=null;
        //gp.intersects()
        shape=null;
//        shapeBound=null;
//        shapePathIn=null;
        shapePathMain=null;
    }
    private synchronized void setGeneralPath(){
        int i;
        Point2DSerializable pointsP2[]=jl.getP2Points();
        Point2DSerializable pointsCtrl[]=jl.getCtrlPoints();
        gp.reset();
        gp.moveTo((float)jl.getP1().getX()
                ,(float)jl.getP1().getY());
        if(pointsCtrl==null||pointsP2==null){
            System.out.println("неясная ситуация в "+jl+
                    " ctrl="+pointsCtrl+" p2="+pointsP2);
            return;
        }
        int len=pointsCtrl.length;
        if(pointsP2.length!=pointsCtrl.length){
            System.out.println("неравное колличество точек!!! "
                    +"в setJeneralPath jl="+jl
                    +" P2="+pointsP2.length
                    +" Ctrl="+pointsCtrl.length);
            len=pointsP2.length<pointsCtrl.length?pointsP2.length:pointsCtrl.length;
        }
        for(i=0;i<len;i++){
            gp.quadTo((float)pointsCtrl[i].getX()
                    ,(float)pointsCtrl[i].getY()
                    ,(float)pointsP2[i].getX()
                    ,(float)pointsP2[i].getY());
        }
//        gp.transform(Point2DSerializable.at);
    }
    public synchronized void initializeShapes(){
        arrLst.clear();
        type=jl.getType();
        int layer;
        switch(type){
            case HIGH_WAY:
                layer = LAYER_ROAD_7_HIWAY_OUTLINE;
                break;
            case MAIN_ROAD:
                layer=LAYER_ROAD_5_MAIN_OUTLINE;
                break;
            case BY_WAY:
                layer = LAYER_ROAD_3_BY_OUTLINE;
                break;
            default:
                System.out.println("JLR тип не списке!!! "+type);
            case EARTH_ROAD:
                layer = LAYER_ROAD_1_EARTH_OUTLINE;
                break;

        }
        shapePathMain=new PreferencedShape(gp,
                layer);
        arrLst.add(shapePathMain);
        Point2DSerializable pnts[]=jl.getCtrlPoints();
        int i;
        Ellipse2D ell;
        for(i=0;pnts!=null&&i<pnts.length;i++){
            ell=new Ellipse2D.Double();
            shape=new PreferencedShape(ell,
                    LAYER_POINTS_1_CTRL_FILL);
            arrLst.add(shape);
//            shape=new PreferencedShape(ell,
//                    LAYER_POINTS_2_CTRL_OUTLINE);//,
//            arrLst.add(shape);
        }
    }
    public synchronized final boolean setShapes(){
        if(jl==null){
            return false;
        }
        if(jl.isDead()){
            jl.remove();
            jl=null;
            return false;
        }
        /*if(jl.getType()!=this.type){
            try {
                System.out.println("JLR "+this+"изменен тип "+type+" на "+jl.getType());
                PSRender.revalidateRenderedObject(this);
                this.type = jl.getType();
            } catch (Exception e) {
                System.out.println("jl="+jl+" jl type="+jl.getType()+" type="+type);
                e.printStackTrace(); e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }*/
        if(PSRender.isRepaintOn()==false&&jl.isChanged()==false){
            return false;
        }
        boolean has_new_point= jl.hasNewPoint();
        if(has_new_point||jl.getType()!=this.type){
            try {
                System.out.println("JLR revalidate");
                if(has_new_point){
                    System.out.println("JLR "+this+" добавлена точка в "+jl);
                }
                if(jl.getType()!=this.type){
                    System.out.println("JLR "+this+" изменен тип "+type+" на "+jl.getType());
                }
                PSRender.revalidateRenderedObject(this);
                this.type = jl.getType();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }
        setGeneralPath();
//        int layer;
//        switch(type){
//            case HIGH_WAY:
//                layer = DRW_PRIORITY_LAYER_ROAD_7_HIWAY_OUTLINE;
//                break;
//            case MAIN_ROAD:
//                layer=DRW_PRIORITY_LAYER_ROAD_5_MAIN_OUTLINE;
//                break;
//            case BY_WAY:
//                layer = DRW_PRIORITY_LAYER_ROAD_3_BY_OUTLINE;
//                break;
//            default:
//                System.out.println("JLR тип не списке!!! "+type);
//            case EARTH_ROAD:
//                layer = DRW_PRIORITY_LAYER_ROAD_1_EARTH_OUTLINE;
//                break;
//
//        }
//        shapePathMain.setShapePreferences(gp,
//                getStrokeBasicLine(jl.getType()),
////                getColorBasicLine(jl.getType()),
//                layer);//,
////                false,
////                true);
//
//        shapePathIn.setShapePreferences(gp,
//                getStrokeInLine(jl.getType()),
////                getColorInLine(jl.getType()),
//                layer+1);//,
////                false,
////                false);
        /*Rectangle2D bnd=jl.getBound();
        Point2DSerializable str=new Point2DSerializable(bnd.getX(),bnd.getY()).cnvAbsToFrm();
        //Point2DSerializable end=new Point2DSerializable(bnd.getWidth(),bnd.getHeight()).cnvAbsToFrm();
        double scale1=PSRender.scale;
        bound.setFrame(str.getX(),str.getY(),bnd.getWidth()*scale1,bnd.getHeight()*scale1);*/

        Point2DSerializable pnts[]=jl.getCtrlPoints();
        for(int i=1;i<arrLst.size();i++){
            tmpP.setLocation(pnts[i-1]).cnvAbsToFrm();
            ((Ellipse2D)((PreferencedShape)arrLst.get(i)).getShape())
                    .setFrame(tmpP.getX()-3.5//-scale/2.0
                            ,tmpP.getY()-3.5//-scale/2.0
                            ,7.0,7.0);//scale,scale);

//            //tmpP.setLocation(pnts[i-1]).cnvAbsToFrm();
//            ((Ellipse2D)((PreferencedShape)arrLst.get(i+1)).getShape())
//                    .setFrame(tmpP.getX()-5.0//-scale/2.0
//                            ,tmpP.getY()-5.0//-scale/2.0
//                            ,10.0,10.0);//scale,scale);
//            //arrLst.
        }
        return true;
    }
    public synchronized void translate(Point2DSerializable p) {
        ArrayList al=jl.getArrayListCtrl();
        for(int i=0;i<al.size();i++){
            ((Point2DSerializable)al.get(i)).translate(p);
        }
        al=jl.getArrayListP2();
        for(int i=0;i<al.size();i++){
            ((Point2DSerializable)al.get(i)).translate(p);
        }
        jl.calcBounds();
    }
    public synchronized final ArrayList getShapes(){
        return arrLst;
    }
    public static String getRoadName(int roadType){
        switch(roadType){
            case HIGH_WAY:
                return "High way";
            case MAIN_ROAD:
                return "Main road";
            case BY_WAY:
                return "By road";
            case EARTH_ROAD:
                return "Earth road";
            default:
                return "No have prototype";
        }
    }
    public static int getRoadType(String roadName){
        String[] roadNames=JunctionLink.road_type_names;
        for(int i=0,len=roadNames.length;i<len;i++){
            if(roadNames[i].equalsIgnoreCase(roadName)){
                return i+HIGH_WAY;
            }
        }
        return NO_SELECTED_TYPE;
    }
    public String toString(){
        return "jlrender/"+jl.toString();
    }
    public Rectangle2D getBoubds(){
        return jl.getBound();
    }
    public GeneralPath getPath() {
        return gp;
    }
}

