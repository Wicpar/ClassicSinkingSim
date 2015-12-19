package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.physics.IPhysical;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 15/10/2015 at 20:13.
 */
public class Spring extends com.wicpar.wicparbase.physics.system.Defaults.Spring implements IDrawable
{
	private final double breakForce;
	private final boolean canDraw;

	public Spring(Shipsel a, Shipsel b, boolean canDraw)
	{
		this(0.5, Math.min(a.getMaterial().getStrength(), b.getMaterial().getStrength()) * 50000, Math.min(a.getMaterial().getStrength(), b.getMaterial().getStrength()) * 1, a, b, canDraw);
	}

	private Spring(double damping, double strength, double breakForce, Shipsel a, Shipsel b, boolean canDraw)
	{
		super(a, b, damping, strength);
		this.breakForce = breakForce;
		this.canDraw = canDraw;
	}

	@Override
	public synchronized boolean ApplyForce(IPhysical physical, double delta)
	{
		super.ApplyForce(physical, delta);
		return (breakForce < lastForce);
	}

	@Override
	public void draw()
	{
		if (canDraw && !isDisposed())
		{
			Color acol = ((Shipsel)a).getColor();
			Color bcol = ((Shipsel)b).getColor();
			final Camera cam = Main.ClassicSinkingSim.getInstance().getCam();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth((float) Main.ClassicSinkingSim.getInstance().getCam().scaleSize(0.1));
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(acol.r, acol.g, acol.b, acol.a);
			GL11.glVertex3d(cam.transformX(a.getPos().x), cam.transformY(a.getPos().y), 0);
			GL11.glColor4f(bcol.r, bcol.g, bcol.b, bcol.a);
			GL11.glVertex3d(cam.transformX(b.getPos().x), cam.transformY(b.getPos().y), 0);
			GL11.glEnd();
			GL11.glLineWidth(1);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
	}
}
