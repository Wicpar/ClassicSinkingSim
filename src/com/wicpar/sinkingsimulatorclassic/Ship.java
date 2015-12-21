package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.mech.Base;
import com.wicpar.wicparbase.physics.IDynamical;
import com.wicpar.wicparbase.utils.ClassPool;
import org.joml.Vector3d;

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
	private final Spring[][][][] springs;
	private final String name;
	private final BufferedImage texture;
	private int subsprings = 4;

	private Ship(Shipsel[][] shipsels, Spring[][][][] springs, String name, BufferedImage texture)
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
		springs = new Spring[Math.max(texture.getHeight(), texture.getWidth())][4][][];
		//subsprings = Math.max((Math.min(texture.getHeight(), texture.getWidth()) - 1) / 4, subsprings);
		for (int i = 0; i < Math.max(1, springs.length / 2); i++)
		{
			if (texture.getHeight() - i >= 0 && texture.getWidth() - 1 - i >= 0)
				springs[i][0] = new Spring[texture.getHeight()][texture.getWidth() - 1 - i];
			if (texture.getHeight() - 1 - i >= 0 && texture.getWidth() - 1 - i >= 0)
				springs[i][1] = new Spring[texture.getHeight() - 1 - i][texture.getWidth() - 1 - i];
			if (texture.getHeight() - 1 - i >= 0 && texture.getWidth() - i >= 0)
				springs[i][2] = new Spring[texture.getHeight() - 1 - i][texture.getWidth()];
			if (texture.getHeight() - 1 - i >= 0 && texture.getWidth() - 1 - i >= 0)
				springs[i][3] = new Spring[texture.getHeight() - 1 - i][texture.getWidth() - 1 - i];
		}
		for (int y = 0; y < texture.getHeight(); y++)
		{
			for (int x = 0; x < texture.getWidth(); x++)
			{
				Color c = new Color();
				Color.argb8888ToColor(c, texture.getRGB(x, y));
				if (!c.equals(Color.WHITE) && Material.fromColor(c.toString()) != null)
				{
					Shipsel current = shipsels[y][x] = new Shipsel(Material.fromColor(c.toString()), x - texture.getWidth() / 2. + pos.x, -(y - texture.getHeight() / 2.) + pos.y);
					Shipsel tmp = null;
					if (x > 0 && (tmp = shipsels[y][x - 1]) != null)
						springs[0][0][y][x - 1] = new Spring(tmp, current, true);
					if (y > 0 && x > 0 && (tmp = shipsels[y - 1][x - 1]) != null)
						springs[0][1][y - 1][x - 1] = new Spring(tmp, current, true);
					if (y > 0 && (tmp = shipsels[y - 1][x]) != null)
						springs[0][2][y - 1][x] = new Spring(tmp, current, true);
					if (y > 0 && x < shipsels[0].length - 1 && (tmp = shipsels[y - 1][x + 1]) != null)
						springs[0][3][y - 1][x] = new Spring(tmp, current, true);
				}
			}
		}
		for (int i = 1; i <= subsprings; i++)
		{
			if (springs[i] != null && springs[i - 1] != null && springs[i][0] != null && springs[i - 1][0] != null)
			{
				Spring[][] curLayer = springs[i][0];
				Spring[][] lastLayer = springs[i - 1][0];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x];
						Spring lb = lastLayer[y][x + 1];
						if (la != null && lb != null)
						{
							curLayer[y][x] = new Spring(la, lb);
						}
					}
				}
			}

			if (springs[i] != null && springs[i - 1] != null && springs[i][1] != null && springs[i - 1][1] != null)
			{
				Spring[][] curLayer = springs[i][1];
				Spring[][] lastLayer = springs[i - 1][1];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x];
						Spring lb = lastLayer[y + 1][x + 1];
						if (la != null && lb != null)
						{
							curLayer[y][x] = new Spring(la, lb);
						}
					}
				}
			}
			if (springs[i] != null && springs[i - 1] != null && springs[i][2] != null && springs[i - 1][2] != null)
			{
				Spring[][] curLayer = springs[i][2];
				Spring[][] lastLayer = springs[i - 1][2];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x];
						Spring lb = lastLayer[y + 1][x];
						if (la != null && lb != null)
						{
							curLayer[y][x] = new Spring(la, lb);
						}
					}
				}
			}
			if (springs[i] != null && springs[i - 1] != null && springs[i][3] != null && springs[i - 1][3] != null)
			{
				Spring[][] curLayer = springs[i][3];
				Spring[][] lastLayer = springs[i - 1][3];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x + 1];
						Spring lb = lastLayer[y + 1][x];
						if (la != null && lb != null)
						{
							curLayer[y][x] = new Spring(la, lb);
						}
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
			for (int y = 0, shipselsLength = shipsels.length; y < shipselsLength; y++)
			{
				if (shipsels[y] != null)
					for (int x = 0, shipselLength = shipsels[y].length; x < shipselLength; x++)
					{
						Shipsel shipsel = shipsels[y][x];
						if (shipsel != null && !shipsel.isDisposed())
						{
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
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * top));
									++done;
								}
								if (y < shipsels[0].length && (tmp = shipsels[y + 1][x]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * top));
									++done;
								}
								if (y > 0 && (tmp = shipsels[y - 1][x]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * 0.1 * top));
									++done;
								}
								if (x < shipsels[y].length - 1 && (tmp = shipsels[y][x + 1]) != null)
								{
									tmp.setFlooded((float) (tmp.getFlooded() + flood * delta * top));
									++done;
								}
							}
						} else if (shipsel != null && shipsel.isDisposed())
							shipsels[y][x] = null;
					}
			}
		}
	}

	@Override
	public void draw()
	{

	}

	@Override
	public void UpdateForces(double v)
	{
		int i = 0;
		if (springs[i] != null && springs[i][0] != null)
		{
			Spring[][] curLayer = springs[i][0];

			for (int y = 0; y < curLayer.length; y++)
			{
				for (int x = 0; x < curLayer[y].length; x++)
				{
					if (curLayer[y][x] != null && curLayer[y][x].isDisposed())
					{
						curLayer[y][x].getA().setDamaged(true);
						curLayer[y][x].getB().setDamaged(true);
						curLayer[y][x] = null;
					}
				}
			}
		}
		if (springs[i] != null && springs[i][1] != null)
		{
			Spring[][] curLayer = springs[i][1];

			for (int y = 0; y < curLayer.length; y++)
			{
				for (int x = 0; x < curLayer[y].length; x++)
				{
					if (curLayer[y][x] != null && curLayer[y][x].isDisposed())
					{
						curLayer[y][x].getA().setDamaged(true);
						curLayer[y][x].getB().setDamaged(true);
						curLayer[y][x] = null;
					}
				}
			}
		}
		if (springs[i] != null && springs[i][2] != null)
		{
			Spring[][] curLayer = springs[i][2];

			for (int y = 0; y < curLayer.length; y++)
			{
				for (int x = 0; x < curLayer[y].length; x++)
				{
					if (curLayer[y][x] != null && curLayer[y][x].isDisposed())
					{
						curLayer[y][x].getA().setDamaged(true);
						curLayer[y][x].getB().setDamaged(true);
						curLayer[y][x] = null;
					}
				}
			}
		}
		if (springs[i] != null && springs[i][3] != null)
		{
			Spring[][] curLayer = springs[i][3];

			for (int y = 0; y < curLayer.length; y++)
			{
				for (int x = 0; x < curLayer[y].length; x++)
				{
					if (curLayer[y][x] != null && curLayer[y][x].isDisposed())
					{
						curLayer[y][x].getA().setDamaged(true);
						curLayer[y][x].getB().setDamaged(true);
						curLayer[y][x] = null;
					}
				}
			}
		}
		for (i = 1; i <= subsprings; i++)
		{
			if (springs[i] != null && springs[i - 1] != null && springs[i][0] != null && springs[i - 1][0] != null)
			{
				Spring[][] curLayer = springs[i][0];
				Spring[][] lastLayer = springs[i - 1][0];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x];
						Spring lb = lastLayer[y][x + 1];
						if ((la == null || lb == null) && curLayer[y][x] != null)
						{
							curLayer[y][x].dispose();
							curLayer[y][x] = null;
						}
					}
				}
			}

			if (springs[i] != null && springs[i - 1] != null && springs[i][1] != null && springs[i - 1][1] != null)
			{
				Spring[][] curLayer = springs[i][1];
				Spring[][] lastLayer = springs[i - 1][1];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x];
						Spring lb = lastLayer[y + 1][x + 1];
						if ((la == null || lb == null) && curLayer[y][x] != null)
						{
							curLayer[y][x].dispose();
							curLayer[y][x] = null;
						}
					}
				}
			}
			if (springs[i] != null && springs[i - 1] != null && springs[i][2] != null && springs[i - 1][2] != null)
			{
				Spring[][] curLayer = springs[i][2];
				Spring[][] lastLayer = springs[i - 1][2];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x];
						Spring lb = lastLayer[y + 1][x];
						if ((la == null || lb == null) && curLayer[y][x] != null)
						{
							curLayer[y][x].dispose();
							curLayer[y][x] = null;
						}
					}
				}
			}
			if (springs[i] != null && springs[i - 1] != null && springs[i][3] != null && springs[i - 1][3] != null)
			{
				Spring[][] curLayer = springs[i][3];
				Spring[][] lastLayer = springs[i - 1][3];

				for (int y = 0; y < curLayer.length; y++)
				{
					for (int x = 0; x < curLayer[y].length; x++)
					{
						Spring la = lastLayer[y][x + 1];
						Spring lb = lastLayer[y + 1][x];
						if ((la == null || lb == null) && curLayer[y][x] != null)
						{
							curLayer[y][x].dispose();
							curLayer[y][x] = null;
						}
					}
				}
			}
		}
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
