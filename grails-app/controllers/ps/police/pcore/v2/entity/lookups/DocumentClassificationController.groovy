package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route DocumentType requests between model and views.
 *@see DocumentTypeService
 *@see FormatService
 **/
class DocumentClassificationController {

    DocumentClassificationService documentClassificationService

    def autocomplete = {
        render text: (documentClassificationService.autoCompleteDocumentClassification(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
}
