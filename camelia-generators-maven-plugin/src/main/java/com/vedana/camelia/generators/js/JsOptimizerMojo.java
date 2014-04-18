package com.vedana.camelia.generators.js;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

import com.vedana.camelia.generator.js.parser.JsOptimizer;

/**
 * 
 * @author JBM
 * @version
 */

@Mojo(name = "jar", defaultPhase = LifecyclePhase.PACKAGE)
public class JsOptimizerMojo extends AbstractMojo {

	@Parameter(defaultValue = "c1", required = true)
	protected String levels;
	
	@Parameter(defaultValue = JSF_VERSION_22, required = true)
	protected String jsfVersion;
	
	@Parameter(required = true)
	protected String sources;
	
	@Parameter()
	protected String symbols;
	
	@Parameter(defaultValue="html")
	protected String renderkit;
	
	@Parameter(defaultValue = "${basedir}/src/")
	protected String sourceDirectory;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources/src-", required = true)
	protected File outputDirectory;

	@Parameter(defaultValue = "${project.build.directory}/classes/", required = true)
	protected File classes;

	@Parameter(property = "project.build.finalName")
	private String finalName;
	
	@Parameter( defaultValue = "${basedir}/src/META-INF/MANIFEST.MF", required = true)
	private File defaultManifestFile;

	private final static String JSF_VERSION_12 = "1.2";
	private final static String JSF_VERSION_22 = "2.2.4";
	
	private final static String C3_CLASSIFIER = "c3";
	private final static String C2_CLASSIFIER = "c2";

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
			String[] classifiers = levels.split(",");

			for (String classifier : classifiers) {
				
				classifier = classifier.trim();
				File innerDestDir = new File(getOutputDirectory() + classifier);
				innerDestDir.mkdirs();

				if (innerDestDir.exists()) {
					
					archive.setManifestFile(defaultManifestFile);
					
					optimize(classifier);
					
					archive.addManifestEntry("Rcfaces-Javascript-Level", classifier);
					
					File outputFile = generateArchive(innerDestDir, finalName
							+ "-" + classifier + ".jar");

					projectHelper.attachArtifact(project, outputFile,
							classifier);
				}

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
	
	
	private void optimize(String classifier) throws Exception {
		ArrayList<String> arguments = new ArrayList<String>();
		
		arguments.add("-source:"+renderkit);
		arguments.add(sourceDirectory+sources);
		
		arguments.add("-dest:"+renderkit);
		arguments.add(outputDirectory.getPath()+classifier+"/"+sources);
		
		arguments.add("-renderkit");
		arguments.add(renderkit);
		
		arguments.add("-extension");
		arguments.add("js");
		
		arguments.add("-label");
		arguments.add("level-"+classifier);
		
		if(symbols != null) {
			arguments.add("-symbols");
			arguments.add("../"+symbols +"/target/generated-sources/src-" 
					+classifier+"/"+symbols.replace('.', '/')+"/internal/javascript/symbols" );
		}
		
		if(classifier.equals(C3_CLASSIFIER) || classifier.equals(C2_CLASSIFIER)) {
			arguments.add("+mergeVariables");
			if (renderkit.equals("html")) {
				arguments.add("+resolveSuper");
				arguments.add("+inlineAspects");
			} else {
				arguments.add("-resolveSuper");
				arguments.add("-inlineAspects");
			}
			
		} else {
			arguments.add("-mergeVariables");
			arguments.add("-resolveSuper");
			arguments.add("-inlineAspects");
		}
		
		if(classifier.equals(C3_CLASSIFIER)) {
			arguments.add("+multiWindow");
		} else {
			arguments.add("-multiWindow");
		}
		
		new JsOptimizer().files(arguments.toArray(new String[0]));
		
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
			archiver.getArchiver().addDirectory(classes,
					new String[] { "**/**" },
					new String[] { "**/*.js", "**/symbols", "**/old" });
		}

		try {
			// we don't want Maven stuff
			archive.setAddMavenDescriptor(false);
			
			HashMap<String, String> entries = new HashMap<String, String>();
			entries.put("jsfVersion", jsfVersion);
			
			if(jsfVersion.equals(JSF_VERSION_12)) {
				entries.put("Build-Jdk", "1.5");
			}
			
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
		return outputDirectory.getAbsoluteFile().toString();
	}

}
