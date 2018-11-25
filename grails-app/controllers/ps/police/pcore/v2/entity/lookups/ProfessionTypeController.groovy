package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route ProfessionType requests between model and views.
 *@see ProfessionTypeService
 *@see FormatService
**/
class ProfessionTypeController {
    ProfessionTypeService professionTypeService

    def autocomplete = {
        render text: (professionTypeService.autoCompleteProfessionType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

