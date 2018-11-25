package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import org.springframework.web.servlet.ModelAndView
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.CommandParamsMap
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.pcore.v2.entity.lookups.commands.v1.UnitOfMeasurementCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonArrestHistoryCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonArrestHistoryDTO
import ps.police.pcore.v2.entity.person.lookups.commands.v1.ArrestJudgementDetailsCommand

import java.lang.reflect.Field

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonArrestHistory requests between model and views.
 *@see PersonArrestHistoryService
 *@see FormatService
 **/
class PersonArrestHistoryController  {

    PersonArrestHistoryService personArrestHistoryService
    EmployeeService employeeService
    PersonService personService
    def formatService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {
        Map model = sharedService.getAttachmentTypeListAsMap(EnumDomainName.PERSON_ARREST_HISTORY.toString(), EnumOperation.PERSON_ARREST_HISTORY, EnumOperation.PERSON)
        return new ModelAndView("/pcore/person/personArrestHistory/list", model)
    }

    def show= {
        if(params.long("id")){
            PersonArrestHistoryDTO personArrestHistory = personArrestHistoryService.getPersonArrestHistory(PCPUtils.convertParamsToSearchBean(params))
            render(view:"/pcore/person/personArrestHistory/show",model: [personArrestHistory:personArrestHistory])
        }else{
            notFound()
        }
    }

    def preCreate = {
        render view:"/pcore/person/personArrestHistory/preCreate"
    }

    /**
     * this action is used to return employee info to be used in create person arrest history
     */
    def selectEmployee = {
        if (params["employee.id"]) {
            params["id"] = params["employee.id"]
            Employee employee = employeeService.getInstance(params)
            Long personId = employee?.personId
            render text: ([success: true, encodedPersonId: HashHelper.encode(personId.toString())] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'allowanceRequest.employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    def create = {
        if (params["person.encodedId"]) {
            params["person.id"] = HashHelper.decode(params["person.encodedId"])
        }
        PersonArrestHistoryCommand personArrestHistoryCommand = new PersonArrestHistoryCommand()

        bindData(personArrestHistoryCommand, params)
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: personArrestHistoryCommand?.person?.id))
        def dto = personService.getPerson(searchBean)
        personArrestHistoryCommand.person = PCPUtils.toCommand(dto, PersonCommand)

        Map map = [:]
        map.put("personArrestHistory",personArrestHistoryCommand)
        map.put("isPersonDisabled",true)
        render(view:"/pcore/person/personArrestHistory/create", model:map)
    }

    def filter = {
        String orderColumn = params["orderColumn"],orderDirection = params["orderDirection"]
        SearchBean searchBean = PCPUtils.convertParamsToSearchBean(params)
        searchBean.searchCriteria.put("orderColumn", new SearchConditionCriteriaBean(operand: 'orderColumn', value1: orderColumn))
        searchBean.searchCriteria.put("orderDirection", new SearchConditionCriteriaBean(operand: 'orderDirection', value1: orderDirection))
        PagedList pagedResultList = personArrestHistoryService.searchPersonArrestHistory(searchBean)
        render text: (personArrestHistoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonArrestHistoryCommand personArrestHistory = new PersonArrestHistoryCommand()

        bindData(personArrestHistory,params)
        PCPUtils.bindZonedDateTimeFields(personArrestHistory,params)

        if(params.boolean("isJudgementForEver") == false && params["arrestPeriod"]){
            def arrestJudgementTypeList  = params.list("arrestJudgementType")
            def unitOfMeasurementIdsList = params.list("unitOfMeasurement.id")
            def arrestPeriodList         = params.list("arrestPeriod")

            arrestPeriodList.eachWithIndex { arrestPeriod, index ->
                personArrestHistory.arrestJudgementDetails << new ArrestJudgementDetailsCommand(
                        arrestJudgementType: arrestJudgementTypeList[index] ? ps.police.pcore.enums.v1.ArrestJudgementType.valueOf(arrestJudgementTypeList[index]) : null,
                        unitOfMeasurement:unitOfMeasurementIdsList[index]? new UnitOfMeasurementCommand(id:(unitOfMeasurementIdsList[index] as long)):null,
                        arrestPeriod: arrestPeriod as int,
                )
            }
        }

        //set all props to send to service
        personArrestHistory.paramsMap.put("arrestJudgementDetails",new CommandParamsMap(nameOfPropertiesToSend:
                [
                        "arrestPeriod":"arrestPeriod",
                        "arrestJudgementType":"arrestJudgementType",
                        "unitOfMeasurement.id":"unitOfMeasurement.id",
                ]
        ))


        if(personArrestHistory.validate()) {
            personArrestHistory = personArrestHistoryService.savePersonArrestHistory(personArrestHistory)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), personArrestHistory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), personArrestHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personArrestHistory, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personArrestHistory?.hasErrors()) {
                respond personArrestHistory, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            PersonArrestHistoryDTO personArrestHistory = personArrestHistoryService.getPersonArrestHistory(PCPUtils.convertParamsToSearchBean(params))
            render(view:"/pcore/person/personArrestHistory/edit",model: [personArrestHistory:personArrestHistory, isPersonDisabled:true])
        }else{
            notFound()
        }
    }

    def update = {
        PersonArrestHistoryCommand personArrestHistory = new PersonArrestHistoryCommand()

        bindData(personArrestHistory,params)
        PCPUtils.bindZonedDateTimeFields(personArrestHistory,params)


        if(params.boolean("isJudgementForEver") == false && params["arrestPeriod"]){
            def arrestJudgementTypeList  = params.list("arrestJudgementType")
            def unitOfMeasurementIdsList = params.list("unitOfMeasurement.id")
            def arrestPeriodList         = params.list("arrestPeriod")

            arrestPeriodList.eachWithIndex { arrestPeriod, index ->
                personArrestHistory.arrestJudgementDetails << new ArrestJudgementDetailsCommand(
                        arrestJudgementType: arrestJudgementTypeList[index] ? ps.police.pcore.enums.v1.ArrestJudgementType.valueOf(arrestJudgementTypeList[index]) : null,
                        unitOfMeasurement:unitOfMeasurementIdsList[index]? new UnitOfMeasurementCommand(id:(unitOfMeasurementIdsList[index] as long)):null,
                        arrestPeriod: arrestPeriod as int,
                )
            }
        }

        //set all props to send to service
        personArrestHistory.paramsMap.put("arrestJudgementDetails",new CommandParamsMap(nameOfPropertiesToSend:
                [
                        "arrestPeriod":"arrestPeriod",
                        "arrestJudgementType":"arrestJudgementType",
                        "unitOfMeasurement.id":"unitOfMeasurement.id",
                ]
        ))

        if(personArrestHistory.validate()) {
            personArrestHistory = personArrestHistoryService.savePersonArrestHistory(personArrestHistory)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), personArrestHistory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), personArrestHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personArrestHistory,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personArrestHistory.hasErrors()) {
                respond personArrestHistory, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personArrestHistoryService.deletePersonArrestHistory(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), params?.id,deleteBean.responseMessage?:""])
        if (request.xhr) {
            def json = [:]
            json.success = deleteBean.status
            json.message = deleteBean.status ? msg.success(label: successMessage) : msg.error(label: failMessage)
            render text: (json as JSON), contentType: "application/json"
        } else {
            if (deleteBean.status) {
                flash.message = msg.success(label: successMessage)
            } else {
                flash.message = msg.error(label: failMessage)
            }
            redirect(action: "list")
        }
    }

    def autocomplete = {
        render text: (personArrestHistoryService.autoCompletePersonArrestHistory(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

