package roadmapproject;

//import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Junction implements java.io.Serializable
        ,PreferencedShapeInterface
        ,DBAccess,MultyLanguage,Comparable{

    private static final String JUNCTION_TYPE_NAMES[]={"Large locality"
                                                       ,"Medium locality"
                                                       ,"Small loclity"
                                                       ,"Junction"
                                                       ,"No type setted"};

    private int type;
    private String code;
    private String currName;
    private Point2DSerializable pn;
    private ArrayList paths;
    private boolean visibilityName=false;
    private ArrayList arrDrwShpLst=new ArrayList();
//    private static final Stroke stroke=new BasicStroke(1);
//    private static final Paint colorDraw=Color.RED;
//    private static final Paint colorFill=Color.BLACK;
    private static Ellipse2D ell;
    private static Point2DSerializable tmpP=new Point2DSerializable(true);
    private static PreferencedShape shape;
    private JunctionLabel label;
    protected boolean changed=false;

    public Junction() {
        paths=new ArrayList();
        pn=new Point2DSerializable(0.0,0.0,true);
        code=null;
        type=JunctionLabel.LOW_IMPORTANCE;
        label=new JunctionLabel(this);
        setVisibilityName(false);

        changed=true;
        System.out.println("создан "+this);
    }
    public Junction(String juncName){
        this();
        if(juncName!=null){
            addJunction(juncName,this);
        }else{
            addJunction("Junction",this);
        }

    }
    public void finalize(){
        /*PSRender.removeRenderedObject(this);
        int i;
        for(i=0;i<paths.size();i++){
        ((JunctionLink)paths.get(i)).removeJunction(name);
        }
        Global.allJunctions.remove(name);
        */
        try {
            super.finalize();
            changed=true;
        }
        catch (Throwable e) {
            e.printStackTrace(DBService.getErrorStream());
        }
        System.out.println("finalize Junction:"+code+"-"+this);
    }
    public String getCode(){
        return code;
    }
    public void changeCode(String newName){
        changed=true;
        if(Global.containsJunction(newName)){
            addJunction(newName,this);
            return;
        }
        if(code!=null){
            if(Global.removeJunction(code)==null){
                throw new Error("ошибка, не возможно удалить старое имя code="+code);
            }
        }
        System.out.print(this+" переименован в ");
        code=newName;
        Global.addJunction(this);
        System.out.println(this);
        setCurrName(currName);
        try {
            PSRender.revalidateRenderedObject(this);
        } catch (Exception e) {
            e.printStackTrace();   //To change body of catch statement use Options | File Templates.
            e.printStackTrace(DBService.getErrorStream());
        }
        DBService.revalidateObject(this);
    }
    private void addJunction(String nName,Junction nj){
        int cnt=0;
        String cName=nName+"."+cnt;
        while(Global.containsJunction(cName)==true){
            cnt++;
            cName=nName+"."+cnt;
        }
        nj.changeCode(cName);

    }
    public Point2DSerializable getP1(){
        return pn;
    }
    public Point2DSerializable getP2(){
        return pn;
    }
    public Point2DSerializable getPn(){
        return pn;
    }
    public void addPath(JunctionLink jl){
        // paths.add(jl);
        jl.addJunction(this);
        changed=true;
    }
    public void removePath(JunctionLink jl){
        jl.removeJunction(this);
        changed=true;
        /*
        если Junction не содержит JL значит его нужно удалить
        if(paths.size()==0){
        finalize();
        }
        */
    }
    public final void setpN(Point2DSerializable npn){
        if(npn.isFrameCoords()){
            throw new Error("нельзя ставить фрамовые координаты в "+this+" "+npn);
        }
        pn.setLocation(npn);
        pn.setCoordsAsAbs();
        pointMoved();
    }
    public final ArrayList getPaths(){
        return paths;
    }
    public synchronized final void initializeShapes(){
        changed=true;
        arrDrwShpLst.clear();
        tmpP.setLocation(getPn()).cnvAbsToFrm();
        ell=new Ellipse2D.Double();
        shape=new PreferencedShape(ell
                ,LAYER_POINTS_1_CTRL_FILL);
        arrDrwShpLst.add(shape);
//        shape=new PreferencedShape(ell
//                ,LAYER_POINTS_2_CTRL_OUTLINE);
//        arrDrwShpLst.add(shape);
    }
    public synchronized final ArrayList getShapes(){
        return arrDrwShpLst;
    }
    public synchronized final boolean setShapes(){
        if(isDead()){
            //PSRender.removeRenderedObject(this);
            System.out.println("УМЕР "+this+" loading="+DBService.isLoading());
            remove();
            return false;
        }
        if(PSRender.isRepaintOn()==false&&isChanged()==false){
            return false;
        }
        tmpP.setLocation(pn).cnvAbsToFrm();
        ((Ellipse2D)((PreferencedShape)arrDrwShpLst.get(0)).getShape())
                .setFrame(tmpP.getX()-2.5//-PSRender.scale*1.2/2.0//..переделать под фрейм
                        ,tmpP.getY()-2.5//-PSRender.scale*1.2/2.0
                        ,5.0//PSRender.scale*1.2
                        ,5.0);//PSRender.scale*1.2);

//        ((Ellipse2D)((PreferencedShape)arrDrwShpLst.get(1)).getShape())
//                .setFrame(tmpP.getX()-2.5//-PSRender.scale*1.2/2.0//..переделать под фрейм
//                        ,tmpP.getY()-2.5//-PSRender.scale*1.2/2.0
//                        ,5.0//PSRender.scale*1.2
//                        ,5.0);//PSRender.scale*1.2);
        return true;
    }
    public synchronized void translate(Point2DSerializable p) {
        pn.translate(p);
        changed=true;
    }
    public final void pointMoved(){
        changed=true;
        int i;
        for(i=0;i<paths.size();i++){
            ((JunctionLink)paths.get(i)).calcBounds();
        }
    }
    public final void saveFields(Statement stmt){
        try {
            stmt.executeUpdate("INSERT INTO JUNCTIONS VALUES("+
                    "'"+code.trim()+"',"+
                    pn.getX()+","+pn.getY()+","+type+")");
        }
        catch (SQLException e) {
            e.printStackTrace(DBService.getErrorStream());
        }
    }
    public synchronized final boolean loadFields(Statement stmt,int indObj){
        changed=true;
        ResultSet rs;
        boolean hasNext=false;
        try {
            rs= stmt.executeQuery("SELECT CODE,PNX,PNY,TYPE " +
                    "FROM JUNCTIONS "+
                    "ORDER BY CODE;");

            //rs.first();
            for(int i=0;i<=indObj;i++){
                hasNext=rs.next();
            }
            if(hasNext==false){
                System.out.println("критическая ситуация, обьект индекс="+indObj+" не найден в DB!!!"
                        +" загрузка Junction остановлена");
                return false;
            }
            changeCode(rs.getString("CODE").trim());
            double tmpX=rs.getDouble("PNX");
            double tmpY=rs.getDouble("PNY");
//            pn.setLocation(tmpX,tmpY);
//            pn.setCoordsAsAbs();
            setpN(new Point2DSerializable(tmpX,tmpY,true));
            setType(rs.getInt("TYPE"));
            hasNext=rs.next();
            if(code==null){
                throw new Error(" код не может быть null");
            }
        }catch(SQLException e){
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }
        return hasNext;/////////////////////////////////////////////
    }
    public final String[] getTablesConfiguration(){
        String tmpS[]=new String[1];
        tmpS[0]="(CODE CHAR(64),"+
                "PNX DOUBLE,"+
                "PNY DOUBLE,"+
                "TYPE INT )";
        return tmpS;
    }
    public final String[] getTableNames(){
        String tmpS[]=new String[1];
        tmpS[0]="JUNCTIONS";
        return tmpS;
    }
    public final boolean isDead(){
        return paths.size()==0
                &&(DBService.isLoading()==false);
    }
    public final int getLoadPriority(){
        return LOAD_SAVE_PRIORITY_0;
    }
    public synchronized void remove(){
        System.out.println("\t*REMOVE J "+this);
        if(visibilityName){
            try {
                PSRender.removeRenderedObject(label);
            } catch (Exception e) {
                e.printStackTrace();   //To change body of catch statement use Options | File Templates.
                e.printStackTrace(DBService.getErrorStream());
            }
        }
        for(int i=0;i<paths.size();i++){
            removePath((JunctionLink)paths.get(i));
        }
        try {
            PSRender.removeRenderedObject(this);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            e.printStackTrace(DBService.getErrorStream());
        }
        DBService.removeFromDBService(this);
        Global.removeJunction(code);
        System.out.println("\t~REMOVED J "+this);
        changed=true;
    }
    public CheckReport selfCheck(){
        CheckReport report=new CheckReport(this);
        if(getCurrName().startsWith("NO NAME")){
            report.addError(CheckReport.NAME_NOT_SETTED);
        }
        if(PSRender.isInFrame(pn)==false){
            report.addError(CheckReport.OUTSIDE_OF_FRAME);
        }
        return report;
    }
    public CurveContainer getOutline(){
        return null;
    }
    public String getCurrName(){
        if(currName==null){
            setCurrName(null);
        }
        return currName;
    }
    public void setCode(String code){
        changeCode(code);
    }
    public void setCurrName(String currName){
        this.currName=currName;
        if(this.currName==null
                ||this.currName.length()<1
                ||"NO NAME".equalsIgnoreCase(this.currName)
                ||"null".equalsIgnoreCase(this.currName)){
            this.currName="NO NAME"+(code!=null?" J"+code.substring(8):"");
        }
        changed=true;
    }
    public String toString(){
        String st=super.toString();
        int ind=st.indexOf("@");
        return code+".J"+st.substring(ind)+" type:"+JUNCTION_TYPE_NAMES[type];
    }
    public boolean getVisibilityName(){
        return this.visibilityName;
    }
    public void setVisibilityName(boolean visibility){
        if(this.visibilityName==true){
            if(visibility==false){
                try {
                    PSRender.removeRenderedObject(label);
                } catch (Exception e) {
                    e.printStackTrace();
                    e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
                }
            }else{
                System.out.println("что за хуйня в "+this+" попытка повторно удалить лабел "+label);
            }
        }else if(visibility==true){
            try {
                PSRender.addRenderedObject(label);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }
        this.visibilityName=visibility;

        changed=true;
    }
    public boolean isChanged(){
        if(changed==true){
            label.setJunctionChanged();
            changed=false;
            return true;
        }
        return false;
    }
    public boolean equals(Object obj){
        if(obj instanceof Junction){
            return code==((Junction)obj).code;
        }
        return super.equals(obj);
    }
    public void setType(int type){
        this.type=type;
        if(visibilityName==true){
            try {
                PSRender.revalidateRenderedObject(label);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }
        changed=true;
    }
    public int getType(){
        return type;
    }
    public String[] getTypeNames() {
        return JUNCTION_TYPE_NAMES;
    }
    public boolean contains(Point2DSerializable p){
        int layer=JunctionLabel.LOW_IMPORTANCE-(PSRender.curr_label_visibility-LAYER_LABEL_3_LOW_IMPORTANCE_FILL)/2;
        if(visibilityName&&type<=layer){
            return label.contains(p);
        }
        return false;
    }
    public JunctionLink getLink(Junction secondHalf){
        if(secondHalf==null){
            return null;
        }
        String second_code;
        Junction second_junction;
        for(int i=0;i<paths.size();i++){
            second_junction=((JunctionLink)paths.get(i)).getSecondHalfJunction(this);
            if(second_junction==null){
                continue;
            }
            second_code=second_junction.getCode();
            if(secondHalf.getCode().equalsIgnoreCase(second_code)){
                return (JunctionLink)paths.get(i);
            }
        }
        System.out.println("JL не найден!!!! "+this+" second="+secondHalf);
        return null;
    }

    public int compareTo(Object o) {
        if(o==null){
            return 1;
        }
        if(o instanceof Junction){
            return currName.compareTo(((Junction)o).getCurrName());
        }
        return 0;
    }
}

