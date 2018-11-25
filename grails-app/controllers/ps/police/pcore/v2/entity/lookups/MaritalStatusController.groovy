package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route MaritalStatus requests between model and views.
 *@see MaritalStatusService
 *@see FormatService
**/
class MaritalStatusController {
    MaritalStatusService maritalStatusService

    def autocomplete = {
        render text: (maritalStatusService.autoCompleteMaritalStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

