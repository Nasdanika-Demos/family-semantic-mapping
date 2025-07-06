package org.nasdanika.models.family.demos.mapping.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;
import org.nasdanika.ai.emf.DoubleEObjectGraphMessageProcessor;
import org.nasdanika.ai.emf.EObjectGraphMessageProcessor;
import org.nasdanika.ai.emf.EObjectGraphMessageProcessor.Collector;
import org.nasdanika.ai.emf.EObjectGraphMessageProcessor.Message;
import org.nasdanika.ai.emf.similarity.DoubleEStructuralFeatureSimilarity;
import org.nasdanika.ai.emf.similarity.DoubleEStructuralFeatureVectorMessageCollectorSimilarityConnectionFactory;
import org.nasdanika.ai.emf.similarity.DoubleEStructuralFeatureVectorSimilarityConnection;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.common.Util;
import org.nasdanika.graph.Connection;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.Node;
import org.nasdanika.graph.emf.EClassConnection;
import org.nasdanika.graph.emf.EObjectNode;
import org.nasdanika.graph.emf.EReferenceConnection;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.models.family.FamilyPackage;
import org.nasdanika.models.family.Person;

public class TestFamilySimilarity {
	
	@Test
	public void testSyncMessageProcessing() throws Exception {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
				
		File familyDiagramFile = new File("family.drawio").getCanonicalFile();
		Resource familyResource = resourceSet.getResource(URI.createFileURI(familyDiagramFile.getAbsolutePath()), true);
		
		EObjectGraphMessageProcessor<String,Void,Void> messageProcessor = new EObjectGraphMessageProcessor<>(false, familyResource.getContents(), progressMonitor) {
			
			@Override
			protected boolean test(Message<String> message, ProgressMonitor tpm) {
				if (message.depth() > 20) {
					return false;
				}
				Element sender = message.sender();				
				if (sender instanceof EReferenceConnection) {
					EReferenceConnection refConn = (EReferenceConnection) sender;
					EReference ref = refConn.get().reference();
					EPackage pkg = ref.getEContainingClass().getEPackage();
					return pkg == FamilyPackage.eINSTANCE;
				}
				if (sender instanceof EObjectNode) {
					return ((EObjectNode) sender).get() instanceof Person;
				}
				Element recipient = message.recipient();				
				if (recipient instanceof EObjectNode) {
					return ((EObjectNode) recipient).get() instanceof Person;
				}
				return false; // super.test(message);
			}
			
		};
		
		// Sender -> receiver:counter
		Map<Element, Map<Node,AtomicInteger>> counters = new ConcurrentHashMap<>();
		
		Collector<String> collector = new Collector<String>() {
			
			@Override
			public void outgoing(Node node, Connection connection, Message<String> input, ProgressMonitor progressMonitor) {
				Element rootSender = input.rootSender().sender();
				if (rootSender != null) {
					counters.computeIfAbsent(rootSender, e -> new ConcurrentHashMap<>()).computeIfAbsent(node, n -> new AtomicInteger()).incrementAndGet();
				}
			}
			
			@Override
			public void initial(Node node, String value) {
				System.out.println("Initial " + node);				
			}
			
			@Override
			public void incoming(Node node, Connection connection, Message<String> input, ProgressMonitor progressMonitor) {
				Element rootSender = input.rootSender().sender();
				if (rootSender != null) {
					counters.computeIfAbsent(rootSender, e -> new ConcurrentHashMap<>()).computeIfAbsent(node, n -> new AtomicInteger()).incrementAndGet();
				}
			}
			
		};
		Function<Map<Element, ProcessorInfo<BiFunction<Message<String>, ProgressMonitor, Void>>>, Stream<BiFunction<Message<String>, ProgressMonitor, Void>>> selector = processors -> {
			return processors
					.entrySet()
					.stream()
					.filter(e -> {
						Element key = e.getKey();
						if (key instanceof EObjectNode) {
							EObjectNode eObjNode = (EObjectNode) key;
							return eObjNode.get() instanceof Person && ((Person) eObjNode.get()).getName().equals("Katell");
						}
						return false;
					})
					.map(Map.Entry::getValue)
					.map(pr -> pr.getProcessor());
			
		};
		BiFunction<Message<String>, ProgressMonitor, Message<String>> messageTransformer = null;		
		messageProcessor.processes(
				"Hello", 
				selector, 
				messageTransformer,
				collector, 
				progressMonitor);

		for (Entry<Element, Map<Node, AtomicInteger>> se: counters.entrySet()) {			
			Element key = se.getKey();
			if (key instanceof EObjectNode) {
				EObject sObj = ((EObjectNode) key).get();
				if (sObj instanceof Person) {
					System.out.println(((Person) sObj).getName());
					for (Entry<Node, AtomicInteger> ne: se.getValue().entrySet()) {
						Node nKey = ne.getKey();
						if (key instanceof EObjectNode) {
							EObject nObj = ((EObjectNode) nKey).get();
							if (nObj instanceof Person) {
								System.out.println("\t" + ((Person) nObj).getName() + ": " + ne.getValue());
							}
						}			
					}
				}
			}			
		}
		
	}
		
	@Test
	public void testDoubleSyncMessageProcessing() throws Exception {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
				
		File familyDiagramFile = new File("family.drawio").getCanonicalFile();
		Resource familyResource = resourceSet.getResource(URI.createFileURI(familyDiagramFile.getAbsolutePath()), true);
		
		DoubleEObjectGraphMessageProcessor<Void> messageProcessor = new DoubleEObjectGraphMessageProcessor<>(false, familyResource.getContents(), progressMonitor) {
			
			@Override
			protected Double getOutgoingConnectionWeight(Connection connection) {
				return null;
			}
			
			@Override
			protected Double getIncomingConnectionWeight(Connection connection) {
				return null;
			}
			
			@Override
			protected Double getIncomingEReferenceWeight(EReference eReference) {
				return null;
			}
			
			@Override
			protected Double getOutgoingEReferenceWeight(EReference eReference) {
				return eReference == FamilyPackage.Literals.PERSON__PARENTS ? 1.0 : null;
			}
			
			@Override
			protected Double getConnectionMessageValue(BiFunction<Connection, Boolean, Double> state,
					Connection activator, boolean incomingActivator, Node sender, Connection recipient,
					boolean incomingRrecipient, Message<Double> parent, ProgressMonitor progressMonitor) {
				
				Double connectionMessageValue = super.getConnectionMessageValue(
						state, 
						activator, 
						incomingActivator, 
						sender, 
						recipient, 
						incomingRrecipient,
						parent, 
						progressMonitor);
				
				if (connectionMessageValue != null) {
					return 0.8 * connectionMessageValue;
				}
				
				return connectionMessageValue;
			}
			
		};
		
		// Sender -> receiver:counter
		Map<Element, Map<Node,double[]>> counters = new ConcurrentHashMap<>();
		
		Collector<Double> collector = new Collector<Double>() {
			
			@Override
			public void outgoing(Node node, Connection connection, Message<Double> input, ProgressMonitor progressMonitor) {
				Element rootSender = input.rootSender().sender();
				if (rootSender != null) {
					counters.computeIfAbsent(rootSender, e -> new ConcurrentHashMap<>()).computeIfAbsent(node, n -> new double[] { 0.0 })[0] += input.value();
				}
			}
			
			@Override
			public void initial(Node node, Double value) {
				System.out.println("Initial " + node + ": " + value);				
			}
			
			@Override
			public void incoming(Node node, Connection connection, Message<Double> input, ProgressMonitor progressMonitor) {
				Element rootSender = input.rootSender().sender();
				if (rootSender != null) {
					counters.computeIfAbsent(rootSender, e -> new ConcurrentHashMap<>()).computeIfAbsent(node, n -> new double[] { 0.0 })[0] += input.value();
				}
			}
			
		};
		Function<Map<Element, ProcessorInfo<BiFunction<Message<Double>, ProgressMonitor, Void>>>, Stream<BiFunction<Message<Double>, ProgressMonitor, Void>>> selector = processors -> {
			return processors
					.entrySet()
					.stream()
					.filter(e -> {
						Element key = e.getKey();
						if (key instanceof EObjectNode) {
							EObjectNode eObjNode = (EObjectNode) key;
							return eObjNode.get() instanceof Person && ((Person) eObjNode.get()).getName().equals("Alan");
						}
						return false;
					})
					.map(Map.Entry::getValue)
					.map(pr -> pr.getProcessor());
			
		};
		BiFunction<Message<Double>, ProgressMonitor, Message<Double>> messageTransformer = null;		
		messageProcessor.processes(
				1.0, 
				selector, 
				messageTransformer,
				collector, 
				progressMonitor);

		for (Entry<Element, Map<Node, double[]>> se: counters.entrySet()) {			
			Element key = se.getKey();
			if (key instanceof EObjectNode) {
				EObject sObj = ((EObjectNode) key).get();
				if (sObj instanceof Person) {
					System.out.println(((Person) sObj).getName());
					for (Entry<Node, double[]> ne: se.getValue().entrySet().stream().sorted((a,b) -> a.getValue()[0] > b.getValue()[0] ? -1 : 1).toList()) {
						Node nKey = ne.getKey();
						if (key instanceof EObjectNode) {
							EObject nObj = ((EObjectNode) nKey).get();
							if (nObj instanceof Person) {
								System.out.println("\t" + ((Person) nObj).getName() + ": " + ne.getValue()[0]);
							}
						}			
					}
				}
			}			
		}
		
	}	
	
	@Test
	public void testDoubleInheritanceSimilarity() throws Exception {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
				
		File familyDiagramFile = new File("family.drawio").getCanonicalFile();
		Resource familyResource = resourceSet.getResource(URI.createFileURI(familyDiagramFile.getAbsolutePath()), true);
		
		DoubleEObjectGraphMessageProcessor<Void> messageProcessor = new DoubleEObjectGraphMessageProcessor<>(false, familyResource.getContents(), progressMonitor) {
			
			/**
			 * Override this method to filter messages:
			 * - Drop long messages
			 * - Pass messages only through certain types of connections
			 *   or from/to certain types of nodes
			 */
			@Override
			protected boolean test(Message<Double> message, ProgressMonitor tpm) {
				if (message.depth() > 20 || message.value() < 0.000001) {
					return false;
				}
				Element recipient = message.recipient();				
				if (recipient instanceof EObjectNode) {
					EObject eObject = ((EObjectNode) recipient).get();
					return eObject instanceof Person || eObject instanceof EClass;
				}
				return true; 
			}
			
			/*
			 *  Customize connection weights and message values,
			 *  return null for connections which shall not be traversed
			 */  
			
			@Override
			protected Double getOutgoingConnectionWeight(Connection connection) {
				return connection instanceof EClassConnection ? 1.0 : null;
			}
			
			@Override
			protected Double getIncomingConnectionWeight(Connection connection) {
				return connection instanceof EClassConnection ? 1.0 : null;
			}
			
			@Override
			protected Double getIncomingEReferenceWeight(EReference eReference) {
				return eReference == EcorePackage.Literals.ECLASS__ESUPER_TYPES ? 1.0 : null;
			}
			
			@Override
			protected Double getOutgoingEReferenceWeight(EReference eReference) {
				return eReference == EcorePackage.Literals.ECLASS__ESUPER_TYPES ? 1.0 : null;
			}
			
			@Override
			protected Double getConnectionMessageValue(
					BiFunction<Connection, Boolean, Double> state,
					Connection activator, 
					boolean incomingActivator, 
					Node sender, 
					Connection recipient,
					boolean incomingRrecipient, 
					Message<Double> parent, 
					ProgressMonitor progressMonitor) {
				
				Double connectionMessageValue = super.getConnectionMessageValue(
						state, 
						activator, 
						incomingActivator, 
						sender, 
						recipient, 
						incomingRrecipient,
						parent, 
						progressMonitor);
				
				if (connectionMessageValue != null) {
					return 0.8 * connectionMessageValue;
				}
				
				return connectionMessageValue;
			}
			
		};
		
		// Sender -> receiver:counter
		Map<Element, Map<Node,double[]>> counters = new ConcurrentHashMap<>();
		
		Collector<Double> collector = new Collector<Double>() {
			
			@Override
			public void outgoing(Node node, Connection connection, Message<Double> input, ProgressMonitor progressMonitor) {
				Element rootSender = input.rootSender().sender();
				if (rootSender != null) {
					counters.computeIfAbsent(rootSender, e -> new ConcurrentHashMap<>()).computeIfAbsent(node, n -> new double[] { 0.0 })[0] += input.value();
				}
			}
			
			@Override
			public void initial(Node node, Double value) {
				System.out.println("Initial " + node + ": " + value);				
			}
			
			@Override
			public void incoming(Node node, Connection connection, Message<Double> input, ProgressMonitor progressMonitor) {
				Element rootSender = input.rootSender().sender();
				if (rootSender != null) {
					counters.computeIfAbsent(rootSender, e -> new ConcurrentHashMap<>()).computeIfAbsent(node, n -> new double[] { 0.0 })[0] += input.value();
				}
			}
			
		};
		Function<Map<Element, ProcessorInfo<BiFunction<Message<Double>, ProgressMonitor, Void>>>, Stream<BiFunction<Message<Double>, ProgressMonitor, Void>>> selector = processors -> {
			return processors
					.entrySet()
					.stream()
					.filter(e -> {
						Element key = e.getKey();
						if (key instanceof EObjectNode) {
							EObjectNode eObjNode = (EObjectNode) key;
							return eObjNode.get() instanceof Person && ((Person) eObjNode.get()).getName().equals("Elias");
						}
						return false;
					})
					.map(Map.Entry::getValue)
					.map(pr -> pr.getProcessor());
			
		};
		
		AtomicLong messageCounter = new AtomicLong();
		AtomicLong depthCounter = new AtomicLong();
		
		BiFunction<Message<Double>, ProgressMonitor, Message<Double>> messageTransformer = (m,p) -> {
			messageCounter.incrementAndGet();
			depthCounter.addAndGet(m.depth());
			return m;
		};
		messageProcessor.processes(
				1.0, 
				selector, 
				messageTransformer,
				collector, 
				progressMonitor);
	
		for (Entry<Element, Map<Node, double[]>> se: counters.entrySet()) {			
			Element key = se.getKey();
			if (key instanceof EObjectNode) {
				EObject sObj = ((EObjectNode) key).get();
				if (sObj instanceof Person) {
					System.out.println(((Person) sObj).getName());
					for (Entry<Node, double[]> ne: se.getValue().entrySet()) { //.stream().sorted((a,b) -> a.getValue()[0] > b.getValue()[0] ? -1 : 1).toList()) {
						Node nKey = ne.getKey();
						if (key instanceof EObjectNode) {
							EObject nObj = ((EObjectNode) nKey).get();
							if (nObj instanceof Person) {
								System.out.println("\t" + ((Person) nObj).getName() + ": " + ne.getValue()[0]);
							}
						}			
					}
				}
			}			
		}
		
		System.out.println(messageCounter + " / " + depthCounter);
		
	}
	
	@Test
	public void testDoubleFeatureVectorInheritanceSimilarity() throws Exception {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
				
		File familyDiagramFile = new File("family.drawio").getCanonicalFile();
		Resource familyResource = resourceSet.getResource(URI.createFileURI(familyDiagramFile.getAbsolutePath()), true);
		
		DoubleEObjectGraphMessageProcessor<Void> messageProcessor = new DoubleEObjectGraphMessageProcessor<>(false, familyResource.getContents(), progressMonitor) {
			
			/**
			 * Override this method to filter messages:
			 * - Drop long messages
			 * - Pass messages only through certain types of connections
			 *   or from/to certain types of nodes
			 */
			@Override
			protected boolean test(Message<Double> message, ProgressMonitor tpm) {
				if (message.depth() > 10 || message.value() < 0.000001) {
					return false;
				}
				Element recipient = message.recipient();				
				if (recipient instanceof EObjectNode) {
					EObject eObject = ((EObjectNode) recipient).get();
					return eObject instanceof Person || eObject instanceof EClass;
				}
				return true; 
			}
			
			/*
			 *  Customize connection weights and message values,
			 *  return null for connections which shall not be traversed
			 */  
			
			@Override
			protected Double getOutgoingConnectionWeight(Connection connection) {
				return connection instanceof EClassConnection ? 1.0 : null;
			}
			
			@Override
			protected Double getIncomingConnectionWeight(Connection connection) {
				return connection instanceof EClassConnection ? 1.0 : null;
			}
			
			@Override
			protected Double getIncomingEReferenceWeight(EReference eReference) {
				return eReference == EcorePackage.Literals.ECLASS__ESUPER_TYPES ? 1.0 : null;
			}
			
			@Override
			protected Double getOutgoingEReferenceWeight(EReference eReference) {
				return eReference == EcorePackage.Literals.ECLASS__ESUPER_TYPES ? 1.0 : null;
			}
			
			@Override
			protected Double getConnectionMessageValue(
					BiFunction<Connection, Boolean, Double> state,
					Connection activator, 
					boolean incomingActivator, 
					Node sender, 
					Connection recipient,
					boolean incomingRrecipient, 
					Message<Double> parent, 
					ProgressMonitor progressMonitor) {
				
				Double connectionMessageValue = super.getConnectionMessageValue(
						state, 
						activator, 
						incomingActivator, 
						sender, 
						recipient, 
						incomingRrecipient,
						parent, 
						progressMonitor);
				
				if (connectionMessageValue != null) {
					return 0.8 * connectionMessageValue;
				}
				
				return connectionMessageValue;
			}
			
		};
		
		DoubleEStructuralFeatureVectorMessageCollectorSimilarityConnectionFactory similarityConnectionFactory = new DoubleEStructuralFeatureVectorMessageCollectorSimilarityConnectionFactory();
		
		Function<Map<Element, ProcessorInfo<BiFunction<Message<Double>, ProgressMonitor, Void>>>, Stream<BiFunction<Message<Double>, ProgressMonitor, Void>>> selector = processors -> {
			return processors
					.entrySet()
					.stream()
					.filter(e -> {
						Element key = e.getKey();
						if (key instanceof EObjectNode) {
							EObjectNode eObjNode = (EObjectNode) key;
							return eObjNode.get() instanceof Person && ((Person) eObjNode.get()).getName().equals("Elias");
						}
						return false;
					})
					.map(Map.Entry::getValue)
					.map(pr -> pr.getProcessor());
			
		};
		
		AtomicLong messageCounter = new AtomicLong();
		AtomicLong depthCounter = new AtomicLong();
		
		BiFunction<Message<Double>, ProgressMonitor, Message<Double>> messageTransformer = (m,p) -> {
			messageCounter.incrementAndGet();
			depthCounter.addAndGet(m.depth());
			return m;
		};
		messageProcessor.processes(
				1.0, 
				selector, 
				messageTransformer,
				similarityConnectionFactory, 
				progressMonitor);
		
		Collection<DoubleEStructuralFeatureVectorSimilarityConnection> similarityConnections = similarityConnectionFactory.createSimilarityConnections();
		System.out.println(similarityConnections.size());
		Map<Node, List<DoubleEStructuralFeatureVectorSimilarityConnection>> groupedBySource = Util.groupBy(similarityConnections, Connection::getSource);
		
		for (Entry<Node, List<DoubleEStructuralFeatureVectorSimilarityConnection>> se: groupedBySource.entrySet()) {
			System.out.println(se.getKey());
			for (DoubleEStructuralFeatureVectorSimilarityConnection te: se.getValue()) {
				EObject target = te.getTarget().get();
				if (target instanceof Person) {
					DoubleEStructuralFeatureSimilarity doubleEStructuralFeatureSimilarity = te.get();
					System.out.println("\t" + ((Person) target).getName() + ": " + doubleEStructuralFeatureSimilarity.get());
					for (Entry<EStructuralFeature, Double> fe: doubleEStructuralFeatureSimilarity.getFeatureSimilarities().entrySet()) {
						System.out.println("\t\t" + fe.getKey().getName() + ": " + fe.getValue());
					}
				}
			}
		}
		
		System.out.println(messageCounter + " / " + depthCounter);
		
	}	
	
	
}
