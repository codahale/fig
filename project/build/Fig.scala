import sbt._

class Fig(info: ProjectInfo) extends DefaultProject(info)
                                     with IdeaProject
                                     with posterous.Publish
                                     with maven.MavenDependencies {
  /**
   * Publish the source as well as the class files.
   */
  override def packageSrcJar = defaultJarPath("-sources.jar")
  val sourceArtifact = Artifact.sources(artifactID)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  /**
   * Publish to my repo.
   */
  lazy val publishTo = Resolver.sftp("Personal Repo",
                                     "codahale.com",
                                     "/home/codahale/repo.codahale.com/")

  /**
   * Repos
   */
  val codasRepo = "codahale.com" at "http://repo.codahale.com/"

  /**
   * Dependencies
   */
  val jerkson = "com.codahale" %% "jerkson" % "0.1.2"

  /**
   * Test Dependencies
   */
  val simplespec = "com.codahale" %% "simplespec" % "0.2.0" % "test" withSources()
}
