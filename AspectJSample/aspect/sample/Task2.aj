package aspect.sample;

import aspect.sample.fm.*;

public aspect Task2 {

	pointcut getName() : cflow(execution(* getName())) && !within(aspect.sample.Task2);

	after() returning(Object obj) : getName() {
		System.out.println("Object Name is " + obj);
	}

}
