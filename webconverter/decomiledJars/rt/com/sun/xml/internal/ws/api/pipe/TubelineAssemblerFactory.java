package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.assembler.MetroTubelineAssembler;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TubelineAssemblerFactory
{
  private static final Logger logger = Logger.getLogger(TubelineAssemblerFactory.class.getName());
  
  public TubelineAssemblerFactory() {}
  
  public abstract TubelineAssembler doCreate(BindingID paramBindingID);
  
  /**
   * @deprecated
   */
  public static TubelineAssembler create(ClassLoader paramClassLoader, BindingID paramBindingID)
  {
    return create(paramClassLoader, paramBindingID, null);
  }
  
  public static TubelineAssembler create(ClassLoader paramClassLoader, BindingID paramBindingID, @Nullable Container paramContainer)
  {
    Object localObject2;
    if (paramContainer != null)
    {
      localObject1 = (TubelineAssemblerFactory)paramContainer.getSPI(TubelineAssemblerFactory.class);
      if (localObject1 != null)
      {
        localObject2 = ((TubelineAssemblerFactory)localObject1).doCreate(paramBindingID);
        if (localObject2 != null) {
          return localObject2;
        }
      }
    }
    Object localObject1 = ServiceFinder.find(TubelineAssemblerFactory.class, paramClassLoader).iterator();
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (TubelineAssemblerFactory)((Iterator)localObject1).next();
      localObject3 = ((TubelineAssemblerFactory)localObject2).doCreate(paramBindingID);
      if (localObject3 != null)
      {
        logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { localObject2.getClass(), localObject3 });
        return localObject3;
      }
    }
    localObject1 = ServiceFinder.find(PipelineAssemblerFactory.class, paramClassLoader).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PipelineAssemblerFactory)((Iterator)localObject1).next();
      localObject3 = ((PipelineAssemblerFactory)localObject2).doCreate(paramBindingID);
      if (localObject3 != null)
      {
        logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { localObject2.getClass(), localObject3 });
        return new TubelineAssemblerAdapter((PipelineAssembler)localObject3);
      }
    }
    return new MetroTubelineAssembler(paramBindingID, MetroTubelineAssembler.JAXWS_TUBES_CONFIG_NAMES);
  }
  
  private static class TubelineAssemblerAdapter
    implements TubelineAssembler
  {
    private PipelineAssembler assembler;
    
    TubelineAssemblerAdapter(PipelineAssembler paramPipelineAssembler)
    {
      this.assembler = paramPipelineAssembler;
    }
    
    @NotNull
    public Tube createClient(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext)
    {
      ClientPipeAssemblerContext localClientPipeAssemblerContext = new ClientPipeAssemblerContext(paramClientTubeAssemblerContext.getAddress(), paramClientTubeAssemblerContext.getWsdlModel(), paramClientTubeAssemblerContext.getService(), paramClientTubeAssemblerContext.getBinding(), paramClientTubeAssemblerContext.getContainer());
      return PipeAdapter.adapt(this.assembler.createClient(localClientPipeAssemblerContext));
    }
    
    @NotNull
    public Tube createServer(@NotNull ServerTubeAssemblerContext paramServerTubeAssemblerContext)
    {
      if (!(paramServerTubeAssemblerContext instanceof ServerPipeAssemblerContext)) {
        throw new IllegalArgumentException("{0} is not instance of ServerPipeAssemblerContext");
      }
      return PipeAdapter.adapt(this.assembler.createServer((ServerPipeAssemblerContext)paramServerTubeAssemblerContext));
    }
  }
}
