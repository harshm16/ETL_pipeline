package com.cc

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.typesafe.config.{Config, ConfigFactory}


class EmailClient {

}
// todo Add IAM policy for the instance sending email

/**
 * This is the email client that can send an email from an AWS SES verified account to any other email account
 * It uses standard AWS SES SDK to send emails that can be formatted to suit our needs. A simple HTML
 * content is sent currently.
 */
object EmailClient {

  val config: Config = ConfigFactory.load()
  val logger = CreateLogger(classOf[EmailClient])
  val FROM = config.getString("spark.email_sender")
  val TO = config.getString("spark.email_receiver")
  val SUBJECT = config.getString("spark.email_subject")


  /**
   * This method is invoked to send a mail from and to accounts defined in application config.
   *
   * @param errorType describes the type of log message that was detected. "Warn", or "Error".
   * @param time contains a string of two times: start time of window where the message was detected and the end time
   * @param count is the number of message of type errorType was detected
   *
   *
   */
  def sendMail(errorType:String,time:String,count:Int) = {

    try {
      val client = AmazonSimpleEmailServiceClientBuilder.standard.withRegion(Regions.US_EAST_1).build()
      val msgType =  errorType match {
        case "Error" => "Errors detected"
        case "Warn" => "Warnings detected"
      }
      val HTMLBODY: String = s"<h1>${msgType}</h1>" + s"<p>${count} instances detected in time range ${time}</p>"

      val request = new SendEmailRequest()
        .withDestination(new Destination().withToAddresses(TO))
        .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(HTMLBODY)))
          .withSubject(new Content().withCharset("UTF-8")
            .withData(SUBJECT))).withSource(FROM)

      client.sendEmail(request)
      logger.info("Email sent!")
    } catch {
      case ex: Exception =>
        logger.error("The email was not sent. Error message: " + ex.getMessage)
    }
  }
}
