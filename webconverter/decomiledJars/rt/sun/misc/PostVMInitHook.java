package sun.misc;

import sun.usagetracker.UsageTrackerClient;

public class PostVMInitHook
{
  public PostVMInitHook() {}
  
  public static void run() {}
  
  private static void trackJavaUsage()
  {
    UsageTrackerClient localUsageTrackerClient = new UsageTrackerClient();
    localUsageTrackerClient.run("VM start", System.getProperty("sun.java.command"));
  }
}
