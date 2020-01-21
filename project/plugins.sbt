// Comment to get more information during initialization
logLevel := Level.Warn

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.2")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.8.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")

resolvers += Resolver.bintrayRepo("givers", "maven")

addSbtPlugin("givers.vuefy" % "sbt-vuefy" % "4.1.0")

addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.2")