package styles;

import java.awt.Color;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.ShapeFactory;
//import PredatorPrey.agents.Sheep;
//import PredatorPrey.agents.Wolf;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;


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

  public Shape3D getVSpatial(Object agent, Shape3D spatial) { 
    if (spatial == null) {
      
    }
    spatial = ShapeFactory.createCube(4, 16); // You can use it to create circles, rectangles, images, or any shape by passing in a java.awt.Shape
    return spatial;
  }	
	
}