package data_objs;

import mensamodell.SharedStuff;
import mensamodell.Student;

public class MeanTimeCalulator_Meat {

	SharedStuff sharedstuff;

	public MeanTimeCalulator_Meat(SharedStuff sharedstuff) {
		this.sharedstuff = sharedstuff;
	}
	
	public double calcMeanSpentTicks() {
		if (sharedstuff.studierendeMeatEater.size() == 0)
			return 0;
		int sum = 0;
		for (Student s : sharedstuff.studierendeMeatEater) {
			if (!sharedstuff.students_that_left.contains(s))
				sum = sum + s.spentTicks;
		}
		return sum / sharedstuff.studierendeMeatEater.size();
	}

}
