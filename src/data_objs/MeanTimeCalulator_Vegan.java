package data_objs;

import mensamodell.SharedStuff;
import mensamodell.Student;

public class MeanTimeCalulator_Vegan {

	SharedStuff sharedstuff;

	public MeanTimeCalulator_Vegan(SharedStuff sharedstuff) {
		this.sharedstuff = sharedstuff;
	}
	
	public double calcMeanSpentTicks() {
		if (sharedstuff.studierendeVegan.size() == 0) 
			return 0;
		int sum = 0;
		for (Student s : sharedstuff.studierendeVegan) {
			if (!sharedstuff.students_that_left.contains(s))
				sum = sum + s.spentTicks;
		}
		return sum / sharedstuff.studierendeVegan.size();
	}

}
