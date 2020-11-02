package aspect.sample.fm;

public class CalmFeature extends OrFeature {

	public CalmFeature() {
		super("Calm");
		{ Feature ch = new LarchFeature();
		getChildren().add(ch); }
		{ Feature ch = new BoxerFeature();
		getChildren().add(ch); }
	}

}
