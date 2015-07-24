package roadmapproject;
//import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
//todo сделать обьединение точек или цометов
//todo разобраться с запретом обьединения точек из одного цомета
class EditorPopupListener extends MouseAdapter
        implements ActionListener,PreferencedShapeInterface,SelectedTypeCONSTANTS {
    JPopupMenu popup;
    JMenu menuChangeTypeLine;
    JMenu menuAddRoad;
    JMenu menuNewOtherObject;
    JMenuItem menuItemChangeTypeLine[];
    JMenuItem menuItemAddRoad[];
    JMenuItem menuItemAddRoadToEnd;
    JMenuItem menuItemCreateJunction;

    JMenuItem menuItemChangeName;
    JMenuItem menuItemInsertPoint;
    JMenuItem menuItemDeletePoint;
    JMenuItem menuItemDeleteCurve;
    JMenuItem menuItemSetLength;
    JMenuItem menuItemJLProperties;
    JMenuItem menuItemJProperties;
    JMenuItem menuItemAddOtherPath;
    JMenuItem menuItemNewBound;
    JMenuItem menuItemNewPond;
    JMenuItem menuItemNewCity;

    JMenuItem menuItemCenterize;

    static boolean addRoad=false;
    static boolean addOOPath=false;
    static boolean ctrlOnLine=false;
    //static boolean popUpIsOn=false;
    static JunctionLink firstSelected,secondSelected;
    static OtherObjects selectedOtherObject;
    static Point2DSerializable firstPressedPoint=new Point2DSerializable(true)
    ,secondPressedPoint=new Point2DSerializable(true);
    int firstSelectedPointType,secondSelectedPointType;

    static QuadCurve2D quad;
    static double start=System.currentTimeMillis();


    static ArrayList arrShpDrw=new ArrayList();

    public EditorPopupListener(){

        popup = new JPopupMenu();

        String roadNames[]=JunctionLink.road_type_names;
        menuItemChangeTypeLine=new JMenuItem[roadNames.length];
        for(int i=0,len=roadNames.length;i<len;i++){
            menuItemChangeTypeLine[i]=new JMenuItem(roadNames[i]);
        }
        menuChangeTypeLine=new JMenu("Change type");
        for(int i=0;i<menuItemChangeTypeLine.length;i++){
            menuItemChangeTypeLine[i].addActionListener(this);
            menuChangeTypeLine.add(menuItemChangeTypeLine[i]);
        }
        menuItemAddRoad=new JMenuItem[roadNames.length];
        for(int i=0,len=roadNames.length;i<len;i++){
            menuItemAddRoad[i]=new JMenuItem(roadNames[i]);
        }
        menuAddRoad=new JMenu("Add new road");
        for(int i=0;i<menuItemAddRoad.length;i++){
            menuItemAddRoad[i].addActionListener(this);
            menuAddRoad.add(menuItemAddRoad[i]);
        }

        menuItemCreateJunction = new JMenuItem("Create Junction");
        menuItemAddRoadToEnd = new JMenuItem("Add new Road");

        menuItemChangeName = new JMenuItem("Change name");
        menuItemInsertPoint = new JMenuItem("Insert point");
        menuItemDeletePoint = new JMenuItem("Delete point");
        menuItemDeleteCurve = new JMenuItem("Delete curve");
        menuItemSetLength = new JMenuItem("Set path length");
        menuItemJLProperties = new JMenuItem("Path properties");
        menuItemJProperties = new JMenuItem("Junction properties");
        menuItemAddOtherPath = new JMenuItem("Add path");
        menuItemNewBound = new JMenuItem("Bound");
        menuItemNewCity = new JMenuItem("City outline");
        menuItemNewPond = new JMenuItem("Pond");

       menuItemCenterize = new JMenuItem("Centerize");

        menuItemCreateJunction.addActionListener(this);
        menuItemAddRoadToEnd.addActionListener(this);

        menuItemChangeName.addActionListener(this);
        menuItemInsertPoint.addActionListener(this);
        menuItemDeletePoint.addActionListener(this);
        menuItemDeleteCurve.addActionListener(this);
        menuItemSetLength.addActionListener(this);
        menuItemJLProperties.addActionListener(this);
        menuItemJProperties.addActionListener(this);
        menuItemAddOtherPath.addActionListener(this);
        menuItemNewBound.addActionListener(this);
        menuItemNewCity.addActionListener(this);
        menuItemNewPond.addActionListener(this);

        menuItemCenterize.addActionListener(this);

        menuNewOtherObject=new JMenu("Create new object");
        menuNewOtherObject.add(menuItemNewBound);
        menuNewOtherObject.add(menuItemNewCity);
        menuNewOtherObject.add(menuItemNewPond);
    }
    public void mousePressed(MouseEvent e) {//PopupListener
        // System.out.println("in method: mousePressed(e) e.point="+e.getPoint()+" time="+(System.currentTimeMillis()-start));
        if(e.isPopupTrigger()){
            Point2DSerializable checkRange=new Point2DSerializable(e.getPoint(),true);
            if(checkRange.checkInsideFrame()==false){
                return;
            }
        }
        maybeShowPopup(e);
        if((e.getModifiers() & MouseEvent.BUTTON1_MASK)!=0 ){
            if(addRoad==true){
                SelectedObject.setNoBlink();
                //firstSelected.setAlwaysRepaint();
                addJunctionAndRoad(new Point2DSerializable(e.getPoint(),true));//////***
                addRoad=false;
                ctrlOnLine=true;
                PSRender.allwaysRepaintOn();
            }else if(ctrlOnLine==true){
                ////////////////////////////////
                PSRender.allwaysRepaintOff();
                firstSelected.removeLastCtrl();
                firstSelected.addPointCtrl2End(new Point2DSerializable(e.getPoint(),true));
                /**/
                PSRender.repaint();
                //firstSelected.resetAlwaysRepaint();
                firstSelected=null;
                secondSelected=null;
                firstSelectedPointType=NO_SELECTED_TYPE;
                secondSelectedPointType=NO_SELECTED_TYPE;

                SelectedObject.setBlink();
                ctrlOnLine=false;
            }else if(addOOPath==true){
                addOOPath=false;
                if(selectedOtherObject!=null){
                    selectedOtherObject.addP2(new Point2DSerializable(e.getPoint(),true));
                }else{
                    System.out.println("Other object == null");
                }
                PSRender.repaint();
            }
        }
        //System.out.println("\tout method: mousePressed()");
    }

    public void mouseReleased(MouseEvent e) {
        // SelectedObject.setNoBlink();
        /*
        if(popUpIsOn==true){
        popUpIsOn=false;
        SelectedObject.setBlink();
        }*/
        //System.out.println("in method: mouseReleased(e) e.point="+e.getPoint()+" time="+(System.currentTimeMillis()-start));
        if(e.isPopupTrigger()){
            Point2DSerializable checkRange=new Point2DSerializable(e.getPoint(),true);
            if(checkRange.checkInsideFrame()==false){
                return;
            }
        }
        maybeShowPopup(e);
        //System.out.println("\tout method: mouseReleased");
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()&&addRoad==false&&ctrlOnLine==false) {
            // System.out.println("in method: maybeShowPopup(e) e.point="+e.getPoint());
            //SelectedObject.setNoBlink();
            //popUpIsOn=true;
            selectedOtherObject=null;
            firstSelected=null;
            if(Global.selectedBR instanceof JunctionLink){
                firstSelected=(JunctionLink)Global.selectedBR;

            }else if(Global.selectedBR instanceof OtherObjects){
                selectedOtherObject=(OtherObjects)Global.selectedBR;

            }
            firstSelectedPointType=Global.selectedObject.getType();
            if((firstSelectedPointType&MASK_POINT)!=0){
                firstPressedPoint.setLocation((Point2DSerializable)(Global.selectedObject.getObject()));
            }else{
                firstPressedPoint.setLocation(new Point2DSerializable(e.getPoint(),true));////***

            }
            switch(firstSelectedPointType){
                case P1_POINT:// можно выкинуть
                    //SelectedObject.setBlink();
                    return;
                case CTRL_POINT:// ничо там не надо
                    //SelectedObject.setBlink();
                    return;
                case CTRL_POINT|OTHER_OBJECT:
                    return;
                case P2_POINT:
                    /* 1. создать Junction
                    2. сменить тип линии
                    3. добавить линию (меню с типом линий)
                    а) после выбоа типа линии создать в точке Junction
                    б) запустить функцию присоединения новой линии с выбранным типом к Junction */
                    popup.removeAll();
                    popup.add(menuItemCreateJunction);
                    popup.add(menuChangeTypeLine);
                    popup.add(menuAddRoad);
                    popup.add(menuItemDeletePoint);
                    popup.add(menuItemSetLength);
                    popup.add(menuItemJLProperties);
                    popup.add(menuItemCenterize);
                    break;
                case CURVE:
                    /* 1. сменить тип линии
                    */
                    popup.removeAll();
                    popup.add(menuItemInsertPoint);
                    popup.add(menuItemChangeName);
                    popup.add(menuChangeTypeLine);
                    popup.add(menuItemDeleteCurve);
                    popup.add(menuItemSetLength);
                    popup.add(menuItemJLProperties);
                    popup.add(menuItemCenterize);
                    Point2DSerializable quadPoints[]=Global.selectedObject.getCurvePoints();
                    quad= new QuadCurve2D.Double(
                            quadPoints[0].getX()
                            ,quadPoints[0].getY()
                            ,quadPoints[1].getX()
                            ,quadPoints[1].getY()
                            ,quadPoints[2].getX()
                            ,quadPoints[2].getY());
                    break;
                case P1_POINT|END_POINT:
                    /* 1. создать Junction
                    2. сменить тип линии
                    3. присоединить линию к концу
                    */
                    popup.removeAll();
                    popup.add(menuItemCreateJunction);
                    popup.add(menuChangeTypeLine);
                    popup.add(menuItemAddRoadToEnd);
                    break;
                case P2_POINT|END_POINT:
                    /* 1. создать Junction
                    2. присоединить линию к концу
                    */
                    popup.removeAll();
                    popup.add(menuItemCreateJunction);
                    popup.add(menuItemAddRoadToEnd);
                    popup.add(menuItemJLProperties);
                    break;
                case P1_POINT|END_POINT|JUNCTION_POINT:
                    /* 1. присоединить новую линию (меню с типом линии) */
                    popup.removeAll();
                    popup.add(menuAddRoad);
                    popup.add(menuItemChangeName);

                    popup.add(menuItemJProperties);
                    break;
                case P2_POINT|END_POINT|JUNCTION_POINT:
                    /* 1. присоединить новую линию (меню с типом линии) */
                    popup.removeAll();
                    popup.add(menuAddRoad);
                    popup.add(menuItemChangeName);

                    popup.add(menuItemJProperties);
                    break;
                case P1_POINT|END_POINT|OTHER_OBJECT:
                case P2_POINT|END_POINT|OTHER_OBJECT:
                case P2_POINT|OTHER_OBJECT:
                    popup.removeAll();
                    popup.add(menuItemDeletePoint);
                    break;
                case CURVE|OTHER_OBJECT:
                    popup.removeAll();
                    popup.add(menuItemAddOtherPath);
                    break;
                case IN_OBJECT|OTHER_OBJECT:
                    popup.removeAll();
                    popup.add(menuItemChangeName);
                    break;
                default:
                    popup.removeAll();
                    popup.add(menuItemCreateJunction);
                    popup.add(menuNewOtherObject);
            }

            popup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
        //System.out.println("\tout method: maybeShowPopup()");
    }
    public void actionPerformed(ActionEvent ae){
        if(!(ae.getSource() instanceof JMenuItem)){
            return;
        }
        if(menuItemCenterize==ae.getSource()){
              PSRender.moveToTop(firstSelected.getBound(),1.0);
        }else
        if(menuItemNewBound==ae.getSource()){
//            todo подкрутить что б выдавло первую линию сделать
            OtherObjects oo=new OtherObjects(new Point2DSerializable(firstPressedPoint),
                    OtherObjects.BORDER);
            oo.addP2(new Point2DSerializable(firstPressedPoint));
        }else if(menuItemNewCity==ae.getSource()){
            OtherObjects oo=new OtherObjects(new Point2DSerializable(firstPressedPoint),
                    OtherObjects.CITY);
            oo.addP2(new Point2DSerializable(firstPressedPoint));
        }else if(menuItemNewPond==ae.getSource()){
            OtherObjects oo=new OtherObjects(new Point2DSerializable(firstPressedPoint),
                    OtherObjects.POND);
            oo.addP2(new Point2DSerializable(firstPressedPoint));
        }else if(menuItemAddOtherPath==ae.getSource()){
            addOOPath=true;
            try {
                PSRender.addRenderedObject(this);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }else if(menuItemJProperties==ae.getSource()){
            IFrameSupervisor.showJInternalFrame(Global.selectedObject.getJunction(),IFrameSupervisor.PROPERTIES_FRAME);
        }else if(menuItemJLProperties==ae.getSource()){
            IFrameSupervisor.showJInternalFrame(firstSelected,IFrameSupervisor.PROPERTIES_FRAME);
        }else if(menuItemSetLength==ae.getSource()){
            double currLen=firstSelected.getPathLength();
            String st=JOptionPane.showInputDialog(RoadPanel.lastRoadPanel,"Type new length (m)",new Double(currLen));
            try {
                if(st!=null){
                    currLen=Double.parseDouble(st);
                    firstSelected.setPathLenght(currLen);
                }
            }
            catch (NumberFormatException ex) {
            }
            System.out.println("st="+st);
        }else if(menuItemDeleteCurve==ae.getSource()){
            PathDataProcession.remove(firstSelected,quad);
        }else if(menuItemDeletePoint==ae.getSource()){
            if(firstSelected!=null){
                PathDataProcession.remove(firstSelected,firstPressedPoint);
            }else if(selectedOtherObject!=null){
                selectedOtherObject.delete_point(firstPressedPoint);
            }else{
                System.out.println("хуйня в делете все нули");
            }
        }else if(menuItemInsertPoint==ae.getSource()){
            PathDataProcession.insert(firstSelected,new Point2DSerializable(firstPressedPoint));
        }else if(menuItemChangeName==ae.getSource()){
            //PSRender.paintOff();
            /*LanguageTableFrame ltf;
            if(Global.selectedObject.isJunction()){
            ltf=new LanguageTableFrame(Global.selectedObject.getJunction());
            }else{
            ltf=new LanguageTableFrame(firstSelected);
            }
            RoadPanel.lastRoadPanel.add(ltf);
            try {
            ltf.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {}*/
            MultyLanguage ml;
            if(Global.selectedObject.isJunction()){
                ml=Global.selectedObject.getJunction();
            }else if(selectedOtherObject!=null){
                ml=selectedOtherObject;
            }else{
                ml=firstSelected;
            }
            IFrameSupervisor.showJInternalFrame(ml,IFrameSupervisor.LANGUAGE_FRAME);
        }else if(menuItemAddRoadToEnd == ae.getSource()){
            System.out.println("add clicked");
            addRoad=true;
            try {
                PSRender.addRenderedObject(this);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }else if(menuItemCreateJunction==ae.getSource()){
            createJunction(firstSelected,firstPressedPoint,firstSelectedPointType);
        }else {
            int i;
            for(i=0;i<menuItemAddRoad.length;i++){
                if(menuItemAddRoad[i]==ae.getSource()){
                    Junction j2add=null;
                    j2add=createJunction(firstSelected,firstPressedPoint,firstSelectedPointType);
                    firstSelected=addNewRoad(j2add,i+1);
                    break;
                }else if(menuItemChangeTypeLine[i]==ae.getSource()){
                    setRoadType(i+1);
                    break;
                }
            }
        }
        //System.out.println("\tout method: actionPerformed()");
        //SelectedObject.setBlink();
    }

    /**
     * jl - JunctionLink на котором нужно поделить в точке pnt и установить в
     * этой точке Junction
     */
    public Junction createJunction(JunctionLink jl,Point2DSerializable pnt,int typePnt){
        /*System.out.println("in method: createJunction(jl,pnt,typePnt) jl="
        +jl
        +" pnt="
        +pnt
        +" typePnt="
        +typePnt
        +" time="
        +(System.currentTimeMillis()-start));*/
        if((typePnt&CTRL_POINT)!=0){
            return null;
        }
        Junction tmpJ;
        if((tmpJ=getJunction(jl,typePnt))!=null){
            System.out.println(tmpJ.getCode()+" уже создан"+" запрос из"+jl);
            return tmpJ;
        }
        tmpJ=new Junction(null);
        tmpJ.setpN(pnt);
        if(typePnt==NO_SELECTED_TYPE){
            firstSelected=addNewRoad(tmpJ,typePnt);
        }else{
            PathDataProcession.subdivide(jl,tmpJ);
        }
        return tmpJ;
    }
    public void setRoadType(int type){
        firstSelected.setType(type);
    }
    public JunctionLink addNewRoad(Junction j,int typeRoad){
        // System.out.println("in method: addNewRoad(j,typeRoad) j="+j+" typeRoad="+typeRoad+" time="+(System.currentTimeMillis()-start));
        JunctionLink jl=new JunctionLink(j);
        //firstSelected=jl;
        jl.setType(typeRoad);
        try {
            PSRender.addRenderedObject(this);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
        }
        addRoad=true;
        // System.out.println("\tout method: addNewRoad()");
        return jl;
    }

    public void addJunctionAndRoad(Point2DSerializable p){
        System.out.println("in method: addJunctionAndRoad(p) "+p+" time="+(System.currentTimeMillis()-start));

        if(Global.selectedBR instanceof JunctionLink){
            secondSelected=(JunctionLink)Global.selectedBR;
        }
        secondSelectedPointType=Global.selectedObject.getType();
        if((secondSelectedPointType&MASK_POINT)==0){
            secondPressedPoint.setLocation(p);
            //secondPressedPoint.cnvFrmToAbs();
        }else{
            secondPressedPoint.setLocation(
                    (Point2DSerializable)(Global.selectedObject.getObject()));
        }/*
        if(Global.selectedObject.isJunction()){
        Junction j_1=Global.selectedObject.getJunction();
        if(firstSelectedPointType==Global.NO_SELECTED_TYPE){
        firstSelectedPointType=Global.P1_POINT;
        }

        ArrayList al=j_1.getPaths();
        System.out.println("########\n### al="+al.size()+"\n#######");
        for(int i=0,len=al.size();i<len;i++){
        if(al.get(i)==secondSelected){
        firstSelected=(JunctionLink)al.get(i);
        firstSelectedPointType|=Global.JUNCTION_POINT|Global.END_POINT;
        break;
        }
        }
        }*/
        if(firstSelected==secondSelected){
            firstSelected=addNewRoad(createJunction(firstSelected
                    ,firstPressedPoint
                    ,firstSelectedPointType)
                    ,firstSelected.getType());
        }
        /*firstSelected.addPointP22End(
        new Point2DSerializable(secondPressedPoint.getX()////***
        ,secondPressedPoint.getY()));
        firstSelected.addPointCtrl2End(RoadPanel.mousePosition);*/
        firstSelected.addPairToEnd(RoadPanel.mousePosition
                ,new Point2DSerializable(secondPressedPoint.getX()
                ,secondPressedPoint.getY(),true));

        if((secondSelectedPointType&MASK_P1_OR_P2)!=0){
            System.out.println("соединение точек");
            Junction junc;
            if((junc=createJunction(secondSelected,secondPressedPoint,secondSelectedPointType))!=null){
                junc.addPath(firstSelected);
            }else{
                System.out.println("попытка создать Junction в Ctrl точке");
            }
        }
        System.out.println("\tout method: addJunctionAndRoad();");
    }

    public Junction getJunction(JunctionLink jl,int typePnt){
        System.out.println("in method: getJunction(jl,typePnt) jl="
                +jl
                +" typePnt="
                +typePnt
                +" time="
                +(System.currentTimeMillis()-start));
        if((typePnt&JUNCTION_POINT)!=0){
            if((typePnt&P1_POINT)!=0){
                System.out.println("\tout method: getJunction() junction in P1");
                    return jl.getFirstJunction();
            }else if((typePnt&P2_POINT)!=0){
                System.out.println("\tout method: getJunction() junction in P2");
                    return jl.getSecondJunction();
            }else{
                System.out.println("ERROR: проверь ошибку в getJunction()"
                        +" невозможно вытащить Junction тип точки не определен");
            }
        }
        System.out.println("\tout method: getJunction() no junction");
        return null;
    }
    public synchronized void initializeShapes(){
        PreferencedShape tmpShpPref;



        tmpShpPref=new PreferencedShape(new Ellipse2D.Double(),
                LAYER_POINTS_1_ADD_FILL);
        arrShpDrw.add(tmpShpPref);
        tmpShpPref=new PreferencedShape(new Line2D.Double(),
                LAYER_POINTS_2_ADD_OUTLINE);
        arrShpDrw.add(tmpShpPref);
        if(selectedOtherObject!=null){
            tmpShpPref=new PreferencedShape(new Line2D.Double(),
                    LAYER_POINTS_2_ADD_OUTLINE);
            arrShpDrw.add(tmpShpPref);
        }
    }

    public synchronized boolean setShapes(){
        if(addRoad==false&&addOOPath==false){
            try {
                PSRender.removeRenderedObject(this);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());  //To change body of catch statement use Options | File Templates.
            }
        }
        Point2DSerializable p1,p2;
        p1 = new Point2DSerializable(firstPressedPoint,false);
        p2 = new Point2DSerializable(RoadPanel.mousePosition,false);
        ((Ellipse2D)((PreferencedShape)arrShpDrw.get(0)).getShape())
                .setFrame(p2.getX()-5.0//PSRender.scale/2
                        ,p2.getY()-5.0//-PSRender.scale/2
                        ,10.0//PSRender.scale
                        ,10.0);//PSRender.scale);
        if(selectedOtherObject==null){
            ((Line2D)((PreferencedShape)arrShpDrw.get(1)).getShape())
                    .setLine(p1.getX()
                            ,p1.getY()
                            ,p2.getX()
                            ,p2.getY());
        } else {

            p1=new Point2DSerializable(selectedOtherObject.getFP(),false);
            ((Line2D)((PreferencedShape)arrShpDrw.get(1)).getShape())
                    .setLine(p1.getX()
                            ,p1.getY()
                            ,p2.getX()
                            ,p2.getY());
            p1=new Point2DSerializable(selectedOtherObject.getSP(),false);
            ((Line2D)((PreferencedShape)arrShpDrw.get(2)).getShape())
                    .setLine(p1.getX()
                            ,p1.getY()
                            ,p2.getX()
                            ,p2.getY());
        }


        return true;
    }

    public synchronized void translate(Point2DSerializable p) {
    }

    public synchronized ArrayList getShapes(){
        return arrShpDrw;
    }
}