package roadmapproject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MapApplication extends JFrame
        implements ActionListener,MapApplicationListener {

    private static MapApplication ma;
    private static JDesktopPane desktop;
    private int mode=MapApplicationEvent.SET_EDIT_MODE;
    private int picture_visibility=MapApplicationEvent.SET_PICTURE_UNVISIBLE;
    private int edit_mode=MapApplicationEvent.SET_EDIT_MODE_PAINT;
    private MapApplicationListener listener=null;
    private RoadPanel rp;
    private MapViewPanel mvp;
    private JMenuBar jMenuBar;

    JMenu menuFile;
    JMenuItem menuItemNew;
    JMenuItem menuItemSave;
    JMenuItem menuItemOpen;
    JMenuItem menuItemLoadImgMap;
    JMenuItem menuItemExit;

    JMenu menuEdit;
    JMenuItem menuItemCheck;
    JMenuItem menuItemFindPlace;
    JMenuItem menuItemFindOptimalTraceFrame;
    JMenuItem menuItemCanvasSizes;
    JMenuItem menuItemSetPassword;
    JMenuItem menuItemAntialiasing;

    JMenu menuLanguage;
    JMenuItem menuItemEng;
    JMenuItem menuItemRus;
    JMenuItem menuItemHeb;

    JMenu menuHelp;
    JMenuItem menuItemHelp;
    JMenuItem menuItemAbout;

    private JToolBar jToolBar = new JToolBar();

    private ImageIcon iconOpenUp;
    private ImageIcon iconOpenDown;

    private ImageIcon iconSaveUp;
    private ImageIcon iconSaveDown;

    private ImageIcon iconPrintUp;
    private ImageIcon iconPrintDown;

    private ImageIcon iconZoomInUp;
    private ImageIcon iconZoomInDown;
    private ImageIcon iconZoomOutUp;
    private ImageIcon iconZoomOutDown;

    private ImageIcon iconEditModeUp;
    private ImageIcon iconEditModeDn;

    private ImageIcon iconUserModeUp;
    private ImageIcon iconUserModeDn;

    private ImageIcon iconEnEn;
    private ImageIcon iconEnDs;

    private ImageIcon iconRuEn;
    private ImageIcon iconRuDs;

    private ImageIcon iconHeEn;
    private ImageIcon iconHeDs;

    private ImageIcon icon_edit_mode_up;
    private ImageIcon icon_edit_mode_dn;

    private ImageIcon icon_pic_move_mode_up;
    private ImageIcon icon_pic_move_mode_dn;

    private ImageIcon icon_map_move_mode_up;
    private ImageIcon icon_map_move_mode_dn;

    private ImageIcon icon_pic_visible_up;
    private ImageIcon icon_pic_visible_dn;
    private ImageIcon icon_pic_unvisible_up;
    private ImageIcon icon_pic_unvisible_dn;

    public static JLabel statusBar = new JLabel();
    private BorderLayout borderLayout1 = new BorderLayout();

    private JButton bOpen = new JButton();
    private JButton bSave = new JButton();
    private JButton bZoomIn = new JButton();
    private JButton bZoomOut = new JButton();
    private JButton bMode = new JButton();
    private JButton bPrint = new JButton();
    private JButton bEditMode = new JButton();
    private JButton bPicVisiblity = new JButton();
    private JButton okButton;
    private JButton cancelButton;
    private JDialog dialogSizes;
    private JTextField pic_width_field;
    private JTextField pic_heigth_field;
    private JTextField map_width_field;
    private JTextField map_heigth_field;


    public MapApplication(){
        ma=this;
    }
    public void init(){
        super.setSize(new Dimension(800, 600));
        super.setLocation(0, 0);
        addMapApplicationListener(this);
//                                     Toolkit.getDefaultToolkit().getImage("");
        iconOpenUp = new ImageIcon("images\\open_up.jpg");
        iconOpenDown = new ImageIcon("images\\open_down.jpg");

        iconSaveUp = new ImageIcon("images\\save_up.jpg");
        iconSaveDown = new ImageIcon("images\\save_down.jpg");

        iconZoomInUp = new ImageIcon("images\\zoom_in_up.jpg");
        iconZoomInDown = new ImageIcon("images\\zoom_in_down.jpg");

        iconZoomOutUp = new ImageIcon("images\\zoom_out_up.jpg");
        iconZoomOutDown = new ImageIcon("images\\zoom_out_down.jpg");

        iconPrintUp = new ImageIcon("images\\printer_up.jpg");
        iconPrintDown = new ImageIcon("images\\printer_down.jpg");

        iconUserModeUp = new ImageIcon("images\\without_up.jpg");
        iconUserModeDn = new ImageIcon("images\\without_down.jpg");
//
        iconEditModeUp = new ImageIcon("images\\with_up.jpg");
        iconEditModeDn = new ImageIcon("images\\with_down.jpg");
// EDIT MODE
        icon_edit_mode_up = new ImageIcon("images\\edit_mode_up.gif");
        icon_edit_mode_dn = new ImageIcon("images\\edit_mode_dn.gif");
// MAP MOVE MODE
        icon_map_move_mode_up = new ImageIcon("images\\move_map_up.gif");
        icon_map_move_mode_dn = new ImageIcon("images\\move_map_dn.gif");
// PIC MOVE MODE
        icon_pic_move_mode_up = new ImageIcon("images\\move_img_up.gif");
        icon_pic_move_mode_dn = new ImageIcon("images\\move_img_dn.gif");
// PIC VISIBLE
        icon_pic_visible_up = new ImageIcon("images\\map_on_up.jpg");
        icon_pic_visible_dn = new ImageIcon("images\\map_on_down.jpg");
// PIC UNVISIBLE
        icon_pic_unvisible_up = new ImageIcon("images\\no_img_up.gif");
        icon_pic_unvisible_dn = new ImageIcon("images\\no_img_dn.gif");
// EN
        iconEnEn= new ImageIcon("images\\en_en.jpg");
        iconEnDs= new ImageIcon("images\\en_ds.jpg");
// RU
        iconRuEn= new ImageIcon("images\\ru_en.jpg");
        iconRuDs= new ImageIcon("images\\ru_ds.jpg");
// HE
        iconHeEn= new ImageIcon("images\\he_en.jpg");
        iconHeDs= new ImageIcon("images\\he_ds.jpg");

        statusBar.setText("status bar");

        bOpen.setIcon(iconOpenUp);
        bOpen.setPressedIcon(iconOpenDown);
        bOpen.setToolTipText("Open File");
        bOpen.setBorderPainted(false);
        bOpen.setContentAreaFilled(false);
        bOpen.setFocusable(false);
        bOpen.addActionListener(this);

        bSave.setIcon(iconSaveUp);
        bSave.setPressedIcon(iconSaveDown);
        bSave.setToolTipText("Save File");
        bSave.setBorderPainted(false);
        bSave.setContentAreaFilled(false);
        bSave.setFocusable(false);
        bSave.addActionListener(this);

        bZoomIn.setIcon(iconZoomInUp);
        bZoomIn.setPressedIcon(iconZoomInDown);
        bZoomIn.setToolTipText("Zoom in");
        bZoomIn.setBorderPainted(false);
        bZoomIn.setContentAreaFilled(false);
        bZoomIn.setFocusable(false);
        bZoomIn.addActionListener(this);

        bZoomOut.setIcon(iconZoomOutUp);
        bZoomOut.setPressedIcon(iconZoomOutDown);
        bZoomOut.setToolTipText("Zoom out");
        bZoomOut.setBorderPainted(false);
        bZoomOut.setContentAreaFilled(false);
        bZoomOut.setFocusable(false);
        bZoomOut.addActionListener(this);

//        bMode.setIcon(iconEditModeUp);
//        bMode.setPressedIcon(iconEditModeDn);
//        bMode.setToolTipText("Set visiblity control points");
        bMode.setBorderPainted(false);
        bMode.setContentAreaFilled(false);
        bMode.setFocusable(false);
        bMode.addActionListener(this);
//        bCtrl.setActionCommand("change ctrl");

//        bEditMode.setIcon(icon_edit_mode_up);
//        bEditMode.setPressedIcon(icon_edit_mode_dn);
//        bEditMode.setToolTipText("Mode");
        bEditMode.setBorderPainted(false);
        bEditMode.setContentAreaFilled(false);
        bEditMode.setFocusable(false);
        bEditMode.addActionListener(this);


//        bPicVisiblity.setIcon(icon_pic_visible_up);
//        bPicVisiblity.setPressedIcon(icon_pic_visible_dn);
//        bPicVisiblity.setToolTipText("Pic visibility");
        bPicVisiblity.setBorderPainted(false);
        bPicVisiblity.setContentAreaFilled(false);
        bPicVisiblity.setFocusable(false);
        bPicVisiblity.addActionListener(this);

        bPrint.setIcon(iconPrintUp);
        bPrint.setPressedIcon(iconPrintDown);
        bPrint.setToolTipText("Print");
        bPrint.setBorderPainted(false);
        bPrint.setContentAreaFilled(false);
        bPrint.setFocusable(false);
        bPrint.addActionListener(this);

        jToolBar.add(bOpen);
        jToolBar.add(bSave);
        jToolBar.add(bZoomIn);
        jToolBar.add(bZoomOut);
        jToolBar.add(bMode);
        jToolBar.add(bEditMode);
        jToolBar.add(bPicVisiblity);
        jToolBar.add(bPrint);

        jMenuBar = new JMenuBar();

        menuFile = new JMenu("File");
        menuItemNew = new JMenuItem("New");
        menuItemSave = new JMenuItem("Save");
        menuItemOpen = new JMenuItem("Open");
        menuItemLoadImgMap = new JMenuItem("Load image");
        menuItemExit = new JMenuItem("Exit");
        menuItemNew.addActionListener(this);
        menuItemSave.addActionListener(this);
        menuItemOpen.addActionListener(this);
        menuItemLoadImgMap.addActionListener(this);
        menuItemExit.addActionListener(this);
        menuFile.add(menuItemNew);
        menuFile.add(menuItemSave);
        menuFile.add(menuItemOpen);
        menuFile.add(menuItemLoadImgMap);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);

        jMenuBar.add(menuFile);

        menuEdit = new JMenu("Edit");
        menuItemCheck = new JMenuItem("Check");
        menuItemCheck.addActionListener(this);
        menuItemFindPlace=new JMenuItem("Find place");
        menuItemFindPlace.addActionListener(this);
        menuItemFindOptimalTraceFrame=new JMenuItem("Find optimal trace");
        menuItemFindOptimalTraceFrame.addActionListener(this);
        menuItemCanvasSizes = new JMenuItem("Sizes");
        menuItemCanvasSizes.addActionListener(this);
        menuItemSetPassword = new JMenuItem("Set password");
        menuItemSetPassword.addActionListener(this);
        menuItemAntialiasing=new JMenuItem("Disable antialiasing");
        menuItemAntialiasing.addActionListener(this);

        menuEdit.add(menuItemCheck);
        menuEdit.add(menuItemFindPlace);
        menuEdit.add(menuItemFindOptimalTraceFrame);
        menuEdit.add(menuItemAntialiasing);
        jMenuBar.add(menuEdit);

        menuLanguage = new JMenu("Language");
        menuItemEng = new JMenuItem("English",iconEnDs);
        menuItemRus = new JMenuItem("Russian",iconRuDs);
        menuItemHeb = new JMenuItem("Hebrew",iconHeDs);
        menuLanguage.add(menuItemEng);
        menuLanguage.add(menuItemRus);
        menuLanguage.add(menuItemHeb);
        menuItemEng.addActionListener(this);
        menuItemRus.addActionListener(this);
        menuItemHeb.addActionListener(this);
        jMenuBar.add(menuLanguage);

        menuHelp=new JMenu("Help");
        menuItemHelp=new JMenuItem("Help");
        menuItemAbout=new JMenuItem("About");
        menuHelp.add(menuItemHelp);
        menuHelp.add(menuItemAbout);
        menuItemHelp.addActionListener(this);
        menuItemAbout.addActionListener(this);
        jMenuBar.add(menuHelp);

        desktop = new JDesktopPane(); //a specialized layered pane
        setContentPane(desktop);
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        rp = new RoadPanel();
        mvp=new MapViewPanel();
        InfoPanel ip=new InfoPanel();
        TracePanel tp=new TracePanel();
        JPanel east_panel=new JPanel(null);//(new GridLayout(5,1));
        east_panel.setPreferredSize(new Dimension(220,600));
        mvp.setBounds(2,0,215,230);
        ip.setBounds(2,230,215,70);
        tp.setBounds(2,300,215,290);
        east_panel.add(mvp);
        east_panel.add(ip);
        east_panel.add(tp);
        Container c = getContentPane();

        c.setLayout(borderLayout1);
        c.add(jToolBar, BorderLayout.NORTH);
        c.add(statusBar, BorderLayout.SOUTH);
        setJMenuBar(jMenuBar);
        c.add(rp, BorderLayout.CENTER);
        c.add(east_panel,BorderLayout.EAST);
        rp.setPreferredSize(new Dimension(600, 600));
//        mvp.setPreferredSize(new Dimension(150,600));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dSplash = getPreferredSize();
        Dimension dScreen = toolkit.getScreenSize();
        int x = (dScreen.width / 2) - (dSplash.width / 2);
        int y = 0;//(dScreen.height / 2) - (dSplash.height / 2);
        setLocation(x, y);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitProgramm();
            }
        });
        processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE));
//        processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_ENGLISH_LANGUAGE));
        processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_PICTURE_UNVISIBLE));
        processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE_PAINT));

        okButton=new JButton("Ok");
        cancelButton=new JButton("Cancel");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    PSRender.setGridHeigth(Double.parseDouble(map_heigth_field.getText())>0
                            ?Double.parseDouble(map_heigth_field.getText())
                            :PSRender.getGridHeigth());
                    PSRender.setGridWidth(Double.parseDouble(map_width_field.getText())>0
                            ?Double.parseDouble(map_width_field.getText())
                            :PSRender.getGridWidth());
                    PSRender.setMap_heigth(Double.parseDouble(pic_heigth_field.getText())>0
                            ?Double.parseDouble(pic_heigth_field.getText())
                            :PSRender.getMap_heigth());
                    PSRender.setMap_width(Double.parseDouble(pic_width_field.getText())>0
                            ?Double.parseDouble(pic_width_field.getText())
                            :PSRender.getMap_width());
                    PSRender.repaint();
                    MapViewPanel.resize();
                    dialogSizes.dispose();
                } catch(NumberFormatException e1){
                    JOptionPane.showMessageDialog(ma,"Number is not correct","Error",JOptionPane.OK_OPTION);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                PSRender.setGridHeigth(PSRender.getGridHeigth());
                PSRender.setGridWidth(PSRender.getGridWidth());
                PSRender.setMap_heigth(PSRender.getMap_heigth());
                PSRender.setMap_width(PSRender.getGridWidth());
                PSRender.repaint();
                MapViewPanel.resize();
                dialogSizes.dispose();
            }
        });
    }
    private void exitProgramm(){
        int ans;
        if(mode==MapApplicationEvent.SET_EDIT_MODE){
            ans=JOptionPane.showConfirmDialog(desktop
                    , "save before exit?"
                    , "save confirmation"
                    ,
                    JOptionPane.
                    YES_NO_CANCEL_OPTION);
            if (ans == 0) {
//                        rp.saveDB(true);
                savePreferences(true);
            }
        }else{
            ans=0;
        }
        if (ans != 2) {
            dispose();
            System.exit(0);
        }
    }
    public void actionPerformed(ActionEvent e) {
        if (menuItemSave == e.getSource() || bSave == e.getSource()) {
            savePreferences(false);
        }else if (menuItemOpen == e.getSource() || bOpen == e.getSource()) {
            loadPreferences();
        }else if(menuItemNew==e.getSource()){
            int ans;
            DBService.setPassword("");
            if(mode==MapApplicationEvent.SET_EDIT_MODE){
                ans=JOptionPane.showConfirmDialog(desktop
                        , "save before new?"
                        , "save confirmation"
                        ,
                        JOptionPane.
                        YES_NO_CANCEL_OPTION);
                if (ans == 0) {
                    savePreferences(false);
                }
            }else{
                ans=0;
                processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE));
            }
            if (ans != 2) {
                rp.deleteAll();
                UserPopupListener.resetRoad();
                createSizeDialog();
                Point2DSerializable.at.setTransform(AffineTransform.getTranslateInstance(0.0,0.0));
            }

        }else if(menuItemLoadImgMap==e.getSource()){
            loadPicture();
        }else if(menuItemExit==e.getSource()){
             exitProgramm();
        }else if(menuItemCheck==e.getSource()){
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.CHECK_FOR_WARNINGS));
//            rp.find_no_lenght();
        }else if(menuItemFindPlace==e.getSource()){
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.FIND_PLACE));
        }else if(menuItemFindOptimalTraceFrame==e.getSource()){
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.TRACE_OPTIMAL_ROUTE));
        }else if(menuItemCanvasSizes==e.getSource()){
            createSizeDialog();
        }else if(menuItemSetPassword==e.getSource()){
            new PasswordFrame("",true);
        }else if(menuItemAntialiasing==e.getSource()){
            if(rp.getAntialiasingValue().equals(RenderingHints.VALUE_ANTIALIAS_ON)){
                menuItemAntialiasing.setText("Enable antialiasing");
                rp.setAntialiasingValue(RenderingHints.VALUE_ANTIALIAS_OFF);
            }else{
                menuItemAntialiasing.setText("Disable antialiasing");
                rp.setAntialiasingValue(RenderingHints.VALUE_ANTIALIAS_ON);
            }
        }else if (menuItemEng == e.getSource()) {
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_ENGLISH_LANGUAGE));
        }else if (menuItemRus == e.getSource()) {
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_RUSSIAN_LANGUAGE));
        }else if (menuItemHeb == e.getSource()) {
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_HEBREW_LANGUAGE));
        }else if (bZoomIn == e.getSource()) {
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.ZOOM_IN));
        }else if (bZoomOut == e.getSource()) {
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.ZOOM_OUT));
        }else if (bMode == e.getSource()) {
            switch(mode){
                case MapApplicationEvent.SET_USER_MODE:
                    String pass=DBService.getPassword();
                    if(pass.length()>0){
                        PasswordFrame pf=new PasswordFrame(pass,false);
                        if(pf.isSuccess()==false){
                            return;
                        }
                    }
                    processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE));
                    break;
                case MapApplicationEvent.SET_EDIT_MODE:
                    processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_USER_MODE));
                    break;
            }
        }else if (bEditMode == e.getSource()) {
            switch(edit_mode){
                case MapApplicationEvent.SET_EDIT_MODE_PAINT:
                    processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE_PICTURE_MOVE));
                    break;
                case MapApplicationEvent.SET_EDIT_MODE_MAP_MOVE:
                    processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE_PAINT));
                    break;
                case MapApplicationEvent.SET_EDIT_MODE_PICTURE_MOVE:
                    processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.SET_EDIT_MODE_MAP_MOVE));
                    break;
            }
        }else if (bPicVisiblity == e.getSource()) {
            processMapApplicationEvent(new MapApplicationEvent(picture_visibility==MapApplicationEvent.SET_PICTURE_VISIBLE?
                    MapApplicationEvent.SET_PICTURE_UNVISIBLE:MapApplicationEvent.SET_PICTURE_VISIBLE));
        }else if(bPrint==e.getSource()){
            processMapApplicationEvent(new MapApplicationEvent(MapApplicationEvent.PRINT));
        }else if(menuItemHelp==e.getSource()){
            openHelp();
        }else if(menuItemAbout==e.getSource()){
            new SplashScreen("About");
        }
    }
    public void savePreferences(boolean wait) {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")
                +File.separatorChar+"db"));
        fc.setSelectedFile(new File("test.jds"));
        fc.setFileFilter(new FileFilter(){
            public boolean accept(File f){
                if (f.isDirectory()) {
                    return true;
                }
                String extension = Utils.getExtension(f);
                if (extension != null) {
                    if (extension.equals("jds")) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
            public String getDescription(){
                return "map file";
            }
        });
//        System.out.println();
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            DBService.setFileName(file.getPath());
            rp.saveDB(wait);
            System.out.println("Opening: " + file.getPath());
        } else {
            System.out.println("Open command cancelled by user." );
        }
    }


    public void loadPreferences() {
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")
                +File.separatorChar+"db"));
        fc.setFileFilter(new FileFilter(){
            public boolean accept(File f){
                if (f.isDirectory()) {
                    return true;
                }
                String extension = Utils.getExtension(f);
                if (extension != null) {
                    if (extension.equals("jds")) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
            public String getDescription(){
                return "map file";
            }
        });
//        System.out.println();
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            DBService.setFileName(file.getPath());
            rp.loadDB();
            System.out.println("Opening: " + file.getPath());
        } else {
            System.out.println("Open command cancelled by user." );
        }
    }
    public void loadPicture(){
        JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setFileView(new ImageFileView());
        fc.setAccessory(new ImagePreview(fc));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")
                +File.separatorChar+"images"));

        int returnVal = fc.showDialog(this,"Open image");

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            System.out.println("Attaching file: " + file.getPath());
            String url=null;
            url=file.getPath();
            PSRender.createImage(url);
        } else {
            System.out.println("Attachment cancelled by user." );
        }
    }
    public static void addMAListener(MapApplicationListener l){
        if(ma!=null){
            ma.addMapApplicationListener(l);
        }else{
            System.out.println("ma==null!!!");
        }

    }
    public static void removeMAListener(MapApplicationListener l){
        if(ma!=null){
            ma.removeMapApplicationListener(l);
        }else{
            System.out.println("ma==null!!!");
        }
    }
    public static void processMAEvent(MapApplicationEvent e){
        if(ma!=null){
            ma.processMapApplicationEvent(e);
        }else{
            System.out.println("ma==null!!!");
        }
    }
    public void addMapApplicationListener(MapApplicationListener l){
        if (l == null) {
            return;
        }
        listener = ButtonListenerContainer.add(listener,l);
    }
    public void removeMapApplicationListener(MapApplicationListener l){
        if (l == null) {
            return;
        }
        listener = ButtonListenerContainer.remove(listener, l);
    }
    public void processMapApplicationEvent(MapApplicationEvent e) {
        listener.mapApplicationActionPerformed(e);
    }
    public void mapApplicationActionPerformed(MapApplicationEvent e){
        switch(e.getEventType()){
            case MapApplicationEvent.SET_EDIT_MODE:
                bMode.setIcon(iconEditModeUp);
                bMode.setPressedIcon(iconEditModeDn);
                bMode.setToolTipText("Press for user mode");
                bEditMode.setEnabled(true);
                bPicVisiblity.setEnabled(true);
                bSave.setEnabled(true);
                bPrint.setEnabled(false);
                menuFile.add(menuItemSave,2);
                menuFile.add(menuItemLoadImgMap,3);
                menuEdit.add(menuItemCheck);
                menuEdit.add(menuItemCanvasSizes);
                menuEdit.add(menuItemSetPassword);
                menuEdit.remove(menuItemFindOptimalTraceFrame);
                menuEdit.remove(menuItemFindPlace);
                mode=MapApplicationEvent.SET_EDIT_MODE;
                break;
            case MapApplicationEvent.SET_USER_MODE:
                bMode.setIcon(iconUserModeUp);
                bMode.setPressedIcon(iconUserModeDn);
                bMode.setToolTipText("Press for edit mode");
                bEditMode.setEnabled(false);
                bPicVisiblity.setEnabled(false);
                bSave.setEnabled(false);
                bPrint.setEnabled(true);
                menuEdit.add(menuItemFindOptimalTraceFrame);
                menuEdit.add(menuItemFindPlace);
                menuEdit.remove(menuItemCheck);
                menuEdit.remove(menuItemCanvasSizes);
                menuEdit.remove(menuItemSetPassword);
//                menuFile.remove(menuItemNew);
                menuFile.remove(menuItemSave);
                menuFile.remove(menuItemLoadImgMap);
                mode=MapApplicationEvent.SET_USER_MODE;
                break;
            case MapApplicationEvent.SET_ENGLISH_LANGUAGE:
                menuItemEng.setIcon(iconEnEn);
                menuItemRus.setIcon(iconRuDs);
                menuItemHeb.setIcon(iconHeDs);
                break;
            case MapApplicationEvent.SET_HEBREW_LANGUAGE:
                menuItemEng.setIcon(iconEnDs);
                menuItemRus.setIcon(iconRuDs);
                menuItemHeb.setIcon(iconHeEn);
                break;
            case MapApplicationEvent.SET_PICTURE_UNVISIBLE:
                bPicVisiblity.setIcon(icon_pic_unvisible_up);
                bPicVisiblity.setPressedIcon(icon_pic_unvisible_dn);
                bPicVisiblity.setToolTipText("Set picture visible");
                picture_visibility=MapApplicationEvent.SET_PICTURE_UNVISIBLE;
                break;
            case MapApplicationEvent.SET_PICTURE_VISIBLE:
                bPicVisiblity.setIcon(icon_pic_visible_up);
                bPicVisiblity.setPressedIcon(icon_pic_visible_dn);
                bPicVisiblity.setToolTipText("Set picture unvisible");
                picture_visibility=MapApplicationEvent.SET_PICTURE_VISIBLE;
                break;
            case MapApplicationEvent.SET_RUSSIAN_LANGUAGE:
                menuItemEng.setIcon(iconEnDs);
                menuItemRus.setIcon(iconRuEn);
                menuItemHeb.setIcon(iconHeDs);
                break;

            case MapApplicationEvent.SET_EDIT_MODE_PAINT:
                bEditMode.setIcon(icon_edit_mode_up);
                bEditMode.setPressedIcon(icon_edit_mode_dn);
                bEditMode.setToolTipText("Press for picture moving mode");
                edit_mode=MapApplicationEvent.SET_EDIT_MODE_PAINT;
                bMode.setEnabled(true);
                break;
            case MapApplicationEvent.SET_EDIT_MODE_MAP_MOVE:
                bEditMode.setIcon(icon_map_move_mode_up);
                bEditMode.setPressedIcon(icon_map_move_mode_dn);
                bEditMode.setToolTipText("Press for edit mode");
                edit_mode=MapApplicationEvent.SET_EDIT_MODE_MAP_MOVE;
                bMode.setEnabled(false);
                break;
            case MapApplicationEvent.SET_EDIT_MODE_PICTURE_MOVE:
                bEditMode.setIcon(icon_pic_move_mode_up);
                bEditMode.setPressedIcon(icon_pic_move_mode_dn);
                bEditMode.setToolTipText("Press for map moving mode");
                bMode.setEnabled(false);
                edit_mode=MapApplicationEvent.SET_EDIT_MODE_PICTURE_MOVE;
                break;
        }
    }
    public void openHelp(){
        try{
            Runtime.getRuntime().exec("explorer.exe "+System.getProperty("user.dir")
                    +File.separatorChar+"help\\index.htm");
        } catch(IOException e){
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

    }
    public void createSizeDialog(){
        JPanel panelSizes=new JPanel(new GridLayout(2,1,15,15));
        JPanel panel_pic=new JPanel(new GridLayout(2,2));
        JPanel panel_map=new JPanel(new GridLayout(2,2));
        JLabel pic_width_label=new JLabel("Width");
        JLabel pic_heigth_label=new JLabel("Heigth");
        JLabel map_width_label=new JLabel("Width");
        JLabel map_heigth_label=new JLabel("Heigth");

        pic_width_field=new JTextField(PSRender.getMap_width()+"",8);
        pic_heigth_field=new JTextField(PSRender.getMap_heigth()+"",8);
        map_width_field=new JTextField(PSRender.getGridWidth()+"",8);
        map_heigth_field=new JTextField(PSRender.getGridHeigth()+"",8);

        pic_heigth_label.setHorizontalAlignment(JLabel.CENTER);
        pic_width_label.setHorizontalAlignment(JLabel.CENTER);
        map_heigth_label.setHorizontalAlignment(JLabel.CENTER);
        map_width_label.setHorizontalAlignment(JLabel.CENTER);

        panel_pic.add(pic_width_label,0);
        panel_pic.add(pic_width_field,1);
        panel_pic.add(pic_heigth_label,2);
        panel_pic.add(pic_heigth_field,3);

        panel_map.add(map_width_label,0);
        panel_map.add(map_width_field,1);
        panel_map.add(map_heigth_label,2);
        panel_map.add(map_heigth_field,3);

        panel_pic.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10,10,10,10)
                ,BorderFactory.createTitledBorder("Picture sizes")));
        panel_map.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10,10,10,10)
                ,BorderFactory.createTitledBorder("Map sizes")));


        panelSizes.add(panel_pic,0);
        panelSizes.add(panel_map,1);

        Border compoundBorder=BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10,10,10,10)
                ,BorderFactory.createTitledBorder("Sizes"));

        panelSizes.setBorder(compoundBorder);



        JPanel mainPanel=new JPanel(new BorderLayout());
        JPanel buttonsPanel=new JPanel(new GridLayout(5,1,5,5));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10,20,20,10));
        buttonsPanel.add(okButton,0);
        buttonsPanel.add(cancelButton,1);
        mainPanel.add(panelSizes,BorderLayout.CENTER);
        mainPanel.add(buttonsPanel,BorderLayout.EAST);

        dialogSizes=new JDialog(this,"Properties",true);
        dialogSizes.setContentPane(mainPanel);
        dialogSizes.setResizable(false);
        dialogSizes.pack();
        dialogSizes.setLocationRelativeTo(this);
        dialogSizes.setVisible(true);
        PSRender.repaint_anywhere();
        repaint();
    }
    public static JFrame getInstance(){
        return ma;
    }
    public static void main(String args[]) {
        SplashScreen splash=new SplashScreen("Initialising");


//new SplashScreen();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try{
            new MapApplication();
            ma.init();
//            ma.createSizeDialog();
//            frame.s
//            ma.loadPreferences();
        }catch(Exception e){
            System.out.println("б люиме бшкнбкем ейгеоьм!!!!!!!!!!");
            e.printStackTrace();
            e.printStackTrace(DBService.getErrorStream());
        }
        System.out.println("db hash "+DBService.class.getModifiers());
        splash.setVisible(false);
    }

}