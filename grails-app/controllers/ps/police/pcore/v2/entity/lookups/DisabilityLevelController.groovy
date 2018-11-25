package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route DisabilityLevel requests between model and views.
 *@see DisabilityLevelService
 *@see FormatService
**/
class DisabilityLevelController {
    DisabilityLevelService disabilityLevelService

    def autocomplete = {
        render text: (disabilityLevelService.autoCompleteDisabilityLevel(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

