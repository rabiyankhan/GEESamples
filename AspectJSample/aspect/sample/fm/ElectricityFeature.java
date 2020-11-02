package aspect.sample.fm;

public class ElectricityFeature extends XorFeature {

	public ElectricityFeature() {
		super("Electricity");
		{ Feature ch = new EpoxyFeature();
		getChildren().add(ch); }
		{ Feature ch = new BeginnerFeature();
		getChildren().add(ch); }
		{ Feature ch = new PickleFeature();
		getChildren().add(ch); }
	}

}
