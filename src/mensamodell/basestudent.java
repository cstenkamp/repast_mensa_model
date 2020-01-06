package mensamodell;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class basestudent {
	
	int food_preference; // 0=veggie, 1=vegan, 2=meat, 3=no_preference 
	int movement; // 0=chaotic, 1=goal oriented, 2=constant

	// choose randomly 	
	public basestudent() {
		this.food_preference = RandomHelper.nextIntFromTo(0, 3);
		this.movement = RandomHelper.nextIntFromTo(0, 2);
	}
	
	// choose only one preference
	public basestudent(int value, boolean food_pref) {
		// test
		// if foodpref: value in 0,1,2,3, else in 0,1,2
		
		if(food_pref) {
			this.food_preference = value;
			this.movement = RandomHelper.nextIntFromTo(0, 2);
		}else {
			this.food_preference = RandomHelper.nextIntFromTo(0, 3);
			this.movement = value;
		}
	}
	
	// initialise both preferences
	public basestudent(int food_pref, int move_pref) {
		this.food_preference = food_pref;
		this.movement = move_pref; 
	}
	
	
	public double[] destination() {
		double koord[] = new double[2];
		if (this.movement == 0) {
			// randomly choose a destination in a specific search radius
		} 
		if (this.movement == 1) {
			//	search for your previously picked meal in a big search radius		
		}
		if (this.movement == 2) {
			// look for the meals in a constant order
		}
		
		
		return koord;
	}
	
	
	public boolean select_meal() {
		return true;
	}
	
	
	
	
	
	@ScheduledMethod(start = 0, interval = 1, shuffle = true)
	public void step() {
		//pass
	}
	
		
}
