package com.wicpar.sinkingsimulatorclassic;

import com.wicpar.wicparbase.graphics.IDrawable;
import com.wicpar.wicparbase.mech.Base;

import java.util.HashMap;

/**
 * Created by Frederic on 19/12/2015 at 00:46.
 */
public class Stats
{
	private static final HashMap<Class, Integer> classcounts = new HashMap<>();

	public static int getClassCount(Class c)
	{

		final Integer[] tmp = {0};
		Base.getClassHandler().UpdateClass((cl, params) -> {
			tmp[0]++;
		}, IDrawable.class);
		classcounts.put(c, tmp[0]);
		return classcounts.get(c);
	}
}
