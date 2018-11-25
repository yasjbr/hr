package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Locality requests between model and views.
 *@see LocalityService
**/
class LocalityController  {

    LocalityService localityService

    def autocomplete = {
        render text: (localityService.autoCompleteLocality(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

