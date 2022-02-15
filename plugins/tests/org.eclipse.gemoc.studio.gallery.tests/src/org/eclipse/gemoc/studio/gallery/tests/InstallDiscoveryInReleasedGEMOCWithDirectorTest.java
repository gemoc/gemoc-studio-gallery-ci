package org.eclipse.gemoc.studio.gallery.tests;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;


class InstallDiscoveryInReleasedGEMOCWithDirectorTest extends AbstractInstallDiscoveryInGEMOCWithDirectorTest {

	final static String GEMOC_STUDIO_URL= "https://www.eclipse.org/downloads/download.php?file=/gemoc/packages/releases/3.4.0/gemoc_studio-linux.gtk.x86_64.zip&r=1";
	static String workspace_path = Paths.get("target","workspace").toFile().getAbsolutePath(); // default to current dir
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		setUpBeforeClass(GEMOC_STUDIO_URL, workspace_path);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		tearDownAfterClass(workspace_path);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@ParameterizedTest(name = "installFromDeclaredDiscovery[''{0}'']")
	@ArgumentsSource(DiscoveryBasedArgumentProvider_3_1.class) // adapt here to change the version of the discovery
	void installFromDeclaredDiscovery(InstallableComponentWrapper icw) throws IOException {
		installFromDeclaredDiscovery(icw, workspace_path);
		
	}
	
}
