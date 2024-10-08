package org.jzy3d.plot2d.rendering;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import org.jzy3d.maths.Coord2d;

public class AWTGraphicsUtils {

  public static BufferedImage scale(BufferedImage in, float x, float y) {
    BufferedImage out = new BufferedImage((int) (x * in.getWidth(null)),
        (int) (y * in.getHeight(null)), in.getType());
    
    Graphics2D g2d = (Graphics2D) out.getGraphics();

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    g2d.scale(x, y);

    g2d.drawImage(in, 0, 0, null);
    g2d.dispose();

    return out;
  }
  
  public static BufferedImage copy(BufferedImage source) {
    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    Graphics g = b.createGraphics();
    g.drawImage(source, 0, 0, null);
    g.dispose();
    return b;
  }


  /** Force text to be anti-aliased. */
  public static void configureRenderingHints(Graphics2D g2d) {
    RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2d.setRenderingHints(rh);
  }

  /**
   * A draw string method allowing to bypass OS font rendering if noticing font rendering glitches.
   *
   * It may be worth invoking {@link #configureRenderingHints(Graphics2D)} right before.
   */
  public static void drawString(Graphics2D g2d, Font font, boolean useOSFontRendering,
      String string, int x, int y) {
    if (useOSFontRendering) {
      g2d.setFont(font);
      g2d.drawString(string, x, y);
    } else {
      FontRenderContext frc = g2d.getFontRenderContext();
      GlyphVector gv = font.createGlyphVector(frc, string);
      g2d.drawGlyphVector(gv, x, y);
    }
  }

  public static int stringWidth(String string) {
    BufferedImage i = new BufferedImage(0,0, BufferedImage.TYPE_4BYTE_ABGR);
    
    return stringWidth(i.createGraphics(), string);
  }
  public static int stringWidth(Graphics2D g2d, String string) {
    FontMetrics fm = g2d.getFontMetrics();
    if (fm != null) {
      return fm.stringWidth(string);
    } else {
      return -1;
    }

  }

  public static void printGraphicParameters(Graphics2D g2) {
    System.out.println(
        "KEY_ALPHA_INTERPOLATION=" + g2.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION));
    System.out.println("KEY_ANTIALIASING=" + g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
    System.out
        .println("KEY_COLOR_RENDERING=" + g2.getRenderingHint(RenderingHints.KEY_COLOR_RENDERING));
    System.out.println("KEY_DITHERING=" + g2.getRenderingHint(RenderingHints.KEY_DITHERING));
    System.out.println(
        "KEY_FRACTIONALMETRICS=" + g2.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
    System.out
        .println("KEY_INTERPOLATION=" + g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION));
    System.out.println("KEY_RENDERING=" + g2.getRenderingHint(RenderingHints.KEY_RENDERING));
    System.out
        .println("KEY_STROKE_CONTROL=" + g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL));
    System.out.println(
        "KEY_TEXT_ANTIALIASING=" + g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
    // System.out.println("KEY_TEXT_LCD_CONTRAST=" +
    // g2.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST));
    System.out
        .println("KEY_STROKE_CONTROL=" + g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL));
  }
  
  public static Coord2d getPixelScale(Graphics2D g2d) {
    AffineTransform globalTransform = g2d.getTransform();
    return new Coord2d(globalTransform.getScaleX(), globalTransform.getScaleY());
  }
}
