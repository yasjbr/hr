package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Jail requests between model and views.
 *@see JailService
 *@see FormatService
**/
class JailController {
    JailService jailService

    def autocomplete = {
        render text: (jailService.autoCompleteJail(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

