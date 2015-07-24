package roadmapproject;
import java.util.*;
public interface PreferencedShapeInterface extends DrawCONSTANTS{
    public void initializeShapes();
    public ArrayList getShapes();
    public boolean setShapes();
    public void translate(Point2DSerializable p);
}
