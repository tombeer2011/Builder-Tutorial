package oberlin.algebra.builder;

import oberlin.algebra.builder.scanner.AlgebraicScanner;
import oberlin.algebra.builder.scanner.AlgebraicScanner;
import oberlin.builder.*;

public class AlgebraicBuilder extends Builder {

	public AlgebraicBuilder() {
		 setScanner(new AlgebraicScanner());
	}

}
