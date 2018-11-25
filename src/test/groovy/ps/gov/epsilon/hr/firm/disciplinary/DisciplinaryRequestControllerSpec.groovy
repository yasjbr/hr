package ps.gov.epsilon.hr.firm.disciplinary

import grails.buildtestdata.mixin.Build
import grails.converters.JSON
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
import guiplugin.ElementsTagLib
import org.springframework.http.HttpStatus
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgmentService
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryListJudgmentSetup
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReasonService
import ps.gov.epsilon.hr.firm.disciplinary.lookup.JoinedDisciplinaryJudgmentReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.lookups.CountryService
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import spock.lang.Shared

/**
 * unit test for DisciplinaryRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DisciplinaryRequest,DisciplinaryListJudgmentSetup])
@Build([DisciplinaryRequest,DisciplinaryCategory,DisciplinaryJudgment,DisciplinaryReason,DisciplinaryListJudgmentSetup,JoinedFirmOperationDocument,JoinedDisciplinaryJudgmentReason,Employee,EmploymentRecord,EmployeePromotion])
@TestFor(DisciplinaryRequestController)
class DisciplinaryRequestControllerSpec extends CommonUnitSpec {

    GovernorateService governorateService = mockService(GovernorateService)
    OrganizationService organizationService = mockService(OrganizationService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    EmployeeService employeeService = mockService(EmployeeService)
    DisciplinaryReasonService disciplinaryReasonService = mockService(DisciplinaryReasonService)
    DisciplinaryJudgmentService disciplinaryJudgmentService = mockService(DisciplinaryJudgmentService)
    DisciplinaryListService disciplinaryListService = mockService(DisciplinaryListService)
    ManageLocationService manageLocationService = mockService(ManageLocationService)
    CountryService countryService  = mockService(CountryService)
    LocationService locationService = mockService(LocationService)
    DisciplinaryTagLib disciplinaryTagLib = mockTagLib(DisciplinaryTagLib)
    ElementsTagLib elementsTagLib = mockTagLib(ElementsTagLib)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    SharedService sharedService = mockService(SharedService)
    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = DisciplinaryRequest
        service_domain = DisciplinaryRequestService
        entity_name = "disciplinaryRequest"
        required_properties = PCPUtils.getRequiredFields(DisciplinaryRequest)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id";
        primary_key_values = ["encodedId"]
        exclude_actions = ["create","save","update","list","delete"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(DisciplinaryList?.class)
        }

        //to exclude other data for json response
        JSON.registerObjectMarshaller(DisciplinaryList) {
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['version'] = it.version
            returnArray['code'] = it.code
            returnArray['disciplinaryRecordJudgment'] = it.disciplinaryRecordJudgment
            returnArray['name'] = it.name
            returnArray['manualOutgoingNo'] = it.manualOutgoingNo
            returnArray['manualIncomeNo'] = it.manualIncomeNo
            returnArray['currentStatus'] = it.currentStatus
            returnArray['trackingInfo'] = it.trackingInfo
            returnArray['receivingParty'] = it.receivingParty
            returnArray['orderNo'] = it.orderNo
            returnArray['firm'] = it.firm

            return returnArray
        }


    }

    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.disciplinaryListService) {
            serviceInstance.disciplinaryListService = disciplinaryListService
        }

        if(!serviceInstance.manageLocationService) {
            serviceInstance.manageLocationService = manageLocationService
            serviceInstance.manageLocationService.countryService = countryService
            serviceInstance.manageLocationService.locationService = locationService
        }
        if(!serviceInstance.employeeService) {
            serviceInstance.employeeService = employeeService
            serviceInstance.employeeService.personService = personService
            serviceInstance.employeeService.personMaritalStatusService = personMaritalStatusService
            serviceInstance.employeeService.governorateService = governorateService
            serviceInstance.employeeService.organizationService = organizationService
            serviceInstance.employeeService.personService.proxyFactoryService = proxyFactoryService
        }

        if(!controller.disciplinaryReasonService){
            controller.disciplinaryReasonService = disciplinaryReasonService
        }

        sharedService.grailsApplication = grailsApplication

        if(!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.grailsApplication = grailsApplication
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }

        if(!controller.disciplinaryJudgmentService){
            controller.disciplinaryJudgmentService = disciplinaryJudgmentService
        }
    }

    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.DISCIPLINARY
        model.referenceObject == DisciplinaryRequest.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test create action.
     * @expectedResult response with known model.
     */
    def "test_override_create"() {
        setup:
        println("************************test_create********************************")
        when:
        controller.create()
        then:
        model."${entity_name}" == null
        println("test_create done with initialized model ${model}")
    }
    /**
     * @goal test save action.
     * @expectedResult response with known model contains errors.
     */
    def "test_override_fail_save"() {
        setup:
        println("************************test_fail_save********************************")

        when:
        request.method = 'POST'
        controller.save()

        then:
        view == 'create'
        required_properties.each { String property ->
            model?."${entity_name}"?."${property}" == null
        }
        model?."${entity_name}"?.errors?.allErrors?.size() >= 1
        println("test save fail with: ${model} and error size: ${model?."${entity_name}"?.errors?.allErrors?.size()}")
    }
    /**
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_override_success_save"() {
        setup:
        println("************************test_success_save********************************")
        def instanceToSave = fillEntity()

        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryListJudgmentSetup judgmentSetup = DisciplinaryListJudgmentSetup.build(disciplinaryJudgment: currencyJudgment,disciplinaryCategory:disciplinaryCategory)


        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee

        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }

        def previousCount = domain_class.count()
        when:
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }

        //add custom params
        controller.params["disciplinaryCategory"] = disciplinaryCategory?.id
        controller.params["disciplinaryReason"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        controller.params["disciplinaryJudgment"] = [judgment1?.id?.toString(),judgment2?.id?.toString(),currencyJudgment?.id?.toString()]

        //judgment 1 params
        controller.params["value_${judgment1?.id}"] = "20"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["note_${judgment1?.id}"] = "note"

        //judgment 2 params
        controller.params["value_${judgment1?.id}"] = "20"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["orderNo_${judgment1?.id}"] = "001122"

        //currencyJudgment params
        controller.params["value_${currencyJudgment?.id}"] = "20"
        controller.params["currencyId_${currencyJudgment?.id}"] = "4"
        counter++;
        controller.save()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message")))
        previousCount != newCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test save success and new count is ${newCount}")
    }
    /**
     * @goal test save action with ajax request.
     * @expectedResult response with known model contains errors.
     */
    def "test_override_fail_save_ajax"() {
        setup:
        println("************************test_fail_save_ajax********************************")
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.save()

        then:
        def errorList = []
        required_properties.each { String property ->
            if(!exclude_save_properties.contains(property)){
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each{
            response.json.message.contains(it)
        }
        response.json.data == null
        response.json.errorList.size() >= 1
        response.json.errorList?.find{it.field == "global" && it.message == "disciplinaryRequest.noDisciplinaryJudgments.label"} != null
        println("test save ajax fail with error size: ${response.json.errorList.size()}")
    }
    /**
     * @goal test save action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_override_success_save_ajax"() {

        setup:
        println("************************test_success_save_ajax********************************")
        def instanceToSave = fillEntity()

        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryListJudgmentSetup judgmentSetup = DisciplinaryListJudgmentSetup.build(disciplinaryJudgment: currencyJudgment,disciplinaryCategory:disciplinaryCategory)

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee

        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }


        def previousCount = domain_class.count()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }

        //add custom params
        controller.params["disciplinaryCategory"] = disciplinaryCategory?.id
        controller.params["disciplinaryReason"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        controller.params["disciplinaryJudgment"] = [judgment1?.id?.toString(),judgment2?.id?.toString(),currencyJudgment?.id?.toString()]

        //judgment 1 params
        controller.params["value_${judgment1?.id}"] = "20"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["note_${judgment1?.id}"] = "note"

        //judgment 2 params
        controller.params["value_${judgment1?.id}"] = "20"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["orderNo_${judgment1?.id}"] = "001122"

        //currencyJudgment params
        controller.params["value_${currencyJudgment?.id}"] = "20"
        controller.params["currencyId_${currencyJudgment?.id}"] = "4"

        controller.save()

        then:
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount != newCount
        println("test save ajax success and new count is ${newCount}")
        counter++;
    }
    /**
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_save_with_location"() {
        setup:
        println("************************test_save_when_location_errors********************************")
        def instanceToSave = fillEntity()

        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryListJudgmentSetup judgmentSetup = DisciplinaryListJudgmentSetup.build(disciplinaryJudgment: currencyJudgment,disciplinaryCategory:disciplinaryCategory)


        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee

        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }


        def previousCount = domain_class.count()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }

        //add custom params
        controller.params["disciplinaryCategory"] = disciplinaryCategory?.id
        controller.params["disciplinaryReason"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        controller.params["disciplinaryJudgment"] = [judgment1?.id?.toString(),judgment2?.id?.toString(),currencyJudgment?.id?.toString()]

        //judgment 1 params
        controller.params["value_${judgment1?.id}"] = "20"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["note_${judgment1?.id}"] = "note"

        //judgment 2 params
        controller.params["value_${judgment1?.id}"] = "20"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["orderNo_${judgment1?.id}"] = "001122"

        //currencyJudgment params
        controller.params["value_${currencyJudgment?.id}"] = "20"
        controller.params["currencyId_${currencyJudgment?.id}"] = "4"

        //set location info with null country
        controller.params["governorateId"] = "7" //rammallah governorate

        controller.save()

        then:
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount != newCount
        println("test save with location success and new location id is ${response.json.data.locationId}")
        counter++;
    }
    /**
     * @goal test edit action when status is not created.
     * @expectedResult response with null model.
     */
    def "test_edit_status_not_created"() {
        setup:
        println("************************test_override_edit_status_not_created********************************")
        def instance = fillEntity()
        instance.requestStatus = EnumRequestStatus.APPROVED
        instance.save(flush: true, failOnError: true)
        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key,instance,true)
        }
        controller.edit()

        then:
        model."${entity_name}" == null
        println("test edit not exit done with not model: ${model}")
    }
    /**
     * @goal test update action.
     * @expectedResult request with params and response with known model contains errors.
     */
    def "test_override_fail_update"() {
        setup:
        println("************************test_fail_update********************************")
        def testInstance = saveEntity()

        when:
        request.method = 'POST'
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key,testInstance,true)
        }
        required_properties.each { String property ->
            controller.params["${property}"] = null
        }
        controller.update()

        then:
        view == 'edit'
        required_properties.each { String property ->
            getPropertyValue(property,model?."${entity_name}") == null
        }
        model."${entity_name}".errors.allErrors.size() >= 1
        println("test_update fail with: ${model} and error size: ${model."${entity_name}".errors.allErrors.size()}")
    }
    /**
     * @goal test update action.
     * @expectedResult request with params and response with known model not contains any errors.
     */
    def "test_override_success_update"() {

        setup:
        println("************************test_success_update********************************")
        def testInstance = saveEntity()
        def instanceToSave = fillEntity()

        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryListJudgmentSetup judgmentSetup = DisciplinaryListJudgmentSetup.build(disciplinaryJudgment: currencyJudgment,disciplinaryCategory:disciplinaryCategory)


        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee

        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }


        List idValues = []
        primary_key_values.each { key ->
            idValues << [name: key, value: (getPropertyValue(key,testInstance,true))]
        }
        def previousCount = domain_class.count()
        when:
        request.method = 'POST'
        idValues.each { Map map ->
            controller.params["${map?.name}"] = map?.value
        }
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }

        //add custom params
        controller.params["disciplinaryCategory"] = disciplinaryCategory?.id
        controller.params["disciplinaryReason"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        controller.params["disciplinaryJudgment"] = [judgment1?.id?.toString(),judgment2?.id?.toString(),currencyJudgment?.id?.toString()]

        //judgment 1 params
        controller.params["value_${judgment1?.id}"] = "25"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["note_${judgment1?.id}"] = "note2"

        //judgment 2 params
        controller.params["value_${judgment1?.id}"] = "22"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["orderNo_${judgment1?.id}"] = "112233"

        //currencyJudgment params
        controller.params["value_${currencyJudgment?.id}"] = "26"
        controller.params["currencyId_${currencyJudgment?.id}"] = "4"




        controller.update()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.updated.message")))
        previousCount == newCount
        def updateInstance = domain_class.find {
            return idValues.collect { it.name == it.value }
        }
        required_properties.each { String property ->
            getPropertyValue(property,updateInstance) == entity_name + "_" + property + "_" + counter
        }
        response.redirectedUrl == "/${entity_name}/list"
        println("test_update success and new count is ${newCount}")
    }
    /**
     * @goal test update action with ajax request.
     * @expectedResult request with params and response with known model contains errors.
     */
    def "test_override_fail_update_ajax"() {

        setup:
        println("************************test_fail_update_ajax********************************")
        def testInstance = saveEntity()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key,testInstance,true)
        }
        required_properties.each { String property ->
            controller.params["${property}"] = null
        }
        controller.update()

        then:
        def errorList = []
        required_properties.each { String property ->
            if(!exclude_save_properties.contains(property)) {
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each{
            response.json.message.contains(it)
        }
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test_update ajax fail with error size: ${response.json.errorList.size()}")
    }
    /**
     * @goal test update action with ajax request.
     * @expectedResult request with params and response with known model not contains any errors.
     */
    def "test_override_success_update_ajax"() {

        setup:
        println("************************test_success_update_ajax********************************")
        def testInstance = saveEntity()
        def instanceToSave = fillEntity()

        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryListJudgmentSetup judgmentSetup = DisciplinaryListJudgmentSetup.build(disciplinaryJudgment: currencyJudgment,disciplinaryCategory:disciplinaryCategory)


        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee

        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }

        List idValues = []
        primary_key_values.each { key ->
            idValues << [name: key, value: (getPropertyValue(key,testInstance,true))]
        }
        def previousCount = domain_class.count()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        idValues.each { Map map ->
            controller.params["${map?.name}"] = map?.value
        }
        required_properties.each { String property ->
            sendParams(property,instanceToSave,controller.params)
        }

        //add custom params
        controller.params["disciplinaryCategory"] = disciplinaryCategory?.id
        controller.params["disciplinaryReason"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        controller.params["disciplinaryJudgment"] = [judgment1?.id?.toString(),judgment2?.id?.toString(),currencyJudgment?.id?.toString()]

        //judgment 1 params
        controller.params["value_${judgment1?.id}"] = "25"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["note_${judgment1?.id}"] = "note2"

        //judgment 2 params
        controller.params["value_${judgment1?.id}"] = "22"
        controller.params["unitId_${judgment1?.id}"] = "11"
        controller.params["orderNo_${judgment1?.id}"] = "112233"

        //currencyJudgment params
        controller.params["value_${currencyJudgment?.id}"] = "26"
        controller.params["currencyId_${currencyJudgment?.id}"] = "4"

        controller.update()

        then:
        def newCount = domain_class.count()
        response.json.success == true

        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.updated.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount == newCount
        def updateInstance = domain_class.find {
            return idValues.collect { it.name == it.value }
        }
        required_properties.each { String property ->
            getPropertyValue(property,updateInstance) == entity_name + "_" + property + "_" + counter
        }
        println("test_update ajax success and new count is ${newCount}")
    }

    /**
     * @goal test selectEmployee action.
     * @expectedResult response with known model.
     */
    def "test_selectEmployee"() {

        setup:
        println("************************test_selectEmployee********************************")
        when:
        controller.params["employeeId"] = 100L
        controller.selectEmployee()

        then:
        response.json.success == true
        response.json.employeeId == 100L
        println("test selectEmployee done with employeeId: ${response.json.employeeId}")
    }


    /**
     * @goal test selectEmployee action.
     * @expectedResult response with status 404.
     */
    def "test_selectEmployee_not_found"() {

        setup:
        println("************************test_selectEmployee_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.selectEmployee()

        then:
        response.json.success == false
        response.json.employeeId == null
        response.json.message == alertTagLib.error(label: (validationTagLib.message(code: "employee.notFound.error.label"))).toString()
        println("test selectEmployee not found done with message: ${response.json.message}")
    }


    /**
     * @goal test getDisciplinaryReasons action.
     * @expectedResult String.
     */
    def "test_getDisciplinaryReasons_no_data"() {

        setup:
        println("************************test_getDisciplinaryReasons_no_data********************************")

        when:
        controller.getDisciplinaryReasons()

        then:
        response.text == elementsTagLib.labelField(label:validationTagLib.message(code:'disciplinaryRequest.disciplinaryReasons.label'),value: '',size: '6')?.toString()
        println("test getDisciplinaryReasons no data done with message: ${response.text}")
    }

    /**
     * @goal test getDisciplinaryReasons action.
     * @expectedResult String.
     */
    def "test_getDisciplinaryReasons_with_data"() {

        setup:
        println("************************test_getDisciplinaryReasons_with_data********************************")
        //custom data
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)


        when:
        controller.params["disciplinaryCategoryId"] = disciplinaryCategory?.id
        controller.getDisciplinaryReasons()

        then:
        response.text != null
        response.text.contains("getDisciplinaryJudgment()")
        response.text.contains(disciplinaryReason1?.toString())
        response.text.contains(disciplinaryReason2?.toString())
        println("test getDisciplinaryReasons with data done with message: ${response.text}")
    }

    /**
     * @goal test getDisciplinaryJudgments action.
     * @expectedResult String.
     */
    def "test_getDisciplinaryJudgments_no_data"() {

        setup:
        println("************************test_getDisciplinaryJudgments_no_data********************************")

        when:
        controller.getDisciplinaryJudgments()

        then:
        response.text == elementsTagLib.labelField(label:validationTagLib.message(code:'disciplinaryRequest.disciplinaryJudgments.label'),value: '',size: '6')?.toString()
        println("test getDisciplinaryJudgments no data done with message: ${response.text}")
    }

    /**
     * @goal test getDisciplinaryJudgments action.
     * @expectedResult String.
     */
    def "test_getDisciplinaryJudgments_with_data"() {

        setup:
        println("************************test_getDisciplinaryJudgments_with_data********************************")
        //custom data
        DisciplinaryJudgment judgment1 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment judgment2 = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سجن"),unitIds: [10L,11L,12L])
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)

        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: judgment1,disciplinaryReason: disciplinaryReason1)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: judgment2,disciplinaryReason: disciplinaryReason1)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: judgment1,disciplinaryReason: disciplinaryReason2)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: judgment2,disciplinaryReason: disciplinaryReason2)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: currencyJudgment,disciplinaryReason: disciplinaryReason1)

        when:
        controller.params["disciplinaryReasonIds[]"] = [disciplinaryReason1?.id?.toString(),disciplinaryReason2?.id?.toString()]
        controller.getDisciplinaryJudgments()

        then:
        response.text != null
        response.text.contains("viewDisciplinaryJudgmentInputs(this.id,this.name)")
        response.text.contains(judgment1?.toString())
        response.text.contains(judgment2?.toString())
        response.text.contains(currencyJudgment?.toString())
        println("test getDisciplinaryJudgments with data done with message: ${response.text}")
    }

    /**
     * @goal test getDisciplinaryJudgmentsInputs action.
     * @expectedResult String.
     */
    def "test_getDisciplinaryJudgmentsInputs_unit"() {

        setup:
        println("************************test_getDisciplinaryJudgmentsInputs_unit********************************")
        //custom data
        DisciplinaryJudgment judgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"وقف عن العمل"),unitIds: [10L,11L,12L])
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: judgment,disciplinaryReason: disciplinaryReason1)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: judgment,disciplinaryReason: disciplinaryReason2)

        when:
        controller.params["disciplinaryJudgmentId"] = judgment?.id
        controller.getDisciplinaryJudgmentsInputs()

        then:
        response.text != null
        response.text.contains(""" name="unitId_${judgment?.id}" id="unitId_${judgment?.id}" """)
        response.text.contains(""" paramsGenerateFunction="unitParams_${judgment?.id}" """)
        response.text.contains(elementsTagLib.textField(name:'orderNo_'+judgment?.id,class:'',label: validationTagLib.message(code:'disciplinaryRecordJudgment.orderNo.label'),size: '6').toString())
        response.text.contains(elementsTagLib.textField(name:'note_'+judgment?.id,class:'',label: validationTagLib.message(code:'disciplinaryRecordJudgment.note.label'),size: '6').toString())
        println("test getDisciplinaryJudgments unit with message: ${response.text}")
    }

    /**
     * @goal test getDisciplinaryJudgmentsInputs action.
     * @expectedResult String.
     */
    def "test_getDisciplinaryJudgmentsInputs_currency_unit"() {

        setup:
        println("************************test_getDisciplinaryJudgmentsInputs_currency_unit********************************")
        //custom data
        DisciplinaryJudgment currencyJudgment = DisciplinaryJudgment.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"غرامة مالية"),currencyIds: [1L,4L],isCurrencyUnit: true)
        DisciplinaryCategory disciplinaryCategory = DisciplinaryCategory.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"انضباطية"))
        DisciplinaryReason disciplinaryReason1 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"سرقة"),disciplinaryCategories:disciplinaryCategory)
        DisciplinaryReason disciplinaryReason2 = DisciplinaryReason.build(descriptionInfo: new ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo(localName:"هروب"),disciplinaryCategories:disciplinaryCategory)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: currencyJudgment,disciplinaryReason: disciplinaryReason1)
        JoinedDisciplinaryJudgmentReason.build(disciplinaryJudgment: currencyJudgment,disciplinaryReason: disciplinaryReason2)

        when:
        controller.params["disciplinaryJudgmentId"] = currencyJudgment?.id
        controller.getDisciplinaryJudgmentsInputs()

        then:
        response.text != null
        response.text.contains(""" name="currencyId_${currencyJudgment?.id}" id="currencyId_${currencyJudgment?.id}" """)
        response.text.contains(""" paramsGenerateFunction="currencyParams_${currencyJudgment?.id}" """)
        response.text.contains(elementsTagLib.textField(name:'orderNo_'+currencyJudgment?.id,class:'',label: validationTagLib.message(code:'disciplinaryRecordJudgment.orderNo.label'),size: '6').toString())
        response.text.contains(elementsTagLib.textField(name:'note_'+currencyJudgment?.id,class:'',label: validationTagLib.message(code:'disciplinaryRecordJudgment.note.label'),size: '6').toString())
        println("test getDisciplinaryJudgments currency unit done with message: ${response.text}")
    }

    /**
     * @goal test previousJudgmentsModal action.
     * @expectedResult response with known model.
     */
    def "test_previousJudgmentsModal"() {

        setup:
        println("************************test_previousJudgmentsModal********************************")
        when:
        controller.params["id"] = 100L
        controller.previousJudgmentsModal()

        then:
        model != null
        model != [:]
        model == [employeeId:100L]
        println("test previousJudgmentsModal done with model: ${model}")
    }

    /**
     * @goal test previousJudgmentsModal action.
     * @expectedResult response with known model.
     */
    def "test_previousJudgmentsModal_no_data"() {

        setup:
        println("************************test_previousJudgmentsModal_no_data********************************")
        when:
        controller.params["id"] = null
        controller.previousJudgmentsModal()

        then:
        model != [:]
        model == [employeeId:null]
        println("test previousJudgmentsModal no data done with model: ${model}")
    }

    /**
     * @goal test showDetails action.
     * @expectedResult response with known model.
     */
    def "test_showDetails"() {

        setup:
        println("************************test_showDetails********************************")
        DisciplinaryRequest disciplinaryRequest = DisciplinaryRequest.build()
        when:
        controller.params["disciplinaryRequestEncodedId"] = disciplinaryRequest?.encodedId
        controller.showDetails()

        then:
        model != null
        model != [:]
        model.disciplinaryRequest.id == disciplinaryRequest?.id
        println("test showDetails done with model: ${model}")
    }

    /**
     * @goal test showDetails action.
     * @expectedResult response with known model.
     */
    def "test_showDetails_no_data"() {

        setup:
        println("************************test_showDetails_no_data********************************")
        when:
        controller.params["disciplinaryRequestEncodedId"] = null
        controller.showDetails()

        then:
        response.text == ""
        println("test showDetails no data done with model: ${model}")
    }

    /**
     * @goal test createNewDisciplinaryRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewDisciplinaryRequest"() {

        setup:
        println("************************test_createNewDisciplinaryRequest********************************")
        DisciplinaryRequest disciplinaryRequest = DisciplinaryRequest.build()
        when:
        controller.params["employeeId"] = disciplinaryRequest?.employee?.id
        controller.createNewDisciplinaryRequest()

        then:
        model != null
        model != [:]
        model.disciplinaryRequest.employee.id == disciplinaryRequest?.employee?.id
        println("test createNewDisciplinaryRequest done with model: ${model}")
    }

    /**
     * @goal test createNewDisciplinaryRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewDisciplinaryRequest_not_found"() {

        setup:
        println("************************test_createNewDisciplinaryRequest_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.createNewDisciplinaryRequest()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test createNewDisciplinaryRequest done with response: ${response.status}")
    }

}