package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route DisabilityType requests between model and views.
 *@see DisabilityTypeService
 *@see FormatService
**/
class DisabilityTypeController {
    DisabilityTypeService disabilityTypeService

    def autocomplete = {
        render text: (disabilityTypeService.autoCompleteDisabilityType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

