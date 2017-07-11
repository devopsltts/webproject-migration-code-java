package javax.imageio.spi;

public abstract interface RegisterableService
{
  public abstract void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
  
  public abstract void onDeregistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass);
}
