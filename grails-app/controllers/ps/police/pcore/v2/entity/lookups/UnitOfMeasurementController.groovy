package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route UnitOfMeasurement requests between model and views.
 *@see UnitOfMeasurementService
 *@see FormatService
**/
class UnitOfMeasurementController {
    UnitOfMeasurementService unitOfMeasurementService

    def autocomplete = {
        render text: (unitOfMeasurementService.autoCompleteUnitOfMeasurement(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

