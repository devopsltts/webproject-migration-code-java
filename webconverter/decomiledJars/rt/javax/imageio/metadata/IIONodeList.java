package javax.imageio.metadata;

import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class IIONodeList
  implements NodeList
{
  List nodes;
  
  public IIONodeList(List paramList)
  {
    this.nodes = paramList;
  }
  
  public int getLength()
  {
    return this.nodes.size();
  }
  
  public Node item(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > this.nodes.size())) {
      return null;
    }
    return (Node)this.nodes.get(paramInt);
  }
}
