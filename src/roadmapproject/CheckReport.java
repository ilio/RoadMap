package roadmapproject;

/**
 * Created by Igor
 * Date: 24.03.2003
 * Time: 18:57:38
 */
public class CheckReport{
    public static final int NO_ERRORS=0;
    public static final int INTERSECT_WITH_OTHER_OBJECT=1;
    public static final int LENGTH_MOT_SETTED=INTERSECT_WITH_OTHER_OBJECT<<1;
    public static final int NAME_NOT_SETTED=LENGTH_MOT_SETTED<<1;
    public static final int NO_HAVE_SECOND_JUNCTION=NAME_NOT_SETTED<<1;
    public static final int OUTSIDE_OF_FRAME=NO_HAVE_SECOND_JUNCTION<<1;
    private int errorType;
    private MultyLanguage ml;
    public CheckReport(MultyLanguage ml){
        this(NO_ERRORS);
        this.ml=ml;
    }
 public CheckReport(int typeError){
        this.errorType=typeError;
    }
    public void addError(int errorType){
        this.errorType|=errorType;
    }
    public int getErrorType(){
        return errorType;
    }
    public boolean hasError(){
        return errorType!=NO_ERRORS;
    }

    public String toString(){
        String errorString="";
        if(errorType==NO_ERRORS){
//            System.out.println(" запрос на печать где нет ошибки!!!");
            return "no errors";
        }
        if((errorType&INTERSECT_WITH_OTHER_OBJECT)!=0){
            errorString+="intersection | ";
        }
        if((errorType&LENGTH_MOT_SETTED)!=0){
            errorString+="zero length | ";
        }
        if((errorType&NAME_NOT_SETTED)!=0){
            errorString+="name not setted | ";
        }
        if((errorType&NO_HAVE_SECOND_JUNCTION)!=0){
            errorString+="no have second junction | ";
        }
        if((errorType&OUTSIDE_OF_FRAME)!=0){
            errorString+="outside of frame | ";
        }
        return errorString;
    }
    public MultyLanguage getObject(){
        return ml;
    }
}
