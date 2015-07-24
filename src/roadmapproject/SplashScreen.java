package roadmapproject;

/**
 * Created by Igor
 * Date: 26.03.2003
 * Time: 23:43:16
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Splash screen demo. */
public class SplashScreen extends Window{

    private String name;

    /** Constructor. */
    public SplashScreen(String name){
        // Create a dummy parent frame
        super(new JFrame());
        this.name=name;
        // Initialize
        init();
    }

    /** Initialize the splash screen. */
    public void init(){

        // Set background
        Color bg=new Color(255,255,150,100);
        setBackground(bg);


        // Create dummy components for the splash screen
        Font fs=new Font("Arial",Font.BOLD|Font.ITALIC,10);
        Font fb=new Font("Arial",Font.BOLD,14);
        Font fi=new Font("Arial",Font.BOLD|Font.ITALIC,14);
        Font fn=new Font("Arial",Font.PLAIN,12);
        JLabel lbl_name=new JLabel(name,JLabel.CENTER);
        lbl_name.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        lbl_name.setFont(fs);
        JLabel lbl1=new JLabel("Road Map V1.0",JLabel.CENTER);
        lbl1.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        lbl1.setFont(fb);
        JLabel lbl2=new JLabel("by Igor & Maxim",JLabel.CENTER);
        lbl2.setBorder(BorderFactory.createEmptyBorder(10,20,0,20));
        lbl2.setFont(fi);
        JLabel lbl3=new JLabel("Created on 26/03/2003",
                JLabel.CENTER);
        lbl3.setFont(fn);

        // Add the components to the window
        add(lbl_name,BorderLayout.NORTH);
        add(lbl1,BorderLayout.CENTER);
        JPanel panel=new JPanel(new GridLayout(2,1));

        panel.add(lbl2,0);
        panel.add(lbl3,1);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        add(panel,BorderLayout.SOUTH);

        // Pack window
        pack();
//        setSize(200,100);
        // Set the location near the center of the screen
        Toolkit toolkit=Toolkit.getDefaultToolkit();
        Dimension dSplash=getPreferredSize();
        Dimension dScreen=toolkit.getScreenSize();
        int x=(dScreen.width/2)-(dSplash.width/2);
        int y=(dScreen.height/2)-(dSplash.height/2);
        setLocation(x,y);
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                setVisible(false);
            }
//            public void mouseExited(MouseEvent e){
//                setVisible(false);
//            }
            public void mouseClicked(MouseEvent e){
                setVisible(false);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                setVisible(false);
            }
        });
        // Show

        setVisible(true);
    }
}