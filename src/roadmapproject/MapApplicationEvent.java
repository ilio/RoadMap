package roadmapproject;

/**
 * Created by Igor
 * Date: 10.03.2003
 * Time: 17:56:18
 */
public class MapApplicationEvent{
    public static final int SET_USER_MODE=1;
    public static final int SET_EDIT_MODE=2;
    public static final int SET_PICTURE_VISIBLE=3;
    public static final int SET_PICTURE_UNVISIBLE=4;
    public static final int SET_RUSSIAN_LANGUAGE=5;
    public static final int SET_ENGLISH_LANGUAGE=6;
    public static final int SET_HEBREW_LANGUAGE=7;
    public static final int PRINT=8;
    public static final int ZOOM_IN=9;
    public static final int ZOOM_OUT=10;
    public static final int TRACE_OPTIMAL_ROUTE=11;
    public static final int FIND_PLACE=12;
    public static final int SET_EDIT_MODE_PAINT=13;
    public static final int SET_EDIT_MODE_PICTURE_MOVE=14;
    public static final int SET_EDIT_MODE_MAP_MOVE=15;
    public static final int CHECK_FOR_WARNINGS=16;
    private int eventType;
    public MapApplicationEvent(int eventType){
        this.eventType=eventType;
    }
    public int getEventType(){
        return eventType;
    }
}
