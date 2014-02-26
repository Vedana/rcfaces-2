package com.vedana.camelia.generators.css;

import java.io.File;
import java.io.IOException;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.rcfaces.css.internal.CssSteadyStateParser;

@Mojo(name = "parsecss", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class CssParserMojo extends AbstractMojo {


	@Parameter(required = true, defaultValue="${basedir}/src/org/rcfaces/renderkit/html/internal/css/")
	protected String cssInputDirectory;
	
	@Parameter(required = true, defaultValue="${project.build.directory}/classes/org/rcfaces/renderkit/html/internal/css/")
	protected String cssOutputDirectory;


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


	

	public void execute() throws MojoExecutionException, MojoFailureException {
		
		try {
			String args[] = {cssOutputDirectory, cssInputDirectory };
			CssSteadyStateParser.main(args);
			
		} catch (IOException e) {
			failOnError("IOException: Error while paring css", e);
		}

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

}