package javax.swing;

final class LegacyLayoutFocusTraversalPolicy
  extends LayoutFocusTraversalPolicy
{
  LegacyLayoutFocusTraversalPolicy(DefaultFocusManager paramDefaultFocusManager)
  {
    super(new CompareTabOrderComparator(paramDefaultFocusManager));
  }
}
