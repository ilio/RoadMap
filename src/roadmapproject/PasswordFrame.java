package roadmapproject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

/**
 * Created by Igor
 * Date: 26.03.2003
 * Time: 17:53:04
 */
public class PasswordFrame{
    private JDialog modalFrame;
    private JPasswordField passwordField;
    private static char[] correctPassword;
    private boolean isSetPassword;
    private JButton okButton;
    private JButton cancelButton;
    private boolean success;
    public PasswordFrame(String password,boolean isSetPassword){
        correctPassword=new char[password.length()];
        password.getChars(0,password.length(),correctPassword,0);
        success=false;
        this.isSetPassword=isSetPassword;
        passwordField=new JPasswordField(10);
        passwordField.setEchoChar('*');
        passwordField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                okButtonClicked(e);
            }
        });
        okButton=new JButton("Ok");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                okButtonClicked(e);
            }
        });
        cancelButton=new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                modalFrame.dispose();
            }
        });
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));
        panel.add(new JLabel("Enter the password: "),BorderLayout.WEST);
        panel.add(passwordField,BorderLayout.CENTER);
        JPanel buttonPanel=new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        panel.add(buttonPanel,BorderLayout.SOUTH);
        modalFrame=new JDialog(MapApplication.getInstance(),"Enter password",true);
        modalFrame.setContentPane(panel);
        modalFrame.pack();
        modalFrame.setLocationRelativeTo(MapApplication.getInstance());
        modalFrame.setVisible(true);
    }
    private void okButtonClicked(ActionEvent e){
        char[] password=passwordField.getPassword();
        if(isSetPassword){
            String pass=new String(password);
            if(pass.length()>16){
                pass=pass.substring(0,16);
            }
            DBService.setPassword(pass);
        }else{
            if(isPasswordCorrect(password)){
                 success=true;
            } else{
                JOptionPane.showMessageDialog(MapApplication.getInstance(),
                        "Invalid password. Try again.",
                        "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        modalFrame.dispose();
    }
    private static boolean isPasswordCorrect(char[] input){
        if(input.length!=correctPassword.length)
            return false;
        for(int i=0;i<input.length;i++)
            if(input[i]!=correctPassword[i])
                return false;
        return true;
    }
    public boolean isSuccess(){
        return success;
    }
}
