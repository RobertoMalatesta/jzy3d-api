package org.jzy3d.plot3d.rendering.view;


/**
 * <img src="doc-files/layout2D.png"/> 
 * 
 * // PB1 : avec 200 ou 500 samples, view2D calcule une camera qui n'affiche rien
 * 
 * 
 * DONE : changement 2D/3D applique la modif immédiatement, même si animation stop
 * 
 * ------------------------
 * DONE : axe en double sur emulGL, ajoute un fix sur le delete texte (should delete image also if ledend)
 * 
 * ------------------------
 * DONE : configurer la vue avec des paramètres 2D
 * 
 * TODO : considérer la taille des textes.
 * 
 * 
 * ------------------------
 * PB5 : native prend en compte la colorbar sur le côté, mais pas emulgl qui a un viewport qui s'étale sur toute la longueur.
 * 
 * >> soit on évite d'appliquer le stretch qui force la vue 2D à étaler jusqu'au bord de l'écran
 * >> soit on adapte NativeGL pour que la colorbar s'affiche au dessus comme EmulGL
 * >> soit on adapte EmulGL pour pouvoir composer le viewport avec la colorbar sur le côté, en s'appuyant sur la formule PIX x (bounds/canvas)
 *    c'est pratique de pouvoir stretch la 3D sans réfléchir à la position de la colorbar et avoir garantie de non débordement.
 * >> soit on adapte EmuLGL pour prendre des bounds plus grand qui vont permettre de créer un décallage à droite
 * 
 *
 *
 * TO BE TESTED
 * - Y axis rotate / or not
 * - negative values
 * 
 * Take values with x range != y range, include negative and positive values
 * 
 *
 */
public class View2DLayout {
  protected View view;

  protected boolean textAddMargin = true;

  /** Distance between axis and tick labels (hence, length of the tick) */
  protected float xTickLabelsDistance = 0;
  /** Distance between tick labels and axis label */
  protected float xAxisLabelsDistance = 0;
  /** Distance between axis and tick labels (hence, length of the tick) */
  protected float yTickLabelsDistance = 0;
  /** Distance between tick labels and axis label */
  protected float yAxisLabelsDistance = 0;

  /** Extra margin */
  protected float marginLeft = 0;
  /** Extra margin */
  protected float marginRight = 0;
  /** Extra margin */
  protected float marginTop = 0;      
  /** Extra margin */
  protected float marginBottom = 0;

  
  public View2DLayout(View view) {
    this.view = view;
    
    setMarginHorizontal(10);
    setMarginVertical(10);
    setTickLabelDistance(10);
    setAxisLabelDistance(10);
  }
  
  public void setTickLabelDistance(float dist) {
    setxTickLabelsDistance(dist);
    setyTickLabelsDistance(dist);
  }

  public void setAxisLabelDistance(float dist) {
    setxAxisLabelsDistance(dist);
    setyAxisLabelsDistance(dist);
  }

  public void setMargin(float margin) {
    setMarginHorizontal(margin);
    setMarginVertical(margin);
  }

  /**
   * Set the same margin to left and right canvas borders
   */
  public void setMarginHorizontal(float margin) {
    setMarginLeft(margin);
    setMarginRight(margin);
  }

  /**
   * Set the same margin to top and bottom canvas borders
   */
  public void setMarginVertical(float margin) {
    setMarginTop(margin);
    setMarginBottom(margin);
  }
  
  public float getMarginLeft() {
    return marginLeft;
  }

  public void setMarginLeft(float marginLeft) {
    this.marginLeft = marginLeft;
  }

  public float getMarginRight() {
    return marginRight;
  }

  public void setMarginRight(float marginRight) {
    this.marginRight = marginRight;
  }

  public float getMarginTop() {
    return marginTop;
  }

  public void setMarginTop(float marginTop) {
    this.marginTop = marginTop;
  }

  public float getMarginBottom() {
    return marginBottom;
  }

  public void setMarginBottom(float marginBottom) {
    this.marginBottom = marginBottom;
  }

  public boolean isTextAddMargin() {
    return textAddMargin;
  }

  public void setTextAddMargin(boolean keepTextVisible) {
    this.textAddMargin = keepTextVisible;
  }
  
  

  public float getxTickLabelsDistance() {
    return xTickLabelsDistance;
  }

  public void setxTickLabelsDistance(float xAxisTickLabelsDistance) {
    this.xTickLabelsDistance = xAxisTickLabelsDistance;
  }

  public float getxAxisLabelsDistance() {
    return xAxisLabelsDistance;
  }

  public void setxAxisLabelsDistance(float xAxisNameLabelsDistance) {
    this.xAxisLabelsDistance = xAxisNameLabelsDistance;
  }

  public float getyTickLabelsDistance() {
    return yTickLabelsDistance;
  }

  public void setyTickLabelsDistance(float yAxisTickLabelsDistance) {
    this.yTickLabelsDistance = yAxisTickLabelsDistance;
  }

  public float getyAxisLabelsDistance() {
    return yAxisLabelsDistance;
  }

  public void setyAxisLabelsDistance(float yAxisNameLabelsDistance) {
    this.yAxisLabelsDistance = yAxisNameLabelsDistance;
  }

  public void apply() {
    view.getChart().render();
  }
}
