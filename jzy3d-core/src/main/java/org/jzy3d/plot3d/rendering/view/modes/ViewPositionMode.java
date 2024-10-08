package org.jzy3d.plot3d.rendering.view.modes;

/**
 * The {@link ViewBoundMode} allows to apply a restriction on the degree of freedom that is let on
 * the View control.
 * 
 * @author Martin Pernollet
 */
public enum ViewPositionMode {
  /** Enforce view point on top of the scene, leading to a XY axis */
  TOP,
  /** Enforce view point on profile of the scene. */
  PROFILE,
  /** No enforcement of view point: let the user freely turn around the scene. */
  FREE,
  /** Enforce view point on the side of the scene, leading to a YZ axis */  
  YZ,
  /** Enforce view point on the side of the scene, leading to a XZ axis */  
  XZ
}
