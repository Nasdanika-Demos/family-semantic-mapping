package org.nasdanika.models.family.demos.mapping;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.drawio.emf.SemanticDrawioFactory.RepresentationElementFilter;

public class FamilyRepresentationElementFilter implements RepresentationElementFilter {

	@Override
	public void filterRepresentationElement(
			ModelElement representationElement, 
			EObject semanticElement,
			Map<EObject, EObject> registry, 
			ProgressMonitor progressMonitor) {
		// TODO Auto-generated method stub
		
		// Demo of representation filtering - adding a border to Isa
		String semanticId = representationElement.getProperty("semantic-id");
		if ("fiona".equals(semanticId)) {
			representationElement.getStyle().put("imageBorder", "#0000FF");
		}		

	}

}
