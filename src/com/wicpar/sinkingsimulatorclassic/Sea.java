package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.physics.IPhysical;
import com.wicpar.wicparbase.physics.system.Force;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 09/10/2015 at 21:25.
 */
public class Sea extends Force implements IDrawable
{

	private static final Color color = new Color(0, 0.25f, 1, 0.5f);
	private int divisions = 1000;
	private double[] heights = new double[divisions + 1];
	private static double WaterD = 1025;
	private static double AirD = 1.2922;
	private final Camera cam = Main.ClassicSinkingSim.getInstance().getCam();
	private double wh = 1;
	private double ww = 3;
	private double time = System.nanoTime() / 1000000000d;

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
		double time = System.nanoTime() / 1000000000d;
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		GL11.glColor4f(color.r, color.g, color.b, color.a);
		for (int i = 0; i <= divisions; i++)
		{
			double pos = ((double) (i) / (divisions))*2-1;
			GL11.glVertex3d(pos, -1, -0.07);
			GL11.glVertex3d(pos, cam.transformY(getHeight(cam.untransformX(pos), time)), -0.07);
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(color.r, color.g, color.b, 1);
		for (int i = 0; i <= divisions; i++)
		{
			double pos = ((double) (i) / (divisions))*2-1;
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
		time = System.nanoTime() / 1000000000d;
		double h = getHeight(iPhysical.getPos().x, time);
		double pos = iPhysical.getPos().y;
		double submerged = pos - h;
		Vector3d grav = new Vector3d(0,-9.81,0);
		Vector3d grav2 =  new Vector3d(grav);
		double a = Math.min(Math.max(0.5 - submerged,0), 1);
		double b = Math.min(Math.max(submerged + 0.5,0), 1);
		grav.mul(-WaterD * a * v / iPhysical.getMass());
		grav2.mul(-AirD * b * v / iPhysical.getMass());
		iPhysical.getVel().add(grav);
		iPhysical.getVel().add(grav2);
		double damp;
		if (a == 0)
			damp = 0.1;
		else if (b == 0)
			damp = 0.75;
		else
			damp = iPhysical.getVel().y > 0 ? 0.1:0.75;
		Vector3d vel = new Vector3d(iPhysical.getVel());
		iPhysical.getVel().sub(vel.mul(damp * v));
		return false;
	}

}
