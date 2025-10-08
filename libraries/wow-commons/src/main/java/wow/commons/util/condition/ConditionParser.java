package wow.commons.util.condition;

import lombok.RequiredArgsConstructor;
import wow.commons.model.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
public abstract class ConditionParser<T extends Condition> {
	private final Parser parser;

	protected ConditionParser(String value) {
		this.parser = new Parser(value);
	}

	public T parse() {
		var intermediateForm = parser.parse();

		if (intermediateForm == null) {
			return getEmptyCondition();
		}

		return transform(intermediateForm);
	}

	private T transform(Node node) {
		return switch (node) {
			case OrNode(var left, var right) ->
					orOperator(
							transform(left),
							transform(right)
					);

			case AndNode(var left, var right) ->
					andOperator(
							transform(left),
							transform(right)
					);

			case CommaNode(var nodes) ->
					commaOperator(
							nodes.stream()
									.map(this::transform)
									.toList()
					);

			case NotNode(var sub) ->
					notOperator(
							transform(sub)
					);

			case PrimitiveNode(var value) ->
					getBasicCondition(value);
		};
	}

	protected T orOperator(T left, T right) {
		throw new UnsupportedOperationException();
	}

	protected T andOperator(T left, T right) {
		throw new UnsupportedOperationException();
	}

	protected T notOperator(T condition) {
		throw new UnsupportedOperationException();
	}

	protected T commaOperator(List<T> conditions) {
		throw new UnsupportedOperationException();
	}

	protected abstract T getBasicCondition(String value);

	protected abstract T getEmptyCondition();

	private static class Parser {
		private final Tokenizer tokenizer;

		public Parser(String value) {
			this.tokenizer = new Tokenizer(value != null ? value : "");
		}

		public Node parse() {
			tokenizer.tokenize();

			if (tokenizer.getCurrentToken().isEmpty()) {
				return null;
			}

			var result = orExpression();

			if (tokenizer.getCurrentToken().isPresent()) {
				throw new IllegalArgumentException("There are remaining tokens: " + tokenizer.value);
			}

			return result;
		}

		private Node orExpression() {
			var left = andExpression();

			if (tokenizer.isCurrentToken("|")) {
				tokenizer.dropCurrentToken();

				var right = orExpression();

				return new OrNode(left, right);
			}

			return left;
		}

		private Node andExpression() {
			var left = commaExpression();

			if (tokenizer.isCurrentToken("&")) {

				tokenizer.dropCurrentToken();

				var right = andExpression();

				return new AndNode(left, right);
			}

			return left;
		}

		private Node commaExpression() {
			var left = primitiveExpression();

			if (!tokenizer.isCurrentToken(",")) {
				return left;
			}

			var conditions = new ArrayList<Node>();

			conditions.add(left);

			do {
				tokenizer.dropCurrentToken();

				var right = primitiveExpression();

				conditions.add(right);
			} while (tokenizer.isCurrentToken(","));

			return new CommaNode(conditions);
		}

		private Node primitiveExpression() {
			if (tokenizer.isCurrentToken("(")) {
				return parenExpression();
			}

			if (tokenizer.isCurrentToken("~")) {
				return notExpression();
			}

			var token = tokenizer.getCurrentToken().orElseThrow();

			tokenizer.dropCurrentToken();

			return new PrimitiveNode(token);
		}

		private Node parenExpression() {
			tokenizer.requireAndDrop("(");

			var condition = orExpression();

			tokenizer.requireAndDrop(")");
			return condition;
		}

		private Node notExpression() {
			tokenizer.requireAndDrop("~");

			var expression = primitiveExpression();

			return new NotNode(expression);
		}
	}

	@RequiredArgsConstructor
	private static class Tokenizer {
		private final String value;
		private int position;
		private int parenDepth;

		private final List<String> tokens = new ArrayList<>();

		public Optional<String> getCurrentToken() {
			if (tokens.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(tokens.getFirst());
		}

		public boolean isCurrentToken(String expectedValue) {
			var currentToken = getCurrentToken();
			return currentToken.isPresent() && currentToken.get().equals(expectedValue);
		}

		public void dropCurrentToken() {
			tokens.removeFirst();
		}

		public void require(String expectedValue) {
			if (!isCurrentToken(expectedValue)) {
				throw new IllegalArgumentException("Missing ')': " + value);
			}
		}

		public void requireAndDrop(String expectedValue) {
			require(expectedValue);
			dropCurrentToken();
		}

		public void tokenize() {
			while (hasChars()) {
				extractNextToken();
			}
		}

		private void extractNextToken() {
			eatWhitespaces();

			if (!hasChars()) {
				return;
			}

			char c = value.charAt(position);

			if (isOperator(c)) {
				extractOperator(c);
				return;
			}

			if (c == '(') {
				extractLeftParen(c);
				return;
			}

			if (c == ')') {
				extractRightParen(c);
				return;
			}

			extractSubstring();
		}

		private void extractRightParen(char c) {
			if (parenDepth == 0) {
				throw new IllegalArgumentException("Parens don't match: " + value);
			}
			tokens.add("" + c);
			++position;
			--parenDepth;
		}

		private void extractLeftParen(char c) {
			tokens.add("" + c);
			++position;
			++parenDepth;
		}

		private void extractOperator(char c) {
			tokens.add("" + c);
			++position;
		}

		private void extractSubstring() {
			int start = position;
			int substringParenDepth = 0;

			while (hasChars() && !isOperator(value.charAt(position))) {
				char c = value.charAt(position);
				if (c == '(') {
					++substringParenDepth;
				} else if (c == ')') {
					if (substringParenDepth == 0) {
						break;
					}
					--substringParenDepth;
				}
				++position;
			}

			tokens.add(value.substring(start, position).trim());
		}

		private void eatWhitespaces() {
			while (hasChars() && Character.isSpaceChar(value.charAt(position))) {
				++position;
			}
		}

		private boolean hasChars() {
			return position < value.length();
		}

		private boolean isOperator(char c) {
			return c == '&' || c == '|' || c == ',' || c == '~';
		}
	}

	private sealed interface Node {}

	private record OrNode(Node left, Node right) implements Node {}

	private record AndNode(Node left, Node right) implements Node {}

	private record CommaNode(List<Node> nodes) implements Node {}

	private record NotNode(Node node) implements Node {}

	private record PrimitiveNode(String value) implements Node {}
}
