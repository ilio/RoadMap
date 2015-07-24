package roadmapproject;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Igor
 * Date: Dec 22, 2002
 * Time: 2:58:31 AM
 */
public class BlankIcon implements Icon {
  private Color fillColor;
  private int size;

  public BlankIcon() {
    this(null, 11);
  }

  public BlankIcon(Color color, int size) {
    //UIManager.getColor("control")
    //UIManager.getColor("controlShadow")
    fillColor = color;

    this.size = size;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (fillColor != null) {
      g.setColor(fillColor);
      g.drawRect(x, y, size-1, size-1);
    }
  }

  public int getIconWidth() {
    return size;
  }

  public int getIconHeight() {
    return size;
  }
}
