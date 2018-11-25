package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route DocumentType requests between model and views.
 *@see DocumentTypeService
 *@see FormatService
 **/
class DocumentTypeController {

    DocumentTypeService documentTypeService

    def autocomplete = {
        render text: (documentTypeService.autoCompleteDocumentType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
}
