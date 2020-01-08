package styles;

import java.awt.Color;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.ShapeFactory;
//import PredatorPrey.agents.Sheep;
//import PredatorPrey.agents.Wolf;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;


public class KassenStyle extends DefaultStyleOGL2D {

//	@Override
//	public Color getColor(Object o){
//		
//		if (o instanceof Wolf)
//			return Color.DARK_GRAY;
//		
//		else if (o instanceof Sheep)
//			return Color.WHITE;
//		
//		return null;
//	}
	
	@Override
	public Color getColor(Object o){
		return Color.DARK_GRAY;
	}	
	
//	@Override
//	public float getScale(Object o) {
//		if (o instanceof Wolf)
//			return 2f;
//		
//		else if (o instanceof Sheep)
//			return 1f;
//		
//		return 1f;
//	}

	@Override
  public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
		  spatial = shapeFactory.createCircle(100, 100);
		}
    return spatial;
  }
	
}