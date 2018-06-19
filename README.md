# Decision-Tree-20551
A decision tree implementation built as a final project for the course "Introduction to Artificial Intelligence" (20551) at The Open University of Israel.

## Overview
An implementation of a **decision tree** for categorical attributes and 2-class classification tasks. Constructs a decision tree object from a given *training set* and uses the decision tree for classifying instances of the *test set*.

The program offers an option to print not only the tree and the classification of the test set but also the info gain of each attribute (with regards to the root node, before the tree is being built) and the accuracy of the test set classification.

The program is written in java and the project was firstly given as a homework assignment (Homework 3 – HW3) at the University of Wisconsin–Madison in the course **CS 540 - Introduction to Artificial Intelligence.**

More details about the program can be found in the project definition and instructions document, available in both English and Hebrew.

## Usage
To run the program, run the command:

```
java HW3 <modeFlag> <trainFile> <testFile>
```

where trainFile and testFile are the names of the training and testing datasets, respectively.

modeFlag is an integer from 0 to 3, controlling what the program will output as the following:

* **0:** Print the information gain for each attribute at the root node based on the training set
* **1:** Create a decision tree from the training set and print the tree
* **2:** Create a decision tree from the training set and print the classification for each example in the test set 
* **3:** Create a decision tree from the training set and print the accuracy of the classification for the test set

## The program components and flow
The program is virtually based on a few structures:
* **labels** - A strings list contains all the labels in the dataset.
* **Attributes** – A list contains the strings of all the attributes.
* **attributeValues** – A hash map returns for each attribute string, the list of all of the possible values for the attribute.
* **Instances** – A list contains all of the instances objects of the dataset.

It should be mentioned that each instance object contains a label string and a list of strings, contain for each attribute the instance value of the attribute in the attribute's respective index.

The project is comprised of classes responsible of processing the given data set and loading its information into structures that can be easily worked with, (DataSet.java) about constructing a decision tree object and using it for classifying instances (DecisionTreeImpl.java) and about representing a single tree node object (DecTreeNode.java) and a single instance object. (Instance.java) We also have a class handles the main method called HW3.java. (Which stands for Homework 3)
