package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LookupSwitchInsnNode
  extends AbstractInsnNode
{
  public LabelNode dflt;
  public List<Integer> keys;
  public List<LabelNode> labels;
  
  public LookupSwitchInsnNode(LabelNode paramLabelNode, int[] paramArrayOfInt, LabelNode[] paramArrayOfLabelNode)
  {
    super(171);
    this.dflt = paramLabelNode;
    this.keys = new ArrayList(paramArrayOfInt == null ? 0 : paramArrayOfInt.length);
    this.labels = new ArrayList(paramArrayOfLabelNode == null ? 0 : paramArrayOfLabelNode.length);
    if (paramArrayOfInt != null) {
      for (int i = 0; i < paramArrayOfInt.length; i++) {
        this.keys.add(Integer.valueOf(paramArrayOfInt[i]));
      }
    }
    if (paramArrayOfLabelNode != null) {
      this.labels.addAll(Arrays.asList(paramArrayOfLabelNode));
    }
  }
  
  public int getType()
  {
    return 12;
  }
  
  public void accept(MethodVisitor paramMethodVisitor)
  {
    int[] arrayOfInt = new int[this.keys.size()];
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = ((Integer)this.keys.get(i)).intValue();
    }
    Label[] arrayOfLabel = new Label[this.labels.size()];
    for (int j = 0; j < arrayOfLabel.length; j++) {
      arrayOfLabel[j] = ((LabelNode)this.labels.get(j)).getLabel();
    }
    paramMethodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), arrayOfInt, arrayOfLabel);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap)
  {
    LookupSwitchInsnNode localLookupSwitchInsnNode = new LookupSwitchInsnNode(clone(this.dflt, paramMap), null, clone(this.labels, paramMap));
    localLookupSwitchInsnNode.keys.addAll(this.keys);
    return localLookupSwitchInsnNode.cloneAnnotations(this);
  }
}
