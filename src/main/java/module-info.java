import org.nasdanika.capability.CapabilityFactory;
import org.nasdanika.models.family.demos.mapping.FamilyRepresentationElementFilterCapabilityFactory;

module org.nasdanika.demos.family.mapping {
		
	requires transitive org.nasdanika.models.family.processors;
	
	provides CapabilityFactory with FamilyRepresentationElementFilterCapabilityFactory;
	
}
