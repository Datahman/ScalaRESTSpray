resolvers += Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/eed3si9n/sbt-plugins/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.4.0")

