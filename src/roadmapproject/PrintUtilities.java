package roadmapproject;

import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

public class PrintUtilities implements Printable {
  protected Component componentToBePrinted;
  public static void printComponent(Component c) {
    new PrintUtilities(c).print();
  }
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
   public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  // General print routine for JDK 1.2. Use PrintUtilities2
  // for printing in JDK 1.3.
  public int print(Graphics g, PageFormat pageFormat,
                   int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pageFormat.getImageableX(),
                    pageFormat.getImageableY());
      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on,
   *  so this turns it off globally.  This step is only
   *  required in JDK 1.2.
   */

  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager =
                      RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Reenables double buffering globally. This step is only
   *  required in JDK 1.2.
   */

  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager =
                      RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}