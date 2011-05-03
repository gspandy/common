package com.porpoise.common.collect;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

class FruitBowl {
	private final Date	      created	= new Date();
	private final List<Grape>	grapes	= Lists.newArrayList();

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return this.created;
	}

	public Grape addGrape(final int rad, final int ripe, final Color c) {
		final Grape grape = new Grape(rad, ripe, c);
		this.grapes.add(grape);
		return grape;
	}

	/**
	 * @return the grapes
	 */
	@SuppressWarnings("unchecked")
	public List<Grape> getGrapes() {
		return Collections.unmodifiableList(this.grapes);
	}
}