package roadmapproject;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class IFrameSupervisor {
    public static final int LANGUAGE_FRAME=1;
    public static final int PROPERTIES_FRAME=2;
    private static TreeMap frames;
    private static Container content_pane;
    private static int location_x=0,location_y=0;
    public IFrameSupervisor(Container c){
        frames=new TreeMap(new MLComparator());
        content_pane=c;

    }
    public static void showJInternalFrame(MultyLanguage ml,int type){
        if(ml==null){
            System.out.println("ml==null!!!!");
            return;
        }
        JInternalFrame frame=(JInternalFrame)frames.get(ml);
        PSRender.onHold();
        if(frame==null){
            switch(type){
                case PROPERTIES_FRAME:
                    if(ml instanceof Junction){
                        frame=new JunctionDataFrame((Junction)ml);
                    }else if(ml instanceof JunctionLink){
                        frame=new JunctionLinkDataFrame((JunctionLink)ml);
                    }else if(ml instanceof OtherObjects){
                        frame=new LanguageTableFrame(ml);
                    }else{
                        System.out.println(" не определен тип ml"+ml);
                        return;
                    }
                    break;
                case LANGUAGE_FRAME:
                    frame=new LanguageTableFrame(ml);
                    break;
                default:
                    System.out.println("какаято хуйня в ifsupervisor ml="+ml+" type="+type);
                    return;
            }
            frame.setLocation(getLocation());
            frames.put(ml,frame);
            content_pane.add(frame);
            System.out.println("добавлен фрейм для "+ml);
        }else{
            switch(type){
                case PROPERTIES_FRAME:
                    if(frame instanceof LanguageTableFrame){
                        if(ml instanceof Junction){
                            frame=new JunctionDataFrame((Junction)ml);
                        }else if(ml instanceof JunctionLink){
                            frame=new JunctionLinkDataFrame((JunctionLink)ml);
                        }
                        frame.setLocation(getLocation());
                        frames.put(ml,frame);
                        content_pane.add(frame);
                        System.out.println("добавлен фрейм для "+ml);
                    }
                    break;
                case LANGUAGE_FRAME:
                    if(frame instanceof JunctionDataFrame|| frame instanceof JunctionLinkDataFrame){
                        frame=new LanguageTableFrame(ml);
                        frame.setLocation(getLocation());
                        frames.put(ml,frame);
                        content_pane.add(frame);
                        System.out.println("добавлен фрейм для "+ml);
                    }
                    break;
                default:
                    System.out.println("какаято хуйня в ifsupervisor ml="+ml+" type="+type);
                    return;

            }
        }
        try {
            frame.setIcon(false);
            frame.setSelected(true);
            frame.moveToFront();
            frame.pack();
        }catch (java.beans.PropertyVetoException e){
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }
    }
    public static void removeJInternalFrame(MultyLanguage ml){
        //JInternalFrame frame=(JInternalFrame)frames.get(ml);
        frames.remove(ml);
        //content_pane.remove(frame);
        returnFocus();

    }
    public static void returnFocus(){
        if(frames.size()==0){
            PSRender.outHold();
            location_x=30;
            location_y=30;
            RoadPanel.lastRoadPanel.requestFocus();
        }
    }
    private static Point getLocation(){
        Dimension size=content_pane.getSize();
        if(location_x+150>size.getWidth()){
            location_x=30;
        }else{
            location_x+=30;
        }
        if(location_y+150>size.getHeight()){
            location_y=30;
        }else{
            location_y+=30;
        }
        return new Point(location_x,location_y);
    }
}
class MLComparator implements Comparator{
    public int compare(Object a,Object b){
        String aStr,bStr;
        aStr=((MultyLanguage)a).getCode();
        bStr=((MultyLanguage)b).getCode();
        return aStr.compareTo(bStr);
    }
}
