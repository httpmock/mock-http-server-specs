package com.github.httpmock.specs

import org.specs2.mutable.Specification
import org.specs2.specification.{Step, Fragments}

trait BeforeAllAfterAll extends Specification {
  override def map(fragments: => Fragments) =
    Step(beforeAll) ^ fragments ^ Step(afterAll)

  protected def beforeAll()

  protected def afterAll()
}
