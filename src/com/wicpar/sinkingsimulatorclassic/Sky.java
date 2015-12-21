package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import org.lwjgl.opengl.GL11;

/**
 * Created by Frederic on 09/10/2015 at 17:42.
 */
public class Sky implements IDrawable
{
	public static Color SkyColor = new Color(0.529f, 0.808f, 0.980f, 1);
	@Override
	public void draw()
	{

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(SkyColor.r, SkyColor.g, SkyColor.b, SkyColor.a);
		GL11.glVertex3d(-1, -1, -0.9);
		GL11.glVertex3d(-1, 1, -0.9);
		GL11.glVertex3d(1, 1, -0.9);
		GL11.glVertex3d(1, -1, -0.9);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
