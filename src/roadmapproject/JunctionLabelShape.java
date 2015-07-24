package roadmapproject;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;

public class JunctionLabelShape
        implements Shape {
    private GeneralPath label_shape;
    private Point2DSerializable position;
    private RoundRectangle2D rect;
    public JunctionLabelShape() {
        label_shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        position = new Point2DSerializable(true);
        rect=new RoundRectangle2D.Double();
    }
    public JunctionLabelShape(String label, Point2DSerializable position) {
        this();
        setLabel(label, position);
    }
    public void setLabel(String label, Point2DSerializable position) {
        this.position.setLocation(position);
        this.label_shape.reset();
        if(label==null||label.length()<1){
            return;
        }

        TextLayout textLayout = new TextLayout(label,
                new Font("system", Font.PLAIN,12),
                new FontRenderContext(null, false, false));
        AffineTransform textAffineTransform = new AffineTransform();
        double heigth = textLayout.getDescent() + textLayout.getAscent();
        double width = textLayout.getBounds().getWidth();
        double offset_x = width * 0.2 + 5.0;
        double offset_y = heigth + 7.0;

        textAffineTransform.translate(position.getX() + 5.0 - offset_x,
                heigth + position.getY() - offset_y - 3.5);
        rect.setRoundRect(position.getX() - offset_x,
                position.getY() - offset_y, width + 11.0,
                heigth, 15.0, 15.0);
        BasicStroke stroke=new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0.0f);
        Shape rect_bounds = stroke.createStrokedShape(rect);
        Line2D line = new Line2D.Double();
        line.setLine(position.getX(), position.getY(),position.getX() ,
                position.getY() - 5.5);
        label_shape.append(rect_bounds, false);
        rect_bounds=stroke.createStrokedShape(line);
        label_shape.append(rect_bounds,false);
        label_shape.append(textLayout.getOutline(textAffineTransform), false);
        /*label_shape.append(new Ellipse2D.Double(position.getX(), position.getY(), 2.0,
        2.0), false);
        label_shape.append(new Ellipse2D.Double(position.getX() - offset_x / 4.0,
        position.getY() - 5.0, 5.0, 3.0), false);*/
    }
    public void transform(AffineTransform at) {
        label_shape.transform(at);
    }
    public Rectangle getBounds() {
        return label_shape.getBounds();
    }
    public Rectangle2D getBounds2D() {
        return label_shape.getBounds2D();
    }
    public boolean contains(double x, double y) {
        return rect.contains(x, y);
    }
    public boolean contains(Point2D p) {
        return rect.contains(p);
    }
    public boolean intersects(double x, double y, double w, double h) {
        return intersects(x, y, w, h);
    }
    public boolean intersects(Rectangle2D r) {
        return intersects(r);
    }
    public boolean contains(double x, double y, double w, double h) {
        return rect.contains(x, y, w, h);
    }
    public boolean contains(Rectangle2D r) {
        return rect.contains(r);
    }
    public PathIterator getPathIterator(AffineTransform at) {
        return label_shape.getPathIterator(at);
    }
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return label_shape.getPathIterator(at, flatness);
    }
    public RoundRectangle2D getRoundRectangle(){
        return rect;
    }
}