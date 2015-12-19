package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Frederic on 11/10/2015 at 19:50.
 */
public class Material
{
	private static final Logger logger = LoggerFactory.getLogger(Material.class);
	private static final HashMap<String, Material> materials = new HashMap<>();

	public final String name;
	public final double strength;
	public final double mass;
	public final Color colour;
	public final boolean isHull;

	public static void loadFromTemplateList(List<MaterialTemplate> templates)
	{
		for (MaterialTemplate t : templates)
		{
			Color c = Color.valueOf(t.colour.toLowerCase().replace("#",""));
			if (materials.get(c.toString()) != null)
				logger.warn("Failed to load material: The following materials colour already exists: " + t.toString());
			else
			{
				materials.put(c.toString(), new Material(t.name, t.strength, t.mass, c, t.isHull));
				logger.info("Created Material: " + t.toString());
			}
		}
	}

	public static Material fromColor(String c)
	{
		return materials.get(c);
	}

	public Material(String name, double strength, double mass, Color colour, boolean isHull)
	{
		this.name = name;
		this.strength = strength;
		this.mass = mass;
		this.colour = colour;
		this.isHull = isHull;
	}

	public String getName()
	{
		return name;
	}

	public double getStrength()
	{
		return strength;
	}

	public double getMass()
	{
		return mass;
	}

	public Color getColour()
	{
		return colour;
	}

	public boolean isHull()
	{
		return isHull;
	}

	public static class MaterialTemplate
	{
		public String name;
		public double strength;
		public double mass;
		public String colour;
		public boolean isHull;

		public MaterialTemplate()
		{
		}

		public MaterialTemplate(String name, double strength, double mass, String colour, boolean isHull)
		{
			this.name = name;
			this.strength = strength;
			this.mass = mass;
			this.colour = colour;
			this.isHull = isHull;
		}

		@Override
		public String toString()
		{
			return "MaterialTemplate{" +
					"name='" + name + '\'' +
					", strength=" + strength +
					", mass=" + mass +
					", colour='" + colour + '\'' +
					", isHull=" + isHull +
					'}';
		}
	}
}
