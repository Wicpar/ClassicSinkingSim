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
import com.wicpar.wicparbase.physics.IDynamical;
import com.wicpar.wicparbase.utils.ClassPool;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Frederic on 19/12/2015 at 06:22.
 */
public class Ship extends ClassPool implements IDrawable, IDynamical
{
	private final Shipsel[][] shipsels;
	private final boolean[][][] springs;
	private final String name;
	private final BufferedImage texture;
	private int subsprings = 10;
	public static float fluidmul = 2;

	private Ship(Shipsel[][] shipsels, boolean[][][] springs, String name, BufferedImage texture)
	{
		this.shipsels = shipsels;
		this.springs = springs;
		this.name = name;
		this.texture = texture;
	}

	public Ship(File image, Vector3d pos)
	{
		BufferedImage texture = null;
		try
		{
			texture = ImageIO.read(image);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		this.texture = texture;
		assert texture != null;
		shipsels = new Shipsel[texture.getHeight()][texture.getWidth()];
		springs = new boolean[4][][];

		springs[0] = new boolean[texture.getHeight()][texture.getWidth() - 1];
		springs[1] = new boolean[texture.getHeight() - 1][texture.getWidth() - 1];
		springs[2] = new boolean[texture.getHeight() - 1][texture.getWidth()];
		springs[3] = new boolean[texture.getHeight() - 1][texture.getWidth() - 1];

		for (int y = 0; y < texture.getHeight(); y++)
		{
			for (int x = 0; x < texture.getWidth(); x++)
			{
				Color c = new Color();
				Color.argb8888ToColor(c, texture.getRGB(x, y));
				if (!c.equals(Color.WHITE) && Material.fromColor(c.toString()) != null)
				{
					Shipsel current = shipsels[y][x] = new Shipsel(Material.fromColor(c.toString()), x - texture.getWidth() / 2. + pos.x, -(y - texture.getHeight() / 2.) + pos.y, this);
					if (x > 0 && (shipsels[y][x - 1]) != null)
						springs[0][y][x - 1] = true;
					else if (!current.getMaterial().isHull)
						current.setDamaged(true);
					if (y > 0 && x > 0 && (shipsels[y - 1][x - 1]) != null)
						springs[1][y - 1][x - 1] = true;
					if (y > 0 && (shipsels[y - 1][x]) != null)
						springs[2][y - 1][x] = true;
					else if (!current.getMaterial().isHull)
						current.setDamaged(true);
					if (y > 0 && x < shipsels[0].length - 1 && (shipsels[y - 1][x + 1]) != null)
						springs[3][y - 1][x] = true;
				}
			}
		}

		for (int y = 0; y < texture.getHeight(); y++)
		{
			for (int x = 0; x < texture.getWidth(); x++)
			{
				Color c = new Color();
				Color.argb8888ToColor(c, texture.getRGB(x, y));
				if (!c.equals(Color.WHITE) && Material.fromColor(c.toString()) != null)
				{
					Shipsel current = shipsels[y][x];
					if (y >= shipsels.length - 1 || (shipsels[y + 1][x]) == null)
					{
						if (!current.getMaterial().isHull)
							current.setDamaged(true);
					}
					if (x >= shipsels[y].length - 1 || (shipsels[y][x + 1]) == null)
					{
						if (!current.getMaterial().isHull)
							current.setDamaged(true);
					}
				}
			}
		}
		name = image.getName();
	}

	@Override
	public void UpdateClass(Updater updater, Class c, Object... params)
	{
		super.UpdateClass(updater, c, params);
		if (c != null && c.isAssignableFrom(Shipsel.class))
		{
			if (c.equals(IDynamical.class))
				for (int y = 0, shipselsLength = shipsels.length; y < shipselsLength; y++)
				{
					if (shipsels[y] != null)
						for (int x = 0, shipselLength = shipsels[y].length; x < shipselLength; x++)
						{
							Shipsel shipsel = shipsels[y][x];
							if (shipsel != null && !shipsel.isDisposed())
							{
								ProcessForce(x, y);
							}
						}
				}
			for (int y = 0, shipselsLength = shipsels.length; y < shipselsLength; y++)
			{
				if (shipsels[y] != null)
					for (int x = 0, shipselLength = shipsels[y].length; x < shipselLength; x++)
					{
						Shipsel shipsel = shipsels[y][x];
						if (shipsel != null && !shipsel.isDisposed())
						{
							if (c.equals(IDynamical.class))
								ProcessForce(x, y);
							updater.Update(shipsel, params);
							if (!shipsel.getMaterial().isHull && c.equals(IDynamical.class))
							{
								Double delta = ((Double) params[0]);
								Shipsel tmp;
								float flood = shipsel.getFlooded() / 4;
								int top = Main.ClassicSinkingSim.getInstance().getSea().getHeight(shipsel.getPos().x, Base.getTimePassed()) < shipsel.getPos().y ? -1 : 1;
								int done = 0;
								if (x > 0 && (tmp = shipsels[y][x - 1]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * top * fluidmul));
									++done;
								}
								if (y < shipsels.length - 1 && (tmp = shipsels[y + 1][x]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * top * fluidmul));
									++done;
								}
								if (y > 0 && (tmp = shipsels[y - 1][x]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * 0.1 * top * fluidmul));
									++done;
								}
								if (x < shipsels[y].length - 1 && (tmp = shipsels[y][x + 1]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * top * fluidmul));
									++done;
								}
							}
						} else if (shipsel != null && shipsel.isDisposed())
							shipsels[y][x] = null;
					}
			}
		}
	}

	private void ProcessForce(int x, int y)
	{
		final double sqrt2 = Math.sqrt(2);
		Vector3d force = new Vector3d(), force2 = new Vector3d();
		Vector3d tmp;
		Vector3d posA, posB, velA, velB;
		int w = shipsels[0].length;
		int h = shipsels.length;
		int i;
		double stiffness = 100000, strengthmul = 3000, dampening = 0.5, f;
		Shipsel origin = shipsels[y][x];
		Shipsel other;
		posA = origin.getPos();
		velA = origin.getVel();

		i = 0;
		while (++i < w - x && springs[0][y][x + i - 1])
		{
			other = shipsels[y][x + i];
			if (other == null)
			{
				springs[0][y][x + i - 1] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
		}
		force.div(i);
		force.add(force2);
		if (x < w - 1 && y < h)
		{
			if (springs[0][y][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y][x + 1].getMaterial().strength) * strengthmul, 2))
					springs[0][y][x] = false;
				else
				{
					if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
							.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y][x + 1].getMass()).add(shipsels[y][x + 1].getPos())) - i)
							== Math.signum(origin.getPos().distance(shipsels[y][x + 1].getPos()) - i))
						origin.applyForce(force);
					else
						origin.move(origin.getPos().sub(shipsels[y][x + 1].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y][x + 1].getPos()) - i).div(2));
				}
			} else if (!origin.getMaterial().isHull)
				origin.setDamaged(true);
		}
		force.set(0, 0, 0);

		i = 1;
		while (i < w - x && i < h - y && springs[1][y + i - 1][x + i - 1])
		{
			other = shipsels[y + i][x + i];
			if (other == null)
			{
				springs[1][y + i - 1][x + i - 1] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i * sqrt2;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
			i++;
		}
		force.div(i);
		force.add(force2);
		if (x < w - 1 && y < h - 1)
		{
			if (springs[1][y][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y + 1][x + 1].getMaterial().strength) * strengthmul, 2))
					springs[1][y][x] = false;
				else
				{
					if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
							.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y + 1][x + 1].getMass()).add(shipsels[y + 1][x + 1].getPos())) - i* sqrt2)
							== Math.signum(origin.getPos().distance(shipsels[y + 1][x + 1].getPos()) - i* sqrt2))
						origin.applyForce(force);
					else
						origin.move(origin.getPos().sub(shipsels[y + 1][x + 1].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y + 1][x + 1].getPos()) - i* sqrt2).div(2));
				}
			}
		}
		force.set(0, 0, 0);

		i = 0;
		while (++i < h - y && springs[2][y + i - 1][x])
		{
			other = shipsels[y + i][x];
			if (other == null)
			{
				springs[2][y + i - 1][x] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
		}
		force.div(i);
		force.add(force2);
		if (x < w && y < h - 1)
		{
			if (springs[2][y][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y + 1][x].getMaterial().strength) * strengthmul, 2))
					springs[2][y][x] = false;
				else
				{
					if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
							.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y + 1][x].getMass()).add(shipsels[y + 1][x].getPos())) - i)
							== Math.signum(origin.getPos().distance(shipsels[y + 1][x].getPos()) - i))
						origin.applyForce(force);
					else
						origin.move(origin.getPos().sub(shipsels[y + 1][x].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y + 1][x].getPos()) - i).div(2));
				}
			} else if (!origin.getMaterial().isHull)
				origin.setDamaged(true);
		}
		force.set(0, 0, 0);

		i = 1;
		if (x > 0)
		{
			while (i <= x && i < h - y && springs[3][y + i - 1][x - i])
			{
				other = shipsels[y + i][x - i];
				if (other == null)
				{
					springs[3][y + i - 1][x - i] = false;
					break;
				}
				posB = other.getPos();
				velB = other.getVel();

				double stretch = posA.distance(posB) - i * sqrt2;
				double stiff = stiffness;
				tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
				force2.set(new Vector3d(velA).sub(velB).mul(dampening));
				force.add(tmp);
				i++;
			}
			force.div(i);
			force.add(force2);
			if (y < h - 1 && x > 0)
			{
				if (springs[3][y][x - 1])
				{
					f = force.lengthSquared();
					if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y + 1][x - 1].getMaterial().strength) * strengthmul, 2))
						springs[3][y][x - 1] = false;
					else
					{
						if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
								.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y + 1][x - 1].getMass()).add(shipsels[y + 1][x - 1].getPos())) - i* sqrt2)
								== Math.signum(origin.getPos().distance(shipsels[y + 1][x - 1].getPos()) - i* sqrt2))
							origin.applyForce(force);
						else
							origin.move(origin.getPos().sub(shipsels[y + 1][x - 1].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y + 1][x - 1].getPos()) - i* sqrt2).div(2));
					}
				}
			}
			force.set(0, 0, 0);
		}


		//--------------------------------------------------------------------------------------------------------------


		i = 0;
		while (++i <= x && springs[0][y][x - i])
		{
			other = shipsels[y][x - i];
			if (other == null)
			{
				springs[0][y][x - i] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
		}
		force.div(i);
		force.add(force2);
		if (y < h && x > 0)
		{
			if (springs[0][y][x - 1])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y][x - 1].getMaterial().strength) * strengthmul, 2))
					springs[0][y][x - 1] = false;
				else
				{
					if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
							.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y][x - 1].getMass()).add(shipsels[y][x - 1].getPos())) - i)
							== Math.signum(origin.getPos().distance(shipsels[y][x - 1].getPos()) - i))
						origin.applyForce(force);
					else
						origin.move(origin.getPos().sub(shipsels[y][x - 1].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y][x - 1].getPos()) - i).div(2));
				}
			} else if (!origin.getMaterial().isHull)
				origin.setDamaged(true);
		}
		force.set(0, 0, 0);

		i = 1;
		while (i <= x && i <= y && springs[1][y - i][x - i])
		{
			other = shipsels[y - i][x - i];
			if (other == null)
			{
				springs[1][y - i][x - i] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i * sqrt2;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
			i++;
		}
		force.div(i);
		force.add(force2);
		if (x > 0 && y > 0)
		{
			if (springs[1][y - 1][x - 1])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y - 1][x - 1].getMaterial().strength) * strengthmul, 2))
					springs[1][y - 1][x - 1] = false;
				else
				{
					if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
							.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y - 1][x - 1].getMass()).add(shipsels[y - 1][x - 1].getPos())) - i* sqrt2)
							== Math.signum(origin.getPos().distance(shipsels[y - 1][x - 1].getPos()) - i* sqrt2))
						origin.applyForce(force);
					else
						origin.move(origin.getPos().sub(shipsels[y - 1][x - 1].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y - 1][x - 1].getPos()) - i* sqrt2).div(2));
				}
			}
		}
		force.set(0, 0, 0);

		i = 0;
		while (++i <= y && springs[2][y - i][x])
		{
			other = shipsels[y - i][x];
			if (other == null)
			{
				springs[2][y - i][x] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
		}
		force.div(i);
		force.add(force2);
		if (x < w && y > 0)
		{
			if (springs[2][y - 1][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y - 1][x].getMaterial().strength) * strengthmul, 2))
					springs[2][y - 1][x] = false;
				else
				{
					if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
							.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y - 1][x].getMass()).add(shipsels[y][x - 1].getPos())) - i)
							== Math.signum(origin.getPos().distance(shipsels[y - 1][x].getPos()) - i))
						origin.applyForce(force);
					else
						origin.move(origin.getPos().sub(shipsels[y - 1][x].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y - 1][x].getPos()) - i).div(2));
				}
			} else if (!origin.getMaterial().isHull)
				origin.setDamaged(true);
		}
		force.set(0, 0, 0);

		i = 1;
		if (y > 0)
		{
			while (i < w - x && i <= y && springs[3][y - i][x + i - 1])
			{
				other = shipsels[y - i][x + i];
				if (other == null)
				{
					springs[3][y - i][x + i - 1] = false;
					break;
				}
				posB = other.getPos();
				velB = other.getVel();

				double stretch = posA.distance(posB) - i * sqrt2;
				double stiff = stiffness;
				tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
				force2.set(new Vector3d(velA).sub(velB).mul(dampening));
				force.add(tmp);
				i++;
			}
			force.div(i);
			force.add(force2);
			if (x < w - 1 && y > 0)
			{
				if (springs[3][y - 1][x])
				{
					f = force.lengthSquared();
					if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y - 1][x + 1].getMaterial().strength) * strengthmul, 2))
						springs[3][y - 1][x] = false;
					else
					{
						if (Math.signum(new Vector3d(force).mul(Base.getDelta()).div(origin.getMass()).add(origin.getPos())
								.distance(new Vector3d(force).negate().mul(Base.getDelta()).div(shipsels[y - 1][x - 1].getMass()).add(shipsels[y - 1][x + 1].getPos())) - i* sqrt2)
								== Math.signum(origin.getPos().distance(shipsels[y - 1][x + 1].getPos()) - i* sqrt2))
							origin.applyForce(force);
						else
							origin.move(origin.getPos().sub(shipsels[y - 1][x + 1].getPos()).normalize().negate().mul(origin.getPos().distance(shipsels[y - 1][x + 1].getPos()) - i* sqrt2).div(2));
					}
				}
			}
			force.set(0, 0, 0);
		}
	}

	private void ProcessForce2(int x, int y)
	{
		final double sqrt2 = Math.sqrt(2);
		Vector3d force = new Vector3d(), force2 = new Vector3d();
		Vector3d tmp;
		Vector3d posA, posB, velA, velB;
		int w = shipsels[0].length;
		int h = shipsels.length;
		int i;
		double stiffness = 100000, strengthmul = 3000, dampening = 0.5, f;
		Shipsel origin = shipsels[y][x];
		Shipsel other;
		posA = origin.getPos();
		velA = origin.getVel();

		i = 0;
		while (++i < w - x && springs[0][y][x + i - 1])
		{
			other = shipsels[y][x + i];
			if (other == null)
			{
				springs[0][y][x + i - 1] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
		}
		force.add(force2);
		if (x < w - 1 && y < h)
		{
			if (springs[0][y][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y][x + 1].getMaterial().strength) * strengthmul, 2))
					springs[0][y][x] = false;
				else
					origin.applyForce(force);
			} else if (!origin.getMaterial().isHull)
				origin.setDamaged(true);
		}
		force.set(0, 0, 0);

		i = 1;
		while (i < w - x && i < h - y && springs[1][y + i - 1][x + i - 1])
		{
			other = shipsels[y + i][x + i];
			if (other == null)
			{
				springs[1][y + i - 1][x + i - 1] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i * sqrt2;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
			i++;
		}
		force.add(force2);
		if (x < w - 1 && y < h - 1)
		{
			if (springs[1][y][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y + 1][x + 1].getMaterial().strength) * strengthmul, 2))
					springs[1][y][x] = false;
				else
					origin.applyForce(force);
			}
		}
		force.set(0, 0, 0);

		i = 0;
		while (++i < h - y && springs[2][y + i - 1][x])
		{
			other = shipsels[y + i][x];
			if (other == null)
			{
				springs[2][y + i - 1][x] = false;
				break;
			}
			posB = other.getPos();
			velB = other.getVel();

			double stretch = posA.distance(posB) - i;
			double stiff = stiffness;
			tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
			force2.set(new Vector3d(velA).sub(velB).mul(dampening));
			force.add(tmp);
		}
		force.div(i);
		force.add(force2);
		if (x < w && y < h - 1)
		{
			if (springs[2][y][x])
			{
				f = force.lengthSquared();
				if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y + 1][x].getMaterial().strength) * strengthmul, 2))
					springs[2][y][x] = false;
				else
					origin.applyForce(force);
			} else if (!origin.getMaterial().isHull)
				origin.setDamaged(true);
		}
		force.set(0, 0, 0);

		i = 1;
		if (x > 0)
		{
			while (i <= x && i < h - y && springs[3][y + i - 1][x - i])
			{
				other = shipsels[y + i][x - i];
				if (other == null)
				{
					springs[3][y + i - 1][x - i] = false;
					break;
				}
				posB = other.getPos();
				velB = other.getVel();

				double stretch = posA.distance(posB) - i * sqrt2;
				double stiff = stiffness;
				tmp = new Vector3d(posB).sub(posA).normalize().mul(stretch * stiff);
				force2.set(new Vector3d(velA).sub(velB).mul(dampening));
				force.add(tmp);
				i++;
			}
			force.div(i);
			force.add(force2);
			if (y < h - 1 && x > 0)
			{
				if (springs[3][y][x - 1])
				{
					f = force.lengthSquared();
					if (f > Math.pow(Math.min(origin.getMaterial().strength, shipsels[y + 1][x - 1].getMaterial().strength) * strengthmul, 2))
						springs[3][y][x - 1] = false;
					else
						origin.applyForce(force);
				}
			}
			force.set(0, 0, 0);
		}
	}

	@Override
	public void draw()
	{
		final Camera cam = Main.ClassicSinkingSim.getInstance().getCam();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPointSize((float) Main.ClassicSinkingSim.getInstance().getCam().scaleSize(0.1));
		GL11.glBegin(GL11.GL_POINTS);
		for (Shipsel[] s : shipsels)
		{
			if (s != null)
				for (Shipsel shipsel : s)
				{
					if (shipsel != null)
					{
						Color c = shipsel.getColor();
						Vector3d pos = shipsel.getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.8);
					}
				}
		}
		GL11.glEnd();
		GL11.glPointSize(1);

		GL11.glLineWidth((float) Main.ClassicSinkingSim.getInstance().getCam().scaleSize(0.1));
		boolean t, b, l, r, e, f;

		Color c;
		Vector3d pos;
		GL11.glBegin(GL11.GL_LINES);
		for (int y = 0; y < springs[1].length; y++)
		{
			for (int x = 0; x < springs[1][y].length; x++)
			{
				t = springs[0][y][x];
				l = springs[2][y][x];
				e = springs[1][y][x];
				f = springs[3][y][x];

				if (t && (shipsels[y][x] == null || shipsels[y][x + 1] == null))
				{
					springs[0][y][x] = false;
				}
				if (l && (shipsels[y][x] == null || shipsels[y + 1][x] == null))
				{
					springs[2][y][x] = false;
				}
				if (e && (shipsels[y][x] == null || shipsels[y + 1][x + 1] == null))
				{
					springs[1][y][x] = false;
				}
				if (f && (shipsels[y][x + 1] == null || shipsels[y + 1][x] == null))
				{
					springs[3][y][x] = false;
				}

				t = springs[0][y][x];
				b = springs[0][y + 1][x];
				l = springs[2][y][x];
				r = springs[2][y][x + 1];
				e = springs[1][y][x];
				f = springs[3][y][x];

				if (t && b && l && r && e && f)
					continue;
				if (t && r && e)
					continue;
				if (b && l && e)
					continue;
				if (t && l && f)
					continue;
				if (b && r && f)
					continue;
				if (t)
				{
					c = shipsels[y][x].getColor();
					pos = shipsels[y][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);
					c = shipsels[y][x + 1].getColor();
					pos = shipsels[y][x + 1].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);

				}
				if (l)
				{
					c = shipsels[y][x].getColor();
					pos = shipsels[y][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);
					c = shipsels[y + 1][x].getColor();
					pos = shipsels[y + 1][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);

				}
				if (e)
				{
					c = shipsels[y][x].getColor();
					pos = shipsels[y][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);
					c = shipsels[y + 1][x + 1].getColor();
					pos = shipsels[y + 1][x + 1].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);

				}
				if (f)
				{
					c = shipsels[y][x + 1].getColor();
					pos = shipsels[y][x + 1].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);
					c = shipsels[y + 1][x].getColor();
					pos = shipsels[y + 1][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 0.9);

				}
			}
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (int y = 0; y < springs[1].length; y++)
		{
			for (int x = 0; x < springs[1][y].length; x++)
			{
				t = springs[0][y][x];
				b = springs[0][y + 1][x];
				l = springs[2][y][x];
				r = springs[2][y][x + 1];
				e = springs[1][y][x];
				f = springs[3][y][x];

				if (t && b && l && r && e && f)
				{

					c = shipsels[y][x].getColor();
					pos = shipsels[y][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

					c = shipsels[y][x + 1].getColor();
					pos = shipsels[y][x + 1].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

					c = shipsels[y + 1][x].getColor();
					pos = shipsels[y + 1][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

					c = shipsels[y][x + 1].getColor();
					pos = shipsels[y][x + 1].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

					c = shipsels[y + 1][x].getColor();
					pos = shipsels[y + 1][x].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

					c = shipsels[y + 1][x + 1].getColor();
					pos = shipsels[y + 1][x + 1].getPos();
					GL11.glColor4f(c.r, c.g, c.b, c.a);
					GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

				} else
				{
					if (t && r && e)
					{
						c = shipsels[y][x].getColor();
						pos = shipsels[y][x].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y][x + 1].getColor();
						pos = shipsels[y][x + 1].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y + 1][x + 1].getColor();
						pos = shipsels[y + 1][x + 1].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);
					}
					if (b && l && e)
					{
						c = shipsels[y][x].getColor();
						pos = shipsels[y][x].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y + 1][x].getColor();
						pos = shipsels[y + 1][x].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y + 1][x + 1].getColor();
						pos = shipsels[y + 1][x + 1].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);
					}
					if (t && l && f)
					{
						c = shipsels[y][x].getColor();
						pos = shipsels[y][x].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y][x + 1].getColor();
						pos = shipsels[y][x + 1].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y + 1][x].getColor();
						pos = shipsels[y + 1][x].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);
					}
					if (b && r && f)
					{
						c = shipsels[y + 1][x].getColor();
						pos = shipsels[y + 1][x].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y + 1][x + 1].getColor();
						pos = shipsels[y + 1][x + 1].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);

						c = shipsels[y][x + 1].getColor();
						pos = shipsels[y][x + 1].getPos();
						GL11.glColor4f(c.r, c.g, c.b, c.a);
						GL11.glVertex3d(cam.transformX(pos.x), cam.transformY(pos.y), 1);
					}
				}
			}
		}
		GL11.glEnd();
		GL11.glLineWidth(1);

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void UpdateForces(double v)
	{
	}

	@Override
	public void dispose()
	{

	}

	@Override
	public boolean isDisposed()
	{
		return false;
	}
}
