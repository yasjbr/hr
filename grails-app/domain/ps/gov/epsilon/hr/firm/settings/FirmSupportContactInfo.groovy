package ps.gov.epsilon.hr.firm.settings

import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

/**
 *<h1>Purpose</h1>
 * To hold the support information for the firm
 * <h1>Usage</h1>
 * Used  as to represent footer and technical support info for the firm
 * **/

class FirmSupportContactInfo {

    String id

    String encodedId

    String name
    String phoneNumber
    String faxNumber
    String email

    static belongsTo = [firm: Firm]

    static  transients = ['encodedId']
    static constraints = {
        name(Constants.NAME)
        phoneNumber (Constants.STRING_NUMBER_NULLABLE)
        faxNumber (Constants.STRING_NUMBER_NULLABLE)
        email(Constants.EMAIL_NULLABLE)
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

}
