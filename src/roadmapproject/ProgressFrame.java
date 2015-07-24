package roadmapproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProgressFrame implements Runnable{
    public final static int DELAY = 50;
    private JDialog modalFrame;
    private JLabel status;
    private JLabel operation;
    private JProgressBar progress;
    private JPanel panel;
    private final ProgressFrameInterface observable;
    private Timer timer;
    Thread t;
    // private boolean isDead=false;
    public ProgressFrame(ProgressFrameInterface obs) {

//        setVisible(true);
        this.observable=obs;
        panel=new JPanel(new GridLayout(3,1));
        panel.setPreferredSize(new Dimension(250,100));

        progress = new JProgressBar(0,100);
        progress.setValue(0);
        progress.setStringPainted(true);
        progress.setBorder(BorderFactory.createEmptyBorder(5,30,5,30));
//        progress.setBounds(10,10,180,10);

        operation=new JLabel(observable.getCurrentOperation());
        operation.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
//        operation.setBounds(10,30,180,20);

        status=new JLabel(observable.getProgressTitle());
        status.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
//        status.setBounds(10,50,180,20);

        panel.add(progress,0);
        panel.add(operation,1);
        panel.add(status,2);

        timer = new Timer(DELAY, new TimerActionListener());
        t=new Thread(this);
        t.start();

    }
    public void run(){

        RoadPanel.lastRoadPanel.requestFocus();
        modalFrame=new JDialog(MapApplication.getInstance(),"progress 0%",true);
        modalFrame.setContentPane(panel);
        modalFrame.pack();
        modalFrame.setLocationRelativeTo(MapApplication.getInstance());
        timer.start();
        modalFrame.setVisible(true);
        System.out.println("FRAME THREAD is runned");

        System.out.println("FRAME THREAD is run out");

    }

    private class TimerActionListener implements ActionListener{
        public void actionPerformed(ActionEvent evt) {
            if (observable.done()) {
                modalFrame.setTitle("Complete");
                operation.setText("Success");
                status.setText("Done");
                progress.setValue(100);
//                modalFrame.repaint();
                Toolkit.getDefaultToolkit().beep();
                /*try {
                t.sleep(2000);
                }
                catch (InterruptedException ex) {
                ex.printStackTrace();
                }*/
                timer.stop();
                IFrameSupervisor.returnFocus();
                modalFrame.dispose();
            }else{
//                System.out.println("progress "+observable.getProgressStatus());
                progress.setValue(observable.getProgressStatus());
                modalFrame.setTitle("progress " + observable.getProgressStatus() + "%");
                operation.setText(observable.getCurrentOperation());
                status.setText(observable.getProgressTitle());
//                modalFrame.repaint();
            }
        }
    }
}