package aspect.sample.fm;

import java.util.LinkedHashSet;
import java.util.Set;

public class XorFeature extends Feature {

	public XorFeature(String name) {		
		setName(name);
	}
	
	@Override
	public boolean hasChildren() {
		return !getChildren().isEmpty();
	}

	@Override
	public boolean isProduct(Set<String> product) {
		Set<String> cns = getChildrenNames();
		cns.removeAll(product);
		// exactly one child must be in the product
		if (cns.size() != getChildren().size() - 1) {
			return false;
		}
		// check that child allows the sub product
		for (Feature f : getChildren()) {
			if (product.contains(f.getName())) {
				Set<String> subProd = new LinkedHashSet<String>(product);
				subProd.remove(getName());
				return f.isProduct(subProd);
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String children = "";
		for (Feature f : getChildren()) {
			children += f.toString() + " xor ";
		}
		children = children.substring(0, children.length() - 5);
		return getName() + " (" + children + ")";
	}

}
