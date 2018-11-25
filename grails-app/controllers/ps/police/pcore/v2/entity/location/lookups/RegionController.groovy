package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Region requests between model and views.
 *@see RegionService
**/
class RegionController  {

    RegionService regionService

    def autocomplete = {
        render text: (regionService.autoCompleteRegion(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

