package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

public class ColorHSL {
	private Color color;
	private float hue;
	private float saturation;
	private float luminance;

	public ColorHSL(Color color) {
		this.color = color;
		this.hue = color.getRGB().getHSB()[0];
		this.saturation = color.getRGB().getHSB()[1];
		this.luminance = color.getRGB().getHSB()[2];
	}

	public ColorHSL(float hue, float saturation, float luminance) {
		this(new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(hue, saturation, luminance)));
	}

	// TODO: Add test coverage
	public ColorHSL modifyProperty(HSL_PROPERTY propertyToModify, BOUND_BEHAVIOR boundBehavior, float amount) {
		// Hue must be within the range [0, 360]
		// Saturation must be within the range [0, 1]
		// Luminance must be within the range [0, 1]
		float newValue = -1;
		float max = 1;
		float min = 0;

		switch (propertyToModify) {
		case HUE:
			max = 360;
			newValue = hue;
			break;
		case SATURATION:
			newValue = saturation;
			break;
		case LUMINANCE:
			newValue = luminance;
			break;
		}

		newValue = newValue + amount;

		switch (boundBehavior) {
		case CYCLE:
			if (newValue < min) {
				newValue = max - Math.abs(newValue);
			} else if (newValue > max) {
				newValue = min + (newValue - max);
			}
			break;
		case LIMIT:
			newValue = Math.min(newValue, max);
			newValue = Math.max(newValue, min);
			break;
		case REVERSE:
			if (newValue < min) {
				newValue = min + Math.abs(newValue);
			} else if (newValue > max) {
				newValue = max - (newValue - max);
			}

		}

		switch (propertyToModify) {
		case HUE:
			return new ColorHSL(newValue, this.getSaturation(), this.getLuminance());
		case SATURATION:
			return new ColorHSL(this.getHue(), newValue, this.getLuminance());
		case LUMINANCE:
			return new ColorHSL(this.getHue(), this.getSaturation(), newValue);
		}

		return null;
	}

	public void dispose() {
		color.dispose();
	}

	public Color getColor() {
		return color;
	}

	public float getHue() {
		return hue;
	}

	public float getSaturation() {
		return saturation;
	}

	public float getLuminance() {
		return luminance;
	}

	public float getPerceivedLuminance() {
		return (299f * color.getRed() + 587f * color.getGreen() + 114f * color.getBlue()) / 1000;
	}

	/*
	 * How the modification amount to a property should behave when hitting an upper
	 * or lower bound.
	 */
	enum BOUND_BEHAVIOR {
		/*
		 * Jump from one bound to another. Example: For a color with a luminance of
		 * 0.6f, increasing the luminance by 0.5f while using the CYCLE BOUND_BEHAVIOR
		 * will result in the color having a luminance of 0.1f.
		 */
		CYCLE,

		/*
		 * Negate any excess amount from the bound. Example: For a color with a
		 * luminance of 0.6f, increasing the luminance by 0.5f while using the REVERSE
		 * BOUND_BEHAVIOR will result in the color having a luminance of 0.9f.
		 */
		REVERSE,

		/*
		 * Set the amount to the nearest bound. Example: Fora color with a luminance of
		 * 0.6f, increasing the luminance by 0.5f while using the LIMIT BOUND_BEHAVIOR
		 * will result in the color having a luminance of 1f.
		 */
		LIMIT;
	}

	enum HSL_PROPERTY {
		HUE, SATURATION, LUMINANCE;
	}

}
