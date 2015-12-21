package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.physics.IForce;
import com.wicpar.wicparbase.physics.IPhysical;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 09/10/2015 at 18:01.
 */
public class Ground implements IDrawable, IForce
{
	private static final Color color = new Color(0.5f, 0.5f, 0.5f, 1);
	public double h = -1000;


	@Override
	public void draw()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(color.r, color.g, color.b, color.a);
		double h = Main.ClassicSinkingSim.getInstance().getCam().transformY(this.h);
		GL11.glVertex3d(-1, h, -0.8);
		GL11.glVertex3d(-1, -1, -0.8);
		GL11.glVertex3d(1, -1, -0.8);
		GL11.glVertex3d(1, h, -0.8);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public boolean ApplyForce(IPhysical iPhysical, double v)
	{
		if (iPhysical.getPos().y < h)
		{
			iPhysical.getPos().y = h;
			iPhysical.setVel(iPhysical.getVel().negate().mul(0.1));
		}
		return false;
	}
}
