package data_objs;

import mensamodell.SharedStuff;
import mensamodell.Student;

public class MeanTimeCalulator_NoPref {

	SharedStuff sharedstuff;

	public MeanTimeCalulator_NoPref(SharedStuff sharedstuff) {
		this.sharedstuff = sharedstuff;
	}

	public double calcMeanSpentTicks() {
		if (sharedstuff.studierendeNoPref.size() == 0)
			return 0;
		int sum = 0;
		for (Student s : sharedstuff.studierendeNoPref) {
			if (!sharedstuff.students_that_left.contains(s))
				sum = sum + s.spentTicks;
		}
		return sum / sharedstuff.studierendeNoPref.size();
	}

}
