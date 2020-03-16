import sbt.{Configuration, config, IntegrationTest, Test, Runtime}

object Config {

  val EndToEndTest = config("e2e") extend Runtime

  val all: Seq[Configuration] = Seq(Test, IntegrationTest, EndToEndTest)
}
