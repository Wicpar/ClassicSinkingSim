package com.wicpar.sinkingsimulatorclassic.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Frederic on 01/01/2016 at 04:25.
 */
public class Shader
{
	private static final ArrayList<Shader> shaders = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(Shader.class);
	private Integer ID = null;
	private String source = null;
	private final int type;

	public Shader(int type)
	{
		this.type = type;
		shaders.add(this);
	}

	public Shader(int type, String source)
	{
		this(type);
		this.source = source;
	}

	public Shader compile()
	{
		if (ID == null)
		{
			delete();
			ID = GL20.glCreateShader(type);
		}
		GL20.glShaderSource(ID, source);
		GL20.glCompileShader(ID);
		if (GL20.glGetShaderi(ID, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
			logger.error("failed to compile shader: \n" + GL20.glGetShaderInfoLog(ID));
		}
		return this;
	}

	public Shader delete()
	{
		if (ID != null)
		{
			GL20.glDeleteShader(ID);
			ID = null;
		}
		return this;
	}

	public int getType()
	{
		return type;
	}

	public String getSource()
	{
		return source;
	}

	public Integer getID()
	{
		return ID;
	}

	public Shader setSource(String source)
	{
		this.source = source;
		return this;
	}

	public static void Cleanup()
	{
		for (Shader shader : shaders)
		{
			shader.delete();
		}
	}

}
