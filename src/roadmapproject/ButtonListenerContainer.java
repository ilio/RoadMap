package roadmapproject;


/**
 * Created by Igor
 * Date: 10.03.2003
 * Time: 18:21:38
 */
public class ButtonListenerContainer implements MapApplicationListener{
    private MapApplicationListener a;
    private MapApplicationListener b;
    public ButtonListenerContainer(MapApplicationListener a,MapApplicationListener b){
        this.a=a;
        this.b=b;
    }
    public void mapApplicationActionPerformed(MapApplicationEvent e){
        a.mapApplicationActionPerformed(e);
        b.mapApplicationActionPerformed(e);
    }
    public static MapApplicationListener add(MapApplicationListener a,MapApplicationListener b){
        if (a == null)  return b;
        if (b == null)  return a;
        return new ButtonListenerContainer(a, b);
    }
    public static MapApplicationListener remove(MapApplicationListener l,MapApplicationListener oldl){
         if (l == oldl || l == null) {
            return null;
        } else if (l instanceof ButtonListenerContainer) {
            return ((ButtonListenerContainer)l).remove(oldl);
        } else {
            return l;		// it's not here
        }
    }
    protected MapApplicationListener remove(MapApplicationListener oldl) {
        if (oldl == a)  return b;
        if (oldl == b)  return a;
        MapApplicationListener a2 = remove(a, oldl);
        MapApplicationListener b2 = remove(b, oldl);
        if (a2 == a && b2 == b) {
            return this;	// it's not here
        }
        return add(a2, b2);
    }
}
