package ps.gov.epsilon.hr.firm.promotion

import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.mixin.Build
import grails.core.DefaultGrailsClass
import grails.core.GrailsApplication
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.grails.core.DefaultGrailsDomainClass
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

import java.time.ZonedDateTime

/**
 * unit test for PromotionList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([PromotionList])
@Build([PromotionList, JoinedFirmOperationDocument, PromotionListEmployee, CorrespondenceListStatus])
@TestFor(PromotionListController)
class PromotionListControllerSpec extends CommonUnitSpec {
    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    @Shared
    GrailsApplication grailsApplication


    def setupSpec() {
        domain_class = PromotionList
        service_domain = PromotionListService
        entity_name = "promotionList"
        List required = PCPUtils.getRequiredFields(PromotionList)
        required << "code"
        required_properties = required
        filtered_parameters = ["code"];
        primary_key_values = ["encodedId","id"]
        exclude_actions = ["create","edit","delete","save","update","autocomplete","list"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(PromotionList?.class)
        }

    }

    def setup() {
        grails.buildtestdata.TestDataConfigurationHolder.reset()
        sharedService.grailsApplication = grailsApplication
        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }
    }

    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()
        then:
        println model
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.PROMOTION
        model.referenceObject == PromotionList.name
        println("test_list done with data : ${model}")
    }


    /**
     * @goal test edit action.
     * @expectedResult response with known model.
     */
    def "test_edit"() {
        setup:
        println("************************test_edit********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.edit()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_edit done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test edit action.
     * @expectedResult response with null model.
     */
    def "test_edit_not_exists"() {
        setup:
        println("************************test_edit_not_exists********************************")
        when:
        counter++
        required_properties.each { String property ->
            controller.params["${property}"] = entity_name + "_" + property + "_" + counter
        }
        controller.edit()
        then:
        model."${entity_name}" == null
        println("test edit not exit done with not model: ${model}")
    }

    /**
     * @goal test managePromotionList action.
     * @expectedResult response with known model.
     */
    def "test_managePromotionList"() {
        setup:
        println("************************test_managePromotionList********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus
        controller.managePromotionList()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_managePromotionList done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test sendListModal action.
     * @expectedResult response with known model.
     */
    def "test_sendListModal"() {
        setup:
        println("************************test_sendListModal********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["id"] = controller.params["encodedId"]

        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.SUBMITTED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.sendListModal()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_sendListModal done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test receiveListModal action.
     * @expectedResult response with known model.
     */
    def "test_receiveListModal"() {
        setup:
        println("************************test_receiveListModal********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["id"] = controller.params["encodedId"]

        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.SUBMITTED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.receiveListModal()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_receiveListModal done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test closeListModal action.
     * @expectedResult response with known model.
     */
    def "test_closeListModal"() {
        setup:
        println("************************test_closeListModal********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["id"] = controller.params["encodedId"]

        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.closeListModal()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_closeListModal done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test approveRequestModal action.
     * @expectedResult response with known model.
     */
    def "test_approveRequestModal"() {
        setup:
        println("************************test_approveRequestModal********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["id"] = controller.params["encodedId"]

        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.approveRequestModal()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_approveRequestModal done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test rejectRequestModal action.
     * @expectedResult response with known model.
     */
    def "test_rejectRequestModal"() {
        setup:
        println("************************test_rejectRequestModal********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["id"] = controller.params["encodedId"]

        CorrespondenceListStatus correspondenceListStatus = CorrespondenceListStatus.build(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED, receivingParty: EnumReceivingParty.SARAYA)
        instance?.currentStatus = correspondenceListStatus

        controller.rejectRequestModal()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("rejectRequestModal done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test sendList action.
     * @expectedResult response with known model.
     */
    def "test_sendList"() {
        setup:
        println("************************test_sendList********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["manualOutgoingNo"] = "123456789"
        controller.params["fromDate"] = ZonedDateTime.now().toString()

        controller.sendList()
        then:

        println "json: ${response.json}"
        def newCount = domain_class.count()
        response.json.success == true

        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "list.sent.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        println("test_sendList success and new count is ${newCount}")
    }


    /**
     * @goal test sendList action.
     * @expectedResult response with known model.
     */
    def "test_receiveList"() {
        setup:
        println("************************test_receiveList********************************")
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, instance, true)
        }
        controller.params["manualIncomeNo"] = "65432"
        controller.params["toDate"] = ZonedDateTime.now().toString()

        controller.receiveList()
        then:

        println "json: ${response.json}"
        def newCount = domain_class.count()
        response.json.success == true

        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "list.receive.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        println("test_receiveList success and new count is ${newCount}")
    }


    @Override
    public PromotionList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        PromotionList instance
        Map props = [:]
        if (tableData?.disableSave) {
            instance = tableData?.domain?.newInstance(props)
        } else {
            Map addedMap = [:]
            if (tableData.hasSecurity) {
                addedMap.put("springSecurityService", springSecurityService)
            }
            if (tableData.isJoinTable) {
                addedMap.putAll(props)
            }
            instance = tableData?.domain?.buildWithoutValidation(addedMap)


            //add details
            def promotionListEmployee1 = PromotionListEmployee.buildWithoutSave()
            def promotionListEmployee2 = PromotionListEmployee.buildWithoutSave()
            def promotionListEmployee3 = PromotionListEmployee.buildWithoutSave()

            instance.addToPromotionListEmployees(promotionListEmployee1)
            instance.addToPromotionListEmployees(promotionListEmployee2)
            instance.addToPromotionListEmployees(promotionListEmployee3)

            boolean validated = instance.validate()
            if(!validated) {
                DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                if (!builder) {
                    builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                    builders.put(tableData?.objectName, builder)
                }
            }
        }
        return instance
    }


    @Override
    public PromotionList saveEntity(TestDataObject tableData = null) {
        PromotionList instance = fillEntity(tableData)
        once_save_properties.each {property->
            if(PCPSessionUtils.getValue(property)){
                instance."${property}" = PCPSessionUtils.getValue(property)
            }
        }
        instance.save(flush: true, failOnError: true)
        //set current status
        def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
        instance.currentStatus = currentStatus
        instance.save(flush: true, failOnError: true)
        once_save_properties.each {property->
            if(!PCPSessionUtils.getValue(property)){
                PCPSessionUtils.setValue(property,instance."${property}")
            }
        }
        return instance
    }

}