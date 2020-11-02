package aspect.sample.fm;

public class Hahk1FeatureModel extends FeatureModel {

	public Hahk1FeatureModel() {
		super(new WelfareFeature());
	}
	
	public void init() {
		getRoot().setFeatureModel(this);
	}

}
