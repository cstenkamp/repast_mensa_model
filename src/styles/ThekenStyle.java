package styles;

import java.awt.Color;


import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

import mensamodell.consts;
import mensamodell.Theke;

public class ThekenStyle extends DefaultStyleOGL2D {

	@Override
	public Color getColor(Object o){
		return Color.BLUE;
	}	
	
	@Override
	public float getScale(Object o) {
		if (o instanceof Theke)
			return 10f;
		return 1f;
	}

	@Override
  public VSpatial getVSpatial(Object obj, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createCircle(10, 10);
			
			if (obj instanceof Theke) {
				if (((Theke)obj).size != null)
					spatial = shapeFactory.createRectangle((int)((Theke)obj).size.x, (int)((Theke)obj).size.y);
			}
		}
	  return spatial;
  }
	
}