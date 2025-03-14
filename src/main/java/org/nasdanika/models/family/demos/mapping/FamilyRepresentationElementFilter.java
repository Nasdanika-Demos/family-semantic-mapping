package org.nasdanika.models.family.demos.mapping;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.Element;
import org.nasdanika.drawio.ModelElement;
import org.nasdanika.drawio.emf.AbstractDrawioFactory.RepresentationElementFilter;

public class FamilyRepresentationElementFilter implements RepresentationElementFilter<EObject> {

	@Override
	public boolean canHandle(Object source, EObject target) {
		return source instanceof ModelElement;
	}

	@Override
	public void filterRepresentationElement(
			Element element, 
			Map<Element, ? extends EObject> registry,
			ProgressMonitor progressMonitor) {
		
		if (element instanceof ModelElement) {
			// Demo of representation filtering - adding a border to Fiona
			ModelElement modelElement = (ModelElement) element;
			String semanticId = modelElement.getProperty("id");
			if ("fiona".equals(semanticId)) {
				modelElement.getStyle().put("imageBorder", "#0000FF");
			}		
		}		
	}

}
