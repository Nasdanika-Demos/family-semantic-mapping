package org.nasdanika.models.family.demos.mapping.tests;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.Test;
import org.nasdanika.common.Context;
import org.nasdanika.common.Diagnostic;
import org.nasdanika.common.ExecutionException;
import org.nasdanika.common.MutableContext;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.html.model.app.gen.AppSiteGenerator;
import org.nasdanika.models.family.Person;
import org.nasdanika.models.family.processors.doc.FamilyHtmlAppGenerator;
import org.nasdanika.models.family.processors.doc.FamilyNodeProcessorFactory;
import org.nasdanika.models.family.util.FamilyDrawioResource;
import org.nasdanika.models.family.util.FamilyDrawioResourceFactory;

public class TestFamilyMappingSiteGen {
	
	@Test
	public void testGenerateFamilySiteWithMapping() throws Exception {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("drawio", new FamilyDrawioResourceFactory(uri -> (Person) resourceSet.getEObject(uri, true)) {
			
			@Override
			protected void filterRepresentationElement(
					org.nasdanika.drawio.ModelElement representationElement, 
					EObject semanticElement,
					Map<EObject, EObject> registry,
					ProgressMonitor progressMonitor) {
				
				// Demo of representation filtering - adding a border to Isa
				if ("isa".equals(representationElement.getProperty("semantic-id"))) {
					representationElement.getStyle().put("imageBorder", "default");
				}
								
			}
			
			@Override
			protected String getProperty(FamilyDrawioResource resource, String name) {
				if ("alan".equals(name)) {
					return "Alan";
				}
				return super.getProperty(resource, name);
			}
			
		});
		
		File familyDiagramFile = new File("family.drawio").getCanonicalFile();
		Resource familyResource = resourceSet.getResource(URI.createFileURI(familyDiagramFile.getAbsolutePath()), true);
		
		// Generating an action model
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		MutableContext context = Context.EMPTY_CONTEXT.fork();
		
		Consumer<Diagnostic> diagnosticConsumer = d -> d.dump(System.out, 0);		
		
		File actionModelsDir = new File("target\\action-models\\");
		actionModelsDir.mkdirs();
								
		File output = new File(actionModelsDir, "family-actions.xmi");
		
		FamilyHtmlAppGenerator htmlAppGenerator = new FamilyHtmlAppGenerator(
				familyResource.getContents().get(0),
				new FamilyNodeProcessorFactory(context, null));
		
		htmlAppGenerator.generateHtmlAppModel(
				diagnosticConsumer, 
				output,
				progressMonitor);
				
		// Generating a web site
		String rootActionResource = "family-actions.yml";
		URI rootActionURI = URI.createFileURI(new File(rootActionResource).getAbsolutePath());//.appendFragment("/");
		
		String pageTemplateResource = "page-template.yml";
		URI pageTemplateURI = URI.createFileURI(new File(pageTemplateResource).getAbsolutePath());//.appendFragment("/");
		
		String siteMapDomain = "https://family.models.nasdanika.org/demos/mapping";		
		AppSiteGenerator actionSiteGenerator = new AppSiteGenerator() {
			
			@Override
			protected Context createContext(ProgressMonitor progressMonitor) {
				return context;
			}
			
		};		
		
		Map<String, Collection<String>> errors = actionSiteGenerator.generate(
				rootActionURI, 
				pageTemplateURI, 
				siteMapDomain, 
				new File("../../docs/demos/mapping"), // Publishing to the repository's docs directory for GitHub pages 
				new File("target/family-doc-site-work-dir"), 
				true);
				
		int errorCount = 0;
		for (Entry<String, Collection<String>> ee: errors.entrySet()) {
			System.err.println(ee.getKey());
			for (String error: ee.getValue()) {
				System.err.println("\t" + error);
				++errorCount;
			}
		}
		
		System.out.println("There are " + errorCount + " site errors");
		
		if (errorCount != 10) {
			throw new ExecutionException("There are problems with pages: " + errorCount);
		}				
	}
	
}
