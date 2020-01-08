package mensamodell;

import javax.media.j3d.Shape3D;
import repast.simphony.visualization.visualization3D.ShapeFactory;

public class Kasse {
	int x;
	int y;
	
	
	public Kasse(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
  public Shape3D getVSpatial(Object agent, Shape3D spatial) { 
    if (spatial == null) {
      
    }
    spatial = ShapeFactory.createCube(4, 16); // You can use it to create circles, rectangles, images, or any shape by passing in a java.awt.Shape
    return spatial;
  }
	
}
