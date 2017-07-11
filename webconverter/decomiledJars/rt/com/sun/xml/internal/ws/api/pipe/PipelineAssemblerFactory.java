package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.pipe.StandalonePipeAssembler;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @deprecated
 */
public abstract class PipelineAssemblerFactory
{
  private static final Logger logger = Logger.getLogger(PipelineAssemblerFactory.class.getName());
  
  public PipelineAssemblerFactory() {}
  
  public abstract PipelineAssembler doCreate(BindingID paramBindingID);
  
  public static PipelineAssembler create(ClassLoader paramClassLoader, BindingID paramBindingID)
  {
    Iterator localIterator = ServiceFinder.find(PipelineAssemblerFactory.class, paramClassLoader).iterator();
    while (localIterator.hasNext())
    {
      PipelineAssemblerFactory localPipelineAssemblerFactory = (PipelineAssemblerFactory)localIterator.next();
      PipelineAssembler localPipelineAssembler = localPipelineAssemblerFactory.doCreate(paramBindingID);
      if (localPipelineAssembler != null)
      {
        logger.fine(localPipelineAssemblerFactory.getClass() + " successfully created " + localPipelineAssembler);
        return localPipelineAssembler;
      }
    }
    return new StandalonePipeAssembler();
  }
}
