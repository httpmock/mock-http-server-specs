package com.github.httpmock.specs

import com.github.httpmock.api.times.Times
import com.github.httpmock.api.{Stubbing, MockService, MockVerifyException}
import com.github.httpmock.dto.RequestDto
import org.specs2.specification.{After, Scope}

class HttpMock(val mockService: MockService) extends Scope with After {

  def this(mockServer: HttpMockServer) {
    this(mockServer.createMock())
  }

  override def after {
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
