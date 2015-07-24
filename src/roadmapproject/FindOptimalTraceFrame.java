package roadmapproject;

import javax.swing.*;
import java.util.Vector;
import java.util.Arrays;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Igor
 * Date: 22.02.2003
 * Time: 18:46:11
 */
public class FindOptimalTraceFrame {
    private JDialog modalFrame;
    private Junction[] jncs;
    private static FindOptimalTraceFrame instance;
    private SteppedComboBox combo_start;
    private SteppedComboBox combo_end;
    private Vector names;

    private FindOptimalTraceFrame(Junction[] js) {
        this.jncs=js;
        names = new Vector();
        names.add("select");
        combo_start = new SteppedComboBox(names);
        combo_end=new SteppedComboBox(names);
        JPanel panel=new JPanel(new GridLayout(1,3));
        JButton go=new JButton("Trace");
        go.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int start_ind=combo_start.getSelectedIndex();
                int end_ind=combo_end.getSelectedIndex();
                if(start_ind==end_ind){
                    return;
                }
                RoadTracer rc=new RoadTracer();
                rc.setEndJunction(jncs[end_ind]);
                rc.trace(new RoadContainer(jncs[start_ind]));
                UserPopupListener.resetRoad();
                UserPopupListener.optTrace=rc.getOptimalTrace();
                PSRender.moveToTop(UserPopupListener.optTrace);
                modalFrame.dispose();
            }
        });
        panel.add(combo_start,0);
        panel.add(combo_end,1);
        panel.add(go,2);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setNames(jncs);
        modalFrame=new JDialog(MapApplication.getInstance(),"Trace",true);
        modalFrame.setContentPane(panel);
        modalFrame.pack();
        modalFrame.setLocationRelativeTo(MapApplication.getInstance());
        modalFrame.setVisible(true);
    }
    private String getNearName(String name){
                String st="";
                int ind_cheked_char=0;
                int ind_found_str=0;
                int ind_last_found=0;
                if(name.length()==0){
                    return (String)names.get(0);
                }
                do{
                    st+=name.charAt(ind_cheked_char++);
                    for(;ind_found_str<names.size();ind_found_str++){
                        if(st.compareTo(((String)names.get(ind_found_str)).substring(0,(Math.min(st.length(),((String)names.get(ind_last_found)).length()))))==0){
                            ind_last_found=ind_found_str;
                            break;
                        }
                    }
                    if(ind_found_str==names.size()){
                        ind_found_str=ind_last_found;
                        break;
                    }
                }while(st.length()<name.length());
                return (String)names.get(ind_found_str);
            }
    private void setNames(Junction[] jns){
        this.jncs=jns;
        Arrays.sort(jncs);
        combo_start.removeAllItems();
        combo_end.removeAllItems();
        for(int i=0;i<jncs.length;i++){
            combo_start.insertItemAt(jncs[i].getCurrName(),i);
//            combo_end.insertItemAt(jncs[i].getCurrName(),i);
        }
        Dimension d = combo_start.getPreferredSize();
        combo_start.setPreferredSize(new Dimension(150, d.height));
        combo_start.setPopupWidth(d.width);
        combo_start.setSelectedItem(getNearName(""));
        combo_start.repaint();
        d = combo_end.getPreferredSize();
        combo_end.setPreferredSize(new Dimension(150, d.height));
        combo_end.setPopupWidth(d.width);
        combo_end.setSelectedItem(getNearName(""));
        combo_end.repaint();
    }
    public static void createFindFrame(Junction[] jncs){
        new FindOptimalTraceFrame(jncs);
    }
}
