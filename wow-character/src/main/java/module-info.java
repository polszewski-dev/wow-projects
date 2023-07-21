/**
 * User: POlszewski
 * Date: 2023-07-20
 */
module wow.character {
	requires static lombok;
	requires excel.utils;
	requires java.annotation;
	requires spring.beans;
	requires spring.context;

	requires transitive wow.commons;

	exports wow.character.model;
	exports wow.character.model.build;
	exports wow.character.model.character;
	exports wow.character.model.equipment;
	exports wow.character.model.snapshot;
	exports wow.character.repository;
	exports wow.character.repository.impl;
	exports wow.character.service;
	exports wow.character.service.impl;
	exports wow.character.util;

	opens wow.character.repository.impl to spring.core;
	opens wow.character.service.impl to spring.core;
}