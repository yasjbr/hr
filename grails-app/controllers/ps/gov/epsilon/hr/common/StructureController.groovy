package ps.gov.epsilon.hr.common

import grails.converters.JSON
import grails.orm.PagedResultList
import guiplugin.FormatService
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.DepartmentService
import ps.gov.epsilon.hr.firm.FirmService
import ps.police.common.utils.v1.PCPSessionUtils

class StructureController {


    FormatService formatService
    DepartmentService departmentService
    FirmService firmService

    def index() {
        if(!params.id){
            params.id = PCPSessionUtils.getValue("firmId")
        }
        return [firm: firmService.getInstance(params)]
    }

    def filter = {
        PagedResultList pagedResultList = departmentService.search(params)
        render text: (formatService.resultListToTreeMap(pagedResultList, "encodedId") as JSON), contentType: "application/json"
    }

    def getDepartmentInfo = {
        Department department = departmentService.getInstanceWithRemotingValues(params)
        render(template: "/structure/departmentInfo", model: [department: department])
    }
}
