package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.mech.Base;
import com.wicpar.wicparbase.physics.IForce;
import com.wicpar.wicparbase.physics.system.Physical;
import com.wicpar.wicparbase.utils.Disposable;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 11/10/2015 at 19:49.
 */
public class Shipsel extends Physical
{
	public static Color WaterColor = new Color(0, 0.25f, 1, 1);
	private final Material material;
	private boolean damaged = false;
	private float flooded = 0;
	private Color current = new Color();
	private Ship parent;

	public static double waterMassMul = 5;
	public static float trans = 0.5f;


	public Shipsel(Material material, double x, double y, Ship parent)
	{
		this(material,x,y,0,0, parent);
	}

	public Shipsel(Material material, Ship parent)
	{
		this(material,0,0, parent);
	}

	public Shipsel(Material material, double x, double y, double vx, double vy, Ship parent)
	{
		super(new Vector3d(x,y,0), new Vector3d(vx, vy, 0), material.getMass());
		this.material = material;
		mass = genMass();
		this.parent = parent;
	}

	@Override
	public void UpdatePhysicals(double delta)
	{
		super.UpdatePhysicals(delta);
	}

	@Override
	public void UpdateForces(double v)
	{
		super.UpdateForces(v);
		if (damaged && Main.ClassicSinkingSim.getInstance().getSea().getHeight(pos.x, Base.getTimePassed()) > pos.y + 0.5)
		{
			flooded = Math.min(flooded + (float) v / 2, 1);
		}
		mass = (float) genMass();
		current.set(material.colour.r * (1 - flooded * trans) + WaterColor.r * (flooded * trans), material.colour.g * (1 - flooded * trans) + WaterColor.g * (flooded * trans), material.colour.b * (1 - flooded * trans) + WaterColor.b * (flooded * trans), material.colour.a * (1 - flooded * trans) + WaterColor.a * (flooded * trans));
	}

	private double genMass()
	{
		return genMass(material, flooded, this);
	}

	private static double genMass(Material material, double flooded, Shipsel shipsel)
	{
		boolean top = Main.ClassicSinkingSim.getInstance().getSea().getHeight(shipsel.getPos().x, Base.getTimePassed()) < shipsel.getPos().y;
		final double prop = material.name.equalsIgnoreCase("rope") ? 1 : material.isHull ? .1 : 0.07;
		double fl = material.isHull ? Math.max(Sea.WaterD * (top ? 1 : Sea.buoyancyMul / 5),(flooded * Sea.WaterD * (top ? 1 : Sea.buoyancyMul / 5) * waterMassMul + (1 - flooded) * Sea.AirD)) : (flooded * Sea.WaterD * (top ? 1 : Sea.buoyancyMul / 5) * waterMassMul + (1 - flooded) * Sea.AirD) ;
		double mass = (material.mass + material.mass * 10 * flooded) * prop + fl * (1 - prop);
		mass *= 5;
		if (mass == 0)
			mass = 0.0001;
		return mass;
	}

	public float getFlooded()
	{
		return flooded;
	}

	public void setFlooded(float flooded)
	{
		if (flooded < 0)
			this.flooded = 0;
		else if (flooded > 1)
			this.flooded = 1;
		else
			this.flooded = flooded;
	}

	public boolean isDamaged()
	{
		return damaged;
	}

	public void setDamaged(boolean damaged)
	{
		this.damaged = damaged;
	}

	public Material getMaterial()
	{
		return material;
	}

	public Color getColor()
	{
		return current;
	}

	public Ship getParent()
	{
		return parent;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		this.forces.stream().filter(iForce -> iForce instanceof Disposable).forEach(iForce -> ((Disposable) iForce).dispose());
	}
}
