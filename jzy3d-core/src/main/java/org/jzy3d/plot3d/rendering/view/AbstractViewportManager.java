package org.jzy3d.plot3d.rendering.view;


import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;

/**
 * An {@link AbstractViewportManager} describes an element that occupies the whole rendering
 * {@link ICanvas} or only a vertical slice of it.
 * 
 * The {@link AbstractViewportManager} also provides a utility function for debugging the slices,
 * that is the ability to display a 10*10 grid for checking the space occupied by the actual
 * viewport definition.
 * 
 * @author Martin Pernollet
 */
public abstract class AbstractViewportManager {
  private static final float AREA_LEFT = -100;
  private static final float AREA_RIGHT = +100;
  private static final float AREA_TOP = +100;
  private static final float AREA_DOWN = -100;
  private static final float GRID_STEPS = 10;
  private static final float OFFSET = 0.1f;

  protected int screenLeft = 0;
  protected int screenBottom = 0;
  protected int screenXOffset = 0;
  protected int screenYOffset = 0;
  protected int screenWidth = 0;
  protected int screenHeight = 0;
  protected int screenSquaredDim = 0;

  protected boolean screenGridDisplayed = false;
  protected ViewportMode mode = ViewportMode.RECTANGLE_NO_STRETCH;
  
  protected ViewportConfiguration lastViewPort;

  protected boolean applyWindowsHiDPIWorkaround = true;

  /**
   * Set the view port (size of the renderer).
   * 
   * @param width the width of the target window.
   * @param height the height of the target window.
   */
  public void setViewPort(int width, int height) {
    setViewPort(width, height, 0, 1);
  }

  public ViewportMode getViewportMode() {
    return mode;
  }

  public void setViewportMode(ViewportMode mode) {
    this.mode = mode;
  }

  /**
   * Set the view port (size of the renderer).
   * 
   * @param width the width of the target window.
   * @param height the height of the target window.
   * @param left the width's ratio where this subscreen starts in the target window.
   * @param right the width's ratio where this subscreen stops in the target window.
   * 
   * @throws an IllegalArgumentException if right is not greater than left.
   */
  public void setViewPort(int width, int height, float left, float right) {
    if (left >= right)
      throw new IllegalArgumentException(
          "left must be inferior to right : " + left + " | " + right);

    this.screenWidth = getSliceWidth(width, left, right);
    this.screenHeight = height;
    this.screenLeft = (int) (left * width);
    this.screenBottom = 0;
  }

  public int getSliceWidth(int width, float left, float right) {
    return Math.round(width * (right - left));
  }


  public void setViewPort(ViewportConfiguration viewport) {
    this.screenWidth = viewport.getWidth();
    this.screenHeight = viewport.getHeight();
    this.screenLeft = viewport.getX();
    this.screenBottom = viewport.getY();
  }
  
  public ViewportConfiguration getViewPort() {
    ViewportConfiguration vp =
        new ViewportConfiguration(screenWidth, screenHeight, screenXOffset, screenYOffset);
    vp.setMode(mode);
    return vp;
  }

  /** Return the viewport as it was invoked at last rendering. */
  public ViewportConfiguration getLastViewPort() {
    return lastViewPort;
  }


  /**
   * Build and return a {@link ViewportConfiguration}. Uses gl to
   * <ul>
   * <li>apply viewport with {@link IPainter#glViewport(int, int, int, int)}
   * <li>optionnaly to render the viewport debug grid
   * </ul>
   */
  public ViewportConfiguration applyViewport(IPainter painter) {

    // Workaround for https://github.com/jzy3d/jogl/issues/8
    if(applyWindowsHiDPIWorkaround) {
      Coord2d screen = apply_WindowsHiDPI_Workaround(painter, screenWidth, screenHeight);
      
      screenWidth = (int) screen.x;
      screenHeight = (int) screen.y;
    }
    
    // Stretch projection on the whole viewport
    if (ViewportMode.STRETCH_TO_FILL.equals(mode)
        || ViewportMode.RECTANGLE_NO_STRETCH.equals(mode)) {
      applyViewportRectangle(painter);
    }
    // Set the projection into the largest square area centered in the
    // window slice
    else if (ViewportMode.SQUARE.equals(mode)) {
      applyViewportSquared(painter);
    } else {
      throw new IllegalArgumentException("unknown mode " + mode);
    }

    // Render the screen grid if required
    if (screenGridDisplayed)
      renderSubScreenGrid(painter);

    return lastViewPort;
  }

  public static Coord2d apply_WindowsHiDPI_Workaround(IPainter painter, int width, int height) {
    return apply_WindowsHiDPI_Workaround(painter, new Coord2d(width, height));
  }
  
  public static Coord2d apply_WindowsHiDPI_Workaround(IPainter painter, Coord2d viewport) {
    if(painter.getOS().isWindows() && painter.getWindowingToolkit().isAWT()) {
      // We here scale the viewport by either 1 or by the ratio indicated by the JVM
      // if only the JVM is able to detect the pixel ratio and if JOGL
      // can't guess it (which is the case for Windows 10).
      Coord2d scaleHardware = painter.getCanvas().getPixelScale();
      Coord2d scaleJVM = painter.getCanvas().getPixelScaleJVM();

      //System.out.println("AbstractViewportManager.HiDPI : " + isHiDPIEnabled);
      //System.out.println("AbstractViewportManager.GPU   : " + scaleHardware);
      //System.out.println("AbstractViewportManager.JVM   : " + scaleJVM);
      
      if (painter.isJVMScaleLargerThanNativeScale(scaleHardware, scaleJVM)) {
        Coord2d scale = scaleJVM.div(scaleHardware);
        //System.out.println("AbstractViewportManager.Scale : " + scale);
        return new Coord2d (viewport.x * scale.x, viewport.y * scale.y);
      }
    }
    
    return viewport;
  }
  
  public static Coord2d getWindowsHiDPIScale_Workaround(IPainter painter) {
    if(painter.getOS().isWindows() && painter.getWindowingToolkit().isAWT()) {
      // We here scale the viewport by either 1 or by the ratio indicated by the JVM
      // if only the JVM is able to detect the pixel ratio and if JOGL
      // can't guess it (which is the case for Windows 10).
      Coord2d scaleHardware = painter.getCanvas().getPixelScale();
      Coord2d scaleJVM = painter.getCanvas().getPixelScaleJVM();

      //System.out.println("AbstractViewportManager.HiDPI : " + isHiDPIEnabled);
      //System.out.println("AbstractViewportManager.GPU   : " + scaleHardware);
      //System.out.println("AbstractViewportManager.JVM   : " + scaleJVM);
      
      if (painter.isJVMScaleLargerThanNativeScale(scaleHardware, scaleJVM)) {
        return scaleJVM.div(scaleHardware);
      }
    }
    return Coord2d.IDENTITY.clone();
  }


  protected void applyViewportRectangle(IPainter painter) {
    screenXOffset = screenLeft;
    screenYOffset = 0;
    //System.out.println("AbstractViewportManager: yoffset " + screenYOffset);

    painter.glViewport(screenXOffset, screenYOffset, screenWidth, screenHeight);

    lastViewPort =
        new ViewportConfiguration(screenWidth, screenHeight, screenXOffset, screenYOffset);
    lastViewPort.setMode(mode);
  }

  protected void applyViewportSquared(IPainter painter) {
    screenSquaredDim = Math.min(screenWidth, screenHeight);
    screenXOffset = screenLeft + screenWidth / 2 - screenSquaredDim / 2;
    screenYOffset = screenBottom + screenHeight / 2 - screenSquaredDim / 2;

    painter.glViewport(screenXOffset, screenYOffset, screenSquaredDim, screenSquaredDim);

    lastViewPort = new ViewportConfiguration(screenSquaredDim, screenSquaredDim, screenXOffset,
        screenYOffset);
    lastViewPort.setMode(mode);
  }  

  /**
   * Returns the (x,y) offset that was applied to make this {@link AbstractViewportManager} stand in
   * the appropriate canvas part. and the actual width and height of the viewport. Only relevant
   * after a call to {@link applyViewPort}.
   */
  public Rectangle getRectangle() {
    if (ViewportMode.STRETCH_TO_FILL.equals(mode)
        || ViewportMode.RECTANGLE_NO_STRETCH.equals(mode)) {
      return new Rectangle(screenXOffset, screenYOffset, screenWidth, screenHeight);
    } else {
      return new Rectangle(screenXOffset, screenYOffset, screenSquaredDim, screenSquaredDim);
    }
  }

  /********************************************************************************/

  /**
   * Set the status of the screen grid.
   * 
   * @param status the grid is displayed if status is set to true
   */
  public void setScreenGridDisplayed(boolean status) {
    screenGridDisplayed = status;
  }

  /** Renders a grid on the defined sub screen. */
  protected void renderSubScreenGrid(IPainter painter) {
    if (screenWidth <= 0)
      return;

    // Set a 2d projection
    painter.glMatrixMode_Projection();
    painter.glPushMatrix();
    painter.glLoadIdentity();

    if (ViewportMode.STRETCH_TO_FILL.equals(mode)
        || ViewportMode.RECTANGLE_NO_STRETCH.equals(mode)) {
      int screenXoffset = screenLeft;
      int screenYoffset = 0;

      painter.glViewport(screenXoffset, screenYoffset, screenWidth, screenHeight);
    }

    // Set the projection into the largest square area centered in the
    // window slice
    else {
      int dimension = Math.min(screenWidth, screenHeight);
      int screenXoffset = screenLeft + screenWidth / 2 - dimension / 2;
      int screenYoffset = screenHeight / 2 - dimension / 2;

      painter.glViewport(screenXoffset, screenYoffset, dimension, dimension);
    }

    painter.glOrtho(AREA_LEFT, AREA_RIGHT, AREA_DOWN, AREA_TOP, -1, 1);

    // Set a grid
    painter.glMatrixMode_ModelView();
    painter.glPushMatrix();
    painter.glLoadIdentity();
    painter.glColor3f(1f, 0.5f, 0.5f);
    painter.glLineWidth(1f);

    float step;

    step = (AREA_RIGHT - AREA_LEFT) / (GRID_STEPS + 0);
    for (float i = AREA_LEFT; i <= AREA_RIGHT; i += step) {
      float x = i;
      if (x == AREA_LEFT)
        x += OFFSET;
      else if (x == AREA_RIGHT)
        x -= OFFSET;

      painter.glBegin_Line();
      painter.glVertex3f(x, AREA_DOWN, 1);
      painter.glVertex3f(x, AREA_TOP, 1);
      painter.glEnd();
    }

    step = (AREA_TOP - AREA_DOWN) / (GRID_STEPS + 0);
    for (float j = AREA_DOWN; j <= AREA_TOP; j += step) {
      float y = j;
      if (y == AREA_TOP)
        y -= OFFSET;
      else if (y == AREA_DOWN)
        y += OFFSET;

      painter.glBegin_Line();
      painter.glVertex3f(AREA_LEFT, y, 1);
      painter.glVertex3f(AREA_RIGHT, y, 1);
      painter.glEnd();
    }

    // Restore matrices
    painter.glPopMatrix();
    painter.glMatrixMode_Projection();
    painter.glPopMatrix();
  }

  /** The left position of the viewport (x) */
  public int getScreenLeft() {
    return screenLeft;
  }

  /** The bottom position of the viewport (y) */
  public int getScreenBottom() {
    return screenBottom;
  }

  public int getScreenXOffset() {
    return screenXOffset;
  }

  public int getScreenYOffset() {
    return screenYOffset;
  }

  public int getScreenWidth() {
    return screenWidth;
  }

  public int getScreenHeight() {
    return screenHeight;
  }

  public boolean isScreenGridDisplayed() {
    return screenGridDisplayed;
  }

  public boolean isApplyWindowsHiDPIWorkaround() {
    return applyWindowsHiDPIWorkaround;
  }

  public void setApplyWindowsHiDPIWorkaround(boolean applyWindowsHiDPIWorkaround) {
    this.applyWindowsHiDPIWorkaround = applyWindowsHiDPIWorkaround;
  }

  public void setScreenXOffset(int screenXOffset) {
    this.screenXOffset = screenXOffset;
  }

  public void setScreenYOffset(int screenYOffset) {
    this.screenYOffset = screenYOffset;
  }
}
