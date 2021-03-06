package oberlin.builder;

import java.util.List;
import java.util.logging.*;

import oberlin.builder.scanner.*;
import oberlin.builder.parser.*;
import oberlin.builder.parser.ast.*;

public abstract class Builder {
	private Logger logger = Logger.getLogger("Builder");
	private Scanner scanner;
	
	public Object build(String code) throws BuilderException {
		List<AST> tokens = (List<AST>) scanner.apply(new Terminal(code));
		
		//TODO: Generate AST with Parser
		
		//TODO: Generate Object Program with code generator
		//TODO: Return Object Program
		
		//TODO: At this point, objectProgram should be our completed and runnable binary. The following code is more for
		//embedded testing than anything else, and should be removed before release.
		
		StringBuilder szBuilder = new StringBuilder();
		for(AST token : tokens) {
			szBuilder.append(token).append("; ");
		}
		return szBuilder.toString();
		
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

}
