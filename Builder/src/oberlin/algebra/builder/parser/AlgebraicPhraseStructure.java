package oberlin.algebra.builder.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import oberlin.builder.parser.Parser;
import oberlin.builder.parser.PhraseStructure;
import oberlin.builder.parser.SourcePosition;
import oberlin.builder.parser.ast.AST;
import oberlin.builder.parser.ast.EOT;
import oberlin.algebra.builder.nodes.*;

public class AlgebraicPhraseStructure implements PhraseStructure {
	
	private Map<Class<? extends AST>, BiFunction<Parser<?>, 
		SourcePosition, ? extends AST>> map = new HashMap<>();
	{
		map.put(Program.class, new BiFunction<Parser<?>, SourcePosition, AST>() {
			@Override
			public Program apply(Parser<?> parser, SourcePosition position) {
				Program program = null;
				SourcePosition previous = parser.getPreviousTokenPosition();
				AST currentToken = parser.getCurrentToken();
				
				Equality equality = (Equality) parser.getVisitor()
						.visit(Equality.class, parser, previous);
				program = new Program(previous, equality);
				
				if(!(currentToken instanceof EOT)) {
					parser.syntacticError("Expected end of program",
							currentToken.getClass().toString());
				}
				
				return program;
			}
		});
		map.put(Equality.class, new BiFunction<Parser<?>, SourcePosition, AST>() {

			@Override
			public AST apply(Parser<?> parser, SourcePosition position) {
				Equality equality = null;
				List<AST> nodes = new ArrayList<>();
				SourcePosition operationPosition = new SourcePosition();
				
				parser.start(operationPosition);
				//parse operation
				AST operation = parser.getVisitor().visit(Operation.class,
						parser, operationPosition);
				nodes.add(operation);
				if(parser.getCurrentToken() instanceof Equator) {
					nodes.add(parser.getCurrentToken());
					parser.forceAccept();
					nodes.add(parser.getVisitor().visit(Operation.class, parser,
							operationPosition));
				} else {
					parser.syntacticError("Expected: equator", Integer.toString(
							parser.getCurrentToken().getPosition().getStart()));
				}
				parser.finish(operationPosition);
				
				equality = new Equality(operationPosition, nodes);
				return equality;
			}
			
		});
		map.put(Operation.class, new BiFunction<Parser<?>, SourcePosition, AST>() {

			@Override
			public AST apply(Parser<?> parser, SourcePosition position) {
				
				Operation operation = null;
				List<AST> nodes = new ArrayList<>();
				SourcePosition operationPosition = new SourcePosition();
				
				parser.start(operationPosition);
				//parse identifier
				AST identifier = parser.getVisitor().visit(Identifier.class,
						parser, operationPosition);
				nodes.add(identifier);
				//look for operator
				if(parser.getCurrentToken() instanceof Operator) {
					nodes.add(parser.getCurrentToken());
					parser.forceAccept();
					nodes.add(parser.getVisitor().visit(Operation.class,
							parser, operationPosition));
				}
				parser.finish(operationPosition);
				
				operation = new Operation(operationPosition, nodes);
				return operation;
			}
			
		});
		map.put(Identifier.class, new BiFunction<Parser<?>, SourcePosition, AST>() {

			@Override
			public AST apply(Parser<?> parser, SourcePosition position) {
				
				Identifier identifier = null;
				List<AST> nodes = new ArrayList<>();
				SourcePosition identifierPosition = new SourcePosition();
				
				parser.start(identifierPosition);
				if(parser.getCurrentToken() instanceof LParen) {
					nodes.add(parser.getCurrentToken());
					parser.forceAccept();
					
					nodes.add(getHandlerMap().get(Operation.class)
							.apply(parser, identifierPosition));
					parser.accept(Operation.class);
					
					nodes.add(parser.getCurrentToken());
					parser.accept(RParen.class);
				} else if(parser.getCurrentToken() instanceof Nominal) {
					nodes.add(parser.getCurrentToken());
					parser.forceAccept();
				} else if(parser.getCurrentToken() instanceof Numeric) {
					nodes.add(parser.getCurrentToken());
					parser.forceAccept();
				} else {
					parser.syntacticError("Nominal or numeric token expected",
							parser.getCurrentToken().getClass().toString());
				}
				parser.finish(identifierPosition);
				identifier = new Identifier(identifierPosition, nodes);
				
				return identifier;
			}
			
		});
	}
	
	@Override
	public Map<Class<? extends AST>, BiFunction<Parser<?>, SourcePosition, ? extends AST>> getHandlerMap() {
		// TODO Auto-generated method stub
		return map;
	}
	
	
}
