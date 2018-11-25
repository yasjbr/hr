package ps.gov.epsilon.core.personEducation

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.police.pcore.v2.entity.person.PersonEducationService
import ps.gov.epsilon.core.location.ManageLocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonEducationCommand

@Transactional
class ManagePersonEducationService {

    PersonEducationService personEducationService
    SpringSecurityService springSecurityService
    ManageLocationService manageLocationService

    /**
     * save the person education for the applicant
     * @param params
     * @return PersonEducationCommand
     */
    PersonEducationCommand savePersonEducation(PersonEducationCommand personEducationCommand, GrailsParameterMap params) {

        try {
            //save location
            if (params.long("governorateId") || params.long("countryId")) {
                params.remove("id")
                if(params.long("locationId")){
                    params.id = params.locationId //edit location
                }
                LocationCommand locationCommand = manageLocationService.saveLocation(params)
                personEducationCommand.location = locationCommand
            }
            //save the contact info of applicant on core
            personEducationCommand = personEducationService?.savePersonEducation(personEducationCommand)
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            personEducationCommand = new PersonEducationCommand()
            personEducationCommand.errors.reject('default.not.created.message', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }

        return personEducationCommand
    }

}
