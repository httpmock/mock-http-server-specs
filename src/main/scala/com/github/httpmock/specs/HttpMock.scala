package com.github.httpmock.specs

import com.github.httpmock.dto.RequestDto
import com.github.httpmock.rules.{MockService, MockVerifyException, StandaloneMockServer, Stubbing}
import com.github.httpmock.times.Times
import com.github.httpmock.{MockServer, PortUtil}
import org.specs2.mutable
import org.specs2.specification.{After, Fragments, Scope, Step}

trait BeforeAllAfterAll extends mutable.Specification {
  override def map(fragments: => Fragments) =
    Step(beforeAll) ^ fragments ^ Step(afterAll)

  protected def beforeAll()

  protected def afterAll()
}

trait HttpMockServer extends BeforeAllAfterAll {
  var mockServer: MockServer = null

  def createMock() = {
    val mockService = new MockService(baseUri, "/mockserver")
    mockService.create()
    mockService
  }

  def baseUri = mockServer.getBaseUri

  override def beforeAll {
    val ports = PortUtil.getRandomPorts(3)
    import com.github.httpmock.ConfigurationBuilder._
    mockServer = new StandaloneMockServer(config().httpPort(ports.get(0)).stopPort(ports.get(1)).ajpPort(ports.get(2)).build())
    mockServer.start()
  }

  override def afterAll {
    mockServer.stop()
  }
}

class HttpMock(val mockService: MockService) extends Scope with After {

  def this(mockServer: HttpMockServer) {
    this(mockServer.createMock())
  }

  override def after: Unit = {
    deleteMock()
  }

  def deleteMock() {
    mockService.delete()
  }

  def when(request: RequestDto) = new Stubbing(mockService, request)

  def verify(request: RequestDto, times: Times) {
    val numberOfCalls = getNumberOfCalls(request)
    if (!times.matches(numberOfCalls)) {
      val expected = times.getFailedDescription
      throw new MockVerifyException(s"Mock verification failed. Request was called $numberOfCalls times but should have been called $expected")
    }
  }

  private def getNumberOfCalls(request: RequestDto) = verifyResponse(request).getTimes

  private def verifyResponse(request: RequestDto) = mockService.verify(request)

  def requestUrl = mockService.getRequestUrl

}
