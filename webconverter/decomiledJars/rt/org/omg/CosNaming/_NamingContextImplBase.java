package org.omg.CosNaming;

import java.util.Dictionary;
import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;

public abstract class _NamingContextImplBase
  extends DynamicImplementation
  implements NamingContext
{
  private static final String[] _type_ids = { "IDL:omg.org/CosNaming/NamingContext:1.0" };
  private static Dictionary _methods = new Hashtable();
  
  public _NamingContextImplBase() {}
  
  public String[] _ids()
  {
    return (String[])_type_ids.clone();
  }
  
  public void invoke(ServerRequest paramServerRequest)
  {
    NVList localNVList;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject5;
    Object localObject7;
    Object localObject6;
    Any localAny2;
    Object localObject4;
    switch (((Integer)_methods.get(paramServerRequest.op_name())).intValue())
    {
    case 0: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      localObject2 = _orb().create_any();
      ((Any)localObject2).type(ORB.init().get_primitive_tc(TCKind.tk_objref));
      localNVList.add_value("obj", (Any)localObject2, 1);
      paramServerRequest.params(localNVList);
      localObject3 = NameHelper.extract((Any)localObject1);
      localObject5 = ((Any)localObject2).extract_Object();
      try
      {
        bind((NameComponent[])localObject3, (org.omg.CORBA.Object)localObject5);
      }
      catch (NotFound localNotFound4)
      {
        localObject7 = _orb().create_any();
        NotFoundHelper.insert((Any)localObject7, localNotFound4);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (CannotProceed localCannotProceed4)
      {
        localObject7 = _orb().create_any();
        CannotProceedHelper.insert((Any)localObject7, localCannotProceed4);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (InvalidName localInvalidName4)
      {
        localObject7 = _orb().create_any();
        InvalidNameHelper.insert((Any)localObject7, localInvalidName4);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (AlreadyBound localAlreadyBound2)
      {
        localObject7 = _orb().create_any();
        AlreadyBoundHelper.insert((Any)localObject7, localAlreadyBound2);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      Any localAny4 = _orb().create_any();
      localAny4.type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result(localAny4);
      break;
    case 1: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      localObject2 = _orb().create_any();
      ((Any)localObject2).type(NamingContextHelper.type());
      localNVList.add_value("nc", (Any)localObject2, 1);
      paramServerRequest.params(localNVList);
      localObject3 = NameHelper.extract((Any)localObject1);
      localObject5 = NamingContextHelper.extract((Any)localObject2);
      try
      {
        bind_context((NameComponent[])localObject3, (NamingContext)localObject5);
      }
      catch (NotFound localNotFound5)
      {
        localObject7 = _orb().create_any();
        NotFoundHelper.insert((Any)localObject7, localNotFound5);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (CannotProceed localCannotProceed5)
      {
        localObject7 = _orb().create_any();
        CannotProceedHelper.insert((Any)localObject7, localCannotProceed5);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (InvalidName localInvalidName5)
      {
        localObject7 = _orb().create_any();
        InvalidNameHelper.insert((Any)localObject7, localInvalidName5);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (AlreadyBound localAlreadyBound3)
      {
        localObject7 = _orb().create_any();
        AlreadyBoundHelper.insert((Any)localObject7, localAlreadyBound3);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      Any localAny5 = _orb().create_any();
      localAny5.type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result(localAny5);
      break;
    case 2: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      localObject2 = _orb().create_any();
      ((Any)localObject2).type(ORB.init().get_primitive_tc(TCKind.tk_objref));
      localNVList.add_value("obj", (Any)localObject2, 1);
      paramServerRequest.params(localNVList);
      localObject3 = NameHelper.extract((Any)localObject1);
      localObject5 = ((Any)localObject2).extract_Object();
      try
      {
        rebind((NameComponent[])localObject3, (org.omg.CORBA.Object)localObject5);
      }
      catch (NotFound localNotFound6)
      {
        localObject7 = _orb().create_any();
        NotFoundHelper.insert((Any)localObject7, localNotFound6);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (CannotProceed localCannotProceed6)
      {
        localObject7 = _orb().create_any();
        CannotProceedHelper.insert((Any)localObject7, localCannotProceed6);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (InvalidName localInvalidName6)
      {
        localObject7 = _orb().create_any();
        InvalidNameHelper.insert((Any)localObject7, localInvalidName6);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      Any localAny6 = _orb().create_any();
      localAny6.type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result(localAny6);
      break;
    case 3: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      localObject2 = _orb().create_any();
      ((Any)localObject2).type(NamingContextHelper.type());
      localNVList.add_value("nc", (Any)localObject2, 1);
      paramServerRequest.params(localNVList);
      localObject3 = NameHelper.extract((Any)localObject1);
      localObject5 = NamingContextHelper.extract((Any)localObject2);
      try
      {
        rebind_context((NameComponent[])localObject3, (NamingContext)localObject5);
      }
      catch (NotFound localNotFound7)
      {
        localObject7 = _orb().create_any();
        NotFoundHelper.insert((Any)localObject7, localNotFound7);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (CannotProceed localCannotProceed7)
      {
        localObject7 = _orb().create_any();
        CannotProceedHelper.insert((Any)localObject7, localCannotProceed7);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      catch (InvalidName localInvalidName7)
      {
        localObject7 = _orb().create_any();
        InvalidNameHelper.insert((Any)localObject7, localInvalidName7);
        paramServerRequest.except((Any)localObject7);
        return;
      }
      localObject6 = _orb().create_any();
      ((Any)localObject6).type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result((Any)localObject6);
      break;
    case 4: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      paramServerRequest.params(localNVList);
      localObject2 = NameHelper.extract((Any)localObject1);
      try
      {
        localObject3 = resolve((NameComponent[])localObject2);
      }
      catch (NotFound localNotFound2)
      {
        localObject6 = _orb().create_any();
        NotFoundHelper.insert((Any)localObject6, localNotFound2);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      catch (CannotProceed localCannotProceed2)
      {
        localObject6 = _orb().create_any();
        CannotProceedHelper.insert((Any)localObject6, localCannotProceed2);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      catch (InvalidName localInvalidName2)
      {
        localObject6 = _orb().create_any();
        InvalidNameHelper.insert((Any)localObject6, localInvalidName2);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      localAny2 = _orb().create_any();
      localAny2.insert_Object((org.omg.CORBA.Object)localObject3);
      paramServerRequest.result(localAny2);
      break;
    case 5: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      paramServerRequest.params(localNVList);
      localObject2 = NameHelper.extract((Any)localObject1);
      try
      {
        unbind((NameComponent[])localObject2);
      }
      catch (NotFound localNotFound1)
      {
        localAny2 = _orb().create_any();
        NotFoundHelper.insert(localAny2, localNotFound1);
        paramServerRequest.except(localAny2);
        return;
      }
      catch (CannotProceed localCannotProceed1)
      {
        localAny2 = _orb().create_any();
        CannotProceedHelper.insert(localAny2, localCannotProceed1);
        paramServerRequest.except(localAny2);
        return;
      }
      catch (InvalidName localInvalidName1)
      {
        localAny2 = _orb().create_any();
        InvalidNameHelper.insert(localAny2, localInvalidName1);
        paramServerRequest.except(localAny2);
        return;
      }
      localObject4 = _orb().create_any();
      ((Any)localObject4).type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result((Any)localObject4);
      break;
    case 6: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
      localNVList.add_value("how_many", (Any)localObject1, 1);
      localObject2 = _orb().create_any();
      ((Any)localObject2).type(BindingListHelper.type());
      localNVList.add_value("bl", (Any)localObject2, 2);
      localObject4 = _orb().create_any();
      ((Any)localObject4).type(BindingIteratorHelper.type());
      localNVList.add_value("bi", (Any)localObject4, 2);
      paramServerRequest.params(localNVList);
      int i = ((Any)localObject1).extract_ulong();
      localObject6 = new BindingListHolder();
      localObject7 = new BindingIteratorHolder();
      list(i, (BindingListHolder)localObject6, (BindingIteratorHolder)localObject7);
      BindingListHelper.insert((Any)localObject2, ((BindingListHolder)localObject6).value);
      BindingIteratorHelper.insert((Any)localObject4, ((BindingIteratorHolder)localObject7).value);
      Any localAny7 = _orb().create_any();
      localAny7.type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result(localAny7);
      break;
    case 7: 
      localNVList = _orb().create_list(0);
      paramServerRequest.params(localNVList);
      localObject1 = new_context();
      localObject2 = _orb().create_any();
      NamingContextHelper.insert((Any)localObject2, (NamingContext)localObject1);
      paramServerRequest.result((Any)localObject2);
      break;
    case 8: 
      localNVList = _orb().create_list(0);
      localObject1 = _orb().create_any();
      ((Any)localObject1).type(NameHelper.type());
      localNVList.add_value("n", (Any)localObject1, 1);
      paramServerRequest.params(localNVList);
      localObject2 = NameHelper.extract((Any)localObject1);
      try
      {
        localObject4 = bind_new_context((NameComponent[])localObject2);
      }
      catch (NotFound localNotFound3)
      {
        localObject6 = _orb().create_any();
        NotFoundHelper.insert((Any)localObject6, localNotFound3);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      catch (AlreadyBound localAlreadyBound1)
      {
        localObject6 = _orb().create_any();
        AlreadyBoundHelper.insert((Any)localObject6, localAlreadyBound1);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      catch (CannotProceed localCannotProceed3)
      {
        localObject6 = _orb().create_any();
        CannotProceedHelper.insert((Any)localObject6, localCannotProceed3);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      catch (InvalidName localInvalidName3)
      {
        localObject6 = _orb().create_any();
        InvalidNameHelper.insert((Any)localObject6, localInvalidName3);
        paramServerRequest.except((Any)localObject6);
        return;
      }
      Any localAny3 = _orb().create_any();
      NamingContextHelper.insert(localAny3, (NamingContext)localObject4);
      paramServerRequest.result(localAny3);
      break;
    case 9: 
      localNVList = _orb().create_list(0);
      paramServerRequest.params(localNVList);
      try
      {
        destroy();
      }
      catch (NotEmpty localNotEmpty)
      {
        localObject2 = _orb().create_any();
        NotEmptyHelper.insert((Any)localObject2, localNotEmpty);
        paramServerRequest.except((Any)localObject2);
        return;
      }
      Any localAny1 = _orb().create_any();
      localAny1.type(_orb().get_primitive_tc(TCKind.tk_void));
      paramServerRequest.result(localAny1);
      break;
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
  }
  
  static
  {
    _methods.put("bind", new Integer(0));
    _methods.put("bind_context", new Integer(1));
    _methods.put("rebind", new Integer(2));
    _methods.put("rebind_context", new Integer(3));
    _methods.put("resolve", new Integer(4));
    _methods.put("unbind", new Integer(5));
    _methods.put("list", new Integer(6));
    _methods.put("new_context", new Integer(7));
    _methods.put("bind_new_context", new Integer(8));
    _methods.put("destroy", new Integer(9));
  }
}
