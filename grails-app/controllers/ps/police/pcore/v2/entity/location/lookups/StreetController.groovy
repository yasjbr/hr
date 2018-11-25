package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Street requests between model and views.
 *@see StreetService
**/
class StreetController  {

    StreetService streetService

    def autocomplete = {
        render text: (streetService.autoCompleteStreet(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

