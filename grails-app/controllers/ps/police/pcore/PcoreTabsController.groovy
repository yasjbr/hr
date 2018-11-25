package ps.police.pcore

import grails.core.GrailsApplication
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.EnumDomainName

import java.lang.reflect.Field


/**
 * <h1>Purpose</h1>
 * Route all PCORE data transformation and requests between model and views.
 **/

class PcoreTabsController {

    GrailsApplication grailsApplication
    SharedService sharedService

    /**
     * render list with data table layout from search method with pass all parameters from request.
     **/
    def listInLine = {

        String domainClassName = params.remove("tabEntityName");
        String holderEntityPath = PCPSessionUtils.getValue("holderEntityPath")

        Map map = [:]
        map.put("tabEntityName", domainClassName)
        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }
        if (domainClassName) {
            String holderEntityName = PCPSessionUtils.getValue("holderEntityName")
            render(template: "${holderEntityPath}/${holderEntityName}/${domainClassName}/inLine/list", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    /**
     * render create view by initial instance with filling PCORE data and pass all parameters from request.
     **/
    def createInLine = {

        String domainClassName = params.remove("tabEntityName");
        String holderEntityName = PCPSessionUtils.getValue("holderEntityName")
        String holderEntityPath = PCPSessionUtils.getValue("holderEntityPath")
        String commandName = PCPSessionUtils.getValue("commandName")

        def domainClassInstance = Class.forName(commandName)

        List<Field> commandObjects = PCPUtils.getAllDeclaredFields(domainClassInstance)?.findAll {
            it.type.name.contains("Command") && it.name != 'trackingInfo'
        }

        domainClassInstance = domainClassInstance?.newInstance()
        bindData(domainClassInstance, params)

        Object serviceObject
        String serviceName
        String entityName
        SearchBean searchBean

        //try to get PCORE data in dynamic and fill it depends on holder entity name
        commandObjects.each { Field field ->
            if (domainClassInstance?."${field.name}"?.id) {
                entityName = field.type.simpleName?.replace("Command", "")
                searchBean = new SearchBean()
                serviceName = entityName[0]?.toLowerCase() + entityName?.substring(1, entityName.length()) + "Service"
                serviceObject = Holders.applicationContext.getBean(serviceName)
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: domainClassInstance?."${field.name}"?.id))
                def dto = serviceObject."get${entityName}"(searchBean)
                domainClassInstance?."${field.name}" = PCPUtils.toCommand(dto, field.type)
            }
            serviceObject = null
            entityName = null
            searchBean = null
        }

        Map map = [:];
        map.put("tabEntityName", domainClassName)
        map.put(domainClassName, domainClassInstance)
        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }

        if (domainClassName) {
            render(template: "${holderEntityPath}/${holderEntityName}/${domainClassName}/inLine/create", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    /**
     * render edit view by getting instance from PCORE data and pass all parameters from request.
     **/
    def editInLine = {
        String domainClassName = params.remove("tabEntityName");
        Object serviceObject = Holders.applicationContext.getBean(domainClassName + "Service")
        Object domainClassInstance = serviceObject."get${domainClassName?.capitalize()}"(PCPUtils.convertParamsToSearchBean(params))
        String holderEntityPath = PCPSessionUtils.getValue("holderEntityPath")

        Map map = [:]
        map.put("tabEntityName", domainClassName)
        map.put(domainClassName, domainClassInstance)
        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }
        if (domainClassInstance) {
            String holderEntityName = PCPSessionUtils.getValue("holderEntityName")
            render(template: "${holderEntityPath}/${holderEntityName}/${domainClassName}/inLine/edit", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    /**
     * render show view by getting instance from PCORE data and pass all parameters from request.
     **/
    def showInLine = {
        String domainClassName = params.remove("tabEntityName");
        Object serviceObject = Holders.applicationContext.getBean(domainClassName + "Service")
        Object domainClassInstance = serviceObject."get${domainClassName?.capitalize()}"(PCPUtils.convertParamsToSearchBean(params))
        String holderEntityPath = PCPSessionUtils.getValue("holderEntityPath")

        Map map = [:]
        map.put("tabEntityName", domainClassName)
        map.put(domainClassName, domainClassInstance)

        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }
        if (domainClassInstance) {
            String holderEntityName = PCPSessionUtils.getValue("holderEntityName")
            render(template: "${holderEntityPath}/${holderEntityName}/${domainClassName}/inLine/show", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    /**
     * load initial tab with render data list with data table layout from search method with pass all parameters from request.
     **/
    def loadTab = {
        String tabName = params["tabName"]
        String holderEntityName = params["holderEntityName"]
        String holderEntityPath = params["holderEntityPath"]
        String commandName = params["commandName"]
        Long holderEntityId = params.long("holderEntityId")
        String tabEntityName = params["tabEntityName"]
        String phaseName = params["phaseName"]
        List<String> ids = params["ids[]"]
        if (!holderEntityId) {
            render "${message(code: 'default.not.found.message', args: [message(code: holderEntityName + '.entity', default: 'default' + holderEntityName), holderEntityId])}"
        } else {
            //this values not passed to views (not need in views others values the views need it)
            PCPSessionUtils.setValue("holderEntityName", holderEntityName)
            PCPSessionUtils.setValue("commandName", commandName)
            PCPSessionUtils.setValue("holderEntityPath", holderEntityPath)

            Map mapAttach = [:]
            if (tabEntityName == "personArrestHistory") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EnumDomainName.PERSON_ARREST_HISTORY.toString(), EnumOperation.PERSON_ARREST_HISTORY, EnumOperation.PERSON)
            }else if (tabEntityName == "legalIdentifier") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EnumDomainName.LEGAL_IDENTIFIER.toString(), EnumOperation.LEGAL_IDENTIFIER, EnumOperation.PERSON)
            } else if (tabEntityName == "personEducation") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EnumDomainName.PERSON_EDUCATION.toString(), EnumOperation.PERSON_EDUCATION, EnumOperation.PERSON)
            } else if (tabEntityName == "personTrainingHistory") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EnumDomainName.PERSON_TRAINING_HISTORY.toString(), EnumOperation.PERSON_TRAINING_HISTORY, EnumOperation.PERSON)
            } else if (tabEntityName == "personEmploymentHistory") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EnumDomainName.PERSON_EMPLOYMENT_HISTORY.toString(), EnumOperation.PERSON_EMPLOYMENT_HISTORY, EnumOperation.PERSON)
            } else if (tabEntityName == "personHealthHistory") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EnumDomainName.PERSON_HEALTH_HISTORY.toString(), EnumOperation.PERSON_HEALTH_HISTORY, EnumOperation.PERSON)
            } else {
                mapAttach = [:]
            }

            Map dataModel = [entityId: holderEntityId, tabEntityName: tabEntityName, phaseName: phaseName]
            dataModel.putAll(mapAttach)

            if (ids) {
                dataModel.putAt("ids[]", ids);
            }
            render(template: "${holderEntityPath}/${holderEntityName}/tabs/${tabName}", model: dataModel)
        }
    }

    /**
     * remove some params from command.
     **/
    void removeCommonParams(GrailsParameterMap params) {
        params.remove("id")
    }
}
