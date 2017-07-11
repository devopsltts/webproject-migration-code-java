package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.INTERNAL;

public class StateEngineImpl
  implements StateEngine
{
  private static Action emptyAction = new ActionBase("Empty")
  {
    public void doIt(FSM paramAnonymousFSM, Input paramAnonymousInput) {}
  };
  private boolean initializing = true;
  private Action defaultAction = new ActionBase("Invalid Transition")
  {
    public void doIt(FSM paramAnonymousFSM, Input paramAnonymousInput)
    {
      throw new INTERNAL("Invalid transition attempted from " + paramAnonymousFSM.getState() + " under " + paramAnonymousInput);
    }
  };
  
  public StateEngineImpl() {}
  
  public StateEngine add(State paramState1, Input paramInput, Guard paramGuard, Action paramAction, State paramState2)
    throws IllegalArgumentException, IllegalStateException
  {
    mustBeInitializing();
    StateImpl localStateImpl = (StateImpl)paramState1;
    GuardedAction localGuardedAction = new GuardedAction(paramGuard, paramAction, paramState2);
    localStateImpl.addGuardedAction(paramInput, localGuardedAction);
    return this;
  }
  
  public StateEngine add(State paramState1, Input paramInput, Action paramAction, State paramState2)
    throws IllegalArgumentException, IllegalStateException
  {
    mustBeInitializing();
    StateImpl localStateImpl = (StateImpl)paramState1;
    GuardedAction localGuardedAction = new GuardedAction(paramAction, paramState2);
    localStateImpl.addGuardedAction(paramInput, localGuardedAction);
    return this;
  }
  
  public StateEngine setDefault(State paramState1, Action paramAction, State paramState2)
    throws IllegalArgumentException, IllegalStateException
  {
    mustBeInitializing();
    StateImpl localStateImpl = (StateImpl)paramState1;
    localStateImpl.setDefaultAction(paramAction);
    localStateImpl.setDefaultNextState(paramState2);
    return this;
  }
  
  public StateEngine setDefault(State paramState1, State paramState2)
    throws IllegalArgumentException, IllegalStateException
  {
    return setDefault(paramState1, emptyAction, paramState2);
  }
  
  public StateEngine setDefault(State paramState)
    throws IllegalArgumentException, IllegalStateException
  {
    return setDefault(paramState, paramState);
  }
  
  public void done()
    throws IllegalStateException
  {
    mustBeInitializing();
    this.initializing = false;
  }
  
  public void setDefaultAction(Action paramAction)
    throws IllegalStateException
  {
    mustBeInitializing();
    this.defaultAction = paramAction;
  }
  
  public void doIt(FSM paramFSM, Input paramInput, boolean paramBoolean)
  {
    if (paramBoolean) {
      ORBUtility.dprint(this, "doIt enter: currentState = " + paramFSM.getState() + " in = " + paramInput);
    }
    try
    {
      innerDoIt(paramFSM, paramInput, paramBoolean);
    }
    finally
    {
      if (paramBoolean) {
        ORBUtility.dprint(this, "doIt exit");
      }
    }
  }
  
  private StateImpl getDefaultNextState(StateImpl paramStateImpl)
  {
    StateImpl localStateImpl = (StateImpl)paramStateImpl.getDefaultNextState();
    if (localStateImpl == null) {
      localStateImpl = paramStateImpl;
    }
    return localStateImpl;
  }
  
  private Action getDefaultAction(StateImpl paramStateImpl)
  {
    Action localAction = paramStateImpl.getDefaultAction();
    if (localAction == null) {
      localAction = this.defaultAction;
    }
    return localAction;
  }
  
  private void innerDoIt(FSM paramFSM, Input paramInput, boolean paramBoolean)
  {
    if (paramBoolean) {
      ORBUtility.dprint(this, "Calling innerDoIt with input " + paramInput);
    }
    StateImpl localStateImpl1 = null;
    StateImpl localStateImpl2 = null;
    Action localAction = null;
    int i = 0;
    do
    {
      i = 0;
      localStateImpl1 = (StateImpl)paramFSM.getState();
      localStateImpl2 = getDefaultNextState(localStateImpl1);
      localAction = getDefaultAction(localStateImpl1);
      if (paramBoolean)
      {
        ORBUtility.dprint(this, "currentState      = " + localStateImpl1);
        ORBUtility.dprint(this, "in                = " + paramInput);
        ORBUtility.dprint(this, "default nextState = " + localStateImpl2);
        ORBUtility.dprint(this, "default action    = " + localAction);
      }
      Set localSet = localStateImpl1.getGuardedActions(paramInput);
      if (localSet != null)
      {
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext())
        {
          GuardedAction localGuardedAction = (GuardedAction)localIterator.next();
          Guard.Result localResult = localGuardedAction.getGuard().evaluate(paramFSM, paramInput);
          if (paramBoolean) {
            ORBUtility.dprint(this, "doIt: evaluated " + localGuardedAction + " with result " + localResult);
          }
          if (localResult == Guard.Result.ENABLED)
          {
            localStateImpl2 = (StateImpl)localGuardedAction.getNextState();
            localAction = localGuardedAction.getAction();
            if (!paramBoolean) {
              break;
            }
            ORBUtility.dprint(this, "nextState = " + localStateImpl2);
            ORBUtility.dprint(this, "action    = " + localAction);
            break;
          }
          if (localResult == Guard.Result.DEFERED)
          {
            i = 1;
            break;
          }
        }
      }
    } while (i != 0);
    performStateTransition(paramFSM, paramInput, localStateImpl2, localAction, paramBoolean);
  }
  
  private void performStateTransition(FSM paramFSM, Input paramInput, StateImpl paramStateImpl, Action paramAction, boolean paramBoolean)
  {
    StateImpl localStateImpl = (StateImpl)paramFSM.getState();
    int i = !localStateImpl.equals(paramStateImpl) ? 1 : 0;
    if (i != 0)
    {
      if (paramBoolean) {
        ORBUtility.dprint(this, "doIt: executing postAction for state " + localStateImpl);
      }
      try
      {
        localStateImpl.postAction(paramFSM);
      }
      catch (Throwable localThrowable1)
      {
        if (paramBoolean) {
          ORBUtility.dprint(this, "doIt: postAction threw " + localThrowable1);
        }
        if ((localThrowable1 instanceof ThreadDeath)) {
          throw ((ThreadDeath)localThrowable1);
        }
      }
    }
    try
    {
      if (paramAction != null) {
        paramAction.doIt(paramFSM, paramInput);
      }
    }
    finally
    {
      if (i != 0)
      {
        if (paramBoolean) {
          ORBUtility.dprint(this, "doIt: executing preAction for state " + paramStateImpl);
        }
        try
        {
          paramStateImpl.preAction(paramFSM);
        }
        catch (Throwable localThrowable3)
        {
          if (paramBoolean) {
            ORBUtility.dprint(this, "doIt: preAction threw " + localThrowable3);
          }
          if ((localThrowable3 instanceof ThreadDeath)) {
            throw ((ThreadDeath)localThrowable3);
          }
        }
        ((FSMImpl)paramFSM).internalSetState(paramStateImpl);
      }
      if (paramBoolean) {
        ORBUtility.dprint(this, "doIt: state is now " + paramStateImpl);
      }
    }
  }
  
  public FSM makeFSM(State paramState)
    throws IllegalStateException
  {
    mustNotBeInitializing();
    return new FSMImpl(this, paramState);
  }
  
  private void mustBeInitializing()
    throws IllegalStateException
  {
    if (!this.initializing) {
      throw new IllegalStateException("Invalid method call after initialization completed");
    }
  }
  
  private void mustNotBeInitializing()
    throws IllegalStateException
  {
    if (this.initializing) {
      throw new IllegalStateException("Invalid method call before initialization completed");
    }
  }
}
