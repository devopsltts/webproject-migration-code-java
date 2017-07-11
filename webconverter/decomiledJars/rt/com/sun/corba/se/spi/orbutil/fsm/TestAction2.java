package com.sun.corba.se.spi.orbutil.fsm;

import java.io.PrintStream;

class TestAction2
  implements Action
{
  private State oldState;
  private State newState;
  
  public void doIt(FSM paramFSM, Input paramInput)
  {
    System.out.println("TestAction2:");
    System.out.println("\toldState = " + this.oldState);
    System.out.println("\tnewState = " + this.newState);
    System.out.println("\tinput    = " + paramInput);
    if (this.oldState != paramFSM.getState()) {
      throw new Error("Unexpected old State " + paramFSM.getState());
    }
  }
  
  public TestAction2(State paramState1, State paramState2)
  {
    this.oldState = paramState1;
    this.newState = paramState2;
  }
}
