package mensamodell;

import java.awt.Color;
import java.awt.Font;

import saf.v3d.scene.Position;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class ObjectStyle2D extends DefaultStyleOGL2D {
	
	/**
	 * Methode wird aufgerufen, wenn die Farbe der Elemente angefordert wird.
	 */
	@Override
	public Color getColor(Object agent) {
		Object o = (Object) agent;
		if (o instanceof StudentChaotic) return Color.RED;
		else if (o instanceof StudentShortestQueue) return Color.MAGENTA;
		else if (o instanceof StudentGoalOriented) return Color.ORANGE;
		else if (o instanceof Ausgabe) return Color.BLUE;
		else if (o instanceof MensaEingang) return Color.BLACK;
		else return null;
	}
	
	@Override
	public Font getLabelFont(Object o) {
		Font f;
		if (o instanceof Student) f = new Font(getLabel(o), 0, 10);
		else f = new Font(getLabel(o), 0, 15);
	    return f;
	}
	
	@Override
	public Position getLabelPosition(Object o) {
		if (o instanceof Student) {
			return Position.CENTER;
		}
		else if (o instanceof Kasse) {
			return Position.NORTH;
		}
		else if (o instanceof Ausgabe) {
			return Position.SOUTH;
		}
		else if (o instanceof MensaEingang) {
			return Position.NORTH;
		}
		else return Position.NORTH;
	}
	
	@Override
	public String getLabel(Object agent) {
		Object o = (Object) agent;
		String s;
		if (o instanceof Student) {
			s = String.valueOf(((Student) o).num);
			return s;
		} else if (o instanceof Kasse ) {
			s = "Kasse";
			return s;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.SALATBAR) {
			s = "Salatbar";
			return s;
		}
		else if (o instanceof Ausgabe) {		// TODO Zeige Fleisch, Vegan, Veggie  
			s = String.valueOf(((Ausgabe) o).kind);
			String d = "Ausgabe ";
			return d + s;
		}
		else if (o instanceof MensaEingang) {
			s = "Eingang";
			return s;
		}
		else return null;
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial)	 {
		
	    //muss nur 1* aufgerufen werden, wenn noch keine Form festgelegt ist.
		if (spatial == null) {
	      spatial = shapeFactory.createRectangle(15, 15);	//Rechteck mit Groesse
	    }
	    return spatial;
	}
}