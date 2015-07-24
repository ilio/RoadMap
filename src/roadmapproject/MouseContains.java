package roadmapproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MouseContains {
    private static ArrayList obj_contains;
    private static ArrayList selected_objects;
    static{
        obj_contains=new ArrayList();
        selected_objects=new ArrayList();
    }
    public synchronized static void add(ContainsPoint obj){
        obj_contains.add(obj);
    }
    public synchronized static void remove(ContainsPoint obj){
        obj_contains.remove(obj);
    }
    public synchronized static void removeAll(){
         obj_contains.clear();
    }
    public synchronized static final boolean contains(Point2DSerializable cp) {
        selected_objects.clear();
        if(SelectedObject.isStoped()){
            Global.selectedObject.reset();
            Global.setSelectedBR(null);
            return false;
        }
        try{
            ArrayList junctions = new ArrayList(Global.getAllJunctions());
            ArrayList juncLinks;
            Iterator ip, ij = junctions.iterator();
            Junction junc;
            JunctionLink jl;
            Global.selectedObject.reset();
            while (ij.hasNext()) {
                junc = (Junction) ij.next();
                juncLinks = junc.getPaths();
                ip = juncLinks.iterator();
                while (ip.hasNext()) {
                    jl = (JunctionLink) ip.next();
                    if (jl.isFirst(junc)) {
                        if (jl.contains(cp)) {
                            SelectedObjectsContainer soc=
                                    new SelectedObjectsContainer(Global.selectedBR
                                            ,Global.selectedObject.newInstance());
                            selected_objects.add(soc);
                        }
                    }
                }
            }
            if(selected_objects.size()>0){
                SelectedObjectsContainer[] objs=
                        new SelectedObjectsContainer[selected_objects.size()];
                selected_objects.toArray(objs);
                selected_objects.clear();
                Arrays.sort(objs,new ObjectSerializableComparator());
                Global.selectedObject.setObject(objs[0].getObjectSerializable());
                Global.setSelectedBR(objs[0].getBoundRecalcer());
                if(objs.length>1){
                    for(int k=0;k<objs.length;k++){
                        System.out.println("j"+k+"="+objs[k].getObjectSerializable().getJunction());
                    }
                }
                return true;
            }
            for(int i=0;i<obj_contains.size();i++){
                if(((ContainsPoint)obj_contains.get(i)).contains(cp)){
                    return true;
                }
            }
            return false;
        }finally{
            if(Global.selectedObject.isJunction()){
                Junction sj;
                sj=Global.selectedObject.getJunction();
                InfoPanel.setInfoText(sj.getCurrName());
            }else if(Global.selectedBR instanceof JunctionLink){
                JunctionLink sjl;
                sjl = (JunctionLink) Global.selectedBR;
                InfoPanel.setInfoText(sjl.getCurrName()+"\nLength="+sjl.getPathLength());
            }else {
                InfoPanel.clearText();
            }
        }
    }
}
class ObjectSerializableComparator implements Comparator{

    public int compare(Object o1, Object o2) {
        if(o1 instanceof SelectedObjectsContainer && o2 instanceof SelectedObjectsContainer){
            Junction j1,j2;
            j1=((SelectedObjectsContainer) o1).getObjectSerializable().getJunction();
            j2=((SelectedObjectsContainer) o2).getObjectSerializable().getJunction();
            if(j1==null&&j2==null){
                return 0;
            }
            if(j1==null){
                return 1;
            }
            if(j2==null){
                return -1;
            }
            return j1.getType()-j2.getType();
        }
        throw new Error(" o1="+o1+" o2="+o2);
    }
}
class SelectedObjectsContainer{
    private ObjectSerializable os;
    private BoundRecalcer br;

    public SelectedObjectsContainer(BoundRecalcer br, ObjectSerializable os) {
        this.br = br;
        this.os = os;
    }

    public BoundRecalcer getBoundRecalcer() {
        return br;
    }

    public ObjectSerializable getObjectSerializable() {
        return os;
    }
}