package aspect.sample;

import aspect.sample.fm.*;
import java.util.Arrays;
import java.util.Set;

public aspect Task4 {

	pointcut crossChecking() : execution(boolean aspect.sample.fm.FeatureModel.isProduct(..));

	boolean around() : crossChecking() {
		boolean isValid = false;
		Object[] args = thisJoinPoint.getArgs();

		@SuppressWarnings("unchecked")
		Set<String> strings = (Set<String>) args[0];

		if (strings.containsAll(Arrays.asList("Pitching", "Pickle"))) {
			isValid = true;
		}
		if (strings.containsAll(Arrays.asList("Boxer", "Marmalade"))) {
			isValid = false;
		}
		System.out.println("isValid: " + isValid);
		return isValid;
	}

}
