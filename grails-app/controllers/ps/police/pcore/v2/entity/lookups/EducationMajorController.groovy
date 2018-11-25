package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route EducationMajor requests between model and views.
 *@see EducationMajorService
 *@see FormatService
**/
class EducationMajorController {
    EducationMajorService educationMajorService

    def autocomplete = {
        render text: (educationMajorService.autoCompleteEducationMajor(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

