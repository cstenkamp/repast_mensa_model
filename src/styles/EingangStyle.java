package styles;

import java.awt.Color;

import javax.media.j3d.Shape3D;

import mensamodell.Theke;
import mensamodell.consts;
import repast.simphony.visualization.visualization3D.ShapeFactory;
//import PredatorPrey.agents.Sheep;
//import PredatorPrey.agents.Wolf;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;


public class EingangStyle extends DefaultStyleOGL2D {

	
	@Override
	public Color getColor(Object o){
		return Color.LIGHT_GRAY;
	}	

	@Override
	public float getScale(Object o) {
		return 10f;
	}


	@Override
  public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
		  spatial = shapeFactory.createRectangle(consts.SIZE_X*10/5, 10);
		}
    return spatial;
  }
	
}