package aspect.sample.fm;

public class UpstairsFeature extends OrFeature {

	public UpstairsFeature() {
		super("Upstairs");
		{ Feature ch = new DogFeature();
		getChildren().add(ch); }
		{ Feature ch = new PitchingFeature();
		getChildren().add(ch); }
	}

}
