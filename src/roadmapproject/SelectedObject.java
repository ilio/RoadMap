package roadmapproject;

import java.awt.*;
import java.awt.geom.*;

class SelectedObject implements Runnable{
    private Thread t;
    private Object selected;
    private static Shape selectedShape;
    private Point2DSerializable p;
    private static RoadPanel rp;
//  private Rectangle repaintRect;
    private Color selectedColor;
    static Long startTime,endTime;
    static BreakSO bso=new BreakSO();
    private int fpsCounter=0;
    private int junctionCounter=0;
    private static FlagShape currentFlag;
    int colDir;
    static boolean runFlag;

    public SelectedObject(RoadPanel nrp){
        if(rp==null){
            this.rp=nrp;
        }else{
            throw new Error("нет возможности создать 2й SelectedObject");
        }
//    repaintRect=new Rectangle();
        selectedColor=new Color(150,125,0,250);
        colDir=1;
        runFlag=true;

        t=new Thread(this);
        t.start();
    }
    public void run(){
        try{
            while(true){
                if(UserPopupListener.optTrace!=null){
                    if(fpsCounter>10){
                        fpsCounter=0;
                        Junction currentJunction=UserPopupListener.optTrace.get(junctionCounter++);
                        if(InfoPanel.hasText()==false){
                            InfoPanel.setInfoText("#"+(junctionCounter)+" "+currentJunction.getCurrName());
                        }
                        if(junctionCounter>UserPopupListener.optTrace.size()){
                            junctionCounter=0;
                        }
                        currentFlag=new FlagShape(currentJunction.getPn());

                    }
                    fpsCounter++;
                }else{
                    fpsCounter=0;
                    junctionCounter=0;
                    currentFlag=null;
                }

                bso.breakIfNeed();
                if(runFlag&Global.selectedObject!=null){
                    selected=Global.selectedObject.getObject();
                    if(PSRender.isEditMode()==true){
                        if (selected instanceof Point2DSerializable) {
                            p = new Point2DSerializable((Point2DSerializable) selected, false);
                            selectedShape = new Ellipse2D.Double(
                                    p.getX() - 5.0
                                    , p.getY() - 5.0
                                    , 10.0
                                    , 10.0);
                        } else if (selected instanceof QuadCurve2D) {
                            Point2DSerializable p1,ctrl,p2;
                            p1 = new Point2DSerializable(((QuadCurve2D) selected).getP1(), false);
                            ctrl = new Point2DSerializable(((QuadCurve2D) selected).getCtrlPt(), false);
                            p2 = new Point2DSerializable(((QuadCurve2D) selected).getP2(), false);
                            GeneralPath gp=new GeneralPath();
                            gp.moveTo((float)p1.getX(),(float)p1.getY());
                            gp.quadTo((float)ctrl.getX(), (float)ctrl.getY()
                                    , (float)p2.getX(), (float)p2.getY());
//                            gp.transform(Point2DSerializable.at);
                            selectedShape=gp;
                        } else {
                            selectedShape = null;
                        }
                    }else{
                        if (Global.selectedObject.isJunction()) {
                            p = new Point2DSerializable((Point2DSerializable) selected, false);
                            selectedShape = new Ellipse2D.Double(
                                    p.getX() - 5.0//PSRender.scale/2
                                    , p.getY() - 5.0//-PSRender.scale/2
                                    , 10.0//PSRender.scale
                                    , 10.0);//PSRender.scale);
                        } else if(Global.selectedBR!=null){
                            if(Global.selectedBR instanceof JunctionLink){
                                GeneralPath gp=new GeneralPath();
                                JunctionLinkRender jlr=((JunctionLink)Global.selectedBR).getRender();
                                gp.append(jlr.getPath(),false);
                                gp.transform(Point2DSerializable.at);
                                selectedShape=gp;
                            }else if(Global.selectedBR instanceof OtherObjects){
                                selectedShape = null;
                            }else{
                                selectedShape = null;
                            }
                        }else{
                            selectedShape = null;
                        }
                    }
                }else{
                    selectedShape=null;
                    selectedColor=new Color(150,125,0,255);
                }
                Thread.sleep(50);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }catch(Exception e){
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
            t=new Thread(this);
            t.start();
        }
    }

    public void suspendFlash(){
        runFlag=false;
    }
    public void resumeFlash(){
        runFlag=true;
        ///t.run();
    }
    public Shape getSelectedShape(){
        /*if(bso.isStopped){
        return null;
        }*/
        return selectedShape;
    }
    public Color getSelectedColor(){
        if(bso.isStopped){
            return Color.RED;
        }
        int alfa=selectedColor.getAlpha();
        if(alfa+colDir>255){
            colDir=-30;
        }else if(alfa+colDir<0){
            colDir=30;
        }
        alfa+=colDir;
        selectedColor=new Color(150,125,250,alfa);
        return selectedColor;
    }
    public static void setNoBlink(){
        //noBlink=true;
        bso.breakThread();
    }
    public static void setBlink(){
        // noBlink=false;
        //t.notifyAll();
        bso.continueThread();
    }
    public static void setSelectedObject(MultyLanguage ml){
        if(ml instanceof Junction){
            Point2DSerializable pnt=new Point2DSerializable(((Junction)ml).getPn(),false);
            selectedShape=new Ellipse2D.Double(pnt.getX()-5.0,pnt.getY()-5.0,10.0,10.0);
        }else if(ml instanceof JunctionLink){
            JunctionLink jl=(JunctionLink)ml;//p1,p2,ctrl
            GeneralPath gp=new GeneralPath();
            int i;
            Point2DSerializable tmpP1,tmpCtrl,tmpP2;

            Point2DSerializable pointsP2[]=jl.getP2Points();
            Point2DSerializable pointsCtrl[]=jl.getCtrlPoints();
            tmpP1 = new Point2DSerializable(jl.getP1(),false);
            gp.moveTo((float)tmpP1.getX()
                    ,(float)tmpP1.getY());
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
                tmpCtrl = new Point2DSerializable(pointsCtrl[i],false);
                tmpP2 = new Point2DSerializable(pointsP2[i],false);
                gp.quadTo((float)tmpCtrl.getX()
                        ,(float)tmpCtrl.getY()
                        ,(float)tmpP2.getX()
                        ,(float)tmpP2.getY());
            }
            selectedShape=gp;
        }
        PSRender.repaint();
    }
    public static boolean isStoped() {
        return bso.isStopped;
    }
    public static FlagShape getCurrentFlag(){
        return currentFlag;
    }
}
class BreakSO{
    boolean isStopped=false;
    private static long lastTime;
    public BreakSO(){}
    synchronized public void breakIfNeed(){
        if(isStopped==true){
            try{
                System.out.println("поток остановлен через "
                        +(System.currentTimeMillis()-lastTime)
                        +" msec после запроса");
                Global.selectedObject.reset();
                lastTime=System.currentTimeMillis();
                wait();
                System.out.println("поток продолжил выполнятся через "
                        +(System.currentTimeMillis()-lastTime)+" msec");
            }catch(InterruptedException e){
                System.out.println("остановлен интеруптом поток!!! обшибка в BreakSO");
            }
        }
    }
    synchronized public void breakThread(){
        System.out.println("запрос на остановку потока");
        lastTime=System.currentTimeMillis();
        isStopped=true;
    }
    synchronized public void continueThread(){
        isStopped=false;
        notify();
    }

}
