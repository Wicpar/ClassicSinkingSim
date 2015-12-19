package com.wicpar.sinkingsimulatorclassic;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

/**
 * Created by Frederic on 11/10/2015 at 12:39.
 */
public class Camera
{
	private final Matrix4d Ortho = new Matrix4d().identity();
	private final Matrix4d WorldTransform = new Matrix4d().identity();
	private final Matrix4d Combined = new Matrix4d().identity().mul(WorldTransform).mul(Ortho);
	private final Matrix4d InvCombined = new Matrix4d().set(Combined).invert();

	public Camera()
	{
		this(1,1);
	}

	public Camera(int width, int height)
	{
		this(width, height, 0,0);
	}

	public Camera(int width, int height, Vector2d pos)
	{
		this(width, height,pos.x, pos.y);
	}

	public Camera(int width, int height, double x, double y)
	{
		this(width, height, x, y, 1);
	}

	public Camera(int width, int height, Vector2d pos, double scale)
	{
		this(width, height, pos.x, pos.y, scale);
	}

	public Camera(int width, int height,  double x, double y, double scale)
	{
		UpdateViewPort(width, height).Translate(x, y).Scale(scale);
	}

	public Camera Scale(double scale)
	{
		WorldTransform.scale(scale, scale, 1).setTranslation(WorldTransform.m30 * scale, WorldTransform.m31 * scale, WorldTransform.m32);
		return this.Update();
	}

	public Camera Translate(Vector2d translation)
	{
		return Translate(translation.x, translation.y).Update();
	}

	public Camera Translate(double x, double y)
	{
		WorldTransform.translate(x / WorldTransform.m00, y / WorldTransform.m11, 0);
		return this.Update();
	}

	public Camera UpdateViewPort(int width, int height)
	{
		Ortho.identity().ortho(-width / 2, width / 2, -height / 2, height / 2, -1, 1);
		return this.Update();
	}

	public Camera Update()
	{
		Combined.identity().mul(Ortho).mul(WorldTransform);
		Combined.invert(InvCombined);
		return this;
	}

	public Vector4d tranform(Vector4d vec)
	{
		return vec.mul(WorldTransform).mul(Ortho);
	}
	public Vector3d tranform(Vector3d vec)
	{
		final Vector4d tmp = new Vector4d();
		tmp.set(vec,1);
		tmp.mul(Combined);
		return vec.set(tmp.x, tmp.y, tmp.z);
	}

	public Vector2d tranform(Vector2d vec)
	{
		final Vector4d tmp = new Vector4d();
		tmp.set(vec.x, vec.y, 0, 1);
		tmp.mul(Combined);
		return vec.set(tmp.x, tmp.y);
	}

	public double transformX(double x)
	{
		return (x * Combined.m00 + Combined.m30);
	}

	public double transformY(double y)
	{
		return (y * Combined.m11 + Combined.m31);
	}

	public double untransformX(double x)
	{
		return (x * InvCombined.m00 + InvCombined.m30);
	}

	public double untransformY(double y)
	{
		return (y * InvCombined.m11 + InvCombined.m31);
	}

	public double scaleSize(double s)
	{
		return WorldTransform.m00 * s;
	}

}
