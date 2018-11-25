package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route LiveStatus requests between model and views.
 *@see LiveStatusService
 *@see FormatService
**/
class LiveStatusController {
    LiveStatusService liveStatusService

    def autocomplete = {
        render text: (liveStatusService.autoCompleteLiveStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

