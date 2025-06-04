package org.nasdanika.models.family.demos.mapping.tests;

import java.io.File;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.Test;
import org.nasdanika.ai.emf.EObjectGraphMessageProcessor;
import org.nasdanika.ai.emf.EObjectGraphMessageProcessor.Collector;
import org.nasdanika.ai.emf.EObjectGraphMessageProcessor.Message;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.graph.Connection;
import org.nasdanika.graph.Element;
import org.nasdanika.graph.Node;
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
		
		EObjectGraphMessageProcessor<String> messageProcessor = new EObjectGraphMessageProcessor<>(false, familyResource.getContents(), progressMonitor) {
			
			@Override
			protected boolean test(Message<String> message) {
				Element sender = message.sender();
				
				// TODO - person senders are OK
				
				System.out.println(sender);
				if (sender instanceof EReferenceConnection) {
					EReferenceConnection refConn = (EReferenceConnection) sender;
					EReference ref = refConn.get().reference();
					EPackage pkg = ref.getEContainingClass().getEPackage();
					return pkg == FamilyPackage.eINSTANCE;
				}
				return false; // super.test(message);
			}
			
		};
		Collector<String> collector = new Collector<String>() {
			
			@Override
			public void outgoing(Node node, Connection connection, Message<String> input, ProgressMonitor progressMonitor) {
				System.out.println("Outgoing " + node + " " + connection);				
			}
			
			@Override
			public void initial(Node node, String value) {
				System.out.println("Initial " + node);				
			}
			
			@Override
			public void incoming(Node node, Connection connection, Message<String> input, ProgressMonitor progressMonitor) {
				System.out.println("Incoming " + node + " " + connection);				
			}
			
		};
		Function<Map<Element, ProcessorInfo<BiFunction<Message<String>, ProgressMonitor, Void>>>, Stream<BiFunction<Message<String>, ProgressMonitor, Void>>> selector = processors -> {
			return processors
					.entrySet()
					.stream()
					.filter(e -> {
						Element key = e.getKey();
						if (key instanceof EObjectNode) {
							return ((EObjectNode) key).get() instanceof Person;
						}
						return false;
					})
					.map(Map.Entry::getValue)
					.map(pr -> pr.getProcessor());
			
		};
		messageProcessor.processes(
				"Hello", 
				selector, 
				collector, 
				progressMonitor);
		
		
	}
	
}
