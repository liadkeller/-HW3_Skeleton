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

     
	  if(allSame(instances)) // true if entropy == 0  and all of the instances have the same label
	  {
		  String label = instances.get(0).label; // all instances have the same label, Hence we shall use the label of the first one
		  DecTreeNode node = new DecTreeNode(label, null, null, true); // attribute = null, parentAttribute = null, terminal = true
		  this.root = node;
		  return;
	  }
	  
	  int bestAttrIndex = mostImportantAttributeIndex(attributes, attributeValues, instances);
	  String bestAttribute = attributes.get(bestAttrIndex);
	  	  
	  DecTreeNode node = new DecTreeNode(null, bestAttribute, null, false);
	  this.root = node;
	  
	  List<String> bestAttributeValues = attributeValues.get(bestAttribute);
      	  
	  List<String> newAttributes = new ArrayList<>(attributes);
	  Map<String, List<String>> newAttributeValues = new HashMap<String, List<String>>(attributeValues);
	  
	  newAttributeValues.remove(bestAttribute);
	  newAttributes.remove(bestAttrIndex);
	  
	  for(String value : bestAttributeValues)
		  buildTree(newAttributes, newAttributeValues, attributeValueInstances(instances, bestAttribute, value), node, value);
  }
  
  // recursive method for building the tree one layer at a time, passes to each son the entire information of the dataset 
  private void buildTree(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances, DecTreeNode parent, String parentValue)
  {	  
	  if(attributes == null || attributes.isEmpty())  
	  {
		  String label = majorityLabel(instances);
		  DecTreeNode node = new DecTreeNode(label, null, parentValue, true);
	      parent.addChild(node);
		  return;
	  }

	  if(allSame(instances))
	  {
		  String label = instances.get(0).label; // all instances have the same label, Hence we shall use the label of the first one
		  DecTreeNode node = new DecTreeNode(label, null, parentValue, true);
	      parent.addChild(node);
		  return;
	  }
   
	  int bestAttrIndex = mostImportantAttributeIndex(attributes, attributeValues, instances);
	  String bestAttribute = attributes.get(bestAttrIndex);  

	  DecTreeNode node = new DecTreeNode(null, bestAttribute, parentValue, false);
	  parent.addChild(node);
	  
      List<String> bestAttributeValues = attributeValues.get(bestAttribute); 
      
      List<String> newAttributes = new ArrayList<>(attributes);
	  Map<String, List<String>> newAttributeValues = new HashMap<String, List<String>>(attributeValues);
	  
	  newAttributeValues.remove(bestAttribute);
	  newAttributes.remove(bestAttrIndex);
	  
      for(String value : bestAttributeValues)
    	  buildTree(newAttributes, newAttributeValues, attributeValueInstances(instances, bestAttribute, value), node, value);
  }

  private String majorityLabel(List<Instance> instances) {
	  //builds an array with the labels counts
	  List<Integer> labelsCounts = new ArrayList<Integer>(labels.size());
	  
	  for(int i = 0; i < labels.size(); i++)
		  labelsCounts.add(0);
	  	  
	  for(Instance instance : instances)
	  {
		  int labelIndex = getLabelIndex(instance.label); // increments the labelCount in 1 according to the label of the instance
		  int k = labelsCounts.get(labelIndex);
		  labelsCounts.set(labelIndex, k+1);  
	  }
	  
	  //find the maximum of the array
	  int maxIndex = 0;
	  int maxValue = 0;
	  
	  for(int i = 0; i < labels.size(); i++)
	  {
		  if(labelsCounts.get(i) > maxValue)
		  {
			  maxIndex = i;
			  maxValue = labelsCounts.get(maxIndex);
		  }
	  }
	  
	  //returns the label that had the largest number of instances.
	  return labels.get(maxIndex);
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

//private method for inner calculation
private String maxDoubleMap(Map<String, Double> dMap)
  {
	  Map.Entry<String, Double> maxEntry = null;
	  for (Map.Entry<String, Double> entry : dMap.entrySet()) {
	    if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
	      maxEntry = entry;
	    }
	  }

	  return maxEntry.getKey();
  }

//private method for inner calculation
  private double lg2(double x) {
	  return Math.log(x)/Math.log(2);  
  }

//private method for inner calculation
private int sumArray(List<Integer> arr) {
	int sum = 0;
	for(int i = 0; i < arr.size(); i++)
		sum += arr.get(i);

	return sum;
}

//private method for entropy calculation
  private double calcEntropy(List<Integer> arr)
  {
	  int total = sumArray(arr);
	  if(total == 0)
		  return 0;
	  
	  double sum = 0;
	  for(int i = 0; i < arr.size(); i++)
	  {
		  double q = (double) arr.get(i) / total;
		  if(q != 0)
			  sum += q*lg2(q);
	  }
	  
	  return -sum;
  }

private int mostImportantAttributeIndex(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances) {
	Map<String, Double> gains = calculateGains(attributes, attributeValues, instances);
	
	String mostImportantAttribute = maxDoubleMap(gains);
    for (int i = 0; i < attributes.size(); i++)
    {
        if (mostImportantAttribute.equals(attributes.get(i)))
            return i;
    }
    return -1;
}

// return a list of integers containing the info gain of each attribute respectively
  private Map<String, Double> calculateGains(List<String> attributes, Map<String, List<String>> attributeValues, List<Instance> instances)
  {
	  double attributeSum;
	  Map<String, Double> gains = new HashMap<String, Double>();
	  
	  for(int i = 0; i < attributes.size(); i++)
	  {
		  List<String> values = attributeValues.get(attributes.get(i));
		  
		  attributeSum = 0;
		  for(int j = 0; j < values.size(); j++)
		  {
			  List<Instance> attributeValueInstances = attributeValueInstances(instances, attributes.get(i), attributeValues.get(attributes.get(i)).get(j));
			  attributeSum += ((double) attributeValueInstances.size() / instances.size()) * entropy(attributeValueInstances); // the sum of entropies for the specific attribute
		  }
		  
		  gains.put(attributes.get(i), (entropy(instances) - attributeSum));
	  }
	  return gains;
  }

// calculates the entropy for all the attributes together (good instances to bad instances)
  private double entropy(List<Instance> instances)
  {
	  List<Integer> labelsCounts = new ArrayList<Integer>(labels.size());
	  for(int i = 0; i < labels.size(); i++)
		  labelsCounts.add(0);
	  
	  for(Instance instance : instances)
	  {
		  int labelIndex = getLabelIndex(instance.label); // increments the labelCount in 1 according to the label of the instance
		  int k = labelsCounts.get(labelIndex);
		  labelsCounts.set(labelIndex, k+1);  
	  }
	  
	  return calcEntropy(labelsCounts);
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
  
  @Override
  public String classify(Instance instance) {
	DecTreeNode node = this.root;
	
	while(!node.terminal)
	{	
		boolean valueFound = false; // sets true when the loop finds the wanted value for the instance's attribute
		for (int i = 0; i < node.children.size(); i++)
		{
			DecTreeNode child = node.children.get(i);
			
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

@Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    List<Instance> instances = train.instances;
    Map<String, Double> gains = calculateGains(attributes, attributeValues, instances);
	for(int i = 0; i < gains.size(); i++) {
		System.out.format("%s %.5f\n", attributes.get(i), gains.get(attributes.get(i)));
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