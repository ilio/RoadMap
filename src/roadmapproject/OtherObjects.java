package roadmapproject;

import java.util.ArrayList;
import java.awt.geom.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Igor
 * Date: Jan 1, 2003
 * Time: 11:08:37 PM
 */
public class OtherObjects implements PreferencedShapeInterface,
        DBAccess,ContainsPoint,BoundRecalcer,MultyLanguage{
    private static final int NO_TYPE=0;
    public static final int BORDER=1;
    public static final int CITY=2;
    public static final int POND=3;

//    private static final BasicStroke border_stroke;
    private int insert_point;
    private static QuadCurve2D tstCurve;

    private ArrayList p2_pnts;
    private ArrayList ctrl_pnts;
    protected Rectangle2D bound;
    protected boolean changed=false;
    private boolean pointAddest;
    private boolean path_closed;
    private GeneralPath path;
    private ArrayList shape_list;
    private int code;
    private static int last_code=0;
    private int type;
    private boolean name_visibility;
    private String name;
    private static Point2DSerializable ctrl_cnv_pnt;
    private GeneralPath label_shape;
    private GeneralPath label;

    static{
//        border_stroke=new BasicStroke(3,
//                BasicStroke.CAP_ROUND,
//                BasicStroke.JOIN_ROUND,
//                0.0f,
//                new float[]{10.0f,5.0f,2.0f,5.0f},
//                0.0f);
        ctrl_cnv_pnt=new Point2DSerializable(true);
    }
    protected OtherObjects() {
        super();
        this.p2_pnts=new ArrayList();
        this.ctrl_pnts=new ArrayList();
        this.shape_list=new ArrayList();
        this.bound=new Rectangle2D.Double();
        this.pointAddest=false;
        this.path_closed=false;
        this.path=new GeneralPath();
        this.code=last_code++;
        this.type=NO_TYPE;
        this.label = new GeneralPath();
        this.label_shape = new GeneralPath();
        insert_point=0;
    }
    public OtherObjects(Point2DSerializable p1,int type) {
        this();
        this.type=type;
        addP1(p1);
    }
    private void setPath(){
        path.reset();
        if(p2_pnts.size()<1){
            return;
        }
        Point2DSerializable p=(Point2DSerializable)p2_pnts.get(0);
        Point2DSerializable ctrl;
        path.moveTo((float)p.getX(),(float)p.getY());
        for(int i=0;i<ctrl_pnts.size();i++){
            p=(Point2DSerializable)p2_pnts.get(i+1);
            ctrl=(Point2DSerializable)ctrl_pnts.get(i);
            path.quadTo((float)ctrl.getX(),
                    (float)ctrl.getY(),
                    (float)p.getX(),
                    (float)p.getY());
        }
        if(path_closed){
            path.closePath();
        }
        label_shape.transform(Point2DSerializable.at);
    }
    public void addPair(Point2DSerializable p2,Point2DSerializable ctrl){
        if(ctrl.isFrameCoords()||p2.isFrameCoords()){
            throw new Error("addPairToEnd: занесены координаты не переведенные из фреймовых!!!"+
                    ctrl+
                    p2);
        }
        if(p2_pnts.size()!=ctrl_pnts.size()+1){
            throw new Error("нельз€ заносить пару если p1 не установлен p2 size="+
                    p2_pnts.size()+
                    " ctrl size="+
                    ctrl_pnts.size());
        }
        ctrl.setCoordsAsAbs();
        p2.setCoordsAsAbs();
        this.ctrl_pnts.add(ctrl);
        if(this.type!=BORDER){
            this.p2_pnts.remove(0);
            this.p2_pnts.add(0,p2);
        }
        this.p2_pnts.add(p2);
        calcBounds();
        pointAddest=true;
    }
    public void addPairToStart(Point2DSerializable p2,Point2DSerializable ctrl){
        if(ctrl.isFrameCoords()||p2.isFrameCoords()){
            throw new Error("addPairToEnd: занесены координаты не переведенные из фреймовых!!!"+
                    ctrl+
                    p2);
        }
        if(p2_pnts.size()!=ctrl_pnts.size()+1){
            throw new Error("нельз€ заносить пару если p1 не установлен p2 size="+
                    p2_pnts.size()+
                    " ctrl size="+
                    ctrl_pnts.size());
        }
        ctrl.setCoordsAsAbs();
        p2.setCoordsAsAbs();
        this.ctrl_pnts.add(0,ctrl);
        if(this.type!=BORDER){
            this.p2_pnts.remove(0);
            this.p2_pnts.add(0,p2);
        }
        this.p2_pnts.add(0,p2);
        calcBounds();
        pointAddest=true;
    }
    public void addPair(Point2DSerializable p2,Point2DSerializable ctrl,int index){
        if(ctrl.isFrameCoords()||p2.isFrameCoords()){
            throw new Error("addPairToEnd: занесены координаты не переведенные из фреймовых!!!"+
                    ctrl+
                    p2);
        }
        if(p2_pnts.size()!=ctrl_pnts.size()+1){
            throw new Error("нельз€ заносить пару если p1 не установлен p2 size="+
                    p2_pnts.size()+
                    " ctrl size="+
                    ctrl_pnts.size());
        }
        ctrl.setCoordsAsAbs();
        p2.setCoordsAsAbs();
        this.ctrl_pnts.add(index,ctrl);
        if(this.type!=BORDER&&index+1==p2_pnts.size()){
            this.p2_pnts.remove(0);
            this.p2_pnts.add(0,p2);
        }
        this.p2_pnts.add(index+1,p2);
        calcBounds();
        pointAddest=true;
    }
    public void addP2(Point2DSerializable p2,boolean toStart){
        double dx,dy;
        if(toStart){
            dx=((Point2DSerializable)p2_pnts.get(0)).getX();
            dy=((Point2DSerializable)p2_pnts.get(0)).getY();
        }else{
            dx=((Point2DSerializable)p2_pnts.get(p2_pnts.size()-1)).getX();
            dy=((Point2DSerializable)p2_pnts.get(p2_pnts.size()-1)).getY();
        }
        dx=p2.getX()-(p2.getX()-dx)/2.0;
        dy=p2.getY()-(p2.getY()-dy)/2.0;
        if(toStart){
            addPairToStart(p2,new Point2DSerializable(dx,dy,true));
        }else{
            addPair(p2,new Point2DSerializable(dx,dy,true));
        }
    }
    public void addP2(Point2DSerializable p2){
        double dx,dy;
        dx=((Point2DSerializable)p2_pnts.get(insert_point)).getX();
        dy=((Point2DSerializable)p2_pnts.get(insert_point)).getY();
        dx=p2.getX()-(p2.getX()-dx)/2.0;
        dy=p2.getY()-(p2.getY()-dy)/2.0;
        addPair(p2,new Point2DSerializable(dx,dy,true),insert_point);
    }
    public void addP1(Point2DSerializable p1){
        if(p2_pnts.size()!=0||ctrl_pnts.size()!=0){
            throw new Error("нельз€ добавить точку p1 в непустой экземпл€р "+
                    p2_pnts.size()+
                    " ctrl size="+
                    ctrl_pnts.size());
        }
        if(p1.isFrameCoords()){
            throw new Error("addP1: занесены "
                    +"координаты не переведенные из фреймовых!!!"+p1);
        }
        p1.setCoordsAsAbs();
        p2_pnts.add(p1);
        calcBounds();
        pointAddest=true;
        try {
            PSRender.addRenderedObject(this);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        DBService.addToDBService(this);
        MouseContains.add(this);
    }
    public void calcBounds(){
        if(isDead()){
            return;
        }
        changed=true;
        double minX;
        double minY;
        double maxX;
        double maxY;
        Point2DSerializable[] points;
        points = new Point2DSerializable[p2_pnts.size()+ctrl_pnts.size()];
        p2_pnts.toArray(points);
        System.arraycopy(ctrl_pnts.toArray(),0,points,p2_pnts.size(),ctrl_pnts.size());
        minX=maxX=points[0].getX();
        minY=maxY=points[0].getY();
        for(int i=1;i<points.length;i++){
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
        setLabel();

    }
    public String[] getTablesConfiguration() {
        return new String[]{
            "(CODE INT, " +
                "TYPE INT) ",
            "(OOCODE INT,"+
                "CTRLX DOUBLE,"+
                "CTRLY DOUBLE,"+
                "P2X DOUBLE,"+
                "P2Y DOUBLE,"+
                "PNTNUM INT)"};
    }
    public String[] getTableNames() {
        return new String[]{"OTHER_OBJ",
                            "OTHER_PNTS"};
    }
    public boolean loadFields(Statement stmt, int indObj) {
        changed=true;
        ResultSet rs;
        boolean hasNext=false;
        try {
            rs= stmt.executeQuery("SELECT CODE,TYPE " +
                    "FROM OTHER_OBJ "+
                    "ORDER BY CODE;");
            /*
            rs.first();
            if(rs.relative(indObj)==false){
            throw new Error("loadFields: строка не существует");
            }*/
            for(int i=0;i<=indObj;i++){
                hasNext=rs.next();
            }
            if(hasNext==false){
                System.out.println("критическа€ ситуаци€, обьект индекс="+indObj+" не найден в DB!!!"
                        +" загрузка OO остановлена"+" code:"+code);
                return false;
            }
            code=rs.getInt("CODE");
            if(code>last_code){
                last_code=code+1;
            }
            setType(rs.getInt("TYPE"));
            hasNext=rs.next();
            rs= stmt.executeQuery("SELECT CTRLX,CTRLY,P2X,P2Y"+
                    " FROM OTHER_PNTS,OTHER_OBJ"+
                    " WHERE CODE=OOCODE"+
                    " AND CODE ="+code+
                    " ORDER BY PNTNUM");
            if(rs.next()==false){
                System.out.println("невозможно загрузить точки code:"+code);
            }else{
                Point2DSerializable p2,ctrl;
                p2=new Point2DSerializable(rs.getDouble("P2X"),rs.getDouble("P2Y"),true);
                p2.setCoordsAsAbs();
                addP1(p2);
                ctrl=new Point2DSerializable(rs.getDouble("CTRLX"),rs.getDouble("CTRLY"),true);
                ctrl.setCoordsAsAbs();
//                rs.next();
                while(rs.next()){
                    p2=new Point2DSerializable(rs.getDouble("P2X"),rs.getDouble("P2Y"),true);
                    p2.setCoordsAsAbs();
                    addPair(p2,ctrl);
                    ctrl=new Point2DSerializable(rs.getDouble("CTRLX"),rs.getDouble("CTRLY"),true);
                    ctrl.setCoordsAsAbs();
                }
//                ctrl_pnts.remove(ctrl_pnts.size()-1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }

        return hasNext;
        //return false;
    }
    public void saveFields(Statement stmt) {
        try {
            ResultSet rs;
            while(true){
                rs=stmt.executeQuery("SELECT CODE FROM OTHER_OBJ WHERE CODE ="+code+";");
                if(rs.next()==false){
                    break;
                }
                last_code++;
                code=last_code;
            };
            stmt.executeUpdate("INSERT INTO OTHER_OBJ VALUES("+
                    code+","+type+")");
            for(int i=0;i<ctrl_pnts.size();i++){
                stmt.executeUpdate("INSERT INTO OTHER_PNTS VALUES("+
                        code+","+((Point2DSerializable)ctrl_pnts.get(i)).getX()+","+
                        ((Point2DSerializable)ctrl_pnts.get(i)).getY()+","+
                        ((Point2DSerializable)p2_pnts.get(i)).getX()+","+
                        ((Point2DSerializable)p2_pnts.get(i)).getY()+","+i+")");
            }
            stmt.executeUpdate("INSERT INTO OTHER_PNTS VALUES("+
                    code+","+
                    -1.0+","+
                    -1.0+","+
                    ((Point2DSerializable)p2_pnts.get(ctrl_pnts.size())).getX()+","+
                    ((Point2DSerializable)p2_pnts.get(ctrl_pnts.size())).getY()+","+ctrl_pnts.size()+")");
        }catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }
    }
    public boolean isDead(){
        return p2_pnts.size() == 0
                &&(DBService.isLoading()==false);
    }
    public int getLoadPriority() {
        return LOAD_SAVE_PRIORITY_NO_IMPORTANT;
    }
    public synchronized void remove() {
        DBService.removeFromDBService(this);
        MouseContains.remove(this);
        try {
            PSRender.removeRenderedObject(this);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            e.printStackTrace(DBService.getErrorStream());
        }
        System.out.println("удален "+this);
    }
    public CheckReport selfCheck(){
        CheckReport report=new CheckReport(this);
        if(getCurrName().startsWith("NO NAME")){
            report.addError(CheckReport.NAME_NOT_SETTED);
        }
        for(int i=0;i<p2_pnts.size();i++){
            if(PSRender.isInFrame((Point2DSerializable)p2_pnts.get(i))==false){
                report.addError(CheckReport.OUTSIDE_OF_FRAME);
                break;
            }
        }
        ArrayList outlines=DBService.getOutlines();
        CurveContainer thisOutline=getOutline();
        CurveContainer otherOutline;
        if(thisOutline!=null){
            for(int i=0;i<outlines.size();i++){
                otherOutline=(CurveContainer)outlines.get(i);
                if(thisOutline.intersects(otherOutline)){
                    report.addError(CheckReport.INTERSECT_WITH_OTHER_OBJECT);
                    break;
                }
            }
        }
        return report;
    }

    public CurveContainer getOutline(){
        if(type!=POND){
            return null;
        }
        CurveContainer cc=new CurveContainer(this);
        cc.setNoCutted();
        Point2DSerializable p2s=(Point2DSerializable)p2_pnts.get(0);
        Point2DSerializable ctrl_p2s;
        cc.moveTo(p2s.getX(),p2s.getY());
        for(int i=1;i<p2_pnts.size();i++){
            p2s=(Point2DSerializable)p2_pnts.get(i);
            ctrl_p2s=(Point2DSerializable)ctrl_pnts.get(i-1);
            cc.quadTo(ctrl_p2s.getX(),ctrl_p2s.getY(),p2s.getX(),p2s.getY());
        }
        return cc;
    }
    private boolean isPointAdded(){
        if(pointAddest==true){
            pointAddest=false;
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
    public synchronized void initializeShapes() {
        shape_list.clear();
        PreferencedShape ps;
        int priority;
        switch(type){
            case BORDER:
                setPath_closed(false);
                priority=LAYER_OTHER_OBJECTS_1_BORDER_FILL;
                break;
            case CITY:
                setPath_closed(true);
                priority=LAYER_OTHER_OBJECTS_3_CITY_FILL;
                break;
            case POND:
                setPath_closed(true);
                priority=LAYER_OTHER_OBJECTS_5_POND_FILL;
                break;
            default:
                throw new Error("тип не корректен "+this+" "+type);
        }
        ps=new PreferencedShape(path,priority);
        shape_list.add(ps);
        if(type!=BORDER&&name_visibility==true){
            ps=new PreferencedShape(label,LAYER_OTHER_OBJECTS_7_LABEL);
            shape_list.add(ps);
        }
        int i;
        Ellipse2D ell;
        for(i=0;i<ctrl_pnts.size();i++){
            ell=new Ellipse2D.Double();
            ps=new PreferencedShape(ell,
                    LAYER_POINTS_1_CTRL_FILL);//,
            shape_list.add(ps);
        }
    }
    public synchronized ArrayList getShapes() {
        return shape_list;
    }
    public synchronized boolean setShapes() {
        if(isDead()){
            try {
                PSRender.removeRenderedObject(this);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                e.printStackTrace(DBService.getErrorStream());
            }
            return false;
        }
        if(PSRender.isRepaintOn()==false&&isChanged()==false){
            return false;
        }
        if(isPointAdded()){
            try {
                System.out.println("OO revalidate");
                System.out.println("OO "+this+" добавлена точка");
                PSRender.revalidateRenderedObject(this);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }
        setPath();
        if(name_visibility==true&&type!=BORDER){
                setLabel();
                label.reset();
                label.append(label_shape,false);
                label.transform(Point2DSerializable.at);
        }
        int correction=((type==BORDER||name_visibility==false)?1:0);
        for(int i=2-correction;i<shape_list.size();i++){
            ctrl_cnv_pnt.setLocation((Point2DSerializable)
                    ctrl_pnts.get(i-2+correction)).cnvAbsToFrm();
            ((Ellipse2D)((PreferencedShape)shape_list.get(i)).getShape())
                    .setFrame(ctrl_cnv_pnt.getX()-5.0//-scale/2.0
                            ,ctrl_cnv_pnt.getY()-5.0//-scale/2.0
                            ,10.0,10.0);//scale,scale);
        }
        return true;
    }
    public void translate(Point2DSerializable p) {
        for(int i=(type==BORDER?0:1);i<p2_pnts.size();i++){
            ((Point2DSerializable)p2_pnts.get(i)).translate(p);
        }
        for(int i=0;i<ctrl_pnts.size();i++){
            ((Point2DSerializable)ctrl_pnts.get(i)).translate(p);
        }
        calcBounds();
    }
    private void setPath_closed(boolean path_closed) {
        if(this.path_closed!=path_closed){
            this.path_closed = path_closed;
            pointAddest=true;
            calcBounds();
        }
    }
    public String toString(){
        String st=super.toString();
        int ind=st.indexOf("@");
        return code+".OO"+st.substring(ind);
    }
    public boolean contains(Point2DSerializable p) {
        if(p.isFrameCoords()==true){
            throw new Error("координата не абсолютна"+" code:"+code);
        }
        if(isDead()){
            remove();
        }
//        insert_point = -1;
        if(bound.contains(p.getX(),p.getY())==false){
            Global.selectedObject.setObject(null,NO_SELECTED_TYPE);
            Global.setSelectedBR(null);
            //Global.selectedType=Global.NO_SELECTED_TYPE;
            return false;
        }
        double scale=1.0/PSRender.getScale();
        JunctionLink.testCircle.setFrame(p.getX()-5.0*scale//-scale/2.0
                ,p.getY()-5.0*scale//-scale/2.0
                ,10.0*scale,10.0*scale);
        Point2DSerializable p_t=(Point2DSerializable)p2_pnts.get(0);
        if(JunctionLink.testCircle.contains(p_t.getX(),p_t.getY())){
            Global.setSelectedBR(this);
            Global.selectedObject.setObject(p_t,P1_POINT|END_POINT|OTHER_OBJECT);
            return true;
        }
        for(int i=1;i<p2_pnts.size();i++){
            p_t=(Point2DSerializable)p2_pnts.get(i);
            if(JunctionLink.testCircle.contains(p_t.getX(),p_t.getY())){
                Global.setSelectedBR(this);
                if(i==p2_pnts.size()-1){
                    Global.selectedObject.setObject(p_t,P2_POINT|END_POINT|OTHER_OBJECT);
                }else{
                    Global.selectedObject.setObject(p_t,P2_POINT|OTHER_OBJECT);
                }
                return true;
            }

        }
        for(int i=0;i<ctrl_pnts.size();i++){
            p_t=(Point2DSerializable)ctrl_pnts.get(i);
            if(JunctionLink.testCircle.contains(p_t.getX(),p_t.getY())){
                Global.setSelectedBR(this);
                Global.selectedObject.setObject(p_t,CTRL_POINT|OTHER_OBJECT);
                return true;
            }

        }
        Point2DSerializable[] points;
        points = new Point2DSerializable[p2_pnts.size()+ctrl_pnts.size()];
        p2_pnts.toArray(points);
        System.arraycopy(ctrl_pnts.toArray(),0,points,p2_pnts.size(),ctrl_pnts.size());
        for(int i=0;(tstCurve=JunctionLink.getCurveOnIndex(points,i))!=null;i++){
            if(CurveContains.isOnLine(tstCurve,JunctionLink.testCircle)){
                Global.setSelectedBR(this);
                Global.selectedObject.setObject(tstCurve,CURVE|OTHER_OBJECT);
                insert_point = i;
                //Global.selectedType=Global.CURVE;
                return true;
            }
        }
        if(type!=BORDER){
            Point2DSerializable t_p=new Point2DSerializable(p);
            if(path.contains(t_p.getX(),t_p.getY())){
                Global.setSelectedBR(this);
                Global.selectedObject.setObject(p,IN_OBJECT|OTHER_OBJECT);
                return true;
            }
        }
//        JunctionLink.getCurveOnIndex(points,i);
        Global.setSelectedBR(null);
        Global.selectedObject.setObject(null,NO_SELECTED_TYPE);
        return false;
    }
    public static final Point2DSerializable[] getPathPoints(Point2DSerializable sCtrls[]
                                                            ,Point2DSerializable sP2s[]){
        Point2DSerializable points[];
        if(sCtrls==null||sP2s==null){
            System.out.println("JunctionLink.getPathPoints: отсутствуют точки P2 и Ctrl");
            return null;
        }
        points=new Point2DSerializable[sCtrls.length+sP2s.length];
//        int i;
//        points[0]=sP1;
        System.arraycopy(sP2s,0,points,0,sP2s.length);
        System.arraycopy(sCtrls,0,points,sP2s.length,sCtrls.length);
        /*
        for(i=1;i<points.length;i+=2){
        points[i]=sCtrls[i/2];
        points[i+1]=sP2s[i/2];
        }*/
        return points;
    }
    public String getCode() {
        String st;
        switch(type){
            case BORDER:
                st="BORDER";
                break;
            case CITY:
                st="SITY";
                break;
            case POND:
                st="POND";
                break;
            case NO_TYPE:
            default:
                st="NO TYPE";
        }
        return "OO "+st+"."+code;
    }
    public String getCurrName() {
        if(name==null){
            setCurrName(null);
        }
        return name;
    }
    public void setCode(String code) {
        this.code=Integer.parseInt(code);
    }
    public void setCurrName(String currName) {
        this.name=currName;
        if(this.name==null
                ||this.name.length()<1
                ||"NO NAME".equalsIgnoreCase(this.name)
                ||"null".equalsIgnoreCase(this.name)){
            this.name="NO NAME OO"+code;
        }
        setLabel();
        changed=true;
    }
    public boolean getVisibilityName() {
        return name_visibility;
    }
    public void setVisibilityName(boolean visibility) {
        if(this.name_visibility!=visibility){
            this.name_visibility=visibility;
            changed=true;
            try {
                PSRender.revalidateRenderedObject(this);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }
    }
    public void setType(int type){
        if(this.type!=type){
            this.type = type;
            pointAddest=true;
        }
    }
    public Point2DSerializable getFP(){
        if(insert_point==-1){
            throw new Error("точка не выбрана "+this);
        }
        return (Point2DSerializable)p2_pnts.get(insert_point%p2_pnts.size());
    }
    public Point2DSerializable getSP(){
        if(insert_point==-1){
            throw new Error("точка не выбрана "+this);
        }
        return (Point2DSerializable)p2_pnts.get((insert_point+1)%p2_pnts.size());
    }
    public int getType() {
        return type;
    }
    public String[] getTypeNames() {
        String st;
        switch(type){
            case BORDER:
                st="BORDER";
                break;
            case CITY:
                st="SITY";
                break;
            case POND:
                st="POND";
                break;
            case NO_TYPE:
            default:
                st="NO TYPE";
        }
        return new String[]{st};
    }
    public void delete_point(Point2DSerializable p){
        int i;
        for(i=0;i<p2_pnts.size();i++){
            if(p2_pnts.get(i).equals(p)){
                if(i>0){
                    ctrl_pnts.remove(i-1);
                    p2_pnts.remove(i);
                }else{
                    p2_pnts.remove(0);
                    p2_pnts.remove(p2_pnts.size()-1);
                    ctrl_pnts.remove(ctrl_pnts.size()-1);
                    if(p2_pnts.size()>0){
                        p2_pnts.add(0,p2_pnts.get(p2_pnts.size()-1));
                    }
                }
                pointAddest=true;
                calcBounds();
                break;
            }
        }
        if(i==p2_pnts.size()){
            System.out.println("точка не найдена "+this+" "+p+" \n p2: "+p2_pnts);
        }
        PSRender.repaint();
    }
    private void setLabel(){
          if(type!=BORDER&&name!=null){

            Rectangle2D rect=new Rectangle2D.Double();
            double minX;
            double minY;
            double maxX;
            double maxY;
            Point2DSerializable[] points;
            points = new Point2DSerializable[p2_pnts.size()];
            p2_pnts.toArray(points);
            minX=maxX=points[1].getX();
            minY=maxY=points[1].getY();
            for(int i=2;i<points.length;i++){
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
              rect.setFrame(minX-5.0//PSRender.scale*0.6
                      ,minY-5.0//PSRender.scale*0.6
                      ,maxX-minX+10.0//PSRender.scale*1.2
                      ,maxY-minY+10.0);//PSRender.scale*1.2);
              AffineTransform text_at = new AffineTransform();
              TextLayout textLayout = new TextLayout(name,
                      new Font("system", Font.PLAIN,12),
                      new FontRenderContext(null, false, false));
              double object_heigth = rect.getHeight();
              double object_width = rect.getWidth();
              double s;
              label_shape.reset();
              label_shape.append(textLayout.getOutline(text_at), false);
              Rectangle2D r=label_shape.getBounds2D();
              s=object_width*0.3/r.getWidth();
              text_at.setTransform(s,0.0,0.0,s,rect.getX()+(object_width-r.getWidth()*s)*0.5,
                      rect.getY()+(object_heigth)*0.5);
              label_shape.transform(text_at);
        }
    }
}
