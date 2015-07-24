package roadmapproject;
import java.awt.*;
import java.awt.geom.*;

/**
 * Created by Igor
 * Date: Feb 16, 2003
 * Time: 3:01:13 PM
 */
public class FlagShape implements Shape{
    private GeneralPath outline;
    private GeneralPath rect;
    private Point2DSerializable location;

    public FlagShape(Point2DSerializable location) {
        this.location=location;
        this.outline = new GeneralPath();
        this.rect=new GeneralPath();
        rect.append(new Rectangle2D.Double(location.getX(),location.getY()-20.0,20.0,10.0),false);
        outline.append(new Line2D.Double(location.getX(),location.getY(),location.getX(),location.getY()-10.0),false);
        outline.append(rect,false);
    }

    public Rectangle getBounds() {
        return outline.getBounds();
    }

    public Rectangle2D getBounds2D() {
        return outline.getBounds2D();
    }

    public boolean contains(double x, double y) {
        return outline.contains(x,y);
    }

    public boolean contains(Point2D p) {
        return outline.contains(p);
    }

    public boolean intersects(double x, double y, double w, double h) {
        return outline.intersects(x,y,w,h);
    }

    public boolean intersects(Rectangle2D r) {
        return outline.intersects(r);
    }

    public boolean contains(double x, double y, double w, double h) {
        return outline.contains(x,y,w,h);
    }

    public boolean contains(Rectangle2D r) {
        return outline.contains(r);
    }
    public boolean contains(Point p,AffineTransform at){
        Point cnv=new Point((int)(p.getX()-at.getTranslateX()),(int)(p.getY()-at.getTranslateY()));
        return contains(cnv);
    }
    public PathIterator getPathIterator(AffineTransform at) {
        return outline.getPathIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return outline.getPathIterator(at,flatness);
    }
    public GeneralPath getTransformedOutline(final AffineTransform at){
        AffineTransform translate;
        Point2D p=new Point2D.Double(location.getX(),location.getY());
        at.transform(p,p);
        translate=AffineTransform.getTranslateInstance(p.getX()-location.getX(),p.getY()-location.getY());
        GeneralPath gp=new GeneralPath();
        gp.append(outline,false);
        gp.transform(translate);
        return gp;
    }
    public GeneralPath getTransformedRect(AffineTransform at){
        AffineTransform translate;
        Point2D p=new Point2D.Double(location.getX(),location.getY());
        at.transform(p,p);
        translate=AffineTransform.getTranslateInstance(p.getX()-location.getX(),p.getY()-location.getY());
        GeneralPath gp=new GeneralPath();
        gp.append(rect,false);
        gp.transform(translate);
        return gp;
    }
}
