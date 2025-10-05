package wow.commons.model.attribute.condition;

import lombok.RequiredArgsConstructor;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.DruidFormType;
import wow.commons.model.character.MovementType;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
class AttributeConditionParser {
	private final Tokenizer tokenizer;

	public AttributeConditionParser(String value) {
		this.tokenizer = new Tokenizer(value != null ? value : "");
	}

	public AttributeCondition parse() {
		tokenizer.tokenize();

		if (tokenizer.getCurrentToken().isEmpty()) {
			return AttributeCondition.EMPTY;
		}

		var result = orExpression();

		if (tokenizer.getCurrentToken().isPresent()) {
			throw new IllegalArgumentException("There are remaining tokens: " + tokenizer.value);
		}

		return result;
	}

	private AttributeCondition orExpression() {
		var left = andExpression();
		if (tokenizer.isCurrentToken("|")) {
			tokenizer.dropCurrentToken();
			var right = orExpression();
			return ConditionOperator.or(left, right);
		}
		return left;
	}

	private AttributeCondition andExpression() {
		var left = commaExpression();
		if (tokenizer.isCurrentToken("&")) {
			tokenizer.dropCurrentToken();
			var right = andExpression();
			return ConditionOperator.and(left, right);
		}
		return left;
	}

	private AttributeCondition commaExpression() {
		var left = primitiveExpression();

		if (!tokenizer.isCurrentToken(",")) {
			return left;
		}

		var conditions = new ArrayList<AttributeCondition>();

		conditions.add(left);

		do {
			tokenizer.dropCurrentToken();
			var right = primitiveExpression();
			conditions.add(right);
		} while (tokenizer.isCurrentToken(","));

		return ConditionOperator.comma(conditions);
	}

	private AttributeCondition primitiveExpression() {
		if (tokenizer.isCurrentToken("(")) {
			tokenizer.dropCurrentToken();
			var condition = orExpression();
			tokenizer.require(")");
			tokenizer.dropCurrentToken();
			return condition;
		}

		var token = tokenizer.getCurrentToken().orElseThrow();
		tokenizer.dropCurrentToken();
		return getBasicCondition(token);
	}

	private AttributeCondition getBasicCondition(String value) {
		if (value.isEmpty()) {
			return AttributeCondition.EMPTY;
		}

		var talentTree = TalentTree.tryParse(value);
		if (talentTree != null) {
			return AttributeCondition.of(talentTree);
		}

		var spellSchool = SpellSchool.tryParse(value);
		if (spellSchool != null) {
			return AttributeCondition.of(spellSchool);
		}

		var abilityId = AbilityId.tryParse(value);

		if (abilityId != null) {
			return AttributeCondition.of(abilityId);
		}

		var abilityCategory = AbilityCategory.tryParse(value);
		if (abilityCategory != null) {
			return AttributeCondition.of(abilityCategory);
		}

		var petType = PetType.tryParse(value);
		if (petType != null) {
			return AttributeCondition.of(petType);
		}

		var creatureType = CreatureType.tryParse(value);
		if (creatureType != null) {
			return AttributeCondition.of(creatureType);
		}

		var druidFormType = DruidFormType.tryParse(value);
		if (druidFormType != null) {
			return AttributeCondition.of(druidFormType);
		}

		var weaponSubType = WeaponSubType.tryParse(value);
		if (weaponSubType != null) {
			return AttributeCondition.of(weaponSubType);
		}

		var professionId = ProfessionId.tryParse(value);
		if (professionId != null) {
			return AttributeCondition.of(professionId);
		}

		var miscCondition = MiscCondition.tryParse(value);
		if (miscCondition != null) {
			return miscCondition;
		}

		var effectCategory = EffectCategory.tryParse(value);
		if (effectCategory != null) {
			return EffectCategoryCondition.of(effectCategory);
		}

		var ownerHasEffect = OwnerHasEffectCondition.tryParse(value);
		if (ownerHasEffect != null) {
			return ownerHasEffect;
		}

		var targetClass = TargetClassCondition.tryParse(value);
		if (targetClass != null) {
			return targetClass;
		}

		var ownerIsChanneling = OwnerIsChannelingCondition.tryParse(value);
		if (ownerIsChanneling != null) {
			return ownerIsChanneling;
		}

		var movementType = MovementType.tryParse(value);
		if (movementType != null) {
			return MovementTypeCondition.of(movementType);
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
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
			return Optional.of(tokens.get(0));
		}

		public boolean isCurrentToken(String expectedValue) {
			var currentToken = getCurrentToken();
			return currentToken.isPresent() && currentToken.get().equals(expectedValue);
		}

		public void dropCurrentToken() {
			tokens.remove(0);
		}

		private void require(String expectedValue) {
			if (!isCurrentToken(expectedValue)) {
				throw new IllegalArgumentException("Missing ')': " + value);
			}
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
			return c == '&' || c == '|' || c == ',';
		}
	}
}
