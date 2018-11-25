package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Color requests between model and views.
 *@see ColorService
 *@see FormatService
**/
class ColorController {
    ColorService colorService

    def autocomplete = {
        render text: (colorService.autoCompleteColor(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

