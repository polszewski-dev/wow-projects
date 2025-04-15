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

	record LevelScalingByFactor(double factor) implements AttributeScaling {
		@Override
		public double getScaledValue(double value, AttributeScalingParams params) {
			return value + factor * params.getLevel();
		}
	}

	record NumberOfEffectsOnTarget(TalentTree tree, Percent max) implements AttributeScaling {
		public NumberOfEffectsOnTarget {
			Objects.requireNonNull(tree);
			Objects.requireNonNull(max);
		}

		@Override
		public double getScaledValue(double value, AttributeScalingParams params) {
			var scaledValue = value * params.getNumberOfEffectsOnTarget(tree);

			return Math.min(scaledValue, max.value());
		}
	}

	double getScaledValue(double value, AttributeScalingParams params);
}
