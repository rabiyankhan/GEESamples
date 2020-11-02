package aspect.sample.fm;

import java.util.Set;

public class AtomicFeature extends Feature {

	public AtomicFeature(String name) {		
		setName(name);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean isProduct(Set<String> product) {
		return product.size() == 1 && product.contains(getName());
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
