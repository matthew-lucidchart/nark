package com.lucidchart.open.nark.request

import play.api.mvc.Request
import play.api.mvc.WrappedRequest
import play.api.Play
import play.api.Play.current

class AppRequest[A](val request: Request[A]) extends WrappedRequest(request) {
}
