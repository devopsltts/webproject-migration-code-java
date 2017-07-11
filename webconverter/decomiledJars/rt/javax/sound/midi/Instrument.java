package javax.sound.midi;

public abstract class Instrument
  extends SoundbankResource
{
  private final Patch patch;
  
  protected Instrument(Soundbank paramSoundbank, Patch paramPatch, String paramString, Class<?> paramClass)
  {
    super(paramSoundbank, paramString, paramClass);
    this.patch = paramPatch;
  }
  
  public Patch getPatch()
  {
    return this.patch;
  }
}
