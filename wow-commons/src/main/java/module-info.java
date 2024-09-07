/**
 * User: POlszewski
 * Date: 2023-07-20
 */
module wow.commons {
	requires static lombok;
	requires excel.utils;
	requires java.annotation;
	requires spring.beans;
	requires spring.context;

	exports wow.commons.constant;
	exports wow.commons.model;
	exports wow.commons.model.attribute;
	exports wow.commons.model.attribute.condition;
	exports wow.commons.model.buff;
	exports wow.commons.model.categorization;
	exports wow.commons.model.character;
	exports wow.commons.model.config;
	exports wow.commons.model.effect;
	exports wow.commons.model.effect.component;
	exports wow.commons.model.effect.impl;
	exports wow.commons.model.item;
	exports wow.commons.model.profession;
	exports wow.commons.model.pve;
	exports wow.commons.model.racial;
	exports wow.commons.model.source;
	exports wow.commons.model.spell;
	exports wow.commons.model.spell.component;
	exports wow.commons.model.spell.impl to wow.scraper;
	exports wow.commons.model.talent;
	exports wow.commons.repository.item;
	exports wow.commons.repository.pve;
	exports wow.commons.repository.spell;
	exports wow.commons.repository.impl.item;
	exports wow.commons.repository.impl.pve;
	exports wow.commons.repository.impl.parser.excel;
	exports wow.commons.repository.impl.parser.excel.mapper to wow.scraper;
	exports wow.commons.repository.impl.parser.item to wow.scraper;
	exports wow.commons.repository.impl.parser.pve to wow.scraper;
	exports wow.commons.repository.impl.parser.spell to wow.scraper;
	exports wow.commons.repository.impl.spell;
	exports wow.commons.util;
	exports wow.commons.util.parser;

	opens wow.commons.data.item;
	opens wow.commons.data.pve;
	opens wow.commons.data.spell;

	opens wow.commons.repository.impl.item;
	opens wow.commons.repository.impl.parser.excel.mapper;
	opens wow.commons.repository.impl.parser.item;
	opens wow.commons.repository.impl.pve;
	opens wow.commons.repository.impl.spell;
}