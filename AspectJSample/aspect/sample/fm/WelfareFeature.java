package aspect.sample.fm;

public class WelfareFeature extends OrFeature {

	public WelfareFeature() {
		super("Welfare");
		{ Feature ch = new CalmFeature();
		getChildren().add(ch); }
		{ Feature ch = new UpstairsFeature();
		getChildren().add(ch); }
		{ Feature ch = new ElectricityFeature();
		getChildren().add(ch); }
	}

}
