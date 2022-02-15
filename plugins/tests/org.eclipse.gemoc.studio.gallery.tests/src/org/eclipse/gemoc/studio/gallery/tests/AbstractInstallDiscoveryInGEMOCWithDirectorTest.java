package org.eclipse.gemoc.studio.gallery.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


class AbstractInstallDiscoveryInGEMOCWithDirectorTest {

	
	static void setUpBeforeClass(String gemocStudioUrl, String workspacePath) throws Exception {
		if(new File(getDownloadedGEMOCStudioPath(workspacePath)).exists()) {
			System.out.println("Skipping download; GEMOC Studio exist in "+getDownloadedGEMOCStudioPath(workspacePath));
		} else {
			
			System.out.println("Downloading GEMOC Studio ... "+gemocStudioUrl);
			Files.createDirectories(Paths.get(workspacePath));
			URL url = new URL(gemocStudioUrl);
			ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(getDownloadedGEMOCStudioPath(workspacePath));
			fileOutputStream.getChannel()
			  .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			System.out.println("GEMOC Studio downloaded in "+getDownloadedGEMOCStudioPath(workspacePath));
		}
	}

	static void tearDownAfterClass(String workspacePath) throws Exception {
		if(new File(getDownloadedGEMOCStudioPath(workspacePath)).exists()) {
			System.out.println("Removing "+getDownloadedGEMOCStudioPath(workspacePath));
			new File(getDownloadedGEMOCStudioPath(workspacePath)).delete();
		}
	}

	void installFromDeclaredDiscovery(InstallableComponentWrapper icw, String workspacePath) throws IOException {
		
		System.out.println("Unzipping GEMOC Studio dedicated copy in "+getWorkdirPath(icw, workspacePath));
		unzipFolder(getDownloadedGEMOCStudioPath(workspacePath), new File(getWorkdirPath(icw, workspacePath)));
		Files.setPosixFilePermissions(Path.of(getWorkdirPath(icw, workspacePath)+"/GemocStudio"), PosixFilePermissions.fromString("rwxr-xr-x"));
		System.out.println("Installing "+icw.getIc());
		ArrayList<String> commandLine = new ArrayList<String>();
		commandLine.add(getWorkdirPath(icw, workspacePath)+"/GemocStudio");
		commandLine.add("-nosplash");
		commandLine.add("-application");
		commandLine.add("org.eclipse.equinox.p2.director");
		if(!icw.getIc().getSitesURLS().isEmpty()) {
			commandLine.add("-repository");
			commandLine.add(icw.getIc().getSitesURLS().stream().collect(Collectors.joining(",")));
		}
		for (String installedIU : icw.getIc().getId()) {
			commandLine.add("-installIU");
			commandLine.add(installedIU+".feature.group");
		}
		for (String hiddenInstalledIU : icw.getIc().getHiddingFeatureID()) {
			commandLine.add("-installIU");
			commandLine.add(hiddenInstalledIU+".feature.group");
		}
		
		java.lang.String[] commandLineArray = new String[commandLine.size()];
		commandLine.toArray(commandLineArray);
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Running "+commandLine.stream().collect(Collectors.joining(" ")));
		final Process process = runtime.exec(commandLineArray);

		StringBuilder stdout = new StringBuilder();
		// read Stdout
		new Thread() {
		    public void run() {
		        try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            String line = "";
		            try {
		                while((line = reader.readLine()) != null) {
		                	System.out.println(line);
		                	stdout.append(line);
		                }
		            } finally {
		                reader.close();
		            }
		        } catch(IOException ioe) {
		            ioe.printStackTrace();
		        }
		    }
		}.start();
		StringBuilder stderr = new StringBuilder();
		// read StdErr
		new Thread() {
		    public void run() {
		        try {
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		            String line = "";
		            try {
		                while((line = reader.readLine()) != null) {
		                	System.err.println(line);
		                	stderr.append(line);
		                }
		            } finally {
		                reader.close();
		            }
		        } catch(IOException ioe) {
		            ioe.printStackTrace();
		        }
		    }
		}.start();
		try {
			int i = process.waitFor();
			assertEquals(0, i);
			assertEquals("", stderr.toString(),"StdErr isn't empty");
		} catch (InterruptedException e) {
			fail(e.getMessage(), e);
		}
	}
	public static String getWorkdirPath(InstallableComponentWrapper icw, String workspacePath) {
		return workspacePath+"/"+icw.toString().replaceAll("\\W", "_");
	}

	public static String getDownloadedGEMOCStudioPath(String workspacePath) {
		return workspacePath+"/gemoc_studio-linux.gtk.x86_64.zip";
	}
	
	
	public static void unzipFolder(String zipFile, File destDir) throws IOException {
		byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                
                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
        zipEntry = zis.getNextEntry();
       }
        
        zis.closeEntry();
        zis.close();
	}
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(destinationDir, zipEntry.getName());

	    String destDirPath = destinationDir.getCanonicalPath();
	    String destFilePath = destFile.getCanonicalPath();

	    if (!destFilePath.startsWith(destDirPath + File.separator)) {
	        throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
	    }

	    return destFile;
	}


}
