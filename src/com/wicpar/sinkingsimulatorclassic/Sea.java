/*
 * Copyright (c) Frederic Artus Nieto 2016.
 *
 * Statement of Purpose
 *
 * The laws of most jurisdictions throughout the world automatically confer exclusive Copyright and Related Rights (defined below) upon the creator and subsequent owner(s) (each and all, an "owner") of an original work of authorship and/or a database (each, a "Work").
 *
 * Certain owners wish to permanently relinquish those rights to a Work for the purpose of contributing to a commons of creative, cultural and scientific works ("Commons") that the public can reliably and without fear of later claims of infringement build upon, modify, incorporate in other works, reuse and redistribute as freely as possible in any form whatsoever and for any purposes, including without limitation commercial purposes. These owners may contribute to the Commons to promote the ideal of a free culture and the further production of creative, cultural and scientific works, or to gain reputation or greater distribution for their Work in part through the use and efforts of others.
 *
 * For these and/or other purposes and motivations, and without any expectation of additional consideration or compensation, the person associating CC0 with a Work (the "Affirmer"), to the extent that he or she is an owner of Copyright and Related Rights in the Work, voluntarily elects to apply CC0 to the Work and publicly distribute the Work under its terms, with knowledge of his or her Copyright and Related Rights in the Work and the meaning and intended legal effect of CC0 on those rights.
 *
 * 1. Copyright and Related Rights. A Work made available under CC0 may be protected by copyright and related or neighboring rights ("Copyright and Related Rights"). Copyright and Related Rights include, but are not limited to, the following:
 *
 *     the right to reproduce, adapt, distribute, perform, display, communicate, and translate a Work;
 *     moral rights retained by the original author(s) and/or performer(s);
 *     publicity and privacy rights pertaining to a person's image or likeness depicted in a Work;
 *     rights protecting against unfair competition in regards to a Work, subject to the limitations in paragraph 4(a), below;
 *     rights protecting the extraction, dissemination, use and reuse of data in a Work;
 *     database rights (such as those arising under Directive 96/9/EC of the European Parliament and of the Council of 11 March 1996 on the legal protection of databases, and under any national implementation thereof, including any amended or successor version of such directive); and
 *     other similar, equivalent or corresponding rights throughout the world based on applicable law or treaty, and any national implementations thereof.
 *
 * 2. Waiver. To the greatest extent permitted by, but not in contravention of, applicable law, Affirmer hereby overtly, fully, permanently, irrevocably and unconditionally waives, abandons, and surrenders all of Affirmer's Copyright and Related Rights and associated claims and causes of action, whether now known or unknown (including existing as well as future claims and causes of action), in the Work (i) in all territories worldwide, (ii) for the maximum duration provided by applicable law or treaty (including future time extensions), (iii) in any current or future medium and for any number of copies, and (iv) for any purpose whatsoever, including without limitation commercial, advertising or promotional purposes (the "Waiver"). Affirmer makes the Waiver for the benefit of each member of the public at large and to the detriment of Affirmer's heirs and successors, fully intending that such Waiver shall not be subject to revocation, rescission, cancellation, termination, or any other legal or equitable action to disrupt the quiet enjoyment of the Work by the public as contemplated by Affirmer's express Statement of Purpose.
 *
 * 3. Public License Fallback. Should any part of the Waiver for any reason be judged legally invalid or ineffective under applicable law, then the Waiver shall be preserved to the maximum extent permitted taking into account Affirmer's express Statement of Purpose. In addition, to the extent the Waiver is so judged Affirmer hereby grants to each affected person a royalty-free, non transferable, non sublicensable, non exclusive, irrevocable and unconditional license to exercise Affirmer's Copyright and Related Rights in the Work (i) in all territories worldwide, (ii) for the maximum duration provided by applicable law or treaty (including future time extensions), (iii) in any current or future medium and for any number of copies, and (iv) for any purpose whatsoever, including without limitation commercial, advertising or promotional purposes (the "License"). The License shall be deemed effective as of the date CC0 was applied by Affirmer to the Work. Should any part of the License for any reason be judged legally invalid or ineffective under applicable law, such partial invalidity or ineffectiveness shall not invalidate the remainder of the License, and in such case Affirmer hereby affirms that he or she will not (i) exercise any of his or her remaining Copyright and Related Rights in the Work or (ii) assert any associated claims and causes of action with respect to the Work, in either case contrary to Affirmer's express Statement of Purpose.
 *
 * 4. Limitations and Disclaimers.
 *
 *     No trademark or patent rights held by Affirmer are waived, abandoned, surrendered, licensed or otherwise affected by this document.
 *     Affirmer offers the Work as-is and makes no representations or warranties of any kind concerning the Work, express, implied, statutory or otherwise, including without limitation warranties of title, merchantability, fitness for a particular purpose, non infringement, or the absence of latent or other defects, accuracy, or the present or absence of errors, whether or not discoverable, all to the greatest extent permissible under applicable law.
 *     Affirmer disclaims responsibility for clearing rights of other persons that may apply to the Work or any use thereof, including without limitation any person's Copyright and Related Rights in the Work. Further, Affirmer disclaims responsibility for obtaining any necessary consents, permissions or other rights required for any use of the Work.
 *     Affirmer understands and acknowledges that Creative Commons is not a party to this document and has no duty or obligation with respect to this CC0 or use of the Work.
 *
 *
 */

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
	public static double buoyancyMul = 1.5;

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
