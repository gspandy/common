package com.porpoise.common.collect;

class Grape {
	private final int	radius;
	private final int	ripeness;
	private final Color	color;

	Grape(final int r, final int ripe, final Color c) {
		this.radius = r;
		this.ripeness = ripe;
		this.color = c;
	}

	/**
	 * @return the radius
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * @return the ripeness
	 */
	public int getRipeness() {
		return this.ripeness;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}
}