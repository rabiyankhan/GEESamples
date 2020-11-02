package aspect.sample;

import java.util.*;

import aspect.sample.fm.Feature;
import aspect.sample.fm.*;

public aspect Task3 {


	private static Set<Float> productWeights = new TreeSet<Float>();

	pointcut getfmWeight() : call(boolean aspect.sample.fm.FeatureModel.isProduct(..));

	pointcut getProduct(Feature feature): call(boolean aspect.sample.fm.Feature.isProduct(..)) && target(feature);
	
	before(): getfmWeight() {
		System.out.println("getfmweight invoked");
		//productWeights.clear();
	}
	
	after(Feature feature) returning : getProduct(feature){
		System.out.println("After isproduct from Feature");
		productWeights.add(InventorySystem.getWeight(feature)); 
	}

	after() returning : getfmWeight() {
		System.out.println("Minimum weight is : " + productWeights.iterator().next());
	}

}
