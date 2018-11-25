package ps.gov.epsilon.hr.common.domains.v1

import ps.police.config.v1.Constants

/**
 * Created by wassi on 20/07/17.
 */
class DescriptionInfo extends ps.police.common.domains.v1.DescriptionInfo{
    static constraints = {
        localName(Constants.LOOKUP_NAME)
        latinName(Constants.LOOKUP_NAME_NULLABLE)
        hebrewName(Constants.LOOKUP_NAME_NULLABLE)
    }
}
