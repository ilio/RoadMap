package roadmapproject;

import java.util.*;

public class Global{


    private static TreeMap allJunctions=new TreeMap(); // ��� Junctions ��� ������� selected

    public static ObjectSerializable selectedObject=new ObjectSerializable(); // ��������� ������
    //public static int selectedType=NO_SELECTED_TYPE;// ��� ����������� �������
    public static BoundRecalcer selectedBR;// JunctionLink � ������� ���������� ��������� ������


    //public static boolean alwaysRepaint=false;
/*
public static void setAllJunctions(TreeMap tm){
allJunctions.clear();
System.gc();
allJunctions=tm;
}*/
    public static synchronized Junction getJunction(final String code){
        if(code!=null&&"null".equals(code)==false){
            Junction j=(Junction)allJunctions.get(code);
            if(j!=null){
                return j;
            }else{
                throw new Error("������� �������� Junction "+code+
                        " �� ������ � allJunctions !!!");
//                throw new JunctionNotFoundException(nj);
            }
        }else{
            return null;
        }

    }
    public static synchronized void addJunction(Junction j){
        allJunctions.put(j.getCode(),j);
    }
    public static synchronized Junction removeJunction(String key){
        return (Junction)allJunctions.remove(key);
    }
    public static synchronized boolean containsJunction(String key){
        return allJunctions.containsKey(key);
    }
    public static synchronized Collection getAllJunctions(){
        return allJunctions.values();
    }
    public static synchronized void clear(){
        allJunctions.clear();
    }
    public static void setSelectedBR(BoundRecalcer br){
        selectedBR=br;
    }

    /*private static class JunctionNotFoundException extends Exception{

        private final String nj;
        public JunctionNotFoundException(String nj){
            this.nj=nj;
        }

        public String getMessage(){
            return "�� ������ Junction "+nj+" � ������ allJunction";
        }
    }*/

}