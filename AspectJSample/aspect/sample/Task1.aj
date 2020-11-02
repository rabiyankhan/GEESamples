package aspect.sample;

public aspect Task1 {
	pointcut isProduct() : call(* isProduct(*));

	after() returning(Object obj) : isProduct() {
		System.out.println(thisJoinPoint.getSignature());
	}

}
