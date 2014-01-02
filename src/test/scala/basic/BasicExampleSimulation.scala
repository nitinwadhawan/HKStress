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
                /*.baseURL("http://192.168.70.26:8787")*/
                .baseURL("http://admin.healthkart.com")
                .extraInfoExtractor((status:Status, session:Session, request:Request, response: Response) => {
                List[String](request.getRawUrl())                                                   })
                .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
               .acceptHeader("application/json;q=0.9,*/*;q=0.8")
                .acceptEncodingHeader("gzip, deflate")
                .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
               .disableFollowRedirect

        val headers_1 = Map(
               """Content-Type"""-> """application/x-www-form-urlencoded""" ,
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
        
                val addToCartJSON="""{"productVariantBarcode":"${barcode}"}"""
                
				        val scn = scenario("Scenario name")
                .repeat(594) {
                       /* exec(
                                http("HK new Homepage")
                                        .get("/")
                                        .headers(headers_1)
                                        .check(status.is(200))
                                  )                                        
                                .pause(5)  */    
                               //.feed(csv("user_credentials.csv"))
                               exec(
                                      http("submit login")
                                     .post("/core/auth/Login.action")
                                     .param("email", "rahul.agarwal@healthkart.com")
                                     .param("type", """old""")                                     
				                     .param("password", "hkart@123")
                                     .param("login", """Sign In""")
                                     .check(status.is(302))
                                     .check(regex("""Login using an existing account""").notExists)
                                     .headers(headers_2))                                     
                                     .pause(2,3)
                                /*.exec(
                                      http("navigate to product page")
                                     .post("/sv/muscleblaze-bcaa-2000/SP-23391?navKey=VRNT-45106")
                                     .check(status.is(200))
                                     .headers(headers_2))                                     
                                     .pause(2,3) */
                                 .feed(csv("nitin.csv"))    
                                 .exec(
                                      http("Enter barcode")
                                     .post("/admin/inventory/InventoryCheckin.action?stockTransfer=322&saveStockTransfer=")
                                    /* .param("stockTransfer", "322")
                                     .param("checkinInventoryAgainstStockTransfer", "322")*/
                                     .param("productVariantBarcode", "${barcode}")
                                     //.body(StringBody(addToCartJSON)).asJSON
                                     .check(status.is(200))
                                     .headers(headers_1))
                                     
                                  
                                     
                        }.exec(session => {
                          println(session)
                          session
                        })	
                        
				

        setUp(scn.inject(ramp(1) over (1 seconds)))
                .protocols(httpProtocol)

}


