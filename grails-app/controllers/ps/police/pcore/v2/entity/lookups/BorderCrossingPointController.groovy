package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route BorderCrossingPoint requests between model and views.
 *@see BorderCrossingPointService
 *@see FormatService
**/
class BorderCrossingPointController {
    BorderCrossingPointService borderCrossingPointService

    def autocomplete = {
        render text: (borderCrossingPointService.autoCompleteBorderCrossingPoint(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

