package aspect.sample.fm;

import java.util.Set;

public class FeatureModel {

	private Feature root;
	
	public FeatureModel(Feature root) {
		this.root = root;
	}
	
	public boolean isProduct(Set<String> product) {
		return root.isProduct(product);
	}

	
	public Feature getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
