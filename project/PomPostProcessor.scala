object PomPostProcessor {
  import scala.xml._

  // see https://groups.google.com/d/topic/simple-build-tool/pox4BwWshtg/discussion
  // adding a post pom processor to make sure that pom for salat-core correctly specifies depdency type pom for casbah dependency

  def apply(pomXML: Node): Node = {
    def rewrite(pf: PartialFunction[Node, Node])(ns: Seq[Node]): Seq[Node] = for (subnode <- ns) yield subnode match {
      case e: Elem =>
        if (pf isDefinedAt e) pf(e)
        else Elem(e.prefix, e.label, e.attributes, e.scope, rewrite(pf)(e.child): _*)
      case other => other
    }

    val rule: PartialFunction[Node, Node] = {
      case e @ Elem(prefix, "dependency", attribs, scope, children @ _*) => {
        if (children.contains(<groupId>com.github.httpmock</groupId>)) {
          Elem(prefix, "dependency", attribs, scope, children ++ <type>jar</type>: _*)
        }
        else e
      }
    }

    rewrite(rule)(pomXML.theSeq)(0)
  }
}