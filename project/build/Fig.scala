import sbt._

class Fig(info: ProjectInfo) extends DefaultProject(info)
                                     with IdeaProject
                                     with posterous.Publish
                                     with rsync.RsyncPublishing {
  /**
   * Publish the source as well as the class files.
   */
  override def packageSrcJar= defaultJarPath("-sources.jar")
  val sourceArtifact = Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  /**
   * Publish via rsync.
   */
  def rsyncRepo = "codahale.com:/home/codahale/repo.codahale.com"

  /**
   * Repos
   */
  val codasRepo = "codahale.com" at "http://repo.codahale.com/"

  /**
   * Dependencies
   */
  val jerkson = "com.codahale" %% "jerkson" % "0.0.1-SNAPSHOT" withSources() intransitive()
  val paranamer = "com.thoughtworks.paranamer" % "paranamer" % "2.3" withSources() intransitive()
  val liftJson = "net.liftweb" % "lift-json_2.8.0" % "2.2-M1" withSources() intransitive()

  /**
   * Test Dependencies
   */
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.6" % "test" withSources()
  val simplespec = "com.codahale" %% "simplespec" % "0.2.0" % "test" withSources()
}
