package roadmapproject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
// todo сделать отображение всех слоев в таблице в отдельном фрейме
// todo сделать чтоб карта не сматывалась за пределы экрана
// todo сделать режим пользователя
public class PSRender
        implements Runnable,DrawCONSTANTS,MapApplicationListener {
    public static final int EDIT_MODE=1;
    public static final int PIC_MOVE_MODE=2;
    public static final int MAP_MOVE_MODE=3;

    private static int mode =EDIT_MODE;
    public static int curr_label_visibility=LAYER_LABEL_3_LOW_IMPORTANCE_FILL;

    private /*volatile*/ static ArrayList renderedObjects = new ArrayList();
    //private static ArrayList dpLayers;
    private static final ArrayList[] dpLayers;
    private static final GeneralPath[] layerPaths;
    private static final Color[] layerColors;
    private static final Stroke[] layerStroke;
    private static boolean repaintOn = false;
    private static boolean repaint_anywhere = false;
    private static boolean alwaysRepaint = false;
    private static boolean need_repaint = true;
    private static boolean isOnPaint = false; // ведется прорисовка
    private static boolean paintOn = true; // включение/выключение постоянного repaint
    private static boolean not_moved; // флаг активного сдвига или зума
    private static boolean on_hold = false; // включение заморозки изображения
    private static boolean redrawAll=false;
    private static double scale;
    private static boolean draw_pic = true;
    /*frameOffset = new Point2DSerializable()
    ,*/
    public static Point2DSerializable center = new Point2DSerializable(true);
    private static Point2DSerializable pic_offset = new Point2DSerializable(0, 0,true);
    private static BufferedImage off_line_content;
    //private static Image off_line_content;
    private static Rectangle2D clear = new Rectangle2D.Double(); // пустой ректангл для очистки экрана
    private static RoadPanel rp;

    private static double delay; // задержка на сдвиг и зум


    private static boolean edit_mode=true;


    private Thread t;

    private static final Line2D sVertLine = new Line2D.Double()
    , sHorLine = new Line2D.Double()
    , eVertLine = new Line2D.Double()
    , eHorLine = new Line2D.Double();
    private static final Point2DSerializable p1 = new Point2DSerializable(true)
    , p2 = new Point2DSerializable(true)
    , p3 = new Point2DSerializable(true)
    , p4 = new Point2DSerializable(true);

    private static final Stroke strSolid = new BasicStroke(0.3f);
    private static final Stroke solid = new BasicStroke(1.0f);
    private static double gridHeigth = 5000.0d;
    private static double gridWidth = 6000.0d;
    private static double map_width=3000.0d;
    private static double map_heigth=2000.0d;
    private static BufferedImage map;
    private static MediaTracker mt;
    private static final Image arrows = Toolkit.getDefaultToolkit().getImage(
            "images\\arrows.jpg");
    private static Graphics2D g2d_content;
    private static ArrayList repaint_components;

    static {
        //dpLayers = new ArrayList();
        dpLayers=new ArrayList[DRW_PRIORITY_LAYER_QUANTITY];
        layerPaths=new GeneralPath[DRW_PRIORITY_LAYER_QUANTITY];
        layerColors = new Color[DRW_PRIORITY_LAYER_QUANTITY];
        layerStroke = new Stroke[DRW_PRIORITY_LAYER_QUANTITY];
        for (int i = 0; i < DRW_PRIORITY_LAYER_QUANTITY; i++) {
//            dpLayers.add(new ArrayList());
            dpLayers[i]=new ArrayList();
            layerPaths[i]=new GeneralPath();
        }
//        colors
        layerColors[LAYER_0]                                    =Color.ORANGE;// not used
// other object colors
        layerColors[LAYER_OTHER_OBJECTS_1_BORDER_FILL]          =Color.PINK;
        layerColors[LAYER_OTHER_OBJECTS_2_BORDER_DRAW]          =Color.WHITE;// not used
        layerColors[LAYER_OTHER_OBJECTS_3_CITY_FILL]            =Color.GREEN;
        layerColors[LAYER_OTHER_OBJECTS_4_CITY_DRAW]            =Color.ORANGE;
        layerColors[LAYER_OTHER_OBJECTS_5_POND_FILL]            =new Color(200,230,255);//Color.BLUE;
        layerColors[LAYER_OTHER_OBJECTS_6_POND_DRAW]            =Color.GRAY;
        layerColors[LAYER_OTHER_OBJECTS_7_LABEL]                =Color.WHITE;
// road paths colors
        layerColors[LAYER_ROAD_1_EARTH_OUTLINE]                 =Color.BLACK;
        layerColors[LAYER_ROAD_2_EARTH_INLINE]                  =Color.LIGHT_GRAY;
        layerColors[LAYER_ROAD_3_BY_OUTLINE]                    =Color.BLUE;
        layerColors[LAYER_ROAD_4_BY_INLINE]                     =Color.WHITE;
        layerColors[LAYER_ROAD_5_MAIN_OUTLINE]                  =Color.CYAN;
        layerColors[LAYER_ROAD_6_MAIN_INLINE]                   =Color.YELLOW;
        layerColors[LAYER_ROAD_7_HIWAY_OUTLINE]                 =Color.PINK;
        layerColors[LAYER_ROAD_8_HIWAY_INLINE]                  =Color.YELLOW;
// control point colors
        layerColors[LAYER_POINTS_1_CTRL_FILL]                   =Color.GREEN;
        layerColors[LAYER_POINTS_2_CTRL_OUTLINE]                =Color.MAGENTA;
// add point colors
        layerColors[LAYER_POINTS_1_ADD_FILL]                    =Color.GRAY;
        layerColors[LAYER_POINTS_2_ADD_OUTLINE]                 =Color.BLUE;
// label color
        layerColors[LAYER_LABEL_1_NOT_KNOWN_IMPORTANCE_FILL]    =Color.LIGHT_GRAY;
        layerColors[LAYER_LABEL_2_NOT_KNOWN_IMPORTANCE_TEXT]    =Color.GRAY;
        layerColors[LAYER_LABEL_3_LOW_IMPORTANCE_FILL]          =Color.WHITE;
        layerColors[LAYER_LABEL_4_LOW_IMPORTANCE_TEXT]          =new Color(100,100,255);
        layerColors[LAYER_LABEL_5_NORMAL_IMPORTANCE_FILL]       =Color.WHITE;
        layerColors[LAYER_LABEL_6_NORMAL_IMPORTANCE_TEXT]       =Color.BLUE;
        layerColors[LAYER_LABEL_7_MEDIUM_IMPORTANCE_FILL]       =Color.YELLOW;
        layerColors[LAYER_LABEL_8_MEDIUM_IMPORTANCE_TEXT]       =new Color(180,0,0);//Color.GREEN;
        layerColors[LAYER_LABEL_9_HI_IMPORTANCE_FILL]           =Color.YELLOW;
        layerColors[LAYER_LABEL_10_HI_IMPORTANCE_TEXT]          =Color.BLUE;

        // stroke (толщина)
        layerStroke[LAYER_0]                                =new BasicStroke();// not used
// other object stroke
        layerStroke[LAYER_OTHER_OBJECTS_1_BORDER_FILL]      =new BasicStroke(7.0f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,0.0f,
                new float[]{20.0f,9.0f,1.0f,9.0f},
                0.0f);
        layerStroke[LAYER_OTHER_OBJECTS_2_BORDER_DRAW]      =new BasicStroke();// not used
        layerStroke[LAYER_OTHER_OBJECTS_3_CITY_FILL]        =new BasicStroke(1.0f);
        layerStroke[LAYER_OTHER_OBJECTS_4_CITY_DRAW]        =new BasicStroke(1.0f);
        layerStroke[LAYER_OTHER_OBJECTS_5_POND_FILL]        =new BasicStroke(1.0f);
        layerStroke[LAYER_OTHER_OBJECTS_6_POND_DRAW]        =new BasicStroke(1.0f);
        layerStroke[LAYER_OTHER_OBJECTS_7_LABEL]            =new BasicStroke(1.0f);
// road path stroke
        layerStroke[LAYER_ROAD_1_EARTH_OUTLINE]             =new BasicStroke(1.5f);
        layerStroke[LAYER_ROAD_2_EARTH_INLINE]              =new BasicStroke(0.5f);
        layerStroke[LAYER_ROAD_3_BY_OUTLINE]                =new BasicStroke(3.0f);
        layerStroke[LAYER_ROAD_4_BY_INLINE]                 =new BasicStroke(1.0f);
        layerStroke[LAYER_ROAD_5_MAIN_OUTLINE]              =new BasicStroke(4.0f);
        layerStroke[LAYER_ROAD_6_MAIN_INLINE]               =new BasicStroke(2.0f);
        layerStroke[LAYER_ROAD_7_HIWAY_OUTLINE]             =new BasicStroke(5.0f);
        layerStroke[LAYER_ROAD_8_HIWAY_INLINE]              =new BasicStroke(3.0f);
// control point stroke
        layerStroke[LAYER_POINTS_1_CTRL_FILL]               =new BasicStroke(1.0f);
        layerStroke[LAYER_POINTS_2_CTRL_OUTLINE]            =new BasicStroke(1.0f);
// add point stroke
        layerStroke[LAYER_POINTS_1_ADD_FILL]                =new BasicStroke(1.0f);
        layerStroke[LAYER_POINTS_2_ADD_OUTLINE]             =new BasicStroke(2.0f);
// label stroke
        layerStroke[LAYER_LABEL_1_NOT_KNOWN_IMPORTANCE_FILL]=new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_2_NOT_KNOWN_IMPORTANCE_TEXT]=new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_3_LOW_IMPORTANCE_FILL]      =new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_4_LOW_IMPORTANCE_TEXT]      =new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_5_NORMAL_IMPORTANCE_FILL]    =new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_6_NORMAL_IMPORTANCE_TEXT]    =new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_7_MEDIUM_IMPORTANCE_FILL]   =new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_8_MEDIUM_IMPORTANCE_TEXT]   =new BasicStroke(1.0f);
        layerStroke[LAYER_LABEL_9_HI_IMPORTANCE_FILL]       =new BasicStroke(5.0f);
        layerStroke[LAYER_LABEL_10_HI_IMPORTANCE_TEXT]      =new BasicStroke(1.0f);
//        end stroke
        setCenter();
        scale = 1;
        repaint_components=new ArrayList();
    }
    public static void createImage(String url) {
        Image img = Toolkit.getDefaultToolkit().getImage(url);

        try {
            mt = new MediaTracker(rp);
            mt.addImage(img, 0);
            mt.addImage(arrows, 1);
            mt.waitForAll();
        }
        catch (InterruptedException e) {
            return;
        }
        map = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_3BYTE_BGR);
        map.getGraphics().drawImage(img, 0, 0, null);
        repaint();
    }
    public PSRender(JComponent jp) {
        rp = (RoadPanel) jp;
        MapApplication.addMAListener(this);
        createImage("images\\1.jpg");
        t = new Thread(this);
        t.start();
    }
    public static void add(JComponent jc){
        repaint_components.add(jc);
    }
    public synchronized static void addRenderedObject(PreferencedShapeInterface obj) throws Exception{
        System.out.println("+++PS render adding " + obj+"+++++++++++++++++++++++++++++++++++++");
        if (renderedObjects.contains(obj)) {
            System.out.println("обьект " + obj +
                    " уже находиться в PSRender удаление...");
            removeRenderedObject(obj);

        }
        renderedObjects.add(obj);
        ArrayList tmpArrLst;
        PreferencedShape tmpShapePref;
        try {
            obj.initializeShapes();
            tmpArrLst = obj.getShapes();
            System.out.print("в слои:");
            if (tmpArrLst != null) {
                for (int i = 0; i < tmpArrLst.size(); i++) {
                    tmpShapePref = (PreferencedShape) tmpArrLst.get(i);
                    int ind = tmpShapePref.getDrawPriority();
                    System.out.print(ind+",");
//                    ( (ArrayList) dpLayers.get(ind)).add(tmpShapePref);
                    dpLayers[ind].add(tmpShapePref);
                }
            }
            System.out.println();
        }
        catch (Exception e) {
            System.out.println("походу у обьекта "
                    + obj
                    + "не корректная работа метода или не"
                    + " найден метод getPreferencedShapes()"
                    + " .дальнейшая работа рендера остановлена");
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());

        }
        System.out.println("+++PS render added " + obj + " осталось:" +
                renderedObjects.size()+"+++++++++++++++++++++++++++++++++++++");
        redrawAll=true;
        repaint();
    }
    public synchronized static void removeRenderedObject(PreferencedShapeInterface obj) throws Exception{
        System.out.println("---PS render удаление " + obj+"------------------------------");
        if(renderedObjects.remove(obj)==false){
            throw new Exception(" не найден обьект для удаления " + obj);
        }
        System.out.print("в слоях:");
        ArrayList objsForRemove =  obj.getShapes();
        PreferencedShape shpPref;
        for (int i = 0; i < objsForRemove.size(); i++) {
            shpPref = (PreferencedShape) objsForRemove.get(i);
            int indPrior = shpPref.getDrawPriority();
            System.out.print(indPrior+",");
//            if ( ( (ArrayList) dpLayers.get(indPrior)).remove(shpPref) == false) {
            if ( dpLayers[indPrior].remove(shpPref) == false) {
                System.out.print("слои неисправного обькта:");
                for (int j = 0; j < objsForRemove.size(); j++){
                    System.out.print(((PreferencedShape) objsForRemove.get(i)).getDrawPriority()+",");
                }
                throw new Exception(" не найден обьект для удаления " + obj + " " + shpPref+" в слое "+indPrior);
            }
        }
        System.out.println();

        System.out.println("---PS render удален " + obj + " осталось:" +
                renderedObjects.size()+"------------------------------");
        if (renderedObjects.size() == 0) {
            System.out.println("#####\n\nPSRENDER все удалено\n\n#####");
        }
        redrawAll=true;
    }
    public synchronized static void revalidateRenderedObject(PreferencedShapeInterface obj)throws Exception{
        System.out.println("REVALIDATE OBJECT "+obj);
        if(renderedObjects.contains(obj)){
            removeRenderedObject(obj);
        }
        addRenderedObject(obj);
    }
    private synchronized static final void setShapes() {
        if (on_hold&&repaint_anywhere==false&&redrawAll==false) {
            return;
        }
        for (int i = 0; i < renderedObjects.size(); i++) {
            try{
                if ( ( (PreferencedShapeInterface) renderedObjects.get(i)).setShapes()==true) {
                    need_repaint = true;
                }
            } catch(Exception e){
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        if(need_repaint){
            for(int i=0;i<layerPaths.length;i++){
                layerPaths[i].reset();
            }
            PreferencedShapeInterface psi;
            ArrayList ps_al;
            PreferencedShape ps;
            for (int i = 0; i < renderedObjects.size(); i++) {
                psi= ( (PreferencedShapeInterface) renderedObjects.get(i));
                ps_al=psi.getShapes();
                for(int j=0;j<ps_al.size();j++){
                    ps=(PreferencedShape)ps_al.get(j);
                    layerPaths[ps.getDrawPriority()].append(ps.getShape(),false);
                }
            }
            for(int i=LAYER_OTHER_OBJECTS_1_BORDER_FILL;i<LAYER_ROAD_8_HIWAY_INLINE;i+=2){
                layerPaths[i].transform(Point2DSerializable.at);
                layerPaths[i+1]=layerPaths[i];
            }

            layerPaths[LAYER_POINTS_2_CTRL_OUTLINE]=layerPaths[LAYER_POINTS_1_CTRL_FILL];
//            layerPaths[LAYER_ROAD_2_EARTH_INLINE]   =layerPaths[LAYER_ROAD_1_EARTH_OUTLINE];
//            layerPaths[LAYER_ROAD_4_BY_INLINE]      =layerPaths[LAYER_ROAD_3_BY_OUTLINE];
//            layerPaths[LAYER_ROAD_6_MAIN_INLINE]    =layerPaths[LAYER_ROAD_5_MAIN_OUTLINE];
//            layerPaths[LAYER_ROAD_8_HIWAY_INLINE]   =layerPaths[LAYER_ROAD_7_HIWAY_OUTLINE];
        }
    }
    public synchronized static final void drawMainView(Graphics2D g2d,AffineTransform at,boolean repaint){
        int i;
        if(repaint==false&&(need_repaint==false||not_moved==false)){
            return;
        }
        GeneralPath path=new GeneralPath();
        g2d.setPaint(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(0,0,gridWidth,gridHeigth));
        g2d.setStroke(layerStroke[0]);
        for(i = LAYER_OTHER_OBJECTS_3_CITY_FILL;i<LAYER_OTHER_OBJECTS_6_POND_DRAW;i+=2){
            g2d.setPaint(layerColors[i]);
            path.reset();
            for(int j=0;j<dpLayers[i].size();j++){
                path.append(((PreferencedShape)dpLayers[i].get(j)).getShape(),false);
            }
            path.transform(at);
            g2d.fill(path);
        }
        for(i = LAYER_ROAD_1_EARTH_OUTLINE;i<=LAYER_ROAD_8_HIWAY_INLINE;i+=2){
            g2d.setPaint(layerColors[i]);
            path.reset();
            for(int j=0;j<dpLayers[i].size();j++){
                path.append(((PreferencedShape)dpLayers[i].get(j)).getShape(),false);
            }
            path.transform(at);
            g2d.draw(path);
        }

        GeneralPath rect=new GeneralPath(new Rectangle2D.Double(-Point2DSerializable.at.getTranslateX()/scale,
                -Point2DSerializable.at.getTranslateY()/scale,rp.getWidth()/scale,rp.getHeight()/scale));
        rect.transform(at);
        g2d.setPaint(Color.RED);
        g2d.draw(rect);
    }
    public synchronized static final void drawPreferencedShapes(Graphics2D g2d) {
        Graphics2D old_g2d = null;
        need_repaint=false;
        if (System.currentTimeMillis() > delay + 200) {// если задержка прошла
            if (not_moved == false) {
                need_repaint = true;
            }
            not_moved = true;
        }
        else {
            need_repaint = true;
            not_moved = false;
        }
        if(redrawAll){
            redrawAll=false;
            need_repaint=true;
        }
//        double set_shapes_time = System.currentTimeMillis();
//        double start=set_shapes_time;
        setShapes();
//        set_shapes_time=System.currentTimeMillis()-set_shapes_time;
        if (off_line_content == null ||
                off_line_content.getWidth(rp) != rp.getWidth() ||
                off_line_content.getHeight(rp) != rp.getHeight()) {
            off_line_content = new BufferedImage(rp.getWidth(), rp.getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR_PRE);
            clear.setFrame(0, 0, rp.getWidth(), rp.getHeight());
            need_repaint = true;
            g2d_content = (Graphics2D) off_line_content.getGraphics();

            System.out.println("content setted");
            setCenter();
        }
        g2d_content.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                rp.getAntialiasingValue());
        if (need_repaint) {
            isOnPaint = true;
            if (not_moved) {
                old_g2d = g2d;
                g2d = g2d_content;
            }
            g2d.setPaint(Color.white);
            g2d.fill(clear);
            if ((draw_pic||mode==PIC_MOVE_MODE)&&edit_mode) {
                Point2DSerializable p_o=new Point2DSerializable(pic_offset,false);
                int w = (int) (map_width * scale);
                int h = (int) (map_heigth * scale);
                g2d.drawImage(map, (int) (p_o.getX()),
                        (int) (p_o.getY()), w, h, null);
            }
            drawGrid(g2d);
            int i=0;
            if(not_moved){// full repaint
                g2d.setPaint(layerColors[LAYER_OTHER_OBJECTS_1_BORDER_FILL]);
                g2d.setStroke(layerStroke[LAYER_OTHER_OBJECTS_1_BORDER_FILL]);
                g2d.draw(layerPaths[LAYER_OTHER_OBJECTS_1_BORDER_FILL]);
                for(i=LAYER_OTHER_OBJECTS_3_CITY_FILL;i<LAYER_OTHER_OBJECTS_6_POND_DRAW;i+=2){
                    g2d.setPaint(layerColors[i]);
                    g2d.setStroke(layerStroke[i]);
                    g2d.fill(layerPaths[i]);
                    g2d.setPaint(layerColors[i+1]);
                    g2d.setStroke(layerStroke[i+1]);
                    g2d.draw(layerPaths[i+1]);
                }
                for (i=LAYER_ROAD_1_EARTH_OUTLINE; i <=LAYER_ROAD_8_HIWAY_INLINE; i++) {//path layers
                    g2d.setPaint(layerColors[i]);
                    g2d.setStroke(layerStroke[i]);
                    g2d.draw(layerPaths[i]);
                }
                if(edit_mode){
                    g2d.setPaint(layerColors[LAYER_POINTS_1_CTRL_FILL]);
                    g2d.setStroke(layerStroke[LAYER_POINTS_1_CTRL_FILL]);
                    g2d.fill(layerPaths[LAYER_POINTS_1_CTRL_FILL]);

                    g2d.setPaint(layerColors[LAYER_POINTS_2_CTRL_OUTLINE]);
                    g2d.setStroke(layerStroke[LAYER_POINTS_2_CTRL_OUTLINE]);
                    g2d.draw(layerPaths[LAYER_POINTS_2_CTRL_OUTLINE]);
                }

                g2d.setPaint(layerColors[LAYER_POINTS_1_ADD_FILL]);
                g2d.setStroke(layerStroke[LAYER_POINTS_1_ADD_FILL]);
                g2d.fill(layerPaths[LAYER_POINTS_1_ADD_FILL]);

                g2d.setPaint(layerColors[LAYER_POINTS_2_ADD_OUTLINE]);
                g2d.setStroke(layerStroke[LAYER_POINTS_2_ADD_OUTLINE]);
                g2d.draw(layerPaths[LAYER_POINTS_2_ADD_OUTLINE]);

                for(i=curr_label_visibility;i<=LAYER_LABEL_10_HI_IMPORTANCE_TEXT;i+=2){//labels
                    g2d.setPaint(layerColors[i]);
                    g2d.setStroke(layerStroke[i]);
                    g2d.fill(layerPaths[i]);
                    g2d.setPaint(layerColors[i+1]);
                    g2d.setStroke(layerStroke[i+1]);
                    g2d.fill(layerPaths[i+1]);
                }
                g2d.setPaint(layerColors[LAYER_OTHER_OBJECTS_7_LABEL]);
                g2d.setStroke(layerStroke[LAYER_OTHER_OBJECTS_7_LABEL]);
                g2d.fill(layerPaths[LAYER_OTHER_OBJECTS_7_LABEL]);
                g2d.setPaint(Color.DARK_GRAY);
                g2d.draw(layerPaths[LAYER_OTHER_OBJECTS_7_LABEL]);
            }else{//speed repaint on frame move
                g2d.setPaint(Color.BLUE);
                g2d.setStroke(solid);
                g2d.draw(layerPaths[LAYER_ROAD_7_HIWAY_OUTLINE]);
                g2d.draw(layerPaths[LAYER_OTHER_OBJECTS_6_POND_DRAW]);
            }
            if (alwaysRepaint == false) {
                repaintOn = false;
            }
            g2d.setPaint(Color.RED);
            if (not_moved) {
                g2d = old_g2d;
            }
        }
        if (not_moved || (on_hold&&repaint_anywhere==false)) {
            g2d.drawImage(off_line_content, 0, 0, null);
        }
//        g2d.setPaint(Color.RED);
//        g2d.drawString("render time=" + (System.currentTimeMillis() - start), 20,
//                30);
//        g2d.drawString("set time=" + set_shapes_time, 20, 60);
        repaint_anywhere=false;
        isOnPaint = false;
    }
    public static synchronized void setFrameOffset(double x,double y){
        Point2DSerializable.setToTransform(x,y);
        repaintOn = true;
        delay = System.currentTimeMillis();
    }
    public static synchronized void moveFrameOffset(Point2DSerializable mo) {
        if (on_hold) {
            return;
        }
        if (mode == EDIT_MODE) {
            AffineTransform at=(AffineTransform)Point2DSerializable.at.clone();
            at.translate(-mo.getX(),-mo.getY());
            if(checkInsideFrame(at)){
                Point2DSerializable.translate(-mo.getX(),-mo.getY());
            }else{
                return;
            }
        }
        else if(mode == PIC_MOVE_MODE){
            pic_offset.setLocation(pic_offset.getX() - mo.getX()  ,
                    pic_offset.getY() - mo.getY()  );
        }else if(mode==MAP_MOVE_MODE){
            Point2DSerializable p=new Point2DSerializable(-mo.getX(),-mo.getY(),true);
            p.setCoordsAsAbs();
            for(int i=0;i<renderedObjects.size();i++){
                ((PreferencedShapeInterface)renderedObjects.get(i)).translate(p);
            }
        }else{
            throw new Error("неправильный mode "+mode);
        }
        repaintOn = true;
        delay = System.currentTimeMillis();
    }
    private static boolean checkInsideFrame(AffineTransform at){
        double[] pntsMap=new double[]{0,0,gridWidth,0,gridWidth,gridHeigth,0,gridHeigth};
        double[] pntsInside=new double[]{50,50,rp.getWidth()-50,50,rp.getWidth()-50,rp.getHeight()-50,50,rp.getHeight()-50};
        at.transform(pntsMap,0,pntsMap,0,4);
        Rectangle2D insideRectangle=new Rectangle2D.Double(pntsInside[0],pntsInside[1],pntsInside[2]-pntsInside[0],pntsInside[7]-pntsInside[1]);
        Rectangle2D mapRectangle=new Rectangle2D.Double(pntsMap[0],pntsMap[1],pntsMap[2]-pntsMap[0],pntsMap[7]-pntsMap[1]);
        return insideRectangle.intersects(mapRectangle);
    }
    public synchronized static void setScale(double ns){/*, Point2DSerializable position) {*/
        if (on_hold) {
            return;
        }
        if ((ns >= 0.1||ns>scale) &&(ns <= 10||ns<scale)) {
            if(ns<.2){
                setLabelVisibility(LAYER_LABEL_9_HI_IMPORTANCE_FILL);
            }else if(ns<0.4){
                setLabelVisibility(LAYER_LABEL_7_MEDIUM_IMPORTANCE_FILL);
            }else if(ns<0.7){
                setLabelVisibility(LAYER_LABEL_5_NORMAL_IMPORTANCE_FILL);
            }else{
                setLabelVisibility(LAYER_LABEL_3_LOW_IMPORTANCE_FILL);
            }
            AffineTransform oldAt=(AffineTransform)Point2DSerializable.at.clone();
            double oldScale=scale;
            scale = ns;
            delay = System.currentTimeMillis();
            Point2DSerializable.scale(ns);
            if(checkInsideFrame(Point2DSerializable.at)==false){
                Point2DSerializable.at.setTransform(oldAt);
                scale=oldScale;
            }else{
                repaintOn = true;
            }
        }
    }
    public static void setCenter() {
        center.setLocation(RoadPanel.getFrameSize().getWidth() / 2
                , RoadPanel.getFrameSize().getHeight() / 2);
    }
    public void run() {
        while (true) {
            if (isOnPaint == false && paintOn == true && on_hold == false) {
                rp.repaint();
                for(int i=0;i<repaint_components.size();i++){
                    ((JComponent)repaint_components.get(i)).repaint();
                }
            }
            try {
                if (on_hold) {
                    t.sleep(1000);
                }
                else {
                    t.sleep(100);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                e.printStackTrace(DBService.getErrorStream());
            }
        }
    }
    public static final void allwaysRepaintOn() {
        alwaysRepaint = true;
        repaintOn = true;
    }
    public static final void allwaysRepaintOff() {
        alwaysRepaint = false;
    }
    public static void repaint() {
        repaintOn=true;
        if (rp != null){
            rp.repaint();
        }
    }
    public static void repaint_anywhere(){
        repaint_anywhere = true;
        redrawAll=true;
        repaint();
    }
    private static void drawGrid(Graphics2D g2d)  {
        p1.setLocation(0.0d, 0.0d).cnvAbsToFrm();
        p2.setLocation(gridWidth, 0.0d).cnvAbsToFrm();
        p3.setLocation(gridWidth, gridHeigth).cnvAbsToFrm();
        p4.setLocation(0.0d, gridHeigth).cnvAbsToFrm();
        sVertLine.setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        sHorLine.setLine(p2.getX(), p2.getY(), p3.getX(), p3.getY());
        eVertLine.setLine(p3.getX(), p3.getY(), p4.getX(), p4.getY());
        eHorLine.setLine(p4.getX(), p4.getY(), p1.getX(), p1.getY());
        g2d.setStroke(solid);
        g2d.setPaint(Color.GRAY);
        g2d.draw(sVertLine);
        g2d.draw(sHorLine);
        g2d.draw(eVertLine);
        g2d.draw(eHorLine);
        g2d.setStroke(strSolid);
        for (int i = 0; i < gridHeigth; i += 100) {
            p1.setLocation(0.0d, 0.0d + i).cnvAbsToFrm();
            p2.setLocation(gridWidth, 0.0d + i).cnvAbsToFrm();
            sVertLine.setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            g2d.draw(sVertLine);
        }
        for (int i = 0; i < gridWidth; i += 100) {
            p1.setLocation(0.0d + i, 0.0d).cnvAbsToFrm();
            p2.setLocation(0.0d + i, gridHeigth).cnvAbsToFrm();
            sHorLine.setLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            g2d.draw(sHorLine);
        }
    }
    public static void outHold() {
        on_hold = false;
        SelectedObject.setBlink();
        System.out.println("REPAINT OUT HOLD");
        repaint();
    }
    public static void onHold() {
        on_hold = true;
        SelectedObject.setNoBlink();
        System.out.println("REPAINT ON HOLD");
    }
    public static void paintOff() {
        paintOn = false;
    }
    public static void paintOn() {
        paintOn = true;
    }
    public static void draw_picture(boolean b) {
        draw_pic = b;
        repaint();
    }
    public static void setMode(int mod) {
        mode = mod;
        if(mode!=EDIT_MODE){
            SelectedObject.setNoBlink();
        }else{
            SelectedObject.setBlink();
        }
        repaint();
    }
    public static void setEditMode(boolean b){
        edit_mode=b;
        repaint();
    }
    public static boolean isEditMode(){
        return edit_mode;
    }
    public static int getMode() {
        return mode;
    }
    public static void setPic_offset(Point2DSerializable pic_offset) {
        PSRender.pic_offset = pic_offset;
    }
    public static Point2DSerializable getPic_offset() {
        return pic_offset;
    }
    public static void setMap_heigth(double map_heigth) {
        PSRender.map_heigth = map_heigth;
    }
    public static void setMap_width(double map_width) {
        PSRender.map_width = map_width;
    }
    public static void setGridWidth(double gridWidth) {
        PSRender.gridWidth = gridWidth;
        MapViewPanel.resize();
    }
    public static void setGridHeigth(double gridHeigth) {
        PSRender.gridHeigth = gridHeigth;
        MapViewPanel.resize();
    }
    public static double getMap_width() {
        return map_width;
    }
    public static double getMap_heigth() {
        return map_heigth;
    }
    public static double getGridHeigth() {
        return gridHeigth;
    }
    public static double getGridWidth() {
        return gridWidth;
    }
    public static boolean isDraw_pic() {
        return draw_pic;
    }
    public static void setLabelVisibility(int layer_visible){
        curr_label_visibility=layer_visible;
    }
    public static void moveToTop(RoadContainer rc){
        moveToTop(rc.getBounds(),0.8);
    }
    public static void moveToTop(Rectangle2D rect,double ratio){
        double s;
        if(rect.getHeight()>rect.getWidth()){
            s=(rp.getHeight()*ratio)/rect.getHeight();
        }else{
            s=(rp.getWidth()*ratio)/rect.getWidth();
        }
        AffineTransform at=new AffineTransform();
        at.setTransform(s,0.0,0.0,s,0.0,0.0);
        at.translate(-rect.getX(),-rect.getY());
        at.translate((center.getX()/s-(rect.getWidth()*0.5)),
                (center.getY()/s-(rect.getHeight()*0.5)));
        Point2DSerializable.at.setTransform(at);
        scale=at.getScaleX();
        if(scale<.2){
            setLabelVisibility(LAYER_LABEL_9_HI_IMPORTANCE_FILL);
        }else if(scale<0.4){
            setLabelVisibility(LAYER_LABEL_7_MEDIUM_IMPORTANCE_FILL);
        }else if(scale<0.7){
            setLabelVisibility(LAYER_LABEL_5_NORMAL_IMPORTANCE_FILL);
        }else{
            setLabelVisibility(LAYER_LABEL_3_LOW_IMPORTANCE_FILL);
        }
        repaint_anywhere();
    }
    public void mapApplicationActionPerformed(MapApplicationEvent e){
        switch(e.getEventType()){
            case MapApplicationEvent.SET_EDIT_MODE_MAP_MOVE:
                setMode(PSRender.MAP_MOVE_MODE);
                break;
            case MapApplicationEvent.SET_EDIT_MODE_PAINT:
                setMode(PSRender.EDIT_MODE);
                break;
            case MapApplicationEvent.SET_EDIT_MODE_PICTURE_MOVE:
                setMode(PSRender.PIC_MOVE_MODE);
                break;
            case MapApplicationEvent.SET_EDIT_MODE:
                setEditMode(true);
                break;
            case MapApplicationEvent.SET_USER_MODE:
                setEditMode(false);
                break;
            case MapApplicationEvent.SET_PICTURE_VISIBLE:
                draw_picture(true);
                break;
            case MapApplicationEvent.SET_PICTURE_UNVISIBLE:
                draw_picture(false);
                break;
            case MapApplicationEvent.ZOOM_IN:
                setScale(scale * 1.5);
                break;
            case MapApplicationEvent.ZOOM_OUT:
                setScale(scale * 0.5);
                break;
        }
    }
    public static double getScale(){
        return scale;
    }
    public static boolean isRepaintOn(){
        return repaintOn;
    }
    public static ArrayList getRenderedObjects(){
        return renderedObjects;
    }
    public static void clearAllShapes(){
        for(int i=0;i<layerPaths.length;i++){
            layerPaths[i].reset();
            dpLayers[i].clear();
        }
        System.out.println("psrenderer все обьекты удалены");
        repaint_anywhere();
    }
    public static boolean isInFrame(Point2DSerializable absPoint){
        if(absPoint.isFrameCoords()){
            throw new Error("не для фреймовых координат!!!");
        }
        return absPoint.getX()>=0
                &&absPoint.getY()>=0
                &&absPoint.getX()<=gridWidth
                &&absPoint.getY()<=gridHeigth;
    }
}