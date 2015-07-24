package roadmapproject;

import java.awt.*;

public interface RTextLabel {
  public void showLabel(Graphics2D g2d);
  public void setLabel(String label);
   public void setLocation(Point2DSerializable p);
}