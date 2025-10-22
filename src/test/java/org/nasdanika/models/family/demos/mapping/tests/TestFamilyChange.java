package org.nasdanika.models.family.demos.mapping.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.change.FeatureChange;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import org.nasdanika.capability.CapabilityLoader;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.capability.ServiceCapabilityFactory.Requirement;
import org.nasdanika.capability.emf.ResourceSetRequirement;
import org.nasdanika.common.PrintStreamProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Root;
import org.nasdanika.emf.ChangeDescriptionApplier;
import org.nasdanika.mapping.MappingAdapter;
import org.nasdanika.models.family.Family;

public class TestFamilyChange {
	
	/**
	 * Demonstrates tracking changes in the mapped model and applying them to the mapping source - Draw.io diagram.
	 * @throws Exception
	 */
	@Test
	public void testChange() throws Exception {
		CapabilityLoader capabilityLoader = new CapabilityLoader();
		ProgressMonitor progressMonitor = new PrintStreamProgressMonitor();
		Requirement<ResourceSetRequirement, ResourceSet> requirement = ServiceCapabilityFactory.createRequirement(ResourceSet.class);		
		ResourceSet resourceSet = capabilityLoader.loadOne(requirement, progressMonitor);
				
		File familyDiagramFile = new File("family.drawio").getCanonicalFile();
		Resource familyResource = resourceSet.getResource(URI.createFileURI(familyDiagramFile.getAbsolutePath()), true);
				
		// Testing save
		for (EObject root: familyResource.getContents()) {
			if (root instanceof Family) {
				((Family) root).setName("Modified Sample Family");
			}
			System.out.println(root);
		}			
		
		ChangeDescriptionApplier changeDescriptionApplier = (changeDescription, document) -> {
			for (Entry<EObject, EList<FeatureChange>> oc: changeDescription.getObjectChanges()) {
				System.out.println("*** " + oc);
				Adapter mappingAdapter = EcoreUtil.getExistingAdapter(oc.getKey(), MappingAdapter.class);
				if (mappingAdapter instanceof MappingAdapter) {
					Object source = ((MappingAdapter) mappingAdapter).getSource();
					for (FeatureChange fc: oc.getValue()) {
						if ("name".equals(fc.getFeatureName())) {
							((Root) source).getModel().getPage().setName(((Family) oc.getKey()).getName());
						}						
					}
				}
			}
		};
		
		try (OutputStream out = new FileOutputStream(new File("target/modified-family.drawio"))) {
			familyResource.save(out, Map.of(ChangeDescriptionApplier.class, changeDescriptionApplier));
		}
		
	}	
	
}
