package ps.gov.epsilon.hr.firm.profile.employee

import grails.artefact.TagLibrary
import grails.gsp.TagLib

@TagLib
class EmployeeTagLib implements TagLibrary {

    static namespace = "employee"

    /**
     * using to get autocomplete employee.
     */
    def autocomplete = { attrs, body ->
        out << g.render(template: "/employee/wrapper",model: [
                id:'sharedObjectValues',
                name:'sharedObjectValues',
                isMultiple:true,
                isSearch:true,
                size:"6"
        ])
    }

 }
