package roadmapproject;

//import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
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

public class JunctionLink extends PathData implements java.io.Serializable
        ,DBAccess,MultyLanguage,ContainsPoint{
    // PathData path;
    private int type;
    protected Junction firstJunction,secondJunction;
    private String code=null;
    private String currName=null;
    private boolean visibilityName=true;
    private double pathLength;
    private static int numCode=0;
    private static int numName=0;
    private static QuadCurve2D tstCurve;
    static Ellipse2D testCircle=new Ellipse2D.Double();
    private JunctionLinkRender jlr;
    public static String[] road_type_names={"High way","Main road","By road","Earth road","No type"};
    private static final String DEFAULT_CODE="JunctionLink.";
    /**
     * если создаешь обьект без имени его нужно добавить в Junction
     */
    public JunctionLink() {
        firstJunction=null;
        secondJunction=null;
//        this(null);
        //new RoadPathRender(this);
    }
    /**
     * если создаешь обьект с именем он добавляется в Junction с этим именем
     */
    public JunctionLink(Junction junction){
        firstJunction=null;
        secondJunction=null;
        if(junction!=null&&Global.containsJunction(junction.getCode())){
            addJunction(junction);
        }else{
            System.out.println("Junction "+junction+" отсутствует в списке"+
                    ", нет возможности его добавить"+
                    " first="+firstJunction+" second="+secondJunction);
        }
        System.out.println("создан "+this);
    }
    public void finalize(){
        try {
            changed=true;
            super.finalize();
        }
        catch (Throwable ex) {
        }
        System.out.println("finalize JunctionLink:"+this+" code:"+code);
    }
    public synchronized void addJunction(Junction junction){
        System.out.println("ADD-----------------------------------------------------------------------");
        changed=true;
        if(junction==null){
            return;
        }
        if(firstJunction==null&&secondJunction==null){
            if(code==null){
                code=DEFAULT_CODE+(numName++);
                setCurrName(currName);
            }
            jlr=new JunctionLinkRender(this);
            try {
                PSRender.addRenderedObject(jlr);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());   //To change body of catch statement use Options | File Templates.
            }
            DBService.addToDBService(this);

        }
        if(firstJunction==null){
            firstJunction=junction;
        }else if(secondJunction==null){
            super.removeLastP2();
            secondJunction=junction;
        }else{
            throw new Error(this+" code:"+code+", нет возможности добавить 3й Junction"+
                    ",first="+firstJunction+",second="+secondJunction+"добавить="+
                    junction);
        }
        try{
            if(Global.getJunction(junction.getCode()).getPaths().add(this)==true){
                System.out.println("в "+junction+" был удачно"+
                        " добавлен "+this+", fst="+firstJunction+" sec="+secondJunction);
            }else{
                throw new Error("в "+junction+" НЕ "+
                        " ДОБАВЛЕН "+this+", fst="+firstJunction+" sec="+secondJunction);
            }
        }catch(Error error){
            error.printStackTrace();  //To change body of catch statement use Options | File Templates.
            error.printStackTrace(DBService.getErrorStream());
        }
        ArrayList al=null;
        try{
            al=Global.getJunction(junction.getCode()).getPaths();
        } catch(Exception e){
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        for(int i=0,len=al.size();i<len;i++){
            System.out.println("\tэлемент["+i+"] = "+al.get(i));
        }

        super.calcBounds();
        System.out.println("--------------------------------------------------------------------------");
    }
    public synchronized boolean removeJunction(Junction junction){
        System.out.println("DEL JL-----------------------------------------------------------------------");
        System.out.println("\tудаляется из "+this+" f="+firstJunction+" s="+secondJunction+" jcode "+junction);
        changed=true;
        if(junction!=null){
            if(junction.equals(secondJunction)){
                super.addPointP22End(secondJunction.getPn());
                secondJunction=null;
            }else if(junction.equals(firstJunction)){
                super.addPointP22Start(firstJunction.getPn());
                firstJunction=null;
                if(secondJunction!=null){
                    System.out.println("опасная ситуация в " +this+" code:"+code+
                            " first="+firstJunction+
                            " second="+secondJunction);
                }
            }else{
                throw new Error(" Junction "+ junction+
                        "не связан с JunctionLink "+this+" code:"+code);
            }
        }else{
            return false;
        }
        if(junction.getPaths().remove(this)==false){
            throw new Error(this+" не был удален из Junction "+junction);
        }else{
            System.out.println(junction+" был удачно"+
                    " удален из JunctionLink"+", fst="+firstJunction+" sec="+secondJunction);
        }
        ArrayList al=junction.getPaths();
        for(int i=0,len=al.size();i<len;i++){
            System.out.println("\tэлемент["+i+"] = "+al.get(i));
        }
        System.out.println("JL--------------------------------------------------------------------------");
        return true;
    }
    public Point2DSerializable[] getP2Points(){
        Point2DSerializable retPnts[];
        int fi;
        if(firstJunction==null){
            fi=1;
        }else{
            fi=0;
        }
        Point2DSerializable pathTmp[]=super.getArrayP2();
        int pathLength=pathTmp.length;
        if(secondJunction==null){
            if(pathLength!=0){
                retPnts=new Point2DSerializable[pathLength-fi];
                System.arraycopy(pathTmp,fi,retPnts,0,pathLength-fi);
            }else{
                retPnts=new Point2DSerializable[0];
            }
        }else{
            if(pathTmp!=null){
                retPnts=new Point2DSerializable[pathTmp.length+1-fi];
                System.arraycopy(pathTmp,fi,retPnts,0,pathTmp.length-fi);
                retPnts[retPnts.length-1]=secondJunction.getP2();
            }else{
                retPnts=new Point2DSerializable[1];
                retPnts[0]=secondJunction.getP2();
            }
        }
        return retPnts;
    }
    public Point2DSerializable getP1(){
        if(firstJunction==null){
            //throw new Error(" точка P1 не существует ");
            Point2DSerializable[] p2s=getP2Points();
            if(p2s.length>0)
                return getP2Points()[0];//super.getArrayP2()[0];
        }else{
            return firstJunction.getP1();

        }
        return null;
    }
    public Point2DSerializable getLastP2(){
        if(secondJunction==null){
            ArrayList al=getArrayListP2();
            return (Point2DSerializable)al.get(al.size()-1);
        }else{
            return secondJunction.getPn();
        }
    }
    public Point2DSerializable[] getCtrlPoints(){
        return super.getArrayCtrl();
    }
    Point2DSerializable[] addPointArray(Point2DSerializable fst[],Point2DSerializable scd[]){
        Point2DSerializable arrTmp[]=new Point2DSerializable[fst.length+scd.length];
        System.arraycopy(fst,0,arrTmp,0,fst.length);
        System.arraycopy(scd,0,arrTmp,fst.length,scd.length);
        return arrTmp;
    }
    public final boolean contains(Point2DSerializable cp){
        if(cp.isFrameCoords()==true){
            throw new Error("координата не абсолютна"+" code:"+code);
        }
        Object selectedObj;
        int point_type;
        if(firstJunction!=null){
            if(firstJunction.contains(cp)){
                point_type=P1_POINT
                        |JUNCTION_POINT
                        |END_POINT;
                Global.selectedObject.setJunction(firstJunction);
                Global.selectedObject.setObject(firstJunction,point_type);
                Global.setSelectedBR(this);
                return true;
            }
        }
        if(secondJunction!=null){
            if(secondJunction.contains(cp)){
                point_type=P2_POINT
                        |JUNCTION_POINT
                        |END_POINT;
                Global.selectedObject.setJunction(secondJunction);
                selectedObj=secondJunction;
                Global.selectedObject.setObject(selectedObj,point_type);
                Global.setSelectedBR(this);
                return true;
            }
        }
        if(bound.contains(cp.getX(),cp.getY())==false){
            Global.selectedObject.setObject(null,NO_SELECTED_TYPE);
            Global.setSelectedBR(null);
            //Global.selectedType=Global.NO_SELECTED_TYPE;
            return false;
        }
        double scale=1.0/PSRender.getScale();
        Point2DSerializable points[]=getPathPoints(getP1()
                ,getCtrlPoints()
                ,getP2Points());
        testCircle.setFrame(cp.getX()-5.0*scale//-scale/2.0
                ,cp.getY()-5.0*scale//-scale/2.0
                ,10.0*scale,10.0*scale);

        for(int i=0,len=points.length;i<(PSRender.isEditMode()?len:len/2+1);i++){
            if(testCircle.contains(points[i].getPoint2D())){
                Global.setSelectedBR(this);


                selectedObj = points[i];
                if(i==0){
                    //Global.selectedType=Global.P1_POINT;
                    point_type=P1_POINT;
                    if(firstJunction!=null){
                        /*Global.selectedType=Global.P1_POINT
                        |Global.JUNCTION_POINT
                        |Global.END_POINT;*/
                        point_type=P1_POINT
                                |JUNCTION_POINT
                                |END_POINT;
                        Global.selectedObject.setJunction(firstJunction);
                        selectedObj=firstJunction;
                    }else{
                        //Global.selectedType=Global.P1_POINT|Global.END_POINT;
                        point_type=P1_POINT|END_POINT;
                        System.out.println("критическая ситуация в "+ this+" P1 не может быть не Junction");
                    }
                }else if(i>len/2){ // Ctrl
                    //Global.selectedType=Global.CTRL_POINT;
                    point_type=CTRL_POINT;
                }else{// P2
                    if(i==len/2){
                        if(secondJunction!=null){
                            /*Global.selectedType=Global.P2_POINT
                            |Global.JUNCTION_POINT
                            |Global.END_POINT;*/
                            point_type=P2_POINT
                                    |JUNCTION_POINT
                                    |END_POINT;
                            Global.selectedObject.setJunction(secondJunction);
                            selectedObj=secondJunction;
                        }else{
                            /* Global.selectedType=Global.P2_POINT
                            |Global.END_POINT;*/
                            point_type=P2_POINT
                                    |END_POINT;
                        }
                    }else{
                        //Global.selectedType=Global.P2_POINT;
                        point_type=P2_POINT;
                    }
                }
                //Global.selectedObject.setObject(points[i],Global.selectedType);
                Global.selectedObject.setObject(selectedObj,point_type);
                return true;
            }
        }
        for(int i=0;(tstCurve=getCurveOnIndex(points,i))!=null;i++){
            if(CurveContains.isOnLine(tstCurve,testCircle)){
                Global.selectedObject.setObject(tstCurve,CURVE);
                Global.setSelectedBR(this);
                //Global.selectedType=Global.CURVE;
                return true;
            }
        }
        Global.selectedObject.setObject(null,NO_SELECTED_TYPE);
        Global.setSelectedBR(null);
        //Global.selectedType=Global.NO_SELECTED_TYPE;
        return false;
    }
    public static final QuadCurve2D getCurveOnIndex(Point2DSerializable points[], int i){
        Point2DSerializable tmpP1,tmpCtrl,tmpP2;
        if(points.length<i*2+2){
            return null;
        }
        int len=points.length/2;
        tmpP1=points[i].newInstance();
        tmpCtrl=points[len+i+1].newInstance();
        tmpP2=points[i+1].newInstance();
        return new QuadCurve2D.Double(tmpP1.getX(),tmpP1.getY(),
                tmpCtrl.getX(),tmpCtrl.getY(),
                tmpP2.getX(),tmpP2.getY());
    }
    public static final Point2DSerializable[] getPathPoints(JunctionLink jl){
        return getPathPoints(jl.getP1(),jl.getCtrlPoints(),jl.getP2Points());
    }
    public static final Point2DSerializable[] getPathPoints(Point2DSerializable sP1
                                                            ,Point2DSerializable sCtrls[]
                                                            ,Point2DSerializable sP2s[]){
        Point2DSerializable points[];
        if(sCtrls==null||sP2s==null){
            System.out.println("JunctionLink.getPathPoints: отсутствуют точки P2 и Ctrl");
            points=new Point2DSerializable[1];
            points[0]=sP1;
            return points;
        }
        points=new Point2DSerializable[sCtrls.length+sP2s.length+1];
//        int i;
        points[0]=sP1;
        System.arraycopy(sP2s,0,points,1,sP2s.length);
        System.arraycopy(sCtrls,0,points,sP2s.length+1,sCtrls.length);
        /*
        for(i=1;i<points.length;i+=2){
        points[i]=sCtrls[i/2];
        points[i+1]=sP2s[i/2];
        }*/
        return points;
    }
    public int getType(){
        return type;
    }
    public String[] getTypeNames() {
        return road_type_names;
    }
    public synchronized final void setType(int nt){
        type=nt;
        super.changed=true;
    }
    public final boolean isFirst(Junction fst){
        if(firstJunction!=null&&firstJunction.equals(fst)){
            return true;
        }
        return false;
    }
    public final boolean isSecond(Junction scnd){
        if(secondJunction!=null&&secondJunction.equals(scnd)){
            return true;
        }
        return false;
    }
    public Junction getFirstJunction(){
        return firstJunction;
    }
    public Junction getSecondJunction(){
        return secondJunction;
    }
    public Junction getSecondHalfJunction(Junction junction){
        if(junction.equals(firstJunction)){
            if(secondJunction==null){
                System.out.println("JunctionLink:getSecondHalfJunction()"
                        +" второй Junction равен null, first="
                        +firstJunction+" second="+secondJunction+" code:"+code);
            }
            return secondJunction;
        }else if(junction.equals(secondJunction)){
            if(firstJunction==null){
                System.out.println("JunctionLink:getSecondHalfJunction()"
                        +" первый Junction равен null, first="
                        +firstJunction+" second="+secondJunction+" code:"+code);
            }
            return firstJunction;
        }else{
            System.out.println(" не найден Junction "+ junction
                    +" в first="+firstJunction+" second="+secondJunction+" code:"+code);
            return null;
        }
    }
    public synchronized final boolean loadFields(Statement stmp,int indObj){
        changed=true;
        ResultSet rs;
        boolean hasNext=false;
        try {
            rs= stmp.executeQuery("SELECT CODE,J1NAME,J2NAME,TYPE,LENGTH " +
                    "FROM JUNCTIONLINKS "+
                    "ORDER BY POINTSCODE;");
            /*
            rs.first();
            if(rs.relative(indObj)==false){
            throw new Error("loadFields: строка не существует");
            }*/
            for(int i=0;i<=indObj;i++){
                hasNext=rs.next();
            }
            if(hasNext==false){
                System.out.println("критическая ситуация, обьект индекс="+indObj+" не найден в DB!!!"
                        +" загрузка JunctionLink остановлена"+" code:"+code);
                return false;
            }
            code=rs.getString("CODE");
            Junction j_1=Global.getJunction(rs.getString("J1NAME").trim());
            Junction j_2=Global.getJunction(rs.getString("J2NAME").trim());
            type=rs.getInt("TYPE");
            pathLength=rs.getDouble("LENGTH");
            hasNext=rs.next();
            if(code==null){
                throw new Error(" имя не может быть null"+" code:"+code);
            }

            rs= stmp.executeQuery("SELECT CTRLX,CTRLY,P2X,P2Y"+
                    " FROM POINTS,JUNCTIONLINKS"+
                    " WHERE JLCODE=POINTSCODE"+
                    " AND CODE LIKE '"+code+
                    "' ORDER BY PNTNUM");
            code=code.trim();
            if(rs.next()==false){
                System.out.println("невозможно загрузить точки code:"+code);
            }else{

                Point2DSerializable tmpP2,tmpCtrl;
                double tmpX,tmpY;

                do{
                    tmpX=rs.getDouble("CTRLX");
                    tmpY=rs.getDouble("CTRLY");
                    tmpCtrl=new Point2DSerializable(tmpX,tmpY,true);
                    tmpCtrl.setCoordsAsAbs();
                    tmpX=rs.getDouble("P2X");
                    tmpY=rs.getDouble("P2Y");
                    tmpP2=new Point2DSerializable(tmpX,tmpY,true);
                    tmpP2.setCoordsAsAbs();
                    super.addPointCtrl2End(tmpCtrl);
                    super.addPointP22End(tmpP2);
                }while(rs.next());
                addJunction(j_1);
                addJunction(j_2);
            }
        }catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }
        int num=Integer.parseInt(code.substring(DEFAULT_CODE.length()));
        if(num>numName){
            numName=num+1;
        }
        return hasNext;
    }
    public synchronized final void saveFields(Statement stmp){
        try {
            ResultSet rs;
//            int indJL=0;
            while(true){
                rs=stmp.executeQuery("SELECT CODE FROM JUNCTIONLINKS WHERE CODE LIKE '"+code.trim()+"';");
                if(rs.next()==false){
                    break;
                }
                int indColon=code.indexOf(".");
                String st;
                int nameNum=-1;
                if(indColon!=-1){
                    st=code.substring(0,indColon);
                    String num=code.substring(indColon+1);
                    nameNum=Integer.parseInt(num);
                }else{
                    st=code;
                }
                code=st+"."+(nameNum+1);
            };
            stmp.executeUpdate("INSERT INTO JUNCTIONLINKS VALUES('"+
                    code.trim()+"',"+numCode+",'"+(firstJunction!=null?firstJunction.getCode():"null")+"'"+
                    ",'"+(secondJunction!=null?secondJunction.getCode():"null")+"',"+type+","+pathLength+")");
            //stmp.executeUpdate("insert into points values(1,161,77,309,208,0)");
            Point2DSerializable tmpP2[],tmpCtrl[];
            tmpCtrl=getCtrlPoints();
            tmpP2=getP2Points();
            if(tmpCtrl==null){
                throw new Error("Ctrl == null "+this);
            }
            if(tmpP2==null){
                throw new Error("P2 == null "+this);
            }
            if(tmpCtrl.length!=tmpP2.length){
                throw new Error("saveFields: неравное колличество точек в P2 и Ctrl"+" code:"+code);
            }
            for(int i=0;i<tmpCtrl.length;i++){
                stmp.executeUpdate("INSERT INTO POINTS VALUES("+
                        numCode+","+tmpCtrl[i].getX()+","+tmpCtrl[i].getY()+
                        ","+tmpP2[i].getX()+","+tmpP2[i].getY()+","+i+")");
            }
        }catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }finally{
            numCode++;
        }
    }
    public String[] getTablesConfiguration(){
        return new String[]{"(CODE CHAR(64), " +
                " POINTSCODE INT, " +
                " J1NAME CHAR(64),"+
                " J2NAME CHAR(64),"+
                "TYPE INT,"+
                "LENGTH DOUBLE"+")",
                            "(JLCODE INT,"+
                "CTRLX DOUBLE,"+
                "CTRLY DOUBLE,"+
                "P2X DOUBLE,"+
                "P2Y DOUBLE,"+
                "PNTNUM INT)"};
    }
    public String[] getTableNames(){
        return new String[]{"JUNCTIONLINKS","POINTS"};
    }
    public final boolean isDead(){
        return (firstJunction==null&&secondJunction==null)
                &&(DBService.isLoading()==false);
    }
    public final int getLoadPriority(){
        return LOAD_SAVE_PRIORITY_1;
    }
    public synchronized void remove(){
        System.out.println("REMOVE JL: удаляется JL="+this);
        removeJunction(secondJunction);
        System.out.println("second удален");
        removeJunction(firstJunction);
        System.out.println("first удален");
        try {
            PSRender.removeRenderedObject(jlr);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
        }
        DBService.removeFromDBService(this);
        //jlr.remove();
        //jlr=null;
        changed=true;
        System.out.println("REMOVED JL "+this);
        //code=null;
        //currName=null;
    }
    public CheckReport selfCheck(){
        CheckReport report=new CheckReport(this);
        if(getCurrName().startsWith("NO NAME")){
            report.addError(CheckReport.NAME_NOT_SETTED);
        }
        if(getPathLength()<1){
            report.addError(CheckReport.LENGTH_MOT_SETTED);
        }
        if(getSecondJunction()==null){
            report.addError(CheckReport.NO_HAVE_SECOND_JUNCTION);
        }
        if(PSRender.isInFrame(getP1())==false){
            report.addError(CheckReport.OUTSIDE_OF_FRAME);
        }else{
            Point2DSerializable[] p2points=getP2Points();
            for(int i=0;i<p2points.length;i++){
                if(PSRender.isInFrame(p2points[i])==false){
                    report.addError(CheckReport.OUTSIDE_OF_FRAME);
                    break;
                }
            }
        }
        ArrayList outlines=DBService.getOutlines();
        CurveContainer thisOutline=getOutline();
        CurveContainer otherOutline;
        for(int i=0;i<outlines.size();i++){
            otherOutline=(CurveContainer)outlines.get(i);
            if(thisOutline.intersects(otherOutline)){
                report.addError(CheckReport.INTERSECT_WITH_OTHER_OBJECT);
                break;
            }
        }
        return report;
    }
    public CurveContainer getOutline(){
        CurveContainer cc=new CurveContainer(this);
        cc.moveTo(getP1().getX(),getP1().getY());
        Point2DSerializable[] p2s=getP2Points();
        Point2DSerializable[] ctrl=getCtrlPoints();
        for(int i=0;i<p2s.length;i++){
            cc.quadTo(ctrl[i].getX(),ctrl[i].getY(),p2s[i].getX(),p2s[i].getY());
        }
        return cc;
    }
    public synchronized void setP1(Point2DSerializable p){
        if(firstJunction==null){
            throw new Error("нет возможности установить P1"+" code:"+code);
        }
        firstJunction.setpN(p);
    }
    public boolean removeSecondJunction(){
        if(secondJunction!=null){
            return removeJunction(secondJunction);
        }else{
            System.out.println("не возможно удалить второй Junction в "+this+" f="+firstJunction+" s="+secondJunction);
            return false;
        }
    }
    public synchronized void changeJunctionCode(Junction oldJunction,Junction newJunction){
        if(oldJunction==null||newJunction==null){
            throw new Error(" имя не может быть null on="+
                    oldJunction+" nn="+newJunction+" code:"+code);
        }
        if(oldJunction.equals(firstJunction)){
            firstJunction=newJunction;
        }else if(oldJunction.equals(secondJunction)){
            secondJunction=newJunction;
        }else{
            throw new Error(" имя не найдено fj="+ firstJunction+
                    " sj="+secondJunction+" name="+newJunction+" code:"+code);
        }
        changed=true;
    }
    public final String getCode(){
        return code;
    }
    public final String getCurrName(){
        if(currName==null){
            setCurrName(null);
        }
        return currName;
    }
    public synchronized final void setCode(String code){
        this.code=code;
        changed=true;
    }
    public synchronized final void setCurrName(String currName){
        this.currName=currName;
        if(this.currName==null
                ||this.currName.length()<1
                ||"NO NAME".equalsIgnoreCase(this.currName)
                ||"null".equalsIgnoreCase(this.currName)){
            this.currName="NO NAME JL"+(code!=null?code.substring(12):"--");
        }
        changed=true;
    }
    public String toString(){
        String st=super.toString();
        int ind=st.indexOf("@");
        return code+".JL"+st.substring(ind);
    }
    public boolean getVisibilityName(){
        return this.visibilityName;
    }
    public synchronized void setVisibilityName(boolean visibility){
        this.visibilityName=visibility;
        changed=true;
    }
    public synchronized void setPathLenght(double length){
        pathLength=length;
    }
    public double getPathLength(){
        return pathLength;
    }
    public boolean equals(Object obj){
        if(obj instanceof JunctionLink){
            return code==((JunctionLink)obj).code;
        }
        return super.equals(obj);
    }
    public JunctionLinkRender getRender() {
        return jlr;
    }
}

