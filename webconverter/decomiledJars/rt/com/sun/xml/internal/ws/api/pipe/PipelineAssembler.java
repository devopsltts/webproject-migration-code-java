package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public abstract interface PipelineAssembler
{
  @NotNull
  public abstract Pipe createClient(@NotNull ClientPipeAssemblerContext paramClientPipeAssemblerContext);
  
  @NotNull
  public abstract Pipe createServer(@NotNull ServerPipeAssemblerContext paramServerPipeAssemblerContext);
}
