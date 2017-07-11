package java.awt;

public class PointerInfo
{
  private final GraphicsDevice device;
  private final Point location;
  
  PointerInfo(GraphicsDevice paramGraphicsDevice, Point paramPoint)
  {
    this.device = paramGraphicsDevice;
    this.location = paramPoint;
  }
  
  public GraphicsDevice getDevice()
  {
    return this.device;
  }
  
  public Point getLocation()
  {
    return this.location;
  }
}
