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

      List<Double> gains = calculateGains(attributes, attributeValues, instances);
      int maxIndex = maxArrayList(gains);
	  double maxGain = gains.get(maxIndex);
	  
	  String label = "";
	  boolean isTerminal = maxGain == totalEntropy(instances); // true if entropy == 0  and all of the instances have the same label
	  if(isTerminal)
		  label = instances.get(0).label; // all instances have the same label, Hence we shall use the label of the first one
	  	  
	  DecTreeNode node = new DecTreeNode(label, attributes.get(maxIndex), null, isTerminal);
	  this.root = node;
	  
	  List<String> values = attributeValues.get(attributes.get(maxIndex));
      // TODO if isTerminal
	  	  
	  List<String> newAttributes = new ArrayList<>(attributes);
	  Map<String, List<String>> newAttributeValues = new HashMap<String, List<String>>(attributeValues);
			  
	  newAttributeValues.remove(newAttributes.get(maxIndex));
	  newAttributes.remove(maxIndex);
	  
	  for(String value : values)
		  buildTree(newAttributes, newAttributeValues, attributeValueInstances(instances, maxIndex, value), label, node, value);
  }
  
  // recursive method for building the tree one layer at a time, passes to each son the entire information of the dataset 
  private void buildTree(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances ,String label, DecTreeNode parent, String parentValue)
  {
	  if(attributes == null || instances.isEmpty())
		  return;
	  
	  List<Double> gains = calculateGains(attributes, attributeValues, instances);
	  int maxIndex = maxArrayList(gains);
	  double maxGain = gains.get(maxIndex);

	  boolean isTerminal = maxGain == totalEntropy(instances); // true if entropy == 0  and all of the instances have the same label
	  if(isTerminal)
		  label = instances.get(0).label; // all instances have the same label, Hence we shall use the label of the first one
	  	  
	  DecTreeNode node = new DecTreeNode(label, attributes.get(maxIndex), parentValue, isTerminal);
	  
	  if(parent != null)
    	  parent.addChild(node);
	  
      if(isTerminal)
    	  return;
	  
      List<String> values = attributeValues.get(attributes.get(maxIndex)); 
      
      attributeValues.remove(attributes.get(maxIndex));
      attributes.remove(maxIndex);
      
      for(String value : values)
		  buildTree(attributes, attributeValues, attributeValueInstances(instances, maxIndex, value), label, node, value);
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

  public void check(DataSet train) {
	  /*TODO delete 	for(String value : attributeValues.get("A1"))
		{
			System.out.println("A1: " + value);
			System.out.println();*/
			
			System.out.println("A1- x ; A3- n");
			for(Instance instance: train.instances)
	  		{	
				if(instance.attributes.get(0).equals("x") && instance.attributes.get(2).equals("n"))
				{
					System.out.println(instance.attributes.get(6) + " " + instance.label);
				}
	  		}
			
			System.out.println("A1- n ; A5- n");
			for(Instance instance: train.instances)
	  		{
				if(instance.attributes.get(0).equals("x") && instance.attributes.get(2).equals("n"))
				{
					System.out.println(instance.label);
				}
	  		}
			
			System.out.println("A1 - b ; A2- b ; A6- n ; A9 - n");
			for(Instance instance: train.instances)
	  		{
				if(instance.attributes.get(0).equals("b") && instance.attributes.get(1).equals("b") && instance.attributes.get(5).equals("n") && instance.attributes.get(8).equals("n"))
				{
					System.out.println(instance.label);
				}
	  		}
			
			System.out.println("A1- g");
			for(Instance instance: train.instances)
	  		{
				if(instance.attributes.get(0).equals("g"))
				{
					System.out.println(instance.attributes.get(3) + " " + instance.label);
				}
  			}/*
			System.out.println();
			System.out.println();
		}*/
  }
  
  @Override
  public String classify(Instance instance) {
	DecTreeNode node = this.root;
	while(!node.terminal)
	{
		for (int i = 0; i < node.children.size(); i++)
		{
			DecTreeNode child = node.children.get(i);
			if (child.parentAttributeValue.equals(instance.attributes.get(getAttributeIndex(node.attribute))))
			{
				node = child;
				break;
			}
		}
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
  private List<Instance> attributeValueInstances(List<Instance> instances, int attributeIndex, String attributeValue)
  {
	  List<Instance> newInstances = new ArrayList<Instance>();
	  for(Instance instance : instances) {
		  if(instance.attributes.get(attributeIndex).equals(attributeValue)) {
			  newInstances.add(instance);
		  }
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
		  
		  for(int j = 0; j < values.size(); j++) { // 2-class classification is assumed
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
    /**
   * Build a decision tree given a training set then prune it using a tuning set.
   * ONLY for extra credits
   * @param train: the training set
   * @param tune: the tuning set
   */
  DecisionTreeImpl(DataSet train, DataSet tune) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    // only for extra credits
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
