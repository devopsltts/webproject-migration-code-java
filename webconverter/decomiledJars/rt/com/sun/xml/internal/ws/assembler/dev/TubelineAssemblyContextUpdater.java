package com.sun.xml.internal.ws.assembler.dev;

import javax.xml.ws.WebServiceException;

public abstract interface TubelineAssemblyContextUpdater
{
  public abstract void prepareContext(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    throws WebServiceException;
  
  public abstract void prepareContext(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    throws WebServiceException;
}
