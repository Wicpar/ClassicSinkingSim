package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.mech.Base;
import com.wicpar.wicparbase.physics.IPhysical;
import com.wicpar.wicparbase.physics.system.Force;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 09/10/2015 at 21:25.
 */
public class Sea extends Force implements IDrawable
{

	public static Color SeaColor = new Color(0, 0.25f, 1, 0.5f);
	private int divisions = 1000;
	private double[] heights = new double[divisions + 1];
	public static double WaterD = 1025;
	public static double AirD = 1.2922;
	private final Camera cam = Main.ClassicSinkingSim.getInstance().getCam();
	private double wh = 1;
	private double ww = 3;
	private double time = Base.getTimePassed();
	public static double buoyancyMul = 1;

	public double getHeight(double x, double time)
	{
		x /= ww;
		return (Math.sin(x * 0.1f + time) * 0.5f + Math.sin(x * 0.3f - time * 1.1f) * 0.3f) * wh;
	}

	@Override
	public void draw()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
		time = Base.getTimePassed();
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		GL11.glColor4f(SeaColor.r, SeaColor.g, SeaColor.b, SeaColor.a);
		for (int i = 0; i <= divisions; i++)
		{
			double pos = ((double) (i) / (divisions)) * 2 - 1;
			GL11.glVertex3d(pos, -1, -0.07);
			GL11.glVertex3d(pos, cam.transformY(getHeight(cam.untransformX(pos), time)), -0.07);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(SeaColor.r, SeaColor.g, SeaColor.b, 1);
		for (int i = 0; i <= divisions; i++)
		{
			double pos = ((double) (i) / (divisions)) * 2 - 1;
			GL11.glVertex3d(pos, cam.transformY(getHeight(cam.untransformX(pos), time)), -0.07);
		}
		GL11.glEnd();
		GL11.glLineWidth(1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glLineWidth(1);
	}

	public void setDivisions(int divisions)
	{
		this.divisions = divisions;
		heights = new double[divisions + 1];
	}

	@Override
	public boolean ApplyForce(IPhysical iPhysical, double v)
	{
		final double airdamp = 0.5, waterdamp = 2;

		time = Base.getTimePassed();
		double h = getHeight(iPhysical.getPos().x, time);
		double pos = iPhysical.getPos().y;
		double submerged = pos - h;
		Vector3d grav = new Vector3d(0, -9.81, 0);
		Vector3d grav2 = new Vector3d(grav);
		double a = Math.min(Math.max(0.5 - submerged, 0), 1);
		double b = Math.min(Math.max(submerged + 0.5, 0), 1);
		grav.mul(-WaterD * a * 5 * buoyancyMul);
		grav2.mul(-AirD * b * 5 * buoyancyMul);
		iPhysical.applyForce(grav);
		iPhysical.applyForce(grav2);
		double damp;
		if (a == 0)
			damp = airdamp;
		else if (b == 0)
			damp = waterdamp;
		else
			damp = iPhysical.getVel().y > 0 ? airdamp : waterdamp;
		iPhysical.applyForce(new Vector3d(iPhysical.getVel()).mul(-damp * iPhysical.getMass()));
		return false;
	}

}
