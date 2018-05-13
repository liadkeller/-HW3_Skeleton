import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

      this.labels = train.labels;
      this.attributes = train.attributes;
      this.attributeValues = train.attributeValues;
      List<Instance> instances = train.instances;

      /*System.out.println("Instances: ");
      for (Instance instance: instances) {
    	  System.out.print(instance.label + " ");
      }
      System.out.println(allSame(instances));*/
     
      boolean isTerminal = allSame(instances); // true if entropy == 0  and all of the instances have the same label
	  if(isTerminal) {
		  String label = instances.get(0).label; // all instances have the same label, Hence we shall use the label of the first one
		  DecTreeNode node = new DecTreeNode(label, null, null, isTerminal); // attribute = null, parentAttribute = null
		  this.root = node;
		  return;
	  }
	  
	  int bestAttrIndex = mostImportantAttributeIndex(attributes, attributeValues, instances);
	  String bestAttribute = attributes.get(bestAttrIndex);
	  	  
	  DecTreeNode node = new DecTreeNode(null, bestAttribute, null, isTerminal);
	  this.root = node;
	  
	  List<String> bestAttributeValues = attributeValues.get(bestAttribute);
      	  
	  List<String> newAttributes = new ArrayList<>(attributes);
	  Map<String, List<String>> newAttributeValues = new HashMap<String, List<String>>(attributeValues);
	  
	  newAttributeValues.remove(bestAttribute);
	  newAttributes.remove(bestAttrIndex);
	  
	  //System.out.println(bestAttribute + ":");
	  
	  System.out.println("-> " + bestAttribute);
	  
	  int i = 1;
	  for(String value : bestAttributeValues)
	  {
		  //System.out.println("FIRST " + i + " " + bestAttribute + " " + value);
		  buildTree(newAttributes, newAttributeValues, attributeValueInstances(instances, bestAttribute, value), node, value);
		  i++;
	  }
  }
  
  private int mostImportantAttributeIndex(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances) {
	List<Double> gains = calculateGains(attributes, attributeValues, instances);
    return maxArrayList(gains);
}

// recursive method for building the tree one layer at a time, passes to each son the entire information of the dataset 
  private void buildTree(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances, DecTreeNode parent, String parentValue)
  {
	  //System.out.println("PARENT: " + parentValue);
	  //System.out.println("Attr: " + attributes);
	  if(attributes == null || attributes.isEmpty())  
		  return;

	  //System.out.println("instances.isEmpty() " + instances.isEmpty());
	  if(instances.isEmpty())
	  {
		  //System.out.println(parent.attribute + " " + parentValue); //TODO
		  return;
	  }
	  
	  //System.out.println("Instances: ");
      for (Instance instance: instances) {
    	  //System.out.print(instance.label + " ");
      }
      //System.out.println(allSame(instances));
	  boolean isTerminal = allSame(instances);
	  
	  if(isTerminal)
	  {
		  String label = instances.get(0).label; // all instances have the same label, Hence we shall use the label of the first one
		  DecTreeNode node = new DecTreeNode(label, null, parentValue, isTerminal);
	      parent.addChild(node);
		  return;
	  }
	  //System.out.println("Not");    
	  int bestAttrIndex = mostImportantAttributeIndex(attributes, attributeValues, instances);
	  String bestAttribute = attributes.get(bestAttrIndex);
	  //System.out.println("SON: " + bestAttribute+ "  (" + attributes + ")");  
	  
	  
	  	  
	  DecTreeNode node = new DecTreeNode(null, bestAttribute, parentValue, isTerminal);
	  parent.addChild(node);
	  
      List<String> bestAttributeValues = attributeValues.get(bestAttribute); 
      
      List<String> newAttributes = new ArrayList<>(attributes);
	  Map<String, List<String>> newAttributeValues = new HashMap<String, List<String>>(attributeValues);
	  
	  //System.out.println("Attr: " + attributes);
	  //System.out.println("New Attr 1: " + attributes);
	  
	  newAttributeValues.remove(bestAttribute);
	  newAttributes.remove(bestAttrIndex);
	  
	  //System.out.println("New Attr 2: " + attributes);
      
      System.out.println(parent.attribute + "-" + parentValue + " -> " + bestAttribute);
      
      
      for(String value : bestAttributeValues) {
    	  //System.out.println("HEY " + bestAttribute + " " + value + " Attr: " + newAttributes);
    	  //System.out.println();
    	  buildTree(newAttributes, newAttributeValues, attributeValueInstances(instances, bestAttribute, value), node, value);
      }  
  }

  private int maxArrayList(List<Double> list)
  {
	  int maxIndex = 0;
	  double maxVal = 0;
	  for(int i = 0; i < list.size(); i++)
	  {
		  if(list.get(i) > maxVal)
		  {
			  maxIndex = i;
			  maxVal = list.get(i);
		  }
	  }
	  return maxIndex;
  }
  
  // calculates the entropy for all the attributes together (good instances to bad instances)
  private double totalEntropy(List<Instance> instances)
  {
	  int goods = 0, bads = 0;
	  for(Instance instance : instances) {
		  if(instance.label.equals(labels.get(0))) { goods++; }
		  else { bads++; }
	  }
	  if(goods == 0 || bads == 0) return 0;
	  return entropy(goods, bads);
  }
  
  private boolean allSame(List<Instance> instances)
  {
	  if(instances.isEmpty())
		  return false;
	  String label = instances.get(0).label;
	  for(int i = 1; i < instances.size(); i++)
	  {
		  if(!instances.get(i).label.equals(label))
			  return false;
	  }
	  return true;
  }
  
  @Override
  public String classify(Instance instance) {
	DecTreeNode node = this.root;
	//System.out.println("NEW NODE" + " " + instance.attributes + " " + node.attribute); //TODO
	while(!node.terminal)
	{	
		boolean valueFound = false; // sets true when the loop finds the wanted value for the instance's attribute
		for (int i = 0; i < node.children.size(); i++)
		{
			DecTreeNode child = node.children.get(i);
			//System.out.println(child.parentAttributeValue);
			if (child.parentAttributeValue.equals(instance.attributes.get(getAttributeIndex(node.attribute))))
			{
				node = child;
				valueFound = true;
				break;
			}
		}
		if(!valueFound)
			return "Error";
	}
	return node.label;
  }

  //private method for inner calculation
  private double entropy(int p, int n)
  {
	  double q = (double)(p)/(p+n);
	  return -(q*lg2(q) + (1-q)*lg2(1-q));
  }
  
  //private method for inner calculation
  private double lg2(double x) {
	  return Math.log(x)/Math.log(2);  
  }
  
  // We shall consider the fact that 2-class (only!) classification is assumed when using this method
  private int countGood(List<Instance> instances, int attributeIndex, String attributeValue)
  {
	  int count = 0;
	  for(Instance instance : instances) {  
		  if(instance.attributes.get(attributeIndex).equals(attributeValue)) {		  
			  if(instance.label.equals(labels.get(0)))
				  count++;
		  }
	  }
	  return count;
  }
  
  //We shall consider the fact that 2-class (only!) classification is assumed when using this method
  private int countBad(List<Instance> instances, int attributeIndex, String attributeValue) {
	  int count = 0;
	  for(Instance instance : instances) {
		  if(instance.attributes.get(attributeIndex).equals(attributeValue)) {
			  if(instance.label.equals(labels.get(1)))
				  count++;
		  }
	  }
	  return count;
  }
  
  // returns all the instances who have a constant value for a specific attribute (e.g. all instances who have a rented house) 
  private List<Instance> attributeValueInstances(List<Instance> instances, String attribute, String attributeValue)
  {
	  int attributeIndex = getAttributeIndex(attribute);
	  List<Instance> newInstances = new ArrayList<Instance>();
	  
	  for(Instance instance : instances) {
		  if(instance.attributes.get(attributeIndex).equals(attributeValue))
			  newInstances.add(instance);
	  }
	  return newInstances;
  }
  
  // return a list of integers containing the info gain of each attribute respectively
  private List<Double> calculateGains(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances)
  {
	  int goods,bads,attributeTotal;
	  double attributeSum;
	  List<Double> gains = new ArrayList<Double>();
	  //List<Integer> errors = new ArrayList<Integer>(); TODO
	  for(int i = 0; i < attributes.size(); i++) {
		  List<String> values = attributeValues.get(attributes.get(i));
		  attributeSum = 0;
		  for(int j = 0; j < values.size(); j++)  // 2-class classification is assumed
		  {
			  goods = countGood(instances, getAttributeIndex(attributes.get(i)), values.get(j));
			  bads = countBad(instances, getAttributeIndex(attributes.get(i)), values.get(j));
			  attributeTotal = goods+bads;
			  
			  if(goods != 0 && bads != 0)
				  attributeSum += ((double)attributeTotal/instances.size())*entropy(goods, bads); // the sum of entropies for the specific attribute
		  }
		  gains.add(totalEntropy(instances) - attributeSum);
	  }
	  
	  /* 
	      /*
		  if(attributeSum == 0) {
			  errors.add(i);
			  System.out.println("NO ENTROPY " + attributes.get(i));
		  }
		  
		  
	   
	  for(int i = 0; i < errors.size(); i++) {  TODO
		  System.out.println(attributes.get(errors.get(i)));
		  for(int j = 0; j < attributeValues.get(attributes.get(errors.get(i))).size(); j++) {
			  int good = countGood(instances, i, attributeValues.get(attributes.get(errors.get(i))).get(j));
			  System.out.println(attributeValues.get(attributes.get(errors.get(i))).get(j) + " " + good);
		  }
		  
		  for(Instance instance : instances)
		  {
			  //System.out.println("GOODSTART");
			  if(instance.label.equals(labels.get(0)))
		         System.out.println(attributes.get(errors.get(i)) + " " + errors.get(i) + " " + instance.attributes.get(errors.get(i)) + " " + attributeValues.get(attributes.get(errors.get(i))) + " GOOD");
			     System.out.println(attributes);
			  //System.out.println("GOODEND");
		  }
      }*/
	  return gains;
  }
  
  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    List<Instance> instances = train.instances;
	List<Double> gains = calculateGains(attributes, attributeValues, instances);
	for(int i = 0; i < gains.size(); i++) {
		System.out.format("%s %.5f\n", attributes.get(i), gains.get(i));
	} 
  }
  
  @Override
  public void printAccuracy(DataSet test) {
    int count = 0;
    for(Instance instance : test.instances)
    {
    	if(this.classify(instance).equals(instance.label))
    		count++;
    }
    double accuracy = (double) count / test.instances.size();
    System.out.format("%.5f\n", accuracy);
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {
    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  @SuppressWarnings("unused")
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
}
