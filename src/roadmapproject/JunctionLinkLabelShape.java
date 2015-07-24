package roadmapproject;

/**
 * Created by IntelliJ IDEA.
 * User: iXor
 * Date: Dec 19, 2002
 * Time: 12:46:05 AM
 * To change this template use Options | File Templates.
 */

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

public class JunctionLinkLabelShape implements Shape {

    private GeneralPath label_shape;
    private Point2DSerializable position;
    private RoundRectangle2D rect;
    public JunctionLinkLabelShape() {
        label_shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        position = new Point2DSerializable(true);
        rect=new RoundRectangle2D.Double();
    }

    public JunctionLinkLabelShape(String label, Point2DSerializable position) {
        this();
        setLabel(label, position);
    }

    public void setLabel(String text, Point2DSerializable position) {
        if(text==null||text.length()<1){
            return;
        }
        int ind_token;
        ind_token=text.indexOf("::");
        if(ind_token>0){
            text=text.substring(0,ind_token);
            System.out.println("TEXT="+text);
        }
        int font_size;
        double add_coof;
        switch(text.length()){
            case 1:
                font_size=15;
                add_coof=5.0;
                break;
            case 2:
                font_size=13;
                add_coof=1.5;
                break;
            case 3:
                font_size=11;
                add_coof=1.25;
                break;
            case 4:
                font_size=10;
                add_coof=1.2;
                break;
            default:
                font_size=6;
                add_coof=1.0;
                text=text.substring(0,4);
        }
        this.position.setLocation(position);
        this.label_shape.reset();
        TextLayout textLayout = new TextLayout(text,
                new Font("system", Font.PLAIN,font_size*2),
                new FontRenderContext(null, false, false));
        AffineTransform textAffineTransform = new AffineTransform();
        double heigth = textLayout.getDescent() + textLayout.getAscent();
        double width = (textLayout.getBounds().getWidth())*add_coof;
        double offset_x = -width * 0.5;
        double offset_y = -heigth *0.5;

        textAffineTransform.translate(position.getX()  + offset_x+1.5*add_coof,
                (heigth + position.getY() + offset_y )*0.95);
        rect.setRoundRect(position.getX() + offset_x+1.0,
                position.getY() + offset_y, width ,
                heigth, 5.0, 5.0);
        BasicStroke stroke=new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0.0f);
        Shape rect_bounds;
        rect_bounds = stroke.createStrokedShape(rect);
        label_shape.append(rect_bounds, false);
        label_shape.append(textLayout.getOutline(textAffineTransform), false);
       // label_shape.append(new Ellipse2D.Double(position.getX(),position.getY(),2.0,2.0),false);
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