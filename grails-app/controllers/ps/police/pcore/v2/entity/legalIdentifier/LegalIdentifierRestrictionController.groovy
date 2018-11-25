package ps.police.pcore.v2.entity.legalIdentifier

import ps.police.common.utils.v1.PCPUtils

class LegalIdentifierRestrictionController {

    LegalIdentifierRestrictionService legalIdentifierRestrictionService

    def autocomplete = {
        render text: (legalIdentifierRestrictionService.autoCompleteLegalIdentifierRestriction(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
}
