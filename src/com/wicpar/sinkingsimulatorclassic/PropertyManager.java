package com.wicpar.sinkingsimulatorclassic;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by Frederic on 25/12/2015 at 18:26.
 */
public class PropertyManager
{
	private static final LinkedHashMap<String, Property> properties = new LinkedHashMap();

	public static boolean hasProperty(String s)
	{
		return properties.containsKey(s);
	}
	/**
	 * @param s 		The name of the Property
	 * @param number	The value the Property should get
	 * @param force		If the value should write even if the value exists
	 * @return			If the Property was made.
	 */
	public static boolean RegisterProperty(String s, Number number, boolean force)
	{
		if (properties.containsKey(s))
		{
			if (!force)
				return false;
			else
				properties.get(s).def = number;
		}
		else
			properties.put(s, new Property(number));
		return true;
	}

	public static Property getProperty(String s)
	{
		Property p = properties.get(s);
		if (p == null)
		{
			p = new Property(0);
			properties.put(s, p);
		}
		return p;
	}

	public static class Property
	{
		private Number value;
		private Number def;
		private final LinkedList<PropertyChangeCallback> callbacks = new LinkedList<>();

		public Property(Number value)
		{
			this.value = value;
			def = value;
		}

		public Number getValue()
		{
			return value;
		}

		public void setValue(Number value)
		{
			this.value = value;
			for (int i = 0, callbacksSize = callbacks.size(); i < callbacksSize; i++)
			{
				PropertyChangeCallback callback = callbacks.get(i);
				callback.OnPropertyChanged(this);
			}
		}

		public boolean add(PropertyChangeCallback propertyChangeCallback)
		{
			return callbacks.add(propertyChangeCallback);
		}

		public boolean remove(PropertyChangeCallback o)
		{
			return callbacks.remove(o);
		}

		public void reset()
		{
			value = def;
		}
	}

	public interface PropertyChangeCallback
	{
		void OnPropertyChanged(Property property);
	}
}
