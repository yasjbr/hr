package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route TrainingDegree requests between model and views.
 *@see TrainingDegreeService
 *@see FormatService
**/
class TrainingDegreeController {
    TrainingDegreeService trainingDegreeService

    def autocomplete = {
        render text: (trainingDegreeService.autoCompleteTrainingDegree(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

