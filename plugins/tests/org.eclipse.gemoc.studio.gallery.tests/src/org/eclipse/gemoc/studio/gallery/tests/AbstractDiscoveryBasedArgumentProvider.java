package org.eclipse.gemoc.studio.gallery.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.amalgam.discovery.Category;
import org.eclipse.amalgam.discovery.DiscoveryDefinition;
import org.eclipse.amalgam.discovery.InstallableComponent;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public abstract class AbstractDiscoveryBasedArgumentProvider implements ArgumentsProvider {

	public AbstractDiscoveryBasedArgumentProvider() {
		super();
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		System.out.println("Getting Discovery catalog from "+getDiscoveryCatalogURI());
		ResourceSet resSet = new ResourceSetImpl();
	    Resource resource = resSet.getResource(URI.createURI(getDiscoveryCatalogURI()), true);
	    resource.load(Collections.EMPTY_MAP);
	    DiscoveryDefinition dd = (DiscoveryDefinition) resource.getContents().get(0);
	    List<Arguments> icArgs =  new ArrayList<>();
	    for (Category cat : dd.getCategories()) {
			for( InstallableComponent ic :cat.getComponents()) {
				icArgs.add(Arguments.of(new InstallableComponentWrapper(ic)));
			}
		}
	    System.out.println("Discovery catalog contains "+icArgs.size()+" entries");
		return icArgs.stream();
	}
	
	abstract String getDiscoveryCatalogURI();

}
