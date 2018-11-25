package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route DiseaseType requests between model and views.
 *@see DiseaseTypeService
 *@see FormatService
**/
class DiseaseTypeController {
    DiseaseTypeService diseaseTypeService

    def autocomplete = {
        render text: (diseaseTypeService.autoCompleteDiseaseType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

