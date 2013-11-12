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
                .baseURL("http://stagnew.healthkart.com")
                .extraInfoExtractor((status:Status, session:Session, request:Request, response: Response) => {
                List[String](request.getRawUrl())                                                   })
                .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
               .acceptHeader("application/json;q=0.9,*/*;q=0.8")
                .acceptEncodingHeader("gzip, deflate")
                .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
               .disableFollowRedirect

        val headers_1 = Map(
               """Content-Type"""-> """application/json""" ,
               """Accept"""-> """*/*""",
               """Accept-Encoding"""-> """gzip,deflate,sdch""",
               """Accept-Language"""-> """en-US,en;q=0.8"""
                )
                
        val headers_2 = Map(
            "Cache-Control" -> """no-cache""",
            "Content-Type" -> """application/x-www-form-urlencoded; charset=UTF-8""",
            "Pragma" -> """no-cache""",
            "X-Requested-With" -> """XMLHttpRequest"""
            )        

        val headers_3 = Map(
                "Keep-Alive" -> "115",
                "Content-Type" -> "application/x-www-form-urlencoded")
                
        val headers_4=Map(
          "Accept"->"""application/json""",
"Accept-Encoding" ->"""gzip,deflate,sdch""",
"Accept-Language"  ->"""en-US,en;q=0.8""",
"Connection" ->"""keep-alive""",
"Content-Type" ->"""application/json; charset=UTF-8 """ ,
"User-Agent"->"""Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.69 Safari/537.36""",
"X-Requested-With"->"""XMLHttpRequest"""
        )        

        val headers_6 = Map(
                "Accept" -> "application/json, text/javascript, */*; q=0.01",
                "Keep-Alive" -> "115",
                "X-Requested-With" -> "XMLHttpRequest")
        
                val addToCartJSON="""{"oldVariantId": "NUT2140-01","qty": "1","variantId": "37833"}"""
                  
                
                
				        val scn = scenario("Scenario name")
                .repeat(1) {
                        exec(
                                http("HK new Homepage")
                                        .get("/")
                                        .headers(headers_1)
                                        .check(status.is(200))
                                  )                                        
                                .pause(10)      
                               .feed(csv("user_credentials.csv"))
                               .exec(
                                      http("submit login")
                                     .post("/core/auth/Login.action")
                                     .param("email", "${username}")
                                     .param("type", """old""")                                     
				                     .param("password", "${password}")
                                     .param("login", """Sign In""")
                                     .check(status.is(302))
                                     .check(regex("""Login using an existing account""").notExists)
                                     .headers(headers_2))                                     
                                     .pause(2,3)
                                .exec(
                                      http("navigate to product page")
                                     .post("/sv/muscleblaze-bcaa-2000/SP-23391?navKey=VRNT-45106")
                                     .check(status.is(200))
                                     .headers(headers_2))                                     
                                     .pause(2,3)   
                                 .exec(
                                      http("Add to Cart")
                                     .post("/api/cart/productVariant/add")
                                     .body(StringBody(addToCartJSON)).asJSON
                                     .check(status.is(200))
                                     .headers(headers_1))
                                 .exec(
                                      http("Cart page")
                                     .get("/core/cart/AddToCart.action")
                                     .check(status.is(200))
                                     .headers(headers_1))
                                     .pause(2,3) 
                                  .exec(
                                      http("Address page")
                                     .get("/core/user/SelectAddress.action")
                                     .check(status.is(200))
                                     .headers(headers_1))
                                     .pause(2,3) 
                            /*      .exec(
                                      http("Select Address")
                                     .get("/core/user/SelectAddress.action")                                     
                                     .param("selectedAddress", "${address}")
                                     .check(status.is(200))
                                     .headers(headers_2))                                     
                                     .pause(2,3) */   
                                  .exec(
                                      http("Select Address")
                                     .post("/core/user/SelectAddress.action") 
                                     .param("selectedAddress", "${address}")
                                     .check(status.is(200))
                                     .headers(headers_2))                                     
                                     .pause(2,3)
                                  .exec(
                                      http("Order summary")
                                     .get("/core/order/OrderSummary.action")
                                     .check(css(".right_container>form>input", "value").saveAs("orderId"))
                                     .headers(headers_2))      
                                     .pause(2,3)   
                                  .exec(
                                      http("Confirm Payment")
                                     .post("/core/payment/CodPaymentReceive.action")
                                     .param("""order""", "${orderId}")
                                     .param("""codContactName""", """Nitin Wadhawan""")
                                     .param("""codContactPhone""", """9910444067""")
                                     .param("""pre""", """PLACE ORDER""")
                                     .check(status.is(302))
                                     .headers(headers_2))
                                     .pause(2,3)   
                                     
                        }.exec(session => {
                          println(session)
                          session
                        })	
                        
				

        setUp(scn.inject(ramp(3 users) over (5 seconds)))
                .protocols(httpProtocol)

}


