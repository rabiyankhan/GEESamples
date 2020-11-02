package aspect.sample.fm;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class Feature {

	private String name;
	private List<Feature> children = new ArrayList<Feature>();
	private FeatureModel featureModel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Feature> getChildren() {
		return children;
	}

	public void setChildren(List<Feature> children) {
		this.children = children;
	}

	/**
	 * get names of immediate children
	 * 
	 * @return
	 */
	public Set<String> getChildrenNames() {
		Set<String> cns = new LinkedHashSet<String>();
		for (Feature f : getChildren()) {
			cns.add(f.getName());
		}
		return cns;
	}

	public Set<String> getAllFeatures() {
		Set<String> fns = new LinkedHashSet<String>();
		fns.add(name);
		for (Feature f : getChildren()) {
			fns.addAll(f.getAllFeatures());
		}
		return fns;
	}

	public abstract boolean hasChildren();

	public abstract boolean isProduct(Set<String> product);

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
		for (Feature ch : getChildren()) {
			ch.setFeatureModel(featureModel);
		}
	}
}
