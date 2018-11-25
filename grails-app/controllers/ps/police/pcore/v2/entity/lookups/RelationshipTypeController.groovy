package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route RelationshipType requests between model and views.
 *@see RelationshipTypeService
 *@see FormatService
**/
class RelationshipTypeController {
    RelationshipTypeService relationshipTypeService

    def autocomplete = {
        render text: (relationshipTypeService.autoCompleteRelationshipType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

