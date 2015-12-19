package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.mech.PVars;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Frederic on 22/11/2015 at 18:42.
 */
public class ShipBuffer
{
	private static final String shipPath = PVars.GameFolder + File.separator + Main.getPluginID().split("-")[0] + File.separator + "ships";
	private static final HashMap<String, ShipFactory> factories = new HashMap<>();
	private static volatile int maxGeneratedShips = 0;
	private static final Logger logger = LoggerFactory.getLogger(ShipBuffer.class);
	private static final List<ShipSpawnSchedule> toRelease = new LinkedList<>();
	private static final List<Ship> rel = new LinkedList<>();

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

	private static  Future<Ship> getNewShip(String Name)
	{
		ShipFactory f = factories.get(Name);
		if (f == null)
			return null;
		else
			return f.releaseNextShip();
	}

	private static class ShipFactory
	{
		private final File model;
		private List<Future<Ship>> ships = new ArrayList<>();

		private final String name;

		public ShipFactory(File image)
		{
			name = image.getName();
			model = image;
			/*
			for (int i = 0; i < maxGeneratedShips; i++)
			{
				ships.add(ThreadPool.getExecutor().submit(() -> new Ship(model)));
			}*/

		}

		public Future<Ship> releaseNextShip()
		{
			/*
			Future<Ship> s = ships.size() > 0 ? ships.remove(0) : ThreadPool.getExecutor().submit(() -> new Ship(model));
			while (ships.size() < maxGeneratedShips)
			{
				ships.add(ThreadPool.getExecutor().submit(() -> new Ship(model)));
			}
			return s;*/
			return null;
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
		//toRelease.add(new ShipSpawnSchedule(pos, getNewShip(ship)));
		new Ship(factories.get(ship).model, pos);
	}

	public static void ReleaseShips() throws ExecutionException, InterruptedException
	{
		/*
		for (ShipSpawnSchedule schedule : toRelease)
		{
			if (schedule != null && schedule.ship.isDone() && schedule.ship.get() != null)
				schedule.ship.get().Initialize(schedule.pos);
		}*/
	}

	private static class ShipSpawnSchedule
	{
		private final Vector3d pos;
		private final Future<Ship> ship;

		public ShipSpawnSchedule(Vector3d pos, Future<Ship> ship)
		{
			this.pos = pos;
			this.ship = ship;
		}
	}
}
