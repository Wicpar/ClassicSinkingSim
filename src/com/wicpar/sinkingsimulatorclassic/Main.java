package com.wicpar.sinkingsimulatorclassic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wicpar.wicparbase.graphics.Color;
import com.wicpar.wicparbase.input.GenericGLFW;
import com.wicpar.wicparbase.mech.Base;
import com.wicpar.wicparbase.mech.PVars;
import com.wicpar.wicparbase.physics.IDynamical;
import com.wicpar.wicparbase.physics.system.Defaults.Gravity;
import com.wicpar.wicparbase.physics.system.Physical;
import com.wicpar.wicparbase.utils.ClassPool;
import com.wicpar.wicparbase.utils.plugins.Injector;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.libffi.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.Extension;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Frederic on 26/09/2015 at 12:23.
 */
public class Main extends Plugin
{
	private static String PluginID;

	public static String getPluginID()
	{
		return PluginID;
	}

	/**
	 * Constructor to be used by plugin manager for plugin instantiation.
	 * Your plugins have to provide constructor with this exact signature to
	 * be successfully loaded by manager.
	 *
	 * @param wrapper
	 */
	public Main(PluginWrapper wrapper)
	{
		super(wrapper);
		PluginID = wrapper.getPluginId().concat("-").concat(wrapper.getDescriptor().getVersion().toString());
	}

	@Extension
	public static class ClassicSinkingSim implements Injector, IDynamical
	{
		private static ClassicSinkingSim main;
		private final Logger logger = LoggerFactory.getLogger(ClassicSinkingSim.class);
		private Vector2d LastWPos = new Vector2d();
		private final Camera cam = new Camera();
		private static final String dataFolder = PVars.BaseModsFolder.concat("/").concat(Main.PluginID).concat("/data");

		private final Sky sky;
		private final Ground ground;
		private final Sea sea;
		private final Gravity gravity;

		public ClassicSinkingSim()
		{
			main = this;
			sky = new Sky();
			ground = new Ground();
			sea = new Sea();
			gravity = new Gravity();
		}

		@Override
		public void OnHandlerPreInit()
		{
			logger.info("preinit");
			File f = new File(dataFolder);
			if (!f.exists())
				f.mkdirs();
			f = new File(dataFolder + "/materials.json");
			if (!f.exists())
			{
				try
				{
					f.createNewFile();
					PrintWriter out = new PrintWriter(f);
					out.print(Defaults.DefaultMaterials);
					out.close();
				} catch (Exception e)
				{
					logger.error("Failed to create defaults", e);
				}
			}
			byte[] encoded = new byte[0];
			try
			{
				encoded = Files.readAllBytes(Paths.get(f.getPath()));
				Material.loadFromTemplateList((ArrayList<Material.MaterialTemplate>) (new Gson().fromJson(new String(encoded), new TypeToken<ArrayList<Material.MaterialTemplate>>()
				{
				}.getType())));
			} catch (IOException e)
			{
				logger.error("Failed to load materials", e);
			}

		}

		@Override
		public void OnHandlerPostInit()
		{

		}

		@Override
		public void OnGamePreInit()
		{

		}

		@Override
		public void OnGamePostInit()
		{

			Base.getInputHandler().addInput((i, objects) -> {
				if (i == GenericGLFW.onCursorPosCallback)
				{
				} else if (i == GenericGLFW.onMouseButtonCallback)
				{

					int button = (Integer) objects[1];
					int action = (Integer) objects[2];
					final DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
					final DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
					final IntBuffer w = BufferUtils.createIntBuffer(1);
					final IntBuffer h = BufferUtils.createIntBuffer(1);
					glfwGetCursorPos((Long) objects[0], x, y);
					double xp = x.get(0);
					double yp = y.get(0);
					glfwGetWindowSize((Long) objects[0], w, h);
					xp /= w.get(0);
					yp /= h.get(0);
					xp = xp * 2 - 1;
					yp = -yp * 2 + 1;
					xp = cam.untransformX(xp);
					yp = cam.untransformY(yp);
					if (button == 0 && action == 1)
					{


						/*
						int iter = 0;

						while (iter < 100)
						{
							Shipsel s = new Shipsel(Material.fromColor(Color.valueOf("E0D8C0").toString()), xp, yp);
							Shipsel t = new Shipsel(Material.fromColor(Color.valueOf("E0D8C0").toString()), xp + 1, yp);
							Shipsel u = new Shipsel(Material.fromColor(Color.valueOf("E0D8C0").toString()), xp + 1, yp + 1);
							Shipsel v = new Shipsel(Material.fromColor(Color.valueOf("E0D8C0").toString()), xp, yp + 1);
							Base.getClassHandler().addClass(s);
							Base.getClassHandler().addClass(t);
							Base.getClassHandler().addClass(u);
							Base.getClassHandler().addClass(v);

							Spring a = new Spring(s, t, true);
							Spring b = new Spring(t, u, true);
							Spring c = new Spring(u, v, true);
							Spring d = new Spring(v, s, true);
							Spring e = new Spring(s, u, true);
							Spring f = new Spring(t, v, true);
							iter++;

							//ShipBuffer.getAvaliableShips().get(new Random().nextInt(ShipBuffer.getAvaliableShips().size()))
						}*/

						ShipBuffer.ScheduleShip("ship.png", new Vector3d(xp, yp, 0));

					}
					if (button == 1 && action == 1)
					{
						final double[] buf = new double[2];
						buf[0] = xp;
						buf[1] = yp;
						Base.getClassHandler().UpdateClass((o, objects1) -> {
							Shipsel s = ((Shipsel)o);
							if (s.getPos().distance(buf[0], buf[1], 0) < 1)
								s.dispose();
						}, Shipsel.class);
					}
				} else if (i == GenericGLFW.onWindowSizeCallback)
				{
					int x, y;
					IntBuffer vp = BufferUtils.createIntBuffer(4);
					GL11.glGetIntegerv(GL11.GL_VIEWPORT, vp);
					GL11.glViewport(0, 0, x = (Integer) objects[1], y = (Integer) objects[2]);
					cam.Translate(-(x - vp.get(2)) / 2., (y - vp.get(3)) / 2.);
					cam.UpdateViewPort(x, y);
					sea.setDivisions(x / 2);

				} else if (i == GenericGLFW.onWindowPosCallback)
				{
					if (LastWPos == null)
					{
						LastWPos = new Vector2d((Integer) objects[1], (Integer) objects[2]);
					} else
					{
						int x, y;
						cam.Translate((LastWPos.x - (x = (Integer) objects[1])), -(LastWPos.y - (y = (Integer) objects[2])));
						LastWPos.set(x, y);
					}
				} else if (i == GenericGLFW.onKeyCallback)
				{
					if (((Integer) objects[1]) == GLFW.GLFW_KEY_P)
					{
						logger.info(" fps: " + 1 / Base.getDelta() + " Dynamicals Num: " + Stats.getClassCount(Physical.class));
					}
				} else if (i == GenericGLFW.onScrollCallback)
				{
					cam.Scale(Math.pow(1.1, (Double) objects[2]));
				}
				return true;
			});
			Base.getClassHandler().addClass(sky);
			Base.getClassHandler().addClass(ground);
			Base.getClassHandler().addClass(sea);
			Base.getClassHandler().addClass(gravity);
			Base.getClassHandler().addClass(this);

			ShipBuffer.Init();

			final long window = Base.getRenderer().getWindow("Main");
			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);
			glfwGetWindowSize(window, w, h);
			int width = w.get(0);
			int height = h.get(0);


			cam.UpdateViewPort(width, height);


			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();

			GL11.glOrtho(-1, 1, -1, 1, -1, 1);

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
			GL11.glEnable(GL11.GL_POINT_SMOOTH);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
			GL11.glDepthFunc(GL11.GL_LEQUAL);

			glfwGetWindowPos(window, w, h);
			LastWPos.set(w.get(0), h.get(0));

		}

		@Override
		public void OnGameFinish()
		{

		}

		public static ClassicSinkingSim getInstance()
		{
			return main;
		}

		@Override
		public void UpdateForces(double v)
		{
			final long window = Base.getRenderer().getWindow("Main");
			final long speed = 1000;
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == 1)
			{
				cam.Translate(0, -v * speed);
			}
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == 1)
			{
				cam.Translate(0, v * speed);
			}
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == 1)
			{
				cam.Translate(v * speed, 0);
			}
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == 1)
			{
				cam.Translate(-v * speed, 0);
			}
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == 1)
			{
				ground.h -= v * speed;
			}
			if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == 1)
			{
				ground.h += v * speed;
			}
			try
			{
				ShipBuffer.ReleaseShips();
			} catch (Exception e)
			{
				logger.error("Failed to release ships",e);
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

		public Sky getSky()
		{
			return sky;
		}

		public Ground getGround()
		{
			return ground;
		}

		public Sea getSea()
		{
			return sea;
		}

		public Camera getCam()
		{
			return cam;
		}
	}

	private static class Defaults
	{
		private static final String DefaultMaterials = "[\n" +
				"    {\n" +
				"        \"name\": \"Steel Hull\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#A0A0A0\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Steel\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#D8D8D8\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Iron Hull\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#404050\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Iron\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#808090\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Wooden Hull\",\n" +
				"        \"strength\": 6,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#905020\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Wood\",\n" +
				"        \"strength\": 5,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#E0D8C0\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Titanium Hull\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#565663\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Titanium\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#787896\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Cardboard Hull\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 30,\n" +
				"        \"colour\": \"#A88848\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Cardboard\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 20,\n" +
				"        \"colour\": \"#C1A46A\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Rope\",\n" +
				"        \"strength\": 8,\n" +
				"        \"mass\": 500,\n" +
				"        \"colour\": \"#000000\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Red\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#A71000\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Red\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#D80E00\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Red\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#840E00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Red\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#9E0F00\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Red\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#9E2E0C\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Red\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#B51B1B\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Red\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#8F1F00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Red\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#BE1400\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Yel\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#A27A00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Yel\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#D29800\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Yel\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#806200\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Yel\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#997200\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Yel\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#998B0A\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Yel\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#AF7C18\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Yel\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#8B7A00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Yel\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#B88D00\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Gre\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#5A9B00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Gre\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#7BC800\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Gre\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#467B00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Gre\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#569200\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Gre\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#439206\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Gre\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#78A814\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Gre\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#3C8400\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Gre\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#66B100\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Blue\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#008A95\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Blue\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#00B6BF\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Blue\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#006A76\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Blue\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#00818C\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Blue\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#036E8C\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Blue\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#10A19D\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Blue\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#00657E\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Blue\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#009BA9\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Blue2\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#22437B\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Blue2\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#2D58A0\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Blue2\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#1B3261\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Blue2\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#203E74\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Blue2\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#1E3070\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Blue2\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#325989\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Blue2\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#1A2C66\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Blue2\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#26498C\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Pink\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#A900A7\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Pink\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#D100D6\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Pink\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#860080\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Pink\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#9F009C\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Pink\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#A30085\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Pink\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#9B00AF\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Pink\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#93007B\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Pink\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#BF00BA\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"SH_Orange\",\n" +
				"        \"strength\": 35,\n" +
				"        \"mass\": 7580,\n" +
				"        \"colour\": \"#D45B00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"S_Orange\",\n" +
				"        \"strength\": 32,\n" +
				"        \"mass\": 7480,\n" +
				"        \"colour\": \"#F26500\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"IH_Orange\",\n" +
				"        \"strength\": 22,\n" +
				"        \"mass\": 7950,\n" +
				"        \"colour\": \"#A84F00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"I_Orange\",\n" +
				"        \"strength\": 20,\n" +
				"        \"mass\": 7850,\n" +
				"        \"colour\": \"#C85800\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"WH_Orange\",\n" +
				"        \"strength\": 2,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#CE8100\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"W_Orange\",\n" +
				"        \"strength\": 1,\n" +
				"        \"mass\": 800,\n" +
				"        \"colour\": \"#DE4000\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"TH_Orange\",\n" +
				"        \"strength\": 30,\n" +
				"        \"mass\": 4600,\n" +
				"        \"colour\": \"#BA7100\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"T_Orange\",\n" +
				"        \"strength\": 28,\n" +
				"        \"mass\": 4500,\n" +
				"        \"colour\": \"#EF6B00\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"DB_S_Red\",\n" +
				"        \"strength\": 64,\n" +
				"        \"mass\": 6500,\n" +
				"        \"colour\": \"#A21000\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"DB_I_Red\",\n" +
				"        \"strength\": 50,\n" +
				"        \"mass\": 7500,\n" +
				"        \"colour\": \"#7E0C00\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"DB_T_Red\",\n" +
				"        \"strength\": 80,\n" +
				"        \"mass\": 6500,\n" +
				"        \"colour\": \"#842005\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Glass\",\n" +
				"        \"strength\": 4,\n" +
				"        \"mass\": 1000,\n" +
				"        \"colour\": \"#E5FFFF\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Carbon Hull\",\n" +
				"        \"strength\": 120,\n" +
				"        \"mass\": 758,\n" +
				"        \"colour\": \"#1A1A1A\",\n" +
				"        \"isHull\": true\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"Carbon\",\n" +
				"        \"strength\": 120,\n" +
				"        \"mass\": 758,\n" +
				"        \"colour\": \"#333333\"\n" +
				"    },\n" +
				"    {\n" +
				"        \"name\": \"DB_W_Red\",\n" +
				"        \"strength\": 40,\n" +
				"        \"mass\": 1600,\n" +
				"        \"colour\": \"#9B3012\",\n" +
				"        \"isHull\": true\n" +
				"    }\n" +
				"]";
	}

}
