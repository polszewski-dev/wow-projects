package wow.commons.model.attribute;

import wow.commons.model.Percent;
import wow.commons.model.talent.TalentTree;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-02-08
 */
public sealed interface AttributeScaling {
	NoScaling NONE = new NoScaling();

	LevelScaling LEVEL = new LevelScaling();

	record NoScaling() implements AttributeScaling {
		@Override
		public double getScaledValue(double value, AttributeScalingParams params) {
			return value;
		}
	}

	record LevelScaling() implements AttributeScaling {
		@Override
		public double getScaledValue(double value, AttributeScalingParams params) {
			return value * params.getLevel();
		}
	}

	record NumberOfEffectsOnTarget(
			TalentTree tree,
			Percent max
	) implements AttributeScaling {
		public NumberOfEffectsOnTarget {
			Objects.requireNonNull(tree);
			Objects.requireNonNull(max);
		}

		@Override
		public double getScaledValue(double value, AttributeScalingParams params) {
			var scaledValue = value * params.getNumberOfEffectsOnTarget(tree);

			return Math.min(scaledValue, max.value());
		}

		static NumberOfEffectsOnTarget parse(String value) {
			var parts = value.split(",");
			var treeStr = parts[0].trim();
			var maxStr = parts[1].trim();

			return new NumberOfEffectsOnTarget(TalentTree.parse(treeStr), Percent.parse(maxStr));
		}
	}

	double getScaledValue(double value, AttributeScalingParams params);

	static AttributeScaling parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		return switch (value) {
			case "none" -> NONE;
			case "level" -> LEVEL;
			default -> NumberOfEffectsOnTarget.parse(value);
		};
	}
}
