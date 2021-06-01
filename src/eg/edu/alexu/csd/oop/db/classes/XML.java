package eg.edu.alexu.csd.oop.db.classes;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import javafx.util.Pair;

public class XML {
private CurrentDatabase current = CurrentDatabase.getInstance();

	public void createXML(String tableName) {
		File file = new File(current.getPath() + System.getProperty("file.separator") + tableName + ".xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
			try {
				DocumentBuilder builder = dbf.newDocumentBuilder();
				Document doc = builder.newDocument();
				Element name = doc.createElement(tableName);
				doc.appendChild(name);
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult(file);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				
				try {
					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	
	public void createXSD(String tableName , HashMap<String,String> map) {
		
		File file = new File(current.getPath() + System.getProperty("file.separator") + tableName + ".xsd");
		String xsd = "xs:";
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element schema = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, xsd + "schema");
			doc.appendChild(schema);
			Element names = doc.createElement(xsd + "element");
			names.setAttribute("name", tableName);
			Element complex = doc.createElement("complexType");
			names.appendChild(complex);
			schema.appendChild(names);
			Element sequence = doc.createElement("sequence");
			complex.appendChild(sequence);
			
			for(Entry<String,String> iterator : map.entrySet()) {
				
				Element col = doc.createElement(xsd + "element");
				col.setAttribute("name", iterator.getKey());
				col.setAttribute("type", xsd + iterator.getValue());
				sequence.appendChild(col);
			}
			
			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(file);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public HashMap<String, String> readXSD(String tableName){
		HashMap<String,String> Data = new HashMap<String,String>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(current.getPath() + System.getProperty("file.separator") + tableName + ".xsd");
			NodeList getData = doc.getElementsByTagName("xs:element");
			for(int i=0;i<getData.getLength();i++) {
				Node col = getData.item(i);
				if(col.getNodeType() == Node.ELEMENT_NODE && col.getAttributes().getLength()>1) {
					NamedNodeMap attributes = col.getAttributes();
					String name = attributes.item(0).getNodeValue();
					String type = attributes.item(1).getNodeValue();
					Data.put(name,type);
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		for(Entry<String,String> it : Data.entrySet()) {
			System.out.println(it.getKey()+ "   " + it.getValue());
		}
		*/
		return Data;
		
	}	
	
	public int insertIntoTable(String table , HashMap<String,String> columns) {
		
		ArrayList<String> allColumns = new ArrayList<>();
		HashMap<String,String> xsdMap=readXSD(table);
		for(Entry<String,String> entry:xsdMap.entrySet()) {
			allColumns.add(entry.getKey());
		}
		File file = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
		ArrayList<String> givenColumns = new ArrayList<>();
		HashMap<String, Boolean> valid = new HashMap<>();
		boolean exists = false;
		for (Entry<String, String> entry : columns.entrySet()) {
			givenColumns.add(entry.getKey());
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(current.getPath() + System.getProperty("file.separator") + table + ".xml");
			Element root = (Element) doc.getElementsByTagName(table).item(0);
			Element row = doc.createElement("row");
			for (int i = 0; i < allColumns.size(); i++) {
				exists = false;
				for (int j = 0; j < givenColumns.size(); j++) {
					if (allColumns.get(i).equalsIgnoreCase(givenColumns.get(j))) {
						columns.put(allColumns.get(i), columns.get(givenColumns.get(j)));
						exists = true;
					}
				}
				valid.put(allColumns.get(i), exists);
			}
			for (Entry<String, Boolean> entry : valid.entrySet()) {
				if (entry.getValue()) {
					Element col = doc.createElement(entry.getKey());
					Text Value = doc.createTextNode(columns.get(entry.getKey()));
					col.appendChild(Value);
					row.appendChild(col);
				} else {
					Element col = doc.createElement(entry.getKey());
					Text Value = doc.createTextNode("null");
					col.appendChild(Value);
					row.appendChild(col);
				}
			}
			root.appendChild(row);
			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(file);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return 1;
	}
	
	public int updateTable(String table , HashMap<String,String> columns,ArrayList<Pair<String,Pair<String,String>>> conditions,String op)
			throws ParserConfigurationException, SAXException, IOException{
		
		File file = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
		int changedRows = 0;
		boolean currentRow = true;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(current.getPath() + System.getProperty("file.separator") + table + ".xml");
		HashMap <String,String> xsdMap = readXSD(table);
		ArrayList<String> allColumns = new ArrayList<>();
		for(String col : xsdMap.keySet()) {
			allColumns.add(col);
		}
		
		if(op.equals("or")) {
			for(int r=0;r < conditions.size();r++) {
				Pair<String,Pair<String,String>> condition = conditions.get(r);
				for(int h=0;h < allColumns.size();h++) {
					if(condition.getKey().equalsIgnoreCase(allColumns.get(h))) {
						NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(h));
						for(int i=0; i<conditionColumn.getLength();i++) {
							currentRow = true;
							Element  element = (Element) conditionColumn.item(i);
							if(checkCondition(condition,element)) {
								Node parent = element.getParentNode();
								NodeList childern = parent.getChildNodes();
								for(int j=0;j< childern.getLength();j++) {
									Node node = childern.item(j);
									if(node.getNodeType() == Node.ELEMENT_NODE) {
										for(int z=0; z < node.getChildNodes().getLength();z++) {
											Node textNode = node.getChildNodes().item(z);
											for(Entry<String,String> it : columns.entrySet()) {
												if(it.getKey().equalsIgnoreCase(textNode.getParentNode().getNodeName())) {
													textNode.setTextContent(columns.get(it.getKey()));
													if(currentRow) {
														changedRows++;
													}
													currentRow = false;
												
												}
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(file);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			try {
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(op.equals("and")) {
			Pair<String,Pair<String,String>> condition1 = conditions.get(0);
			Pair<String,Pair<String,String>> condition2 = conditions.get(1);
			
			NodeList condition1Column = null,condition2Column = null;
			for(int j=0;j<allColumns.size();j++) {
				if(condition1.getKey().equalsIgnoreCase(allColumns.get(j))) {
					condition1Column = doc.getElementsByTagName(allColumns.get(j));
				}
			}
			for(int j=0;j<allColumns.size();j++) {
				if(condition2.getKey().equalsIgnoreCase(allColumns.get(j))) {
					condition2Column = doc.getElementsByTagName(allColumns.get(j));
				}
			}
			
			for(int i=0;i< condition1Column.getLength();i++) {
				currentRow =true;
				Element element = (Element) condition1Column.item(i);
				if(checkCondition(condition1,element)) {
					for(int h=0;h<condition2Column.getLength();h++) {
						Element element2 = (Element) condition2Column.item(i);
						if(checkCondition(condition2,element2)) {
							Node parent = element.getParentNode();
							NodeList childern = parent.getChildNodes();
							for(int j=0;j<childern.getLength();j++) {
								Node node = childern.item(j);
								if(node.getNodeType() == Node.ELEMENT_NODE) {
									for(int z=0; z<node.getChildNodes().getLength();z++) {
										Node textNode = node.getChildNodes().item(z);
										for(Entry<String,String> it : columns.entrySet()) {
											if(it.getKey().equalsIgnoreCase(textNode.getParentNode().getNodeName())) {
												textNode.setTextContent(columns.get(it.getKey()));
												if(currentRow) {
													changedRows++;
												}
												currentRow = false;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(file);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else if(op.equals("not")) {
			Pair<String,Pair<String,String>> condition = conditions.get(0);
			for(int h=0;h<allColumns.size();h++) {
				if(condition.getKey().equalsIgnoreCase(allColumns.get(h))) {
					NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(h));
					for(int i=0;i< conditionColumn.getLength();i++) {
						currentRow = true;
						Element element = (Element) conditionColumn.item(i);
						if(!checkCondition(condition,element)) {
							Node parent = element.getParentNode();
							NodeList childern = parent.getChildNodes();
							for(int j=0;j<childern.getLength();j++) {
								Node node = childern.item(j);
								if(node.getNodeType() == Node.ELEMENT_NODE) {
									for(int z=0;z < node.getChildNodes().getLength();z++) {
										Node textNode = node.getChildNodes().item(z);
										for(Entry<String,String> it : columns.entrySet()) {
											if(it.getKey().equalsIgnoreCase(textNode.getParentNode().getNodeName())) {
												textNode.setTextContent(columns.get(it.getKey()));
												if(currentRow) {
													changedRows++;
												}
												currentRow = false;
											}
										}
									}
								}
							}
						}
					}
					
					DOMSource source = new DOMSource(doc);
					Result result = new StreamResult(file);
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer;
					try {
						transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");
						try {
							transformer.transform(source, result);
						} catch (TransformerException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (TransformerConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
			}
			
			
		}else if(op.equals("all")) {
			Set<String> colls = readXSD(table).keySet();
			ArrayList<String> cols = new ArrayList<>();
			for(String it : colls) {
				cols.add(it);
			}
			
			for(int i=0;i<cols.size();i++) {
				currentRow = true;
				Element element = (Element) doc.getElementsByTagName(cols.get(i)).item(i);
				if(element != null) {
					Node parent = element.getParentNode();
					NodeList childern = parent.getChildNodes();
					for(int j=0;j<childern.getLength();j++) {
						Node node = childern.item(j);
						if(node.getNodeType() == Node.ELEMENT_NODE) {
							//System.out.println(node.getNodeName());
							for(int z=0;z<node.getChildNodes().getLength();z++) {
								Node textNode = node.getChildNodes().item(z);
								for(Entry<String,String> it : columns.entrySet()) {
									textNode.setTextContent(columns.get(it.getKey()));
									//System.out.println(textNode.getParentNode().getNodeName());
									//System.out.println(textNode.getTextContent());
									if(currentRow) {
										changedRows++;
									}
									currentRow = false;
								}
							}
						}
					}
				}
			}
			DOMSource source = new DOMSource(doc);
			Result result = new StreamResult(file);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			Pair<String, Pair<String, String>> condition = conditions.get(0);
			for (int h = 0; h < allColumns.size(); h++) {
				if (condition.getKey().equalsIgnoreCase(allColumns.get(h))) {
					NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(h));
					for (int i = 0; i < conditionColumn.getLength(); i++) {
						currentRow = true;
						Element element = (Element) conditionColumn.item(i);
						if (checkCondition(condition, element)) {
							Node parent = element.getParentNode();
							NodeList children = parent.getChildNodes();
							for (int j = 0; j < children.getLength(); j++) {
								Node node = children.item(j);
								if (node.getNodeType() == Node.ELEMENT_NODE) {
									for (int z = 0; z < node.getChildNodes().getLength(); z++) {
										Node textNode = node.getChildNodes().item(z);
										// System.out.println(textNode.getParentNode().getNodeName());
										// System.out.println(textNode.getTextContent());
										for (Entry<String, String> entry : columns.entrySet()) {
											if (entry.getKey()
													.equalsIgnoreCase(textNode.getParentNode().getNodeName())) {
												textNode.setTextContent(columns.get(entry.getKey()));
												if (currentRow) {
													changedRows++;
												}
												currentRow = false;
											}
										}
									}
								}

							}
						}
					}
				}

				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult(file);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		return changedRows;
	}
	
	boolean satisfy(String s1, String s2, String operation) {
		System.out.println(operation);
		if(operation == "=="){
			if(s1.equals(s2))return true;
			else return false;
		}
		int x = Integer.parseInt(s1);
		int y = Integer.parseInt(s2);
		if(operation == "=")return x==y;
		else if (operation == ">") return y > x;
		else return y < x;

	}

	boolean satisfyand(NodeIterator iterator, ArrayList<Pair<String, Pair<String, String>>> conditions) {
		int ctn = 0;
		Element b = null;
		int flag =0;

			for (Node n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
				for (int i = 0; i < conditions.size(); i++) {
				Element e = (Element) n;
				if (conditions.get(i).getKey().equals(e.getTagName()) && satisfy(conditions.get(i).getValue().getKey(), e.getTextContent(), conditions.get(i).getValue().getValue())) {
					flag++;
					System.out.println("a7a");
				}
			}
		}
		if(flag == conditions.size())return true;
		else return false;
	}
	boolean satisfynormal(NodeIterator iterator, ArrayList<Pair<String, Pair<String, String>>> conditions) {

		int flag =0;

		for (Node n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
				Element e = (Element) n;
				if (conditions.get(0).getKey().equals(e.getTagName()) && satisfy(conditions.get(0).getValue().getKey(), e.getTextContent(), conditions.get(0).getValue().getValue())) {
					flag++;
					System.out.println("a7a");
				}

		}
		if(flag>0)return true;
		else return false;
	}
	boolean satisfyor(NodeIterator iterator, ArrayList<Pair<String, Pair<String, String>>> conditions) {
		int ctn = 0;
		Element b = null;
		int flag =0;

		for (Node n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
			for (int i = 0; i < conditions.size(); i++) {
				Element e = (Element) n;
				if (conditions.get(i).getKey().equals(e.getTagName()) && satisfy(conditions.get(i).getValue().getKey(), e.getTextContent(), conditions.get(i).getValue().getValue())) {
					flag++;
					System.out.println("a7a");
				}
			}
		}
		if(flag>0)return true;
		else return false;
	}
	
	
	
	
	public int deleteFromTable(String table, ArrayList<Pair<String, Pair<String, String>>> conditions, String op)
			throws ParserConfigurationException, SAXException, IOException {
		File file = new File(current.getPath() + System.getProperty("file.separator") + table + ".xml");
		int changedRows = 0;
		boolean currentRow = true;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(current.getPath() + System.getProperty("file.separator") + table + ".xml");
		
		Node root = doc.getElementsByTagName(table).item(0);
		HashMap<String,String> xsdMap = readXSD(table);
		ArrayList<String> allColumns = new ArrayList<>();
		for(String col : xsdMap.keySet()) {
			allColumns.add(col);
		}
		
		if(op.equals("or")) {
			
			for(int r=0;r<conditions.size();r++) {
				Pair<String,Pair<String,String>> condition = conditions.get(r);
				for(int j=0;j<allColumns.size();j++) {
					if(condition.getKey().equalsIgnoreCase(allColumns.get(j))) {
						NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(j));
						for(int i=0;i< conditionColumn.getLength();i++) {
							Element element = (Element) conditionColumn.item(i);
							if(checkCondition(condition,element)) {
								Node parent = element.getParentNode();
								root.removeChild(parent);
								changedRows++;
								i--;
							}
							DOMSource source = new DOMSource(doc);
							Result result = new StreamResult(file);
							TransformerFactory transformerFactory = TransformerFactory.newInstance();
							try {
								Transformer transformer = transformerFactory.newTransformer();
								transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
								transformer.setOutputProperty(OutputKeys.INDENT, "yes");
								try {
									transformer.transform(source, result);
								} catch (TransformerException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
							} catch (TransformerConfigurationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				}
			}
			
			
		}else if(op.equals("and")) {
			Pair<String,Pair<String,String>> condition1 = conditions.get(0);
			Pair<String,Pair<String,String>> condition2 = conditions.get(1);
			NodeList condition1Column = null,condition2Column = null;
			for(int j=0;j<allColumns.size();j++) {
				if(condition1.getKey().equalsIgnoreCase(allColumns.get(j))) {
					condition1Column = doc.getElementsByTagName(allColumns.get(j));
				}
			}
			
			for(int j=0;j<allColumns.size();j++) {
				if(condition2.getKey().equalsIgnoreCase(allColumns.get(j))) {
					condition2Column = doc.getElementsByTagName(allColumns.get(j));
				}
			}
			
			for(int i=0;i<condition1Column.getLength();i++) {
				Element element = (Element) condition1Column.item(i);
				if(checkCondition(condition1,element)) {
					for(int j=0;j<condition2Column.getLength();j++) {
						Element element2 = (Element) condition2Column.item(i);
						if(checkCondition(condition2,element2)) {
							Node parent = element.getParentNode();
							root.removeChild(parent);
							changedRows++;
						}
					
					}
				}
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult(file);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				try {
					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
		}else if(op.equals("not")) {
			Pair<String,Pair<String,String>> condition = conditions.get(0);
			for(int j=0;j<allColumns.size();j++) {
				if(condition.getKey().equalsIgnoreCase(allColumns.get(j))) {
					NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(j));
					for(int i=0;i<conditionColumn.getLength();i++) {
						Element element = (Element) conditionColumn.item(i);
						if(!checkCondition(condition,element)) {
							Node parent = element.getParentNode();
							root.removeChild(parent);
							changedRows++;
							i--;
						}
						DOMSource source = new DOMSource(doc);
						Result result = new StreamResult(file);
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer;
						try {
							transformer = transformerFactory.newTransformer();
							transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
							transformer.setOutputProperty(OutputKeys.INDENT, "yes");
							try {
								transformer.transform(source, result);
							} catch (TransformerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (TransformerConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}else if(op.equals("all")) {
			HashMap<String,String> map = readXSD(table);
			ArrayList<String> columns = new ArrayList<>();
			for(String col : map.keySet()) {
				columns.add(col);
			}
			
			for(int j=0;j<columns.size();j++) {
				NodeList conditionColumn = doc.getElementsByTagName(columns.get(j));
				Element element = (Element) conditionColumn.item(0);
				Node parent = element.getParentNode();
				root.removeChild(parent);
				changedRows++;
				DOMSource source = new DOMSource(doc);
				Result result = new StreamResult(file);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					try {
						transformer.transform(source, result);
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			Pair<String,Pair<String,String>> condition = conditions.get(0);
			for(int j=0;j<allColumns.size();j++) {
				if(condition.getKey().equalsIgnoreCase(allColumns.get(j))) {
					NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(j));
					for(int i=0;i<conditionColumn.getLength();i++) {
						Element element = (Element) conditionColumn.item(i);
						if(checkCondition(condition,element)) {
							Node parent = element.getParentNode();
							root.removeChild(parent);
							changedRows++;
							i--;
						}
						DOMSource source = new DOMSource(doc);
						Result result = new StreamResult(file);
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer;
						try {
							transformer = transformerFactory.newTransformer();
							transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
							transformer.setOutputProperty(OutputKeys.INDENT, "yes");
							try {
								transformer.transform(source, result);
							} catch (TransformerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (TransformerConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		
		}
		
		return changedRows;
    }
	
	
	public Object[][] selectFromTable(String table,ArrayList<Pair<String,Pair<String,String>>> conditions,ArrayList<String> columns ,String op) 
			throws ParserConfigurationException, SAXException, IOException{ // columns array contain columns to be selected if * it will contain all columns else will contain the chosen columns
		
		
		ArrayList<ArrayList<Object>> array = new ArrayList<>();
		Object[][] tableArray = null;
		HashMap<String,String> xsdMap = readXSD(table);
		ArrayList<String> allColumns = new ArrayList<>();
		for(Entry<String,String> it : xsdMap.entrySet()) {
			allColumns.add(it.getKey());
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(current.getPath() + System.getProperty("file.separator") + table + ".xml");
		
			for(int i=0; i< allColumns.size() ; i++) {
				String type = xsdMap.get(allColumns.get(i));
				NodeList columnElements = doc.getElementsByTagName(allColumns.get(i));
				ArrayList<Object> sub = new ArrayList<>();
				for(int j=0; j< columnElements.getLength();j++) {
					Node element = columnElements.item(j);
					if(element.getNodeType() == Node.ELEMENT_NODE) {
						String elementValue = element.getTextContent();
						if(type.equals("xs:varchar")) {
							sub.add(elementValue);
						}else if(type.equals("xs:int")) {
							sub.add(Integer.parseInt(elementValue));
						}
					}
				}
				array.add(sub);
			}
		
		if(op.equals("all")) {
			//select * from x 
			// select a, b from x
			// this code deal with this condition
			//System.out.println(columns.size());
			int rows = array.get(0).size();
			tableArray = new Object[rows][columns.size()];
			for(int h=0;h<allColumns.size();h++) {
				for(int i=0;i<columns.size();i++) {
					if(allColumns.get(h).equalsIgnoreCase(columns.get(i))) {
						for(int j=0;j<rows;j++) {
							tableArray[j][i] = array.get(h).get(j);
						}
					}
				}
				
			}		
			return tableArray;
		}else if(op.equals("and")) {
			
			Pair<String, Pair<String, String>> condition = conditions.get(0);
			Pair<String, Pair<String, String>> condition2 = conditions.get(1);
			NodeList condition2Column = null, conditionColumn = null;
			Set<ArrayList<Object>> result = new LinkedHashSet<>();
			
			for (int j = 0; j < allColumns.size(); j++) {
				if (condition.getKey().equalsIgnoreCase(allColumns.get(j))) {
					conditionColumn = doc.getElementsByTagName(allColumns.get(j));
				}
			}
			for (int j = 0; j < allColumns.size(); j++) {
				if (condition2.getKey().equalsIgnoreCase(allColumns.get(j))) {
					condition2Column = doc.getElementsByTagName(allColumns.get(j));
				}
			}
			
			for (int i = 0; i < conditionColumn.getLength(); i++) {
				Element element = (Element) conditionColumn.item(i);
				if (checkCondition(condition, element)) {
					for (int h = 0; h < condition2Column.getLength(); h++) {
						Element element2 = (Element) condition2Column.item(i);
						if (checkCondition(condition2, element2)) {
							Node parent = element.getParentNode();
							NodeList children = parent.getChildNodes();
							ArrayList<Object> sub = new ArrayList<>();
							for (int j = 0; j < children.getLength(); j++) {
								Node node = children.item(j);
								if (node.getNodeType() == Node.ELEMENT_NODE) {
									String type = xsdMap.get(node.getNodeName());
									for (int z = 0; z < node.getChildNodes().getLength(); z++) {
										Node textNode = node.getChildNodes().item(z);
										String elementValue = textNode.getTextContent();
										if(type.equals("xs:varchar")) {
											sub.add(elementValue);
										}else if(type.equals("xs:int")) {
											sub.add(Integer.parseInt(elementValue));
										}
										//System.out.print(elementValue +"   ");
									}
								}

							}
							//System.out.println();
							result.add(sub);
						}
						
					}
				}
			}
			//System.out.println(result.size());
			tableArray = new Object[result.size()][columns.size()];
			int cnt=0;
			int temp=-1;
			for(int i=0;i<allColumns.size();i++) {
				for(int j=0;j<columns.size();j++) {
					if(allColumns.get(i).equalsIgnoreCase(columns.get(j))) {
						int k=0;
						for(ArrayList<Object> it : result) {
								tableArray[k++][cnt] = it.get(i);
						}
						cnt++;
					}
					
				}
			}
			
			return tableArray;
			
		}else if(op.equals("or")) {
			Set<ArrayList<Object>> result = new LinkedHashSet<>();
			boolean condition1IsTrue = false;
			for (int r = 0; r < conditions.size(); r++) {
				Pair<String, Pair<String, String>> condition = conditions.get(r);
				for (int h = 0; h < allColumns.size(); h++) {
					if (condition.getKey().equalsIgnoreCase(allColumns.get(h))) {
						NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(h));
						for (int i = 0; i < conditionColumn.getLength(); i++) {
							Element element = (Element) conditionColumn.item(i);
							if (checkCondition(condition, element)) {
								Node parent = element.getParentNode();
								NodeList children = parent.getChildNodes();
								ArrayList<Object> sub = new ArrayList<>();
								for (int j = 0; j < children.getLength(); j++) {
									Node node = children.item(j);
									if (node.getNodeType() == Node.ELEMENT_NODE) {
										String type = xsdMap.get(node.getNodeName());
										for (int z = 0; z < node.getChildNodes().getLength(); z++) {
											Node textNode = node.getChildNodes().item(z);
											String elementValue = textNode.getTextContent();
											if(type.equals("xs:varchar")) {
												sub.add(elementValue);
											}else if(type.equals("xs:int")) {
												sub.add(Integer.parseInt(elementValue));
											}
										}
									}

								}
								result.add(sub);
							}
						}
					}
				}
			}
			tableArray = new Object[result.size()][columns.size()];
			int cnt=0;
			int temp=-1;
			for(int i=0;i<allColumns.size();i++) {
				for(int j=0;j<columns.size();j++) {
					if(allColumns.get(i).equalsIgnoreCase(columns.get(j))) {
						int k=0;
							for(ArrayList<Object> it : result) {
								tableArray[k++][cnt] = it.get(i);
							}
								//tableArray[k][cnt] =  result.get(k).get(i);
						cnt++;
					}
					
				}
			}
			
			return tableArray;
			
		}else if(op.equals("not")) {
			Pair<String, Pair<String, String>> condition = conditions.get(0);
			ArrayList<ArrayList<Object>> result = new ArrayList<>();
			for (int h = 0; h < allColumns.size(); h++) {
				if (condition.getKey().equalsIgnoreCase(allColumns.get(h))) {
					NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(h));
					for (int i = 0; i < conditionColumn.getLength(); i++) {
						Element element = (Element) conditionColumn.item(i);
						if (!checkCondition(condition, element)) {
							Node parent = element.getParentNode();
							NodeList children = parent.getChildNodes();
							ArrayList<Object> sub = new ArrayList<>();
							for (int j = 0; j < children.getLength(); j++) {
								Node node = children.item(j);
								if (node.getNodeType() == Node.ELEMENT_NODE) {
									String type = xsdMap.get(node.getNodeName());
									for (int z = 0; z < node.getChildNodes().getLength(); z++) {
										Node textNode = node.getChildNodes().item(z);
										String elementValue = textNode.getTextContent();
										if(type.equals("xs:varchar")) {
											sub.add(elementValue);
										}else if(type.equals("xs:int")) {
											sub.add(Integer.parseInt(elementValue));
										}
									}
								}

							}
							result.add(sub);
						}
					}
				}
			}
			
			tableArray = new Object[result.size()][columns.size()];
			int cnt=0;
			int temp=-1;
			for(int i=0;i<allColumns.size();i++) {
				for(int j=0;j<columns.size();j++) {
					if(allColumns.get(i).equalsIgnoreCase(columns.get(j))) {
						for(int k=0;k<result.size();k++) {
								tableArray[k][cnt] = result.get(k).get(i);
						}
						cnt++;
					}
					
				}
			}
			
			return tableArray;
		}else {
			// select * from x where a > 4 here code for noCondition
			Pair<String, Pair<String, String>> condition = conditions.get(0);
			ArrayList<ArrayList<Object>> result = new ArrayList<>();
			
			for (int h = 0; h < allColumns.size(); h++) {
				
				if (condition.getKey().equalsIgnoreCase(allColumns.get(h))) {
					NodeList conditionColumn = doc.getElementsByTagName(allColumns.get(h));
					for (int i = 0; i < conditionColumn.getLength(); i++) {
						
						Element element = (Element) conditionColumn.item(i);
						if (checkCondition(condition, element)) {
							Node parent = element.getParentNode();
							NodeList children = parent.getChildNodes();
							ArrayList<Object> sub = new ArrayList<>();
							
							for (int j = 0; j < children.getLength(); j++) {
								Node node = children.item(j);
								
								if (node.getNodeType() == Node.ELEMENT_NODE) {
									//System.out.println(node.getNodeName());
									String type = xsdMap.get(node.getNodeName());
									for (int z = 0; z < node.getChildNodes().getLength(); z++) {
										Node textNode = node.getChildNodes().item(z);
										String elementValue = textNode.getTextContent();
										if(type.equals("xs:varchar")) {
											sub.add(elementValue);
										}else if(type.equals("xs:int")) {
											sub.add(Integer.parseInt(elementValue));
										}
										
									}
								}

							}
							result.add(sub);
							
						}
					}
				}

			}
			tableArray = new Object[result.size()][columns.size()];
			int cnt=0;
			int temp=-1;
			for(int i=0;i<allColumns.size();i++) {
				for(int j=0;j<columns.size();j++) {
					if(allColumns.get(i).equalsIgnoreCase(columns.get(j))) {
						for(int k=0;k<result.size();k++) {
								tableArray[k][cnt] = result.get(k).get(i);
						}
						cnt++;
					}
					
				}
			}
			
			return tableArray;
			
		}
			
	}
	
	
	public boolean checkCondition(Pair<String,Pair<String,String>>condition,Element element) {
		String conditionValue = condition.getValue().getKey();
		String conditionOperator = condition.getValue().getValue();
		
		switch(conditionOperator) {
			case "==":
				if((element.getTextContent()).equalsIgnoreCase(conditionValue)) {
					return true;
				}
				break;
			case "=":
				if(element.getTextContent().equalsIgnoreCase(conditionValue)) {
					return true;
				}
				break;
			case ">":
				if(Integer.parseInt(element.getTextContent()) > Integer.parseInt(conditionValue)) {
					return true;
				}
				break;
			case "<":
				if(Integer.parseInt(element.getTextContent()) < Integer.parseInt(conditionValue)) {
					return true;
				}
				break;
		}
		
		return false;
		
	}
}
