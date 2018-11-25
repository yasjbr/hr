package ps.gov.epsilon.hr.common

import grails.converters.JSON
import grails.gorm.PagedResultList

class ManageAttachmentController {

    ManageAttachmentService manageAttachmentService

    def index() {}

    def list = {}

    def filter = {
        PagedResultList pagedResultList = manageAttachmentService.search(params)
        render text: (manageAttachmentService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }
}
