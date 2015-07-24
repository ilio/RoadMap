package roadmapproject;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.*;

public class FindFrame  {
    private JDialog modalFrame;
    private Junction[] jncs;
    private SteppedComboBox combo;
    private Vector names;

    private FindFrame(Junction[] js) {
        this.jncs=js;

        names = new Vector();
        names.add("select");
        combo = new SteppedComboBox(names);
        JPanel panel=new JPanel(new GridLayout(1,3));
        final JTextField jtf=new JTextField(10);
        jtf.addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e) {
            }
            public void keyPressed(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
                combo.setSelectedItem(getNearName(jtf.getText()));
                combo.repaint();
            }


        });
        JButton go=new JButton("go");
        go.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String selected=(String)combo.getSelectedItem();
                int selected_ind=combo.getSelectedIndex();
                System.out.println(selected_ind+" "+selected);
                if(selected==null){
                    return;
                }
                Point2DSerializable j_center=jncs[selected_ind].getPn();
                PSRender.moveToTop(new Rectangle.Double(j_center.getX(),j_center.getY(),5.0,5.0),0.01);
                modalFrame.dispose();
            }
        });
        panel.add(jtf,0);
        panel.add(combo,1);
        panel.add(go,2);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setNames(jncs);
        modalFrame=new JDialog(MapApplication.getInstance(),"find place",true);
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
        combo.removeAllItems();
        for(int i=0;i<jncs.length;i++){
            combo.insertItemAt(jncs[i].getCurrName(),i);
        }
        Dimension d = combo.getPreferredSize();
        combo.setPreferredSize(new Dimension(150, d.height));
        combo.setPopupWidth(d.width);
        combo.setSelectedItem(getNearName(""));
        combo.repaint();
    }

    public static void createFindFrame(Junction[] jncs){
        new FindFrame(jncs);
    }
}
