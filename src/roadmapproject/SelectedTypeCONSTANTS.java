package roadmapproject;

/**
 * Created by Igor
 * Date: Jan 5, 2003
 * Time: 12:16:46 AM
 */
public interface SelectedTypeCONSTANTS {
    public static final int NO_SELECTED_TYPE    =0;// selected point type
    public static final int P1_POINT            =1;
    public static final int CTRL_POINT          =2;
    public static final int P2_POINT            =4;

    public static final int CURVE               =8;
    public static final int JUNCTION_POINT      =16;
    public static final int END_POINT           =32;
    public static final int OTHER_OBJECT        =64;
    public static final int IN_OBJECT           =128;
    public static final int MASK_POINT          =P1_POINT|P2_POINT|CTRL_POINT;
    public static final int MASK_P1_OR_P2       =P1_POINT|P2_POINT;
}
