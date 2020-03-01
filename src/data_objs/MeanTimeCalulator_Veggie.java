package data_objs;

import mensamodell.SharedStuff;
import mensamodell.Student;

public class MeanTimeCalulator_Veggie {

	SharedStuff sharedstuff;

	public MeanTimeCalulator_Veggie(SharedStuff sharedstuff) {
		this.sharedstuff = sharedstuff;
	}

	public double calcMeanSpentTicks() {
		if (sharedstuff.studierendeVeggie.size() == 0)
			return 0;
		int sum = 0;
		for (Student s : sharedstuff.studierendeVeggie) {
			if (!sharedstuff.students_that_left.contains(s))
				sum = sum + s.spentTicks;
		}
		return sum / sharedstuff.studierendeVeggie.size();
	}

}
