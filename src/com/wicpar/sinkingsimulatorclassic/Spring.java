package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.physics.IPhysical;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 15/10/2015 at 20:13.
 */
public class Spring extends com.wicpar.wicparbase.physics.system.Defaults.Spring implements IDrawable
{
	private final double breakForce;
	private final boolean canDraw;
	public static boolean showforce = false;
	private final Shipsel a,b;

	public static double strengthmul = 100000;
	public static double resmul = 5000;
	public static double resbase = 0;

	public Spring(Shipsel a, Shipsel b, boolean canDraw)
	{
		this(0.5, strengthmul, Math.min(a.getMaterial().getStrength(), b.getMaterial().getStrength()) * resmul + resbase, a, b, canDraw);
	}

	private Spring(double damping, double strength, double breakForce, Shipsel a, Shipsel b, boolean canDraw)
	{
		super(a, b, damping, strength);
		this.breakForce = breakForce;
		this.canDraw = canDraw;
		this.a = a;
		this.b = b;
	}

	public Spring(Spring la, Spring lb)
	{
		this(Math.max(la.dampening, lb.dampening), Math.min(la.stiffness, lb.stiffness), Math.min(la.breakForce, lb.breakForce), (Shipsel) la.a, (Shipsel) lb.b, false);
	}

	@Override
	public synchronized boolean ApplyForce(IPhysical physical, double delta)
	{

		if (isDisposed())
			return true;
		Vector3d posA, posB, velA, velB;
		synchronized (a)
		{
			posA = new Vector3d(a.getPos());
			velA = new Vector3d(a.getVel());
		}
		synchronized (b)
		{
			posB = new Vector3d(b.getPos());
			velB = new Vector3d(b.getVel());
		}
		stretch = posA.distance(posB) - dst;
		Vector3d norm = new Vector3d(posA).sub(posB).normalize();
		Vector3d x = new Vector3d(norm).mul(stretch > 0 ? stretch * stiffness : stretch * stiffness);
		lastForce = stretch * stiffness;
		boolean dispose = (lastForce > breakForce) || a.isDisposed() || b.isDisposed() /*|| Math.abs(stretch) > dst * 5*/;
		if (dispose)
		{
			this.dispose();
			return true;
		}
		if (physical == a)
		{
			physical.applyForce(new Vector3d(x).negate());
			physical.applyForce(new Vector3d(velA).sub(velB).mul(dampening));
		}
		if (physical == b)
		{
			physical.applyForce(x);
			physical.applyForce(new Vector3d(velA).sub(velB).mul(-dampening));
		}
		return false;
	}

	@Override
	public void draw()
	{
		if (canDraw && !isDisposed())
		{
			Color acol = ((Shipsel)a).getColor();
			Color bcol = ((Shipsel)b).getColor();
			Camera cam = Main.ClassicSinkingSim.getInstance().getCam();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth((float) Main.ClassicSinkingSim.getInstance().getCam().scaleSize(0.1));
			GL11.glBegin(GL11.GL_LINES);
			float rat = (float) (lastForce / breakForce);
			if (showforce)
			{
				if (stretch < 0)
				{
					GL11.glColor4f(1 - rat, 1 - rat, 1, 1);
				} else if (stretch > 0)
					GL11.glColor4f(1, 1 - rat, 1 - rat, 1);
			}
			else
				GL11.glColor4f(acol.r,acol.g, acol.b, acol.a);
			GL11.glVertex3d(cam.transformX(a.getPos().x), cam.transformY(a.getPos().y), 0);
			if (!showforce)
			{
				GL11.glColor4f(bcol.r,bcol.g, bcol.b, bcol.a);
			}
			GL11.glVertex3d(cam.transformX(b.getPos().x), cam.transformY(b.getPos().y), 0);
			GL11.glEnd();
			GL11.glLineWidth(1);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
	}

	public Shipsel getA()
	{
		return a;
	}

	public Shipsel getB()
	{
		return b;
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public String toString()
	{
		return "Spring: {lastforce: " + lastForce + "; laststretch: " + stretch + ";}";
	}
}
