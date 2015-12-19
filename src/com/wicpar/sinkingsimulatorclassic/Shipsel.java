package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.physics.system.Physical;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 11/10/2015 at 19:49.
 */
public class Shipsel extends Physical implements IDrawable
{
	private static final Color color = new Color(0, 0.25f, 1, 1);
	private final Material material;
	private boolean damaged = false;
	private float flooded = 0;
	private Color current = new Color();


	public Shipsel(Material material, double x, double y)
	{
		this(material,x,y,0,0);
	}

	public Shipsel(Material material)
	{
		this(material,0,0);
	}

	public Shipsel(Material material, double x, double y, double vx, double vy)
	{
		super(new Vector3d(x,y,0), new Vector3d(vx, vy, 0), material.getMass());
		this.material = material;
	}

	@Override
	public void draw()
	{
		current.set(material.colour.r * (1-flooded) + color.r*(flooded), material.colour.g * (1-flooded) + color.g*(flooded), material.colour.b * (1-flooded) + color.b*(flooded), material.colour.a * (1-flooded) + color.a*(flooded));
		final Camera cam = Main.ClassicSinkingSim.getInstance().getCam();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPointSize((float)Main.ClassicSinkingSim.getInstance().getCam().scaleSize(1));
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glColor4f(current.r, current.g, current.b, current.a);
		GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0);
		GL11.glEnd();
		GL11.glPointSize(1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public Material getMaterial()
	{
		return material;
	}
}
