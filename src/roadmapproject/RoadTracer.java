package roadmapproject;
/**
 * // убрал проверку что минимум, нужно еще раз разобратьс€ с currentOptimalTrace
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
        /*куб рассто€ни€ от ј до ¬*/
        double ab2=Point2D.distanceSq(a.getX(),a.getY(),b.getX(),b.getY());
        /*куб рассто€ни€ от ј до —*/
        double ac2=Point2D.distanceSq(a.getX(),a.getY(),c.getX(),c.getY());
        /*куб рассто€ни€ от ¬ до —*/
        double bc2=Point2D.distanceSq(b.getX(),b.getY(),c.getX(),c.getY());
        /*уравнение cos(alfa)=(AB^2+AC^2-BC^2)/(2*AB*AC) выведено из BC^2=AB^2+AC^2-2*AB*AC*cos(alfa)*/
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

        /*вычисление значени€ угла из его косинуса и перевод значени€ из радиан в градусы*/
        return Math.toDegrees(Math.acos(res)); /*возвращает угол ¬ј— в точке ј соответсвенно*/
    }
    private JunctionLink[] getSortedJunctionLinks(Junction start_junction,Junction prev_junction){
        ArrayList al_jls=start_junction.getPaths();
        /*jl содержит все патчи перекрестка start_junction*/
        JunctionLink[] jls=new JunctionLink[al_jls.size()];
        al_jls.toArray(jls);
        /*массив углов дл€ каждого патча*/
        double[] angles=new double[jls.length];
        double[] angles_sorted=new double[jls.length];
        Junction second_half;

        Point2DSerializable a,b,c;
        /*установка координат виртуальной линии от start_junction (точка ј) до end_junction(точка —)*/
        a=start_junction.getPn();// начальна€ точка ј
        c=end_junction.getPn();// конечна€ точка —
        for(int i=0;i<jls.length;i++){
            /*вытаскиваем противоположный перекресток*/
            second_half=jls[i].getSecondHalfJunction(start_junction);
            if(second_half==null||second_half==prev_junction){ /*если его нет, то это тупикова€ ветвь и нужно ее пометить*/
                angles[i]=-1;
            }else{
                b=second_half.getPn(); /*устанавливаем точку ¬ координатами противоположного перекрестка*/
                angles[i]=getAngleBAC(a,b,c); /*находим угол ¬ј— в точке ј и заносим его в массив углов*/
                if(angles[i]==-1){
                    System.out.println("prev="+prev_junction+" start="+start_junction);
                    System.out.println("end="+end_junction);

                }
            }
        }
        /*копируем массив углов в массив который будет отсортирован*/
        System.arraycopy(angles,0,angles_sorted,0,angles.length);
        Arrays.sort(angles_sorted); /*сортируем массив*/
        al_jls=new ArrayList();
        for(int ind_sorted=0,ind_jls;ind_sorted<angles_sorted.length;ind_sorted++){

            /*если угол в отсортированом массиве не "вычеркнут" как тупиковый*/
            if(angles_sorted[ind_sorted]!=-1){
                ind_jls=0;
                /*ищем индекс этого угла в массиве Ќ≈отсортированых углов*/
                try {
// todo когда одинаковые углы, провер€ть что индекс уже занесен
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
                /*заносим патч по найденому индексу в динамический массив, получаетс€,
                что в динамический массив заносим патчи отсортированые по углу но не включа€ тупиковые*/
                al_jls.add(jls[ind_jls-1]);
            }
        }
        jls=new JunctionLink[al_jls.size()]; /*создаем массив размером с отсортированый динамический массив*/
        al_jls.toArray(jls); /*копируем содержимое динамического массива в обычный*/
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
