package basic

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class BasicExampleSimulation extends Simulation {

        val httpProtocol = http
                .baseURL("http://www.healthkart.com")
                .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
                .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .acceptEncodingHeader("gzip, deflate")
                .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
                .disableFollowRedirect

        val headers_1 = Map(
                "Keep-Alive" -> "115")
                
        val headers_2 = Map(
            "Cache-Control" -> """no-cache""",
            "Content-Type" -> """application/x-www-form-urlencoded; charset=UTF-8""",
            "Pragma" -> """no-cache""",
            "X-Requested-With" -> """XMLHttpRequest"""
            )        

        val headers_3 = Map(
                "Keep-Alive" -> "115",
                "Content-Type" -> "application/x-www-form-urlencoded")

        val headers_6 = Map(
                "Accept" -> "application/json, text/javascript, */*; q=0.01",
                "Keep-Alive" -> "115",
                "X-Requested-With" -> "XMLHttpRequest")
				        val scn = scenario("Scenario name")
                .group("Login") {
                        exec(
                                http("request_1")
                                        .get("/")
                                        .headers(headers_1)
                                        .check(status.is(200))
                                  )                                        
                                .pause(10)             
                                .exec(
                                      http("submit login")
                                     .post("/core/auth/Login.action")
                                     .headers(headers_2)
                                     .param("""loginName""", "nitin.wadhawan@healthkart.com")
                                     .param("""password""", "123456"))
                                     .pause(2,3)
                       
                        }.exec(session => {
                          println(session)
                          session
                        })				
				 .pause(0 milliseconds, 100 milliseconds)
                .repeat(1) {
                                .exec(
                                        http("request_5")
                                                .get("/product/muscleblaze-amino-2288/NUT3179?productReferrerId=11")
                                                .headers(headers_1))
                                .pause(100 milliseconds, 200 milliseconds)
                                .exec(
                                        http("Add to cart")
                                               .post("/core/cart/AddToCart.action")
                                               .param("productVariantList[0]", "NUT3179-01")
                                               .param("productVariantList[0].qty", "1")
                                                .headers(headers_6))
                                .pause(4, 5)
                                .exec(
                                        http("request_7")
                                                .get("/product/dymatize-elite-mass/NUT922?productReferrerId=23&productPosition=1/2")
                                                .headers(headers_1))
                                .pause(100 milliseconds, 200 milliseconds)
                                .exec(
                                        http("request_8")
                                                .get("/sports-nutrition/protein/casein-protein")
                                                .headers(headers_6))
                                .pause(6, 7)
                }.exec(
                        http("request_9")
                                .get("/")
                                .headers(headers_1)
                                .check(status.is(200)))
                .pause(0 milliseconds, 100 milliseconds)


        setUp(scn.inject(ramp(1 users) over (1 seconds)))
                .protocols(httpProtocol)

}


