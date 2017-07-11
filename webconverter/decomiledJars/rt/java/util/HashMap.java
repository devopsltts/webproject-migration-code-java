package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class HashMap<K, V>
  extends AbstractMap<K, V>
  implements Map<K, V>, Cloneable, Serializable
{
  private static final long serialVersionUID = 362498820763181265L;
  static final int DEFAULT_INITIAL_CAPACITY = 16;
  static final int MAXIMUM_CAPACITY = 1073741824;
  static final float DEFAULT_LOAD_FACTOR = 0.75F;
  static final int TREEIFY_THRESHOLD = 8;
  static final int UNTREEIFY_THRESHOLD = 6;
  static final int MIN_TREEIFY_CAPACITY = 64;
  transient Node<K, V>[] table;
  transient Set<Map.Entry<K, V>> entrySet;
  transient int size;
  transient int modCount;
  int threshold;
  final float loadFactor;
  
  static final int hash(Object paramObject)
  {
    int i;
    return paramObject == null ? 0 : (i = paramObject.hashCode()) ^ i >>> 16;
  }
  
  static Class<?> comparableClassFor(Object paramObject)
  {
    if ((paramObject instanceof Comparable))
    {
      Class localClass;
      if ((localClass = paramObject.getClass()) == String.class) {
        return localClass;
      }
      Type[] arrayOfType1;
      if ((arrayOfType1 = localClass.getGenericInterfaces()) != null) {
        for (int i = 0; i < arrayOfType1.length; i++)
        {
          Type localType;
          ParameterizedType localParameterizedType;
          Type[] arrayOfType2;
          if ((((localType = arrayOfType1[i]) instanceof ParameterizedType)) && ((localParameterizedType = (ParameterizedType)localType).getRawType() == Comparable.class) && ((arrayOfType2 = localParameterizedType.getActualTypeArguments()) != null) && (arrayOfType2.length == 1) && (arrayOfType2[0] == localClass)) {
            return localClass;
          }
        }
      }
    }
    return null;
  }
  
  static int compareComparables(Class<?> paramClass, Object paramObject1, Object paramObject2)
  {
    return (paramObject2 == null) || (paramObject2.getClass() != paramClass) ? 0 : ((Comparable)paramObject1).compareTo(paramObject2);
  }
  
  static final int tableSizeFor(int paramInt)
  {
    int i = paramInt - 1;
    i |= i >>> 1;
    i |= i >>> 2;
    i |= i >>> 4;
    i |= i >>> 8;
    i |= i >>> 16;
    return i >= 1073741824 ? 1073741824 : i < 0 ? 1 : i + 1;
  }
  
  public HashMap(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal initial capacity: " + paramInt);
    }
    if (paramInt > 1073741824) {
      paramInt = 1073741824;
    }
    if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
      throw new IllegalArgumentException("Illegal load factor: " + paramFloat);
    }
    this.loadFactor = paramFloat;
    this.threshold = tableSizeFor(paramInt);
  }
  
  public HashMap(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public HashMap()
  {
    this.loadFactor = 0.75F;
  }
  
  public HashMap(Map<? extends K, ? extends V> paramMap)
  {
    this.loadFactor = 0.75F;
    putMapEntries(paramMap, false);
  }
  
  final void putMapEntries(Map<? extends K, ? extends V> paramMap, boolean paramBoolean)
  {
    int i = paramMap.size();
    if (i > 0)
    {
      if (this.table == null)
      {
        float f = i / this.loadFactor + 1.0F;
        int j = f < 1.07374182E9F ? (int)f : 1073741824;
        if (j > this.threshold) {
          this.threshold = tableSizeFor(j);
        }
      }
      else if (i > this.threshold)
      {
        resize();
      }
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        putVal(hash(localObject1), localObject1, localObject2, false, paramBoolean);
      }
    }
  }
  
  public int size()
  {
    return this.size;
  }
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
  
  public V get(Object paramObject)
  {
    Node localNode;
    return (localNode = getNode(hash(paramObject), paramObject)) == null ? null : localNode.value;
  }
  
  final Node<K, V> getNode(int paramInt, Object paramObject)
  {
    Node[] arrayOfNode;
    int i;
    Node localNode1;
    if (((arrayOfNode = this.table) != null) && ((i = arrayOfNode.length) > 0) && ((localNode1 = arrayOfNode[(i - 1 & paramInt)]) != null))
    {
      Object localObject;
      if ((localNode1.hash == paramInt) && (((localObject = localNode1.key) == paramObject) || ((paramObject != null) && (paramObject.equals(localObject))))) {
        return localNode1;
      }
      Node localNode2;
      if ((localNode2 = localNode1.next) != null)
      {
        if ((localNode1 instanceof TreeNode)) {
          return ((TreeNode)localNode1).getTreeNode(paramInt, paramObject);
        }
        do
        {
          if ((localNode2.hash == paramInt) && (((localObject = localNode2.key) == paramObject) || ((paramObject != null) && (paramObject.equals(localObject))))) {
            return localNode2;
          }
        } while ((localNode2 = localNode2.next) != null);
      }
    }
    return null;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return getNode(hash(paramObject), paramObject) != null;
  }
  
  public V put(K paramK, V paramV)
  {
    return putVal(hash(paramK), paramK, paramV, false, true);
  }
  
  final V putVal(int paramInt, K paramK, V paramV, boolean paramBoolean1, boolean paramBoolean2)
  {
    Node[] arrayOfNode;
    int i;
    if (((arrayOfNode = this.table) == null) || ((i = arrayOfNode.length) == 0)) {
      i = (arrayOfNode = resize()).length;
    }
    int j;
    Object localObject1;
    if ((localObject1 = arrayOfNode[(j = i - 1 & paramInt)]) == null)
    {
      arrayOfNode[j] = newNode(paramInt, paramK, paramV, null);
    }
    else
    {
      Object localObject3;
      Object localObject2;
      if ((((Node)localObject1).hash == paramInt) && (((localObject3 = ((Node)localObject1).key) == paramK) || ((paramK != null) && (paramK.equals(localObject3))))) {
        localObject2 = localObject1;
      } else if ((localObject1 instanceof TreeNode)) {
        localObject2 = ((TreeNode)localObject1).putTreeVal(this, arrayOfNode, paramInt, paramK, paramV);
      } else {
        for (int k = 0;; k++)
        {
          if ((localObject2 = ((Node)localObject1).next) == null)
          {
            ((Node)localObject1).next = newNode(paramInt, paramK, paramV, null);
            if (k < 7) {
              break;
            }
            treeifyBin(arrayOfNode, paramInt);
            break;
          }
          if ((((Node)localObject2).hash == paramInt) && (((localObject3 = ((Node)localObject2).key) == paramK) || ((paramK != null) && (paramK.equals(localObject3))))) {
            break;
          }
          localObject1 = localObject2;
        }
      }
      if (localObject2 != null)
      {
        Object localObject4 = ((Node)localObject2).value;
        if ((!paramBoolean1) || (localObject4 == null)) {
          ((Node)localObject2).value = paramV;
        }
        afterNodeAccess((Node)localObject2);
        return localObject4;
      }
    }
    this.modCount += 1;
    if (++this.size > this.threshold) {
      resize();
    }
    afterNodeInsertion(paramBoolean2);
    return null;
  }
  
  final Node<K, V>[] resize()
  {
    Node[] arrayOfNode1 = this.table;
    int i = arrayOfNode1 == null ? 0 : arrayOfNode1.length;
    int j = this.threshold;
    int m = 0;
    int k;
    if (i > 0)
    {
      if (i >= 1073741824)
      {
        this.threshold = Integer.MAX_VALUE;
        return arrayOfNode1;
      }
      if (((k = i << 1) < 1073741824) && (i >= 16)) {
        m = j << 1;
      }
    }
    else if (j > 0)
    {
      k = j;
    }
    else
    {
      k = 16;
      m = 12;
    }
    if (m == 0)
    {
      float f = k * this.loadFactor;
      m = (k < 1073741824) && (f < 1.07374182E9F) ? (int)f : Integer.MAX_VALUE;
    }
    this.threshold = m;
    Node[] arrayOfNode2 = (Node[])new Node[k];
    this.table = arrayOfNode2;
    if (arrayOfNode1 != null) {
      for (int n = 0; n < i; n++)
      {
        Object localObject1;
        if ((localObject1 = arrayOfNode1[n]) != null)
        {
          arrayOfNode1[n] = null;
          if (((Node)localObject1).next == null)
          {
            arrayOfNode2[(localObject1.hash & k - 1)] = localObject1;
          }
          else if ((localObject1 instanceof TreeNode))
          {
            ((TreeNode)localObject1).split(this, arrayOfNode2, n, i);
          }
          else
          {
            Object localObject2 = null;
            Object localObject3 = null;
            Object localObject4 = null;
            Object localObject5 = null;
            Node localNode;
            do
            {
              localNode = ((Node)localObject1).next;
              if ((((Node)localObject1).hash & i) == 0)
              {
                if (localObject3 == null) {
                  localObject2 = localObject1;
                } else {
                  localObject3.next = ((Node)localObject1);
                }
                localObject3 = localObject1;
              }
              else
              {
                if (localObject5 == null) {
                  localObject4 = localObject1;
                } else {
                  localObject5.next = ((Node)localObject1);
                }
                localObject5 = localObject1;
              }
            } while ((localObject1 = localNode) != null);
            if (localObject3 != null)
            {
              localObject3.next = null;
              arrayOfNode2[n] = localObject2;
            }
            if (localObject5 != null)
            {
              localObject5.next = null;
              arrayOfNode2[(n + i)] = localObject4;
            }
          }
        }
      }
    }
    return arrayOfNode2;
  }
  
  final void treeifyBin(Node<K, V>[] paramArrayOfNode, int paramInt)
  {
    int i;
    if ((paramArrayOfNode == null) || ((i = paramArrayOfNode.length) < 64))
    {
      resize();
    }
    else
    {
      int j;
      Object localObject1;
      if ((localObject1 = paramArrayOfNode[(j = i - 1 & paramInt)]) != null)
      {
        Object localObject2 = null;
        Object localObject3 = null;
        do
        {
          TreeNode localTreeNode = replacementTreeNode((Node)localObject1, null);
          if (localObject3 == null)
          {
            localObject2 = localTreeNode;
          }
          else
          {
            localTreeNode.prev = localObject3;
            localObject3.next = localTreeNode;
          }
          localObject3 = localTreeNode;
        } while ((localObject1 = ((Node)localObject1).next) != null);
        if ((paramArrayOfNode[j] =  = localObject2) != null) {
          localObject2.treeify(paramArrayOfNode);
        }
      }
    }
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap)
  {
    putMapEntries(paramMap, true);
  }
  
  public V remove(Object paramObject)
  {
    Node localNode;
    return (localNode = removeNode(hash(paramObject), paramObject, null, false, true)) == null ? null : localNode.value;
  }
  
  final Node<K, V> removeNode(int paramInt, Object paramObject1, Object paramObject2, boolean paramBoolean1, boolean paramBoolean2)
  {
    Node[] arrayOfNode;
    int i;
    int j;
    Object localObject1;
    if (((arrayOfNode = this.table) != null) && ((i = arrayOfNode.length) > 0) && ((localObject1 = arrayOfNode[(j = i - 1 & paramInt)]) != null))
    {
      Object localObject2 = null;
      Object localObject3;
      if ((((Node)localObject1).hash == paramInt) && (((localObject3 = ((Node)localObject1).key) == paramObject1) || ((paramObject1 != null) && (paramObject1.equals(localObject3)))))
      {
        localObject2 = localObject1;
      }
      else
      {
        Node localNode;
        if ((localNode = ((Node)localObject1).next) != null) {
          if ((localObject1 instanceof TreeNode)) {
            localObject2 = ((TreeNode)localObject1).getTreeNode(paramInt, paramObject1);
          } else {
            do
            {
              if ((localNode.hash == paramInt) && (((localObject3 = localNode.key) == paramObject1) || ((paramObject1 != null) && (paramObject1.equals(localObject3)))))
              {
                localObject2 = localNode;
                break;
              }
              localObject1 = localNode;
            } while ((localNode = localNode.next) != null);
          }
        }
      }
      Object localObject4;
      if ((localObject2 != null) && ((!paramBoolean1) || ((localObject4 = ((Node)localObject2).value) == paramObject2) || ((paramObject2 != null) && (paramObject2.equals(localObject4)))))
      {
        if ((localObject2 instanceof TreeNode)) {
          ((TreeNode)localObject2).removeTreeNode(this, arrayOfNode, paramBoolean2);
        } else if (localObject2 == localObject1) {
          arrayOfNode[j] = ((Node)localObject2).next;
        } else {
          ((Node)localObject1).next = ((Node)localObject2).next;
        }
        this.modCount += 1;
        this.size -= 1;
        afterNodeRemoval((Node)localObject2);
        return localObject2;
      }
    }
    return null;
  }
  
  public void clear()
  {
    this.modCount += 1;
    Node[] arrayOfNode;
    if (((arrayOfNode = this.table) != null) && (this.size > 0))
    {
      this.size = 0;
      for (int i = 0; i < arrayOfNode.length; i++) {
        arrayOfNode[i] = null;
      }
    }
  }
  
  public boolean containsValue(Object paramObject)
  {
    Node[] arrayOfNode;
    if (((arrayOfNode = this.table) != null) && (this.size > 0)) {
      for (int i = 0; i < arrayOfNode.length; i++) {
        for (Node localNode = arrayOfNode[i]; localNode != null; localNode = localNode.next)
        {
          Object localObject;
          if (((localObject = localNode.value) == paramObject) || ((paramObject != null) && (paramObject.equals(localObject)))) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public Set<K> keySet()
  {
    Set localSet;
    return (localSet = this.keySet) == null ? (this.keySet = new KeySet()) : localSet;
  }
  
  public Collection<V> values()
  {
    Collection localCollection;
    return (localCollection = this.values) == null ? (this.values = new Values()) : localCollection;
  }
  
  public Set<Map.Entry<K, V>> entrySet()
  {
    Set localSet;
    return (localSet = this.entrySet) == null ? (this.entrySet = new EntrySet()) : localSet;
  }
  
  public V getOrDefault(Object paramObject, V paramV)
  {
    Node localNode;
    return (localNode = getNode(hash(paramObject), paramObject)) == null ? paramV : localNode.value;
  }
  
  public V putIfAbsent(K paramK, V paramV)
  {
    return putVal(hash(paramK), paramK, paramV, true, true);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    return removeNode(hash(paramObject1), paramObject1, paramObject2, true, true) != null;
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2)
  {
    Node localNode;
    Object localObject;
    if (((localNode = getNode(hash(paramK), paramK)) != null) && (((localObject = localNode.value) == paramV1) || ((localObject != null) && (localObject.equals(paramV1)))))
    {
      localNode.value = paramV2;
      afterNodeAccess(localNode);
      return true;
    }
    return false;
  }
  
  public V replace(K paramK, V paramV)
  {
    Node localNode;
    if ((localNode = getNode(hash(paramK), paramK)) != null)
    {
      Object localObject = localNode.value;
      localNode.value = paramV;
      afterNodeAccess(localNode);
      return localObject;
    }
    return null;
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction)
  {
    if (paramFunction == null) {
      throw new NullPointerException();
    }
    int i = hash(paramK);
    int m = 0;
    TreeNode localTreeNode = null;
    Object localObject1 = null;
    Node[] arrayOfNode;
    int j;
    if ((this.size > this.threshold) || ((arrayOfNode = this.table) == null) || ((j = arrayOfNode.length) == 0)) {
      j = (arrayOfNode = resize()).length;
    }
    int k;
    Node localNode;
    if ((localNode = arrayOfNode[(k = j - 1 & i)]) != null)
    {
      if ((localNode instanceof TreeNode))
      {
        localObject1 = (localTreeNode = (TreeNode)localNode).getTreeNode(i, paramK);
      }
      else
      {
        localObject2 = localNode;
        do
        {
          Object localObject3;
          if ((((Node)localObject2).hash == i) && (((localObject3 = ((Node)localObject2).key) == paramK) || ((paramK != null) && (paramK.equals(localObject3)))))
          {
            localObject1 = localObject2;
            break;
          }
          m++;
        } while ((localObject2 = ((Node)localObject2).next) != null);
      }
      if ((localObject1 != null) && ((localObject2 = ((Node)localObject1).value) != null))
      {
        afterNodeAccess((Node)localObject1);
        return localObject2;
      }
    }
    Object localObject2 = paramFunction.apply(paramK);
    if (localObject2 == null) {
      return null;
    }
    if (localObject1 != null)
    {
      ((Node)localObject1).value = localObject2;
      afterNodeAccess((Node)localObject1);
      return localObject2;
    }
    if (localTreeNode != null)
    {
      localTreeNode.putTreeVal(this, arrayOfNode, i, paramK, localObject2);
    }
    else
    {
      arrayOfNode[k] = newNode(i, paramK, localObject2, localNode);
      if (m >= 7) {
        treeifyBin(arrayOfNode, i);
      }
    }
    this.modCount += 1;
    this.size += 1;
    afterNodeInsertion(true);
    return localObject2;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    int i = hash(paramK);
    Node localNode;
    Object localObject1;
    if (((localNode = getNode(i, paramK)) != null) && ((localObject1 = localNode.value) != null))
    {
      Object localObject2 = paramBiFunction.apply(paramK, localObject1);
      if (localObject2 != null)
      {
        localNode.value = localObject2;
        afterNodeAccess(localNode);
        return localObject2;
      }
      removeNode(i, paramK, null, false, true);
    }
    return null;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    int i = hash(paramK);
    int m = 0;
    TreeNode localTreeNode = null;
    Object localObject1 = null;
    Node[] arrayOfNode;
    int j;
    if ((this.size > this.threshold) || ((arrayOfNode = this.table) == null) || ((j = arrayOfNode.length) == 0)) {
      j = (arrayOfNode = resize()).length;
    }
    int k;
    Node localNode;
    if ((localNode = arrayOfNode[(k = j - 1 & i)]) != null) {
      if ((localNode instanceof TreeNode))
      {
        localObject1 = (localTreeNode = (TreeNode)localNode).getTreeNode(i, paramK);
      }
      else
      {
        localObject2 = localNode;
        do
        {
          if ((((Node)localObject2).hash == i) && (((localObject3 = ((Node)localObject2).key) == paramK) || ((paramK != null) && (paramK.equals(localObject3)))))
          {
            localObject1 = localObject2;
            break;
          }
          m++;
        } while ((localObject2 = ((Node)localObject2).next) != null);
      }
    }
    Object localObject2 = localObject1 == null ? null : ((Node)localObject1).value;
    Object localObject3 = paramBiFunction.apply(paramK, localObject2);
    if (localObject1 != null)
    {
      if (localObject3 != null)
      {
        ((Node)localObject1).value = localObject3;
        afterNodeAccess((Node)localObject1);
      }
      else
      {
        removeNode(i, paramK, null, false, true);
      }
    }
    else if (localObject3 != null)
    {
      if (localTreeNode != null)
      {
        localTreeNode.putTreeVal(this, arrayOfNode, i, paramK, localObject3);
      }
      else
      {
        arrayOfNode[k] = newNode(i, paramK, localObject3, localNode);
        if (m >= 7) {
          treeifyBin(arrayOfNode, i);
        }
      }
      this.modCount += 1;
      this.size += 1;
      afterNodeInsertion(true);
    }
    return localObject3;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction)
  {
    if (paramV == null) {
      throw new NullPointerException();
    }
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    int i = hash(paramK);
    int m = 0;
    TreeNode localTreeNode = null;
    Object localObject1 = null;
    Node[] arrayOfNode;
    int j;
    if ((this.size > this.threshold) || ((arrayOfNode = this.table) == null) || ((j = arrayOfNode.length) == 0)) {
      j = (arrayOfNode = resize()).length;
    }
    int k;
    Node localNode;
    Object localObject2;
    if ((localNode = arrayOfNode[(k = j - 1 & i)]) != null) {
      if ((localNode instanceof TreeNode))
      {
        localObject1 = (localTreeNode = (TreeNode)localNode).getTreeNode(i, paramK);
      }
      else
      {
        localObject2 = localNode;
        do
        {
          Object localObject3;
          if ((((Node)localObject2).hash == i) && (((localObject3 = ((Node)localObject2).key) == paramK) || ((paramK != null) && (paramK.equals(localObject3)))))
          {
            localObject1 = localObject2;
            break;
          }
          m++;
        } while ((localObject2 = ((Node)localObject2).next) != null);
      }
    }
    if (localObject1 != null)
    {
      if (((Node)localObject1).value != null) {
        localObject2 = paramBiFunction.apply(((Node)localObject1).value, paramV);
      } else {
        localObject2 = paramV;
      }
      if (localObject2 != null)
      {
        ((Node)localObject1).value = localObject2;
        afterNodeAccess((Node)localObject1);
      }
      else
      {
        removeNode(i, paramK, null, false, true);
      }
      return localObject2;
    }
    if (paramV != null)
    {
      if (localTreeNode != null)
      {
        localTreeNode.putTreeVal(this, arrayOfNode, i, paramK, paramV);
      }
      else
      {
        arrayOfNode[k] = newNode(i, paramK, paramV, localNode);
        if (m >= 7) {
          treeifyBin(arrayOfNode, i);
        }
      }
      this.modCount += 1;
      this.size += 1;
      afterNodeInsertion(true);
    }
    return paramV;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer)
  {
    if (paramBiConsumer == null) {
      throw new NullPointerException();
    }
    Node[] arrayOfNode;
    if ((this.size > 0) && ((arrayOfNode = this.table) != null))
    {
      int i = this.modCount;
      for (int j = 0; j < arrayOfNode.length; j++) {
        for (Node localNode = arrayOfNode[j]; localNode != null; localNode = localNode.next) {
          paramBiConsumer.accept(localNode.key, localNode.value);
        }
      }
      if (this.modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction)
  {
    if (paramBiFunction == null) {
      throw new NullPointerException();
    }
    Node[] arrayOfNode;
    if ((this.size > 0) && ((arrayOfNode = this.table) != null))
    {
      int i = this.modCount;
      for (int j = 0; j < arrayOfNode.length; j++) {
        for (Node localNode = arrayOfNode[j]; localNode != null; localNode = localNode.next) {
          localNode.value = paramBiFunction.apply(localNode.key, localNode.value);
        }
      }
      if (this.modCount != i) {
        throw new ConcurrentModificationException();
      }
    }
  }
  
  public Object clone()
  {
    HashMap localHashMap;
    try
    {
      localHashMap = (HashMap)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
    localHashMap.reinitialize();
    localHashMap.putMapEntries(this, false);
    return localHashMap;
  }
  
  final float loadFactor()
  {
    return this.loadFactor;
  }
  
  final int capacity()
  {
    return this.threshold > 0 ? this.threshold : this.table != null ? this.table.length : 16;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    int i = capacity();
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(this.size);
    internalWriteEntries(paramObjectOutputStream);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    reinitialize();
    if ((this.loadFactor <= 0.0F) || (Float.isNaN(this.loadFactor))) {
      throw new InvalidObjectException("Illegal load factor: " + this.loadFactor);
    }
    paramObjectInputStream.readInt();
    int i = paramObjectInputStream.readInt();
    if (i < 0) {
      throw new InvalidObjectException("Illegal mappings count: " + i);
    }
    if (i > 0)
    {
      float f1 = Math.min(Math.max(0.25F, this.loadFactor), 4.0F);
      float f2 = i / f1 + 1.0F;
      int j = f2 >= 1.07374182E9F ? 1073741824 : f2 < 16.0F ? 16 : tableSizeFor((int)f2);
      float f3 = j * f1;
      this.threshold = ((j < 1073741824) && (f3 < 1.07374182E9F) ? (int)f3 : Integer.MAX_VALUE);
      Node[] arrayOfNode = (Node[])new Node[j];
      this.table = arrayOfNode;
      for (int k = 0; k < i; k++)
      {
        Object localObject1 = paramObjectInputStream.readObject();
        Object localObject2 = paramObjectInputStream.readObject();
        putVal(hash(localObject1), localObject1, localObject2, false, false);
      }
    }
  }
  
  Node<K, V> newNode(int paramInt, K paramK, V paramV, Node<K, V> paramNode)
  {
    return new Node(paramInt, paramK, paramV, paramNode);
  }
  
  Node<K, V> replacementNode(Node<K, V> paramNode1, Node<K, V> paramNode2)
  {
    return new Node(paramNode1.hash, paramNode1.key, paramNode1.value, paramNode2);
  }
  
  TreeNode<K, V> newTreeNode(int paramInt, K paramK, V paramV, Node<K, V> paramNode)
  {
    return new TreeNode(paramInt, paramK, paramV, paramNode);
  }
  
  TreeNode<K, V> replacementTreeNode(Node<K, V> paramNode1, Node<K, V> paramNode2)
  {
    return new TreeNode(paramNode1.hash, paramNode1.key, paramNode1.value, paramNode2);
  }
  
  void reinitialize()
  {
    this.table = null;
    this.entrySet = null;
    this.keySet = null;
    this.values = null;
    this.modCount = 0;
    this.threshold = 0;
    this.size = 0;
  }
  
  void afterNodeAccess(Node<K, V> paramNode) {}
  
  void afterNodeInsertion(boolean paramBoolean) {}
  
  void afterNodeRemoval(Node<K, V> paramNode) {}
  
  void internalWriteEntries(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Node[] arrayOfNode;
    if ((this.size > 0) && ((arrayOfNode = this.table) != null)) {
      for (int i = 0; i < arrayOfNode.length; i++) {
        for (Node localNode = arrayOfNode[i]; localNode != null; localNode = localNode.next)
        {
          paramObjectOutputStream.writeObject(localNode.key);
          paramObjectOutputStream.writeObject(localNode.value);
        }
      }
    }
  }
  
  final class EntryIterator
    extends HashMap<K, V>.HashIterator
    implements Iterator<Map.Entry<K, V>>
  {
    EntryIterator()
    {
      super();
    }
    
    public final Map.Entry<K, V> next()
    {
      return nextNode();
    }
  }
  
  final class EntrySet
    extends AbstractSet<Map.Entry<K, V>>
  {
    EntrySet() {}
    
    public final int size()
    {
      return HashMap.this.size;
    }
    
    public final void clear()
    {
      HashMap.this.clear();
    }
    
    public final Iterator<Map.Entry<K, V>> iterator()
    {
      return new HashMap.EntryIterator(HashMap.this);
    }
    
    public final boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      Object localObject = localEntry.getKey();
      HashMap.Node localNode = HashMap.this.getNode(HashMap.hash(localObject), localObject);
      return (localNode != null) && (localNode.equals(localEntry));
    }
    
    public final boolean remove(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        Object localObject1 = localEntry.getKey();
        Object localObject2 = localEntry.getValue();
        return HashMap.this.removeNode(HashMap.hash(localObject1), localObject1, localObject2, true, true) != null;
      }
      return false;
    }
    
    public final Spliterator<Map.Entry<K, V>> spliterator()
    {
      return new HashMap.EntrySpliterator(HashMap.this, 0, -1, 0, 0);
    }
    
    public final void forEach(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap.Node[] arrayOfNode;
      if ((HashMap.this.size > 0) && ((arrayOfNode = HashMap.this.table) != null))
      {
        int i = HashMap.this.modCount;
        for (int j = 0; j < arrayOfNode.length; j++) {
          for (HashMap.Node localNode = arrayOfNode[j]; localNode != null; localNode = localNode.next) {
            paramConsumer.accept(localNode);
          }
        }
        if (HashMap.this.modCount != i) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
  
  static final class EntrySpliterator<K, V>
    extends HashMap.HashMapSpliterator<K, V>
    implements Spliterator<Map.Entry<K, V>>
  {
    EntrySpliterator(HashMap<K, V> paramHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public EntrySpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) || (this.current != null) ? null : new EntrySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap localHashMap = this.map;
      HashMap.Node[] arrayOfNode = localHashMap.table;
      int j;
      int k;
      if ((j = this.fence) < 0)
      {
        k = this.expectedModCount = localHashMap.modCount;
        j = this.fence = arrayOfNode == null ? 0 : arrayOfNode.length;
      }
      else
      {
        k = this.expectedModCount;
      }
      int i;
      if ((arrayOfNode != null) && (arrayOfNode.length >= j) && ((i = this.index) >= 0) && ((i < (this.index = j)) || (this.current != null)))
      {
        HashMap.Node localNode = this.current;
        this.current = null;
        do
        {
          if (localNode == null)
          {
            localNode = arrayOfNode[(i++)];
          }
          else
          {
            paramConsumer.accept(localNode);
            localNode = localNode.next;
          }
        } while ((localNode != null) || (i < j));
        if (localHashMap.modCount != k) {
          throw new ConcurrentModificationException();
        }
      }
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap.Node[] arrayOfNode = this.map.table;
      int i;
      if ((arrayOfNode != null) && (arrayOfNode.length >= (i = getFence())) && (this.index >= 0)) {
        while ((this.current != null) || (this.index < i)) {
          if (this.current == null)
          {
            this.current = arrayOfNode[(this.index++)];
          }
          else
          {
            HashMap.Node localNode = this.current;
            this.current = this.current.next;
            paramConsumer.accept(localNode);
            if (this.map.modCount != this.expectedModCount) {
              throw new ConcurrentModificationException();
            }
            return true;
          }
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return ((this.fence < 0) || (this.est == this.map.size) ? 64 : 0) | 0x1;
    }
  }
  
  abstract class HashIterator
  {
    HashMap.Node<K, V> next;
    HashMap.Node<K, V> current;
    int expectedModCount = HashMap.this.modCount;
    int index;
    
    HashIterator()
    {
      HashMap.Node[] arrayOfNode = HashMap.this.table;
      this.current = (this.next = null);
      this.index = 0;
      while ((arrayOfNode != null) && (HashMap.this.size > 0) && (this.index < arrayOfNode.length) && ((this.next = arrayOfNode[(this.index++)]) == null)) {}
    }
    
    public final boolean hasNext()
    {
      return this.next != null;
    }
    
    final HashMap.Node<K, V> nextNode()
    {
      HashMap.Node localNode = this.next;
      if (HashMap.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (localNode == null) {
        throw new NoSuchElementException();
      }
      HashMap.Node[] arrayOfNode;
      while (((this.next = (this.current = localNode).next) == null) && ((arrayOfNode = HashMap.this.table) != null) && (this.index < arrayOfNode.length) && ((this.next = arrayOfNode[(this.index++)]) == null)) {}
      return localNode;
    }
    
    public final void remove()
    {
      HashMap.Node localNode = this.current;
      if (localNode == null) {
        throw new IllegalStateException();
      }
      if (HashMap.this.modCount != this.expectedModCount) {
        throw new ConcurrentModificationException();
      }
      this.current = null;
      Object localObject = localNode.key;
      HashMap.this.removeNode(HashMap.hash(localObject), localObject, null, false, false);
      this.expectedModCount = HashMap.this.modCount;
    }
  }
  
  static class HashMapSpliterator<K, V>
  {
    final HashMap<K, V> map;
    HashMap.Node<K, V> current;
    int index;
    int fence;
    int est;
    int expectedModCount;
    
    HashMapSpliterator(HashMap<K, V> paramHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.map = paramHashMap;
      this.index = paramInt1;
      this.fence = paramInt2;
      this.est = paramInt3;
      this.expectedModCount = paramInt4;
    }
    
    final int getFence()
    {
      int i;
      if ((i = this.fence) < 0)
      {
        HashMap localHashMap = this.map;
        this.est = localHashMap.size;
        this.expectedModCount = localHashMap.modCount;
        HashMap.Node[] arrayOfNode = localHashMap.table;
        i = this.fence = arrayOfNode == null ? 0 : arrayOfNode.length;
      }
      return i;
    }
    
    public final long estimateSize()
    {
      getFence();
      return this.est;
    }
  }
  
  final class KeyIterator
    extends HashMap<K, V>.HashIterator
    implements Iterator<K>
  {
    KeyIterator()
    {
      super();
    }
    
    public final K next()
    {
      return nextNode().key;
    }
  }
  
  final class KeySet
    extends AbstractSet<K>
  {
    KeySet() {}
    
    public final int size()
    {
      return HashMap.this.size;
    }
    
    public final void clear()
    {
      HashMap.this.clear();
    }
    
    public final Iterator<K> iterator()
    {
      return new HashMap.KeyIterator(HashMap.this);
    }
    
    public final boolean contains(Object paramObject)
    {
      return HashMap.this.containsKey(paramObject);
    }
    
    public final boolean remove(Object paramObject)
    {
      return HashMap.this.removeNode(HashMap.hash(paramObject), paramObject, null, false, true) != null;
    }
    
    public final Spliterator<K> spliterator()
    {
      return new HashMap.KeySpliterator(HashMap.this, 0, -1, 0, 0);
    }
    
    public final void forEach(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap.Node[] arrayOfNode;
      if ((HashMap.this.size > 0) && ((arrayOfNode = HashMap.this.table) != null))
      {
        int i = HashMap.this.modCount;
        for (int j = 0; j < arrayOfNode.length; j++) {
          for (HashMap.Node localNode = arrayOfNode[j]; localNode != null; localNode = localNode.next) {
            paramConsumer.accept(localNode.key);
          }
        }
        if (HashMap.this.modCount != i) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
  
  static final class KeySpliterator<K, V>
    extends HashMap.HashMapSpliterator<K, V>
    implements Spliterator<K>
  {
    KeySpliterator(HashMap<K, V> paramHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public KeySpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) || (this.current != null) ? null : new KeySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap localHashMap = this.map;
      HashMap.Node[] arrayOfNode = localHashMap.table;
      int j;
      int k;
      if ((j = this.fence) < 0)
      {
        k = this.expectedModCount = localHashMap.modCount;
        j = this.fence = arrayOfNode == null ? 0 : arrayOfNode.length;
      }
      else
      {
        k = this.expectedModCount;
      }
      int i;
      if ((arrayOfNode != null) && (arrayOfNode.length >= j) && ((i = this.index) >= 0) && ((i < (this.index = j)) || (this.current != null)))
      {
        HashMap.Node localNode = this.current;
        this.current = null;
        do
        {
          if (localNode == null)
          {
            localNode = arrayOfNode[(i++)];
          }
          else
          {
            paramConsumer.accept(localNode.key);
            localNode = localNode.next;
          }
        } while ((localNode != null) || (i < j));
        if (localHashMap.modCount != k) {
          throw new ConcurrentModificationException();
        }
      }
    }
    
    public boolean tryAdvance(Consumer<? super K> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap.Node[] arrayOfNode = this.map.table;
      int i;
      if ((arrayOfNode != null) && (arrayOfNode.length >= (i = getFence())) && (this.index >= 0)) {
        while ((this.current != null) || (this.index < i)) {
          if (this.current == null)
          {
            this.current = arrayOfNode[(this.index++)];
          }
          else
          {
            Object localObject = this.current.key;
            this.current = this.current.next;
            paramConsumer.accept(localObject);
            if (this.map.modCount != this.expectedModCount) {
              throw new ConcurrentModificationException();
            }
            return true;
          }
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return ((this.fence < 0) || (this.est == this.map.size) ? 64 : 0) | 0x1;
    }
  }
  
  static class Node<K, V>
    implements Map.Entry<K, V>
  {
    final int hash;
    final K key;
    V value;
    Node<K, V> next;
    
    Node(int paramInt, K paramK, V paramV, Node<K, V> paramNode)
    {
      this.hash = paramInt;
      this.key = paramK;
      this.value = paramV;
      this.next = paramNode;
    }
    
    public final K getKey()
    {
      return this.key;
    }
    
    public final V getValue()
    {
      return this.value;
    }
    
    public final String toString()
    {
      return this.key + "=" + this.value;
    }
    
    public final int hashCode()
    {
      return Objects.hashCode(this.key) ^ Objects.hashCode(this.value);
    }
    
    public final V setValue(V paramV)
    {
      Object localObject = this.value;
      this.value = paramV;
      return localObject;
    }
    
    public final boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        if ((Objects.equals(this.key, localEntry.getKey())) && (Objects.equals(this.value, localEntry.getValue()))) {
          return true;
        }
      }
      return false;
    }
  }
  
  static final class TreeNode<K, V>
    extends LinkedHashMap.Entry<K, V>
  {
    TreeNode<K, V> parent;
    TreeNode<K, V> left;
    TreeNode<K, V> right;
    TreeNode<K, V> prev;
    boolean red;
    
    TreeNode(int paramInt, K paramK, V paramV, HashMap.Node<K, V> paramNode)
    {
      super(paramK, paramV, paramNode);
    }
    
    final TreeNode<K, V> root()
    {
      TreeNode localTreeNode;
      for (Object localObject = this;; localObject = localTreeNode) {
        if ((localTreeNode = ((TreeNode)localObject).parent) == null) {
          return localObject;
        }
      }
    }
    
    static <K, V> void moveRootToFront(HashMap.Node<K, V>[] paramArrayOfNode, TreeNode<K, V> paramTreeNode)
    {
      int i;
      if ((paramTreeNode != null) && (paramArrayOfNode != null) && ((i = paramArrayOfNode.length) > 0))
      {
        int j = i - 1 & paramTreeNode.hash;
        TreeNode localTreeNode1 = (TreeNode)paramArrayOfNode[j];
        if (paramTreeNode != localTreeNode1)
        {
          paramArrayOfNode[j] = paramTreeNode;
          TreeNode localTreeNode2 = paramTreeNode.prev;
          HashMap.Node localNode;
          if ((localNode = paramTreeNode.next) != null) {
            ((TreeNode)localNode).prev = localTreeNode2;
          }
          if (localTreeNode2 != null) {
            localTreeNode2.next = localNode;
          }
          if (localTreeNode1 != null) {
            localTreeNode1.prev = paramTreeNode;
          }
          paramTreeNode.next = localTreeNode1;
          paramTreeNode.prev = null;
        }
        assert (checkInvariants(paramTreeNode));
      }
    }
    
    final TreeNode<K, V> find(int paramInt, Object paramObject, Class<?> paramClass)
    {
      Object localObject1 = this;
      do
      {
        TreeNode localTreeNode1 = ((TreeNode)localObject1).left;
        TreeNode localTreeNode2 = ((TreeNode)localObject1).right;
        int i;
        if ((i = ((TreeNode)localObject1).hash) > paramInt)
        {
          localObject1 = localTreeNode1;
        }
        else if (i < paramInt)
        {
          localObject1 = localTreeNode2;
        }
        else
        {
          Object localObject2;
          if (((localObject2 = ((TreeNode)localObject1).key) == paramObject) || ((paramObject != null) && (paramObject.equals(localObject2)))) {
            return localObject1;
          }
          if (localTreeNode1 == null)
          {
            localObject1 = localTreeNode2;
          }
          else if (localTreeNode2 == null)
          {
            localObject1 = localTreeNode1;
          }
          else
          {
            int j;
            if (((paramClass != null) || ((paramClass = HashMap.comparableClassFor(paramObject)) != null)) && ((j = HashMap.compareComparables(paramClass, paramObject, localObject2)) != 0))
            {
              localObject1 = j < 0 ? localTreeNode1 : localTreeNode2;
            }
            else
            {
              TreeNode localTreeNode3;
              if ((localTreeNode3 = localTreeNode2.find(paramInt, paramObject, paramClass)) != null) {
                return localTreeNode3;
              }
              localObject1 = localTreeNode1;
            }
          }
        }
      } while (localObject1 != null);
      return null;
    }
    
    final TreeNode<K, V> getTreeNode(int paramInt, Object paramObject)
    {
      return (this.parent != null ? root() : this).find(paramInt, paramObject, null);
    }
    
    static int tieBreakOrder(Object paramObject1, Object paramObject2)
    {
      int i;
      if ((paramObject1 == null) || (paramObject2 == null) || ((i = paramObject1.getClass().getName().compareTo(paramObject2.getClass().getName())) == 0)) {
        i = System.identityHashCode(paramObject1) <= System.identityHashCode(paramObject2) ? -1 : 1;
      }
      return i;
    }
    
    final void treeify(HashMap.Node<K, V>[] paramArrayOfNode)
    {
      Object localObject1 = null;
      TreeNode localTreeNode;
      for (Object localObject2 = this; localObject2 != null; localObject2 = localTreeNode)
      {
        localTreeNode = (TreeNode)((TreeNode)localObject2).next;
        ((TreeNode)localObject2).left = (((TreeNode)localObject2).right = null);
        if (localObject1 == null)
        {
          ((TreeNode)localObject2).parent = null;
          ((TreeNode)localObject2).red = false;
          localObject1 = localObject2;
        }
        else
        {
          Object localObject3 = ((TreeNode)localObject2).key;
          int i = ((TreeNode)localObject2).hash;
          Class localClass = null;
          Object localObject4 = localObject1;
          for (;;)
          {
            Object localObject5 = ((TreeNode)localObject4).key;
            int k;
            int j;
            if ((k = ((TreeNode)localObject4).hash) > i) {
              j = -1;
            } else if (k < i) {
              j = 1;
            } else if (((localClass == null) && ((localClass = HashMap.comparableClassFor(localObject3)) == null)) || ((j = HashMap.compareComparables(localClass, localObject3, localObject5)) == 0)) {
              j = tieBreakOrder(localObject3, localObject5);
            }
            Object localObject6 = localObject4;
            if ((localObject4 = j <= 0 ? ((TreeNode)localObject4).left : ((TreeNode)localObject4).right) == null)
            {
              ((TreeNode)localObject2).parent = localObject6;
              if (j <= 0) {
                localObject6.left = ((TreeNode)localObject2);
              } else {
                localObject6.right = ((TreeNode)localObject2);
              }
              localObject1 = balanceInsertion((TreeNode)localObject1, (TreeNode)localObject2);
              break;
            }
          }
        }
      }
      moveRootToFront(paramArrayOfNode, (TreeNode)localObject1);
    }
    
    final HashMap.Node<K, V> untreeify(HashMap<K, V> paramHashMap)
    {
      Object localObject1 = null;
      Object localObject2 = null;
      for (Object localObject3 = this; localObject3 != null; localObject3 = ((HashMap.Node)localObject3).next)
      {
        HashMap.Node localNode = paramHashMap.replacementNode((HashMap.Node)localObject3, null);
        if (localObject2 == null) {
          localObject1 = localNode;
        } else {
          localObject2.next = localNode;
        }
        localObject2 = localNode;
      }
      return localObject1;
    }
    
    final TreeNode<K, V> putTreeVal(HashMap<K, V> paramHashMap, HashMap.Node<K, V>[] paramArrayOfNode, int paramInt, K paramK, V paramV)
    {
      Class localClass = null;
      int i = 0;
      TreeNode localTreeNode1 = this.parent != null ? root() : this;
      TreeNode localTreeNode2 = localTreeNode1;
      for (;;)
      {
        int k;
        int j;
        Object localObject2;
        if ((k = localTreeNode2.hash) > paramInt)
        {
          j = -1;
        }
        else if (k < paramInt)
        {
          j = 1;
        }
        else
        {
          Object localObject1;
          if (((localObject1 = localTreeNode2.key) == paramK) || ((paramK != null) && (paramK.equals(localObject1)))) {
            return localTreeNode2;
          }
          if (((localClass == null) && ((localClass = HashMap.comparableClassFor(paramK)) == null)) || ((j = HashMap.compareComparables(localClass, paramK, localObject1)) == 0))
          {
            if (i == 0)
            {
              i = 1;
              if ((((localObject2 = localTreeNode2.left) != null) && ((localTreeNode3 = ((TreeNode)localObject2).find(paramInt, paramK, localClass)) != null)) || (((localObject2 = localTreeNode2.right) != null) && ((localTreeNode3 = ((TreeNode)localObject2).find(paramInt, paramK, localClass)) != null))) {
                return localTreeNode3;
              }
            }
            j = tieBreakOrder(paramK, localObject1);
          }
        }
        TreeNode localTreeNode3 = localTreeNode2;
        if ((localTreeNode2 = j <= 0 ? localTreeNode2.left : localTreeNode2.right) == null)
        {
          localObject2 = localTreeNode3.next;
          TreeNode localTreeNode4 = paramHashMap.newTreeNode(paramInt, paramK, paramV, (HashMap.Node)localObject2);
          if (j <= 0) {
            localTreeNode3.left = localTreeNode4;
          } else {
            localTreeNode3.right = localTreeNode4;
          }
          localTreeNode3.next = localTreeNode4;
          localTreeNode4.parent = (localTreeNode4.prev = localTreeNode3);
          if (localObject2 != null) {
            ((TreeNode)localObject2).prev = localTreeNode4;
          }
          moveRootToFront(paramArrayOfNode, balanceInsertion(localTreeNode1, localTreeNode4));
          return null;
        }
      }
    }
    
    final void removeTreeNode(HashMap<K, V> paramHashMap, HashMap.Node<K, V>[] paramArrayOfNode, boolean paramBoolean)
    {
      int i;
      if ((paramArrayOfNode == null) || ((i = paramArrayOfNode.length) == 0)) {
        return;
      }
      int j = i - 1 & this.hash;
      Object localObject1 = (TreeNode)paramArrayOfNode[j];
      Object localObject2 = localObject1;
      TreeNode localTreeNode2 = (TreeNode)this.next;
      TreeNode localTreeNode3 = this.prev;
      if (localTreeNode3 == null)
      {
        TreeNode tmp62_60 = localTreeNode2;
        localObject1 = tmp62_60;
        paramArrayOfNode[j] = tmp62_60;
      }
      else
      {
        localTreeNode3.next = localTreeNode2;
      }
      if (localTreeNode2 != null) {
        localTreeNode2.prev = localTreeNode3;
      }
      if (localObject1 == null) {
        return;
      }
      if (((TreeNode)localObject2).parent != null) {
        localObject2 = ((TreeNode)localObject2).root();
      }
      TreeNode localTreeNode1;
      if ((localObject2 == null) || (((TreeNode)localObject2).right == null) || ((localTreeNode1 = ((TreeNode)localObject2).left) == null) || (localTreeNode1.left == null))
      {
        paramArrayOfNode[j] = ((TreeNode)localObject1).untreeify(paramHashMap);
        return;
      }
      TreeNode localTreeNode4 = this;
      TreeNode localTreeNode5 = this.left;
      TreeNode localTreeNode6 = this.right;
      TreeNode localTreeNode8;
      TreeNode localTreeNode7;
      if ((localTreeNode5 != null) && (localTreeNode6 != null))
      {
        for (localObject3 = localTreeNode6; (localTreeNode8 = ((TreeNode)localObject3).left) != null; localObject3 = localTreeNode8) {}
        boolean bool = ((TreeNode)localObject3).red;
        ((TreeNode)localObject3).red = localTreeNode4.red;
        localTreeNode4.red = bool;
        TreeNode localTreeNode9 = ((TreeNode)localObject3).right;
        TreeNode localTreeNode10 = localTreeNode4.parent;
        if (localObject3 == localTreeNode6)
        {
          localTreeNode4.parent = ((TreeNode)localObject3);
          ((TreeNode)localObject3).right = localTreeNode4;
        }
        else
        {
          TreeNode localTreeNode11 = ((TreeNode)localObject3).parent;
          if ((localTreeNode4.parent = localTreeNode11) != null) {
            if (localObject3 == localTreeNode11.left) {
              localTreeNode11.left = localTreeNode4;
            } else {
              localTreeNode11.right = localTreeNode4;
            }
          }
          if ((((TreeNode)localObject3).right = localTreeNode6) != null) {
            localTreeNode6.parent = ((TreeNode)localObject3);
          }
        }
        localTreeNode4.left = null;
        if ((localTreeNode4.right = localTreeNode9) != null) {
          localTreeNode9.parent = localTreeNode4;
        }
        if ((((TreeNode)localObject3).left = localTreeNode5) != null) {
          localTreeNode5.parent = ((TreeNode)localObject3);
        }
        if ((((TreeNode)localObject3).parent = localTreeNode10) == null) {
          localObject2 = localObject3;
        } else if (localTreeNode4 == localTreeNode10.left) {
          localTreeNode10.left = ((TreeNode)localObject3);
        } else {
          localTreeNode10.right = ((TreeNode)localObject3);
        }
        if (localTreeNode9 != null) {
          localTreeNode7 = localTreeNode9;
        } else {
          localTreeNode7 = localTreeNode4;
        }
      }
      else if (localTreeNode5 != null)
      {
        localTreeNode7 = localTreeNode5;
      }
      else if (localTreeNode6 != null)
      {
        localTreeNode7 = localTreeNode6;
      }
      else
      {
        localTreeNode7 = localTreeNode4;
      }
      if (localTreeNode7 != localTreeNode4)
      {
        localObject3 = localTreeNode7.parent = localTreeNode4.parent;
        if (localObject3 == null) {
          localObject2 = localTreeNode7;
        } else if (localTreeNode4 == ((TreeNode)localObject3).left) {
          ((TreeNode)localObject3).left = localTreeNode7;
        } else {
          ((TreeNode)localObject3).right = localTreeNode7;
        }
        localTreeNode4.left = (localTreeNode4.right = localTreeNode4.parent = null);
      }
      Object localObject3 = localTreeNode4.red ? localObject2 : balanceDeletion((TreeNode)localObject2, localTreeNode7);
      if (localTreeNode7 == localTreeNode4)
      {
        localTreeNode8 = localTreeNode4.parent;
        localTreeNode4.parent = null;
        if (localTreeNode8 != null) {
          if (localTreeNode4 == localTreeNode8.left) {
            localTreeNode8.left = null;
          } else if (localTreeNode4 == localTreeNode8.right) {
            localTreeNode8.right = null;
          }
        }
      }
      if (paramBoolean) {
        moveRootToFront(paramArrayOfNode, (TreeNode)localObject3);
      }
    }
    
    final void split(HashMap<K, V> paramHashMap, HashMap.Node<K, V>[] paramArrayOfNode, int paramInt1, int paramInt2)
    {
      TreeNode localTreeNode1 = this;
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      int i = 0;
      int j = 0;
      TreeNode localTreeNode2;
      for (Object localObject5 = localTreeNode1; localObject5 != null; localObject5 = localTreeNode2)
      {
        localTreeNode2 = (TreeNode)((TreeNode)localObject5).next;
        ((TreeNode)localObject5).next = null;
        if ((((TreeNode)localObject5).hash & paramInt2) == 0)
        {
          if ((((TreeNode)localObject5).prev = localObject2) == null) {
            localObject1 = localObject5;
          } else {
            localObject2.next = ((HashMap.Node)localObject5);
          }
          localObject2 = localObject5;
          i++;
        }
        else
        {
          if ((((TreeNode)localObject5).prev = localObject4) == null) {
            localObject3 = localObject5;
          } else {
            localObject4.next = ((HashMap.Node)localObject5);
          }
          localObject4 = localObject5;
          j++;
        }
      }
      if (localObject1 != null) {
        if (i <= 6)
        {
          paramArrayOfNode[paramInt1] = localObject1.untreeify(paramHashMap);
        }
        else
        {
          paramArrayOfNode[paramInt1] = localObject1;
          if (localObject3 != null) {
            localObject1.treeify(paramArrayOfNode);
          }
        }
      }
      if (localObject3 != null) {
        if (j <= 6)
        {
          paramArrayOfNode[(paramInt1 + paramInt2)] = localObject3.untreeify(paramHashMap);
        }
        else
        {
          paramArrayOfNode[(paramInt1 + paramInt2)] = localObject3;
          if (localObject1 != null) {
            localObject3.treeify(paramArrayOfNode);
          }
        }
      }
    }
    
    static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> paramTreeNode1, TreeNode<K, V> paramTreeNode2)
    {
      TreeNode localTreeNode1;
      if ((paramTreeNode2 != null) && ((localTreeNode1 = paramTreeNode2.right) != null))
      {
        TreeNode localTreeNode3;
        if ((localTreeNode3 = paramTreeNode2.right = localTreeNode1.left) != null) {
          localTreeNode3.parent = paramTreeNode2;
        }
        TreeNode localTreeNode2;
        if ((localTreeNode2 = localTreeNode1.parent = paramTreeNode2.parent) == null) {
          (paramTreeNode1 = localTreeNode1).red = false;
        } else if (localTreeNode2.left == paramTreeNode2) {
          localTreeNode2.left = localTreeNode1;
        } else {
          localTreeNode2.right = localTreeNode1;
        }
        localTreeNode1.left = paramTreeNode2;
        paramTreeNode2.parent = localTreeNode1;
      }
      return paramTreeNode1;
    }
    
    static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> paramTreeNode1, TreeNode<K, V> paramTreeNode2)
    {
      TreeNode localTreeNode1;
      if ((paramTreeNode2 != null) && ((localTreeNode1 = paramTreeNode2.left) != null))
      {
        TreeNode localTreeNode3;
        if ((localTreeNode3 = paramTreeNode2.left = localTreeNode1.right) != null) {
          localTreeNode3.parent = paramTreeNode2;
        }
        TreeNode localTreeNode2;
        if ((localTreeNode2 = localTreeNode1.parent = paramTreeNode2.parent) == null) {
          (paramTreeNode1 = localTreeNode1).red = false;
        } else if (localTreeNode2.right == paramTreeNode2) {
          localTreeNode2.right = localTreeNode1;
        } else {
          localTreeNode2.left = localTreeNode1;
        }
        localTreeNode1.right = paramTreeNode2;
        paramTreeNode2.parent = localTreeNode1;
      }
      return paramTreeNode1;
    }
    
    static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> paramTreeNode1, TreeNode<K, V> paramTreeNode2)
    {
      paramTreeNode2.red = true;
      for (;;)
      {
        TreeNode localTreeNode1;
        if ((localTreeNode1 = paramTreeNode2.parent) == null)
        {
          paramTreeNode2.red = false;
          return paramTreeNode2;
        }
        TreeNode localTreeNode2;
        if ((!localTreeNode1.red) || ((localTreeNode2 = localTreeNode1.parent) == null)) {
          return paramTreeNode1;
        }
        TreeNode localTreeNode3;
        if (localTreeNode1 == (localTreeNode3 = localTreeNode2.left))
        {
          TreeNode localTreeNode4;
          if (((localTreeNode4 = localTreeNode2.right) != null) && (localTreeNode4.red))
          {
            localTreeNode4.red = false;
            localTreeNode1.red = false;
            localTreeNode2.red = true;
            paramTreeNode2 = localTreeNode2;
          }
          else
          {
            if (paramTreeNode2 == localTreeNode1.right)
            {
              paramTreeNode1 = rotateLeft(paramTreeNode1, paramTreeNode2 = localTreeNode1);
              localTreeNode2 = (localTreeNode1 = paramTreeNode2.parent) == null ? null : localTreeNode1.parent;
            }
            if (localTreeNode1 != null)
            {
              localTreeNode1.red = false;
              if (localTreeNode2 != null)
              {
                localTreeNode2.red = true;
                paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode2);
              }
            }
          }
        }
        else if ((localTreeNode3 != null) && (localTreeNode3.red))
        {
          localTreeNode3.red = false;
          localTreeNode1.red = false;
          localTreeNode2.red = true;
          paramTreeNode2 = localTreeNode2;
        }
        else
        {
          if (paramTreeNode2 == localTreeNode1.left)
          {
            paramTreeNode1 = rotateRight(paramTreeNode1, paramTreeNode2 = localTreeNode1);
            localTreeNode2 = (localTreeNode1 = paramTreeNode2.parent) == null ? null : localTreeNode1.parent;
          }
          if (localTreeNode1 != null)
          {
            localTreeNode1.red = false;
            if (localTreeNode2 != null)
            {
              localTreeNode2.red = true;
              paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode2);
            }
          }
        }
      }
    }
    
    static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> paramTreeNode1, TreeNode<K, V> paramTreeNode2)
    {
      for (;;)
      {
        if ((paramTreeNode2 == null) || (paramTreeNode2 == paramTreeNode1)) {
          return paramTreeNode1;
        }
        TreeNode localTreeNode1;
        if ((localTreeNode1 = paramTreeNode2.parent) == null)
        {
          paramTreeNode2.red = false;
          return paramTreeNode2;
        }
        if (paramTreeNode2.red)
        {
          paramTreeNode2.red = false;
          return paramTreeNode1;
        }
        TreeNode localTreeNode2;
        TreeNode localTreeNode4;
        TreeNode localTreeNode5;
        if ((localTreeNode2 = localTreeNode1.left) == paramTreeNode2)
        {
          TreeNode localTreeNode3;
          if (((localTreeNode3 = localTreeNode1.right) != null) && (localTreeNode3.red))
          {
            localTreeNode3.red = false;
            localTreeNode1.red = true;
            paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode1);
            localTreeNode3 = (localTreeNode1 = paramTreeNode2.parent) == null ? null : localTreeNode1.right;
          }
          if (localTreeNode3 == null)
          {
            paramTreeNode2 = localTreeNode1;
          }
          else
          {
            localTreeNode4 = localTreeNode3.left;
            localTreeNode5 = localTreeNode3.right;
            if (((localTreeNode5 == null) || (!localTreeNode5.red)) && ((localTreeNode4 == null) || (!localTreeNode4.red)))
            {
              localTreeNode3.red = true;
              paramTreeNode2 = localTreeNode1;
            }
            else
            {
              if ((localTreeNode5 == null) || (!localTreeNode5.red))
              {
                if (localTreeNode4 != null) {
                  localTreeNode4.red = false;
                }
                localTreeNode3.red = true;
                paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode3);
                localTreeNode3 = (localTreeNode1 = paramTreeNode2.parent) == null ? null : localTreeNode1.right;
              }
              if (localTreeNode3 != null)
              {
                localTreeNode3.red = (localTreeNode1 == null ? false : localTreeNode1.red);
                if ((localTreeNode5 = localTreeNode3.right) != null) {
                  localTreeNode5.red = false;
                }
              }
              if (localTreeNode1 != null)
              {
                localTreeNode1.red = false;
                paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode1);
              }
              paramTreeNode2 = paramTreeNode1;
            }
          }
        }
        else
        {
          if ((localTreeNode2 != null) && (localTreeNode2.red))
          {
            localTreeNode2.red = false;
            localTreeNode1.red = true;
            paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode1);
            localTreeNode2 = (localTreeNode1 = paramTreeNode2.parent) == null ? null : localTreeNode1.left;
          }
          if (localTreeNode2 == null)
          {
            paramTreeNode2 = localTreeNode1;
          }
          else
          {
            localTreeNode4 = localTreeNode2.left;
            localTreeNode5 = localTreeNode2.right;
            if (((localTreeNode4 == null) || (!localTreeNode4.red)) && ((localTreeNode5 == null) || (!localTreeNode5.red)))
            {
              localTreeNode2.red = true;
              paramTreeNode2 = localTreeNode1;
            }
            else
            {
              if ((localTreeNode4 == null) || (!localTreeNode4.red))
              {
                if (localTreeNode5 != null) {
                  localTreeNode5.red = false;
                }
                localTreeNode2.red = true;
                paramTreeNode1 = rotateLeft(paramTreeNode1, localTreeNode2);
                localTreeNode2 = (localTreeNode1 = paramTreeNode2.parent) == null ? null : localTreeNode1.left;
              }
              if (localTreeNode2 != null)
              {
                localTreeNode2.red = (localTreeNode1 == null ? false : localTreeNode1.red);
                if ((localTreeNode4 = localTreeNode2.left) != null) {
                  localTreeNode4.red = false;
                }
              }
              if (localTreeNode1 != null)
              {
                localTreeNode1.red = false;
                paramTreeNode1 = rotateRight(paramTreeNode1, localTreeNode1);
              }
              paramTreeNode2 = paramTreeNode1;
            }
          }
        }
      }
    }
    
    static <K, V> boolean checkInvariants(TreeNode<K, V> paramTreeNode)
    {
      TreeNode localTreeNode1 = paramTreeNode.parent;
      TreeNode localTreeNode2 = paramTreeNode.left;
      TreeNode localTreeNode3 = paramTreeNode.right;
      TreeNode localTreeNode4 = paramTreeNode.prev;
      TreeNode localTreeNode5 = (TreeNode)paramTreeNode.next;
      if ((localTreeNode4 != null) && (localTreeNode4.next != paramTreeNode)) {
        return false;
      }
      if ((localTreeNode5 != null) && (localTreeNode5.prev != paramTreeNode)) {
        return false;
      }
      if ((localTreeNode1 != null) && (paramTreeNode != localTreeNode1.left) && (paramTreeNode != localTreeNode1.right)) {
        return false;
      }
      if ((localTreeNode2 != null) && ((localTreeNode2.parent != paramTreeNode) || (localTreeNode2.hash > paramTreeNode.hash))) {
        return false;
      }
      if ((localTreeNode3 != null) && ((localTreeNode3.parent != paramTreeNode) || (localTreeNode3.hash < paramTreeNode.hash))) {
        return false;
      }
      if ((paramTreeNode.red) && (localTreeNode2 != null) && (localTreeNode2.red) && (localTreeNode3 != null) && (localTreeNode3.red)) {
        return false;
      }
      if ((localTreeNode2 != null) && (!checkInvariants(localTreeNode2))) {
        return false;
      }
      return (localTreeNode3 == null) || (checkInvariants(localTreeNode3));
    }
  }
  
  final class ValueIterator
    extends HashMap<K, V>.HashIterator
    implements Iterator<V>
  {
    ValueIterator()
    {
      super();
    }
    
    public final V next()
    {
      return nextNode().value;
    }
  }
  
  static final class ValueSpliterator<K, V>
    extends HashMap.HashMapSpliterator<K, V>
    implements Spliterator<V>
  {
    ValueSpliterator(HashMap<K, V> paramHashMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public ValueSpliterator<K, V> trySplit()
    {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) || (this.current != null) ? null : new ValueSpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap localHashMap = this.map;
      HashMap.Node[] arrayOfNode = localHashMap.table;
      int j;
      int k;
      if ((j = this.fence) < 0)
      {
        k = this.expectedModCount = localHashMap.modCount;
        j = this.fence = arrayOfNode == null ? 0 : arrayOfNode.length;
      }
      else
      {
        k = this.expectedModCount;
      }
      int i;
      if ((arrayOfNode != null) && (arrayOfNode.length >= j) && ((i = this.index) >= 0) && ((i < (this.index = j)) || (this.current != null)))
      {
        HashMap.Node localNode = this.current;
        this.current = null;
        do
        {
          if (localNode == null)
          {
            localNode = arrayOfNode[(i++)];
          }
          else
          {
            paramConsumer.accept(localNode.value);
            localNode = localNode.next;
          }
        } while ((localNode != null) || (i < j));
        if (localHashMap.modCount != k) {
          throw new ConcurrentModificationException();
        }
      }
    }
    
    public boolean tryAdvance(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap.Node[] arrayOfNode = this.map.table;
      int i;
      if ((arrayOfNode != null) && (arrayOfNode.length >= (i = getFence())) && (this.index >= 0)) {
        while ((this.current != null) || (this.index < i)) {
          if (this.current == null)
          {
            this.current = arrayOfNode[(this.index++)];
          }
          else
          {
            Object localObject = this.current.value;
            this.current = this.current.next;
            paramConsumer.accept(localObject);
            if (this.map.modCount != this.expectedModCount) {
              throw new ConcurrentModificationException();
            }
            return true;
          }
        }
      }
      return false;
    }
    
    public int characteristics()
    {
      return (this.fence < 0) || (this.est == this.map.size) ? 64 : 0;
    }
  }
  
  final class Values
    extends AbstractCollection<V>
  {
    Values() {}
    
    public final int size()
    {
      return HashMap.this.size;
    }
    
    public final void clear()
    {
      HashMap.this.clear();
    }
    
    public final Iterator<V> iterator()
    {
      return new HashMap.ValueIterator(HashMap.this);
    }
    
    public final boolean contains(Object paramObject)
    {
      return HashMap.this.containsValue(paramObject);
    }
    
    public final Spliterator<V> spliterator()
    {
      return new HashMap.ValueSpliterator(HashMap.this, 0, -1, 0, 0);
    }
    
    public final void forEach(Consumer<? super V> paramConsumer)
    {
      if (paramConsumer == null) {
        throw new NullPointerException();
      }
      HashMap.Node[] arrayOfNode;
      if ((HashMap.this.size > 0) && ((arrayOfNode = HashMap.this.table) != null))
      {
        int i = HashMap.this.modCount;
        for (int j = 0; j < arrayOfNode.length; j++) {
          for (HashMap.Node localNode = arrayOfNode[j]; localNode != null; localNode = localNode.next) {
            paramConsumer.accept(localNode.value);
          }
        }
        if (HashMap.this.modCount != i) {
          throw new ConcurrentModificationException();
        }
      }
    }
  }
}
