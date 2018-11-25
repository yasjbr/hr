package ps.gov.epsilon.hr.firm.request

class RequestStopExtraInfo extends RequestExtraInfo{

    //informing the employee managers about the request stop  via SMS
    Boolean sendSMS;
    //informing the employee managers about the request stop  via emails
    Boolean sendEmail;
    //if the employee stopped his/her request based on a call by the HR
    Boolean byHR;

    static constraints = {
        sendSMS(nullable: true)
        sendEmail(nullable: true)
        byHR(nullable: true)
    }
}
