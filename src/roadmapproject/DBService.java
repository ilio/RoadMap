package roadmapproject;

import com.borland.datastore.DataStore;
import com.borland.datastore.TxManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.TreeMap;
public class DBService
        implements ProgressFrameInterface, Runnable,LoadSaveCONTANTS,MapApplicationListener {
    public static final int DB_NO_OPERATIONS = 0;
    public static final int DB_LOAD = 1;
    public static final int DB_SAVE = 2;
    public static final int DB_CHECK = 3;
    public static final int DB_LOAD_LANG=4;
    public static final int DB_FIND_PLACE=5;
    public static final int DB_FIND_OPTIMAL_TRACE=6;
    public static final int DB_DELETE_ALL=7;
    private static int operation = DB_NO_OPERATIONS;
    private static String password="";
    private volatile static ArrayList servicedObjects = new ArrayList();
    private static int progress;
    private static String curr_operation;
    private static String progress_title;
    private static boolean progress_done;
    //private static DBService db_service=new DBService();
//    private static Container content_pane; //=RoadPanel.lastRoadPanel;
    private static Connection connection = null;
    private static int connection_cnt=0;
    private static DBService db_service=null;
    private static String DRIVER = "com.borland.datastore.jdbc.DataStoreDriver";
    private static boolean loading;
    // String DRIVER =   "sun.jdbc.odbc.JdbcOdbcDriver";
    //String DRIVER = "org.gjt.mm.mysql.Driver";

    private static String URL = "jdbc:borland:dslocal:";
    //String URL    =   "jdbc:odbc:";

    private static String DB_FILE = null;
    //String FILE   =   "test" ;
    private static String LOG_FILE="error.log";
    Thread t;
    private static FileOutputStream LOG_FILE_STREAM;
    private static PrintStream LOG_FILE_PRINT_STREAM;
    private static ProgressFrame frame;
    public static final String[] languages={"RUS","ENG","HEB"};
    private static final int LANG_RUS                                        =0;
    private static final int LANG_ENG                                        =1;
    private static final int LANG_HEB                                        =2;
    private static int currLang=LANG_RUS;
    private static ArrayList outlines;
//    private Icon icon=new ImageIcon("images/middle.gif");;
    static{
        outlines=new ArrayList();
        try {
            File log_file=new File(LOG_FILE);
            ByteArrayOutputStream baos;
            if(log_file.exists()==false){
                log_file.createNewFile();
                baos=new ByteArrayOutputStream();
            }else{
                FileInputStream fis=new FileInputStream(log_file);
                baos=new ByteArrayOutputStream(fis.available());
                while(fis.available()>0){
                    baos.write(fis.read());
                }
            }
//            log_file.
            String msg="CREATED ON "+new Date(System.currentTimeMillis())+" "+new Time(System.currentTimeMillis())+"\r\n\r\n";
            baos.write(msg.getBytes());
            LOG_FILE_STREAM=new FileOutputStream(log_file);
            baos.writeTo(LOG_FILE_STREAM);
            baos.close();
            LOG_FILE_PRINT_STREAM = new PrintStream(LOG_FILE_STREAM);
        } catch (FileNotFoundException e) {
            e.printStackTrace();   //To change body of catch statement use Options | File Templates.
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
    }
    public void finalize(){
        try {
            String str="CLOSED ON "+new Date(System.currentTimeMillis())+" "+new Time(System.currentTimeMillis());
            byte[] msg=str.getBytes();
            LOG_FILE_STREAM.write(msg);
            LOG_FILE_STREAM.close();
            LOG_FILE_PRINT_STREAM.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();   //To change body of catch statement use Options | File Templates.
            throwable.printStackTrace(LOG_FILE_PRINT_STREAM);
        }

    }
    public DBService() {
//        content_pane = pane;
        MapApplication.addMAListener(this);
        if(db_service==null){
            db_service = this;
        }
    }
    public synchronized void run() {
        System.out.println("$$$$$$$db run");
        switch (operation) {
            case DB_LOAD:
                loadDBData();
                break;
            case DB_SAVE:
                saveToDB();
                break;
            case DB_CHECK:
                checkObjects();
                break;
            case DB_LOAD_LANG:
                loadLanguage();
                break;
            case DB_FIND_PLACE:
                find_place();
                break;
            case DB_FIND_OPTIMAL_TRACE:
                find_optimal_trace_frame();
                break;
            case DB_DELETE_ALL:
                deleteAll();
                break;
            case DB_NO_OPERATIONS:
                System.out.println("вызов рун без типа операции");
                break;

        }
        System.out.println("^^^^^^db out run");
    }
    public void execute(int operation_code) {
        operation = operation_code;
        t = new Thread(this);
        t.start();
    }
    public static void addToDBService(DBAccess obj) {
        if (servicedObjects.contains(obj)) {
            throw new Error("обьект " + obj + " уже находитьс€ в db service");
        }
        servicedObjects.add(obj);
        System.out.println("size db servised=" + servicedObjects.size() +
                " added obj=" + obj);
    }
    public static void removeFromDBService(DBAccess obj) {
        servicedObjects.remove(obj);
        System.out.println("---DB service удален " + obj + " осталось:" +
                servicedObjects.size());
        if (servicedObjects.size() == 0) {
            System.out.println("#####\n\nDB SERVICE все удалено\n\n#####");
        }
    }
    public static void revalidateObject(DBAccess obj){
        if(servicedObjects.contains(obj)){
            removeFromDBService(obj);
        }
        addToDBService(obj);
    }
    public static void setFile(){
        final JFileChooser fc=new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")
                +File.separatorChar+"db"));
        fc.setSelectedFile(new File("test.jds"));
        fc.setFileFilter(new FileFilter(){
            public boolean accept(File f){
                if(f.isDirectory()){
                    return true;
                }
                String extension=Utils.getExtension(f);
                if(extension!=null){
                    if(extension.equals("jds")){
                        return true;
                    } else{
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
        int returnVal=fc.showSaveDialog(MapApplication.getInstance());
        if(returnVal==JFileChooser.APPROVE_OPTION){
            File file=fc.getSelectedFile();
            setFileName(file.getPath());
            System.out.println(DB_FILE);
        } else{
            DB_FILE=null;
//            неоткрыт
        }
    }
    private synchronized static void connectToDB() {
        DataStore store = new DataStore();
        connection_cnt++;
        if(connection!=null){
            return;
        }


        /**
         * создание файла DB
         */
        try{
            try {
                if(DB_FILE==null){
                    setFile();
                    if(DB_FILE==null){
                        throw new FileNotFoundException("file name=null");
                    }
                }
                store.setUserName("CreateTX");
                store.setTxManager(new TxManager());
                store.setFileName(DB_FILE);
                if (new java.io.File(store.getFileName()).exists() == false) {
                    store.create();
                    if(operation!=DB_SAVE){
                        saveLanguage();
                    }
                }
                else {
                    store.open();
                }
                store.close();
            }catch (com.borland.dx.dataset.DataSetException e) {
                e.printStackTrace();
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
//            catch (Exception e) {
//                e.printStackTrace();
//                e.printStackTrace(LOG_FILE_PRINT_STREAM);
//            }
            /**
             * подключение к DB
             */

            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL + DB_FILE, "user", "");
            checkForWarning(connection.getWarnings());
        }catch (Exception e) {
            System.out.println("CONECTION FAILED");
            if(e instanceof FileNotFoundException==false){
                e.printStackTrace();
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            curr_operation="Connection to Data base is FAILED";
            progress_title="Try later";
            progress_done=true;
            JOptionPane.showMessageDialog(MapApplication.getInstance(),"Connection failed. Try later.\n operation canceled","access denied",JOptionPane.OK_OPTION);
        }
    }
    private synchronized static void closeConnection(){
        connection_cnt--;
        if(connection_cnt == 0){
            try {
                if(connection!=null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();   //To change body of catch statement use Options | File Templates.
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            connection=null;
            PSRender.repaint();
            System.gc();
            curr_operation = "close connection";
            progress = 100;
            progress_done = true;
            frame=null;
        }else{
            System.out.println("cnt connections="+connection_cnt);
        }
    }
    private synchronized void saveToDB() {

        System.out.println("SAVE STARTED");
        curr_operation="Prepare to saving...";
        File old_file=null;
        if(new File(DB_FILE).exists()){
            int indFileName=DB_FILE.lastIndexOf(File.separatorChar);
            String oldFilePath=DB_FILE.substring(0,indFileName+1);
            String oldFileName=DB_FILE.substring(indFileName+1);
            old_file=new File(oldFilePath+"~"+oldFileName);//D:\PROJECTS\java\RoadMapProject\db\small.jds
            try {
                if(old_file.exists()==false){
                    if(old_file.createNewFile()==false){
                        System.out.println("не возможно создать файл");
                    }
                }
                FileOutputStream fos=new FileOutputStream(old_file);
                FileInputStream ios=new FileInputStream(DB_FILE);
                while(ios.available()>0){
                    fos.write(ios.read());
                }
                fos.close();
                ios.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        connectToDB();
        if(connection==null){
            return;
        }
        createProgressFrame("Saving data, please wait... ");
        double progress_double = 0.0;
        double count = servicedObjects.size();
        Object obj; //=new Object();
        Class c;
        String className;
        ArrayList tmpArr = new ArrayList();
        TreeMap map = new TreeMap();
        saveLanguage();
        saveVariables();
        Statement stmt = null;
        curr_operation="open tables";
        try {
            stmt = connection.createStatement();
            try {
                stmt.executeUpdate("DROP TABLE CLASSNAME");
            }
            catch (SQLException e) {
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            stmt.executeUpdate("CREATE TABLE CLASSNAME" +
                    "(CLASS CHAR(64)," +
                    "PRIORITY INT)");
            curr_operation="save classes";
            for (int i = 0; i < servicedObjects.size(); i++) {
                obj = servicedObjects.get(i);

                c = obj.getClass();
                className = c.getName();
                if (map.containsKey(className) == false) {
                    map.put(className, new ArrayList());
                    stmt.executeUpdate("INSERT INTO CLASSNAME VALUES(" +
                            "'" + className + "'," +
                            ( (DBAccess) obj).getLoadPriority() + ")");
                }
                tmpArr = ( (ArrayList) map.get(className));
                tmpArr.add(obj);
            }
            tmpArr = new ArrayList(map.values());

            for (int i = 0; i < tmpArr.size(); i++) {
                ArrayList classList = ( (ArrayList) tmpArr.get(i));
                String tConf[] = ( (DBAccess) classList.get(0)).getTablesConfiguration();
                String tNames[] = ( (DBAccess) classList.get(0)).getTableNames();
                curr_operation="open table for class "+ classList.get(0).getClass();
                for (int j = 0; j < tConf.length; j++) {
                    try {
                        stmt.executeUpdate("DROP TABLE " + tNames[j]);
                    }
                    catch (SQLException e) {
                        e.printStackTrace(LOG_FILE_PRINT_STREAM);
                    }
                }
                for (int j = 0; j < tConf.length; j++) {
                    stmt.executeUpdate("CREATE TABLE " + tNames[j] + tConf[j]);
                }
                DBAccess cont;
                curr_operation="store data";
                for (int j = 0; j < classList.size(); j++) {
                    cont = (DBAccess) classList.get(j);
                    progress_double += 100.0 / count;
                    progress = (int) progress_double;
                    curr_operation = "Save " + cont;
                    if (cont.isDead()) {
                        servicedObjects.remove(cont);
                    }
                    else {
                        double start = System.currentTimeMillis();
                        cont.saveFields(stmt);
                        System.out.println("save time=" +
                                (System.currentTimeMillis() - start) + " obj=" +
                                cont);

                    }
                }
                classList.clear();
                System.out.println("SAVED");
            }
            curr_operation="saved...";
        }
        catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
            if(new File(DB_FILE).exists()){
                int indFileName=DB_FILE.lastIndexOf(File.separatorChar);
                String oldFilePath=DB_FILE.substring(0,indFileName+1);
                String oldFileName=DB_FILE.substring(indFileName+1);
                old_file=new File(oldFilePath+"~"+oldFileName);
                File tmp_file=new File("jds.tmp");
                try {
                    if(old_file.exists()==false){
                        System.out.println("невозможно открыть файл");
                    }else{
                        if(tmp_file.exists()==false){
                            try {
                                tmp_file.createTempFile("~01",".tmp");
                            } catch (Exception e1) {
                                e1.printStackTrace();  //To change body of catch statement use Options | File Templates.
                            }
                        }else{

                        }
                        tmp_file.deleteOnExit();
                        FileOutputStream fos;
                        FileInputStream ios;
                        fos = new FileOutputStream(tmp_file);
                        ios = new FileInputStream(DB_FILE);
                        while(ios.available()>0){
                            fos.write(ios.read());
                        }
                        fos.close();
                        ios.close();
                        fos = new FileOutputStream(DB_FILE);
                        ios = new FileInputStream(old_file);
                        while(ios.available()>0){
                            fos.write(ios.read());
                        }
                        fos.close();
                        ios.close();
                        fos = new FileOutputStream(old_file);
                        ios = new FileInputStream(tmp_file);
                        while(ios.available()>0){
                            fos.write(ios.read());
                        }
                        fos.close();
                        ios.close();
//                    tmp_file.
                        System.out.println("файл был восстановлен");
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    ex.printStackTrace(LOG_FILE_PRINT_STREAM);
                } catch (IOException ex) {
                    ex.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    ex.printStackTrace(LOG_FILE_PRINT_STREAM);
                }catch (Exception ex) {
                    ex.printStackTrace(LOG_FILE_PRINT_STREAM);
                }
            }

        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
            map.clear();
            tmpArr.clear();
            System.gc();
        }
    }
    public static boolean isLoading(){
        return loading;
    }
    private synchronized void loadDBData() {
        connectToDB();
        UserPopupListener.resetRoad();
        if(connection==null){
            return;
        }
        loading=true;
        createProgressFrame("Loading data, please wait... ");
        System.out.println("LOAD STARTED");
        PSRender.onHold();
        loadVariables();
        double progress_double=deleteAll();
        double count;
        //PSRender.repaint();
        curr_operation = "loading";
        System.out.println("~~~~~~~~ DB удаление закончено");
        System.gc();
//        servicedObjects.clear();
        Statement stmt = null;
        Statement statment = null;

        try {
            stmt = connection.createStatement();
            statment = connection.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery("SELECT ELEMENT_COUNT FROM VARIABLES;");

            if (rs.next()) {
                count = rs.getInt("ELEMENT_COUNT");
            }
            else {
                count = 100.0;
            }
            rs = stmt.executeQuery("SELECT CLASS "
                    + "FROM CLASSNAME "
                    + "ORDER BY PRIORITY;");
            String className;
            Object obj;
            DBAccess dbaObj;
            boolean hasNext;
            while (rs.next()) {
                className = rs.getString("CLASS").trim();
                int i = 0;
                try {
                    do {
                        obj = Class.forName(className).newInstance();
                        dbaObj=(DBAccess)obj;
                        hasNext=dbaObj.loadFields(statment,i++);
                        progress_double += 90.0 / count;
                        progress = (int) progress_double;
                        curr_operation = "load " + dbaObj;
                    }while(hasNext);
                }
                catch (ClassCastException e) {
                    e.printStackTrace();
                    e.printStackTrace(LOG_FILE_PRINT_STREAM);
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    e.printStackTrace(LOG_FILE_PRINT_STREAM);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                    e.printStackTrace(LOG_FILE_PRINT_STREAM);
                }
                catch (InstantiationException e) {
                    e.printStackTrace();
                    e.printStackTrace(LOG_FILE_PRINT_STREAM);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }catch (Exception e) {
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            loading=false;
            closeConnection();
            System.gc();
        }
//        loadLanguage();
        switch(currLang){
            case LANG_ENG:
                MapApplication.processMAEvent(new MapApplicationEvent(MapApplicationEvent.SET_ENGLISH_LANGUAGE));
                break;
            case LANG_HEB:
                MapApplication.processMAEvent(new MapApplicationEvent(MapApplicationEvent.SET_HEBREW_LANGUAGE));
                break;
            case LANG_RUS:
                MapApplication.processMAEvent(new MapApplicationEvent(MapApplicationEvent.SET_RUSSIAN_LANGUAGE));
                break;
        }
        System.out.println("LOADED");
        PSRender.outHold();
    }
    private double deleteAll(){
//        DBAccess objDBA;
        double progress_double = 10.0;
//        double count;
//        count = servicedObjects.size();
//        if (count == 0) {
//            progress_double = 10.0;
//        }
//        UserPopupListener.resetRoad();
//        try{
//            for(int i, priority=LOAD_SAVE_PRIORITY_QUANTITY;priority>=LOAD_SAVE_PRIORITY_NO_IMPORTANT;priority--){
//                i=0;
//                curr_operation="deleting";
//                while(servicedObjects.size()>0&&i<servicedObjects.size()){
//                    objDBA=((DBAccess)servicedObjects.get(i));
//                    if(objDBA.getLoadPriority()==priority){
//                        objDBA.remove();
//                        curr_operation="deleting "+objDBA;
//                        progress_double+=10.0/count;
//                        progress=Math.round((float)progress_double);
//                        System.out.println("by db deleted "+objDBA);
//                    } else{
//                        i++;
//                    }
//                }
//            }
//        }catch(Exception e){
        MouseContains.removeAll();
        servicedObjects.clear();
        PSRender.getRenderedObjects().clear();
        Global.clear();
//        }
//        PSRender.allwaysRepaintOn();
//        PSRender.repaint_anywhere();
        PSRender.clearAllShapes();
        MouseContains.contains(new Point2DSerializable(0,0,true));
        return progress_double;
    }
    public synchronized static void loadVariables() {
        //CREATE TABLE IF NOT EXISTS  сейвить смщение имаджа, видимость контролов, вычисление видимости цометов
        Statement stmt = null;
//        ActionEvent ae=new ActionEvent(null,ActionEvent.ACTION_PERFORMED,"change ctrl");
//
//        throw ae;
        connectToDB();
        if(connection==null){
            return;
        }
//        progress_title="loading variables";
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CURRLANG,"
                    + "FRAMEX,FRAMEY,"
                    + "SCALE,"
                    +"IMG_OFF_X,IMG_OFF_Y,"
                    +"PASSWORD,"
                    +"MAP_SIZE_WIDTH,MAP_SIZE_HEIGTH"
                    + " FROM VARIABLES;");
            if (rs.next() == false) {
                System.out.println("загрузка variables не удалась, не найдены данные");
                return;
            }
            currLang = rs.getInt("CURRLANG");
            Point2DSerializable.at.setToTranslation(0.0,0.0);
            PSRender.outHold();
            PSRender.setScale(rs.getDouble("SCALE"));
            PSRender.onHold();
            Point2DSerializable.at.setTransform(rs.getDouble("SCALE"),
                    0.0,
                    0.0,
                    rs.getDouble("SCALE"),
                    rs.getDouble("FRAMEX"),
                    rs.getDouble("FRAMEY"));
//            Point2DSerializable.translate(rs.getDouble("FRAMEX"),rs.getDouble("FRAMEY"));
            PSRender.setPic_offset(new Point2DSerializable(rs.getDouble("IMG_OFF_X"),
                    rs.getDouble("IMG_OFF_Y"),true));
            PSRender.setGridWidth(rs.getDouble("MAP_SIZE_WIDTH"));
            PSRender.setGridHeigth(rs.getDouble("MAP_SIZE_HEIGTH"));
            setPassword(rs.getString("PASSWORD"));
//            MapApplication.ctrl_visibility_pressed(rs.getBoolean("CTRL_VISIBILITY"));
            MapApplication.processMAEvent(new MapApplicationEvent(MapApplicationEvent.SET_USER_MODE));
            // PSRender.zoom=rs.getDouble("ZOOM");
        }
        catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }catch (Exception e) {
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
        }
    }
    public synchronized static void saveVariables() {
        Statement stmt = null;
        connectToDB();
        if(connection==null){
            return;
        }
        curr_operation="save variables";
//        progress_title="saving variables";
        try {
            stmt = connection.createStatement();
            try {
                stmt.executeUpdate("DROP TABLE VARIABLES");
            }
            catch (SQLException e) {
                System.out.println("все нормально - создание новой базы");
                e.printStackTrace();
                LOG_FILE_PRINT_STREAM.println("все нормально - создание новой базы");
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            stmt.executeUpdate("CREATE TABLE VARIABLES"   //сейвить смщение имаджа, видимость контролов, вычисление видимости цометов
                    +"(CURRLANG INT,"
                    +"FRAMEX DOUBLE,"
                    +"FRAMEY DOUBLE,"
                    +"SCALE DOUBLE,"
                    +"ELEMENT_COUNT INT,"
                    +"IMG_OFF_X DOUBLE,"
                    +"IMG_OFF_Y DOUBLE,"
                    +"PASSWORD CHAR(16),"
                    +"MAP_SIZE_WIDTH DOUBLE,"
                    +"MAP_SIZE_HEIGTH DOUBLE)");
            stmt.executeUpdate("INSERT INTO VARIABLES VALUES("
                    +currLang + ","
                    +Point2DSerializable.at.getTranslateX() + ","
                    +Point2DSerializable.at.getTranslateY() + ","
                    +PSRender.getScale() + ","
                    +servicedObjects.size() + ","
                    +PSRender.getPic_offset().getX()+","
                    +PSRender.getPic_offset().getY()+","
                    +"'"+password+"',"
                    +PSRender.getGridWidth()+","
                    +PSRender.getGridHeigth()+")");

        }
        catch (SQLException e) {
            System.out.println("INSERT INTO VARIABLES VALUES("
                    +currLang+","
                    +Point2DSerializable.at.getTranslateX()+","
                    +Point2DSerializable.at.getTranslateY()+","
                    +PSRender.getScale()+","
                    +servicedObjects.size()+","
                    +PSRender.getPic_offset().getX()+","
                    +PSRender.getPic_offset().getY()+","
                    +password+","
                    +PSRender.getGridWidth()+","
                    +PSRender.getGridHeigth()+")");
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }catch (Exception e) {
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception ex) {
            }
            closeConnection();
        }
    }

    public synchronized void loadLanguage(){
        Statement stmt=null;
        MultyLanguage ml=null;
        String code=null;
        ResultSet rs=null;
        double progress_double=0.0;
        double count=servicedObjects.size();
        try{
            connectToDB();
            if(connection==null){
                return;
            }
            progress_title="loading language";
            stmt=connection.createStatement();
            for(int i=0;i<servicedObjects.size();i++){
                if(servicedObjects.get(i) instanceof MultyLanguage){
                    ml=((MultyLanguage)servicedObjects.get(i));
                    code=ml.getCode();
                    rs=stmt.executeQuery("SELECT "
                            +languages[currLang]
                            +",VISIBLE"
                            +" FROM LANG"
                            +" WHERE CODE='"
                            +code
                            +"';");
                    if(rs.next()==false){
                        System.out.println("не найдено им€ дл€ "
                                +code
                                +" код €зыка="
                                +currLang);
                    } else{
                        ml.setCurrName(rs.getString(languages[currLang]).trim());
                        ml.setVisibilityName(rs.getBoolean("VISIBLE"));
                    }
                    progress_double+=100.0/count;
                    progress=(int)progress_double;
                    curr_operation="Save "+ml;

                    //ml.setCode();
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
            System.out.println(""+ml.toString()+" code="+code+" lang="+currLang);
        } catch(Exception e){
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        } finally{
            try{
                if(stmt!=null){
                    stmt.close();
                }
            } catch(Exception e){
                e.printStackTrace();
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
//            MapApplication.set_lang_icon();

        }
    }
    public synchronized static void loadLanguageMl(MultyLanguage ml){
        Statement stmt=null;
        String code=null;
        ResultSet rs=null;
        double progress_double=0.0;
        double count=servicedObjects.size();
        try{
            connectToDB();
            if(connection==null){
                return;
            }
            progress_title="loading language";
            stmt=connection.createStatement();
            code=ml.getCode();
            rs=stmt.executeQuery("SELECT "
                    +languages[currLang]
                    +",VISIBLE"
                    +" FROM LANG"
                    +" WHERE CODE='"
                    +code
                    +"';");
            if(rs.next()==false){
                System.out.println("не найдено им€ дл€ "
                        +code
                        +" код €зыка="
                        +currLang);
            } else{
                ml.setCurrName(rs.getString(languages[currLang]).trim());
                ml.setVisibilityName(rs.getBoolean("VISIBLE"));
            }
            progress_double+=100.0/count;
            progress=(int)progress_double;
            curr_operation="Save "+ml;
        } catch(SQLException e){
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
            System.out.println(""+ml.toString()+" code="+code+" lang="+currLang);
        } catch(Exception e){
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        } finally{
            try{
                if(stmt!=null){
                    stmt.close();
                }
            } catch(Exception e){
                e.printStackTrace();
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
//            MapApplication.set_lang_icon();

        }
    }
    public synchronized static void saveLanguage() { /////////////////////////////////////
        Statement stmt = null;
        int curr_lang=currLang;
        connectToDB();
        if(connection==null){
            return;
        }
        createProgressFrame("saving language");
        MultyLanguage ml=null;
        double progress_double = 0.0;
        double count = servicedObjects.size();
        try {
            stmt = connection.createStatement();
            String st;
            try {
                stmt.executeQuery("SELECT * FROM LANG");
            }
            catch (SQLException e) {
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
                st = "CREATE TABLE LANG (";
                for (int i = 0; i < languages.length; i++) {
                    st += languages[i] + " CHAR(64),";
                }
                st += " VISIBLE BOOLEAN, CODE CHAR(64));";
                stmt.executeUpdate(st);
            }

            ResultSet rs;
            for (int i = 0; i < servicedObjects.size(); i++) {
                if (servicedObjects.get(i)instanceof MultyLanguage) {
                    ml = ( (MultyLanguage) servicedObjects.get(i));
                    rs = stmt.executeQuery("SELECT CODE FROM LANG WHERE CODE='"
                            + ml.getCode() + "';");
                    if (rs.next() == false) {
                        st = "(";
                        for (int j = 0; j < languages.length; j++) {
                            if (j == curr_lang) {
                                st += "'" + ml.getCurrName() + "',";
                            }
                            else {
                                st += "'',";
                            }
                        }
                        st += ml.getVisibilityName() + ",'" + ml.getCode() + "')";
                        stmt.executeUpdate("INSERT INTO LANG VALUES" + st);
                    }
                    else {
                        try{
                            stmt.executeUpdate("UPDATE LANG SET "
                                    + languages[curr_lang]
                                    + "='" + ml.getCurrName().trim()
                                    + "',VISIBLE=" + ml.getVisibilityName()
                                    + " WHERE CODE='" + ml.getCode().trim() + "';");
                        }catch(Exception e){
                            e.printStackTrace();
                            e.printStackTrace(LOG_FILE_PRINT_STREAM);
                            System.out.println(ml);
                            System.out.println(ml.getCurrName());
                            System.out.println(ml.getCode());
                        }

                    }
                    progress_double += 100.0 / count;
                    progress = (int) progress_double;
                    curr_operation = "Save lng for " + ml;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
            System.out.println(ml);
        }
        catch (Exception e) {
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
        }
    }
    private static boolean checkForWarning(SQLWarning warn) {
        boolean rc = false;

        // ≈сли дан объект SQLWarning, отобразить
        // сообщени€ о предупреждени€х. «аметьте, что может быть
        // несколько предупреждений, св€занных в цепочку

        if (warn != null) {
            System.out.println("\n *** Warning ***\n");
            rc = true;
            while (warn != null) {
                System.out.println("SQLState: " + warn.getSQLState());
                System.out.println("Message:  " + warn.getMessage());
                System.out.println("Vendor:   " + warn.getErrorCode());
                System.out.println("");
                warn = warn.getNextWarning();
            }
        }
        return rc;
    }
    public synchronized static Object[][] getNamesFor(MultyLanguage ml) {
        Statement stmt = null;
        Object data[][] = new Object[1][4];
        connectToDB();
        if(connection==null){
            data[0][0]="";
            data[0][1]="";
            data[0][2]="";
            data[0][3]=new Boolean(false);
            return data;
        }
//        progress_title="geting data for "+ml;
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ENG,RUS,HEB,VISIBLE "
                    + "FROM LANG WHERE CODE LIKE '"
                    + ml.getCode()
                    + "';");

            if (rs.next() == true) {
                data[0][0] = rs.getString("RUS");
                data[0][1] = rs.getString("ENG");
                data[0][2] = rs.getString("HEB");
                data[0][3] = new Boolean(rs.getBoolean("VISIBLE"));
            }
            else {
                data[0][0] = "";
                data[0][1] = "";
                data[0][2] = "";
                data[0][3] = new Boolean(false);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        catch (Exception e) {
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
        }
        //al.toArray(data);
        return data;
    }
    public synchronized static void putNamesFor(MultyLanguage ml, Object langData[][]) {
        Statement stmt = null;
        connectToDB();
        if(connection==null){
            return;
        }
//        progress_title="saving data for "+ml;
        ResultSet rs;
        String st;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT CODE FROM LANG WHERE CODE='"
                    + ml.getCode() + "';");
            int i, len = langData[0].length - 1;
            if (rs.next() == false) {
                st = "(";

                for (i = 0; i < len; i++) {
                    st += "'" + langData[0][i] + "',";
                }
                st += langData[0][len] + ",'" + ml.getCode() + "')";
                stmt.executeUpdate("INSERT INTO LANG VALUES" + st);
            }
            else {
//	UPDATE table_name SET column1 = Сdata1Т, column2 = Сdata2Т
//WHERE column3 = Сdata3Т;
                st = "UPDATE LANG SET ";
                for (i = 0; i < len; i++) {
                    st += languages[i] + "='" + langData[0][i] + "',";
                    /*if(i<langData.length-1){
                    st+=",";
                    }*/
                }
                st += "VISIBLE=" + langData[0][len] + " WHERE CODE='" + ml.getCode() +
                        "';";
                stmt.executeUpdate(st);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        catch (Exception e) {
            e.printStackTrace(LOG_FILE_PRINT_STREAM);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
                e.printStackTrace(LOG_FILE_PRINT_STREAM);
            }
            closeConnection();
        }
    }
    private static void createProgressFrame(String title) {
        if(frame==null){
            progress_done = false;
            progress = 0;
            progress_title = title;
            frame = new ProgressFrame(db_service);
//            content_pane.add(frame);
        }
//        frame.setLocation( (RoadPanel.lastRoadPanel.getWidth() - frame.getWidth()) /
//                2
//                ,
//                (RoadPanel.lastRoadPanel.getHeight() - frame.getHeight()) / 2);
//
//        try {
//            frame.setSelected(true);
//            frame.moveToFront();
//        }
//        catch (java.beans.PropertyVetoException e) {
//            e.printStackTrace();
//            e.printStackTrace(LOG_FILE_PRINT_STREAM);
//        }
//        catch (Exception e) {
//            e.printStackTrace(LOG_FILE_PRINT_STREAM);
//        }
        /*try {
        frame.t.join(10000);
        //Thread.currentThread().sleep(5000);
        }
        catch (InterruptedException ex) {
        ex.printStackTrace();
        }*/
    }
    public int getProgressStatus() {
        return progress;
    }
    public String getProgressTitle() {
        return progress_title;
    }
    public String getCurrentOperation() {
        return curr_operation;
    }
    public boolean done() {
        return progress_done;
    }
//    private void find_jls_no_setted_length(){
//        ArrayList al_jls=new ArrayList();
//        JunctionLink jl;
//        for(int i=0;i<servicedObjects.size();i++){
//            if(servicedObjects.get(i) instanceof JunctionLink){
//                jl=(JunctionLink)servicedObjects.get(i);
//                if(jl.getPathLength()<1){
//                    //IFrameSupervisor.showJInternalFrame(jl,IFrameSupervisor.PROPERTIES_FRAME);
//                    al_jls.add(jl);
//                }
//            }
//        }
//        JunctionLink[] jls=new JunctionLink[al_jls.size()];
//        if(jls.length>0){
//            al_jls.toArray(jls);
//            JunctionLinkAllPropertiesIFrame frame = JunctionLinkAllPropertiesIFrame.activate_frame(jls);
//            frame.setLocation( (RoadPanel.lastRoadPanel.getWidth() - frame.getWidth()) / 2,
//                    (RoadPanel.lastRoadPanel.getHeight() - frame.getHeight()) / 2);
//            content_pane.add(frame);
//            try {
//                frame.setSelected(true);
//                frame.moveToFront();
//            }
//            catch (java.beans.PropertyVetoException e) {
//                e.printStackTrace();
//                e.printStackTrace(LOG_FILE_PRINT_STREAM);
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//                e.printStackTrace(LOG_FILE_PRINT_STREAM);
//            }
//        }else{
//            JOptionPane.showMessageDialog(RoadPanel.lastRoadPanel,"No found","Congratulation",JOptionPane.WARNING_MESSAGE);
//        }
//    }
    public static DBService getInstance(){
        return db_service;
    }
    public static PrintStream getErrorStream(){
        return LOG_FILE_PRINT_STREAM;
    }
    private void find_place(){
        ArrayList al_jncs=new ArrayList();
        Junction j;
        for(int i=0;i<servicedObjects.size();i++){
            if(servicedObjects.get(i) instanceof Junction){
                j=(Junction)servicedObjects.get(i);
                if("NO NAME".equalsIgnoreCase(j.getCurrName())==false){
                  al_jncs.add(j);
                }
            }
        }
        Junction[] jncs=new Junction[al_jncs.size()];
        al_jncs.toArray(jncs);
        FindFrame.createFindFrame(jncs);
    }
    private void find_optimal_trace_frame(){
        ArrayList al_jncs=new ArrayList();
        Junction j;
        for(int i=0;i<servicedObjects.size();i++){
            if(servicedObjects.get(i) instanceof Junction){
                j=(Junction)servicedObjects.get(i);
                if("NO NAME".equalsIgnoreCase(j.getCurrName())==false){
                  al_jncs.add(j);
                }
            }
        }
        Junction[] jncs=new Junction[al_jncs.size()];
        al_jncs.toArray(jncs);
        FindOptimalTraceFrame.createFindFrame(jncs);
    }
    public void mapApplicationActionPerformed(MapApplicationEvent e){
        switch(e.getEventType()){
            case MapApplicationEvent.SET_HEBREW_LANGUAGE:
                currLang=LANG_HEB;
                execute(DB_LOAD_LANG);
                break;
            case MapApplicationEvent.SET_ENGLISH_LANGUAGE:
                currLang=LANG_ENG;
                execute(DB_LOAD_LANG);
                break;
            case MapApplicationEvent.SET_RUSSIAN_LANGUAGE:
                currLang=LANG_RUS;
                execute(DB_LOAD_LANG);
                break;
            case MapApplicationEvent.FIND_PLACE:
                execute(DB_FIND_PLACE);
                break;
            case MapApplicationEvent.TRACE_OPTIMAL_ROUTE:
                execute(DB_FIND_OPTIMAL_TRACE);
                break;
            case MapApplicationEvent.CHECK_FOR_WARNINGS:
                execute(DB_CHECK);
                break;
        }
    }
    public static int getCurrLang(){
        return currLang;
    }
    public static void setFileName(String fileName){
        DBService.DB_FILE=fileName;
    }
    private void checkObjects(){
        ArrayList reports_al=new ArrayList();
        createProgressFrame("checking");
        curr_operation="set outlines";
        initOutlines();
        curr_operation="check objects";
        DBAccess currentObject;
        double d_p=0;
        for(int i=0;i<servicedObjects.size();i++){
            currentObject=(DBAccess)servicedObjects.get(i);
            reports_al.add(currentObject.selfCheck());
            d_p+=50+50.0/servicedObjects.size();
            progress=(int)d_p;
            curr_operation="check object "+currentObject;
        }

        CheckReport[] reports=new CheckReport[reports_al.size()];
        reports_al.toArray(reports);
        progress_done=true;
        new ErrorTableFrame(reports);
    }

    public static void initOutlines(){
        CurveContainer currrentOutline;
        outlines.clear();
        double d_p=0;
        for(int i=0;i<servicedObjects.size();i++){
            currrentOutline=((DBAccess)servicedObjects.get(i)).getOutline();
            if(currrentOutline!=null){
                outlines.add(currrentOutline);
            }
            d_p+=50.0/servicedObjects.size();
            progress=(int)d_p;
        }
    }
    public static ArrayList getOutlines(){
        return outlines;
    }
    public static String getPassword(){
        return password;
    }
    public static void setPassword(String password){
        DBService.password=password;
    }
}