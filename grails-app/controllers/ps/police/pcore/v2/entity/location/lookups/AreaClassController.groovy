package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route AreaClass requests between model and views.
 *@see AreaClassService
**/
class AreaClassController  {

    AreaClassService areaClassService


    def autocomplete = {
        render text: (areaClassService.autoCompleteAreaClass(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
}

