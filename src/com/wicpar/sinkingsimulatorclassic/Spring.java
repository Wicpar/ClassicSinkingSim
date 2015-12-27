package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.physics.IPhysical;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 15/10/2015 at 20:13.
 */
public class Spring extends com.wicpar.wicparbase.physics.system.Defaults.Spring
{
	private final double breakForce;
	private final boolean canDraw;
	public static boolean showforce = false;
	private final Shipsel a,b;

	public static double strengthmul = 200;
	public static double resmul = 10000;
	public static double resbase = 0;

	public Spring(Shipsel a, Shipsel b, boolean canDraw)
	{
		this(0.5, 1000, Math.min(a.getMaterial().getStrength(), b.getMaterial().getStrength()), a, b, canDraw);
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
		double breakForce = this.breakForce  * resmul + resbase;
		double stiffness = this.stiffness * strengthmul;
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

	public double getBreakForce()
	{
		return breakForce;
	}

	public static boolean isShowforce()
	{
		return showforce;
	}

	public double getStretch()
	{
		return this.stretch;
	}

	public double getLastForce()
	{
		return this.lastForce;
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
