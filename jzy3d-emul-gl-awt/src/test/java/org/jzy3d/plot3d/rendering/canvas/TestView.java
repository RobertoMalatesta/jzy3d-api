package org.jzy3d.plot3d.rendering.canvas;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.mocks.jzy3d.Mocks;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPITwoFontSizesPolicy;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;

public class TestView {
  @Test
  public void whenPixelScaleChange_ThenTextFont_ofAxisAndColorbar_Changes() {
    
    HiDPITwoFontSizesPolicy fontSizePolicyUnderTest;
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = surface();
    chart.add(surface);
    AWTColorbarLegend legend = chart.colorbar(surface);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    Font font_NoHiDPI = new Font("font_NoHiDPI", 10);
    Font font_HiDPI = new Font("font_HiDPI", 20);
    
    // Configure policy that is under test
    fontSizePolicyUnderTest = new HiDPITwoFontSizesPolicy(chart.getView());
    fontSizePolicyUnderTest.setFontHiDPI(font_HiDPI);
    fontSizePolicyUnderTest.setFontNoHiDPI(font_NoHiDPI);
    chart.getAxisLayout().setFontSizePolicy(fontSizePolicyUnderTest);
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(2, 2); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size

    // Then
    Assert.assertEquals(font_HiDPI, chart.getAxisLayout().getFont());
    Assert.assertEquals(font_HiDPI, legend.getFont());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    // Then
    Assert.assertEquals(font_NoHiDPI, chart.getAxisLayout().getFont());
    Assert.assertEquals(font_NoHiDPI, legend.getFont());
  }
  
  @Test
  public void whenPixelScaleChange_ThenFontPolicyChangeSizeProportionnaly() {
    
    HiDPIProportionalFontSizePolicy fontSizePolicyUnderTest;
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = surface();
    chart.add(surface);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    // Configure policy that is under test
    fontSizePolicyUnderTest = new HiDPIProportionalFontSizePolicy(chart.getView());
    chart.getAxisLayout().setFontSizePolicy(fontSizePolicyUnderTest);
    chart.getAxisLayout().setFont(new Font("BaseFont-Size-10", 10));
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size

    // Then
    Assert.assertEquals(10, chart.getAxisLayout().getFont().getHeight());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1.5, 1.5); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size

    // Then
    Assert.assertEquals(15, chart.getAxisLayout().getFont().getHeight());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(2, 2); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    // Then
    Assert.assertEquals(20, chart.getAxisLayout().getFont().getHeight());
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    // Then
    Assert.assertEquals(10, chart.getAxisLayout().getFont().getHeight());

  }
  
  @Test
  public void whenPixelScaleIsNan_ThenViewKeepsPixelScaleAt1() {
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    
    // -----------------------------------
    // When init

    // Then
    Assert.assertEquals(new Coord2d(1,1), chart.getView().getPixelScale());
    
    // -----------------------------------
    // When NAN
    canvas.firePixelScaleChanged(Double.NaN, Double.NaN); 

    // Then
    Assert.assertEquals(new Coord2d(1,1), chart.getView().getPixelScale());

    // -----------------------------------
    // When NAN partial
    canvas.firePixelScaleChanged(2, Double.NaN); 

    // Then
    Assert.assertEquals(new Coord2d(2,1), chart.getView().getPixelScale());
    
    // -----------------------------------
    // When NAN partial
    canvas.firePixelScaleChanged(Double.NaN, 2); 

    // Then
    Assert.assertEquals(new Coord2d(1,2), chart.getView().getPixelScale());

    // -----------------------------------
    // When 0
    canvas.firePixelScaleChanged(0, 0); 

    // Then
    Assert.assertEquals(new Coord2d(1,1), chart.getView().getPixelScale());

    // -----------------------------------
    // When 0 partial
    canvas.firePixelScaleChanged(0, 2); 

    // Then
    Assert.assertEquals(new Coord2d(1,2), chart.getView().getPixelScale());

    // -----------------------------------
    // When 0 partial
    canvas.firePixelScaleChanged(2, 0); 

    // Then
    Assert.assertEquals(new Coord2d(2,1), chart.getView().getPixelScale());

  }
  
  
  /*@Test
  public void whenPixelScaleChange_ThenTextFont_ofAxisAndColorbar_Changes() {
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = surface();
    chart.add(surface);
    
    // -----------------------------------
    // When
    chart.view2d();


  }*/

  
  protected Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    
    //Mapper mapper = (x,y)->{return x * Math.sin(x * y);};
    
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setPolygonOffsetFillEnable(false);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f));

    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

}
