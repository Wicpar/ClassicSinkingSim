package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.mech.Base;
import com.wicpar.wicparbase.mech.PVars;
import com.wicpar.wicparbase.utils.ThreadPool;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Frederic on 22/11/2015 at 18:42.
 */
public class ShipBuffer
{
	private static final String shipPath = PVars.GameFolder + File.separator + Main.getPluginID().split("-")[0] + File.separator + "ships";
	private static final HashMap<String, ShipFactory> factories = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ShipBuffer.class);
	private static final List<Future<Ship>> toRelease = new LinkedList<>();

	public static void Init()
	{
		File f = new File(shipPath);
		if (!f.exists())
		{
			f.mkdirs();
			logger.info("Ships folder not found, creating it.");
		} else
		{
			logger.info("Loading Ships from: " + new File(shipPath).getAbsolutePath());
			for (File child : f.listFiles())
			{
				logger.info("checking file: " + child.getAbsolutePath());
				if (child.getName().endsWith(".png"))
				{
					factories.put(child.getName(), new ShipFactory(child));
					logger.info("successfully loaded ship: " + f.getName());
				}
			}
		}
		logger.info("Avaliable ships are: " + factories.keySet());
	}

	private static Future<Ship> getNewShip(String Name, Vector3d pos)
	{
		ShipFactory f = factories.get(Name);
		if (f == null)
			return null;
		else
			return f.MakeNextShip(pos);
	}

	private static class ShipFactory
	{
		private final File model;

		private final String name;

		public ShipFactory(File image)
		{
			name = image.getName();
			model = image;
		}

		public Future<Ship> MakeNextShip(Vector3d pos)
		{
			return ThreadPool.getExecutor().submit(() -> new Ship(model, pos));
		}
	}

	public static List<String> getAvaliableShips()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(factories.keySet());
		return list;
	}

	public static void ScheduleShip(String ship, Vector3d pos)
	{
		toRelease.add(getNewShip(ship, pos));
	}

	public static void ReleaseShips() throws ExecutionException, InterruptedException
	{
		for (Iterator<Future<Ship>> iterator = toRelease.iterator(); iterator.hasNext(); )
		{
			Future<Ship> schedule = iterator.next();
			Ship s;
			if (schedule != null && schedule.isDone() && (s = schedule.get()) != null)
			{
				Base.getClassHandler().addClass(s);
				iterator.remove();
			}
		}
	}
}
