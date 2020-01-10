package mensamodell;

import javax.media.j3d.Shape3D;

import repast.simphony.query.space.continuous.ContinuousWithin;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.util.collections.FilteredIterator;
import repast.simphony.visualization.visualization3D.ShapeFactory;

public class Kasse {
	int x;
	int y;
	private ContinuousSpace space;
	private double payRange = 3;
	
	
	public Kasse(int x, int y, ContinuousSpace s) {
		this.x = x;
		this.y = y;
		this.space = s;
	}
	
  public Shape3D getVSpatial(Object agent, Shape3D spatial) { 
    if (spatial == null) {
      
    }
    spatial = ShapeFactory.createCube(4, 16); // You can use it to create circles, rectangles, images, or any shape by passing in a java.awt.Shape
    return spatial;
  }

  // Prueft ob studenten entfernt werden koennen
  public boolean pay(Student s) {
	  ContinuousWithin StudentPayRange = new ContinuousWithin(space, this, payRange);
		for (Object k : StudentPayRange.query()) {
			// Wenn student s in pay Range 
			if (k instanceof Student && k == s) {
				return true;
			}
		}
	  return false;
  }
}
