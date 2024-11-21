package org.nasdanika.models.family.demos.mapping;

import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;

import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.drawio.emf.SemanticDrawioFactory;
import org.nasdanika.drawio.emf.SemanticDrawioFactory.RepresentationElementFilter;

/**
 * Provides an {@link OpenAIClient} instance.  
 */
public class FamilyRepresentationElementFilterCapabilityFactory extends ServiceCapabilityFactory<SemanticDrawioFactory<?>, RepresentationElementFilter> {

	@Override
	public boolean isFor(Class<?> type, Object requirement) {
		return RepresentationElementFilter.class.equals(type);
	}

	@Override
	protected CompletionStage<Iterable<CapabilityProvider<RepresentationElementFilter>>> createService(
			Class<RepresentationElementFilter> serviceType, SemanticDrawioFactory<?> serviceRequirement,
			BiFunction<Object, ProgressMonitor, CompletionStage<Iterable<CapabilityProvider<Object>>>> resolver,
			ProgressMonitor progressMonitor) {
		
		return wrap(new FamilyRepresentationElementFilter());
	}

}
