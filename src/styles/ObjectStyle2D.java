package styles;

import java.awt.Color;
import java.awt.Font;

import mensamodell.Ausgabe;
import mensamodell.Kasse;
import mensamodell.MensaEingang;
import mensamodell.Student;
import mensamodell.StudentChaotic;
import mensamodell.StudentGoalOriented;
import mensamodell.StudentShortestQueue;
import mensamodell.consts;
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
		String d = "";
		
		if (o instanceof Ausgabe) {
			if (((Ausgabe)o).essen == consts.ESSEN_VEGAN) d = "\n(vegan)";
			else if (((Ausgabe)o).essen == consts.ESSEN_VEGGIE) d = "\n(vegetarisch)";
			else if (((Ausgabe)o).essen == consts.ESSEN_MEAT) d = "\n(fleisch)";
			else d = "\n(salat)";
		}	
		
		if (o instanceof Student) {
			s = String.valueOf(((Student) o).num);
			return s;
		} else if (o instanceof Kasse ) {
			s = "Kasse";
			return s;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.SALATBAR) {
			s = "Salatbar";
			return s;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.AKTIONSTHEKE) {
			s = "Aktion";
			return s+d;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.FLEISCHTHEKE) {
			s = "Fleisch";
			return s;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.POMMES) {
			s = "Pommes";
			return s;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.VEGGIETHEKE) {
			s = "Veggie";
			return s;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.EINTOPF) {
			s = "Eintopf";
			return s+d;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.SCHNELLERTELLER) {
			s = "Schnellerteller";
			return s+d;
		} else if (o instanceof Ausgabe && ((Ausgabe) o).kind == consts.VEGANTHEKE) {
			s = "Vegan";
			return s;
		} else if (o instanceof MensaEingang) {
			s = "Eingang";
			return s;
		} else return null;
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
