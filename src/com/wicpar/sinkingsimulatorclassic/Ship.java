package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.utils.ClassPool;
import org.joml.Vector3d;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Frederic on 19/12/2015 at 06:22.
 */
public class Ship extends ClassPool implements IDrawable
{
	private final Shipsel[][] shipsels;
	private final Spring[][][][] springs;
	private final String name;
	private final BufferedImage texture;

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
					Shipsel current = shipsels[y][x] = new Shipsel(Material.fromColor(c.toString()), x - x / 2. + pos.x, -(y - y / 2.) + pos.y);
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
		/*
		for (int i = 1; i < springs.length; i++)
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
		}*/
		name = image.getName();
	}

	@Override
	public void UpdateClass(Updater updater, Class c, Object... params)
	{
		//super.UpdateClass(updater, c, params);
		if (c != null && c.isAssignableFrom(Shipsel.class))
		{
			for (int i = 0, shipselsLength = shipsels.length; i < shipselsLength; i++)
			{
				Shipsel[] shipsel = shipsels[i];
				if (shipsel != null)
					for (int i1 = 0, shipselLength = shipsel.length; i1 < shipselLength; i1++)
					{
						Shipsel shipsel1 = shipsel[i1];
						if (shipsel1 != null && !shipsel1.isDisposed())
							updater.Update(shipsel1, params);
						if (shipsel1 != null && shipsel1.isDisposed())
							shipsel[i1] = null;
					}
			}
		}
	}

	@Override
	public void draw()
	{

	}
}
