package roadmapproject;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Created by Igor
 * Date: 10.03.2003
 * Time: 17:03:35
 */
public class UserPopupListener extends MouseAdapter implements ActionListener,SelectedTypeCONSTANTS{
    private JMenuItem menuItemSetFirstPoint;
    private JMenuItem menuItemSetEndPoint;
    private static Junction startJunc=null,endJunc=null;
    public static FlagShape startFlag=null,endFlag=null;
    static RoadContainer optTrace=null;
    private JPopupMenu popup;
    private Junction selectedJunction;
    private int selectedType;
    public UserPopupListener(){
        popup = new JPopupMenu();
        menuItemSetFirstPoint = new JMenuItem("Set Start point");
        menuItemSetEndPoint = new JMenuItem("Trace road to this point");
        menuItemSetFirstPoint.addActionListener(this);
        menuItemSetEndPoint.addActionListener(this);
    }
    public void actionPerformed(ActionEvent e){
        if(menuItemSetFirstPoint==e.getSource()){
            resetRoad();
            startJunc=selectedJunction;
            startFlag=new FlagShape(startJunc.getPn());
        }else if(menuItemSetEndPoint==e.getSource()){
            endJunc=selectedJunction;
            endFlag=new FlagShape(endJunc.getPn());
            RoadTracer rc=new RoadTracer();
            rc.setEndJunction(endJunc);
            rc.trace(new RoadContainer(startJunc));
            //System.out.println(rc.getOptimalTrace());
            optTrace=rc.getOptimalTrace();
//            PSRender.moveToTop(optTrace);
            startJunc=null;
            endJunc=null;
        }
    }
    public void mousePressed(MouseEvent e) {//PopupListener
        selectedJunction=Global.selectedObject.getJunction();
        selectedType=Global.selectedObject.getType();
        maybeShowPopup(e);
    }
    private void maybeShowPopup(MouseEvent e){
        if (e.isPopupTrigger()){
            switch(selectedType){
                case P1_POINT|END_POINT|JUNCTION_POINT:
                case P2_POINT|END_POINT|JUNCTION_POINT:
                    popup.removeAll();
                    if(startJunc==null){
                        popup.add(menuItemSetFirstPoint);
                    }else{
                        popup.add(menuItemSetEndPoint);
                    }
                    popup.show(e.getComponent(),
                            e.getX(), e.getY());
                    break;
            }
        }
    }
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }
    public static void resetRoad(){
        startJunc=null;
        startFlag=null;
        endJunc=null;
        endFlag=null;
        optTrace=null;
        TracePanel.setTrace(null);
    }
}
