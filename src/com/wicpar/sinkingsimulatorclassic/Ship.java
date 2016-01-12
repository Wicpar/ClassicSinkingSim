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
					origin.applyForce(force);
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
					origin.applyForce(force);
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
					origin.applyForce(force);
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
						origin.applyForce(force);
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
					origin.applyForce(force);
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
					origin.applyForce(force);
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
					origin.applyForce(force);
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
