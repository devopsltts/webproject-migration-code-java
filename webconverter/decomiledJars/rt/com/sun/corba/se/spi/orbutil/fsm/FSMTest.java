package com.sun.corba.se.spi.orbutil.fsm;

public class FSMTest
{
  public static final State STATE1 = new StateImpl("1");
  public static final State STATE2 = new StateImpl("2");
  public static final State STATE3 = new StateImpl("3");
  public static final State STATE4 = new StateImpl("4");
  public static final Input INPUT1 = new InputImpl("1");
  public static final Input INPUT2 = new InputImpl("2");
  public static final Input INPUT3 = new InputImpl("3");
  public static final Input INPUT4 = new InputImpl("4");
  private Guard counterGuard = new Guard()
  {
    public Guard.Result evaluate(FSM paramAnonymousFSM, Input paramAnonymousInput)
    {
      MyFSM localMyFSM = (MyFSM)paramAnonymousFSM;
      return Guard.Result.convert(localMyFSM.counter < 3);
    }
  };
  
  public FSMTest() {}
  
  private static void add1(StateEngine paramStateEngine, State paramState1, Input paramInput, State paramState2)
  {
    paramStateEngine.add(paramState1, paramInput, new TestAction1(paramState1, paramInput, paramState2), paramState2);
  }
  
  private static void add2(StateEngine paramStateEngine, State paramState1, State paramState2)
  {
    paramStateEngine.setDefault(paramState1, new TestAction2(paramState1, paramState2), paramState2);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    TestAction3 localTestAction3 = new TestAction3(STATE3, INPUT1);
    StateEngine localStateEngine = StateEngineFactory.create();
    add1(localStateEngine, STATE1, INPUT1, STATE1);
    add2(localStateEngine, STATE1, STATE2);
    add1(localStateEngine, STATE2, INPUT1, STATE2);
    add1(localStateEngine, STATE2, INPUT2, STATE2);
    add1(localStateEngine, STATE2, INPUT3, STATE1);
    add1(localStateEngine, STATE2, INPUT4, STATE3);
    localStateEngine.add(STATE3, INPUT1, localTestAction3, STATE3);
    localStateEngine.add(STATE3, INPUT1, localTestAction3, STATE4);
    add1(localStateEngine, STATE3, INPUT2, STATE1);
    add1(localStateEngine, STATE3, INPUT3, STATE2);
    add1(localStateEngine, STATE3, INPUT4, STATE2);
    MyFSM localMyFSM = new MyFSM(localStateEngine);
    TestInput localTestInput1 = new TestInput(INPUT1, "1.1");
    TestInput localTestInput2 = new TestInput(INPUT1, "1.2");
    TestInput localTestInput3 = new TestInput(INPUT2, "2.1");
    TestInput localTestInput4 = new TestInput(INPUT2, "2.2");
    TestInput localTestInput5 = new TestInput(INPUT3, "3.1");
    TestInput localTestInput6 = new TestInput(INPUT3, "3.2");
    TestInput localTestInput7 = new TestInput(INPUT3, "3.3");
    TestInput localTestInput8 = new TestInput(INPUT4, "4.1");
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput2.getInput());
    localMyFSM.doIt(localTestInput8.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput4.getInput());
    localMyFSM.doIt(localTestInput5.getInput());
    localMyFSM.doIt(localTestInput7.getInput());
    localMyFSM.doIt(localTestInput8.getInput());
    localMyFSM.doIt(localTestInput8.getInput());
    localMyFSM.doIt(localTestInput8.getInput());
    localMyFSM.doIt(localTestInput4.getInput());
    localMyFSM.doIt(localTestInput6.getInput());
    localMyFSM.doIt(localTestInput8.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput2.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
    localMyFSM.doIt(localTestInput1.getInput());
  }
}
