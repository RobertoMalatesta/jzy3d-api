package org.jzy3d.chart.graphs;

import java.util.List;

import org.apache.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.picking.IObjectPickedListener;
import org.jzy3d.picking.PickingSupport;

public class GraphChartComponentFactory extends AWTChartComponentFactory{
    static Logger logger = Logger.getLogger(GraphChartComponentFactory.class);
    
    @Override
    public ICameraMouseController newMouseCameraController(Chart chart){
        return newAWTMouseController(chart);
    }
    
    public ICameraMouseController newAWTMouseController(final Chart chart){
        AWTMousePickingController mouse = new AWTMousePickingController(chart);
        mouse.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
            @Override
            public void objectPicked(List<? extends Object> vertices, PickingSupport picking) {
                for(Object vertex: vertices){
                    logger.info("picked: " + vertex);
                    //dGraph.setVertexHighlighted((String)vertex, true);
                }
                chart.render();
            }
        });
        return mouse;
    }
}
