package org.nasdanika.models.family.demos.mapping;

import java.util.concurrent.CompletionStage;

import org.nasdanika.capability.CapabilityProvider;
import org.nasdanika.capability.ServiceCapabilityFactory;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.mapping.AbstractMappingFactory;
import org.nasdanika.mapping.AbstractMappingFactory.Contributor;

/**
 * Provides an {@link OpenAIClient} instance.  
 */
public class FamilyRepresentationElementFilterCapabilityFactory extends ServiceCapabilityFactory<AbstractMappingFactory<?,?>, Contributor<?,?>> {

	@Override
	public boolean isFor(Class<?> type, Object requirement) {
		return Contributor.class.equals(type);
	}

	@Override
	protected CompletionStage<Iterable<CapabilityProvider<Contributor<?, ?>>>> createService(
			Class<Contributor<?, ?>> serviceType, 
			AbstractMappingFactory<?, ?> serviceRequirement, 
			Loader loader,
			ProgressMonitor progressMonitor) {
		return wrap(new FamilyRepresentationElementFilter());
	}

}
