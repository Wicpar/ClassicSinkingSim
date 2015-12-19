package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.physics.IPhysical;

/**
 * Created by Frederic on 15/10/2015 at 20:13.
 */
public class Spring extends com.wicpar.wicparbase.physics.system.Defaults.Spring
{
	private final double breakForce;

	public Spring(Shipsel a, Shipsel b)
	{
		this(0.5, Math.min(a.getMaterial().getStrength(), b.getMaterial().getStrength()), Math.min(a.getMaterial().getStrength(), b.getMaterial().getStrength()) * 1000, a, b);
	}

	private Spring(double damping, double strength, double breakForce, Shipsel a, Shipsel b)
	{
		super(a, b, damping, strength);
		this.breakForce = breakForce ;
	}

	@Override
	public synchronized void ApplyForce(IPhysical physical, double delta)
	{
		super.ApplyForce(physical, delta);
		if (breakForce < lastForce)
			this.dispose();
	}
}
