package com.github.httpmock.specs

import com.github.httpmock.MockServer
import com.github.httpmock.api.MockService
import com.github.httpmock.exec.{PortUtil, Configuration, StandaloneMockServer}

trait HttpMockServer extends BeforeAllAfterAll {
  var mockServer: MockServer = null

  def createMock() = {
    val mockService = new MockService(baseUri, "/mockserver")
    mockService.create()
    mockService
  }

  def baseUri = mockServer.getBaseUri

  override def beforeAll {
    mockServer = new StandaloneMockServer(randomPortsConfig)
    mockServer.start()
  }

  def randomPortsConfig: Configuration = {
    val ports = PortUtil.getRandomPorts(3)
    import com.github.httpmock.exec.ConfigurationBuilder._
    config()
      .httpPort(ports.get(0))
      .stopPort(ports.get(1))
      .ajpPort(ports.get(2)).build()
  }

  override def afterAll {
    mockServer.stop()
  }
}
