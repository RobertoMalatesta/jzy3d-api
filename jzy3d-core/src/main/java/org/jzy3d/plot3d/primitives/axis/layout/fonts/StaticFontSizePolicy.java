package org.jzy3d.plot3d.primitives.axis.layout.fonts;

import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;

/**
 * Does nothing but returning{@link IAxisLayout#getFont()}.
 * 
 * @author martin
 *
 */
public class StaticFontSizePolicy implements IFontSizePolicy{
  @Override
  public Font apply(AxisLayout layout) {
    return layout.getFont();
  }
}
