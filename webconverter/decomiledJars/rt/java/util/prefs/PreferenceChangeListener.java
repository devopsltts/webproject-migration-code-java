package java.util.prefs;

import java.util.EventListener;

@FunctionalInterface
public abstract interface PreferenceChangeListener
  extends EventListener
{
  public abstract void preferenceChange(PreferenceChangeEvent paramPreferenceChangeEvent);
}
