package ps.police.pcore.v2.entity.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route ContactType requests between model and views.
 *@see ContactTypeService
 *@see FormatService
**/
class ContactTypeController  {

    ContactTypeService contactTypeService

    def autocomplete = {
        render text: (contactTypeService.autoCompleteContactType(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

