package ps.gov.epsilon.core.person

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.util.Environment
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.police.common.beans.v1.CommandParamsMap
import ps.police.common.commands.v1.TrackingInfoCommand
import ps.police.common.domains.v1.TrackingInfo
import ps.police.pcore.v2.entity.lookups.commands.v1.MaritalStatusCommand
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.gov.epsilon.core.location.ManageLocationService
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class ManagePersonService {
    PersonService personService
    ManageLocationService manageLocationService
    SpringSecurityService springSecurityService
    PersonMaritalStatusService personMaritalStatusService

    /**
     * save new person in core
     * @param params
     * @return PersonEducationCommand
     */
    PersonCommand saveNewPerson(PersonCommand personCommand, GrailsParameterMap params) {

        PCPUtils.bindZonedDateTimeFields(personCommand, params)
        personCommand.paramsMap.put("birthPlace", new CommandParamsMap(nameOfParameterKeyInService: "location", nameOfValueInCommand: "birthPlace"))
        try {
            if (params.long("countryId")) {
                LocationCommand locationCommand = manageLocationService.getLocationCommand(params)
                PCPUtils.bindZonedDateTimeFields(locationCommand, params)
                if (locationCommand.validate()) {
                    personCommand.birthPlace = locationCommand
                } else {
                    personCommand.errors.reject("location is not valid")
                }
            }
            if(params.listLong("competency.id")){
               personCommand.paramsMap.put("competency.id",params.listLong("competency.id"))
            }



            if (!params["recentCardNo"] && !params["recentPassportNo"]) {
                personCommand.errors.reject("person.legalIdentifierError.label")
                return personCommand
            }

            //set tracking inf
            TrackingInfoCommand trackingInfo = new TrackingInfoCommand()
            def applicationName = Holders.grailsApplication.config?.grails?.applicationName
            if (!applicationName) applicationName = "BootStrap"
            if (!trackingInfo.createdBy)
                trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
            if (!trackingInfo.lastUpdatedBy)
                trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
            if (!trackingInfo.sourceApplication)
                trackingInfo.sourceApplication = applicationName
            if (!trackingInfo.dateCreatedUTC)
                trackingInfo.dateCreatedUTC = ZonedDateTime.now()
            if (!trackingInfo.lastUpdatedUTC)
                trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
            if (!trackingInfo.ipAddress)
                trackingInfo.ipAddress = "localhost"
            personCommand.trackingInfo = trackingInfo

            personCommand = personService.savePerson(personCommand)

            //check if the person has marital status, then save marital status after save person.
            if (!personCommand.hasErrors() && params.long("maritalStatus.id")) {
                PersonMaritalStatusCommand personMaritalStatusCommand = new PersonMaritalStatusCommand()
                personMaritalStatusCommand.person = personCommand;
                personMaritalStatusCommand.maritalStatus = new MaritalStatusCommand(id: params.long("maritalStatus.id"))
                personMaritalStatusCommand.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                personMaritalStatusCommand.isCurrent = true
                personMaritalStatusService.savePersonMaritalStatus(personMaritalStatusCommand)
            }

        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            personCommand = new PersonCommand()
            personCommand.errors.reject('person.not.created.message', [ex?.cause?.localizedMessage] as Object[], "")
        }
        return personCommand
    }

    /**
     * get the personDTO object
     * @param personId
     * @return dto
     */
    PersonDTO getPersonDTO(GrailsParameterMap params) {
        PersonDTO personDTO
        try {
            if (params.long("personId")) {
                Long personId = params.long("personId")
                GrailsParameterMap paramsForPersonDTO = new GrailsParameterMap([id: personId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                personDTO = personService.getPerson(PCPUtils.convertParamsToSearchBean(paramsForPersonDTO));
            }
        } catch (Exception ex) {

        }
        return personDTO;
    }

}
