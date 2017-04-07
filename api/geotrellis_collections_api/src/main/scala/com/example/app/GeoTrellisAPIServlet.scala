package com.example.app

import org.scalatra._

class GeoTrellisAPIServlet extends Geotrellis_collections_apiStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
