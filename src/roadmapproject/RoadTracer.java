package roadmapproject;
/**
 * // ����� �������� ��� �������, ����� ��� ��� ����������� � currentOptimalTrace
 */
import java.awt.geom.*;
import java.util.*;
class RoadTracer {
    private RoadContainer optimalTrace;
    private Junction end_junction;
    private TreeMap traced;
    private int recursionCnt;
    public RoadTracer(){
        optimalTrace = new RoadContainer(null);
        traced=new TreeMap();
    }
    public void trace (RoadContainer prevRoute){
        recursionCnt=0;
        road_trace(prevRoute.getFirstJunction(),null,new RoadContainer(prevRoute.getFirstJunction()));
        TracePanel.setTrace(optimalTrace);
        System.out.println("recursion number="+recursionCnt);
    }
    public void setEndJunction(Junction j){
        end_junction=j;
    }
    public RoadContainer getOptimalTrace(){
        return optimalTrace;
    }
    private double getAngleBAC(Point2DSerializable a,Point2DSerializable b,Point2DSerializable c){
        /*��� ���������� �� � �� �*/
        double ab2=Point2D.distanceSq(a.getX(),a.getY(),b.getX(),b.getY());
        /*��� ���������� �� � �� �*/
        double ac2=Point2D.distanceSq(a.getX(),a.getY(),c.getX(),c.getY());
        /*��� ���������� �� � �� �*/
        double bc2=Point2D.distanceSq(b.getX(),b.getY(),c.getX(),c.getY());
        /*��������� cos(alfa)=(AB^2+AC^2-BC^2)/(2*AB*AC) �������� �� BC^2=AB^2+AC^2-2*AB*AC*cos(alfa)*/
        double res=(ab2+ac2-bc2)/(2.0*Math.sqrt(ab2)*Math.sqrt(ac2));

        if(Double.isNaN(res)==true){
            System.out.println("NAN res before="+res);
            System.out.println("(ab2+ac2-bc2)="+(ab2+ac2-bc2));
            System.out.println("(2.0*Math.sqrt(ab2)*Math.sqrt(ac2))="+(2.0*Math.sqrt(ab2)*Math.sqrt(ac2)));
            System.out.println("res="+res+" acos="+Math.acos(res));
            System.out.println("point a="+a);
            System.out.println("point b="+b);
            System.out.println("point c="+c);
            return -1.0;
        }
//        res=Math.round(res);

        if(res>1.0){
            System.out.println("res before="+res);
            res=1.0;
        }else if(res<-1.0){
            System.out.println("res before="+res);
            res=-1.0;
        }

        /*���������� �������� ���� �� ��� �������� � ������� �������� �� ������ � �������*/
        return Math.toDegrees(Math.acos(res)); /*���������� ���� ��� � ����� � �������������*/
    }
    private JunctionLink[] getSortedJunctionLinks(Junction start_junction,Junction prev_junction){
        ArrayList al_jls=start_junction.getPaths();
        /*jl �������� ��� ����� ����������� start_junction*/
        JunctionLink[] jls=new JunctionLink[al_jls.size()];
        al_jls.toArray(jls);
        /*������ ����� ��� ������� �����*/
        double[] angles=new double[jls.length];
        double[] angles_sorted=new double[jls.length];
        Junction second_half;

        Point2DSerializable a,b,c;
        /*��������� ��������� ����������� ����� �� start_junction (����� �) �� end_junction(����� �)*/
        a=start_junction.getPn();// ��������� ����� �
        c=end_junction.getPn();// �������� ����� �
        for(int i=0;i<jls.length;i++){
            /*����������� ��������������� �����������*/
            second_half=jls[i].getSecondHalfJunction(start_junction);
            if(second_half==null||second_half==prev_junction){ /*���� ��� ���, �� ��� ��������� ����� � ����� �� ��������*/
                angles[i]=-1;
            }else{
                b=second_half.getPn(); /*������������� ����� � ������������ ���������������� �����������*/
                angles[i]=getAngleBAC(a,b,c); /*������� ���� ��� � ����� � � ������� ��� � ������ �����*/
                if(angles[i]==-1){
                    System.out.println("prev="+prev_junction+" start="+start_junction);
                    System.out.println("end="+end_junction);

                }
            }
        }
        /*�������� ������ ����� � ������ ������� ����� ������������*/
        System.arraycopy(angles,0,angles_sorted,0,angles.length);
        Arrays.sort(angles_sorted); /*��������� ������*/
        al_jls=new ArrayList();
        for(int ind_sorted=0,ind_jls;ind_sorted<angles_sorted.length;ind_sorted++){

            /*���� ���� � �������������� ������� �� "���������" ��� ���������*/
            if(angles_sorted[ind_sorted]!=-1){
                ind_jls=0;
                /*���� ������ ����� ���� � ������� ���������������� �����*/
                try {
// todo ����� ���������� ����, ��������� ��� ������ ��� �������
                    while(angles_sorted[ind_sorted]!=angles[ind_jls++]);
                    angles[ind_jls-1]=-1;
                } catch (Exception e) {
                    System.out.println("index_jls="+ind_jls+" size="+angles.length+" ind_sorted="+ind_sorted+" size="+angles_sorted.length);
                    System.out.println("angles");
                    for(int k=0;k<angles.length;k++){
                        System.out.println(angles[k]);
                    }
                    System.out.println("sorted");
                    for(int k=0;k<angles_sorted.length;k++){
                        System.out.println(angles_sorted[k]);
                    }
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                /*������� ���� �� ��������� ������� � ������������ ������, ����������,
                ��� � ������������ ������ ������� ����� �������������� �� ���� �� �� ������� ���������*/
                al_jls.add(jls[ind_jls-1]);
            }
        }
        jls=new JunctionLink[al_jls.size()]; /*������� ������ �������� � �������������� ������������ ������*/
        al_jls.toArray(jls); /*�������� ���������� ������������� ������� � �������*/
        return jls;
    }
    private void road_trace(Junction start_junction,Junction prev_junction,RoadContainer prev_trace){
        recursionCnt++;
        if(start_junction.equals(end_junction)){
            optimalTrace.changeIfMin(prev_trace);
            return;
        }
        RoadContainer trace;
        if((trace=(RoadContainer)traced.get(start_junction.getCode()))!=null){
            if(trace.getDistance()<prev_trace.getDistance()){
                return;
            }
        }
        traced.put(start_junction.getCode(),prev_trace);
        JunctionLink[] jls=getSortedJunctionLinks(start_junction,prev_junction);
        for(int i=0;i<jls.length;i++){
            trace = RoadContainer.copy(prev_trace);
            trace.add(jls[i]);
            if(optimalTrace.getSize()==0||optimalTrace.isLongestFor(trace)){
                road_trace(jls[i].getSecondHalfJunction(start_junction),start_junction,trace);
            }
        }
    }
}
