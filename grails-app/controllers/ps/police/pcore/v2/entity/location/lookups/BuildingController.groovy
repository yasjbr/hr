package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Building requests between model and views.
 *@see BuildingService
**/
class BuildingController  {

    BuildingService buildingService

    def autocomplete = {
        render text: (buildingService.autoCompleteBuilding(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

