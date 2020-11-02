package aspect.sample.fm;

public class DogFeature extends XorFeature {

	public DogFeature() {
		super("Dog");
		{ Feature ch = new MarmaladeFeature();
		getChildren().add(ch); }
		{ Feature ch = new VisitorFeature();
		getChildren().add(ch); }
		{ Feature ch = new DebateFeature();
		getChildren().add(ch); }
		{ Feature ch = new ElephantFeature();
		getChildren().add(ch); }
	}

}
