package roadmapproject;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Igor
 * Date: Feb 16, 2003
 * Time: 2:25:25 PM
 */
public class InfoPanel extends JPanel{
    private JTextArea textArea;
    private static InfoPanel instance;
    private static boolean hasText;
    public InfoPanel() {
        instance=this;
        super.setBorder(BorderFactory.createTitledBorder("Info"));
        textArea=new JTextArea(2,18);
        textArea.setEditable(false);
        textArea.setAutoscrolls(false);
        super.setSize(new Dimension(210,50));
        super.add(textArea);
    }
    public static void setInfoText(String text){
        if(instance!=null){
            instance.textArea.setText(text);
            hasText=true;
        }else{
            System.out.println("хуйня INFOPANEL не существует");
            DBService.getErrorStream().println("хуйня INFOPANEL не существует");
        }
    }
    public static void clearText(){
        if(instance!=null){
            setInfoText("Select Object");
            hasText=false;
        }else{
            System.out.println("хуйня INFOPANEL не существует");
            DBService.getErrorStream().println("хуйня INFOPANEL не существует");
        }
    }
    public static boolean hasText(){
        return instance.textArea.getText().startsWith("#")==false&&hasText;
    }
}
