package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.orm.PagedResultList
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.AlertTagLib
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.Job
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.lookups.CompetencyService
import ps.police.pcore.v2.entity.lookups.EducationDegreeService
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.ProfessionTypeService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.firm.lookups.InspectionCategoryService
import ps.gov.epsilon.hr.firm.lookups.JobType
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

import javax.servlet.http.HttpServletRequestWrapper

/**
 * unit test for Vacancy controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([Vacancy])
@Build([Vacancy, JoinedFirmOperationDocument, Job, JobType, JobRequisition, RecruitmentCycle, Firm])
@TestFor(VacancyController)
class VacancyControllerSpec extends CommonUnitSpec {

    WorkExperienceService workExperienceService = mockService(WorkExperienceService)
    GovernorateService governorateService = mockService(GovernorateService)
    EducationMajorService educationMajorService = mockService(EducationMajorService)
    EducationDegreeService educationDegreeService = mockService(EducationDegreeService)
    ProfessionTypeService professionTypeService = mockService(ProfessionTypeService)
    CompetencyService competencyService = mockService(CompetencyService)
    MaritalStatusService maritalStatusService = mockService(MaritalStatusService)
    InspectionCategoryService inspectionCategoryService = mockService(InspectionCategoryService)
    RecruitmentCycleService recruitmentCycleService = mockService(RecruitmentCycleService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)
    JobRequisitionService jobRequisitionService = mockService(JobRequisitionService)

    def setupSpec() {
        domain_class = Vacancy
        service_domain = VacancyService
        entity_name = "vacancy"
        required_properties = PCPUtils.getRequiredFields(Vacancy)
        filtered_parameters = ["job.descriptionInfo.localName", "id"];
        autocomplete_property = "job.descriptionInfo.localName"
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ['list', 'delete', 'edit']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    def setup() {
        if (!workExperienceService) {
            workExperienceService.proxyFactoryService = proxyFactoryService
            serviceInstance.workExperienceService = workExperienceService
        }
        if (!educationMajorService) {
            educationMajorService.proxyFactoryService = proxyFactoryService
            serviceInstance.educationMajorService = educationMajorService
        }
        if (!educationDegreeService) {
            educationDegreeService.proxyFactoryService = proxyFactoryService
            serviceInstance.educationDegreeService = educationDegreeService
        }
        if (!governorateService) {
            governorateService.proxyFactoryService = proxyFactoryService
            serviceInstance.governorateService = governorateService
        }
        if (!professionTypeService) {
            professionTypeService.proxyFactoryService = proxyFactoryService
            serviceInstance.professionTypeService = professionTypeService
        }
        if (!competencyService) {
            competencyService.proxyFactoryService = proxyFactoryService
            serviceInstance.competencyService = competencyService
        }
        if (!maritalStatusService) {
            maritalStatusService.proxyFactoryService = proxyFactoryService
            serviceInstance.maritalStatusService = maritalStatusService
        }
        if (!inspectionCategoryService) {
            serviceInstance.inspectionCategoryService = inspectionCategoryService
        }
        if (!jobRequisitionService) {
            jobRequisitionService.proxyFactoryService = jobRequisitionService
            serviceInstance.inspectionCategoryService = inspectionCategoryService
        }
        if (!recruitmentCycleService) {
            serviceInstance.recruitmentCycleService = recruitmentCycleService
        }
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
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.VACANCY
        model.referenceObject == Vacancy.name
        println("test_list done with data : ${model}")
    }


    def "test_edit"() {
        when:
        controller.edit()
        then:
        def editInstance = serviceInstance.getInstance(params)
        if (editInstance?.vacancyStatus in [ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.NEW]) {
            model != [:]
            model.type == "edit"
            model.vacancy == editInstance
            println "test_edit  done with data : ${model}"
        } else {
            flash.message == alertTagLib.error(label: (validationTagLib.message(code: "vacancy.errorEditMessage.label")))

        }
    }

/**
 * @goal test delete action.
 * @expectedResult request without params and response with success delete result.
 */
    def "new_test_success_delete"() {

        setup:
        println("************************test_success_delete********************************")
        Firm firm = Firm.build()
        def testInstance = Vacancy.build(firm: firm)
        def previousCount = domain_class.count()
        def searchMap = controller?.params?.clone()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                searchMap.put(key, (getPropertyValue(key, testInstance)))
            }
        }

        when:
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if (!with_key_join_table) {
                    controller.params["${key}"] = getPropertyValue(key, testInstance)
                } else {
                    def propertyId = join_table_ids.get(key) ?: "id"
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            } else {
                controller.params["${key}"] = getPropertyValue(key, testInstance)
            }
        }
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        deletedInstance != null
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message")))
        newCount == previousCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test delete success and new count is ${newCount}")
    }

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with success delete result.
     */

    def "new_test_success_delete_ajax"() {

        setup:
        println("************************test_success_delete_ajax********************************")
        Firm firm = Firm.build()
        def testInstance = Vacancy.build(firm: firm)
        def previousCount = domain_class.count()
        def searchMap = controller?.params?.clone()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                sendParams(key, testInstance, searchMap)
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if (!with_key_join_table) {
                    sendParams(key, testInstance, controller.params)
                } else {
                    def propertyId = join_table_ids.get(key) ?: "id"
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            } else {
                sendParams(key, testInstance, controller.params)
            }
        }
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message"))).toString()
        newCount == previousCount
        println("test delete ajax success and new count is ${newCount}")
    }

    /**
     * @goal test delete action.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "new_test_fail_delete"() {

        setup:
        println("************************test_fail delete********************************")
        Firm firm = Firm.build()
        def testInstance = Vacancy.build(firm: firm)
        def searchMap = controller?.params?.clone()
        def previousCount = domain_class.count()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                searchMap.put(key, (getPropertyValue(key, testInstance)))
            }
        }

        when:
        request.method = 'POST'
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(params)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        def newCount = domain_class.count()
        response.redirectedUrl == "/${entity_name}/list"
        newCount == previousCount
        flash.message == alertTagLib.error(label: (validationTagLib.message(code: "default.not.deleted.message")))
        println("test_delete fail done")
    }

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "new_test_fail_delete_ajax"() {

        setup:
        println("************************test_fail_delete_ajax********************************")
        Firm firm = Firm.build()
        def testInstance = Vacancy.build(firm: firm)
        def searchMap = controller?.params?.clone()
        def previousCount = domain_class.count()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                searchMap.put(key, (getPropertyValue(key, testInstance)))
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(params)[0]
        deletedInstance != null
        def newCount = domain_class.count()
        newCount == previousCount
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        flash.message == null
        println("test_delete ajax fail done")
    }


    def "test_getMandatoryInspection"() {
        setup:
        println("************************test_getMandatoryInspection********************************")
        when:
        params["isRequiredByFirmPolicy"] = true
        params["allInspectionCategory"] = true
        controller.getMandatoryInspection()
        then:
        def PagedResultList = inspectionCategoryService.search(params)
        PagedResultList.size() >= 0
        println("test_getMandatoryInspection done with data : ${PagedResultList}")
    }


    def "test_removeMandatoryInspection"() {
        setup:
        println("************************test_removeMandatoryInspection********************************")
        def instance = saveEntity()
        when:
        controller.removeMandatoryInspection(instance)
        then:
        def vacancyInstance = controller.removeMandatoryInspection(instance)
        vacancyInstance != null
        vacancyInstance.inspectionCategories != null
        println("test removeMandatoryInspection done with data : ${vacancyInstance}")
    }


    def "test_autoCompleteVacancyRecruitmentCycle"() {
        setup:
        println "****************test_autoCompleteVacancyRecruitmentCycle******************"
        RecruitmentCycle.build()
        RecruitmentCycle.build()
        def instance = RecruitmentCycle.build()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.id = instance?.id
        controller.autoCompleteVacancyRecruitmentCycle(parameterMap)
        then:
        response != null
        println("test autoCompleteVacancyRecruitmentCycle all data done: ${response.json}")
    }

    def "test_getJobRequisitionInfo"() {
        setup:
        println "****************test_getJobRequisitionInfo******************"
        Firm firm = Firm.build()
        def testInstance = JobRequisition.build(firm: firm)
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap["isRequiredByFirmPolicy"] = true
        parameterMap["allInspectionCategory"] = true
        params.id = testInstance?.id
        PCPSessionUtils.setValue("firmId",firm.id)
        controller.getJobRequisitionInfo(parameterMap)
        then:
        def map = controller.getJobRequisitionInfo(parameterMap)
        map != [:]
        println("test getJobRequisitionInfo all data done: ${map}")
    }
}