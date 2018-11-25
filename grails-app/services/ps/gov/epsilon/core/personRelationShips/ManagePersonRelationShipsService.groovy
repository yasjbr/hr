package ps.gov.epsilon.core.personRelationShips

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.enums.v1.GenderType
import ps.police.pcore.enums.v1.RelationshipType
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.pcore.v2.entity.lookups.commands.v1.MaritalStatusCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.RelationshipTypeCommand
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class ManagePersonRelationShipsService {
    PersonRelationShipsService personRelationShipsService
    PersonService personService

    /**
     * return the PersonRelationShipsCommand
     * @param params
     * @return
     */
    PersonRelationShipsCommand getPersonRelationShipsCommand(GrailsParameterMap params) {
        PersonRelationShipsCommand personRelationShipsCommand = new PersonRelationShipsCommand()
        try {
            personRelationShipsCommand.fromDate = params.fromDate
            personRelationShipsCommand.person = new PersonCommand(id: params?.personId)
            personRelationShipsCommand.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            personRelationShipsCommand.relatedPerson = new PersonCommand(id: params?.relatedPersonId);
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: "id", value1: params?.relatedPersonId))
            personRelationShipsCommand.isDependent = params.boolean("isDependent")
            personRelationShipsCommand.relationshipType = new RelationshipTypeCommand(id: params.relationshipType)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return personRelationShipsCommand
    }

    /**
     * save the PersonRelationShips remotely in core
     * @param params
     * @return PersonRelationShipsCommand obj
     */
    PersonRelationShipsCommand savePersonRelationShips(GrailsParameterMap params) {
        PersonRelationShipsCommand personRelationShipsCommand
        try {
            if (params) {
                //get the personal RelationShips command object
                personRelationShipsCommand = getPersonRelationShipsCommand(params);

                //if command.validate
                if (personRelationShipsCommand.validate()) {
                    personRelationShipsCommand = personRelationShipsService.savePersonRelationShips(personRelationShipsCommand)
                } else {
                    println "errors:: " + personRelationShipsCommand?.errors
                    throw new Exception(personRelationShipsCommand.errors);
                }
            } else {
                throw new Exception("params sent are null!")
            }

        } catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            ex.printStackTrace()
            println personRelationShipsCommand?.errors
            if(!personRelationShipsCommand.errors)
                personRelationShipsCommand.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")

        }
        return personRelationShipsCommand
    }
}
