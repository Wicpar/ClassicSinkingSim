package com.wicpar.sinkingsimulatorclassic.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Frederic on 01/01/2016 at 10:35.
 */
public class ShaderProgram
{
	private static final ArrayList<ShaderProgram> shaderPrograms = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(ShaderProgram.class);

	private Integer ID = null;
	private final ArrayList<Shader> shaders = new ArrayList<>();

	public ShaderProgram()
	{
	}

	public ShaderProgram create()
	{
		delete();
		ID = GL20.glCreateProgram();
		return this;
	}

	public ShaderProgram setShaders(Shader... shaders)
	{
		if (ID == null)
			create();
		if (this.shaders.size() != 0)
		{
			for (Shader shader : this.shaders)
			{
				GL20.glDetachShader(ID, shader.getID());
			}
			this.shaders.clear();
		}
		for (Shader shader : shaders)
		{
			GL20.glAttachShader(ID, shader.getID());
		}
		GL20.glLinkProgram(ID);
		if (GL20.glGetProgrami(ID, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
			logger.error("failed to link program: \n" + GL20.glGetProgramInfoLog(ID));
		}
		return this;
	}

	public ShaderProgram delete()
	{
		if (ID != null)
		{
			GL20.glDeleteProgram(ID);
			ID = null;
		}
		return this;
	}

	public Integer getID()
	{
		return ID;
	}

	public Shader[] getShaders()
	{
		return (Shader[]) shaders.toArray();
	}

	public static void Cleanup()
	{
		for (ShaderProgram shader : shaderPrograms)
		{
			shader.delete();
		}
	}
}
