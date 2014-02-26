package com.vedana.camelia.generators.js;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

import com.vedana.js.doc.camelia.CameliaJavascriptDoc;

@Mojo(name = "jsdoc", defaultPhase = LifecyclePhase.PACKAGE)
public class JsDocMojo extends AbstractMojo {


	@Parameter(required = true, defaultValue="${basedir}/src/org/rcfaces/renderkit/html/internal/javascript/")
	protected String sourcesjs;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/camelia-jsdoc", required = true)
	protected String outputDirectory;

	@Parameter(property = "project.build.finalName")
	private String finalName;
	
	@Parameter( defaultValue = "${basedir}/src/META-INF/MANIFEST.MF", required = true)
	private File defaultManifestFile;

	/**
	 * Specifies the directory where the generated jar file will be put.
	 */
	@Parameter(property = "project.build.directory")
	private String jarOutputDirectory;

	/**
	 * The archive configuration to use. See <a
	 * href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
	 * Archiver Reference</a>.
	 * 
	 * @since 2.5
	 */
	@Parameter
	private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

	@Parameter(property = "maven.javadoc.failOnError", defaultValue = "true")
	protected boolean failOnError;

	/**
	 * The Maven Project Object
	 */
	@Component
	protected MavenProject project;

	/**
	 * Used for attaching the artifact in the project.
	 */
	@Component
	private MavenProjectHelper projectHelper;
	
	

	/**
	 * The Jar archiver.
	 * 
	 * @since 2.5
	 */
	@Component(role = Archiver.class, hint = "jar")
	private JarArchiver jarArchiver;

	public void execute() throws MojoExecutionException, MojoFailureException {

		try {
			
			File out = new File(outputDirectory+"/com/vedana/js/doc/camelia");
			if (!out.exists()){
				out.mkdirs();
			}
			
			URL url=  CameliaJavascriptDoc.class.getResource("/com/vedana/js/doc/camelia/CameliaJavascriptDoc.class");
			URLConnection uc = url.openConnection();
			JarURLConnection juc = (JarURLConnection) uc;
			JarFile  jar = juc.getJarFile();
			
//			JarFile  jar = new JarFile(CameliaJavascriptDoc.class.getProtectionDomain().getCodeSource()
//				        .getLocation().getFile()) ;
			
			Enumeration<JarEntry> je=  jar.entries();
			while (je.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) je.nextElement();
				if(jarEntry.getName().contains(".js")){
					File f = new File(outputDirectory + File.separator + jarEntry.getName());
					java.io.InputStream is = jar.getInputStream(jarEntry); 
				    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
				    while (is.available() > 0) { 
				    	fos.write(is.read());
				    }
				    fos.close();
				    is.close();
				}
				
			}
			
			
			List<String> l = new ArrayList<String>();
			
			l.add("-public");
			l.add("-d");
            l.add(outputDirectory);
            l.add("-coreFiles");
            l.add(out.getAbsolutePath());
            l.add("-html");
            File inputFile = new File(sourcesjs);
            
            String args[] = (String[]) l.toArray(new String[l.size()]);
            CameliaJavascriptDoc.main(args, inputFile);
            
			
			String classifier = getClassifier();
			File innerDestDir = new File(getOutputDirectory());

			if (innerDestDir.exists()) {
				
				archive.setManifestFile(defaultManifestFile);
				
				archive.addManifestEntry("Rcfaces-Javascript-Doc", classifier);
				
				File outputFile = generateArchive(innerDestDir, finalName
						+ "-" + classifier + ".jar");

				projectHelper.attachArtifact(project, outputFile,
						classifier);
			}
			

			

		} catch (ArchiverException e) {
			failOnError("ArchiverException: Error while creating archive", e);
		} catch (IOException e) {
			failOnError("IOException: Error while creating archive", e);
		} catch (RuntimeException e) {
			failOnError("RuntimeException: Error while creating archive", e);
		} catch (Exception e) {
			failOnError("Exception: Error while optimize javascript", e);
		}

	}
	
	private String getClassifier() {
		return "jsdoc";
	}


	/**
	 * Method that creates the jar file
	 * 
	 * @param sources
	 *            the directory where the generated jar file will be put
	 * @param jarFileName
	 *            the filename of the generated jar file
	 * @return a File object that contains the generated jar file
	 * @throws ArchiverException
	 *             if any
	 * @throws IOException
	 *             if any
	 */
	private File generateArchive(File sources, String jarFileName)
			throws ArchiverException, IOException {
		File optimizeJar = new File(jarOutputDirectory, jarFileName);

		if (optimizeJar.exists()) {
			optimizeJar.delete();
		}

		MavenArchiver archiver = new MavenArchiver();
		archiver.setArchiver(jarArchiver);
		archiver.setOutputFile(optimizeJar);

		if (!sources.exists()) {
			getLog().warn(
					"JAR will be empty - no content was marked for inclusion!");
		} else {
			archiver.getArchiver().addDirectory(sources,
					new String[] { "**/**" }, new String[0]);
			
		}

		try {
			// we don't want Maven stuff
			archive.setAddMavenDescriptor(false);
			
			HashMap<String, String> entries = new HashMap<String, String>();
			
			archive.addManifestEntries(entries);
			archiver.createArchive(null, project, archive);
		} catch (ManifestException e) {
			throw new ArchiverException("ManifestException: " + e.getMessage(),
					e);
		} catch (DependencyResolutionRequiredException e) {
			throw new ArchiverException(
					"DependencyResolutionRequiredException: " + e.getMessage(),
					e);
		}

		return optimizeJar;
	}

	protected void failOnError(String prefix, Exception e)
			throws MojoExecutionException {
		if (failOnError) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new MojoExecutionException(prefix + ": " + e.getMessage(), e);
		}

		getLog().error(prefix + ": " + e.getMessage(), e);
	}


	/**
	 * @return the output directory
	 */
	protected String getOutputDirectory() {
		return outputDirectory;//.getAbsoluteFile().toString();
	}

}
