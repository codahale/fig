import sbt._

class FigProject(info: ProjectInfo) extends DefaultProject(info)
                                     with IdeaProject
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
  val simplespec = "com.codahale" %% "simplespec" % "0.3.2" % "test"
  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)
}
