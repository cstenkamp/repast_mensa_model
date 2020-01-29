package mensamodell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.exception.NullArgumentException;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;

public class StudentPathfinder extends Student {

	private List<Ausgabe> ausgaben;
	private int random;


	public StudentPathfinder(ContinuousSpace s, Context c, int num, SharedStuff sharedstuff, int fp) {
		super(s, c, num, sharedstuff, fp);
		this.random = RandomHelper.nextIntFromTo(0,2); //er geht entweder die standard-route von vorne nach durch, oder von hinten nach vorne, oder komplett random (1/3, 1/3, 1/3)
		this.ausgaben = new ArrayList<Ausgabe>();
		generatePath();
	}

	public Vector2d move() {
		//returns null wenn er gerade was zu essen gefunden hat, nicht mehr hungrig ist, oder schon alle Theken besucht hat

		// Falls der student vor einer Ausgabe steht
		if (at_bar()) {
			if (this.hungry) {
				if (chooseMeal()) {
					this.hungry = false;
					return null;
				}
			}
		}
		
		if (!this.hungry)
			return null;

		try {
			Ausgabe closesttheke = (Ausgabe) next_aim();
			return walk_but_dont_bump(closesttheke);
		} catch (NullArgumentException e) {
			return null;
		}
	}


	public Object next_aim() {
		List<Ausgabe> nonvisited_ausgaben = new ArrayList<Ausgabe>();
		for (Ausgabe t : this.ausgaben) {
			if (!visitedAusgaben.contains(t)) nonvisited_ausgaben.add(t);
		}
		if (nonvisited_ausgaben.isEmpty()) return null;
		Ausgabe nextBar = nonvisited_ausgaben.get(0);
		return nextBar;
	}


	private void generatePath() {
		if (this.random == 0) {
			//FIX: in der Variante vorher hat der wahrscheinlich mehrfach dieselbe hinzugefügt
			//this.ausgaben.add(sharedstuff.ausgaben.get(RandomHelper.nextIntFromTo(0, sharedstuff.ausgaben.size()-1)));
			List<Integer> range = IntStream.rangeClosed(0, sharedstuff.ausgaben.size()-1).boxed().collect(Collectors.toList());
			Collections.shuffle(range);
			for (int i = 0; i < range.size(); i++) {
				this.ausgaben.add(sharedstuff.ausgaben.get(range.get(i)));
			}
		} else if (this.random == 1) {
			for (int i = 0; i < sharedstuff.ausgaben.size(); i++) {
				this.ausgaben.add(sharedstuff.ausgaben.get(i));
			}
		} else {
			for (int i = sharedstuff.ausgaben.size()-1; i >= 0; i--) {
				this.ausgaben.add(sharedstuff.ausgaben.get(i));
			}
		}
	}


}
