package ps.gov.epsilon.core.personMaritalStatus

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.enums.v1.MaritalStatusEnum
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.commands.v1.MaritalStatusCommand
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class ManagePersonMaritalStatusService {

    MaritalStatusService maritalStatusService
    PersonMaritalStatusService personMaritalStatusService
    PersonService personService
    PersonRelationShipsService personRelationShipsService

    /**
     * return the location command
     * @param params
     * @return
     */
    PersonMaritalStatusCommand getPersonMaritalStatusCommand(MaritalStatusRequest request) {
        PersonMaritalStatusCommand personMaritalStatusCommand = new PersonMaritalStatusCommand()
        try {
            //PCPUtils.bindZonedDateTimeFields(personMaritalStatusCommand,request?.properties)
            personMaritalStatusCommand?.fromDate = request?.maritalStatusDate
            personMaritalStatusCommand.maritalStatus = new MaritalStatusCommand(id: request?.newMaritalStatusId);//the new status

            //need to check in case of male, and has current wives:
            PersonDTO person = personService.getPerson(PCPUtils.convertParamsToSearchBean(new GrailsParameterMap([id: request?.employee?.personId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())))
            personMaritalStatusCommand?.isCurrent = true
            //if the employee is male, check the number of his current wives
            if (person?.genderType?.id == ps.police.pcore.enums.v1.GenderType.MALE.value() && request?.newMaritalStatusId in [MaritalStatusEnum.WIDOWED.value(), MaritalStatusEnum.DIVORCED.value()]){

                GrailsParameterMap filterParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                filterParams["person.id"] = request?.employee?.personId
                filterParams["isCurrent"] = true
                filterParams["relationshipType.id"] = [RelationshipTypeEnum.WIFE.value()]
                PagedList personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))

                println "size:: ${personRelationShipsDTOPagedList?.resultList?.size()}"
                if (personRelationShipsDTOPagedList?.resultList?.size() > 1) {
                    personMaritalStatusCommand?.isCurrent = false
                }
            }

            personMaritalStatusCommand.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            personMaritalStatusCommand.person = new PersonCommand(id: request?.employee?.personId)
            return personMaritalStatusCommand
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    /**
     * save the location remotely on core
     * @param params
     * @return PersonMaritalStatusCommand obj
     */
    PersonMaritalStatusCommand savePersonMaritalStatus(MaritalStatusRequest request) {
        PersonMaritalStatusCommand personMaritalStatusCommand
        try {
            if (request) {
                personMaritalStatusCommand = getPersonMaritalStatusCommand(request);
                if (personMaritalStatusCommand.validate()) {
                    personMaritalStatusCommand = personMaritalStatusService.savePersonMaritalStatus(personMaritalStatusCommand)
                } else {
                    println "errors!"
                    println personMaritalStatusCommand.errors
                    throw new Exception(personMaritalStatusCommand.errors)
                }
            } else {
                throw new Exception("Request is null")
            }

        } catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            ex.printStackTrace()
            personMaritalStatusCommand.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")

        }
        return personMaritalStatusCommand
    }
}
