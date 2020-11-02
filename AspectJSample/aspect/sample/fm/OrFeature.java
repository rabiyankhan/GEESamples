package aspect.sample.fm;

import java.util.LinkedHashSet;
import java.util.Set;

public class OrFeature extends Feature {

	public OrFeature(String name) {		
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
		// at least one child must be in the product
		if (cns.size() == getChildren().size()) {
			return false;
		}
		boolean subsOk = true;
		// check that all children in product allow their sub product
		for (Feature f : getChildren()) {
			if (product.contains(f.getName())) {
				Set<String> subProd = new LinkedHashSet<String>(product);
				subProd.remove(getName());
				for (Feature cf : getChildren()) {
					if (cf != f) {
						subProd.removeAll(cf.getAllFeatures());
					}
				}
				subsOk &= f.isProduct(subProd);
			}
		}
		return subsOk;
	}
	
	@Override
	public String toString() {
		String children = "";
		for (Feature f : getChildren()) {
			children += f.toString() + " or ";
		}
		children = children.substring(0, children.length() - 4);
		return getName() + " (" + children + ")";
	}

}
